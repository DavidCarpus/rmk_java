package carpus.util;

import java.io.RandomAccessFile;
import java.util.GregorianCalendar;
import java.io.File;

public class ErrorLogging{
    static final int TXRF_SYSTEM = 1;
    static final int BILLING_SYSTEM = 2;

    static RandomAccessFile streams[] = new RandomAccessFile[8];

    public static void log(String system, String msg ){
	int systemID = 0;
	String outfile = "";
	RandomAccessFile currStream;
	String message="";
	if (carpus.util.SystemPrefrences.runningOnWindows())
	    outfile = "\\Logs\\";
	else
	    outfile = "/home/carpus/code/java/Logs/";

	if (system.equals("TXRF")){
	    systemID = TXRF_SYSTEM;
	    outfile += "TXRF_Err.log";
	} else if (system.equals("BILLING")){
	    systemID = BILLING_SYSTEM;
	    outfile += "Billing_Err.log";
	} else {
	    outfile += "UnknownSystem_Err.log";
	}
	try{
	    File fileCheck = new File(outfile);
	    System.out.println("Check : " + outfile);
	    
	    if ( !fileCheck.exists() ){
		message = outfile;
		message += "\nCreated: " + DateFunctions.getSQLDateTimeStr(new GregorianCalendar()) + "\n";
//  		FileOutputStream f = null;
//  		System.out.println("Open FileOutputStream ");
		
//                  f = new FileOutputStream(outfile);
//  		System.out.println(message);
		
//  		message = outfile;
//  		message += "\nCreated: " + DateFunctions.getSQLDateTimeStr(new GregorianCalendar()) + "\n";
//  		f.write(message.getBytes());
//  		f.close();
	    }

	    if (streams[systemID] == null){
		streams[systemID] = new RandomAccessFile(outfile, "rw");
	    }
	    currStream = streams[systemID];
	    currStream.seek(currStream.length());

	    message += DateFunctions.getSQLDateTimeStr(new GregorianCalendar());
	    message += ": [" + system + "] " + msg + "\n";

	    currStream.writeBytes(message);
	} catch (Exception e){
	    System.out.println("ERR Logging Error: " + e);	    
	}

    }

    static String getFunctionNameFromTrace(StackTraceElement trace){
        String results = ""+trace;
        results = results.substring(0,results.indexOf('('));
        results = results.substring(results.lastIndexOf('.')+1);
        return results+"()";
    }
      
      public static String stkTrace(Exception e, String delClass) {
          String results = "";
          StackTraceElement[] trace = e.getStackTrace();
          results += ""+e +"\n";        
          results += "\tat ";
          for (int i = 0; i < trace.length; i++) {
              String item = "" + trace[i];
              if (!(item.startsWith("java.") || item.startsWith("javax.") || item
                      .startsWith("sun.")))
                      if ((item.startsWith("carpus.") && !(item.startsWith("carpus.util.ErrorLogging")))) { 
                          // only display OUR classes
                          if (delClass != null) { // If we passed a class name,only show errors in that class
                              if (item.toUpperCase().indexOf(
                                      delClass.trim().toUpperCase()) > 0) {
                                  results += "\n " + trace[i];
                              }
                          } else {
                              results += "\n " + trace[i];
                          }
                          //  		if(!(item.startsWith("carpus.database.Logger")))
                      }
          }
          return results;

      }
    
      public static String stkTrace(Exception e) {
          return stkTrace(e, null);
      }

      public static String stkTrace(String delClass) {
          Exception e = new Exception();
          return stkTrace(e, delClass);
      }

    public static void main(String args[]) {
	ErrorLogging.log("BILLING", "Test message");
    } 

}
