package rmk.database.Workers;

import java.sql.*;
import java.util.*;

import java.io.*;

import carpus.database.*;

public class InvoiceWorker extends DBObjectLoader implements IDBObjectLoader{
    public static final String ID_FIELD = "Invoice";
    public static final String TABLE_NAME = "Invoices";

    public Vector  load(Connection  cx, String criteria) throws Exception{	
	Vector results = new Vector();
	String qry = "Select * from " + TABLE_NAME + " ";

	if(criteria == null) throw new Exception("Workers.Invoice.load: Must specify criteria.");

	if(criteria.length() > 0){
	    if(! criteria.toUpperCase().trim().startsWith("WHERE")){
		qry += " where ";
	    }
	    qry += criteria;
	}
//  	System.out.println(this.getClass().getName() + ":"+ qry);
	
	Statement stmt  = cx.createStatement();
	ResultSet rs = stmt.executeQuery(qry);
	rmk.database.dbobjects.Invoice object;
	while(rs.next()){
		int id = rs.getInt(ID_FIELD);
	    object = new rmk.database.dbobjects.Invoice(id);
	    object.setValues(rs);
	    object.markSaved();
	    results.add(object);
	}
	return results;
    }

//      public Vector save(Connection  cx, Vector items) throws Exception{
    // moved to superclass

    public rmk.database.dbobjects.Invoice fetch(Connection cx, int invNum){
        return (rmk.database.dbobjects.Invoice)fetchItem(cx, invNum, TABLE_NAME, ID_FIELD);
    }
    
    public String lookup(Connection  cx, String field, String keyValue) throws Exception{
        return lookup(cx, TABLE_NAME, ID_FIELD, field, keyValue);
    }

    public static void main(String args[]) throws Exception{
      carpus.database.PostgresDB db = new carpus.database.PostgresDB();
      db.connect();

	String fileName=Configuration.Config.getDataFileLocation("F_Invoices");

	String currString="";
	BufferedInputStream in = 
	    (new BufferedInputStream(
				     new FileInputStream(fileName)));
	int row=0;
	carpus.database.Fixed fixed = new carpus.database.Fixed();
	int ct= rmk.database.dbobjects.Invoice.getTotalFieldLengths_txt();
	
	byte[] currInput = new byte[ct+2];
	int read=0;
	Object[] lst;
	Vector outputLst = new Vector();
	while( (read = in.read(currInput))!= -1 && row < 2){
	    currString = new String(currInput);
	    
	    int sum=0;
	    lst = fixed.getArray(currString,rmk.database.dbobjects.Invoice.lengths);
	    rmk.database.dbobjects.Invoice invoice = new rmk.database.dbobjects.Invoice(lst);
	    outputLst.add(invoice);
//  	    System.out.println(invoice.saveSql((int)invoice.getInvoice()));

	    row++;
	}
  	outputLst = db.saveItems("Invoices", outputLst);
    }
}
