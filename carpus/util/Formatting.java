package carpus.util;


public class Formatting{
    public static final int ALIGN_LEFT   = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT  = 2;
    public final static int FORMAT_FINANCIAL=0;
    public final static int FORMAT_PERCENT=1;
    public final static int FORMAT_TEXT=2;
    public final static int FORMAT_NUMBER=3;
    public final static int NA = -99999;

    public static String financial(double number, int minSize){
	String results="";
	java.text.NumberFormat formatter= java.text.NumberFormat.getInstance() ;
  	formatter.setMaximumFractionDigits(2);
  	formatter.setMinimumFractionDigits(2);
	results = formatter.format(number);
	return "$" + textSizer(results, minSize, ALIGN_RIGHT ) ;
    }

    public static String financial(double number){
	return "$" + twoPlaces(number);
    }
    public static String percent(double number){
	return "%" + textSizer(twoPlaces(number), 6, ALIGN_RIGHT);
    }

    public static String twoPlaces(double number){
	String results="";
	java.text.NumberFormat formatter= java.text.NumberFormat.getInstance() ;
  	formatter.setMaximumFractionDigits(2);
  	formatter.setMinimumFractionDigits(2);
	results = formatter.format(number);
	return results;
    }
    public static String replicate(String text, int size){
	String results="";
	while (results.length() < size){
	    results += text;
	}
	return results;
    }
    public static String textSizer(String text, int size, int alignment, String fill){
	String results="";
	text = text + "";
	if (size < text.length())
	    if (alignment == ALIGN_RIGHT)
		return "*" + text.substring(0,size-1);
	    else
		return text.substring(0,size-1) + "*";

	if (size == text.length()) return text;
	if (alignment == ALIGN_RIGHT){
	    while (results.length() + text.length() < size)
		results += fill;
	    results += text;
	} else if (alignment == ALIGN_LEFT){
	    results = text;
	    for (int indx=text.length(); indx < size; indx++)
		results += fill;
	} else if (alignment == ALIGN_CENTER){
	    while (results.length()*2 + text.length() < size)
		results += fill;
	    results += text;
	    for (int indx=results.length(); indx < size; indx++)
		results += fill;
	}
	return results;
    }
    public static String textSizer(String text, int size, int alignment){
	return textSizer(text, size, alignment, " ");
    }
    public static String textSizer(String text, int size){
	return textSizer(text, size, ALIGN_LEFT);
    }

    public static String tabledData(String rowHeadings[], String columnHeadings[], double data[][], 
				    int dataFormats[], int columnWidths[], boolean totals) {
	String results="";
	final String spacer="  ";
	//  	int columnWidths[] = new int[columnHeadings.length+1];
	int column;
	int row;
	double colTotals[] = new double[columnHeadings.length+1];

  	for( column=0; column < columnHeadings.length; column++){
  	    results += textSizer(columnHeadings[column], columnWidths[column],  ALIGN_CENTER) + spacer;
  	}
	results += "\n";
	for( column=0; column < columnHeadings.length; column++){
	    results += replicate("_", columnWidths[column]) + spacer;
	}
	results += "\n";

	for( row=0; row < rowHeadings.length-1; row++){
	    results += textSizer(rowHeadings[row], columnWidths[0],  ALIGN_LEFT) + spacer;
	    for( column=0; column < columnHeadings.length-1; column++){
		double value = data[row][column];
		if (value == NA){
		    results += textSizer("NA", columnWidths[column+1]);
		} else {
		    switch (dataFormats[column+1]){
		    case FORMAT_FINANCIAL:
			results += textSizer(financial(value), columnWidths[column+1], ALIGN_RIGHT);
			break;
		    case FORMAT_NUMBER:
			results += textSizer(""+value, columnWidths[column+1],  ALIGN_RIGHT);
			break;
		    case FORMAT_PERCENT:
			results += textSizer(percent(value), columnWidths[column+1],  ALIGN_RIGHT);
			break;
			//    		case FORMAT_TEXT:
			//    		    results += formatter.textSizer(data[row][column], columnWidths[column]);
			//    		    break;
		    }
		    if (totals && dataFormats[column+1] != FORMAT_TEXT)
			colTotals[column] += data[row][column];
		}  
		results += spacer;
  	    }
	    results += "\n";
	}
	if (totals){
	    for( column=0; column < columnHeadings.length; column++){
		results += replicate("=", columnWidths[column]) + spacer;
	    }
	    results += "\n";
	    results += textSizer("TOTAL", columnWidths[0],  ALIGN_LEFT) + spacer;
	    for( column=0; column < columnHeadings.length-1; column++){
  		switch (dataFormats[column+1]){
  		case FORMAT_FINANCIAL:
    		    results += financial(colTotals[column], columnWidths[column+1]-1);
  		    break;
		case FORMAT_NUMBER:
		    results += textSizer(""+colTotals[column], columnWidths[column+1],  ALIGN_RIGHT);
		    break;
  		case FORMAT_PERCENT:
  		    results += textSizer("", columnWidths[column+1], ALIGN_RIGHT);
  		    break;
  		case FORMAT_TEXT:
  		    results += textSizer(" ", columnWidths[column+1]);
  		    break;
  		}
		results += spacer;
	    }
	}		
	return results;
    }
    public static String getCleanedTextData(String original){
	if(original == null) return null;

	String results="";
	results = original.replace('\r', ' ');
	results = results.replace('\f', ' ');
	results = results.replace('\n', ' ');
	results = results.replace('\t', ' ');
	results = results.replace('\'', '^');
	//  	results = results.replace('\"', '^');
	return results;
    }

    public static void main(String args[])
	throws Exception
    {
	System.out.println(textSizer("Labor", 12,  ALIGN_CENTER) + " ");
    }


}
