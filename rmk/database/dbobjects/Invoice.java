package rmk.database.dbobjects;
import java.sql.*;
import java.io.*;
import carpus.database.Fixed;
import carpus.util.DateFunctions;

import java.util.Vector;

public class Invoice extends DBObject{
    Vector items=null;
    Boolean dealer=null;

    public static final int[] lengths={
	Fixed.LONG_SIZE, Fixed.DATE_SIZE, Fixed.DATE_SIZE, 
	Fixed.DATE_SIZE, Fixed.DATE_SIZE, Fixed.LONG_SIZE, 
	Fixed.LONG_SIZE, Fixed.MEMO_SIZE, Fixed.CURRENCY_SIZE, 
	Fixed.FLOAT_SIZE, Fixed.FLOAT_SIZE, Fixed.CURRENCY_SIZE, 
	15, Fixed.MEMO_SIZE, Fixed.CURRENCY_SIZE, 3, 16, 21, 5, 
	Fixed.BOOLEAN_SIZE, Fixed.BOOLEAN_SIZE, Fixed.BOOLEAN_SIZE};

    public static final String[] fields={
	"Invoice","DateOrdered","DateEstimated","DateEdit",
	"DateShipped","CustomerID","BillingAddress",
	"ShippingInfo","TotalRetail","DiscountPercentage",
	"TaxPercentage","ShippingAmount","ShippingInstructions",
	"Comment","AmountPaid","Zone","PONumber",
	"CreditCardNumber","CreditCardExpiration","StockOrder",
	"PickUp","ShopSale"};

    public static int[] getFieldLengths_txt(){	return 	lengths;    }
    public static int getTotalFieldLengths_txt(){return getTotalFieldLengths(lengths);}

    public Invoice(){
    	super(fields);
    }
    
    public Invoice(Object[]  data){
	super(fields);
	setDefaults();
	setValues(data);
  	transfering = true;
//  	transfering = false;
    }

    public Invoice(int ID){
	super(fields);
	setID(new Long(ID));
	setDefaults();
    }
    private void setDefaults(){
	setTotalRetail(0);
	setShippingAmount(0);
	setDiscountPercentage(0);
	setTaxPercentage(0);
	setShopSale(false);
	setDateOrdered(new java.util.GregorianCalendar()); // Now
	setDateEdit(null);
	setDateShipped(null);
	setItems(null);
	setPickUp(false);
//  	setCorrectedAddressID(0);
//  	setTimesUsed(0);
//    	setZONE("");
    }

    void setEdited(){
	edited=true;
	setDateEdit(new java.util.GregorianCalendar());
    }
    public void setInvoice(long value){ values[0] = new Long(value);setEdited();}
    public void setDateOrdered(java.util.GregorianCalendar value){values[1] = value;setEdited();}
    public void setDateEstimated(java.util.GregorianCalendar value){values[2] = value;setEdited();}
    public void setDateEdit(java.util.GregorianCalendar value){values[3] = value;edited=true;}
    public void setDateShipped(java.util.GregorianCalendar value){values[4] = value;setEdited();}
    public void setCustomerID(long value){values[5] = new Long(value);setEdited();}
    public void setBillingAddress(long value){values[6] = new Long(value);setEdited();}
    public void setShippingInfo(String value){values[7] = value;setEdited();}
    public void setTotalRetail(double value){values[8] = new Double(value);setEdited();}
    public void setDiscountPercentage(double value){values[9] = new Double(value);setEdited();}
    public void setTaxPercentage(double value){values[10] = new Double(value);setEdited();}
    public void setShippingAmount(double value){values[11] = new Double(value);setEdited();}
    public void setShippingInstructions(String value){values[12] = value;setEdited();}
    public void setComment(String value){values[13] = value;setEdited();}
    public void setAmountPaid(double value){values[14] = new Double(value);setEdited();}
    public void setZone(String value){values[15] = value;setEdited();}
    public void setPONumber(String value){values[16] = value;setEdited();}
    public void setCreditCardNumber(String value){values[17] = value;setEdited();}
    public void setCreditCardExpiration(String value){values[18] = value;setEdited();}
    public void setStockOrder(boolean value){values[19] = new Boolean(value);setEdited();}
    public void setPickUp(boolean value){values[20] = new Boolean(value);setEdited();}
    public void setShopSale(boolean value){values[21] = new Boolean(value);setEdited();}
    public void setItems(Vector lst){
	items = lst;
    }


