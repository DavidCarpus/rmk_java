package rmk.reports;

import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.Address;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.*;


public class ReportDataCust{
    public static rmk.DataModel sys = rmk.DataModel.getInstance();
    static final SimpleDateFormat dateFormatter = new SimpleDateFormat ("MM/dd/yyyy");
    final NumberFormat priceFormatter;
    Invoice invoice=null;
    Customer customer=null;
    Vector listData=null;
    Vector invoices=null;

    public ReportDataCust(Invoice invoice){
	this.invoice = invoice;
	priceFormatter = NumberFormat.getNumberInstance();
	priceFormatter.setMinimumFractionDigits(2);
    }
    public ReportDataCust(){
	priceFormatter = NumberFormat.getNumberInstance();
	priceFormatter.setMinimumFractionDigits(2);
    }

    public void setShipDate(GregorianCalendar shipDate){
		invoices = sys.invoiceInfo.getInvoicesByDate("DateShipped", shipDate,shipDate);
    }


    public void setCustomer(Customer cust){	
	customer = cust;
    }
    public void setInvoice(Invoice inv){
	if(inv != null)
	    invoice = inv;
    }
    public Invoice getInvoice(){
	return invoice;
    }
    public Customer getCustomer(){
	return customer;
    }
    public String[] getListRow(int row){
	int currRow=0;
	for(Enumeration items=listData.elements(); items.hasMoreElements();){
	    String item[][] =  (String[][])items.nextElement();
	    for(int i=0;i<item.length;i++){
		if(currRow++ == row)
		    return item[i];
	    }		    
//  	    rows += item.length;
	}
	return null;
//  	return (String[][])listData.get(row);
    }
    public int getTotalListRows(){
	int rows=0;
	if(listData == null){
	    return 0;
	}
	for(Enumeration items=listData.elements(); items.hasMoreElements();){
	    String item[][] =  (String[][])items.nextElement();
	    rows += item.length;
	}
	return rows;
    }
    public void setInvoiceNumber(int id) throws Exception{
	Invoice invoice = sys.invoiceInfo.getInvoice(id);
	
	setCustomer(sys.customerInfo.getCustomerByID(invoice.getCustomerID()));
	setInvoice(invoice);

	setFormat(customer.isDealer()?0:1);
    }
    public void setFormat(int format){
	if(invoices == null){ // NOT invoiceList
	    if(format == 0)
		listData = getInvoiceItemsDealer(invoice);
	    else
		listData = getInvoiceItems(invoice);
//    	} else{
//    	    listData = ReportDataCustBladeList.getBladeList(invoices);
	}
    }

