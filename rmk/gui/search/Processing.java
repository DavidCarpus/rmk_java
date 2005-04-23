package rmk.gui.search;

import java.util.Vector;
import java.util.GregorianCalendar;

import rmk.DataModel;
import rmk.ErrorLogger;
import rmk.database.dbobjects.*;

public class Processing{
    static rmk.DataModel sys = rmk.DataModel.getInstance();

    public static Vector getSearchResults(String type, Vector criteria) throws Exception{
	Vector results = null;
	results = new Vector();
	if(criteria != null){
	    ErrorLogger.getInstance().logMessage(""+criteria);
	    ErrorLogger.getInstance().logMessage(type);
	    if(type.equals("InvoiceEntries")){
//  		String dbCriteria = getEntriesCriteriaString(criteria);
//      		ErrorLogger.getInstance().logMessage(dbCriteria);
//  		results = sys.db.getItems("InvoiceEntries", getEntriesCriteriaString(criteria));
  	    } else if(type.equals("Invoices")){
		return getInvoices(criteria);
	    }
	}
	return null;
    }
    public static Vector getInvoices(Vector criteria){
	String invoiceCriteria = entriesInvoiceNumberCriteria(criteria);
	String dateCriteria = entriesInvoiceDateCriterias(criteria);
	String partCriteria = entriesPartNumberCriteria(criteria);
	String featureCriteria = entriesFeaturesCriteria(criteria);
	
	String invCriteria = "";
	invCriteria += invoiceCriteria;
	if(dateCriteria.length() > 0){
	    if(invCriteria.length() > 0)	    invCriteria += " and ";
	    invCriteria += dateCriteria;
	}
	if(partCriteria.length() > 0){
	    if(invCriteria.length() > 0)	    invCriteria += " and ";
	    invCriteria += " invoice in (select invoice from invoiceentries where " + partCriteria + ")";
	}

//  	ErrorLogger.getInstance().logMessage(invCriteria);
	Vector invoices = DataModel.db.getItems("Invoices", invCriteria);
	// now have invoices that match invoice info and model #, if entered
	if(invoices == null) return null;

//  	for(java.util.Enumeration enum = invoices.elements(); enum.hasMoreElements();){
//  	    Invoice invoice = (Invoice )enum.nextElement();
//  	    String entryCriteria = " invoiceentryadditions";
//  	    Vector entries = sys.db.getItems("InvoiceEntries", );
//  	}

	return invoices;
    }
//      public static Vector getSearchResults(String type, Vector criteria){
//  	Vector results = null;
//  	results = new Vector();
//  	if(criteria != null){
//  	    ErrorLogger.getInstance().logMessage(criteria);
//  	    ErrorLogger.getInstance().logMessage(type);
//  	    if(type.equals("InvoiceEntries")){
//  		String dbCriteria = getEntriesCriteriaString(criteria);
//      		ErrorLogger.getInstance().logMessage(dbCriteria);
//  		results = sys.db.getItems("InvoiceEntries", getEntriesCriteriaString(criteria));
//  	    }
//  	    if(type.equals("Invoices")){
//  		String dbCriteria = getInvoicesCriteriaString(criteria);
//      		ErrorLogger.getInstance().logMessage(dbCriteria);
//      		results = sys.db.getItems("Invoices", dbCriteria);
//  	    }
//  	    return results;
//  	}
//  	return null;
//      }
    //===============================================================
//      static String getEntriesCriteriaString(Vector criteriaList){
//  	String results = "";
//  	results += entriesInvoiceNumberCriteria(criteriaList);
//  	if(results.length() > 0)	    results += " and ";
//  	results += entriesPartNumberCriteria(criteriaList);

//  	String dateCriteria = entriesInvoiceDateCriterias(criteriaList);
//  	if(dateCriteria.length() > 0){
//  	    if(results.length() > 0)	    results += " and ";
//  	    results += "invoice in (select invoice from invoices where " + dateCriteria + ")";
//  	}

//  	ErrorLogger.getInstance().logMessage(results);	    
//  	return results;
//      }
    //===============================================================

//      static String getInvoicesCriteriaString(Vector criteriaList){
//  	String results = "";
//  	String numberCriteria = entriesInvoiceNumberCriteria(criteriaList);
//  	String dateCriteria = entriesInvoiceDateCriterias(criteriaList);
//  	String partCriteria = entriesPartNumberCriteria(criteriaList);
//  	String featureCriteria = entriesFeaturesCriteria(criteriaList);

//  	results += numberCriteria;

//  	//-----------------------
//  	if(dateCriteria.length() > 0){
//  	    if(results.length() > 0)	    results += " and ";
//  	    results += dateCriteria;
//  	}
//  	//-----------------------
//  	if(partCriteria.length() > 0){
//  	    if(results.length() > 0)	    results += " and ";
//  	    results += "invoice in (select invoice from invoiceEntries where ";
//  	    results += partCriteria;
//  	    if(numberCriteria.length() > 0){
//  		if(results.length() > 0)	    results += " and ";
//  		results += numberCriteria;
//  	    }
//  	    results += ")";
//  	}
//  	//-----------------------
//  	if(featureCriteria.length() > 0){
//  	    if(results.length() > 0)	    results += " and ";
//  	    results += "invoice in (select invoice from invoiceEntries";

//  	    String field1 = sys.db.table_fieldName("invoiceentries.invoiceentryid");
//  	    String field2 = sys.db.table_fieldName("invoiceentryadditions.entryid");
    
//  	    results += " left join invoiceentryadditions on " + field1 + "=" + field2;
//  	    results += " where ";
//  	    results += featureCriteria;
//  	    results += ")";
//  	}


//  	ErrorLogger.getInstance().logMessage(results);	    
//  	return results;
//      }

