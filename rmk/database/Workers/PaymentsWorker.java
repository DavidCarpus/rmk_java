package rmk.database.Workers;

import java.sql.*;
import java.util.*;

import java.io.*;

import rmk.ErrorLogger;
import carpus.database.*;

public class PaymentsWorker extends DBObjectLoader implements IDBObjectLoader {
    public static final String ID_FIELD = "PaymentID";
    public static final String TABLE_NAME = "Payments";

    public Vector  load(Connection  cx, String criteria) throws Exception{	
	Vector results = new Vector();
	String qry = "Select * from " + TABLE_NAME + " ";

	if(criteria == null) throw new Exception("Workers.Payments.load: Must specify criteria.");

	if(criteria.length() > 0){
	    if(! criteria.toUpperCase().trim().startsWith("WHERE")){
		qry += " where ";
	    }
	    qry += criteria;
	}	
	Statement stmt  = cx.createStatement();
	ResultSet rs = stmt.executeQuery(qry);
	rmk.database.dbobjects.Payments object;
	while(rs.next()){
	    object = new rmk.database.dbobjects.Payments(rs.getInt(ID_FIELD));
	    object.setValues(rs);
	    object.markSaved();
	    results.add(object);
	}
	return results;
    }
//      public Vector save(Connection  cx, Vector items) throws Exception{
    // moved to superclass

    public String lookup(Connection  cx, String field, String keyValue) throws Exception{
	ErrorLogger.getInstance().logMessage(this.getClass().getName() + TABLE_NAME + " Lookup");
	String qry = "Select " + field + " from "+ TABLE_NAME +" where " + ID_FIELD + " = " + keyValue ;

	Statement stmt  = cx.createStatement();
	ResultSet rs = stmt.executeQuery(qry);
	if(rs.next()){ return rs.getString(1); }
  	return null;
    }

    public static void main(String args[]) throws Exception{
      carpus.database.PostgresDB db = new carpus.database.PostgresDB();
      db.connect();

	String fileName=Configuration.Config.getDataFileLocation("F_Payments");

	String currString="";
	BufferedInputStream in = 
	    (new BufferedInputStream(
				     new FileInputStream(fileName)));
	int row=0;
	carpus.database.Fixed fixed = new carpus.database.Fixed();
	int ct= rmk.database.dbobjects.Payments.getTotalFieldLengths_txt();
	
	byte[] currInput = new byte[ct+2];
	int read=0;
	Object[] lst;
	Vector outputLst = new Vector();
	while( (read = in.read(currInput))!= -1 && row < 2){
	    currString = new String(currInput);
	    
	    int sum=0;
	    lst = fixed.getArray(currString,rmk.database.dbobjects.Payments.lengths);
	    rmk.database.dbobjects.Payments item = new rmk.database.dbobjects.Payments(lst);
	    outputLst.add(item);
//  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + item.saveSql((int)item.getPaymentID()));

	    row++;
	}
  	outputLst = db.saveItems("Payments", outputLst);
    }
}