    public String customerCCNum(){
	if(sys.financialInfo.getInvoiceDue(invoice) > 0)
	    return sys.financialInfo.getLastCreditCard(customer.getCustomerID());
	else
	    return "";
    }


//------------------------------------------------------------------------
    private Vector getInvoiceItems(Invoice invoice){
	Vector entries = invoice.getItems();
	Vector results = new Vector();
	rmk.DataModel sys = rmk.DataModel.getInstance();

	if(entries == null) return results;
	for(int i=0; i< entries.size(); i++){
	    InvoiceEntries entry = (InvoiceEntries)entries.get(i);
	    results.add(getRow_LongForm(entry));
	}
	return results;
    }
//------------------------------------------------------------------------
    private Vector getInvoiceItemsDealer(Invoice invoice){
	Vector entries = invoice.getItems();
	if(entries == null){
	    entries = sys.invoiceInfo.getInvoiceEntries(invoice.getInvoice());
	    invoice.setItems(entries);
	}
	Vector results = new Vector();
	rmk.DataModel sys = rmk.DataModel.getInstance();
	
	for(int i=0; i< entries.size(); i++){
	    InvoiceEntries entry = (InvoiceEntries)entries.get(i);
  	    results.add(getRow_ShortForm(entry));
	}
	return results;
    }
//------------------------------------------------------------------------
    private String[][] getRow_LongForm(InvoiceEntries entry){
	Vector features = sys.invoiceInfo.getInvoiceEntryAdditions(entry.getInvoiceEntryID());
	int txtRows = 1;
	if(features != null) txtRows += features.size();

	String comment = entry.getComment();
	if(comment != null && comment.trim().length() == 0) comment = null;
	if(comment != null)  txtRows++;

	String results[][] = new String[txtRows][5];
//    	System.out.println("txtRows:"+ txtRows);

	int col=0;
	int row=0;
	results[row][col++] = ""+entry.getQuantity();
//  	results[row][col++] = "."+entry.getPartDescription();
  	results[row][col++] = ""+sys.partInfo.getPartCodeFromID(entry.getPartID());
  	results[row][col++] = ""+sys.partInfo.getPartDescFromID(entry.getPartID());
	results[row][col++] = ""+priceFormatter.format(entry.getPrice());
	results[row][col++] = ""+priceFormatter.format(entry.getPrice() * entry.getQuantity());
	row++;

	String field="";
	for(Enumeration enum = features.elements(); enum.hasMoreElements();){
	    InvoiceEntryAdditions feature = (InvoiceEntryAdditions)enum.nextElement();
	    col=0;
	    int partID = (int)feature.getPartID();
	    results[row][col++] = "";
	    field="";
	    field = sys.partInfo.getPartCodeFromID(partID);
	    if(feature.getPrice() == 0)
		field = field.toLowerCase();
	    results[row][col++] = "   " + field;

	    field="";
	    field = sys.partInfo.getPartDescFromID(partID);
	    if(feature.getPrice() == 0)
		field = field.toLowerCase();
	    results[row][col++] = "   " + field;

	    field="";
	    field = priceFormatter.format(feature.getPrice());
	    if(feature.getPrice() == 0)
		field = "(" + field + ")";
	    results[row][col++] = field;
	    results[row][col++] = field;
	    for(col=0; col < 5; col++)
		if(results[row][col] == null) results[row][col] = "";

	    row++;
	}
	if(comment != null){
	    col=0;
	    results[row][col++] = "";
	    results[row][col++] = "**NOTE**";
	    results[row][col++] = ""+comment;
	    results[row][col++] = "";
	    results[row][col++] = "";
	}

	return results;
    }
//------------------------------------------------------------------------
    private String[][] getRow_ShortForm(InvoiceEntries entry){
	Vector features = sys.invoiceInfo.getInvoiceEntryAdditions(entry.getInvoiceEntryID());
	int txtRows = 1;
//  	if(entry.getComment() != null && entry.getComment().length() > 0) txtRows++;
//  	if(features != null && features.size() > 0) txtRows++;

	String comment = entry.getComment();
	if(comment != null && comment.trim().length() == 0) comment = null;
	if(comment != null)  txtRows++;
//      	System.out.println("txtRows:"+ txtRows);

	String results[][] = new String[txtRows][5];
	
	int col=0;
	int row=0;
	results[row][col++] = ""+entry.getQuantity();
	results[row][col++] = ""+sys.partInfo.getPartCodeFromID(entry.getPartID());
//  	results[row][col++] = entry.getPartDescription();
	results[row][col++] = "";
//  	results[row][col++] = sys.getPartDescFromID(entry.getPartID());
	results[row][col++] = ""+priceFormatter.format(entry.getPrice());
	results[row][col++] = ""+priceFormatter.format((entry.getPrice() * entry.getQuantity()));
//  	row++;

	if(features == null || features.size() == 0)
	    results[0][2] = entry.getPartDescription();
	for(Enumeration enum = features.elements(); enum.hasMoreElements();){
	    InvoiceEntryAdditions feature = (InvoiceEntryAdditions)enum.nextElement();

	    String part = sys.partInfo.getPartCodeFromID((int)feature.getPartID());
	    if(feature.getPrice() == 0){
		results[row][2] += part.toLowerCase()+",";
	    } else {
		results[row][2] += part.toUpperCase()+",";
	    }
	}
	if(results[row][2].length() > 0){
	    results[row][2] = results[row][2].substring(0,results[row][2].length()-1);
	}

	for(col=0; col < 5; col++)
	    if(results[row][col] == null) results[row][col] = "";

	if(comment != null){
	    col=0;
	    row++;
	    results[row][col++] = "";
	    results[row][col++] = "**NOTE**";
	    results[row][col++] = ""+comment;
	    results[row][col++] = "";
	    results[row][col++] = "";
	}

	return results;
    }
   //===================================================================
    private String[] getCustomerAddress(Customer customer, Address address){
	Vector rows = new Vector();
	String txt="";
	String results[] = new String[0];

	if(customer.getPrefix() != null) txt += customer.getPrefix() + " ";
	if(customer.getFirstName() != null) txt += customer.getFirstName() + " ";
	if(customer.getLastName() != null) txt  += customer.getLastName() + " ";
	rows.add(txt.trim());

	if(address != null){
	    for(int i=0; i< 3; i++){
		if(address.getAddress(i) != null){
		    txt = address.getAddress(i).trim();
		    if(txt.length() > 0){
			rows.add(txt);
		    }
		}
	    }
	    txt = address.getCITY().trim();
	    txt += " "+ address.getSTATE().trim();
	    txt += ", "+ address.getZIP().trim();
	    if(txt.length() > 1){
		rows.add(txt);
	    }
	}
	if(rows.size() > 0){
	    results = new String[rows.size()];
	    for(int i=0; i< rows.size(); i++){
		results[i] = (String)rows.get(i);
	    }
	}
	return results;

    }
    //------------------------------------------------------------------------
    String[] getCustomerAddress(){
		rmk.database.dbobjects.Address address;
		try {
			address = sys.customerInfo.getCurrentAddress(customer.getCustomerID());
			return getCustomerAddress(customer, address);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String[1];
    }
   //===================================================================
    public String[] invoiceShippingInstructions(){
	String results[] = {"","","",""};

	String info = invoice.getShippingInfo();
	if(info == null) return results;

	for(int i=0; i< 4; i++){
	    results[i] = "";
	    if(info.indexOf("|") > 0){
		results[i] = info.substring(0,info.indexOf("|"));
		info = info.substring(info.indexOf("|")+1);
	    } else{
		if(info.length() > 0){
		    results[i] = info;
		    info = "";
		}
	    }
	}

	return results;
    }
   //===================================================================
    public String [] invoiceInfo(){
	String results[]=new String[3];
	results[0] = customer.getPhoneNumber();
	results[1] = invoice.getPONumber() != null?invoice.getPONumber():"";
	results[2] = ""+invoice.getID();
	return results;
    }
   //===================================================================
    public String [] acknowledgeInfo(){
	String results[]=new String[2];
	results[0] = customer.getPhoneNumber();
	results[1] = invoice.getPONumber() != null?invoice.getPONumber():"";
	return results;
    }

   //===================================================================
    public String [] invoiceShipDates(){
	String results[]=new String[3];
	results[0] = invoice.getDateEstimated()!= null? dateFormatter.format(invoice.getDateEstimated().getTime()): "";
	results[1] = invoice.getDateOrdered()!= null? dateFormatter.format(invoice.getDateOrdered().getTime()): "";
  	results[2] = invoice.getDateShipped()!= null? dateFormatter.format(invoice.getDateShipped().getTime()): "";
	return results;
    }
    //===================================================================
    String getCurrentDate(){
	return dateFormatter.format(new Date());
    }
   //===================================================================
    public String [] remittanceAddress(){
	String text[] = {"Randall Made", "P.O. Box 1988", "Orlando, FL 32802-1988"};
	return text;
    }
   //===================================================================
    public String [] invoiceTotals(){
	String results[]=new String[8];
	NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance( Locale.US );

	for(int i=0; i< results.length; i++){
	    results[i] = "";
	}
	double discountPercentage = invoice.getDiscountPercentage();
	if(discountPercentage > 1) discountPercentage /= 100.0;
	double retail = invoice.getTotalRetail();
	double discount = sys.financialInfo.getTotalInvoiceDiscount(invoice);

	double shipping = invoice.getShippingAmount();	
	double taxPercentage = invoice.getTaxPercentage();
//  	double taxPercentage = sys.financialInfo.getInvoiceTaxRate(invoice);
	double payments = sys.financialInfo.getTotalInvoicePayments(invoice.getInvoice());

	results[0] += currencyFormatter.format(retail);
	results[1] += currencyFormatter.format(discount);
	// BASE due
	results[2] += currencyFormatter.format(retail - discount);
	//shipping
	results[3] += currencyFormatter.format(shipping);
	// taxes
	results[4] += currencyFormatter.format(retail * taxPercentage);
	// payments
  	results[5] = currencyFormatter.format(payments);      
	
  	results[7] = currencyFormatter.format(sys.financialInfo.getInvoiceDue(invoice));

	return results;
    }
   //===================================================================
    public String[] getTerms(){	
	String results[];
	if(customer.getTerms().equals("2")){
	    results = new String[1];
	    results[0] = "Please Pay Net 20 Days";
	}else{
	    results = new String[2];
	    results[0] = "Please Pay Net 21 Days";
	    results[1] = "Prior to the Scheduled Ship Date.";
	}
	return results;
    }
   //===================================================================
    public String invoiceComment(){
	String comment = invoice.getComment();
	if(comment != null && comment.trim().length() > 0)
	    comment = "*** " + comment + " ***";
//  	comment = "Test Comment";
	return (comment == null?"":comment);
    }
   //===================================================================
    public String [] invoicePercentages(){
	String results[]=new String[8];
	NumberFormat percentFormat = NumberFormat.getPercentInstance();
	percentFormat.setMinimumFractionDigits(2);
	for(int i=0; i< results.length; i++){
	    results[i] = "";
	}
	double discount = invoice.getDiscountPercentage()/100;
  	results[1] += " (" + percentFormat.format(invoice.getDiscountPercentage()/100) + ")";
  	results[4] += " (" + percentFormat.format(invoice.getTaxPercentage()) + ")";

	return results;
    }

   //===================================================================
   //===================================================================
   //===================================================================
    public static void main(String args[]) throws Exception{
//  	int fontHeight = 10;
  	rmk.DataModel sys = rmk.DataModel.getInstance();

	java.util.GregorianCalendar date = new java.util.GregorianCalendar();
	while(date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
	    date.add(Calendar.DATE,1);
	System.out.println(date);
	date.add(Calendar.DATE,14);
	Vector invoices = sys.invoiceInfo.getInvoicesByDate("DateShipped", date,date);
//  	Vector data = ReportDataCustBladeList.getBladeList(invoices);

//  	System.out.println(invoices.size());
//  	System.out.println(data.size());
//  	rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog(null, rmk.gui.HtmlReportDialog.INVOICE_REPORT);
//  //  	rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog(null, rmk.gui.HtmlReportDialog.ACKNOWLEDGE_REPORT);
//  	rpt.exitOnCancel=true;
//  //  	rpt.setText(text);
//  	rpt.setReport(new rmk.reports.InvoiceReport(42496));
//  //    	rpt.setInvoice(42684); // 42496, 50004, 42684
//  //    	rpt.setInvoice(5000);
//  	rpt.setVisible(true);

//  	String data[][] = ReportDataCust.getInvoiceItems(invoice);
//  	int lstHt = getListHeight(fontHeight, 0, data.length/2, data);
//  	System.out.println(lstHt);
	
    }
//  	setCustomer(sys.getCustomer());
//  	setInvoice(sys.getCurrentInvoice());


}
