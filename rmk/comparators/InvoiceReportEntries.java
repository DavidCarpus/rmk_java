package rmk.comparators;

import java.util.Comparator;
import rmk.database.dbobjects.InvoiceEntries;

import java.text.SimpleDateFormat;

public class InvoiceReportEntries
    implements Comparator{
    static carpus.util.DateFunctions dateFunctions = new carpus.util.DateFunctions();
    static SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yy");
      
  public int compare(
                  Object o1,Object o2){
    if(!(o1 instanceof rmk.database.dbobjects.InvoiceEntries)) throw new ClassCastException();
    if(!(o2 instanceof rmk.database.dbobjects.InvoiceEntries)) throw new ClassCastException();
    InvoiceEntries entry1 = (InvoiceEntries)o1;
    InvoiceEntries entry2 = (InvoiceEntries)o2;
    int diff=0;
    
    diff = getDescDiff(entry1, entry2);
//      if(diff != 0){
//  	System.out.print(entry1.getPartDescription() + ":" + entry2.getPartDescription());
//  	System.out.println("  ***" + diff);
//      }

    if(diff != 0) return diff;

    diff = (int)(entry1.getInvoiceEntryID() - entry2.getInvoiceEntryID());
    return diff;
  }//end compare()

    int getDescDiff(InvoiceEntries entry1, InvoiceEntries entry2){
	String desc1 = entry1.getPartDescription();
	String desc2 = entry2.getPartDescription();
	if(desc1 == null || desc2 == null){
	    return (int)(entry1.getInvoiceEntryID() - entry2.getInvoiceEntryID());
	}

	if(desc2.toUpperCase().indexOf("MINI") > 0)
		return -1;
	
	int index1 = desc1.indexOf("-"); 
	int index2 = desc2.indexOf("-"); 
//  	System.out.println(index1 + ":" + index2);
	if(index1 > 0 && index2 < 0){
	    return -1;
	}
	if(index2 > 0 && index1 < 0){
	    return 1;
	}
	if(index1 < 0 && index2 < 0){ // BOTH are just text
	    return desc1.compareTo(desc2);
	}
	
	int mod1=0;
	int mod2=0;
	try {
		mod1 = Integer.parseInt(desc1.substring(0, index1));
		mod2 = Integer.parseInt(desc2.substring(0, index2));
	} catch (Exception e) {
		if(mod1==0){
			if(mod2 != 0)
				return 1;
			return desc1.compareTo(desc2);
		}
		if(mod2==0){
			if(mod1 != 0)
				return -1;
			return desc1.compareTo(desc2);
		}
	}

	int diff = mod1-mod2;
	if(diff != 0) return diff;
//    	System.out.println(desc1 + "(" + mod1 + "):" + desc2 +"(" + mod2 + ")");

	return (int)(entry1.getInvoiceEntryID() - entry2.getInvoiceEntryID());
    }
    
  public boolean equals(Object o){
    if(!(o instanceof rmk.comparators.InvoiceReportEntries))
        return false;
    else return true;
  }//end overridden equals()

}//end class TheComparator