    static String entriesInvoiceNumberCriteria(Vector criteriaList){
	String results="";
	for(java.util.Enumeration enum = criteriaList.elements(); enum.hasMoreElements();){
	    String criteria = (String )enum.nextElement();
	    int split = criteria.indexOf("-");
	    String field = criteria.substring(0,split);
	    String value = criteria.substring(split+1);
	    if(field.equals("InvoiceStart")){
		if(results.length() > 0)	    results += " and ";
		results += " invoice >= " +  value;
	    }
	    if(field.equals("InvoiceEnd")){
		if(results.length() > 0)	    results += " and ";
		results += " invoice <= " +  value;
	    }
	}
	return results;
    }
    static String entriesPartNumberCriteria(Vector criteriaList){
	String results="";
	for(java.util.Enumeration enum = criteriaList.elements(); enum.hasMoreElements();){
	    String criteria = (String )enum.nextElement();
	    int split = criteria.indexOf("-");
	    String field = criteria.substring(0,split);
	    String value = criteria.substring(split+1);
	    if(field.equals("InvoiceItemModel")){
		if(results.length() > 0)	    results += " and ";

		
		results += " partid  in ";
		Vector partCodes = sys.partInfo.getPartCodesFromString(value);
		Vector parts = sys.partInfo.getPartsFromPartCodeVector(partCodes);

		results += "(";
		for(java.util.Enumeration enum2 = parts.elements(); enum2.hasMoreElements();){
		    Parts part = (Parts )enum2.nextElement();
		    if(part != null)
			results += part.getPartID() + ",";
		}
		results = results.substring(0,results.length()-1);
		results += ")";

//  		results += " partid  = ";
//  		results += sys.partInfo.getPartIDFromCode(value);
	    }
	}
	return results;
    }
    static String entriesFeaturesCriteria(Vector criteriaList){
	String results="";
	for(java.util.Enumeration enum = criteriaList.elements(); enum.hasMoreElements();){
	    String criteria = (String )enum.nextElement();
	    int split = criteria.indexOf("-");
	    String field = criteria.substring(0,split);
	    String value = criteria.substring(split+1).toUpperCase();
	    if(field.equals("InvoiceFeatures")){
		if(results.length() > 0)	    results += " and ";
		String partField = DataModel.db.table_fieldName("invoiceentryadditions.partid");
		results += " " + partField + " in ";

		Vector partCodes = sys.partInfo.getPartCodesFromString(value);
		ErrorLogger.getInstance().logMessage("Processing:entriesFeaturesCriteria:" + partCodes);
		Vector parts = sys.partInfo.getPartsFromPartCodeVector(partCodes);

		results += "(";
		for(java.util.Enumeration enum2 = parts.elements(); enum2.hasMoreElements();){
		    Parts part = (Parts )enum2.nextElement();
		    results += part.getPartID() + ",";
		}
		results = results.substring(0,results.length()-1);
		results += ")";
	    }
	}
	return results;
    }

    static String entriesInvoiceDateCriterias(Vector criteriaList){
	String results="";
	GregorianCalendar date;
	for(java.util.Enumeration enum = criteriaList.elements(); enum.hasMoreElements();){
	    String criteria = (String )enum.nextElement();
	    int split = criteria.indexOf("-");
	    String field = criteria.substring(0,split);
	    String value = criteria.substring(split+1);
//      static final String fieldNames[] = {"InvoiceFeatures"};

	    if(field.equals("InvoiceEstimatedStart")){
		if(results.length() > 0)	    results += " and ";
		date = carpus.util.DateFunctions.gregorianFromString(value);
		results += " DateEstimated >= " + DataModel.db.dateStr(date);
	    }
	    if(field.equals("InvoiceEstimatedEnd")){
		if(results.length() > 0)	    results += " and ";
		date = carpus.util.DateFunctions.gregorianFromString(value);
		results += "  DateEstimated <= " +  DataModel.db.dateStr(date);
	    }
	    if(field.equals("InvoiceShippedStart")){
		if(results.length() > 0)	    results += " and ";
		date = carpus.util.DateFunctions.gregorianFromString(value);
		results += " DateShipped >= " + DataModel.db.dateStr(date);
	    }
	    if(field.equals("InvoiceShippedEnd")){
		if(results.length() > 0)	    results += " and ";
		date = carpus.util.DateFunctions.gregorianFromString(value);
		results += "  DateShipped <= " +  DataModel.db.dateStr(date);
	    }
	}
	return results;
    }

    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    public static void main(String args[]) throws Exception{
	Vector tmp = new Vector();
//  	tmp.add("InvoiceStart-4000");
//  	tmp.add("InvoiceEnd-6000");

	tmp.add("InvoiceShippedStart-9/1/03");

	tmp.add("InvoiceItemModel-11-3 1/4,11-4,11-5");
	tmp.add("InvoiceFeatures-wt,cw");

  	Vector data = getSearchResults("Invoices",tmp);
//    	Vector data = getSearchResults("InvoiceEntries",tmp);
//  	Vector data = rmk.gui.Dialogs.getSearchResults();
	if(data != null){
	    for(java.util.Enumeration enum = data.elements(); enum.hasMoreElements();){	    
		ErrorLogger.getInstance().logMessage(""+enum.nextElement());
	    }
	}
    	System.exit(0);
    }
}
