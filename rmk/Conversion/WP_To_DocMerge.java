package rmk.Conversion;

import java.io.*;

public class WP_To_DocMerge{
//      DataInputStream in=null;
//      DataOutputStream out=null;
    BufferedReader infile=null;
    BufferedWriter outfile=null;
    
    public WP_To_DocMerge(String srcFile, String destFile) throws Exception{
//  	in = new DataInputStream(new BufferedInputStream(new FileInputStream(srcFile)));
//  	out= new DataOutputStream(new BufferedOutputStream(new FileOutputStream(destFile)));
	infile = new BufferedReader(new FileReader(srcFile));
	outfile = new BufferedWriter(new FileWriter(destFile));
    }
    public void write(String line) throws Exception{
//  	if(line.trim().length() > 1)
  	outfile.write(line);
	outfile.newLine();
//    	System.out.println(this.getClass().getName() + line);
    }
    
    public void closeFiles() throws Exception{
	outfile.flush();
    }

    public String read() throws Exception{
	String line;
	String row="";
	int rowCnt=0;
	while( (line = infile.readLine()) != null){
//  	    if(line.length() <= 1)
//  		return null;
	    row += line.substring(0,line.length()-1);
	    rowCnt++;
	    if((int)line.charAt(line.length()-1) != 18){
		System.out.println(this.getClass().getName() + ":"+ rowCnt);
		if(row.length() <= 1)
		    return null;
		return row.substring(0,row.length()-1);
	    } else {
		row += "|";
	    }
	}
	return null;
    }


    public static void main(String args[]) throws Exception{
	String in = "h:/RMKMerge.DAT";
	String out = "h:/MyFiles/out.txt";
	if(! carpus.util.SystemPrefrences.runningOnWindows()){
	    in = "/home/carpus/RMKMerge.DAT";
	    out = "/home/carpus/out.txt";
	}
	
	if(args.length > 0)
	    in = args[0];
	if(args.length > 1)
	    out = args[1];

	WP_To_DocMerge merge= new WP_To_DocMerge(in, out);
	String line = "Invoice|Ordered|Estimated|Shipped||Phone|Address1|Address2|Address3|City|State|Zip||";
	line +=  "Address4|Address5|Address6|City|State|Zip|||Due|HoldDate";
	merge.write(line);
	while((line = merge.read()) != null){
//  	    System.out.println(line);	    
	    merge.write(line);
	}
	merge.closeFiles();
    }

}
