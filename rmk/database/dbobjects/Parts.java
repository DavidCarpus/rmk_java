package rmk.database.dbobjects;
import java.sql.*;
import java.io.*;
import carpus.database.Fixed;

public class Parts extends DBObject{
    public static final int[] lengths={Fixed.LONG_SIZE, 15, 30,
				       Fixed.BOOLEAN_SIZE, Fixed.BOOLEAN_SIZE, Fixed.BOOLEAN_SIZE, 
				       Fixed.BOOLEAN_SIZE, Fixed.BOOLEAN_SIZE, Fixed.LONG_SIZE, Fixed.BOOLEAN_SIZE};
    public static final String[] fields={"PartID","PartCode","Description","Discountable",
					 "BladeItem","Taxable","Sheath","Active", "PartType", "AskPrice"};

//      public static final String[] partTypes={"Knives", "Blades", "Handles", "Hilt", "Butt", "Misc"};

//      public static final int KNIFE_TYPE = 1;
//      public static final int BLADE_TYPE = 2;
//      public static final int HANDLE_TYPE = 3;
//      public static final int HILT_TYPE = 4;
//      public static final int BUTT_TYPE = 5;

//      public static final int MISC_TYPE = 99;

//      public static String getPartTypeDesc(int type){
//  	String desc="Unknown";
//  	if(type>=1 && type <= 4) desc = partTypes[type-1];
//  	if(type == 99) desc =  "Misc";
//  	if(type == 9999) desc = "InActive";
//  	return desc;
//      }
    public static int[] getFieldLengths_txt(){	return 	lengths;    }
    public static int getTotalFieldLengths_txt(){	
	int ct=0;
	for(int i=0; i< lengths.length; i++){
	    ct += lengths[i];
	}
	return 	ct;    
    }

    public Parts(Object[]  data){
	super(fields);
	defaults();
	setValues(data);
	transfering=true;
    }
    public Parts(int ID){
	super(fields);
	defaults();
	setID(new Long(ID));
    }

    public long getPartID(){ return ((Long)values[0]).longValue();}
    public String getPartCode(){ return (String)values[1];}
    public String getDescription(){ return (String)values[2];}
    public boolean isDiscountable(){ return ((Boolean)values[3]).booleanValue();}
    public boolean isBladeItem(){ return ((Boolean)values[4]).booleanValue();}
    public boolean isTaxable(){ return ((Boolean)values[5]).booleanValue();}
    public boolean isSheath(){ return ((Boolean)values[6]).booleanValue();}
    public boolean isActive(){ return ((Boolean)values[7]).booleanValue();}
    public int getPartType(){ return ((Integer)values[8]).intValue();}
    public boolean askPrice(){ return ((Boolean)values[9]).booleanValue();}

    public void setPartID(long value){values[0] = new Long(value);edited=true;}
    public void setPartCode(String value){values[1] = value;edited=true;}
    public void setDescription(String value){values[2] = value;edited=true;}
    public void setDiscountable(boolean value){values[3] = new Boolean(value);edited=true;}
    public void setBladeItem(boolean value){values[4] = new Boolean(value);edited=true;}
    public void setTaxable(boolean value){values[5] = new Boolean(value);edited=true;}
    public void setSheath(boolean value){values[6] = new Boolean(value);edited=true;}
    public void setActive(boolean value){values[7] = new Boolean(value);edited=true;}
    public void setPartType(int value){values[8] = new Integer(value);edited=true;}
    public void setAskPrice(boolean value){values[9] = new Boolean(value);edited=true;}

//      public void setValues(ResultSet  recordSet) throws Exception;
    public void setValues(Object[] data) {
        int i = 0;
        setPartID(Long.parseLong((String) data[i++]));
        setPartCode((String) data[i++]);
        setDescription((String) data[i++]);
        setDiscountable((!((String) data[i++]).equals("0")));
        setBladeItem((!((String) data[i++]).equals("0")));
        setTaxable((!((String) data[i++]).equals("0")));
        setSheath((!((String) data[i++]).equals("0")));
        setActive((!((String) data[i++]).equals("0")));
        if (data.length >= i && ((String) data[i]).length() > 0) {
            String val = (String) data[i++];
            setPartType(Integer.parseInt(val));
        }
    }
    
    void defaults(){
    	setPartCode("");
    	setDescription("");
    	setDiscountable(false);
    	setBladeItem(false);
    	setTaxable(true);
    	setSheath(false);
    	setActive(true);
    	setAskPrice(false);
    	setPartType(99);
    }

    public String toString(){
	String results="";
	results +=  "prt:" + getPartID() + ":" + getPartCode() + " " + getDescription();
	return "[" + results + "]";
    }
   public String getIDSQL(){	
	String qry = "";
	String dbClass = Configuration.Config.getDBType();
	if(dbClass.equals("ACCESS")){
	    qry = "select max(PartID)+1 from Parts";
	} else if(dbClass.equals("MYSQL")){
		qry = "select null";
	} else
  	   qry = "select nextval('Parts_Partid_seq')";
	return qry;
   }

    public String saveSql(long id) throws Exception{
	return saveSql("Parts", id);
    }

    public String updateSql(long id) throws Exception{
	return updateSql("Parts", "PartID", id);
    }
    public String deleteSql(long id) throws Exception{
	return deleteSql("Customers", "CustomerID", id);
    }
    public String deactivateSql(long id) throws Exception{
	return deactivateSql("Customers", "CustomerID", id);
    }

    public void setValues(ResultSet  recordSet) throws Exception{
// setPartID(recordSet.getInt("PartID"));
	setPartCode(recordSet.getString("PartCode"));
	setDescription(recordSet.getString("Description"));
	setDiscountable(recordSet.getBoolean("Discountable"));
	setBladeItem(recordSet.getBoolean("BladeItem"));
	setTaxable(recordSet.getBoolean("Taxable"));
	setSheath(recordSet.getBoolean("Sheath"));
	setActive(recordSet.getBoolean("Active"));
	setPartType(recordSet.getInt("PartType"));
	setAskPrice(recordSet.getBoolean("AskPrice"));
    }
    public static void main(String args[]) throws Exception{
// 	carpus.database.PostgresDB db = new carpus.database.PostgresDB();
// 	db.connect();
 	String fileName=Configuration.Config.getDataFileLocation("Parts");

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
//  		fixed.list(lst);
		System.out.println(fixed.list(lst));
		Parts item = new Parts(lst);
 		System.out.print(row);
   		System.out.println(":" + item);
		System.out.println(item);		
//      		java.util.Vector outputLst = new java.util.Vector();
//  		outputLst.add(item);
//        		if(db.saveItems("Parts", outputLst) == null) return;
//  	    }
	    row++;
	}

    }   
}
