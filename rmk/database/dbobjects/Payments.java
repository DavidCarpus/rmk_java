package rmk.database.dbobjects;
import java.sql.*;
import java.io.*;

import rmk.ErrorLogger;
import carpus.database.Fixed;

public class Payments extends DBObject{
    public static final int[] lengths={Fixed.LONG_SIZE, Fixed.LONG_SIZE, Fixed.FLOAT_SIZE, 
				       Fixed.CURRENCY_SIZE, 16, Fixed.DATE_SIZE, Fixed.LONG_SIZE,
				       6, Fixed.DATE_SIZE};
    public static final String[] fields={"PaymentID","CustomerID","Invoice",
					 "Payment","CheckNumber","PaymentDate", "PaymentType",
					 "VCODE", "ExpirationDate" };

    public static int[] getFieldLengths_txt(){	return 	lengths;    }
    public static int getTotalFieldLengths_txt(){	
	int ct=0;
	for(int i=0; i< lengths.length; i++){
	    ct += lengths[i];
	}
	return 	ct;    
    }

    public Payments(Object[]  data){
	super(fields);
	setValues(data);
	transfering=true;
    }
    public Payments(int ID){
	super(fields);
	setPaymentID(ID);
    }
//=====================================================================
public void setPaymentID(long value){values[0] = new Long(value);edited=true;}
public void setCustomerID(long value){values[1] = new Long(value);edited=true;}
public void setInvoice(double value){values[2] = new Double(value);edited=true;}
public void setPayment(double value){values[3] = new Double(value);edited=true;}
public void setCheckNumber(String value){values[4] = value;edited=true;}
public void setPaymentDate(java.util.GregorianCalendar value){values[5] = value;edited=true;}
public void setPaymentType(long value){values[6] = new Long(value);edited=true;}
public void setVCODE(String value){values[7] = value;edited=true;}
public void setExpirationDate(java.util.GregorianCalendar value){values[8] = value;edited=true;}
//=====================================================================
public long getPaymentID(){ return ((Long)values[0]).longValue();}
public long getCustomerID(){ return ((Long)values[1]).longValue();}
public double getInvoice(){ return ((Double)values[2]).doubleValue();}
public double getPayment(){ return ((Double)values[3]).doubleValue();}
public String getCheckNumber(){ return (String)values[4];}
public java.util.GregorianCalendar getPaymentDate(){ return (java.util.GregorianCalendar)values[5];}
public long getPaymentType(){ return ((Long)values[6]).longValue();}
public String getVCODE(){ return (String)values[7];}
public java.util.GregorianCalendar getExpirationDate(){ return (java.util.GregorianCalendar)values[8];}
//=====================================================================

    public void setValues(Object[]  data){
	int i=0;
setPaymentID(Long.parseLong((String)data[i++]));
setCustomerID(Long.parseLong((String)data[i++]));
setInvoice(Double.parseDouble((String)data[i++]));
setPayment(Double.parseDouble((String)data[i++]));
setCheckNumber((String)data[i++]);
setPaymentDate(carpus.util.DateFunctions.gregorianFromString((String)data[i++]));
setPaymentType(Long.parseLong((String)data[i++]));
setVCODE((String)data[i++]);
setExpirationDate(carpus.util.DateFunctions.gregorianFromString((String)data[i++]));
    }

    public String toString(){
	String results="";
	results +=  "pmtID:" + getPaymentID() + ": Cust:" + getCustomerID() + " $" + getPayment();
	return "[" + results + "]";
    }
    public String getIDSQL(){	
	String qry = "";
	String dbClass = Configuration.Config.getDBType();
	if(dbClass.equals("ACCESS")){
	    qry = "select max(PaymentID)+1 from Payments";
	} else if(dbClass.equals("MYSQL")){
		qry = "select null";
	} else
	    qry = "select nextval('Payments_Paymentid_seq')";
	return qry;
    }

    public String saveSql(long id) throws Exception{
	return saveSql("Payments", id);
    }
    public String updateSql(long id) throws Exception{
	return updateSql("Payments", "PaymentID", id);
    }
    public String deleteSql(long id) throws Exception{
	return deleteSql("Payments", "PaymentID", id);
    }
    public String deactivateSql(long id) throws Exception{
	return deactivateSql("Payments", "PaymentID", id);
    }

    public void setValues(ResultSet  recordSet) throws Exception{
//      fields={"PaymentID","CustomerID","Invoice",
//  					 "Payment","CheckNumber","PaymentDate"};
//  setPaymentID(recordSet.getInt("PaymentID"));
setCustomerID(recordSet.getInt("CustomerID"));
setInvoice(recordSet.getFloat("Invoice"));
setPayment(recordSet.getFloat("Payment"));
setCheckNumber(recordSet.getString("CheckNumber"));
setPaymentDate(carpus.util.DateFunctions.gregorianFromJavaDate(recordSet.getDate("PaymentDate")));
setPaymentType(recordSet.getInt("PaymentType"));
setVCODE(recordSet.getString("VCODE"));
setExpirationDate(carpus.util.DateFunctions.gregorianFromJavaDate(recordSet.getDate("ExpirationDate")));

//  	setShortDescription(recordSet.getString("shortDescription"));
//  	setLongDescription(recordSet.getString("longDescription"));
    }
    public static void main(String args[]) throws Exception{
//  	carpus.database.DBInterface db= Configuration.Config.getDB();
//  	java.util.Vector lst = db.getItems("Payments", "invoice=44469" );	

	carpus.database.PostgresDB db = new carpus.database.PostgresDB();
	db.connect();
	String fileName=Configuration.Config.getDataFileLocation("Payments");

	carpus.database.Fixed fixed = new carpus.database.Fixed();
	BufferedInputStream in = (new BufferedInputStream( new FileInputStream(fileName)));
	int row=0;
	byte[] currInput = new byte[getTotalFieldLengths_txt()+2]; // CR-LF
	Object[] lst;
	while( in.read(currInput)!= -1
){
//   && row < 200){
//  	    if(row < 5){
		lst = fixed.getArray(new String(currInput),lengths);
		ErrorLogger.getInstance().logMessage(fixed.list(lst));
		Payments item = new Payments(lst);
  		System.out.print(row);
    		ErrorLogger.getInstance().logMessage(":" + item.getPaymentID());
//  		ErrorLogger.getInstance().logMessage(item);		
    		java.util.Vector outputLst = new java.util.Vector();
		outputLst.add(item);
//        		if(db.saveItems("Payments", outputLst) == null) return;
//  	    }
	    row++;
	}
    }   
}
