package Configuration;
import java.util.Properties;
import java.io.FileInputStream;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import rmk.ErrorLogger;

import carpus.database.MySQLDB;

public class Config
{
	//      public static final boolean IDE = ! carpus.util.SystemPrefrences.runningOnWindows();
//	public static final boolean IDE = false;
	public static final boolean IDE =
		System.getProperties().getProperty("user.home").endsWith("carpus") ||
		System.getProperties().getProperty("user.home").endsWith("David");

	public static Properties p = loadProperties();
	public static carpus.database.DBInterface db = null;
	static String errMsg="";

	
	public static Properties loadProperties()
	{
		errMsg="";
	
		String propFileName = getPropFileName();
		if (propFileName == null)
			errMsg = "RMKSystem.txt";

		if(errMsg != null && errMsg.length()>0){
            errMsg = "Missing configuration files: \n" + errMsg;
            JOptionPane.showMessageDialog(null, errMsg, "Configuration problem:",
		            JOptionPane.ERROR_MESSAGE);
		    System.exit(0);
		}
		
		try {
			FileInputStream propFile = new FileInputStream(propFileName);
			if (propFile != null) {
				Properties loadedProps = new Properties();
				loadedProps.load(propFile);
				propFile.close();				
				ErrorLogger.getInstance().logMessage("Using configuration file:" + propFileName);
				return loadedProps;
			}
		} catch (Exception e) {
			errMsg = "loading configuration file:" + propFileName;
			e.printStackTrace();
            JOptionPane.showMessageDialog(null, errMsg, "Configuration problem:",
		            JOptionPane.ERROR_MESSAGE);
		    System.exit(0);
		}
		return null;
	}
		
	public static String getHomeDir(){
		String home="";
		if (carpus.util.SystemPrefrences.runningOnWindows())
		    home="c:\\";
		else
		{
            try {
                home = System.getProperties().getProperty("user.home") + "/";
            } catch (Exception e) {
                errMsg += "unable to determine home directory\n";
            }
		}
		if(!home.endsWith("/"))
		    home += "/";
		
		return home;
	}
	
	public static String getPropFileName(){
		String home = getHomeDir();
		
		String[] locations = {
		        "u:/RMKSystem.txt",
		        "/home/carpus/RMKSystem.txt",
		        "./RMKSystem.txt",
		        "./rmk/RMKSystem.txt",
		        home + "RMKSystem.txt",
		        home + "rmk/RMKSystem.txt"
		        };
		FileInputStream propFile=null;
		String propertyFileName = "";
		
		for(int locationID=0; locationID < locations.length;locationID++){
			if(propFile==null){ // try alternate location for config file
	            try {
	    		    propertyFileName = locations[locationID];
	                propFile = new FileInputStream(propertyFileName);
	                errMsg ="";
	            } catch (Exception e) {
	                errMsg += "     " + propertyFileName + "\n";
	            }
			} else{
				return propertyFileName;
			}
		}
		if(propFile != null)
			return propertyFileName;
		else 
			return null;
	}
	
	public static String dbUserName()
	{
		try
		{
			String results = p.getProperty("dbuser");
			if (results != null)
				return results;
		} catch (Exception e)
		{
		}
		return "dcarpus";
	}

	public static String dbPassword()
	{
		try
		{
			String results = p.getProperty("dbpasswd");
			if (results != null)
				return results;
		} catch (Exception e)
		{
		}
		return "";
	}

	public static String getDBType()
	{
		try
		{
			String results = p.getProperty("databaseType");
			if (results != null)
				return results;
		} catch (Exception e)
		{
		}
		return "ACCESS";
	}

