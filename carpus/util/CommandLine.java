package carpus.util;

import java.util.GregorianCalendar;

public class CommandLine{
    public CommandLine(String header, String message, GregorianCalendar date, String args[]){
    	if (sendMail(args)){
	    System.out.println("Sending....");
//  	    new CLASS.EMail.Cathy( header, message, date);
	}
	if (saveLocation(args) != null){
	    System.out.println("Saving to " + saveLocation(args) + "....");
//  	    new CLASS.FileIO.WriteFile(message, saveLocation(args));
	}
	else {
	    if (sendMail(args)) 
		System.out.println("-- Sent --");
	    
	    System.out.println(message);
	}
    }

    public static GregorianCalendar getDateFromArgs(String args[]){
	GregorianCalendar results = new GregorianCalendar();
	int year = results.get(GregorianCalendar.YEAR);
	int month= results.get(GregorianCalendar.MONTH);
	for (int i = 0; i < args.length; i++){
//  	    System.out.println("Arg(" + i + ") = " + args[i]);
	    if (args[i].toUpperCase().equals("-MONTH"))
		month = (new Integer(args[i+1])).intValue();
	    if (args[i].toUpperCase().equals("-YEAR"))
		year = (new Integer(args[i+1])).intValue();
	}
	results.set(GregorianCalendar.MONTH, month-1);
	results.set(GregorianCalendar.YEAR, year);
	results.set(GregorianCalendar.DATE, 1);
	return results;
    }

    public static boolean sendMail(String args[]){
	for (int i = 0; i < args.length; i++){
	    if (args[i].toUpperCase().equals("-MAIL"))
		return true;
	}
	return false;
    }
    public static String saveLocation(String args[]){
	for (int i = 0; i < args.length; i++){
	    if (args[i].toUpperCase().equals("-SAVE") && args.length > i+1){
		return args[i+1];
	    }
	    if (args[i].toUpperCase().equals("-WRITE") && args.length > i+1)
		return args[i+1];
	}
	return null;
    }
}
