package carpus.util;

public interface LoggerInterface {
//      public static Logger getInstance();
    public void logError(String msg, Exception e);
    public void logWarning(String msg);
    public void logMessage(String msg);
}
