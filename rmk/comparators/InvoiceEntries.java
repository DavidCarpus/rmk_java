package rmk.comparators;

import java.util.Comparator;

import java.text.SimpleDateFormat;

public class InvoiceEntries
    implements Comparator{
    static carpus.util.DateFunctions dateFunctions = new carpus.util.DateFunctions();
    static SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yy");
      
  public int compare(
                  Object o1,Object o2){
    if(!(o1 instanceof rmk.database.dbobjects.InvoiceEntries)) throw new ClassCastException();
    if(!(o2 instanceof rmk.database.dbobjects.InvoiceEntries)) throw new ClassCastException();
    rmk.database.dbobjects.InvoiceEntries entry1 = (rmk.database.dbobjects.InvoiceEntries)o1;
    rmk.database.dbobjects.InvoiceEntries entry2 = (rmk.database.dbobjects.InvoiceEntries)o2;
    int diff;

    diff = (int)(entry1.getPartType() - entry2.getPartType());
    if(diff != 0) return diff;

    diff = (int)(entry1.getInvoiceEntryID() - entry2.getInvoiceEntryID());
    return diff;
  }//end compare()
    
  public boolean equals(Object o){
    if(!(o instanceof rmk.database.dbobjects.InvoiceEntries))
        return false;
    else return true;
  }//end overridden equals()
}//end class TheComparator