    public long getInvoice(){ return ((Long)values[0]).longValue();}
    public java.util.GregorianCalendar getDateOrdered(){ return (java.util.GregorianCalendar)values[1];}
    public java.util.GregorianCalendar getDateEstimated(){ return (java.util.GregorianCalendar)values[2];}
    public java.util.GregorianCalendar getDateEdit(){ return (java.util.GregorianCalendar)values[3];}
    public java.util.GregorianCalendar getDateShipped(){ return (java.util.GregorianCalendar)values[4];}
    public long getCustomerID(){ return ((Long)values[5]).longValue();}
    public long getBillingAddress(){ return ((Long)values[6]).longValue();}
    public String getShippingInfo(){ return (String)values[7];}
    public double getTotalRetail(){ return ((Double)values[8]).doubleValue();}
    public double getDiscountPercentage(){
	double results = ((Double)values[9]).doubleValue();
	if(results > 1) results /= 100.0;
	return results;
//  	return ((Double)values[9]).doubleValue();
    }
    public double getTaxPercentage(){
    	double taxRate = ((Double)values[10]).doubleValue();
    	taxRate = Math.floor(taxRate * 1000 + 0.5) / 1000.0;
    	return taxRate;
//    	return ((Double)values[10]).doubleValue();
    	}
    public double getShippingAmount(){ return ((Double)values[11]).doubleValue();}
    public String getShippingInstructions(){ return (String)values[12];}
    public String getComment(){ return (String)values[13];}
    public double getAmountPaid(){ return ((Double)values[14]).doubleValue();}
    public String getZone(){ return (String)values[15];}
    public String getPONumber(){ return (String)values[16];}
    public String getCreditCardNumber(){ return (String)values[17];}
    public String getCreditCardExpiration(){ return (String)values[18];}
    public boolean isStockOrder(){ return ((Boolean)values[19]).booleanValue();}
    public boolean isPickUp(){ return values[20] != null?((Boolean)values[20]).booleanValue():false;}
    public boolean isShopSale(){ return ((Boolean)values[21]).booleanValue();}
    public Vector getItems(){return items;}


    public Boolean isDealer(){    	return dealer;    }
    public void setDealer(boolean dealerInv){ dealer = new Boolean(dealerInv); }


//      public void setValues(ResultSet  recordSet) throws Exception;
    public void setValues(Object[]  data){
	int i=0;
	setInvoice(Long.parseLong((String)data[i++]));
	setDateOrdered(carpus.util.DateFunctions.gregorianFromString((String)data[i++]));
	setDateEstimated(carpus.util.DateFunctions.gregorianFromString((String)data[i++]));
	setDateEdit(carpus.util.DateFunctions.gregorianFromString((String)data[i++]));
	setDateShipped(carpus.util.DateFunctions.gregorianFromString((String)data[i++]));
	setCustomerID(Long.parseLong((String)data[i++]));
	if(data[i] == null || (""+data[i]).equals(""))
	    data[i] = "0";
	setBillingAddress(Long.parseLong((String)data[i++]));
	setShippingInfo((String)data[i++]);
	setTotalRetail(Double.parseDouble((String)data[i++]));
	setDiscountPercentage(Double.parseDouble((String)data[i++]));
	setTaxPercentage(Double.parseDouble((String)data[i++]));
	setShippingAmount(Double.parseDouble((String)data[i++]));
	setShippingInstructions((String)data[i++]);
	setComment((String)data[i++]);
	if(data[i] == null || (""+data[i]).equals(""))
	    data[i] = "0";
	setAmountPaid(Double.parseDouble((String)data[i++]));
	setZone((String)data[i++]);
	setPONumber((String)data[i++]);
	setCreditCardNumber((String)data[i++]);
	setCreditCardExpiration((String)data[i++]);
	setStockOrder((!((String)data[i++]).equals("0")));
	setPickUp((!((String)data[i++]).equals("0")));
	setShopSale((!((String)data[i++]).equals("0")));
    }

