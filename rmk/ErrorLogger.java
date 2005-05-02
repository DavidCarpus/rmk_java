package rmk;

//  rmk.ErrorLoger.getInstance().logError();

//  import java.sql.*;
import java.io.*;
import java.util.Date;

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
	    + "(" + timestamp() + ")"
	    + "\n"
	    + e + "\n"
	    + stkTrace(e) + "\n"
	    + "-----------------";
	log(message);
    }
    public void logWarning(String msg){
  	log(
	    ""
//  	    "-----------------\n"
//	    + "Warning:\n" + msg + "\n"
//	    + "-----------------"
	    + "Warning:  "
	    + "(" + timestamp() + ")"
		+ "[" + msg + "]"
		);
    }
    public void logMessage(String msg){
  	log(
	    ""
//  	    "-----------------\n"
	    + "Message:  "
	    + "(" + timestamp() + ")"
	    + "-" + getCaller() + "-"
		+ "[" + msg + "]"
//	    + "-----------------"
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

    String timestamp(){
    	String results="";
    	Date currDate = new java.util.Date();
    	results += currDate.getMonth()+1 + "/" + currDate.getDate();
    	results += " " + (currDate.getHours()) + ":";
    	results += currDate.getMinutes() + ":";
    	results += currDate.getSeconds();
    	return results;
    }
    public static String getCaller(){
    	String results="";
    	Exception e = new Exception();
    	StackTraceElement[] trace = e.getStackTrace();
    	for(int i=2; i< 3; i++){
    	    String item = ""+trace[i];
    	    if(!(item.startsWith("java.") || item.startsWith("javax.")  || item.startsWith("sun.")))
      		if(!(item.startsWith("rmk.DBModel."))){
    			results += " " + trace[i];
    		}
    	}
    	return results.substring(results.indexOf("("));
    }
    public static String getCallerFunction(){
    	String results="";
    	Exception e = new Exception();
    	StackTraceElement[] trace = e.getStackTrace();
    	for(int i=2; i< 3; i++){
    	    String item = ""+trace[i];
    	    if(!(item.startsWith("java.") || item.startsWith("javax.")  || item.startsWith("sun.")))
      		if(!(item.startsWith("rmk.DBModel."))){
    			results += " " + trace[i];
    		}
    	}
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
    public void logDebug(String msg, boolean logTrace){
    	String caller = getCallerFunction();

    	String mesg = "Debug:" + caller.substring(caller.indexOf("(")) + ":" +  msg;
    	
    	if(logTrace){
    		String trace = stkTrace("ErrorLogger");
    		trace = trace.replaceFirst(caller,"");
    		mesg += trace;
    	}
    	
    	log(mesg);
    }
    
    public void logDebugCommand(String msg){
    	String caller = getCallerFunction();
    	caller = caller.substring(caller.indexOf("("));
    	
    	String mesg = "Action:" + caller + ":" +  msg ;
    	
    	String trace = stkTrace("ErrorLogger"); 
    	
    	mesg += trace;
    	log(mesg);
    }
    
    public void TODO(){
    	String caller = getCallerFunction();
    	String mesg = "***** Unimplemented Functionallity *****:" + caller;
    	log(mesg);
    	
    }
    
}
