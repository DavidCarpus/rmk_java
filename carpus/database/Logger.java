package carpus.database;

public class Logger implements carpus.util.LoggerInterface{
    private static Logger instance = new Logger();
    boolean debug = !carpus.util.SystemPrefrences.runningOnWindows();
    
    private Logger(){} // singleton... 

    public static Logger getInstance(){
  	return instance;
    }
    public void logMessage(String msg){
  	System.out.println("-----------------\n"
			   + "Message:\n" + msg + "\n"
			   + "-----------------"
			   );
//    	if(debug) prtStack();
    }
    
    public void logError(String msg, Exception e){
  	System.out.println("-----------------\n"
			   + "Error:\n" + msg + "\n"
			   + e + "\n"
			   + "-----------------"
			   );
	if(debug){
	    prtStack(e);
//  	    System.exit(0);
	}

    }
    private void prtStack(Exception e){
	StackTraceElement[] trace = e.getStackTrace();
	System.out.println("******************");
	for(int i=0; i< trace.length; i++){
	    String item = ""+trace[i];
	    if(!(item.startsWith("java.") || item.startsWith("javax.")  || item.startsWith("sun.")))
		if(!(item.startsWith("carpus.database.Logger"))
		   && !(item.startsWith("org.postgresql"))
		   )
		    System.out.println("\tat " + trace[i]);	    
	}
	System.out.println("******************");
    }
    private void prtStack(){
	try{
	    Class crasher = Class.forName("NonExsistentClass");
	} catch (Exception e){
	    prtStack(e);
	}
    }

    public void logWarning(String msg){
  	System.out.println("-----------------\n"
			   + "Warning:\n" + msg + "\n"
			   + "-----------------"
			   );
  	if(debug) prtStack();
    }
    public void logError(java.sql.SQLException e , String stmt){	
	logError(e + "\n" + stmt, e);
    }

}
