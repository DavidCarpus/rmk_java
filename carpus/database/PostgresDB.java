package carpus.database;

import java.sql.*;
import java.util.GregorianCalendar;
import java.util.Properties;
import Configuration.Config;

public class PostgresDB extends DBAccess implements carpus.database.DBInterface
{
//      static public Connection  cx;
    String[] results;

    public String getDBType(){ return "POSTRGES";}

    public PostgresDB(){
        super();
        systemName = Config.systemName();
        postgresql = true;
    }
    public PostgresDB(String username, String passwd, String className){
	postgresql = true;
	systemName = Config.systemName();
	connect(username, passwd, className);
    }

    public void connect(){
	systemName = Config.systemName();
	connect(Config.dbUserName(), 
		Config.dbPassword(), "Unknown");

    }
    public void connect(String username, String passwd, String className){
	boolean   passed = true;
	String      url="";
	if(isConnected()){ return;}

  	System.out.println("Connect:" + username + ":" + passwd);	
	System.out.println("Starting DBAccess:" + className);
	Properties props = new Properties();
	Driver driver;
	props.put("user", username );
//    	props.put("password",  passwd );
	try{
	    driver = (Driver) Class.forName("org.postgresql.Driver").newInstance();
	    url      = "jdbc:postgresql://" + Config.databaseIP() + "/"+ 
		Config.databaseName() +"/";

//  	    System.out.println(url);
	    
	    cx = driver.connect(url, props);
	    connected = true;
	} catch (java.lang.Exception e) { 
	    dbLogger.logError("while connecting to PostgresDB:" 
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
	return " ~* '" + cleanTEXT(criteria) + "'";
    }

    public String lenCriteria(String field){
	return "length(" + field + ")";
    }
    public String cleanTEXT(String txt){
	if(txt == null || txt.indexOf("'") <=0) return txt;
	String results="";
	while(txt.indexOf("'") > 0){
	    results += txt.substring(0, txt.indexOf("'")) + "''";
	    txt = txt.substring(txt.indexOf("'")+1);
	}
	results += txt;
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
  	PostgresDB db = new PostgresDB();
//  	System.out.println(db.dateStr(new GregorianCalendar()));
//  	System.out.println(db.table_fieldName("[test].[me]"));
	System.out.println(db.cleanTEXT("sullivan's"));
    }

	public String booleanValueSQL(Boolean val) {
		return ""+((Boolean)val).booleanValue(); 
	}
}
