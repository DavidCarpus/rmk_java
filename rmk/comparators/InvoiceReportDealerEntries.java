package rmk.comparators;

import java.util.Comparator;
import rmk.database.dbobjects.InvoiceEntries;

public class InvoiceReportDealerEntries
    implements Comparator{
      
  public int compare(
                  Object o1,Object o2){
    if(!(o1 instanceof rmk.database.dbobjects.InvoiceEntries)) throw new ClassCastException();
    if(!(o2 instanceof rmk.database.dbobjects.InvoiceEntries)) throw new ClassCastException();
    InvoiceEntries entry1 = (InvoiceEntries)o1;
    InvoiceEntries entry2 = (InvoiceEntries)o2;
    int diff=0;
    
    diff = (int)(entry1.getInvoiceEntryID() - entry2.getInvoiceEntryID());
    return diff;
  }//end compare()

    
  public boolean equals(Object o){
    if(!(o instanceof rmk.comparators.InvoiceReportEntries))
        return false;
    else return true;
  }//end overridden equals()

}//end class InvoiceReportDealerEntries