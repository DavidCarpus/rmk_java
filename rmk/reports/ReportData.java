package rmk.reports;

import rmk.DataModel;
import rmk.ErrorLogger;
import rmk.database.PartInfo;
import rmk.database.PartPriceTable;
import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.Address;
import rmk.database.dbobjects.Parts;
import rmk.database.dbobjects.Payments;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import rmk.gui.HtmlReportDialog;

import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.*;

import carpus.util.Logger;

public class ReportData {

    public static rmk.DataModel sys = rmk.DataModel.getInstance();

    static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "MM/dd/yyyy");

    static final SimpleDateFormat expiredDateFormatter = new SimpleDateFormat(
            "MM/yyyy");

//    static final int maxCommentLength = 50;
    int currentYear=0;

    static final int YEARS_TO_FETCH = 3;

    //      final NumberFormat priceFormatter;
    NumberFormat currencyFormatter;

    //      final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(
    // Locale.US );
    Invoice invoice = null;

    Customer customer = null;

    Payments payment = null;

    Vector listData = null;
    
    Vector invoices = null;

    Vector partList = null;
    
    public ReportData(Invoice invoice) {
        setCurFormatter();
        currentYear = (new java.util.GregorianCalendar()).get(java.util.GregorianCalendar.YEAR);
        this.invoice = invoice;
    }

    public ReportData() {
        currentYear = (new java.util.GregorianCalendar()).get(java.util.GregorianCalendar.YEAR);
        setCurFormatter();
    }
    void setCurFormatter(){
        currencyFormatter = NumberFormat.getNumberInstance();
        currencyFormatter.setMinimumFractionDigits(2);
        currencyFormatter.setMaximumFractionDigits(2);
        currencyFormatter.setMaximumIntegerDigits(6);
    }

    public void setCustomer(Customer cust) {
        customer = cust;
    }

    public void setInvoice(Invoice inv) {
        if (inv != null) invoice = inv;
    }

    public Vector getPartsList(){
        if(partList != null && partList.size() > 3) return partList;

        PartInfo partInfo = DataModel.getInstance().partInfo;
        PartPriceTable prices = DataModel.getInstance().pricetable;
        
        Vector parts = new Vector();
        for(int i=0; i<= partInfo.largestPartID(); i++){
            Parts part = partInfo.getPart(i);
            if(part != null && part.isActive()){
                parts.add(part);
            }
        }
        Object[] partArray = parts.toArray();
        Arrays.sort(partArray, new rmk.comparators.PartListRpt());
        
        partList=new Vector();
        Object[] record;
        for(int i=0; i< partArray.length; i++){
//        for(Enumeration lst = partArray.elements(); lst.hasMoreElements();){
            Parts part = (Parts)partArray[i];
            record = new Object[YEARS_TO_FETCH+3];
            record[0] = part;
            int partYear = currentYear;
            for(int year = 0; year <= YEARS_TO_FETCH; year++ ){
                double price = prices.getPartPrice(partYear, (int)part.getPartID());
                record[1+year] = currencyFormatter.format(price);
                partYear--;
            }
            record[record.length-1] = ""+part.getPartType();
            partList.add(record);
        }
        
        return partList;
    }
    
    public Invoice getInvoice() {
        return invoice;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String[] getListRow(int row) {
        int currRow = 0;
        if(partList != null){
            if(row > partList.size() ) return null;
            return partListRow(row);
        }
        for (Enumeration items = listData.elements(); items.hasMoreElements();) {
            String item[][] = (String[][]) items.nextElement();
            for (int i = 0; i < item.length; i++) {
                if (currRow++ == row) return item[i];
            }
            //  	    rows += item.length;
        }
        return null;
        //  	return (String[][])listData.get(row);
    }
    
    String[] partListRow(int row){
        try{
        Object[] item = (Object[])partList.get(row);
        
        String[] results = new String[item.length+2];
        Parts part = (Parts)item[0];
        results[0] = part.getPartCode(); 
        results[1] = part.getDescription();
        
        String codes="";
        codes += (part.isDiscountable()?"D":" ");
        codes += (part.isTaxable()?"T":" ");
        codes += (part.isBladeItem()?"B":" ");
        results[2] = codes;

        int yearStartCol=1;
        for(int year=0; year <= YEARS_TO_FETCH; year++){
            results[3+year] = ""+item[yearStartCol+year];
        }
        results[results.length-1] = ""+item[item.length-1];
        return results;
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    public int getTotalListRows() {
        int rows = 0;
        if (listData == null && partList == null) { return 0; }
        if(listData != null){
            for (Enumeration items = listData.elements(); items.hasMoreElements();) {
                String item[][] = (String[][]) items.nextElement();
                rows += item.length;
            }
        } else if(partList != null){
            int cnt=0;
            for (Enumeration items = partList.elements(); items.hasMoreElements();) {
                items.nextElement();
                cnt++;
            }
            return cnt;
        }
        return rows;
    }

    public void setInvoiceNumber(int id, int maxCommentLength) throws Exception {
        Invoice invoice = sys.invoiceInfo.getInvoice(id);
        if (invoice != null) {
            setCustomer(sys.customerInfo.getCustomerByID(invoice
                    .getCustomerID()));
            setInvoice(invoice);
            setFormat(customer.isDealer() ? 0 : 1, maxCommentLength);
        }
    }

    public void setFormat(int format, int maxCommentLength) {
        if ((invoices == null && partList ==null) && invoice != null) { 
            // NOT invoice/parts List, AND invoice not loaded yet
            if (format == 0)
                listData = getInvoiceItemsDealer(invoice, maxCommentLength);
            else
                listData = getInvoiceItems(invoice, maxCommentLength);
            //    	} else{
            //    	    listData = ReportDataBladeList.getBladeList(invoices);
        }
    }

    public String getTaxID() {
        String id = "";
        if (customer.getTaxNumber() != null
                && customer.getTaxNumber().length() > 0)
                id = "EIN: " + customer.getTaxNumber();
        return id;
    }

    public String customerCCNum() {
        String results = "";
        if (sys.financialInfo.getInvoiceDue(invoice) > 0) {
            //  	    return
            // sys.financialInfo.getLastCreditCard(customer.getCustomerID());
            String ccNum = customer.getCreditCardNumber();
            if (ccNum == null || ccNum.trim().length() == 0) return "";
            ccNum = rmk.database.FinancialInfo.removeCardNumberDashes(ccNum);
            ccNum = rmk.database.FinancialInfo.addCardNumberDashes(ccNum);
            results = ccNum;

            GregorianCalendar expDate = customer.getCreditCardExpiration();
            if (expDate == null) {
                results += "** Missing Exp. Date **";
            } else {
                if (expDate.before(new GregorianCalendar()))
                        results += "** Expired **";
                results += "  "
                        + expiredDateFormatter.format(expDate.getTime());
            }
            //	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + "CC#" + ":"+ results);

        }
        return results;
    }

    //------------------------------------------------------------------------
    public Vector getInvoiceItems(Invoice invoice, int maxCommentLength) {
        //      private Vector getInvoiceItems(Invoice invoice){
        Object entries[];
        Vector results = new Vector();

        if (invoice == null) return null;
        if (invoice.getItems() == null) {
            invoice.setItems(sys.invoiceInfo.getInvoiceEntries(invoice
                    .getInvoice()));
        }
        if (invoice.getItems() == null) { return results; }
        entries = invoice.getItems().toArray();
        Arrays.sort(entries, new rmk.comparators.InvoiceReportEntries());

        for (int i = 0; i < entries.length; i++) {
            InvoiceEntries entry = (InvoiceEntries) entries[i];
            results.add(getRow_LongForm(entry, maxCommentLength));
        }
        return results;
    }

    //------------------------------------------------------------------------
    public Vector getInvoiceItemsDealer(Invoice invoice, int maxCommentLength) {
        //      private Vector getInvoiceItemsDealer(Invoice invoice){
        Object entries[];
        Vector results = new Vector();

        if (invoice.getItems() == null) {
            invoice.setItems(sys.invoiceInfo.getInvoiceEntries(invoice
                    .getInvoice()));
        }
        if (invoice.getItems() == null) { return results; }
        entries = invoice.getItems().toArray();
        Arrays.sort(entries, new rmk.comparators.InvoiceReportEntries());

        // switched ALL to sort by model number
//        Arrays.sort(entries, new rmk.comparators.InvoiceReportDealerEntries());
        
        for (int i = 0; i < entries.length; i++) {
            InvoiceEntries entry = (InvoiceEntries) entries[i];
            results.add(getRow_ShortForm(entry, maxCommentLength));
        }
        return results;
    }

    //------------------------------------------------------------------------
    private String[][] getRow_LongForm(InvoiceEntries entry, int maxCommentLength) {
        Vector features = sys.invoiceInfo.getInvoiceEntryAdditions(entry
                .getInvoiceEntryID());
        String comment = entry.getComment();
        if (comment != null && comment.trim().length() == 0) {
            comment = null;
        }
        Vector resultsVector = new Vector();

        int col = 0;
        int row = 0;
        double entryTotalRetail = entry.getPrice();
        int entryQty = entry.getQuantity();
        String[] baseKnifeRow = new String[5];
		resultsVector.add(baseKnifeRow);
        baseKnifeRow[0] = "" + entryQty;
        baseKnifeRow[1] = ""+ sys.partInfo.getPartCodeFromID(entry.getPartID());
        baseKnifeRow[2] = ""+ sys.partInfo.getPartDescFromID(entry.getPartID());
        baseKnifeRow[3] = "" + currencyFormatter.format(entryTotalRetail / entryQty);
        baseKnifeRow[4] = "" + currencyFormatter.format(entryTotalRetail);

        String field = "";
        double featuresTotal = 0;
        for (Enumeration enum = features.elements(); enum.hasMoreElements();) {
            InvoiceEntryAdditions feature = (InvoiceEntryAdditions) enum
                    .nextElement();
            addFeatureLong(resultsVector, feature, entryQty, 5);
            featuresTotal += feature.getPrice();
        }
        double baseKnife = (entryTotalRetail / entryQty) - featuresTotal;
        baseKnifeRow[3] = "" + currencyFormatter.format(baseKnife);
        baseKnifeRow[4] = "" + currencyFormatter.format(baseKnife * entryQty);

        int commentRows=addCommentLong(resultsVector, comment, 5, maxCommentLength);
//        if (features.size() > 0) { 
        // Make ALL items have a sub-total line Email:  Wed, 27 Oct 2004
            int prevRowIndex = resultsVector.size()-(1+commentRows);
            String[] previousRow = (String[])resultsVector.get(prevRowIndex);
            previousRow[3] = "<U>" + previousRow[3] + "</U>";
            previousRow[4] = "<U>" + previousRow[4] + "</U>";
            addKnifeSubtotalLine(resultsVector, entryTotalRetail/ entryQty, entryTotalRetail, 5);
//        }

        return (String[][]) resultsVector.toArray(new String[resultsVector.size()][5]);
    }
/*================================================================================*/
	public void addFeatureLong(Vector results, InvoiceEntryAdditions feature, int knifeQty, int cols){
        String row[] = new String[cols];
        String field = "";

        int partID = (int) feature.getPartID();
        row[0] = "";

        field = "";
        field = sys.partInfo.getPartCodeFromID(partID);
        if (feature.getPrice() == 0) field = field.toLowerCase();
        row[1] = "   " + field;

        field = "";
        field = sys.partInfo.getPartDescFromID(partID);
        if (feature.getPrice() == 0) field = field.toLowerCase();
        row[2] = "   " + field;

        field = "";
        field = currencyFormatter.format(feature.getPrice());
        if (feature.getPrice() == 0) field = "(" + field + ")";
        row[3] = field;

        field = currencyFormatter.format(feature.getPrice() * knifeQty);
        if (feature.getPrice() == 0) field = "(" + field + ")";
        row[4] = field;
        for (int col = 0; col < cols; col++){
            if (row[col] == null) row[col] = "";
        }
        results.add(row);
	}
	

    /**
     * 
     * @param results
     * @param comment
     * @param cols
     * @return
     */
    public int addCommentLong(Vector results, String comment, int cols, int maxCommentLength){
        int rowsAdded =0;
        if (comment != null) {
        	Vector rows = commentRows(comment, maxCommentLength);
            for (Enumeration enum = rows.elements(); enum.hasMoreElements();) {
                String commentPart = (String) enum.nextElement();
				String row[] = new String[cols];
                if (commentPart != null) {
                    row[0] = "";
                    row[1] = "**NOTE**";
                    if (row[2] != null)
                        row[2] = "" + commentPart + row[2];
                    else
                        row[2] = "" + commentPart;
                    row[3] = "     ";
                    row[4] = "     ";
                    rowsAdded++;
                    results.add(row);
                }
            }
        }
        return rowsAdded;
    }
    /**
     * 
     * @param results
     * @param subtotal
     * @param total
     * @param cols
     */
    public void addKnifeSubtotalLine(Vector results,double subtotal, double total, int cols){
        String row[] = new String[cols];
        row[0] = "";
        row[1] = "";
        row[2] = "" + carpus.util.Formatting.replicate(" ", 80) + "Knife SubTotal: ";
        row[3] = "" + currencyFormatter.format(subtotal);
        row[4] = "" + currencyFormatter.format(total);
        results.add(row);
    }
//================================================================================
    public static Vector commentRows(String commentStr, int maxCommentLength) {
        int cnt = 0;
        Vector results = new Vector();
        if(commentStr == null)
        	return results;
        
        String comment = commentStr + " ";
        String commentPart = "";
        
        try {
			while (comment.length() > 0) {
				if (comment.length() > maxCommentLength){
					commentPart = comment.substring(0, maxCommentLength);
					commentPart = commentPart.trim();
					int etchStart = 0;
					etchStart = commentPart.lastIndexOf("ET1 ");
					if(etchStart <= 0)
						etchStart = commentPart.lastIndexOf("ET2 ");
					
					if(etchStart > 0){
						commentPart = commentPart.substring(0,etchStart);
					}
				}else{
					commentPart = comment;
				}
				while (!commentPart.endsWith(" ") && commentPart.length() > 0) {
					if(commentPart.length() >= 1){
						commentPart = commentPart.substring(0,
								commentPart.length() - 1);
					}else{
						commentPart = "";
					}
				}
				if(commentPart.length() > 0)
					comment = comment.substring(commentPart.length()); // remove part
				else
					comment = "";
				// from front of
				// comment
				commentPart = commentPart.replace('\r', ';');
				commentPart = commentPart.replace('\n', ';');
				if (commentPart.trim().length() > 0)
					results.add(commentPart);
			}
		} catch (Exception e) {
			Logger.getInstance().logError("Error Processing comment:", e);
			e.printStackTrace();
		}
        return results;
    }

    //------------------------------------------------------------------------
    private String[][] getRow_ShortForm(InvoiceEntries entry, int maxCommentLength) {
        Vector features = sys.invoiceInfo.getInvoiceEntryAdditions(entry
                .getInvoiceEntryID());
        int txtRows = 1;
        //  	if(entry.getComment() != null && entry.getComment().length() > 0)
        // txtRows++;
        //  	if(features != null && features.size() > 0) txtRows++;

        String comment = entry.getComment();
        if (comment != null && comment.trim().length() == 0) comment = null;
        if (comment != null) {
            int commentLines = commentRows(comment, maxCommentLength).size();
            txtRows += commentLines;
        }

        //      	ErrorLogger.getInstance().logMessage(this.getClass().getName() + "txtRows:"+ txtRows);

        String results[][] = new String[txtRows][5];

        int col = 0;
        int row = 0;
        double entryTotalRetail = entry.getPrice();
        int entryQty = entry.getQuantity();
        results[row][col++] = "" + entryQty;
        results[row][col++] = ""
                + sys.partInfo.getPartCodeFromID(entry.getPartID());
        //  	results[row][col++] = entry.getPartDescription();
        results[row][col++] = "";
        //  	results[row][col++] = sys.getPartDescFromID(entry.getPartID());
        results[row][col++] = ""
                + currencyFormatter.format(entryTotalRetail / entryQty);
        results[row][col++] = "" + currencyFormatter.format(entryTotalRetail);

        if (features == null || features.size() == 0) {
            results[0][2] = "**STANDARD**";
            Parts part = sys.partInfo.getPart((int) entry.getPartID());
            if (part != null && part.getPartType() != 10) // 10 is model
                                                          // parttype
                    results[0][2] = "";
        }

        for (Enumeration enum = features.elements(); enum.hasMoreElements();) {
            InvoiceEntryAdditions feature = (InvoiceEntryAdditions) enum
                    .nextElement();

            String part = sys.partInfo.getPartCodeFromID((int) feature
                    .getPartID());
            if (feature.getPrice() == 0) {
                results[row][2] += part.toLowerCase() + ",";
            } else {
                results[row][2] += part.toUpperCase() + ",";
            }
        }
        results[row][2] = results[row][2].trim();
        int rowLen = results[row][2].length();
        if (rowLen > 0 && results[row][2].lastIndexOf(',') == rowLen - 1) {
            results[row][2] = results[row][2].substring(0, results[row][2]
                    .length() - 1);
        }

        for (col = 0; col < 5; col++)
            if (results[row][col] == null) results[row][col] = "";

        if (comment != null) {
            row++;
            for (Enumeration enum = commentRows(comment, maxCommentLength)
                    .elements(); enum.hasMoreElements();) {
                String commentPart = (String) enum.nextElement();
                col = 0;
                results[row][col++] = "";
                results[row][col++] = "**NOTE**";
                results[row][col++] = "" + commentPart;
                results[row][col++] = "";
                results[row][col++] = "";
                row++;
            }
        }

        return results;
    }

    //===================================================================
    private String[] getCustomerAddress(Customer customer, Address address) {
        Vector rows = new Vector();
        String txt = "";
        String results[] = new String[0];

        if (customer.getPrefix() != null) txt += customer.getPrefix() + " ";
        if (customer.getFirstName() != null)
                txt += customer.getFirstName() + " ";
        if (customer.getLastName() != null)
                txt += customer.getLastName() + " ";
        rows.add(txt.trim().toUpperCase());

        if (address != null) {
            for (int i = 0; i < 3; i++) {
                if (address.getAddress(i) != null) {
                    txt = address.getAddress(i).trim();
                    if (txt.length() > 0) {
                        rows.add(txt.toUpperCase());
                    }
                }
            }
            txt = address.getCITY().trim();
            txt += ", " + address.getSTATE().trim();
            txt += "          " + address.getZIP().trim();
            if (address.getCOUNTRY() != null) {
            	 txt += "           " + address.getCOUNTRY().trim();
            }
            if (txt.length() > 1) {
                rows.add(txt.toUpperCase());
            }
        }
        if (rows.size() > 0) {
            results = new String[rows.size()];
            for (int i = 0; i < rows.size(); i++) {
                results[i] = (String) rows.get(i);
            }
        }
        return results;

    }

    //------------------------------------------------------------------------
    String[] getCustomerAddress() {
        String results[] = { ""};
        try {
            rmk.database.dbobjects.Address address = sys.customerInfo
                    .getCurrentAddress(customer.getCustomerID());
            results = getCustomerAddress(customer, address);
        } catch (Exception e) {
            rmk.ErrorLogger.getInstance().logError(
                    this.getClass().getName() + ".getCustomerAddress()", e);
        } // end of try-catch
        return results;
    }

    //===================================================================
    public String[] invoiceShippingInstructions() {
        String results[] = { "", "", "", ""};

        String info = invoice.getShippingInfo();
        if (info == null || info.trim().length() == 0) {
            if (invoice.isShopSale())
                info = "Shop Sale";
            else
                info = "SAME";
        }

        for (int i = 0; i < 4; i++) {
            results[i] = "";
            if (info.indexOf("|") >= 0) {
                results[i] = info.substring(0, info.indexOf("|"));
                info = info.substring(info.indexOf("|") + 1);
            } else {
                if (info.length() > 0) {
                    results[i] = info.toUpperCase();
                    info = "";
                }
            }
        }

        return results;
    }

    //===================================================================
    public String[] invoiceInfo() {
        String results[] = new String[3];
        results[0] = customer.getPhoneNumber();
        results[1] = invoice.getPONumber() != null ? invoice.getPONumber() : "";
        results[2] = "" + invoice.getID();
        return results;
    }

    //===================================================================
    public String[] acknowledgeInfo() {
        String results[] = new String[3];
        results[0] = customer.getPhoneNumber();
        results[1] = invoice.getPONumber() != null ? invoice.getPONumber() : "";
        results[2] = "" + invoice.getID();
        return results;
    }

    //===================================================================
    public String[] invoiceShipDates() {
        String results[] = new String[3];
        results[0] = invoice.getDateEstimated() != null ? dateFormatter
                .format(invoice.getDateEstimated().getTime()) : "";
        results[1] = invoice.getDateOrdered() != null ? dateFormatter
                .format(invoice.getDateOrdered().getTime()) : "";
        results[2] = invoice.getDateShipped() != null ? dateFormatter
                .format(invoice.getDateShipped().getTime()) : "";
        return results;
    }

    //===================================================================
    String getCurrentDate() {
        return dateFormatter.format(new Date());
    }

    //===================================================================
    public String[] remittanceAddress() {
        String text[] = { "RANDALL MADE KNIVES", "P.O. Box 1988",
                "Orlando, FL 32802-1988"};
        return text;
    }

    //===================================================================
    public String[] invoiceTotals() {
        String results[] = new String[8];
        //  	NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(
        // Locale.US );

        for (int i = 0; i < results.length; i++) {
            results[i] = "";
        }
        double discountPercentage = invoice.getDiscountPercentage();
        if (discountPercentage > 1) discountPercentage /= 100.0;
        double retail = sys.financialInfo.getTotalRetail(invoice);
		double discount = sys.financialInfo.getTotalInvoiceDiscount(invoice);

        double shipping = invoice.getShippingAmount();
        double taxPercentage = invoice.getTaxPercentage();
        if (taxPercentage > 1) taxPercentage /= 100.0;
        //  	double taxPercentage = sys.financialInfo.getInvoiceTaxRate(invoice);
        double payments = sys.financialInfo.getTotalInvoicePayments(invoice);

        results[0] += currencyFormatter.format(retail);
        if (discount > 0) results[1] += currencyFormatter.format(discount);
        // BASE due
        if (retail - discount > 0) results[2] += currencyFormatter.format(retail - discount);
        //shipping
        //	if(shipping > 0) // print shipping ALWAYS per VR 2004-02-05
        results[3] += currencyFormatter.format(shipping);
        // taxes
        double taxesDue = sys.financialInfo.getInvoiceTaxes(invoice);
        if (taxesDue > 0){
            results[4] += currencyFormatter.format(taxesDue);
        }
        // payments
        results[5] = currencyFormatter.format(payments);

        results[7] = currencyFormatter.format(sys.financialInfo.getInvoiceDue(invoice));

        return results;
    }

    //===================================================================
    public String lastInvoicePayment() {
        //  	return "<U>Last Payment:Recieved on 99/99/99 for $0.00 (MC)</U>";
        String results = "";
        if (invoice == null) return "";

        if (payment == null || payment.getInvoice() != invoice.getInvoice()) {
            payment = sys.financialInfo.getLastPayment(invoice.getInvoice());
        }
        if (payment == null) return "<U>No Payments Recieved</U>";

        results += "<U>";
        results += "Last Payment Recieved on ";
        results += dateFormatter.format(payment.getPaymentDate().getTime());
        results += " for " + currencyFormatter.format(payment.getPayment());
        String code = sys.financialInfo.getPaymentTypeCode(payment);
        results += "(" + code + ")";
        results += "</U>";
        return results;
    }

    public String[] getTerms() {
        String results[];
        if (customer.getTerms().equals("2")) {
            results = new String[1];
            results[0] = "Please Pay Net 20 Days";
        } else {
            results = new String[2];
            results[0] = "Please Pay Net 21 Days";
            results[1] = "Prior to the Scheduled Ship Date.";
        }
        return results;
    }

    //===================================================================
    public String[] invoiceComment() {
        String comment = invoice.getComment();
//        int lines = 1;
        int width = 30;
        int maxLines = 3;
//        if (comment != null) lines = comment.length() / width + 1;
        ArrayList comments = new ArrayList();

        if(comment == null){
            for (int i = 0; i < maxLines; i++)
                comments.add("");
            String[] results = new String[comments.size()];
        	comments.toArray(results);
        	return results;
        }        	
        
        comment = sys.financialInfo.substituteInCCNum(invoice);
        if(comment.indexOf("|") > 0) // trim back portion of comment/note '|' is delimiter for this
            comment = comment.substring(0, comment.indexOf("|"));
        int currLine = 0;
        while (comment != null && comment.length() > 0 && currLine < maxLines) {
            int endMark = width;
            if (comment.length() < endMark) endMark = comment.length() - 1;
            while (comment.length() > endMark && comment.charAt(endMark) != ' ')
                endMark++;
            if(endMark >= comment.length())
            	endMark = comment.length();
            comments.add(comment.substring(0, endMark));
            comment = comment.substring(endMark);
            currLine++;
        }
        while(currLine++ < maxLines)
            comments.add("");
        	
        String[] results = new String[comments.size()]; 
		comments.toArray(results);
        return results;
    }

    //===================================================================
    public String[] invoicePercentages() {
        String results[] = new String[8];
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(2);
        for (int i = 0; i < results.length; i++) {
            results[i] = "";
        }
        double taxPercentage = invoice.getTaxPercentage();
        if (taxPercentage > 1) taxPercentage /= 100.0;

        double discount = invoice.getDiscountPercentage();
        if (discount > 1) discount /= 100.0;

        results[1] += " (" + percentFormat.format(discount) + ")";

        String shippingInstr = invoice.getShippingInstructions();
        if (shippingInstr != null) {
            if (shippingInstr.length() > 15) {
                String part1 = "";
                part1 = shippingInstr.substring(0, 15);

                //  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+
                // shippingInstr);

                //  	    while(part1.length() < 10 &&
                // Character.isJavaLetterOrDigit(shippingInstr.charAt(part1.length()))){
                //  //
                // if(Character.isJavaLetterOrDigit(shippingInstr.charAt(part1.length())))
                //  		    part1 += shippingInstr.charAt(part1.length());
                //  // else
                //  	    }
                //  	    String part2 = shippingInstr.substring(part1.length());
                //  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":part2:"+
                // part2);
                results[3] += " " + part1 + "";
            } else {
                results[3] += " " + shippingInstr + "";
            }
        }
        //  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + "results[3]:"+
        // results[3]);

        results[4] += " (" + percentFormat.format(taxPercentage) + ")";

        return results;
    }

    //===================================================================
    //===================================================================
    //===================================================================
    public static void main(String args[]) throws Exception {
        //  	int fontHeight = 10;
    	try{
    	    HtmlReportDialog rptDialog = new HtmlReportDialog();
    	    PartListReport rpt = new PartListReport();
    	    rptDialog.exitOnCancel = true;
    	    rptDialog.setReport(rpt);
    	    rptDialog.setVisible(true);
    	} catch (Exception ex){
    	    ex.printStackTrace();
    	    ErrorLogger.getInstance().logMessage(rmk.ErrorLogger.getInstance().stkTrace(ex));
    	}

//        ReportData rptData = new ReportData();
//        Vector data = rptData.getPartsList();
//        for(Enumeration enum = data.elements(); enum.hasMoreElements();){
//            Object[] item = (Object[] )enum.nextElement();
//            for(int i=0; i< item.length;i++){
//                System.out.print(""+item[i] + " " );
//            }
//            ErrorLogger.getInstance().logMessage();
//        }
        
//        rmk.DataModel sys = rmk.DataModel.getInstance();
//        rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog();
//        rpt.exitOnCancel = true;
//        rmk.reports.InvoiceReport tst = new rmk.reports.InvoiceReport(42496);
//        ErrorLogger.getInstance().logMessage(tst.getInvoice());
//        rpt.setReport(tst);
//        //    	rpt.setInvoice(60001); // 42496, 42683, 50000, 42684, 44732, 53163,
//        // 53384, 44800, 53483, 44424
//        rpt.setVisible(true);


        //		=====================================================
        //	ReportData rptData = new ReportData();
        //	Invoice invoice = sys.invoiceInfo.getInvoice(44683); // 53163
        //  	Vector data = rptData.getInvoiceItems(invoice);
        //	for(Enumeration enum = data.elements(); enum.hasMoreElements();){
        //  	    String [][]item = (String [][] )enum.nextElement();
        //	    for(int i=0; i< item.length; i++){
        //		String row[] = item[i];
        //		for(int j=0; j< row.length; j++)
        //		    System.out.print(row[j] + " ");
        //		System.out.print("\n");
        //	    }
        //	}
        //=====================================================
        //// getDealerSpecRequestList();
        //	  ErrorLogger.getInstance().logMessage(MergeFiles.getBalanceDueList());
        //		  ErrorLogger.getInstance().logMessage(getBalanceDueList());

        //	  generateMergeFile(MERGE_TYPE_BALANCE_DUE,
        //				Configuration.Config.getMergeFileLocation() + "BalanceDue.txt");
        System.exit(0);
        //  	int lstHt = getListHeight(fontHeight, 0, data.length/2, data);
        //  	ErrorLogger.getInstance().logMessage(lstHt);

    }
    //  	setCustomer(sys.getCustomer());
    //  	setInvoice(sys.getCurrentInvoice());

}
//class CurrencyColumnFormat extends DecimalFormat{
//    public CurrencyColumnFormat(){
//        this.applyPattern("####.##");
//        setMinimumFractionDigits(2);
//        setDecimalSeparatorAlwaysShown(true);
//    }
//    public String formatPrice(double val){
//        String results= format(val);
//        int dif = 7-results.length();
//        while(dif-- > 0){
//            results = "  " + results;
//        }
//        return "$" + results;
//    }
//}
