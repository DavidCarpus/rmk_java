package rmk.comparators;

import java.util.Comparator;

import rmk.database.dbobjects.Invoice;
import java.text.SimpleDateFormat;

import carpus.util.DateFunctions;

public class InvoiceComparator  
    implements Comparator{
    static carpus.util.DateFunctions dateFunctions = new carpus.util.DateFunctions();
    static SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yy");
      
  public int compare(
                  Object o1,Object o2){
    if(!(o1 instanceof Invoice)) throw new ClassCastException();
    if(!(o2 instanceof Invoice)) throw new ClassCastException();
    Invoice inv1 = (Invoice)o1;
    Invoice inv2 = (Invoice)o2;
    int diff;

    if(inv1.getDateShipped() == null && inv2.getDateShipped() != null ) return 1;
    if(inv1.getDateShipped() != null && inv2.getDateShipped() == null ) return -1;
    diff = DateFunctions.dateDifference( inv1.getDateShipped(), inv2.getDateShipped());
    if(diff != 0) return diff;

    if(inv1.getDateEstimated() == null && inv2.getDateEstimated() != null ) return 1;
    if(inv1.getDateEstimated() != null && inv2.getDateEstimated() == null ) return -1;
    diff = DateFunctions.dateDifference( inv1.getDateEstimated(), inv2.getDateEstimated());

    if(diff != 0) return diff;

    if(inv1.getInvoice() < inv2.getInvoice()) return 1;
    if(inv1.getInvoice() > inv2.getInvoice()) return -1;

    return 0;
  }//end compare()
    
  public boolean equals(Object o){
    if(!(o instanceof InvoiceComparator))
        return false;
    else return true;
  }//end overridden equals()
}//end class TheComparator

