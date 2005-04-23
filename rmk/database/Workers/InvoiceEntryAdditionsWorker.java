package rmk.database.Workers;

import java.sql.*;
import java.util.*;

import rmk.ErrorLogger;

import carpus.database.*;

public class InvoiceEntryAdditionsWorker extends DBObjectLoader implements IDBObjectLoader{
    public static final String ID_FIELD = "additionid";
    public static final String TABLE_NAME = "InvoiceEntryAdditions";
    boolean logButNotExecute = true;

    public Vector  load(Connection  cx, String criteria) throws Exception{	
	Vector results = new Vector();
	String qry = "Select * from " + TABLE_NAME + " ";

	if(criteria == null) throw new Exception("Workers.InvoiceEntryAddition.load: Must specify criteria.");

	if(criteria.length() > 0){
	    if(! criteria.toUpperCase().trim().startsWith("WHERE")){
		qry += " where ";
	    }
	    qry += criteria;
	}	
	Statement stmt  = cx.createStatement();
	ResultSet rs = stmt.executeQuery(qry);
	rmk.database.dbobjects.InvoiceEntryAdditions object;
	while(rs.next()){
	    object = new rmk.database.dbobjects.InvoiceEntryAdditions(rs.getInt(ID_FIELD));
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
	rmk.gui.Application.main(args);
    }

}
