package carpus.util;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;

public class CodeUtils{
    public String readScheme(BufferedReader in) throws Exception{
	String results="";
	String currString;
	while((currString = in.readLine())!=null){
	    results += currString.trim() + "\n";
	}
	return results;
    }
    
    public Vector getSetValuesRSFunction(Vector scheme) throws Exception{
	Vector results = new Vector();
	String line="";
	String field="";
	String type="";
	String function="";
	int i=0;
	for(Enumeration e = scheme.elements(); e.hasMoreElements();){
	    line = (String)e.nextElement();
	    int index=line.indexOf(":");	    
	    field = line.substring(0,index);
	    type = line.substring(index+1).trim();
	    function="set";
	    function += field + "(";
	    if(getJavaObjectFieldType(type).equals("String")){
		function += "recordSet.getString(\"" + field + "\"));";
	    }else if(getJavaObjectFieldType(type).equals("Boolean")){
		function += "recordSet.getBoolean(\"" + field + "\"));";
	    } else if(getJavaObjectFieldType(type).equals("java.util.GregorianCalendar")){
//  	setCompletionDate( recordSet.getDate("completionDate"));	    
		function += "carpus.util.DateFunctions.gregorianFromString(";
		function += "recordSet.getString(\"";
		function += field + "\")));";
	    }else if (getJavaFieldType(type).equals("long")){
		function += "recordSet.getInt(\"" + field + "\"));";
	    }else if (getJavaFieldType(type).equals("double")){
		function += "recordSet.getFloat(\"" + field + "\"));";
	    }else if (getJavaFieldType(type).equals("int")){
		function += "recordSet.getInt(" + field + "));";
	    } else{
		function += "new ";
		function += getJavaObjectFieldType(type) + "((String)data[i++]));";
	    }
	    results.addElement(function);

	}
	return results;
    }

    public Vector getSetValuesFunction(Vector scheme) throws Exception{
	Vector results = new Vector();
	String line="";
	String field="";
	String type="";
	String function="";
	int i=0;
	for(Enumeration e = scheme.elements(); e.hasMoreElements();){
	    line = (String)e.nextElement();
	    int index=line.indexOf(":");	    
	    field = line.substring(0,index);
	    type = line.substring(index+1).trim();
	    function="set";
	    function += field + "(";
	    if(getJavaObjectFieldType(type).equals("String")){
	       function += "(String)data[i++]);";
	    }else if(getJavaObjectFieldType(type).equals("Boolean")){
	       function += "(!((String)data[i++]).equals(\"0\")));";
	    } else if(getJavaObjectFieldType(type).equals("java.util.GregorianCalendar")){
		function += "carpus.util.DateFunctions.gregorianFromString((String)data[i++]));";
	    }else if (getJavaFieldType(type).equals("long")){
		function += getJavaObjectFieldType(type) + ".parseLong((String)data[i++]));";
	    }else if (getJavaFieldType(type).equals("double")){
		function += getJavaObjectFieldType(type) + ".parseDouble((String)data[i++]));";
	    }else if (getJavaFieldType(type).equals("int")){
		function += getJavaObjectFieldType(type) + ".parseInt((String)data[i++]));";
	    } else{
		function += "new ";
		function += getJavaObjectFieldType(type) + "((String)data[i++]));";
	    }
	    results.addElement(function);

	}
	return results;
    }

    public Vector getSetFunctions(Vector scheme) throws Exception{
	Vector results = new Vector();
	String line="";
	String field="";
	String type="";
	String function="";
	int i=0;
	for(Enumeration e = scheme.elements(); e.hasMoreElements();){
	    line = (String)e.nextElement();
	    int index=line.indexOf(":");	    
	    field = line.substring(0,index);
	    type = line.substring(index+1).trim();
	    function="public void set";
	    function += field + "(";
	    function += getJavaFieldType(type) + " value){values[" + i++ + "]";
	    if (getJavaObjectFieldType(type).equals("java.util.GregorianCalendar")){
		function += " = value";
	    }else if (getJavaObjectFieldType(type).equals("String")){
		function += " = value";
	    }else{
		function += " = new " + getJavaObjectFieldType(type) + "(value)";
	    }

	    function += ";edited=true;}";
	    results.addElement(function);

	}
	return results;
    }

