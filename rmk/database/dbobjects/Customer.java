package rmk.database.dbobjects;
import java.sql.*;
import java.util.Enumeration;
import java.util.Vector;

import carpus.database.Fixed;

public class Customer extends DBObject{
    public static final int[] lengths={Fixed.LONG_SIZE, 13, 30, 30, Fixed.LONG_SIZE, 
				       Fixed.FLOAT_SIZE, Fixed.FLOAT_SIZE, Fixed.INT_SIZE, 
				       Fixed.BOOLEAN_SIZE, Fixed.MEMO_SIZE,
				       5, 5, 1, 20, 30, 30, Fixed.DATE_SIZE ,50};
    public static int[] getFieldLengths_txt(){	return 	lengths;    }
    public static int getTotalFieldLengths_txt(){return getTotalFieldLengths(lengths);}
    public static final String[] fields={"CustomerID","PhoneNumber","LastName","FirstName",
					 "CurrrentAddress","Balance","Discount","Dealer","Flag",
					 "Memo","Prefix","Suffix","Terms","TaxNumber","BladeList",
					 "CreditCardNumber","CreditCardExpiration","EMailAddress"};
    public final int TERMS_IN_ADVANCED = 1;
    public final int TERMS_INVOICE = 2;
    
    java.util.Vector invoices;

    public Customer(Object[]  data){
	super(fields);
	setDefaults();
	setValues(data);
	transfering = true;
    }
    public Customer(int ID){
	super(fields);
	setID(new Long(ID));
	setDefaults();
    }
    private void setDefaults(){
	setDealer(0);
	setFlag(false);
	setCreditCardExpiration(null);
	setCurrentAddress(0);
	setDiscount(0);
	setBalance(0);
	invoices = null;
    }

    public void setCustomerID(long value){values[0] = new Long(value);edited=true;}
    public void setPhoneNumber(String value){values[1] = value;edited=true;}
    public void setLastName(String value){values[2] = value;edited=true;}
    public void setFirstName(String value){values[3] = value;edited=true;}
    public void setCurrentAddress(long value){values[4] = new Long(value);edited=true;}
    public void setBalance(double value){values[5] = new Double(value);edited=true;}
    public void setDiscount(double value){values[6] = new Double(value);edited=true;}
    public void setDealer(int value){values[7] = new Integer(value);edited=true;}
    public void setFlag(boolean value){values[8] = new Boolean(value);edited=true;}
    public void setMemo(String value){values[9] = value;edited=true;}
    public void setPrefix(String value){values[10] = value;edited=true;}
    public void setSuffix(String value){values[11] = value;edited=true;}
    public void setTerms(String value){values[12] = value;edited=true;}
    public void setTaxNumber(String value){values[13] = value;edited=true;}
    public void setBladeList(String value){values[14] = value;edited=true;}
    public void setCreditCardNumber(String value){values[15] = value;edited=true;}
    public void setCreditCardExpiration(java.util.GregorianCalendar value){values[16] = value;edited=true;}
    public void setEMailAddress(String value){values[17] = value;edited=true;}




    public long getCustomerID(){ 
    	return ((Long)values[0]).longValue();
    	}
    public String getPhoneNumber(){ return (String)values[1];}
    public String getLastName(){ return (String)values[2];}
    public String getFirstName(){ return (String)values[3];}
    public long getCurrentAddress(){ return ((Long)values[4]).longValue();}
    public double getBalance(){ return ((Double)values[5]).doubleValue();}
    public double getDiscount(){ return ((Double)values[6]).doubleValue();}
    public int getDealer(){ return ((Integer)values[7]).intValue();}
    public boolean isDealer(){ return ((Integer)values[7]).intValue() != 0;}
    public boolean isFlag(){ return ((Boolean)values[8]).booleanValue();}
    public String getMemo(){ return (String)values[9];}
    public String getPrefix(){ return (String)values[10];}
    public String getSuffix(){ return (String)values[11];}
    public String getTerms(){ return (String)values[12];}
    public String getTaxNumber(){ return (String)values[13];}
    public String getBladeList(){ return (String)values[14];}
    public String getCreditCardNumber(){ return (String)values[15];}
    public java.util.GregorianCalendar getCreditCardExpiration(){ return (java.util.GregorianCalendar)values[16];}
    public String getEMailAddress(){ return (String)values[17];}

