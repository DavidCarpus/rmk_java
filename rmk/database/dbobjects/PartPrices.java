package rmk.database.dbobjects;
import java.sql.*;
import carpus.database.Fixed;

public class PartPrices extends DBObject{
    public static final int[] lengths={Fixed.LONG_SIZE, Fixed.LONG_SIZE,
				       Fixed.LONG_SIZE, Fixed.CURRENCY_SIZE, Fixed.BOOLEAN_SIZE};
    public static final String[] fields={"PartPriceID","PartID","Year","Price", "Discountable"};
    public static int[] getFieldLengths_txt(){	return 	lengths;    }
    public static int getTotalFieldLengths_txt(){	
  	int ct=0;
	for(int i =0; i< lengths.length; i++) ct += lengths[i];
	return 	ct;    
    }

    public PartPrices(Object[]  data){
	super(fields);
	setValues(data);
	transfering=true;
    }
    public PartPrices(int ID){
	super(fields);
	setID(new Integer(ID));
    }
    public long getPartPriceID(){
    	if(values[0].getClass().getName().endsWith("Integer"))
    		return ((Integer)values[0]).intValue();
    	else
    		return ((Long)values[0]).longValue();
    	}
    public long getPartID(){ return ((Long)values[1]).longValue();}
    public long getYear(){ return ((Long)values[2]).longValue();}
    public double getPrice(){ return ((Double)values[3]).doubleValue();}
    public boolean isDiscountable(){return ((Boolean)values[4]).booleanValue();}

    public void setPartPriceID(long value){values[0] = new Long(value);edited=true;}
    public void setPartID(long value){values[1] = new Long(value);edited=true;}
    public void setYear(long value){values[2] = new Long(value);edited=true;}
    public void setPrice(double value){values[3] = new Double(value);edited=true;}
    public void setDiscountable(boolean value){values[4] = new Boolean(value);edited=true;}

//      public void setValues(ResultSet  recordSet) throws Exception;
    public void setValues(Object[]  data){
	int i=0;
	setPartPriceID(Long.parseLong((String)data[i++]));
	setPartID(Long.parseLong((String)data[i++]));
	setYear(Long.parseLong((String)data[i++]));
	setPrice(Double.parseDouble((String)data[i++]));
	setDiscountable((!((String)data[i++]).equals("0")));
    }

    public String toString(){
	String results="";
	results +=  "prtPr:" + getPartID() + ":" + getYear() + " Price:" + getPrice();
	return "[" + results + "]";
    }
   public String getIDSQL(){	
	String qry = "";
	String dbClass = Configuration.Config.getDBType();
	if(dbClass.equals("ACCESS")){
	    qry = "select max(PartPriceID)+1 from PartPrices";
	} else if(dbClass.equals("MYSQL")){
		qry = "select null";
	}else 
	    qry = "select nextval('PartPrices_PartPriceid_seq')";
	return qry;
   }

    public String saveSql(long id) throws Exception{
	return saveSql("PartPrices", id);
    }
    public String updateSql(long id) throws Exception{
	return updateSql("PartPrices", "PartPriceID", id);
    }
    public String deleteSql(long id) throws Exception{
	return deleteSql("PartPrices", "PartPriceID", id);
    }
    public String deactivateSql(long id) throws Exception{
	return deactivateSql("PartPrices", "PartPriceID", id);
    }

    public void setValues(ResultSet recordSet) throws Exception{
	setPartID(recordSet.getInt(fields[1]));
	setYear(recordSet.getInt(fields[2]));
	setPrice(recordSet.getDouble(fields[3]));
	setDiscountable(recordSet.getBoolean(fields[4]));
    }
    public static void main(String args[]) throws Exception{
	carpus.database.DBInterface db= Configuration.Config.getDB();
	java.util.Vector lst = db.getItems("PartPrices", "year=2003" );	

//  	carpus.database.PostgresDB db = new carpus.database.PostgresDB();
//  	db.connect();
//  	String fileName=Configuration.Config.getDataFileLocation("F_PartPrices");

//  	carpus.database.Fixed fixed = new carpus.database.Fixed();
//  	BufferedInputStream in = (new BufferedInputStream( new FileInputStream(fileName)));
//  	int row=0;
//  	byte[] currInput = new byte[getTotalFieldLengths_txt()+2]; // CR-LF
//  	Object[] lst;
//  	while( in.read(currInput)!= -1
//  ){
//  //   && row < 200){
//  //  	    if(row < 5){
//  		lst = fixed.getArray(new String(currInput),lengths);
//  		fixed.list(lst);
//  		PartPrices item = new PartPrices(lst);
//    		System.out.print(row);
//      		ErrorLogger.getInstance().logMessage(":" + item);
//  //  		ErrorLogger.getInstance().logMessage(item);		
//  //      		java.util.Vector outputLst = new java.util.Vector();
//  //  		outputLst.add(item);
//  //        		if(db.saveItems("PartPrices", outputLst) == null) return;
//  //  	    }
//  	    row++;
//  	}
    }   
}