    public Vector getGetFunctions(Vector scheme) throws Exception{
	Vector results = new Vector();
	String line="";
	String field="";
	String type="";
	String function="";
	int i=0;
	for(Enumeration e = scheme.elements(); e.hasMoreElements();){
	    line = (String)e.nextElement();
	    int index=line.indexOf(":");	    
	    field = line.substring(0,index);
	    type = line.substring(index+1).trim();
	    if(type.equals("BOOLEAN"))
		function="public " + getJavaFieldType(type) + " is";
	    else
		function="public " + getJavaFieldType(type) + " get";

	    function += field + "(){ return ";

	    if (!getJavaFieldType(type).equals("String") && !type.equals("DATE")){
		function += "((" + getJavaObjectFieldType(type) + ")values[" + i++ + "])";
		function += "." + getJavaFieldType(type) + "Value();}";
	    }else{
		function += "(" + getJavaObjectFieldType(type) + ")values[" + i++ + "];}";
	    }
	    results.addElement(function);

	}
	return results;
    }



    public Vector getTableScheme(String scheme, String table){
	Vector results = new Vector();
	table = table.toUpperCase();
	String tmpStr=scheme.toUpperCase();
	while(! tmpStr.startsWith(table)){
	    tmpStr = tmpStr.substring(tmpStr.indexOf("CREATE TABLE")+12);
	    tmpStr = tmpStr.trim();
	}	
	tmpStr = tmpStr.substring(tmpStr.indexOf("("), tmpStr.indexOf(";"));
	String tableScheme = scheme.toUpperCase();
	if(tableScheme.indexOf(";") > 0)
	    tableScheme = scheme.substring(tableScheme.indexOf(tmpStr));
	if(tableScheme.indexOf(";") > 0)
	    tableScheme = tableScheme.substring(0,tableScheme.indexOf(";"));
	tableScheme = tableScheme.substring(tableScheme.indexOf("(")+1
					    , tableScheme.lastIndexOf(")"));
	tableScheme=tableScheme.trim();
	String line="";
	String field="";
	String type="";
	while(tableScheme.length() > 2){
	    if(tableScheme.indexOf(",") >0)
		line = tableScheme.substring(0,tableScheme.indexOf(",")+1);
	    else
		line = tableScheme;
	    tableScheme = tableScheme.substring(line.length());
	    line = line.trim();
	    int index1=line.indexOf(" ");
	    int index2=line.indexOf("\t");
	    int index=0;
	    if(index1>0 && index1<line.length()) 
		index=index1;
	    else if(index2>0 && index1<line.length()) 
		index=index2;
	    if(index2>0 && index2<index)
		index=index2;
	    field = line.substring(0,index);
	    type = line.substring(index).trim();
	    if(type.indexOf(" ") > 0)  type = type.substring(0,type.indexOf(" "));
	    if(type.indexOf("\t") > 0) type = type.substring(0,type.indexOf("\t"));
	    if(type.indexOf(",") > 0)  type = type.substring(0,type.indexOf(","));
	    results.addElement(field + ":" + type);
	}
	return results;
    }

    public Vector getFieldList(Vector tableScheme){
	Vector results=new Vector();
	String line="";
	String field="";
	String type="";
	for(Enumeration e = tableScheme.elements(); e.hasMoreElements();){
	    line = (String)e.nextElement();
	    int index=line.indexOf(":");	    
	    field = line.substring(0,index);
	    type = line.substring(index+1).trim();
	    results.addElement(field);
	}
	return results;
    }
    public int getSizeFromType(String type) throws Exception{
	type = type.toUpperCase();
	if(type.equals("SERIAL")) return carpus.database.Fixed.LONG_SIZE;
	if(type.equals("FLOAT")) return carpus.database.Fixed.FLOAT_SIZE;
	if(type.equals("BIGINT")) return carpus.database.Fixed.LONG_SIZE;
	if(type.equals("INTEGER")) return carpus.database.Fixed.INT_SIZE;
	if(type.equals("TEXT")) return carpus.database.Fixed.MEMO_SIZE;
	if(type.equals("BOOLEAN")) return carpus.database.Fixed.BOOLEAN_SIZE;
	if(type.equals("DATE")) return carpus.database.Fixed.DATE_SIZE;
	if(type.startsWith("VARCHAR")){
	    type = type.substring(8,type.indexOf(")"));
	    return (int)Integer.parseInt(type);
	}
	throw new Exception("Unknown Data Type size:" + type);
    }
    public String getJavaFieldType(String type) throws Exception{
	type = type.toUpperCase();
	if(type.equals("SERIAL")) return "long";
	if(type.equals("FLOAT")) return "double";
	if(type.equals("BIGINT")) return "long";
	if(type.equals("INTEGER")) return "int";
	if(type.equals("TEXT")) return "String";
	if(type.equals("BOOLEAN")) return "boolean";
	if(type.equals("DATE")) return "java.util.GregorianCalendar";
	if(type.startsWith("VARCHAR")) return "String";

	throw new Exception("Unknown Data Type size:" + type);
    }

