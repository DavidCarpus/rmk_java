package carpus.database;

import java.sql.*;
import java.util.*;

import Configuration.Config;
import Configuration.ConfigStrings;
import carpus.util.ErrorLogging;

import rmk.database.dbobjects.DBObject;

public class DBObjectLoader {
    public boolean logButNotExecute = false;
//        public boolean logButNotExecute=!carpus.util.SystemPrefrences.runningOnWindows();

    public Vector save(Connection  cx, Vector items) throws Exception{
	String sql="";
	long id;
	if(items == null) return null;
	try {
	    Statement   stmt = cx.createStatement();

	    for(int i=0; i< items.size(); i++){
	        save(cx, (DBObject)items.get(i));
	    }
//  	    return items;
	} catch (Exception e){
	    throw e;
	} // end of try-catch
	return items;
    }

	public void save(Connection cx, DBObject item) throws Exception
	{
		String sql = "";
		long id = item.getID().longValue();
		Statement stmt = cx.createStatement();

		//  	if(logButNotExecute){
		//  	    String msg="";
		//  	    if(item.isEdited())	    msg += "*";
		//  	    if(item.isNew())	    msg += "+";
		//  	    msg += item;
		//  	    carpus.database.Logger.getInstance().logMessage(msg);
		//  	}
		id = getID(cx, item);
		
		if (item.isNew() || item.isTransfering())
		{
			item.setID(id);
			sql = item.saveSql(id);
		} else if (item.isEdited())
		{
			if (item.getID().longValue() > 0)
				id = item.getID().longValue();
			sql = item.updateSql(id);
		} else
		{
			return;
		}
		//  	if(!carpus.util.SystemPrefrences.runningOnWindows())
		//  	    System.out.println(this.getClass().getName() + ":"+ sql);
		//  	carpus.database.Logger.getInstance().logMessage(sql);

		try
		{
			if (logButNotExecute)
				carpus.database.Logger.getInstance().logMessage(sql);
			else{
				stmt.execute(sql);
				if (id == 0){
					String dbClass = Config.getDBType();
					if(dbClass.equals("MYSQL")){
						ResultSet  rs = stmt.executeQuery("select last_insert_id()");
						if (rs.next()){
							id = rs.getLong(1);
							item.setID((long)id);
						}
					}
				}
				item.edited = false;
			}
				
		} catch (java.sql.SQLException e)
		{
			throw new Exception("" + e.getMessage() + " : " + sql);
		} // end of try-catch
	}
    
    //==========================================================
    public Vector remove(Connection  cx, Vector items) throws Exception{
	String sql="";
	long id;
	if(items == null) return null;
	try {
	    Statement   stmt = cx.createStatement();

	    for(int i=0; i< items.size(); i++){
		remove(cx, (DBObject)items.get(i));
	    }
//  	    return items;
	} catch (Exception e){
	    throw e;
	} // end of try-catch
	return items;
    }

    public void remove(Connection  cx, DBObject item) throws Exception{
	String sql="";
	long id = item.getID().longValue();
	Statement   stmt = cx.createStatement();

	id = getID(cx, item);

	if(item.isNew() || item.isTransfering()){ 
	    // removeing an unsaved Item?
	    // don't need to do anything
	    return;
	} else{
	    id = item.getID().longValue();
	    sql = item.deleteSql(id);
	}
	carpus.database.Logger.getInstance().logMessage(sql);
	
	try {
	    if(! logButNotExecute)
		stmt.execute(sql);
	} catch (java.sql.SQLException e){
	    throw new Exception(""+e.getMessage()+" : " + sql);
	} // end of try-catch
    }
    //==========================================================

    long getID(Connection  cx, DBObject item) throws Exception{
	long id = item.getID().longValue();
	Statement   stmt = cx.createStatement();
	String sql="";
	if(id == 0){
	    try{
		sql = item.getIDSQL();
		ResultSet  rs = stmt.executeQuery(sql);
		if (rs.next()){
		    id = rs.getLong(1);
		} else{
		    carpus.database.Logger.getInstance().logError(sql,new Exception("Unable to obtain new ID"));
		}
	    }
	    catch (java.sql.SQLException e){
		carpus.database.Logger.getInstance().logError(e, sql);
		throw new Exception(sql);
	    }
	}
	return id;
    }
    
