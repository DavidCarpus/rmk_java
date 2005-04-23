package rmk.database.dbobjects;
import java.sql.*;
import java.io.*;

import rmk.ErrorLogger;
import carpus.database.Fixed;

public class PartTypes extends DBObject{
    public static final int[] lengths={Fixed.LONG_SIZE, 50};
    public static final String[] fields={"PartTypeID","Description"};

    public static int[] getFieldLengths_txt(){	return 	lengths;    }
    public static int getTotalFieldLengths_txt(){	
	int ct=0;
	for(int i=0; i< lengths.length; i++){
	    ct += lengths[i];
	}
	return 	ct;    
    }

    public PartTypes(Object[]  data){
	super(fields);
	setValues(data);
	transfering=true;
    }
    public PartTypes(int ID){
	super(fields);
	setID(new Long(ID));
    }

    public long getPartTypeID(){ return ((Long)values[0]).longValue();}
    public String getDescription(){ return (String)values[1];}

    public void setPartTypeID(long value){values[0] = new Long(value);edited=true;}
    public void setDescription(String value){values[1] = value;edited=true;}

//      public void setValues(ResultSet  recordSet) throws Exception;
    public void setValues(Object[]  data){
	int i=0;
	setPartTypeID(Long.parseLong((String)data[i++]));
	setDescription((String)data[i++]);
    }

    public String toString(){
	String results="";
//  	results +=  "prtType:" + getPartTypeID() + " " + getDescription();
	results +=  getDescription();
	return results;
    }
   public String getIDSQL(){	
	String qry = "";
	String dbClass = Configuration.Config.getDBType();
	if(dbClass.equals("ACCESS")){
	    qry = "select max(PartTypeID)+1 from PartTypes";
	} else if(dbClass.equals("MYSQL")){
		qry = "select null";
	} else
  	   qry = "select nextval('PartTypes_Partid_seq')";
	return qry;
   }

    public String saveSql(long id) throws Exception{
	return saveSql("PartTypes", id);
    }

    public String updateSql(long id) throws Exception{
	return updateSql("PartTypes", "PartID", id);
    }
    public String deleteSql(long id) throws Exception{
	return deleteSql("PartTypes", "PartID", id);
    }
    public String deactivateSql(long id) throws Exception{
	return deactivateSql("PartTypes", "PartID", id);
    }

    public void setValues(ResultSet  recordSet) throws Exception{
// setPartID(recordSet.getInt("PartID"));
	setDescription(recordSet.getString("Description"));
    }
    public static void main(String args[]) throws Exception{
	carpus.database.PostgresDB db = new carpus.database.PostgresDB();
	db.connect();
 	String fileName=Configuration.Config.getDataFileLocation("PartTypes");
	ErrorLogger.getInstance().logMessage(fileName);
	
	carpus.database.Fixed fixed = new carpus.database.Fixed();
	BufferedInputStream in = (new BufferedInputStream( new FileInputStream(fileName)));
	int row=0;
	byte[] currInput = new byte[getTotalFieldLengths_txt()+2]; // CR-LF
	Object[] lst;
	while( in.read(currInput)!= -1
){
//   && row < 200){
		lst = fixed.getArray(new String(currInput),lengths);
//  		fixed.list(lst);
		ErrorLogger.getInstance().logMessage(fixed.list(lst));
		PartTypes item = new PartTypes(lst);
 		System.out.print(row);
   		ErrorLogger.getInstance().logMessage(":" + item);
		ErrorLogger.getInstance().logMessage(""+item);		
    		java.util.Vector outputLst = new java.util.Vector();
		outputLst.add(item);
      		if(db.saveItems("PartTypes", outputLst) == null) return;
	    row++;
	}

    }   
}
