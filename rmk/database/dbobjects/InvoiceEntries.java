package rmk.database.dbobjects;
import java.sql.*;
import carpus.database.Fixed;
import java.util.Vector;

public class InvoiceEntries extends DBObject{
    Vector items=null;
    int partType=0;
    Invoice parent=null;

    public static final int[] lengths={Fixed.LONG_SIZE, Fixed.FLOAT_SIZE, Fixed.LONG_SIZE, 10, 
				       Fixed.INT_SIZE, Fixed.CURRENCY_SIZE, Fixed.MEMO_SIZE, Fixed.BOOLEAN_SIZE, 
				       Fixed.CURRENCY_SIZE, Fixed.CURRENCY_SIZE, Fixed.BOOLEAN_SIZE};
    public static final String[] fields={"InvoiceEntryID","Invoice","PartID","PartDescription",
					 "Quantity","Price","Comment", "Discounted", 
					 "NonDiscountable", "TotalRetail", "Taxable" };
    
    public static int[] getFieldLengths_txt(){	return 	lengths;    }
    public static int getTotalFieldLengths_txt(){	
	int ct=0;
	for(int i=0; i< lengths.length; i++){
	    ct += lengths[i];
	}
	return 	ct;    
    }

    public InvoiceEntries(Object[]  data){
		super(fields);
		setDefaults();
		setValues(data);
		transfering=true;
    }
    public InvoiceEntries(int ID){
		super(fields);
		setID(new Long(ID));
		setDefaults();
		setInvoice(0);
		setPartID(0);
		setPrice(0);
		setQuantity(0);
//  	transfering = false;
    }
    private void setDefaults(){

    }
    public long getInvoiceEntryID(){ return ((Long)values[0]).longValue();}
    public double getInvoice(){ return ((Long)values[1]).longValue();}
    public long getPartID(){ return ((Long)values[2]).longValue();}
    public String getPartDescription(){ return (String)values[3];}
    public int getQuantity(){ return ((Integer)values[4]).intValue();}
    public double getPrice(){ return ((Double)values[5]).doubleValue();}
    public String getComment(){ return (String)values[6];}
    public boolean isDiscounted(){
    	if(values[7]==null) return false;
    	return ((Boolean)values[7]).booleanValue();}
    public double getNonDiscountable(){ return (Double)values[8];}
    public double getTotalRetail(){ return (Double)values[9];}
    public boolean isTaxable(){
    	if(values[10]==null) return false;
    	return ((Boolean)values[10]).booleanValue();}

    public Vector getFeatures(){return items;}

    public int getPartType(){ return partType;}
    public void setPartType(int type){partType = type;}

    public void addFeature(InvoiceEntryAdditions feature){
	if(items == null){
	    items = new Vector();
	}
	feature.parent = this;
	feature.setEntryID(getInvoiceEntryID());
	items.add(feature);
    }

    public void setInvoiceEntryID(long value){
	values[0] = new Long(value);
	if(items != null){
	    for(java.util.Iterator<InvoiceEntryAdditions> iter = items.iterator(); iter.hasNext();){
		InvoiceEntryAdditions addition = (InvoiceEntryAdditions)iter.next();
		if(addition.getEntryID() != value)
		    addition.setEntryID(value);
	    }
	}
	edited=true;
    }
    public void setInvoice(long value){values[1] = new Long(value);edited=true;}
    public void setPartID(long value){values[2] = new Long(value);edited=true;}
    public void setPartDescription(String value){values[3] = value;edited=true;}
    public void setQuantity(int value){values[4] = new Integer(value);edited=true;}
    public void setPrice(double value){values[5] = new Double(value);edited=true;}
    public void setComment(String value){values[6] = value;edited=true;}
    public void setDiscounted(boolean value){values[7] = new Boolean(value);edited=true;}
    public void setNonDiscountable(double value){values[8] = new Double(value);edited=true;}
    public void setTotalRetail(double value){values[9] = new Double(value);edited=true;}
    public void setTaxable(boolean value){values[10] = new Boolean(value);edited=true;}

    public void setFeatures(Vector lst){
//  	(new Exception("e")).printStackTrace();
		if(lst == null) {
		    items = null;
		    return;
		}
		items = new Vector();
		for(int item=0; item < lst.size(); item++){
		    addFeature((InvoiceEntryAdditions)lst.get(item));
		}
    }

//      public void setValues(ResultSet  recordSet) throws Exception;
    public void setValues(Object[]  data){
	int i=0;
	setInvoiceEntryID(Long.parseLong((String)data[i++]));
	setInvoice((long)Double.parseDouble((String)data[i++]));
	setPartID(Long.parseLong((String)data[i++]));
	setPartDescription((String)data[i++]);
	setQuantity(Integer.parseInt((String)data[i++]));
	setPrice(Double.parseDouble((String)data[i++]));
	setComment((String)data[i++]);
    }

    public String toString(){
	String results="";
	results +=  "Ie:" + getInvoiceEntryID() + ":" + getInvoice() + " $" + getPrice() + " ";
	String desc =getPartDescription();
	if(desc == null || desc.length()<=0) desc = "#"+getPartID(); 
	results += desc;
	
	if(getComment() != null && getComment().trim().length() > 0)
	    results +=  "**" + getComment() + "**";
	if(items != null)
	    for(int lineNumber=0; lineNumber < items.size(); lineNumber++){
		results += (InvoiceEntryAdditions)items.get(lineNumber) + ",";
	    }
	return "(" + results + ")";
    }
   public String getIDSQL(){	
	String qry = "";
	String dbClass = Configuration.Config.getDBType();
	if(dbClass.equals("ACCESS")){
	    qry = "select max(InvoiceEntryID)+1 from InvoiceEntries";
	} else if(dbClass.equals("MYSQL")){
		qry = "select null";
	} else
  	   qry =   "select nextval('invoiceentryaddi_additionid_seq')";   
	return qry;
   }


    public String saveSql(long id) throws Exception{
	return saveSql("InvoiceEntries", id);
    }
    public String updateSql(long id) throws Exception{
	return updateSql("InvoiceEntries", "InvoiceEntryID", id);
    }
   public String deleteSql(long id) throws Exception{
	return deleteSql("InvoiceEntries", "InvoiceEntryID", id);
    }
    public String deactivateSql(long id) throws Exception{
	return deactivateSql("InvoiceEntries", "InvoiceEntryID", id);
    }

    public void setValues(ResultSet  recordSet) throws Exception{
// setInvoiceEntryID(recordSet.getInt("InvoiceEntryID"));
	setInvoice(recordSet.getLong("Invoice"));
	setPartID(recordSet.getInt("PartID"));
	setPartDescription(recordSet.getString("PartDescription"));
	setQuantity(recordSet.getInt("Quantity"));
	setPrice(recordSet.getFloat("Price"));
	setComment(recordSet.getString("Comment"));
	setDiscounted(recordSet.getBoolean("Discounted"));
	setNonDiscountable(recordSet.getFloat("NonDiscountable"));
	setTotalRetail(recordSet.getFloat("TotalRetail"));
	setTaxable(recordSet.getBoolean("Taxable"));

	transfering = false;
    }

    public void setParent(Invoice invoice){parent = invoice;}
    public Invoice getParent(){return parent;}
}
