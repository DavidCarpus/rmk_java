package rmk.database;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import carpus.database.DBInterface;
import rmk.database.dbobjects.PartPrices;

public class PartPriceTable{
//    private static PartPriceTable instance = new PartPriceTable();
    private static Hashtable prices;
//      static public DBAccess db;
    public static carpus.database.DBInterface db;

  public PartPriceTable(DBInterface dbase){
	if(db == null)	db = dbase;
	if(prices == null) prices = new Hashtable();
    }

    //    private PartPriceTable(){
//	prices = new Hashtable();
//	db = Configuration.Config.getDB();
//    }
//
//    public static synchronized PartPriceTable getInstance(){
//	return instance;
//    }

    
    public static int getMinYear(){
	return 2002;
    }
    public static int getMaxYear(){
	GregorianCalendar now = new GregorianCalendar();
	now.add(GregorianCalendar.MONTH, Configuration.Config.getMonthsBacklogged());
	if(now.get(Calendar.MONTH) == 11)
	    return now.get(Calendar.YEAR) +1;
	else
	    return now.get(Calendar.YEAR);
    }


    public PartPrices getPartPriceObject(int year, int partID){
	String key = year + ":" + partID + ":";
	PartPrices partPrice = (PartPrices)prices.get(key);

	if(partPrice != null && partPrice.getYear() == year) 
		return partPrice;

//	ErrorLogger.getInstance().logMessage(this.getClass().getName() + " load price table:" + year);
//    	ErrorLogger.getInstance().logMessage(rmk.ErrorLogger.getInstance().stkTrace("PartPriceTable"));
	
	Vector lst;
	synchronized(db){
	    lst = db.getItems("PartPrices", "year=" + year);
	}
	if(lst == null) return null;
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + "lst size:" + lst.size());
	for(Enumeration items = lst.elements(); items.hasMoreElements();){
	    partPrice = (PartPrices)items.nextElement();
	    key = year + ":" + partPrice.getPartID()+ ":";
	    prices.put(key, partPrice);
	}
	key = year + ":" + partID+ ":";
	return (PartPrices)prices.get(key);
    }

    public double getPartPrice(int year, int partID){
	double results=0;

	PartPrices partPrice = getPartPriceObject(year, partID);
	if(partPrice != null){
//	    rmk.ErrorLogger.getInstance().logMessage("Got " + year +
//			     " price for:" + partID + ":$" + partPrice.getPrice());
	    results = partPrice.getPrice();
	} else {
	    rmk.ErrorLogger.getInstance().logMessage("Unable to determine " + year +
						     " price for:" + partID);
	}

	return results;
    }
    public double getPartPriceDiscounted(int year, int partID, double discount){
	double results=0;

	PartPrices partPrice = getPartPriceObject(year, partID);
	if(partPrice != null){
	    results = partPrice.getPrice();
	    if(partPrice.isDiscountable())
    		results *= (1.0 - discount);
	}

	return results;
    }
    public double getPartPriceDiscount(int year, int partID, double discount){
	double results=0;

	PartPrices partPrice = getPartPriceObject(year, partID);
	if(partPrice != null){
	    results = partPrice.getPrice();
	    if(partPrice.isDiscountable())
		results *= discount;
	}

	return results;
    }
}
