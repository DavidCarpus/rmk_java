/*
 * Created: May 9, 2004
 * By: David Carpus
 * 
 * Last Modified:
 * Last Modified by:
 * 
 */
package rmk.comparators;

import java.util.Comparator;

import rmk.database.dbobjects.Invoice;


/**
 * @author dcarpus
 *
 * 
 */
public class RptTaxShipped implements Comparator {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        if(!(o1 instanceof Invoice)) throw new ClassCastException();
        if(!(o2 instanceof Invoice)) throw new ClassCastException();
        Invoice inv1 = (Invoice)o1;
        Invoice inv2 = (Invoice)o2;
        int diff;

//        if(inv1.getDateShipped().before(inv2.getDateShipped())) return -1;
//        if(inv2.getDateShipped().before(inv1.getDateShipped())) return 1;
     
        // finally, by invoice #
        diff = (int)(inv1.getInvoice() - inv2.getInvoice());
        if(diff != 0) return diff;

        return 0;    }

    public static void main(String[] args) {
    }
}
