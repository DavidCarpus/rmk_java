package rmk.database;

import rmk.ErrorLogger;
import rmk.database.dbobjects.*;

import java.util.Vector;
import java.util.Hashtable;
import java.awt.*;

public class CustomerInfo{
    Hashtable customersByID= new Hashtable();
    static carpus.database.DBInterface db=null;
    Customer lastLoadedCust = null;
    
	public CustomerInfo(carpus.database.DBInterface db) {
		if (CustomerInfo.db != null)
			return;
		CustomerInfo.db = db;
	}
    //------------------------------------------------------
    void addCustomerToHashes(Customer customer){
	customersByID.put(customer.getID(), customer);
    }

    //==========================================================
    public Vector validate(Customer cust){ // returns errors
	Vector errors = new Vector();
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ inv.lst());
	String ccNum = cust.getCreditCardNumber();
	if(ccNum != null) ccNum = ccNum.trim();	
	else ccNum = "";

	if(ccNum.length() > 0){
	    if(!rmk.database.FinancialInfo.isValidCCNumber(ccNum)){
		errors.add("Invalid CreditCard Number.");
	    }
//  	    if(rmk.database.FinancialInfo.getVCode(ccNum) == 0)
//  		errors.add("Missing VCode.");

	    if(cust.getCreditCardExpiration() == null)
		errors.add("Must have Expiration date for CreditCard Number.");
	}

