package rmk.database;

public class DBAccess{
    static DBAccess instance = new DBAccess();
    carpus.database.DBInterface db= Configuration.Config.getDB();

    public static DBAccess getInstance(){
	return instance;
    }
    public java.util.Vector getItems(String table, String filter){
	return db.getItems(table, filter);
    }
}
