package rmk;

//  rmk.ErrorLoger.getInstance().logError();

//  import java.sql.*;
import java.io.*;

import Configuration.Config;

public class ErrorLogger implements carpus.util.LoggerInterface{
    private static ErrorLogger instance = new ErrorLogger();
//      boolean debug = !carpus.util.SystemPrefrences.runningOnWindows();
    boolean debug = Configuration.Config.IDE;
    FileOutputStream logFile=null;
    String logLocation = "";
    
    private ErrorLogger(){ // singleton... 
        
		logLocation = "s:" + File.separatorChar + Config.getName() + "RMKSystemErrors.log" ;
        if(!debug){
            try{
                logLocation = Config.getName() + "_RMKSystemErrors.log" ;
                System.out.println(this.getClass().getName() + ":"+ logLocation);
                logFile = new FileOutputStream( logLocation, true);
            } catch (Exception e) {}
        }
        if(logFile == null){
            try{
                logLocation = Config.getErrorLogLocation();
                if(logLocation != null && logLocation.length()>0)
                	logFile = new FileOutputStream( logLocation, true);
            } catch (Exception e){
                System.out.println("Unable To Open Error LogFile.....\n" + e);
            }
        }
        if(logFile != null){
        	if(!Configuration.Config.IDE){
        	    PrintStream ps = new PrintStream(logFile);
        	    System.setErr(ps);
        		System.out.println(System.getProperties().getProperty("user.home"));
        		System.setOut(ps);
        	}
        }
    }

    public static ErrorLogger getInstance(){
        return instance;
    }

    public void logError(String msg, Exception e){
	String message = 
	    ""
//  	    "-----------------\n"
	    + "Error:\n" + msg 
	    + "(" + (new java.util.Date()) + ")"
	    + "\n"
	    + e + "\n"
	    + stkTrace(e) + "\n"
	    + "-----------------";
	log(message);
	
//    	log("-----------------\n"
//  	    + "Error:\n" + msg + "\n"
//  	    + e + "\n"
//  	    + stkTrace(e) + "\n"
//  	    + "-----------------"
//  	    );
    }
    public void logWarning(String msg){
  	log(
	    ""
//  	    "-----------------\n"
	    + "Warning:\n" + msg + "\n"
	    + "-----------------"
	    );
    }
    public void logMessage(String msg){
  	log(
	    ""
//  	    "-----------------\n"
	    + "Message:\n" + msg + "\n"
	    + "-----------------"
	    );
    }
    public String stkTrace(String delClass){
	String results="";
	Exception e = new Exception();
	StackTraceElement[] trace = e.getStackTrace();
	results += "\tat ";
	for(int i=0; i< trace.length; i++){
	    String item = ""+trace[i];
	    if(!(item.startsWith("java.") || item.startsWith("javax.")  || item.startsWith("sun.")))
  		if(!(item.startsWith("rmk.DBModel."))){
		    if(!(delClass != null && item.toUpperCase().indexOf(delClass.trim().toUpperCase()) > 0))
			//  		if(!(item.startsWith("carpus.database.Logger")))
			results += "\n " + trace[i];
		}
	}
//  	System.out.println(results);
	return results;
    }

    public String stkTrace(Exception e){
	String results="";
	StackTraceElement[] trace = e.getStackTrace();
	results += "\tat ";
	for(int i=0; i< trace.length; i++){
	    String item = ""+trace[i];
	    if(!(item.startsWith("java.") || item.startsWith("javax.")  || item.startsWith("sun.")))
  		if(!(item.startsWith("rmk.DBModel.")))
//  		if(!(item.startsWith("carpus.database.Logger")))
		    results += "\n " + trace[i];
	}
//  	System.out.println(results);
	return results;
    }

    void log(String message){
    	
	try{
		if (logFile != null)
			logFile.write(( message + "\n").getBytes());
		else
			System.out.println(message);
//  	    logFile.write((new java.util.Date() + "\n" + message + "\n").getBytes());
	    if(logFile != null && debug)
	    	System.out.println(message);	    
	} catch (Exception e){
	    System.out.println(this.getClass().getName() + ":"+ e);
	    
	}
    }
    public static void main(String args[])
	throws Exception
    {
	ErrorLogger.getInstance().logError("TestError", new Exception("Test"));
    }
}
