package carpus.util;

import java.util.Properties;

public class SystemPrefrences{
    public static final boolean IDE = ! runningOnWindows();
    String defaultcarpusWebPage="../jsp/index.jsp";

    public static boolean runningOnWindows(){
	Properties osProps;
	osProps = System.getProperties();
	try{ if (osProps.getProperty("os.name").startsWith("Windows"))	return true;}  catch (Exception e){	}
	return false;
    }
    public String getDefaultcarpusWebPage(){
	return defaultcarpusWebPage;
    }
    public static String defaultDBUserName(){
	return "ordrc";
    }
    public static String defaultDBPassword(){
	return "duntyr";
    }
    public static String getName(){
	Properties osProps;
	String results="";
	osProps = System.getProperties();
	try{ results = osProps.getProperty("user.name");}  catch (Exception e){	}
	if(results.equals("dcarpus") && ! runningOnWindows())
	    results = "ordrc";

	return results;
    }
    public static void main(String args[])
	throws Exception
    {
	System.out.println(getName());	
    }

}
