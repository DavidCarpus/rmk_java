package carpus.util;

public class Logger implements LoggerInterface{
    private static Logger instance = new Logger();

    private Logger(){} // singleton... 

    public static Logger getInstance(){
	return instance;
    }
    public void logMessage(String msg){
  	System.out.println("\n-----------------\n"
			   + "Message:" + msg + "\n"
			   + "-----------------\n"
			   );
    }
    
    public void logError(String msg, Exception e){
  	System.out.println("\n-----------------\n"
			   + "Error:" + msg + "\n"
			   + "-----------------\n"
			   );
    }
    public void logWarning(String msg){
  	System.out.println("\n-----------------\n"
			   + "Warning:" + msg + "\n"
			   + "-----------------\n"
			   );
    }
}
