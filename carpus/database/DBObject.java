package carpus.database;
import java.sql.ResultSet;

public abstract class DBObject{
//      protected Integer ID;
    protected String shortDescription;
    protected String longDescription;
    protected boolean edited = false;
    protected boolean active = true;
    protected boolean transfering = false;
    protected String[] fieldNames;
    protected Object[] values;

//      public abstract void setFieldNames(String[] fieldnames){fieldNames = fieldnames;}
    protected DBObject(String[] fieldnames){
	fieldNames = fieldnames;
	values = new Object[fieldnames.length];
    }
    public void setField(int index, Object value){values[index] = value;}
    protected DBObject(){};

    public abstract void setValues(Object[]  data);

    public static int getTotalFieldLengths(int[] lengths){
	int ct=0;
	for(int i=0; i< lengths.length; i++){ct += lengths[i];}
	return 	ct;    
    }
    public Object getField(int index){
	if(index <= values.length) return values[index];
	else return null;
    }
    public Object getField(String fieldName){
	String field = fieldName.toUpperCase();
	for(int i=0; i<values.length; i++){
	    if(field.equals(fieldNames[i])) return values[i];
	}
	return null;
    }

    public void    markSaved(){ edited = false;}
    public boolean isEdited(){return edited;}
    public boolean isNew(){ return getID().intValue() <= 0;  }
    public boolean isTransfering(){return transfering;}

    public abstract void setValues(ResultSet  recordSet) throws Exception;
    public abstract String getIDSQL() throws Exception;
    public abstract String saveSql(long id) throws Exception;
    public abstract String updateSql(long id) throws Exception;
    public abstract String deleteSql(long id) throws Exception;
    public abstract String deactivateSql(long id) throws Exception;

    public void    setID(Integer id){values[0] = id;}
    public void    setID(Long id){values[0] = id;}
    public void    setID(int id){values[0] = new Integer(id);}
    public void    setID(long id){values[0] = new Long(id);}
    public void    setShortDescription(String value){shortDescription = value; edited = true;}
    public void    setLongDescription(String value){longDescription = value; edited = true;}
    public void    setActive(boolean state){active = state;}
    public void    markTransfering(boolean state){transfering  = state;}

    public Long getID(){ 
	if(values[0].getClass().getName().endsWith("Integer"))
	    return new Long(((Integer)values[0]).intValue());
	else if(values[0].getClass().getName().endsWith("Long"))
	    return (Long)values[0];
	else
	    return null;
    }
    public String  getShortDescription(){ return shortDescription;}
    public String  getLongDescription(){ return longDescription;}
    public boolean getActive(){return active;}

    public String dateSQL( java.util.GregorianCalendar value ){
	return (value == null ? "NULL ": "'" + carpus.util.DateFunctions.getSQLDateTimeStr(value) + "'" );
    }
    public String getCleanedTextData(String original){
	if(original == null) return null;

	String results=original;
	results = results.replace('\r', ' ');
	results = results.replace('\f', ' ');
	results = results.replace('\n', ' ');
	results = results.replace('\t', ' ');
//  	results = results.replace('\'', '^');
//  	results = results.replace('\\', ' ');
//  	results = results.replace('\"', '^');

	return results;
    }
}
