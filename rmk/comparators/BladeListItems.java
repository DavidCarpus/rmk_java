package rmk.comparators;

import java.util.Comparator;

//import rmk.DataModel;
//import rmk.database.dbobjects.InvoiceEntries;
import java.text.SimpleDateFormat;

import rmk.database.dbobjects.InvoiceEntries;

public class BladeListItems  
    implements Comparator
{

    static rmk.DataModel sys = rmk.DataModel.getInstance();

    static carpus.util.DateFunctions dateFunctions = new carpus.util.DateFunctions();
    static SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yy");
      
  public int compare(
                  Object o1,Object o2){
  	if(!(o1 instanceof InvoiceEntries)) throw new ClassCastException();
  	if(!(o2 instanceof InvoiceEntries)) throw new ClassCastException();
  	InvoiceEntries entry1 = (InvoiceEntries)o1;
  	InvoiceEntries entry2 = (InvoiceEntries)o2;
  	if(entry1.getPartDescription().equalsIgnoreCase("14MINI"))
  		return 1;
  	if(entry2.getPartDescription().equalsIgnoreCase("14MINI"))
  		return -1;
  	InvoiceReportEntries dupeSorter = new InvoiceReportEntries();
  	return dupeSorter.compare(o1, o2);
//    if(!(o1 instanceof InvoiceEntries)) throw new ClassCastException();
//    if(!(o2 instanceof InvoiceEntries)) throw new ClassCastException();
//    InvoiceEntries entry1 = (InvoiceEntries)o1;
//    InvoiceEntries entry2 = (InvoiceEntries)o2;
//    int diff;
// 
////    // order items were entered
////    diff = (int)(entry1.getInvoiceEntryID() - entry2.getInvoiceEntryID());
////    if(diff != 0) return diff;
//    
//    // order items by model number
//    String partCode1=DataModel.getInstance().partInfo.getPartCodeFromID(entry1.getPartID());
//    String partCode2=DataModel.getInstance().partInfo.getPartCodeFromID(entry2.getPartID());
//    
//    diff = partCode1.compareTo(partCode2);
//    if(diff != 0) return diff;
//
//    return 0;
  }//end compare()
    
  public boolean equals(Object o){
    if(!(o instanceof BladeList))
        return false;
    else return true;
  }//end overridden equals()
}//end class TheComparator

