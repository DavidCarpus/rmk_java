package rmk.comparators;

import java.util.Comparator;

import rmk.database.dbobjects.Invoice;
import java.text.SimpleDateFormat;

public class RptTaxOrdered
    implements Comparator
{

    static rmk.DataModel sys = rmk.DataModel.getInstance();

    static carpus.util.DateFunctions dateFunctions = new carpus.util.DateFunctions();
    static SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yy");
      
  public int compare(
                  Object o1,Object o2){
    if(!(o1 instanceof Invoice)) throw new ClassCastException();
    if(!(o2 instanceof Invoice)) throw new ClassCastException();
    Invoice inv1 = (Invoice)o1;
    Invoice inv2 = (Invoice)o2;
    int diff;

    if(inv1.getDateOrdered().before(inv2.getDateOrdered())) return -1;
    if(inv2.getDateOrdered().before(inv1.getDateOrdered())) return 1;
 
    // finally, by invoice #
    diff = (int)(inv1.getInvoice() - inv2.getInvoice());
    if(diff != 0) return diff;

    return 0;
  }//end compare()
    
  public boolean equals(Object o){
    if(!(o instanceof BladeList))
        return false;
    else return true;
  }//end overridden equals()
}//end class TheComparator

