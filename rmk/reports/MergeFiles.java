package rmk.reports;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import javax.swing.*;

import rmk.ErrorLogger;
import rmk.database.FinancialInfo;
import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.Address;


public class MergeFiles{
    public static final int MERGE_TYPE_DEALER_SPEC=0;
    public static final int MERGE_TYPE_BALANCE_DUE=1;
    public static final int MERGE_TYPE_WIERD_BALANCE_DUE=2;
    
    public static final String typeDesc[] = {"Dealer Specs", "Balance Due"};


    static final SimpleDateFormat dateFormatter = new SimpleDateFormat ("MMMM dd, yyyy");
    static final NumberFormat priceFormatter = NumberFormat.getNumberInstance();

//      priceFormatter.setMinimumFractionDigits(2);

    static String getDealerSpecRequestList() throws Exception{
	rmk.DataModel sys = rmk.DataModel.getInstance();
	String results = "";
	GregorianCalendar date = rmk.gui.Dialogs.getExpirationDate("Month/Year\nExample: Aug04 entered as 08/04", null);
	if(date == null) 
	    return results;
	Vector data = sys.invoiceInfo.getDealerSpecInvoices(date);
	if(data == null || data.size() == 0) return "";
	ErrorLogger.getInstance().logMessage("Got invoices");
	
	results += "Invoice|";
	results += "Estimated|";
	results += "FirstName|LastName|";
	results += "Address0|Address1|Address2|";
	results += "City|State|Zip|Country|";
	results += "Spaces";
	results += "\n";

	String[] lastRow=null;
	Invoice lastInv=null;
	for (Iterator<Invoice> iterator = data.iterator(); iterator.hasNext();) {
		Invoice invoice = (Invoice) iterator.next();
	    boolean dupe = false;
//  	    ErrorLogger.getInstance().logMessage(invoice.getInvoice());
	    
	    String row[] = getDealerRow(invoice);
	    if(lastInv != null){
		if(lastInv.getCustomerID() == invoice.getCustomerID()){
		    dupe = true;
		}
	    }
	    if(dupe){
		int lastCnt = Integer.parseInt(lastRow[11]);
		int currCnt = Integer.parseInt(row[11]);
//  		ErrorLogger.getInstance().logMessage("*** dupe cust ***" + lastCnt + ":" + currCnt 
		row[11] = ""+(lastCnt + currCnt);
//  		ErrorLogger.getInstance().logMessage("*** dupe cust ***" + lastRow[0] + ":" + row[0] + " (" + row[11] + ")"
//  				   + "  **   " + invoice.getCustomerID());
		lastInv = null;
	    } 

	    if(lastInv != null){
//  		ErrorLogger.getInstance().logMessage("add:"+ lastRow[0] + "  " + lastRow[11]);
		
		results += addRow(lastRow);
	    }

	    lastInv = invoice;
	    lastRow = (String[])row.clone();
	}
	if(lastInv != null){
//  	    ErrorLogger.getInstance().logMessage("add:"+ lastRow[0] + "  " + lastRow[11]);
	    results += addRow(lastRow);
	}
//  	if(lastInv != null){
//  	    ErrorLogger.getInstance().logMessage("add:"+ lastRow[0]);
//  	    results += addRow(lastRow);
//  	}

	return results;
    }
    // ------------------------------------------------
    static String addRow(String[] row){
	String results="";
	for(int rowNum = 0; rowNum< row.length; rowNum++){
	    results += row[rowNum] + "|";
	}
	results = results.substring(0, results.length()-1); // remove last delimiter
	
	results += "\n";
	return results;
    }


    // ------------------------------------------------
    static String[] getDealerRow(Invoice inv){
	rmk.DataModel sys = rmk.DataModel.getInstance();
	String [] results = new String[12];

	int col=0;
	results[col++] = ""+inv.getInvoice();	
	Date estDate = inv.getDateEstimated().getTime();
//	ErrorLogger.getInstance().logMessage("MergeFiles:getDealerRow:"+DataModel.db.dateStr(inv.getDateEstimated()));
	results[col++] = dateFormatter.format(estDate);

	try {
	    Customer cust = sys.customerInfo.getCustomerByID(inv.getCustomerID());
	    Address address = sys.customerInfo.getCurrentAddress(cust.getCustomerID());
	    results[col++] = clearNull(cust.getFirstName()) ;
	    results[col++] = clearNull(cust.getLastName()) ;
	    results[col++] = clearNull(address.getAddress(0)) ;
	    results[col++] = clearNull(address.getAddress(1)) ;
	    results[col++] = clearNull(address.getAddress(2)) ;
	    results[col++] = clearNull(address.getCITY()) ;
	    results[col++] = clearNull(address.getSTATE()) ;
	    results[col++] = clearNull(address.getZIP()) ;
	    results[col++] = clearNull(address.getCOUNTRY()) ;
  	    results[col++] = ""+sys.invoiceInfo.getKnifeCount((int)inv.getInvoice()) ;
	} catch (Exception e){
	} // end of try-catch

	return results;
    }