	public static String getLookAndFeel(){
	    if(!carpus.util.SystemPrefrences.runningOnWindows()){
	        return null;
//	        return "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	    }
	    
		try
		{
			String results = p.getProperty("lookandfeel");
			if (results != null)
				return results;
		} catch (Exception e){}
		
		return UIManager.getCrossPlatformLookAndFeelClassName();
	    
	}
	
	public static String getShippingCodeTranslation(String code){
		try
		{
			String results = p.getProperty("ShippingCode"+code);
			if (results != null)
				return results;
		} catch (Exception e){}
		
		return code;
	}
	public static carpus.database.DBInterface getDB()
	{
		if (db != null)
			return db;
		ErrorLogger.getInstance().logMessage(getDBType());

		if (getDBType().equals("POSTGRESQL"))
		{
			db = (carpus.database.DBInterface)new carpus.database.PostgresDB();
		} else if (getDBType().equals("ACCESS"))
		{
			db = (carpus.database.DBInterface)new carpus.database.AccessDB();
		} else if (getDBType().equals("MYSQL"))
		{
			db = (carpus.database.DBInterface)new MySQLDB();
		} else
		{
			db = (carpus.database.DBInterface)new carpus.database.AccessDB();
		}
		db.connect();
		return db;
	}
	public static String getDataFileLocation(String which)
	{
		String results = "";
		try
		{
			results = p.getProperty("dataFileLocation");
			results += which + ".txt";
		} catch (Exception e)
		{
		}
		return results;
	}

	public static String getPDFLocation(){
		String results = "/home/dcarpus/";
        String osName = System.getProperty("os.name");
        if(osName.equals("Linux")){
            return results;
        }
		try
		{
		    if(p.getProperty("pdfsLocation") != null)
		        results = p.getProperty("pdfsLocation");
		} catch (Exception e)
		{
		}
		return results;
	}
	
	public static String getDBF_FileLocation()
	{
		String results = "";
		try
		{
			results = p.getProperty("dbFileLocation");
		} catch (Exception e)
		{
		}
		return results;
	}

	public static String getMergeFileLocation()
	{
		try
		{
			String results = p.getProperty("mergeFileLocation");
			if (results != null)
				return results;
		} catch (Exception e)
		{
		}
		return "s:\\NewRMK\\merge\\";
	}

	public static double getFLTaxRate()
	{
		try
		{
			String results = p.getProperty("FLTaxRate");
			if (results != null)
				return Double.parseDouble(results);
		} catch (Exception e)
		{
		}
		return 6.5;
	}

	static String getBackgroundImageDir()
	{
		if (carpus.util.SystemPrefrences.runningOnWindows())
			return "c:\\";
		else
			return "/home/carpus/rmk/";
	}
	public static int getMonthsBacklogged()
	{
		try
		{
			String results = p.getProperty("monthsBacklogged");
			if (results != null)
				return Integer.parseInt(results);
		} catch (Exception e)
		{
		}
		return 42;
	}
	public static int getCreditCardSearchMonths()
	{
		try
		{
			String results = p.getProperty("CreditCardSearchMonths");
			if (results != null)
				return Integer.parseInt(results);
		} catch (Exception e)
		{
		}
		return 12;
	}

	public static String getBackgroundImageLocation()
	{
		return getBackgroundImageDir() + "rmkbackground.jpg";
	}

	public static String systemName()
	{
		return "rmk";
	}
	public static String databaseIP()
	{
		try
		{
			String results = p.getProperty("databaseIP");
			if (results != null)
				return results;
		} catch (Exception e)
		{
		}
		return "localhost";
	}
	public static String databaseName()
	{
		return systemName();
	}

	public static String getErrorLogLocation()
	{
		try
		{
			String results = p.getProperty("errorLogFile");
			if (results != null)
				return results;
		} catch (Exception e)
		{
		}
//		return getBackgroundImageDir() + "RMKSystemErrors.log";
		return "";
	}
	public static String getBusinessNumber()
	{
		try
		{
			String results = p.getProperty("businessNumber");
			if (results != null)
				return results;
		} catch (Exception e)
		{
		}
		//  	return "(407)855-8075";
		return "999 999 9999";
	}
	public static String getFaxNumber()
	{
		try
		{
			String results = p.getProperty("faxNumber");
			if (results != null)
				return results;
		} catch (Exception e)
		{
		}
		return "(407)855-9054";
	}

	public static String getName()
	{
		Properties osProps;
		String results = "";
		osProps = System.getProperties();
		try
		{
			results = osProps.getProperty("user.home");
			if(results!= null && results.length() > 0)
				results = results.substring(results.lastIndexOf('\\')+1);
			else
				results = osProps.getProperty("user.name");
		} catch (Exception e)
		{
		}
		if (results.endsWith("carpus")
			&& !carpus.util.SystemPrefrences.runningOnWindows())
			results = "ordrc";

		return results;
	}
	public static void main(String args[]) throws Exception
	{
		loadProperties();
		getDB();
		//  	loadProperties();
		p.list(System.out);
		//  	ErrorLogger.getInstance().logMessage(getDataFileLocation("PartTypes"));
		ErrorLogger.getInstance().logMessage(getFaxNumber());
		ErrorLogger.getInstance().logMessage(getDBType());
		//  	ErrorLogger.getInstance().logMessage(p);	
	}

}
