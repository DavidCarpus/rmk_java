package rmk.database.Workers;

import java.sql.*;
import java.util.*;

import rmk.ErrorLogger;

import carpus.database.*;

public class AddressWorker extends DBObjectLoader implements IDBObjectLoader {
    public static final String ID_FIELD = "AddressID";
    public static final String TABLE_NAME = "Address";

    public Vector  load(Connection  cx, String criteria) throws Exception{	
	Vector results = new Vector();
	String qry = "Select * from " + TABLE_NAME + " ";

	if(criteria == null) throw new Exception("Workers.Address.load: Must specify criteria.");

	if(criteria.length() > 0){
	    if(! criteria.toUpperCase().trim().startsWith("WHERE")){
		qry += " where ";
	    }
	    qry += criteria;
	}	
	Statement stmt  = cx.createStatement();
	ResultSet rs = stmt.executeQuery(qry);
	rmk.database.dbobjects.Address object;
	while(rs.next()){
	    object = new rmk.database.dbobjects.Address(rs.getLong(ID_FIELD));
	    object.setValues(rs);
	    object.markSaved();
	    results.add(object);
	}
	return results;
    }

//        public Vector save(Connection  cx, Vector items) throws Exception{ 
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
	carpus.database.DBInterface db = Configuration.Config.getDB();
  	db.connect();

	carpus.util.Formatting formatter= new carpus.util.Formatting();
	java.util.Vector lst = db.getItems("Address", "customerid = 1");
//  	java.util.Vector lst = db.getItems("Address", "AddressID < 10");

	for(int cust=0; cust<10; cust++){
	    lst = db.getItems("Address", "customerid = " + cust);
	    for(int i=0; i < lst.size(); i++){
		rmk.database.dbobjects.Address item = 
		    (rmk.database.dbobjects.Address)lst.get(i);
  		item.setAddressID(item.getAddressID());
		lst = db.saveItems("Address", lst);

//  		if(item.isPrimaryCustomerAddress())
//  		    System.out.print(formatter.textSizer("*"+item.getAddressID(),6));
//  		else
//  		    System.out.print(formatter.textSizer(""+item.getAddressID(),6));
//  		System.out.print(formatter.textSizer("",6));
//  		System.out.print(formatter.textSizer(item.getAddress0(),32));
//  		System.out.print(formatter.textSizer(item.getAddress1(),32));
//  		System.out.print(item.getAddress2());
//  		ErrorLogger.getInstance().logMessage();
	    }
//  	    ErrorLogger.getInstance().logMessage();
	}
    }
}
