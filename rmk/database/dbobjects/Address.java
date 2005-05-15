package rmk.database.dbobjects;
import java.sql.*;

import rmk.ErrorLogger;

import carpus.database.Fixed;

public class Address extends DBObject{
    public static final int[] lengths={Fixed.LONG_SIZE, Fixed.LONG_SIZE, Fixed.LONG_SIZE, 
				       Fixed.LONG_SIZE, 30, 30, 30, 23, 2, 10, 30, 3, 
				       Fixed.INT_SIZE, Fixed.BOOLEAN_SIZE };
    public static final String[] fields={"AddressID", "CorrectedAddressID", "AddressType", 
					 "CustomerID", "ADDRESS0", "ADDRESS1", "ADDRESS2", 
					 "CITY", "STATE", "ZIP", "COUNTRY", "ZONE", 
					 "TimesUsed", "PrimaryCustomerAddress"};

    public static int[] getFieldLengths_txt(){	return 	lengths;    }
    public static int getTotalFieldLengths_txt(){return getTotalFieldLengths(lengths);}


    public void setAddressID(long value){values[0] = new Long(value);edited=true;}
    public void setCorrectedAddressID(long value){values[1] = new Long(value);edited=true;}
    public void setAddressType(long value){values[2] = new Long(value);edited=true;}
    public void setCustomerID(long value){values[3] = new Long(value);edited=true;}
    public void setAddress0(String value){values[4] = value;edited=true;}
    public void setAddress1(String value){values[5] = value;edited=true;}
    public void setAddress2(String value){values[6] = value;edited=true;}
    public void setCITY(String value){values[7] = value;edited=true;}
    public void setSTATE(String value){values[8] = value;edited=true;}
    public void setZIP(String value){values[9] = value;edited=true;}
    public void setCOUNTRY(String value){values[10] = value;edited=true;}
    public void setZONE(String value){values[11] = value;edited=true;}
    public void setTimesUsed(int value){values[12] = new Integer(value);edited=true;}
    public void setPrimaryCustomerAddress(boolean value){values[13] = new Boolean(value);edited=true;}


    public String getAddress(int index){
	return (String)values[4+index];
    }

    public Address(long ID){
	super(fields);
	setID(new Long(ID));
	setDefaults();
    }
    public Address(Object[]  data){
	super(fields);
	setDefaults();
	//  	address = new String[3];
	setValues(data);
	transfering = true;
    }

    private void setDefaults(){
	setAddressType(1);
	setCorrectedAddressID(0);
	setAddress0("");
	setAddress1("");
	setAddress2("");
	setCITY("");
	setSTATE("");
	setZIP("");
	setCOUNTRY("");
	setTimesUsed(0);
  	setZONE("");
    }


    public long getAddressID(){ return ((Long)values[0]).longValue();}
    public long getCorrectedAddressID(){ return values[1] != null?
					     ((Long)values[1]).longValue(): 0;}
    public long getAddressType(){ return ((Long)values[2]).longValue();}
    public long getCustomerID(){ return ((Long)values[3]).longValue();}
    public String getAddress0(){ return (String)values[4];}
    public String getAddress1(){ return (String)values[5];}
    public String getAddress2(){ return (String)values[6];}
    public String getCITY(){ return (String)values[7];}
    public String getSTATE(){ return (String)values[8];}
    public String getZIP(){ return (String)values[9];}
    public String getCOUNTRY(){ return (String)values[10];}
    public String getZONE(){ return (String)values[11];}
    public int getTimesUsed(){ return ((Integer)values[12]).intValue();}
    public boolean isPrimaryCustomerAddress(){ return ((Boolean)values[13]).booleanValue();}


    //      public void setValues(ResultSet  recordSet) throws Exception;
    public void setValues(Object[]  data){
	int i=0;
	setAddressID(Long.parseLong((String)data[i++]));
	setCorrectedAddressID(Long.parseLong((String)data[i++]));
	setAddressType(Long.parseLong((String)data[i++]));
	setCustomerID(Long.parseLong((String)data[i++]));
	setAddress0((String)data[i++]);
	setAddress1((String)data[i++]);
	setAddress2((String)data[i++]);
	setCITY((String)data[i++]);
	setSTATE((String)data[i++]);
	setZIP((String)data[i++]);
	setCOUNTRY((String)data[i++]);
	setZONE((String)data[i++]);
	setTimesUsed(Integer.parseInt((String)data[i++]));
	setPrimaryCustomerAddress((!((String)data[i++]).equals("0")));
    }

