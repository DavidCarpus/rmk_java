package rmk.database.dbobjects;
import java.sql.*;
import java.io.*;
import carpus.database.Fixed;

public class InvoiceEntryAdditions extends DBObject{
    int partType=0;

    public static final int[] lengths={Fixed.LONG_SIZE, Fixed.FLOAT_SIZE, 
				       Fixed.LONG_SIZE, Fixed.CURRENCY_SIZE};
    public static final String[] fields={"AdditionID","EntryID","PartID","Price"};

    public static int[] getFieldLengths_txt(){	return 	lengths;    }
    public static int getTotalFieldLengths_txt(){	
	int ct=0;
	for(int i=0; i< lengths.length; i++){
	    ct += lengths[i];
	}
	return 	ct;    
    }

    public InvoiceEntryAdditions(Object[]  data){
	super(fields);
	setValues(data);
	transfering=true;
    }
    public InvoiceEntryAdditions(int ID){
	super(fields);
	setID(new Long(ID));
	setDefaults();
    }
    private void setDefaults(){
	setEntryID(0);
	setPartID(0);
	setPrice(0);
    }
    public long getAdditionID(){ return ((Long)values[0]).longValue();}
    public long getEntryID(){ return ((Long)values[1]).longValue();}
    public long getPartID(){ return ((Long)values[2]).longValue();}
    public double getPrice(){ return ((Double)values[3]).doubleValue();}


    public void setAdditionID(long value){values[0] = new Long(value);edited=true;}
    public void setEntryID(long value){values[1] = new Long(value);edited=true;}
    public void setPartID(long value){values[2] = new Long(value);edited=true;}
    public void setPrice(double value){values[3] = new Double(value);edited=true;}

    public int getPartType(){ return partType;}
    public void setPartType(int type){partType = type;}


    public void setValues(ResultSet  recordSet) throws Exception{
	// 	setAdditionID(recordSet.getInt("AdditionID"));
	setEntryID((long)recordSet.getFloat("EntryID"));
	setPartID(recordSet.getInt("PartID"));
	setPrice(recordSet.getFloat("Price"));
    }

    public void setValues(Object[]  data){
	int i=0;
	setAdditionID(Long.parseLong((String)data[i++]));
	setEntryID((long)Double.parseDouble((String)data[i++]));
	setPartID(Long.parseLong((String)data[i++]));
	setPrice(Double.parseDouble((String)data[i++]));
    }

    public String toString(){
	String results="";
	results +=  "Iea:" + getAdditionID() 
	    + ": Eid:" + (int)getEntryID() 
	    + " #" + getPartID()
	    + " $" + getPrice()
	    ;
	return "[" + results +"]";
    }
    public String getIDSQL(){	
	String qry = "";
	String dbClass = Configuration.Config.getDBType();
	if(dbClass.equals("ACCESS")){
	    qry = "select max(AdditionID)+1 from InvoiceEntryAdditions";
	} else if(dbClass.equals("MYSQL")){
		qry = "select null";
	} else
	    qry = "select nextval('invoiceentryaddi_additionid_seq')";
	return qry;
    }
    public String saveSql(long id) throws Exception{
	return saveSql("InvoiceEntryAdditions", id);
    }
    public String updateSql(long id) throws Exception{
	return updateSql("InvoiceEntryAdditions", "AdditionID", id);
    }
    public String deleteSql(long id) throws Exception{
	return deleteSql("InvoiceEntryAdditions", "AdditionID", id);
    }
    public String deactivateSql(long id) throws Exception{
	return deactivateSql("InvoiceEntryAdditions", "AdditionID", id);
    }

    public static void main(String args[]) throws Exception{
	carpus.database.PostgresDB db = new carpus.database.PostgresDB();
	db.connect();
	String fileName=Configuration.Config.getDataFileLocation("F_InvoiceEntryAdditions");

	carpus.database.Fixed fixed = new carpus.database.Fixed();
	BufferedInputStream in = (new BufferedInputStream( new FileInputStream(fileName)));
	int row=0;
	byte[] currInput = new byte[getTotalFieldLengths_txt()+2]; // CR-LF
	Object[] lst;
	while( in.read(currInput)!= -1
	       ){
	    //     && row < 200){
  	    if(row > 200){
		lst = fixed.getArray(new String(currInput),lengths);
		fixed.list(lst);
		InvoiceEntryAdditions item = new InvoiceEntryAdditions(lst);
//    		ErrorLogger.getInstance().logMessage(row + ":" + item.getAdditionID());
//     		java.util.Vector outputLst = new java.util.Vector();
//  		outputLst.add(item);
//     		if(db.saveItems("InvoiceEntryAdditions", outputLst) == null) return;
	    }
	    row++;
	}
    }   
}
