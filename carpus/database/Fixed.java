package carpus.database;

import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;

public class Fixed{
    Vector lst;
    StringTokenizer st;
    public static final int INT_SIZE = 6;
    public static final int LONG_SIZE = 11;
    public static final int DATE_SIZE=19;
    public static final int FLOAT_SIZE=22;
    public static final int CURRENCY_SIZE=21;
    public static final int BOOLEAN_SIZE=2;
    public static final int MEMO_SIZE=512;

    public Fixed(){
    }

    public Object[] getArray(String data, int[] lengths){
	String part="";
	lst = new Vector();
	int i=0;
	int ct=0;
	try{
	    for(i=0; i< lengths.length; i++){
		part = data.substring(0,lengths[i]);
		part = part.trim();
		if(part.startsWith("$") && lengths[i] == CURRENCY_SIZE)
		    part = part.substring(1);
		if(part.startsWith("($") && lengths[i] == CURRENCY_SIZE)
		    part = "-" + part.substring(2, part.trim().length()-1);

		data = data.substring(lengths[i]);
		lst.addElement(part);
	    }
	} catch (Exception e){
	    System.out.println("********************");
	    System.out.println(i);	    
	    System.out.println(e);
	    System.out.println("********************");	    
	}
	return lst.toArray();
    }
    public String list(Object[] lst){
	String part="";
	String results="";
	for(int i=0; i< lst.length; i++){
	    part = (String)lst[i];
// 	    if(i!=7)
	    results += (i+1) + "|" + part + "|\n";
	}
	return results;
    }

    public Object[] getArrayUntrimed(String data, int[] lengths){
	String part="";
	lst = new Vector();

	int ct=0;
	for(int i=0; i< lengths.length; i++){
	    part = data.substring(0,lengths[i]);
// 	    part = part.trim();
	    data = data.substring(lengths[i]);
	    lst.addElement(part);
	}
	return lst.toArray();
    }

   public static void main(String args[]) throws Exception{
  	int[] lengths=rmk.database.dbobjects.Customer.lengths;
	String fileName=Configuration.Config.getDataFileLocation("Customers");
//  	int[] lengths={Fixed.LONG_SIZE, Fixed.LONG_SIZE, Fixed.LONG_SIZE, Fixed.CURRENCY_SIZE};
	rmk.database.dbobjects.Customer item;

	String currString="";
	BufferedInputStream in = (new BufferedInputStream(new FileInputStream(fileName)));
	int row=0;
	Fixed fixed = new Fixed();
	int ct=0;
	for(int i=0; i< lengths.length; i++){
//  	    System.out.println(i + ":" + lengths[i]);	    
	    ct += lengths[i];
	}
	ct = rmk.database.dbobjects.Customer.getTotalFieldLengths_txt();
  	byte[] currInput = new byte[ct+2]; // CR-LF
  	Object[] lst;
  	while( in.read(currInput)!= -1 && row <= 1010){
//    	while( in.read(currInput)!= -1 && row <= 75100){
//  	    if(row == 40){
  		currString = new String(currInput);
 
//      		System.out.println(currString.length());
  		lst = fixed.getArray(currString,lengths);
//      		lst = fixed.getArrayUntrimed(currString,lengths);
//        		System.out.println(fixed.list(lst));
    		item = new rmk.database.dbobjects.Customer(lst);
//  //  		System.out.println( item.getID().longValue());
		
//  //  		if(item.getPrice() == 0 && 
		if(item.getID().longValue() == 1005){
//  		    System.out.println(item + ":" + item.getPrice());
		    System.out.println( currString);
		    
		    lst = fixed.getArrayUntrimed(currString,lengths);
		    System.out.println(fixed.list(lst));
		}
  	    row++;
  	}
    System.out.println("Rows:"+ row);
   }
    
}