    public String getJavaObjectFieldType(String type) throws Exception{
	type = getJavaFieldType(type);
	if(type.equals("int")) type = "Integer";
	if(type.equals("java.util.GregorianCalendar")) return type;

	return (""+type.charAt(0)).toUpperCase() + type.substring(1);
    }

    public Vector getFieldSizes(Vector tableScheme) throws Exception{
	Vector results=new Vector();
	String line="";
	String field="";
	String type="";
	for(Enumeration e = tableScheme.elements(); e.hasMoreElements();){
	    line = (String)e.nextElement();
	    int index=line.indexOf(":");	    
	    field = line.substring(0,index);
	    type = line.substring(index+1).trim();
	    if(type.indexOf(" ") > 0)  type = type.substring(0,type.indexOf(" "));
	    if(type.indexOf("\t") > 0) type = type.substring(0,type.indexOf("\t"));
	    if(type.indexOf(",") > 0)  type = type.substring(0,type.indexOf(","));
	    results.addElement(new Integer(getSizeFromType(type)));
	}
	return results;
    }
    public Vector getJavaFieldTypes(Vector tableScheme) throws Exception{
	Vector results=new Vector();
	String line="";
	String field="";
	String type="";
	for(Enumeration e = tableScheme.elements(); e.hasMoreElements();){
	    line = (String)e.nextElement();
	    int index=line.indexOf(":");	    
	    field = line.substring(0,index);
	    type = line.substring(index+1).trim();
	    if(type.indexOf(" ") > 0)  type = type.substring(0,type.indexOf(" "));
	    if(type.indexOf("\t") > 0) type = type.substring(0,type.indexOf("\t"));
	    if(type.indexOf(",") > 0)  type = type.substring(0,type.indexOf(","));
	    results.addElement(getJavaFieldType(type));
	}
	return results;
    }

    public static void main(String args[]) throws Exception{
 	String fileName="/home/carpus/rmk/databaseSchemePostgreSQL.sql";
//  	String fileName="e:/code/java/rmk/databaseSchemePostgreSQL.sql";

	String currString="";
	BufferedReader
		in = new BufferedReader(new FileReader(new File(fileName)));
//	DataInputStream in = 
//	    new DataInputStream(new BufferedInputStream(
//							new FileInputStream(fileName)));
							
	CodeUtils util=new CodeUtils();
	Vector results;
	String allScheme=util.readScheme(in);
	Vector tableScheme = util.getTableScheme(allScheme, "invoiceentryadditions");
//    	System.out.println(util.getFieldList(tableScheme));
//    	System.out.println(util.getFieldSizes(tableScheme));
//    	System.out.println(util.getJavaFieldTypes(tableScheme));



	results = util.getFieldList(tableScheme);
	for(Enumeration e = results.elements(); e.hasMoreElements();){
	    String line = (String)e.nextElement();
	    System.out.print("\"" + line + "\",");	    
	}
	System.out.println();
	System.out.println();

    	results = util.getSetFunctions(tableScheme);
	for(Enumeration e = results.elements(); e.hasMoreElements();){
	    String line = (String)e.nextElement();
	    System.out.println(line);
	}
	System.out.println();
	System.out.println();

	
    	results = util.getGetFunctions(tableScheme);
	for(Enumeration e = results.elements(); e.hasMoreElements();){
	    String line = (String)e.nextElement();
	    System.out.println(line);
	}

  	System.out.println();
  	System.out.println();
	
  	results = util.getSetValuesFunction(tableScheme);
	for(Enumeration e = results.elements(); e.hasMoreElements();){
	    String line = (String)e.nextElement();
	    System.out.println(line);
	}

  	System.out.println();
  	System.out.println();
	
  	results = util.getSetValuesRSFunction(tableScheme);
	for(Enumeration e = results.elements(); e.hasMoreElements();){
	    String line = (String)e.nextElement();
	    System.out.println(line);
	}
    }


}
