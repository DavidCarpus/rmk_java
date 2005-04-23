package rmk.database.dbobjects;
import java.sql.*;
import java.io.*;
import carpus.database.Fixed;
import java.util.Vector;

public class InvoiceEntries extends DBObject{
    Vector items=null;
    int partType=0;

    public static final int[] lengths={Fixed.LONG_SIZE, Fixed.FLOAT_SIZE, Fixed.LONG_SIZE, 10, 
				       Fixed.INT_SIZE, Fixed.CURRENCY_SIZE, Fixed.MEMO_SIZE, Fixed.BOOLEAN_SIZE};
    public static final String[] fields={"InvoiceEntryID","Invoice","PartID","PartDescription",
					 "Quantity","Price","Comment", "Discounted" };
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

    public Vector getFeatures(){return items;}

    public int getPartType(){ return partType;}
    public void setPartType(int type){partType = type;}

    public void addFeature(InvoiceEntryAdditions feature){
	if(items == null){
	    items = new Vector();
	}
	feature.setEntryID(getInvoiceEntryID());
	items.add(feature);
    }

    public void setInvoiceEntryID(long value){
	values[0] = new Long(value);
	if(items != null){
	    for(java.util.Enumeration enum = items.elements(); enum.hasMoreElements();){
		InvoiceEntryAdditions addition = (InvoiceEntryAdditions)enum.nextElement();
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
	results +=  "Ie:" + getInvoiceEntryID() + ":" + getInvoice() + " $" + getPrice() + " " + getPartID();
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

	transfering = false;
    }

    public static void main(String args[]) throws Exception{
	carpus.database.PostgresDB db = new carpus.database.PostgresDB();
	db.connect();
	String fileName=Configuration.Config.getDataFileLocation("F_InvoiceEntries");

	carpus.database.Fixed fixed = new carpus.database.Fixed();
	BufferedInputStream in = (new BufferedInputStream( new FileInputStream(fileName)));
	int row=0;
	byte[] currInput = new byte[getTotalFieldLengths_txt()+3]; // CR-LF
	Object[] lst;
	while( in.read(currInput)!= -1
){
//   && row < 200){
//  	    if(row < 5){
		lst = fixed.getArray(new String(currInput),lengths);
//  		lst = fixed.getArrayUntrimed(new String(currInput),lengths);
		System.out.println(fixed.list(lst));

//  		fixed.list(lst);
  		InvoiceEntries item = new InvoiceEntries(lst);
    		System.out.print(row);
      		System.out.println(":" + item.getInvoiceEntryID());
  		System.out.println(item);		
//      		java.util.Vector outputLst = new java.util.Vector();
//  		outputLst.add(item);
//        		if(db.saveItems("InvoiceEntries", outputLst) == null) return;
//  	    }
	    row++;
	}
    }   
}