    public String toString(){
	String results="";
	results += "[A:" + carpus.util.Formatting.textSizer(""+getAddressID(), 6);
	if(getCorrectedAddressID() > 0) results += " Corrected:" + getCorrectedAddressID();
	//  	results += ":" + getCustomerID();
	results +=  " (" ;
	if(getAddress0() != null) results += getAddress0() + "|";
	if(getAddress1() != null) results += getAddress1() + "|";
	if(getAddress2() != null) results += getAddress2();
	results +=  ") ";
	results += getCITY() + ", " +getSTATE() + ", "+getZIP() ; //city:state:zip
	return results;
    }
    public String getIDSQL(){
	String qry = "";
	String dbClass = Configuration.Config.getDBType();
	if(dbClass.equals("ACCESS")){
	   qry = "select max(AddressID)+1 from address";
	} else
	   qry = "select nextval('Address_Addressid_seq')";
	return qry;
    }

    public String saveSql(long id) throws Exception{
	return saveSql("Address", id);
    }
    public String updateSql(long id) throws Exception{
	return updateSql("Address", "AddressID", id);
    }
    public String deleteSql(long id) throws Exception{
	return deleteSql("Address", "AddressID", id);
    }
    public String deactivateSql(long id) throws Exception{
	return deactivateSql("Address", "AddressID", id);
    }

    public void setValues(ResultSet  recordSet) throws Exception{
// 	setAddressID(recordSet.getInt("AddressID"));
	setCorrectedAddressID(recordSet.getInt("CorrectedAddressID"));
	setAddressType(recordSet.getInt("AddressType"));
	setCustomerID(recordSet.getInt("CustomerID"));
	setAddress0(recordSet.getString("ADDRESS0"));
	setAddress1(recordSet.getString("ADDRESS1"));
	setAddress2(recordSet.getString("ADDRESS2"));
	setCITY(recordSet.getString("CITY"));
	setSTATE(recordSet.getString("STATE"));
	setZIP(recordSet.getString("ZIP"));
	setCOUNTRY(recordSet.getString("COUNTRY"));
	setZONE(recordSet.getString("ZONE"));
	setTimesUsed(recordSet.getInt("TimesUsed"));
	setPrimaryCustomerAddress(recordSet.getBoolean("PrimaryCustomerAddress"));
    }

      public static void main(String args[])
	throws Exception
    {
	carpus.database.DBInterface db = Configuration.Config.getDB();
  	db.connect();
// 	carpus.database.PostgresDB db = new carpus.database.PostgresDB();
//   	db.connect();
	carpus.util.Formatting formatter= new carpus.util.Formatting();
	java.util.Vector lst = db.getItems("Address", "customerid = 1");
//  	java.util.ArrayList lst = db.getItems("Address", "AddressID < 10");

	lst = db.getItems("Address", "customerid = " + 10);
	Address item = (Address)lst.get(0);
	ErrorLogger.getInstance().logMessage(item.getIDSQL());

//  	for(int cust=0; cust<10000; cust++){
//  	    lst = db.getItems("Address", "customerid = " + cust);
//  	    for(int i=0; i < lst.size(); i++){
//  		Address item = (Address)lst.get(i);
		
//  		if(item.isPrimaryCustomerAddress())
//  		    System.out.print(formatter.textSizer("*"+item.getAddressID(),6));
//  		else
//  		    System.out.print(formatter.textSizer(""+item.getAddressID(),6));
//  		System.out.print(formatter.textSizer("",6));
//  		System.out.print(formatter.textSizer(item.getAddress0(),32));
//  		System.out.print(formatter.textSizer(item.getAddress1(),32));
//  		System.out.print(item.getAddress2());
//  		ErrorLogger.getInstance().logMessage();
//  	    }
//  	    ErrorLogger.getInstance().logMessage();
//  	}
    }


}

