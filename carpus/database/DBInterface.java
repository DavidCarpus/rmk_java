package carpus.database;

import java.util.*;
import java.sql.*;

public interface DBInterface {
    public void connect();
    public void connect(String username, String passwd, String className);
    public Vector getItems(String type, String criteria);
    public Vector saveItems(String type, Vector items);
    public Vector removeItems(String type, Vector items);

    public Connection getConn();
    
    public String lookup(String table, String field, String keyValue);
    public PreparedStatement getPrepStatement(String qry) throws SQLException;
    public  void execute(String sql) throws SQLException;
    public int getIDSQL(String sql);
    public String getDBType();
    public String likeCriteria(String criteria);
    public String lenCriteria(String field);
    public String dateStr(GregorianCalendar date);
    public String booleanValueSQL(Boolean val);
    public String table_fieldName(String field);
    public String cleanTEXT(String txt);
 }
