package rmk.database.Workers;

import java.sql.*;
import java.util.*;

import java.io.*;

import rmk.ErrorLogger;
import carpus.database.*;

public class InvoiceEntriesWorker  extends DBObjectLoader implements IDBObjectLoader{
    boolean logButNotExecute = true;

    public static final String ID_FIELD = "InvoiceEntryID";
    public static final String TABLE_NAME = "InvoiceEntries";

    public Vector  load(Connection  cx, String criteria) throws Exception{	
	Vector results = new Vector();
	String qry = "Select * from " + TABLE_NAME + " ";

	if(criteria == null) throw new Exception("Workers.Invoiceentrie.load: Must specify criteria.");

	if(criteria.length() > 0){
	    if(! criteria.toUpperCase().trim().startsWith("WHERE")){
		qry += " where ";
	    }
	    qry += criteria;
	}	
	Statement stmt  = cx.createStatement();
	ResultSet rs = stmt.executeQuery(qry);
	rmk.database.dbobjects.InvoiceEntries object;
	while(rs.next()){
	    object = new rmk.database.dbobjects.InvoiceEntries(rs.getInt(ID_FIELD));
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
    public void save(Statement stmt, DBObject item) throws Exception{
	String sql="";
	long id = item.getID().longValue();

//  	if(logButNotExecute){
//  	    String msg="";
//  	    if(item.isEdited())	    msg += "*";
//  	    if(item.isNew())	    msg += "+";
//  	    if(item.isTransfering())	    msg += ">";
//  	    msg += item;
//  	    carpus.database.Logger.getInstance().logMessage(msg);
//  	}

	try{
	    if(item.isNew() || item.isTransfering()){
		if(!item.isTransfering()){ // Transfering == needs ID		    
		    sql = item.getIDSQL();
		    ResultSet  rs = stmt.executeQuery(sql);
		    if (rs.next()){
			id = rs.getLong(1);
		    } else{
			carpus.database.Logger.getInstance().logError(sql,new Exception("Unable to obtain new ID"));
		    }
		}
		((rmk.database.dbobjects.InvoiceEntries)item).setInvoiceEntryID(id);
		sql = item.saveSql(id);
		if(logButNotExecute) carpus.database.Logger.getInstance().logMessage(sql);
		else stmt.execute(sql);
		return;
	    }

	    if(item.isEdited()){
		id = item.getID().longValue();
		sql = item.updateSql(id);
		if(logButNotExecute) carpus.database.Logger.getInstance().logMessage(sql);
		else stmt.execute(sql);
		return;
	    }
	}
	catch (java.sql.SQLException e){
	    carpus.database.Logger.getInstance().logError(e, sql);
	    throw e;
	}
	catch (Exception e){
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ e);	    
	    throw e;
	}
    }


    public static void main(String args[]) throws Exception{
      carpus.database.PostgresDB db = new carpus.database.PostgresDB();
      db.connect();

	String fileName=Configuration.Config.getDataFileLocation("F_InvoiceEntries");

	String currString="";
	BufferedInputStream in = 
	    (new BufferedInputStream(
				     new FileInputStream(fileName)));
	int row=0;
	carpus.database.Fixed fixed = new carpus.database.Fixed();
	int ct= rmk.database.dbobjects.InvoiceEntries.getTotalFieldLengths_txt();
	
	byte[] currInput = new byte[ct+2];
	int read=0;
	Object[] lst;
	Vector outputLst = new Vector();
	while( (read = in.read(currInput))!= -1 && row < 2){
	    currString = new String(currInput);
	    
	    int sum=0;
	    lst = fixed.getArray(currString,rmk.database.dbobjects.InvoiceEntries.lengths);
	    rmk.database.dbobjects.InvoiceEntries item = new rmk.database.dbobjects.InvoiceEntries(lst);
	    outputLst.add(item);
//  	    ErrorLogger.getInstance().logMessage(item.saveSql((int)item.getInvoiceEntryID()));

	    row++;
	}
  	outputLst = db.saveItems("InvoiceEntries", outputLst);
    }
}
