package Configuration;
import java.util.Properties;

public class ConfigStrings{
    public static final boolean IDE = ! carpus.util.SystemPrefrences.runningOnWindows();

    public static String defaultDBUserName(){
	return "dcarpus";
    }
    public static String defaultDBPassword(){
	return "";
    }
    public static String systemName(){ return "rmk";}
    public static String databaseIP(){ return "localhost";}
    public static String databaseName(){ return systemName();}
    public static String baseDBEntityPkg() { return "rmk.database.dbobjects.";}

    public static String getName(){
	Properties osProps;
	String results="";
	osProps = System.getProperties();
	try{ results = osProps.getProperty("user.name");}  catch (Exception e){	}
	if(results.equals("dcarpus") && ! carpus.util.SystemPrefrences.runningOnWindows())
	    results = "ordrc";

	return results;
    }
    public static void main(String args[])
	throws Exception
    {
	System.out.println(getName());	
    }

}
