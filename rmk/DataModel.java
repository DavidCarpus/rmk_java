package rmk;

import rmk.database.CustomerInfo;

public class DataModel{
	private static int currYear=0;
    static public java.util.GregorianCalendar currentDate = null;
    static public final String currentDateStr = 
			carpus.util.DateFunctions.getSQLDateStr(currentDate);

    static DataModel instance = new DataModel();

    public static carpus.database.DBInterface db = Configuration.Config.getDB();;
    public rmk.database.PartInfo partInfo = null;
    public rmk.database.CustomerInfo customerInfo = null;
    public rmk.database.InvoiceInfo invoiceInfo = null;
    public rmk.database.FinancialInfo financialInfo = null;
    public rmk.database.PartPriceTable pricetable = null;    

    //==========================================================
    //==========================================================
    //==========================================================
    public static void main(String args[])
	throws Exception
    {
	DataModel sys = DataModel.getInstance();

//  	System.out.println(sys.getClass().getName() + ":"+ sys.partInfo.largestPartID());
//  	System.out.println(sys.getClass().getName() + ":"+ sys.partInfo.getCurrentPartPrices());
//  	System.out.println(sys.getClass().getName() + ":"
//  			   + sys.customerInfo.getCustomersFromPhone(getFixedPhoneNumber("4074361113")));

//  	System.out.println(sys.getClass().getName() + ":"+ sys.invoiceInfo.getInitialInvoices(7));
	
//    	sys.customerInfo.getCustomersByLastName("sportsman");
//  	System.out.println(sys.partInfo.getPart(47));
//  	System.out.println(sys.customerInfo.isDealer(7));
//  	System.out.println(sys.customerInfo.isDealer(80000));
	
//  	double price = sys.partInfo.getPartPrice(47, 2003);
//  	System.out.println("47  :" + price);
//  	price = sys.partInfo.getPartPrice(79, 2003);
//  	System.out.println("79  :" + price);
//  	price = sys.partInfo.getPartPrice(75, 2003);
//  	System.out.println("75  :" + price);
//  	price = sys.partInfo.getPartPrice(135, 2003);
//  	System.out.println("135 :" + price);
//  	System.out.println(sys.invoiceInfo.getInvoiceEntryAdditions(112952));
    }
    //==========================================================
    //==========================================================

    //==========================================================
    public static DataModel getInstance(){
        return instance;
    }
    
    public static int getCurrentYear() {
		if (currYear == 0)
			currYear = (new java.util.GregorianCalendar())
					.get(java.util.GregorianCalendar.YEAR);
		return currYear;
	}
    
    private DataModel(){
        db = Configuration.Config.getDB();;
        partInfo = new rmk.database.PartInfo(db);
        customerInfo = new rmk.database.CustomerInfo(db);
        invoiceInfo = new rmk.database.InvoiceInfo(db);
        financialInfo = new rmk.database.FinancialInfo(db);
        pricetable = new rmk.database.PartPriceTable(db);
        currentDate = new java.util.GregorianCalendar();
    }
    
    //==========================================================
    public static String getFixedPhoneNumber(String phone){
	return CustomerInfo.getFixedPhoneNumber(phone);
    }
    //==========================================================
//      public Vector getPayments(int invoiceID){
//    	Vector lst; 	
//    	lst = db.getItems("Payments", "invoice = " + invoiceID + "");	

//  	return lst;
//      }
    //==========================================================
}
