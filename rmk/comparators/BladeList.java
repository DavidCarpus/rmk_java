package rmk.comparators;

import java.util.Comparator;

import rmk.database.dbobjects.Invoice;
import java.text.SimpleDateFormat;

public class BladeList  
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

    boolean inv1Dealer = sys.invoiceInfo.isDealerInvoice(inv1);
    boolean inv2Dealer = sys.invoiceInfo.isDealerInvoice(inv2);
    inv1.setDealer(inv1Dealer);
    inv2.setDealer(inv2Dealer);
    if(inv1Dealer && !inv2Dealer) return 1;
    if(!inv1Dealer && inv2Dealer) return -1;
    //=====
    //At this point, inv1 && inv2 are BOTH dealers or BOTH NOT
    //=====
    // sort by customerid next? , Only if dealer?
    if(inv1Dealer){
    	diff = (int)(inv1.getCustomerID() - inv2.getCustomerID());
    	if(diff != 0) return diff;
    }
    // by invoice #
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

