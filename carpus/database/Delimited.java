package carpus.database;

import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;

public class Delimited{
    Vector lst;
    StringTokenizer st;

    public Delimited(){
    }

    public Object[] getArray(String data, String delimiter){
	String part="";
	String part2="";
	lst = new Vector();	
	
	while (data.length() > 0) {
	    if(data.indexOf(delimiter) >= 0){
		part = data.substring(0, data.indexOf(delimiter));
		data = data.substring(data.indexOf(delimiter)+1);
	    }else{
		part = data;
		data = "";
	    }

	    if(part.startsWith("\"")){
		part2 = "";
		while (data.length() > 0 && data.indexOf("\"") > 0) {
		    part2 = data.substring(0, data.indexOf("\""));
		    data = data.substring(data.indexOf("\"")+1);
		    part += part2;
		}
		System.out.println("Delimited:P2:" + part2);
	    }
	    System.out.println("Delimited:|" + part + "|");

	    if(part.startsWith("\"")) part=part.substring(1);
	    if(part.endsWith("\"")) part=part.substring(0,part.lastIndexOf("\""));
	    lst.addElement(part);
	} 
	return lst.toArray();
    }

    public static void main(String args[]) throws Exception{
	String fileName="/home/carpus/rmk/New/txt/Customers.txt";

	String currString="";
	BufferedReader
		in = new BufferedReader(new FileReader(new File(fileName)));
//	DataInputStream in = 
//	    new DataInputStream(new BufferedInputStream(
//							new FileInputStream(fileName)));
	int row=0;
	Delimited delimited = new Delimited();
	Object[] lst;
	while((currString = in.readLine())!=null && row < 10){
	    if(row == 1){
		lst = delimited.getArray(currString, "\t");
		System.out.println("Delimited:"+currString);
		for(int i=0; i< lst.length; i++)
		    System.out.println((i+1) + "|" + lst[i] + "|");
		
		System.out.println(lst.length);		
	    }
	    row++;
	}
	System.out.println(row);
	

    }

}
