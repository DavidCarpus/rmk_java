package rmk.database.dbobjects;

public abstract class DBObject extends carpus.database.DBObject{
    static final carpus.database.DBInterface db= Configuration.Config.getDB();
    
    public static final int TYPE_UNKNOWN=0;
    public static final int TYPE_ADDRESS=1;
    public static final int TYPE_CUSTOMER=2;
    public static  final int TYPE_HISTORYITEMS=3;
    public static  final int TYPE_INVOICE=4;
    public static  final int TYPE_INVOICEENTRIES=5;
    public static  final int TYPE_INVOICEENTRYADDITIONS=6;
    public static  final int TYPE_PARTPROCES=7;
    public static  final int TYPE_PARTS=8;
    public static  final int TYPE_PARTTYPES=9;
    public static  final int TYPE_PAYMENTS=10;

    public DBObject(){};
    
    protected DBObject(long id){
    	setID(new Long(id));
    }
    
    protected DBObject(String[] fieldnames){
	fieldNames = fieldnames;
	values = new Object[fieldnames.length];
    }

    protected String saveSql(String tableName, long id) throws Exception{
	String baseQry="Insert  into " + tableName + " (";
	String dataQry="";
	if(id >0){
	    baseQry += fieldNames[0] + ", "; dataQry += " " + id + " ";
	}
	int i=0;
	try{
		Object currValue;
	for(i=1; i< values.length; i++){
		currValue = values[i];
	    if(currValue != null){
	    	String currValueClassName = currValue.getClass().getName();
		if(currValueClassName.endsWith("String")){
		    String value = getCleanedTextData((String)currValue).trim();
		    value = db.cleanTEXT(value);
//  		    if(value.trim() != ""){
			baseQry += fieldNames[i] + ", "; dataQry += ", '" + value + "'";
		} else if (isDateValue(currValue)){
			String value = getDBDateStr(currValue);

		    if(value.trim() != ""){
			baseQry += fieldNames[i] + ", "; dataQry += ",  " + value + "";
		    }
		} else if (currValueClassName.endsWith("Boolean")){
			boolean bVal = ((Boolean)currValue).booleanValue();
			baseQry += fieldNames[i] + ", "; dataQry += ", " + (bVal ? "1":"0");						
		} else {
		    baseQry += fieldNames[i] + ", "; dataQry += ", " + values[i];
		}
	    }
	}
	} catch (Exception e){
	     carpus.database.Logger.getInstance().logError(tableName + ".saveSql():" 
							   + i + fieldNames[i], e);
	}
	baseQry = baseQry.substring(0,  baseQry.length() -2);  
	baseQry += ")";
	dataQry = dataQry.substring(1);

//	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ baseQry + " Select " + dataQry);
	if(Configuration.Config.getDBType().equals("MYSQL")){
		return baseQry + " VALUES (" + dataQry + ")";
	} else{
		return baseQry + " Select " + dataQry;
	}
    }
	protected String updateSql(String tableName, String keyField, long id)
		throws Exception {
		String baseQry = "Update " + tableName + " set ";

		for (int i = 1; i < values.length; i++) {
			if (values[i] != null) {
				if (values[i].getClass().getName().endsWith("String")) {
					String value =
						getCleanedTextData((String) values[i]).trim();
					value = db.cleanTEXT(value);
					if (value.trim() != "") {
						baseQry += fieldNames[i] + "='" + value + "', ";
					}
				} else if (isDateValue(values[i])) {
					String value = getDBDateStr(values[i]);
					if (value.trim() != "") {
						baseQry += fieldNames[i] + "= " + value + ", ";
					}
				}else if (values[i].getClass().getName().endsWith("Boolean")) {
					baseQry += fieldNames[i] + "= " + db.booleanValueSQL((Boolean)values[i])  + ", ";
				} else if (values[i] != null) {
					baseQry += fieldNames[i] + "=" + values[i] + ", ";
				}
			} else {
				baseQry += fieldNames[i] + "=null, ";
			}
		}

		baseQry = baseQry.substring(0, baseQry.length() - 2);
		baseQry += " where " + keyField + "  = " + id;

		return baseQry;
	}
    
	protected String deleteSql(String tableName, String keyField, long id)
		throws Exception {
		String baseQry =
			"Delete from " + tableName + " where " + keyField + " = " + id;
		return baseQry;
	}

	protected String deactivateSql(String tableName, String keyField, long id)
		throws Exception {
		throw new Exception("This system does not deactivate data");
		//  	String baseQry="Update " + tableName + " set active = false where  keyField = " + id;
		//  	return baseQry;
	}

	public String lst() {
		String results = "\n";
		for (int i = 0; i < values.length; i++) {
			String value = "" + values[i];
			if (values[i] != null) {
				if (isDateValue(values[i])){
					value = db.dateStr((java.util.GregorianCalendar) values[i]);
				}
			}
			results += carpus.util.Formatting.textSizer(fieldNames[i], 30)
				+ ":"
				+ value
				+ "\n";
		}
		return results;
	}
	public boolean isDateValue(Object obj){
		if(obj.getClass().getName().endsWith("GregorianCalendar"))
			return true;
		if(obj.getClass().getName().endsWith("Date"))
			return true;			
		return false;
	}
	public String getDBDateStr(Object obj){
		if(obj.getClass().getName().endsWith("GregorianCalendar"))
			return db.dateStr((java.util.GregorianCalendar) obj);
//		if(obj.getClass().getName().endsWith("Date"))
//			return db.dateStr((java.util.Date) obj);
			
		return null;
	}
	public static int typeIDFromClassName(String className){
		if(className.endsWith("HISTORYITEMS")) return TYPE_HISTORYITEMS;
		else if(className.endsWith("INVOICEENTRIES")) return TYPE_INVOICEENTRIES;
		else if(className.endsWith("INVOICE")) return TYPE_INVOICE;
		
		return TYPE_UNKNOWN;
	}
}
