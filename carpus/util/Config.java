package carpus.util;

public abstract class Config{
    public static final boolean IDE = ! carpus.util.SystemPrefrences.runningOnWindows();

    public abstract String getProperty(String property);

//     public abstract String defaultDBUserName();
//     public String defaultDBPassword();
//     public carpus.database.DBInterface getDB() throws Exception;
//     public String getDataFileLocation(String which);
//     public String systemName();
//     public String databaseIP();
//     public String databaseName();
//     public String getName();
}