    //      public void setValues(ResultSet  recordSet) throws Exception;
    public void setValues(Object[]  data){
	int i=0;
	setCustomerID(Long.parseLong((String)data[i++]));
	setPhoneNumber((String)data[i++]);
	setLastName((String)data[i++]);
	setFirstName((String)data[i++]);
	setCurrentAddress(Long.parseLong((String)data[i++]));
	if(data[i] == null || (""+data[i]).equals(""))
	    data[i] = "0";
	setBalance(Double.parseDouble((String)data[i++]));
	if(data[i] == null || (""+data[i]).equals(""))
	    data[i] = "0";
	setDiscount(Double.parseDouble((String)data[i++]));	
	setDealer(Integer.parseInt((String)data[i++]));
	setFlag((!((String)data[i++]).equals("0")));
	setMemo((String)data[i++]);
	setPrefix((String)data[i++]);
	setSuffix((String)data[i++]);
	setTerms((String)data[i++]);
	setTaxNumber((String)data[i++]);
	setBladeList((String)data[i++]);
	setCreditCardNumber((String)data[i++]);
	setCreditCardExpiration(carpus.util.DateFunctions.gregorianFromString((String)data[i++]));
    }

    public String toString(){
	String results="";
	results += "C:" + getID() + ":";
	if(getPrefix() != null)  results += getPrefix() + " ";
	if(getFirstName() != null)  results += getFirstName() + " ";
	if(getLastName() != null)  results += getLastName() + " ";
	if(isDealer())
		results += "(DLR)";
//	results += getPhoneNumber();
	results += "  " + getDiscount();
	return "[" + results + "]";
    }

    public void setValues(ResultSet  recordSet) throws Exception{
// setCustomerID(recordSet.getInt("CustomerID"));
	setPhoneNumber(recordSet.getString("PhoneNumber"));
	setLastName(recordSet.getString("LastName"));
	setFirstName(recordSet.getString("FirstName"));
	setCurrentAddress(recordSet.getInt("CurrrentAddress"));
	setBalance(recordSet.getFloat("Balance"));
	setDiscount(recordSet.getFloat("Discount"));
	setDealer(recordSet.getInt("Dealer"));
	setFlag(recordSet.getBoolean("Flag"));
	String memo = recordSet.getString("Memo");
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ memo);
	
	setMemo(memo);
	setPrefix(recordSet.getString("Prefix"));
	setSuffix(recordSet.getString("Suffix"));
	setTerms(recordSet.getString("Terms"));
	setTaxNumber(recordSet.getString("TaxNumber"));
	setBladeList(recordSet.getString("BladeList"));
	setCreditCardNumber(recordSet.getString("CreditCardNumber"));
	setCreditCardExpiration(carpus.util.DateFunctions.gregorianFromString(recordSet.getString("CreditCardExpiration")));
	setEMailAddress(recordSet.getString("EMailAddress"));
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Done loading...");
	
    }
    public String getIDSQL(){
	String qry = "";
	String dbClass = Configuration.Config.getDBType();
	if(dbClass.equals("ACCESS")){
	    qry = "select max(CustomerID)+1 from customers";
	} else if(dbClass.equals("MYSQL")){
		qry = "select null";
	} else
	    qry = "select nextval('Customers_Customerid_seq')";
	return qry;
    }

    public String saveSql(long id) throws Exception{
	return saveSql("Customers", id);
    }
    public String updateSql(long id) throws Exception{
	return updateSql("Customers", "CustomerID", id);
    }
    public String deleteSql(long id) throws Exception{
	return deleteSql("Customers", "CustomerID", id);
    }
    public String deactivateSql(long id) throws Exception{
	return deactivateSql("Customers", "CustomerID", id);
    }

	/**
	 * @return
	 */
	public java.util.Vector getInvoices() {
		return invoices;
	}

	/**
	 * @param vector
	 */
	public void setInvoices(java.util.Vector vector) {
		invoices = vector;
	}

	public void addInvoice(Invoice invoice){
		if(invoices == null) invoices = new Vector();
		if(invoices.contains(invoice)) return; // THE object is already there
		for(Enumeration enum = invoices.elements(); enum.hasMoreElements();){
			Invoice inv = (Invoice) enum.nextElement();
			if(inv.getInvoice() == inv.getInvoice()){
				invoices.remove(inv);
				invoices.add(invoice);
			}
		}

	}
}