	if(errors.size() == 0) return null;
	return errors;
    }
    //==========================================================
    public void setCustCCNumber(long custID, String num, java.util.GregorianCalendar exp){
	ErrorLogger.getInstance().logMessage(this.getClass().getName() + "setCustCCNumber():"+  num);
	try { // first try to get CC # from customer data
	    Customer cust = getCustomerByID(custID);
	    if(num.endsWith("*"))
	        num = num.substring(0, num.length()-1);
	    cust.setCreditCardNumber(num);
	    cust.setCreditCardExpiration(exp);
	    Vector lst = new Vector();
	    lst.add(cust);
	    db.saveItems("Customers" , lst);
	} catch(Exception e){
	    rmk.ErrorLogger.getInstance().logError("setCustCCNumber(" + custID + "," + num + ")", e);
	}

    }

    //==========================================================
    public static String getFixedPhoneNumber(String phone){
	String results="";
	phone = phone.trim();
	if(phone.length() == 13){
	    return phone;
	} else if(phone.length() == 10){
	    results += "(" + phone.substring(0,3) + ")";
	    results += phone.substring(3,6);
	    results += "-";
	    results += phone.substring(6);
	} else{
	    results = phone;
	}
	return results.toUpperCase();
    }

    //------------------------------------------------------
    public boolean currentTaxIDonFile(long customerID){
	String results =  db.lookup("Customers", "TaxNumber", ""+customerID);
	if(results == null || results.trim().length() == 0){
	    return false;
	}
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":currentTaxIDonFile->"+ results);
	return true;
    }
    
    public boolean isDealer(long customerID){
    	if(lastLoadedCust == null || lastLoadedCust.getCustomerID() != customerID)
    		lastLoadedCust = (Customer) (db.getItems("Customers", "customerID = " + customerID)).get(0);	
		return lastLoadedCust.isDealer();
    }
    //------------------------------------------------------
    public Vector getCustomersByLastName(String criteria){
	Vector lst;
//  	lst = db.getItems("Customers", "lastname " + db.likeCriteria(criteria));
  	lst = db.getItems("Customers", "lastname " + db.likeCriteria(criteria) + ";");
	return lst;
    }
    //------------------------------------------------------
    public Vector getCustomersByName(String criteria){
	if(criteria.indexOf(",") <= 0)
	    return getCustomersByLastName(criteria);

	String first = criteria.substring(criteria.indexOf(",")+1).trim();
	String last = criteria.substring(0,criteria.indexOf(",")).trim();
	String dbCriteria = "";
	
	dbCriteria += "lastname " + db.likeCriteria(last);
	dbCriteria += " and ";
	dbCriteria += "firstname " + db.likeCriteria(first);
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ dbCriteria);
	
	return db.getItems("Customers", dbCriteria);
    }

    public Vector getCustomersByID(String criteria){
        int custID = Integer.parseInt(criteria.substring(1).trim());
    	return db.getItems("Customers", "CustomerID = " + custID );
    }
    //==========================================================
    public Customer getCustomerByID(long customerID) throws Exception{
    	if(lastLoadedCust == null || lastLoadedCust.getCustomerID() != customerID)
    		lastLoadedCust = (Customer) (db.getItems("Customers", "CustomerID = " + customerID)).get(0);	
	
    	return lastLoadedCust;
    }
    //------------------------------------------------------
    public String getCustomerState(long id) throws Exception{
    	Address addr = getCurrentAddress(id);

    	if(addr != null){
    	    return addr.getSTATE();
    	}
    	return "UNKNOWN";
        }
        
    public String getCustomerCountry(long id) throws Exception{
    	Address addr = getCurrentAddress(id);

    	if(addr != null){
    	    return addr.getCOUNTRY();
    	}
    	return "UNKNOWN";
        }
        
    
    public String csz(Customer cust) throws Exception{
	Address addr = getCurrentAddress(cust.getCustomerID());
	return csz(addr);
    }
    public String csz(Address addr){
	if(addr == null) return "No Current Address onn File";

	String results="";
	if(addr.getCITY() != null)	    results += addr.getCITY() + ",";
	if(addr.getSTATE() != null)	    results += addr.getSTATE() + " ";
	if(addr.getZIP() != null)	    results += addr.getZIP() + " ";
	if(addr.getCOUNTRY() != null)	    results += addr.getCOUNTRY() + " ";

	return results;
    }
    //==========================================================
    public Vector getMiniCustomers(){
	Vector results = new Vector();
	String criteria="";
	criteria += "customerid in (Select customerid from invoices where ";
  	criteria += " invoice in (Select invoice from invoiceentries where ";
  	criteria += " partid in (283,307,262) and quantity > 0";
	criteria += " )";

//  	criteria += "PONumber = '@'";
//  	criteria += "or PONumber = '#'";
//  	criteria += "or PONumber = '%'";
	criteria += ")";

  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ criteria);
	
	results = db.getItems("Customers", criteria);
	return results;
    }
    
    //==========================================================
    public Vector getCustomersFromPhone(String criteria){
	Vector lst; 
	if(criteria.length() == 13)
	    lst = db.getItems("Customers", "phonenumber = '" + criteria + "'");
	else
	    lst = db.getItems("Customers", "phonenumber " + db.likeCriteria(criteria));
	
	int records = lst.size();
	return lst;
    }
    //==========================================================
    public Vector getDealers(){
	Vector lst;
	Component parent = rmk.gui.Desktop.getInstance();;
	parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	lst = db.getItems("Customers", "dealer <> 0;");
	parent.setCursor(null);
	return lst;
    }
    //==========================================================
    public Vector getCustomerAddresses(long customerID){
	Vector customerAddresses = db.getItems("Address", "CustomerID = " + customerID + ";");
	
	return customerAddresses;
    }
    public Address getCustomerAddress(long addressID){
    	Vector customerAddresses = db.getItems("Address", "AddressID = " + addressID + ";");
    	if(customerAddresses != null && customerAddresses.size() >0 )
    		return (Address) customerAddresses.get(0);
    	return null;
    }
    //==========================================================
    public Address getCurrentAddress(long customerID) throws Exception{
	Vector customerAddresses = getCustomerAddresses(customerID);

	if(customerAddresses != null && customerAddresses.size()>0){
	    Customer customer = getCustomerByID(customerID);
	    for(int i=0; i< customerAddresses.size(); i++){
		Address address = (Address)customerAddresses.get(i);
		
		if(address.isPrimaryCustomerAddress() || address.getAddressID() == customer.getCurrentAddress())
		    return address;
	    }
	}
	return null;
    }
}