    public String toString(){
	String results="";
	results +=  "Inv:" + carpus.util.Formatting.textSizer(""+getInvoice(),5) 
	    + ":" + " " + carpus.util.Formatting.financial(getTotalRetail(), 9)
//  	    + getShippingInfo()
	    ;
//  	if(items != null)
//  	    for(int lineNumber=0; lineNumber < items.size(); lineNumber++){
//  		results += (InvoiceEntries)items.get(lineNumber);
//  	    }

	return "[" + results + "]";
    }
    public String getIDSQL(){
	String qry = "";
	String dbClass = Configuration.Config.getDBType();
	if(dbClass.equals("ACCESS")){
	    qry = "select max(Invoice)+1 from Invoices";
	} else if(dbClass.equals("MYSQL")){
		qry = "select null";
	} else
  	   qry = "select nextval('Invoices_Invoice_seq')";
	return qry;
    }

    public String saveSql(long id) throws Exception{
	return saveSql("Invoices", id);
    }
    public String updateSql(long id) throws Exception{
	return updateSql("Invoices", "Invoice", id);
    }
   public String deleteSql(long id) throws Exception{
	return deleteSql("Invoices", "Invoice", id);
    }
    public String deactivateSql(long id) throws Exception{
	return deactivateSql("Invoices", "Invoice", id);
    }

    public void setValues(ResultSet  recordSet) throws Exception{
    	if(getInvoice() == 0)
        	setInvoice(recordSet.getInt("Invoice"));
// 	System.out.println(recordSet.getString("DateOrdered"));
	carpus.util.DateFunctions dte = new carpus.util.DateFunctions();
	setDateOrdered(DateFunctions.gregorianFromString(recordSet.getString("DateOrdered")));
	setDateEstimated(DateFunctions.gregorianFromString(recordSet.getString("DateEstimated")));
	setDateEdit(DateFunctions.gregorianFromString(recordSet.getString("DateEdit")));
	setDateShipped(DateFunctions.gregorianFromString(recordSet.getString("DateShipped")));
	setCustomerID(recordSet.getInt("CustomerID"));
	setBillingAddress(recordSet.getInt("BillingAddress"));
	setShippingInfo(recordSet.getString("ShippingInfo"));
	setTotalRetail(recordSet.getFloat("TotalRetail"));
	setDiscountPercentage(recordSet.getFloat("DiscountPercentage"));
	setTaxPercentage(recordSet.getFloat("TaxPercentage"));
	setShippingAmount(recordSet.getFloat("ShippingAmount"));
	setShippingInstructions(recordSet.getString("ShippingInstructions"));
	setComment(recordSet.getString("Comment"));
	setAmountPaid(recordSet.getFloat("AmountPaid"));
	setZone(recordSet.getString("Zone"));
	setPONumber(recordSet.getString("PONumber"));
	setCreditCardNumber(recordSet.getString("CreditCardNumber"));
	setCreditCardExpiration(recordSet.getString("CreditCardExpiration"));
	setStockOrder(recordSet.getBoolean("StockOrder"));
	setPickUp(recordSet.getBoolean("PickUp"));
	setShopSale(recordSet.getBoolean("ShopSale"));
	transfering = false;
    }
    public static void main(String args[]) throws Exception{
	int startRow = 0;
	String fileName=Configuration.Config.getDataFileLocation("Invoices");
	System.out.println("\nXFer: " + fileName);	

	carpus.database.Fixed fixed = new carpus.database.Fixed();
	BufferedInputStream in = (new BufferedInputStream( new FileInputStream(fileName)));
	int row=0;
	byte[] currInput = new byte[Invoice.getTotalFieldLengths_txt()+2]; // CR-LF
	Object[] lst;
	while( in.read(currInput)!= -1 ){
	    if(row%100 == 0) System.out.print(row + "-");
	    if(row > startRow ){
		lst = fixed.getArray(new String(currInput),Invoice.lengths);
		Invoice invoice = new Invoice(lst);
		if(invoice.getComment() != null){
		    System.out.println(invoice.getComment());
		}
//  		java.util.Vector outputLst = new java.util.Vector();
//      		outputLst.add(invoice);
//    		if(db.saveItems("Invoices", outputLst) == null) return;
      	    }
	    row++;
	}
//  	carpus.database.DBInterface db = Configuration.Config.getDB();
//  // 	carpus.database.PostgresDB db = new carpus.database.PostgresDB();
//  	db.connect();
//  	java.util.Vector lst = db.getItems("Invoices", "customerid = 1");

//        for(int i=0; i < lst.size(); i++){
//  	  Invoice item = (Invoice)lst.get(i);
//  	  System.out.println(item);      
//        }

    }   
}
