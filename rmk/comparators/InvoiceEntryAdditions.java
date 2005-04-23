package rmk.comparators;

import java.util.Comparator;

public class InvoiceEntryAdditions
    implements Comparator{
      
  public int compare(
                  Object o1,Object o2){
    if(!(o1 instanceof rmk.database.dbobjects.InvoiceEntryAdditions)) throw new ClassCastException();
    if(!(o2 instanceof rmk.database.dbobjects.InvoiceEntryAdditions)) throw new ClassCastException();
    rmk.database.dbobjects.InvoiceEntryAdditions entry1 = (rmk.database.dbobjects.InvoiceEntryAdditions)o1;
    rmk.database.dbobjects.InvoiceEntryAdditions entry2 = (rmk.database.dbobjects.InvoiceEntryAdditions)o2;
    int diff;

    diff = (int)(entry1.getPartType() - entry2.getPartType());
//      ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ diff);
    
    if(diff != 0) return diff;

    diff = (int)(entry1.getAdditionID() - entry2.getAdditionID());
    return diff;
  }//end compare()
    
  public boolean equals(Object o){
    if(!(o instanceof rmk.database.dbobjects.InvoiceEntryAdditions))
        return false;
    else return true;
  }//end overridden equals()
}//end class TheComparator

