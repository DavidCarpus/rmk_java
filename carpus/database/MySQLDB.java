/*
 * Created on Feb 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package carpus.database;

import java.util.GregorianCalendar;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import Configuration.Config;

/**
 * @author dcarpus
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MySQLDB extends DBAccess implements carpus.database.DBInterface
{
	String[] results;

	public String getDBType(){ return "MYSQL";}

	public MySQLDB(){
		super();
	systemName = Config.systemName();
	postgresql = true;
	}
	public MySQLDB(String username, String passwd, String className){
	postgresql = true;
	systemName = Config.systemName();
	connect(username, passwd, className);
	}

	public void connect(){
	systemName = Config.systemName();
//	connect("rmk", "gtr", "Unknown");
	connect(Config.dbUserName(), 
			Config.dbPassword(), "Unknown");

	}
	public void connect(String username, String passwd, String className){
	boolean   passed = true;
	String      url="";
	if(isConnected()){ return;}
	
	System.out.println("Connect:" + username + ":" + passwd);	
	System.out.println("Starting DBAccess:" + className);
	
	try{
	    String dbName = Config.databaseName();
		url      = "jdbc:mysql://" + Config.databaseIP() + "/" + dbName;

	    MysqlDataSource ds = new MysqlDataSource();
	    ds = new MysqlDataSource();
		ds.setUrl( url );
  		ds.setUser( username );
//  		if(url.indexOf("localhost") < 0)
  		    ds.setPassword( passwd );

		cx = ds.getConnection();
		connected = true;
	} catch (java.lang.Exception e) { 
		dbLogger.logError("while connecting to MySQL DB:" 
				  + Config.databaseName(), e);
		
	}	
	}

	public long save(DBObject item) throws Exception{
	long id = item.getID().longValue();
	int location=0;
	String sql="";
	System.out.println("save:" + item);
	
	try{
		if (id <= 0 || item.transfering){
		location=1;
		sql=item.getIDSQL();
		if ( ! item.transfering)
			id = getIDSQL(sql);
		location=2;
		item.setID(id);
		location=3;
		sql = item.saveSql(id);
		execute(sql);
		} else{
		location=10;
		sql = item.updateSql(id);
		execute(sql);
		}
		location=20;
		return item.getID().longValue();
	} catch (Exception e){
		throw new Exception("" +  e + ":(" + location + ":" + id + ")" + sql);
	}
	}
	public String likeCriteria(String criteria){
	return " like '%" + cleanTEXT(criteria) + "%'";
	}

	public String lenCriteria(String field){
	return "length(" + field + ")";
	}

	public String cleanTEXT(String txt)
	{
		if (txt == null) return txt;
		String results = txt;
		if(results.indexOf( "\\") >= 0 ){
			String orig = "\\\\";
			String repl = "\\\\\\\\";
			results = results.replaceAll( orig, repl );
		}
		if(results.indexOf('\'') >= 0){
			String orig = "'";
			String repl = "\\\\'";
			results = results.replaceAll(orig, repl);
		}
		if(results.indexOf( '\"') >= 0){
			results = results.replaceAll( "\"", "\\\"" );
		}

		return results;
	}

	public String dateStr(GregorianCalendar date){
		String results = "'";
		results += carpus.util.DateFunctions.getSQLDateStr(date);
		String time = carpus.util.DateFunctions.getSQLTimeStr(date);
		if(!time.trim().equals("0:0:00"))
			results += " " + time;
		else
			results += "";
		results += "'";
		return results;
	}
	public String table_fieldName(String desc){
	int split = desc.indexOf(".");
	if(split < 0) return desc;

	String results="";
	String table="";
	String field="";
	table = desc.substring(0,split).trim();
	field = desc.substring(split+1).trim();
	if(table.startsWith("[")){
		table = table.substring(1, split-1);
		field = field.substring(1, field.length()-1);
	}
	results = table + "." + field;

	return results;
	}
	//===============================================
	public static void main(String args[]) throws Exception
	{
		MySQLDB db = new MySQLDB();
		db.connect();
//		System.out.println(db.dateStr(new GregorianCalendar()));
//		System.out.println(db.table_fieldName("[test].[me]"));
		System.out.println(db.cleanTEXT("sullivan's \\"));
	}

	public String booleanValueSQL(Boolean val) {
		return ((Boolean)val).booleanValue()?"1":"0"; 
	}

}
