package carpus.database;
import java.sql.*;
import java.util.*;

import Configuration.Config;

public class AccessDB extends DBAccess implements DBInterface {
	public AccessDB() {
        super();
	}
	public String getDBType() {
		return "ACCESS";
	}
	public void connect() {
		systemName = Config.systemName();
		System.out.println(systemName);
		connect("ordrc", "duntyr", "Test");
	}
	public AccessDB(String username, String passwd, String className) {
		postgresql = false;
		systemName = Config.systemName();
		connect(username, passwd, className);
	}
	public void connect(String username, String passwd, String className) {
		boolean passed = true;
		String url = "";
		if (isConnected()) {
			return;
		}
		System.out.println("Connect:" + username + ":" + passwd);
		System.out.println("Starting DBAccess:" + className);
		Properties props = new Properties();
		Driver driver;
		props.put("user", username);
		//    	props.put("password",  passwd );
		try {
			driver = (Driver) Class.forName("sun.jdbc.odbc.JdbcOdbcDriver")
					.newInstance();
			url = "jdbc:odbc:" + Config.databaseName();
			cx = driver.connect(url, props);
			connected = true;
		} catch (java.lang.Exception e) {
			dbLogger.logError("While connecting to AccessDB:"
					+ Configuration.Config.databaseName(), e);
		}
		//  	} catch (java.lang.ClassNotFoundException e) { 
		//  	    ErrorLogger.getInstance().logMessage("Err connecting to database" + e);	    
		//  	}	
	}
	public static void main(String args[]) throws Exception {
		AccessDB db = new AccessDB();
		//  	ErrorLogger.getInstance().logMessage(db.dateStr(new GregorianCalendar()));
		System.out.println(db.table_fieldName("test.me"));
		//  	ErrorLogger.getInstance().logMessage("connecting");
		//  	db.connect("ordrc", "duntyr","Test");
		//  // 	ArrayList lst = db.getItems("OldPart", "");
		//  // 	ErrorLogger.getInstance().logMessage(lst);
		//  	Statement stmt = db.cx.createStatement();
		//  	ResultSet tables = stmt.executeQuery("Select * from Parts");   
		//  	ResultSetMetaData tst = tables.getMetaData();
		//  	while (tables.next()){
		//  	    String tableName =  tables.getString(3); //column 3 is tableName
		//  	    ErrorLogger.getInstance().logMessage("Table name: " + tableName);		
		//  	}
		//  // 	ErrorLogger.getInstance().logMessage(db.getDBOptions("Customers","job_customerID",0));    
	}
	public String cleanTEXT(String txt) {
		if (txt == null || txt.indexOf("'") <= 0)
			return txt;
		String results = "";
		while (txt.indexOf("'") > 0) {
			results += txt.substring(0, txt.indexOf("'")) + "''";
			txt = txt.substring(txt.indexOf("'") + 1);
		}
		results += txt;
		return results;
	}
	public String likeCriteria(String criteria) {
		//    	return "like '%" + criteria + "%'";
		return "like '%" + cleanTEXT(criteria) + "%'";
		//  	return "like '*" + cleanTEXT(criteria) + "*'";
	}
	public String lenCriteria(String field) {
		return "len(" + field + ")";
	}
	public String dateStr(GregorianCalendar date) {
		String results = "#";
		results += carpus.util.DateFunctions.getAccessDateStr(date);
		String time = carpus.util.DateFunctions.getSQLTimeStr(date);
		if (!time.trim().equals("0:0:00"))
			results += " " + time;
		else
			results += "";
		results += "#";
		return results;
		//		getSQLTimeStr
	}
	public String table_fieldName(String desc) {
		int split = desc.indexOf(".");
		if (split < 0)
			return desc;
		String results = "";
		String table = "";
		String field = "";
		table = desc.substring(0, split).trim();
		field = desc.substring(split + 1).trim();
		if (!table.startsWith("["))
			results = "[" + table + "].[" + field + "]";
		else
			results = table + "." + field;
		return results;
	}

	public String booleanValueSQL(Boolean val) {
		return ""+((Boolean)val).booleanValue(); 
	}
}