    public String lookup(Connection  cx, String TABLE_NAME, String ID_FIELD, 
            String field, String keyValue) throws Exception{
//  	System.out.println(TABLE_NAME + " Lookup");
	String qry = "Select " + field + " from "+ TABLE_NAME +" where " + ID_FIELD + " = " + keyValue ;

	Statement stmt  = cx.createStatement();
	ResultSet rs = stmt.executeQuery(qry);
	if(rs.next()){ return rs.getString(1); }
  	return null;
    }
    
    /** 
     * Load the entities defined in baseEntityPackage using given criteria
     * @param connection
     *            connection to database
     * @param tableName
     *            name of table in database
     * @param idFieldName
     *            name of id field
     * @param entityName
     * 			  Name of entity dbobjects 
     * @param criteria
     * 			  filter of records used in where clause 
     * @return loadedItems 
     * 			Vector of entities created and loaded from database 
     * @throws SQLException
     */
    public Vector load(Connection cx, String TABLE_NAME, String ID_FIELD,
            String entityName, String criteria) throws Exception {
        
        Vector results = new Vector();
        String qry = "Select * from " + TABLE_NAME + " ";
        
        if (criteria == null)
            throw new Exception("Must specify criteria.");

        if (criteria.length() > 0) {
            if (!criteria.toUpperCase().trim().startsWith("WHERE")) {
                qry += " where ";
            }
            qry += criteria;
        }
        try {
            Statement stmt = cx.createStatement();
            ResultSet rs = stmt.executeQuery(qry);
            DBObject object;
            while (rs.next()) {
                object = getDBEntity(entityName);
                if(object == null) 
                    return results;
                long ID = rs.getLong(ID_FIELD);
                object.setID(new Long(ID));
                object.setValues(rs);
                results.add(object);
            }
        } catch (Exception e) {
            System.err.print(qry);
            System.out.println(ErrorLogging.stkTrace(e));
            throw e;
        }
        return results;
    }

    /**
     * Fetch a single item with given ID from a table
     * @param cx
     * @param ID
     * @param TABLE_NAME
     * @param ID_FIELD
     * @return
     */
    public Object fetchItem(Connection cx, int ID, String TABLE_NAME, String ID_FIELD){
        try{
            return load(cx, TABLE_NAME, ID_FIELD,
                    capitalizeFirstCharacterOfEachWord(TABLE_NAME)
                    , ID_FIELD + "=" + ID).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Helper function to instantiate a dbObject of specified type
     * @param type entity type to instantiate
     * @return dbObject of specified type
     */
    static DBObject getDBEntity(String type) {
        if(type.equalsIgnoreCase("Invoices"))
            type = "Invoice";
        Class workerClass;
        String itemName;
        String baseEntityPackage = ConfigStrings.baseDBEntityPkg();
        itemName = baseEntityPackage + type;
        try {
            workerClass = Class.forName(itemName);
            DBObject tst = (DBObject) workerClass.newInstance();
            return tst;
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: Unable to find entity " + itemName + ".");
        } catch (Exception e) {
            System.out.println("ERROR: Unable to obtain entity object " + itemName
                    + ".");
//            System.out.println(ErrorLogging.stkTrace(e));
        }
        return null;
    }
    
	/**
	 * @param String - one "create table" SQL command
	 * @return String - parameter returned with first character of every word capitalized
	 * 			doing nothing otherwise
	 */
	public static String capitalizeFirstCharacterOfEachWord(String original){
        return changeFirstCharacterOfEachWord( original, true );
	}
    
    public static String uncapitalizeFirstCharacterOfEachWord( String original )
        {
        return changeFirstCharacterOfEachWord( original, false );
        }


    public static String changeFirstCharacterOfEachWord( String original, boolean upper )
    {
    String results="";
    String word="";
    if(original == null) return null;
    StringTokenizer st = new StringTokenizer(original);
    while (st.hasMoreTokens()) {
        word = st.nextToken();
        results += ( upper ? word.substring(0,1).toUpperCase() : word.substring( 0, 1 ).toLowerCase() ) + word.substring(1) + " ";
    }
    return results.trim();
    }

}
