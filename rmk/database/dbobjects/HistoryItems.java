package rmk.database.dbobjects;
import java.sql.*;
import carpus.database.Fixed;

public class HistoryItems extends DBObject{
    public static final int[] lengths={Fixed.LONG_SIZE, Fixed.LONG_SIZE, Fixed.DATE_SIZE };
    public static final String[] fields={"HistoryItemID","Invoice","DateStamp"};
    String customerName=""; 

    public static int[] getFieldLengths_txt(){	return 	lengths;    }
    public static int getTotalFieldLengths_txt(){	
	int ct=0;
	for(int i=0; i< lengths.length; i++){
	    ct += lengths[i];
	}
	return 	ct;    
    }

    public HistoryItems(Object[]  data){
	super(fields);
	setDefaults();
	setValues(data);
	transfering=true;
    }
    public HistoryItems(int ID){
	super(fields);
	setID(new Long(ID));
	setDefaults();
    }
    private void setDefaults(){
	setDate(new java.util.GregorianCalendar()); // Now
    }

    public long getHistoryItemID(){ return ((Long)values[0]).longValue();}
    public long getInvoice(){ return ((Long)values[1]).longValue();}
    public java.util.GregorianCalendar getDate(){return (java.util.GregorianCalendar)values[2];}
	public String getCustomerName(){ 
		return customerName;
	}
	
    public void setHistoryItemID(long value){values[0] = new Long(value);edited=true;}
    public void setInvoice(long value){ values[1] = new Long(value);edited=true;}
    public void setDate(java.util.GregorianCalendar value){values[2] = value;edited=true;}

	public void setCustomerName(String cust){
		customerName = cust;
	}
	
//      public void setValues(ResultSet  recordSet) throws Exception;
    public void setValues(Object[]  data){
	int i=0;
	setHistoryItemID(Long.parseLong((String)data[i++]));
	setDate(carpus.util.DateFunctions.gregorianFromString((String)data[i++]));
    }

    public String toString(){
	String results="";
//  	results +=  "prtType:" + getHistoryItemID() + " " + getDescription();
  	results +=  ""+ getInvoice() + ":" + carpus.util.DateFunctions.javaDateFromGregorian(getDate());
	return results;
    }
   public String getIDSQL(){	
	String qry = "";
	String dbClass = Configuration.Config.getDBType();
	if(dbClass.equals("ACCESS")){
	    qry = "select max(HistoryItemID)+1 from HistoryItems";
	} else if(dbClass.equals("MYSQL")){
		qry = "select null";
	} else
  	   qry = "select nextval('HistoryItems_HistoryItemid_seq')";
	return qry;
   }

    public String saveSql(long id) throws Exception{
	return saveSql("HistoryItems", id);
    }

    public String updateSql(long id) throws Exception{
	return updateSql("HistoryItems", "HistoryItemID", id);
    }
    public String deleteSql(long id) throws Exception{
	return deleteSql("HistoryItems", "HistoryItemID", id);
    }
    public String deactivateSql(long id) throws Exception{
	return deactivateSql("HistoryItems", "HistoryItemID", id);
    }

    public void setValues(ResultSet  recordSet) throws Exception{
// setPartID(recordSet.getInt("PartID"));
		setInvoice(recordSet.getLong("Invoice"));
		setDate(carpus.util.DateFunctions.gregorianFromString(recordSet.getString("DateStamp")));
    }
    public static void main(String args[]) throws Exception{
    }   
}