    //-----------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------
    static String getBalanceDueList() throws Exception{
	rmk.DataModel sys = rmk.DataModel.getInstance();
	String results = "";

  	java.util.GregorianCalendar date = new java.util.GregorianCalendar();
  	while(date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
  	    date.add(Calendar.DATE,1);

	date = rmk.gui.Dialogs.getDate("Estimated Ship Date", date);
	Vector data = sys.invoiceInfo.getBalanceDueInvoices(date);
	
	if(data == null || data.size() == 0) return "";

	Object sortedData[] = data.toArray();
	java.util.Arrays.sort(sortedData, new rmk.comparators.InvoicesInvoiceNumber());

	priceFormatter.setMinimumFractionDigits(2);
	priceFormatter.setMaximumFractionDigits(2);

	results += "Invoice|";
	results += "Ordered|Estimated|HoldDate|";
	results += "FirstName|LastName|";
	results += "Address0|Address1|Address2|";
	results += "City|State|Zip|Country|";
	results += "Due";
	results += "\n";

	for(int i=0; i< sortedData.length; i++){
	    Invoice invoice = (Invoice )sortedData[i];
//  	for(java.util.Enumeration enum = data.elements(); enum.hasMoreElements();){
//  	    Invoice invoice = (Invoice )enum.nextElement();
	    Customer cust = sys.customerInfo.getCustomerByID(invoice.getCustomerID());
	    Address address = sys.customerInfo.getCurrentAddress(cust.getCustomerID());
	    String tmpTxt="";

	    results += invoice.getInvoice() + "|";
	    results += dateFormatter.format(invoice.getDateOrdered().getTime()) + "|";
	    results += dateFormatter.format(invoice.getDateEstimated().getTime()) + "|";
	    GregorianCalendar holdDate = (GregorianCalendar )invoice.getDateEstimated().clone();
	    holdDate.add(Calendar.MONTH, 1);
	    results += dateFormatter.format(holdDate.getTime()) + "|";

	    results += clearNull(cust.getFirstName()) + "|";
	    results += clearNull(cust.getLastName()) + "|";
	    results += clearNull(address.getAddress(0)) + "|";
	    results += clearNull(address.getAddress(1)) + "|";
	    results += clearNull(address.getAddress(2)) + "|";
	    results += clearNull(address.getCITY()) + "|";
	    results += clearNull(address.getSTATE()) + "|";
	    results += clearNull(address.getZIP()) + "|";
	    results += clearNull(address.getCOUNTRY()) + "|";

	    double due = sys.financialInfo.getInvoiceDue(invoice);

	    results += "$" + priceFormatter.format(due) + "|";

	    results = results.substring(0, results.length()-1); // remove last comma

  	    results += "\n";
	}

	return results;
    }

    static String getWierdBalanceDueList() throws Exception{
    	rmk.DataModel sys = rmk.DataModel.getInstance();
    	String results="";
//		GregorianCalendar cutoff = new GregorianCalendar();
//		cutoff.add(GregorianCalendar.YEAR, -3);

    	FinancialInfo.INVOICE_DUE_CUTOFF = false;
		for(int invNum=1; invNum<70000; invNum++){
    		Invoice invoice = sys.invoiceInfo.getInvoice(invNum);
    		if(invoice!=null){
    			double due = sys.financialInfo.getInvoiceDue(invoice);
    			if(invoice.getDateShipped() != null && due > 0)
    				results += "" + invoice.getInvoice() + "\n";
    		}
    	}
    	FinancialInfo.INVOICE_DUE_CUTOFF = true;
    	
    	return results;
    }
    //-----------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------
    static String clearNull(String tmpTxt){
	if(tmpTxt == null || tmpTxt.equals("null")) tmpTxt = "";
	return (tmpTxt);
    }
    //-----------------------------------------------------------------------------------
    public static boolean generateMergeFile(int type, String location){
	String txt="";

	try{
		switch (type) {
		case MERGE_TYPE_DEALER_SPEC:
			txt = getDealerSpecRequestList();
			break;
		case MERGE_TYPE_BALANCE_DUE:
			txt = getBalanceDueList();
			break;
		case MERGE_TYPE_WIERD_BALANCE_DUE:
			txt = getWierdBalanceDueList();
			break;
		}
		if (txt.length() > 1) {
			return saveFile(txt, location);
		}

	} catch (Exception e){
	    JOptionPane.showMessageDialog(null, 
					  "Error generating " + typeDesc[type] + ":" + e, 
					  "Error"
					  , JOptionPane.WARNING_MESSAGE);
	}
	return false; 
    }
    static boolean saveFile(String data,String location){
	try{
	    FileOutputStream file=new FileOutputStream( location);
	    file.write(( data + "\n").getBytes());
	    return true;
	} catch (Exception e){
	    carpus.util.Logger.getInstance().logError(
						      "MergeFiles:" +
						      "saveFile(String data,String location)" + ":\n" +
						      "Unable to save data.\n" + e,
						      e
						      );
	    if((""+e).indexOf("is being used by another process") > 0){
	    	String errMsg = "Unable to save File:"+location+"\n File is probably open and must be closed to re-create.";
	    	JOptionPane.showMessageDialog(null, errMsg, "File Access Error",
                    JOptionPane.ERROR_MESSAGE);
	    }
	    return false;
	}
    }

    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    public static void main(String args[]) throws Exception{
//  //  	getDealerSpecRequestList();
//      	ErrorLogger.getInstance().logMessage("MergeFiles:main:"+getDealerSpecRequestList());
        	ErrorLogger.getInstance().logMessage("MergeFiles:main:"+getBalanceDueList());

  	generateMergeFile(MERGE_TYPE_WIERD_BALANCE_DUE, 
  			  Configuration.Config.getMergeFileLocation() + "WierdBalanceDue.txt");
  	
    	System.exit(0);
    }

}
