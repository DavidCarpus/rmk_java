package carpus.database;

import java.sql.*;
import java.util.Vector;
import java.util.* ;

import carpus.util.Formatting;

public abstract class DBAccess implements DBInterface{
    public static boolean logButNotExecute=true;
    public Connection  cx;
    static GregorianCalendar today=null;
    static protected String systemName;
    static protected boolean connected=false;
    protected boolean postgresql=false;
    carpus.database.Logger dbLogger = carpus.database.Logger.getInstance();

    /**
	 * 
	 */
	protected DBAccess() {
		try {
			TimeZone tz = TimeZone.getTimeZone("GMT-4"); // eastern
			today = new GregorianCalendar(tz);
		} catch (Exception e) {
		}
	}
	
    public Connection getConn(){
        return cx;
    }

    public abstract void connect();

    public abstract void connect(String username, String passwd, String className);

    public carpus.database.IDBObjectLoader getWorker(String type){
	Class workerClass;
	String itemName = systemName + ".database.Workers." + type + "Worker";
  	try{
	    workerClass = Class.forName(itemName);
	    return (carpus.database.IDBObjectLoader)workerClass.newInstance();
    	}catch( ClassNotFoundException e ){
	    dbLogger.logError("Class " + itemName + " Not found.", e);
    	}catch( Exception e ){
	    dbLogger.logError(""  + e.getMessage() + " while getting worker " +  itemName,e);
  	}
	return null;
    }
    private void err(Exception e, String action, String type){
	dbLogger.logError(" while " + action + ": " +  type, e);
//  	dbLogger.logError(""  + e.getMessage() + " while " + action + ": " +  type, e);
//  	e.printStackTrace();
    }
    public Vector getItems(String type, String criteria){
	carpus.database.IDBObjectLoader worker = getWorker(type);
	try{
	    return worker.load(cx, criteria);
    	} catch( Exception e ){
	    err(e, "loading", type + ":" + criteria);
    	}
  	return null;
    }

    public Vector saveItems(String type, Vector items){
	carpus.database.IDBObjectLoader worker = getWorker(type);
	Vector results;
	try{
	    results = worker.save(cx, items);
	    return results;
    	} catch( Exception e ){
  	    err(e, "saving", type);
    	}
  	return null;
    }
    public Vector removeItems(String type, Vector items){
	carpus.database.IDBObjectLoader worker = getWorker(type);
	Vector results;
	try{
	    results = worker.remove(cx, items);
	    return results;
    	} catch( Exception e ){
  	    err(e, "removing", type);
    	}
  	return null;
    }
    public String lookup(String table, String field, String keyValue){
	carpus.database.IDBObjectLoader worker = getWorker(table);
	try{
	    return worker.lookup( cx,  field, keyValue);
    	} catch( Exception e ){
	    err(e, "lookup", table);
    	}
  	return null;
    }

    public boolean isConnected(){
	if (! connected) return false;
	try{
	    return (! cx.isClosed());
	} catch (Exception e){
	    return false;
	}
    }
    public PreparedStatement getPrepStatement(String qry) throws SQLException{
	try{
	    if (cx == null) connect();
	    PreparedStatement stmt= cx.prepareStatement(qry);
	    return stmt;
	} catch (Exception e){
	    return null;
	}
    }
    public  void execute(String sql) throws SQLException{
	Statement   stmt;
	stmt     = cx.createStatement();
	stmt.execute(sql);
    }

    public int getIDSQL(String sql){
	Statement   stmt;
	Vector results = new Vector();
	ResultSet  rs1=null;
	try{
	    stmt     = cx.createStatement();
	    rs1 = stmt.executeQuery(sql);
	
	    if (!rs1.next()){
		System.out.println("Unable to get Results");
		return 0;
	    }
	    return rs1.getInt(1);
	}catch (Exception e){
	    System.out.println(e);
	    return 0;
	}       
    }
    public static String displayColData(DatabaseMetaData dmd, String tableName)
    {
	try {
	    ResultSet rs = dmd.getColumns(null, null, tableName, "*");
	    return carpus.database.DBAccess.displayResults(rs);
	} catch (java.sql.SQLException e){
	    return ""+e;
	} // end of try-catch
    }

    public static String displayResults(ResultSet rs) throws java.sql.SQLException
    {
	if (rs == null) {
	    //        System.err.println("ERROR in displayResult(): No data in ResulSet");
	    return "ERROR in displayResult(): No data in ResulSet";
	}
	String results="";
	int rowCnt=0;
	ResultSetMetaData meta = rs.getMetaData();
	int cols = meta.getColumnCount();
	int[] width = new int[cols];

	// To Display column headers
	//
	boolean first=true;
	StringBuffer head = new StringBuffer();
	StringBuffer line = new StringBuffer();
	carpus.util.Formatting formatter = new carpus.util.Formatting();

	// fetch each row
	//
	while (rs.next()) {
	    rowCnt++;
	    // get the column, and see if it matches our expectations
	    //
	    String text = new String();
	    for (int ii=0; ii<cols; ii++) {
		String value = rs.getString(ii+1);

		if (first) {
		    width[ii] = 0;
		    if (value != null) width[ii] = value.length();
		    if (meta.getColumnName(ii+1).length() > width[ii])
			width[ii] = meta.getColumnName(ii+1).length();

		    head.append(Formatting.textSizer(meta.getColumnName(ii+1), width[ii]));
		    head.append(" ");

		    line.append(Formatting.textSizer("", width[ii], Formatting.ALIGN_CENTER, "="));
		    line.append(" ");
		}

		text += Formatting.textSizer(value, width[ii]);  
		text += " ";   // the gap between the columns
	    }

	    if (first) {
		results += "  " + head.toString() + "\n";
		results += "  " + line.toString() + "\n";
		first = false;
	    }
	    results += Formatting.textSizer(""+rowCnt, 2) + text + "\n";
	}
	return results;
    }
}

