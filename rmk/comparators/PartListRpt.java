/*
 * Created: Jul 3, 2004
 * By: David Carpus
 * 
 * Last Modified:
 * Last Modified by:
 * 
 */
package rmk.comparators;

import java.util.Comparator;

import rmk.database.dbobjects.Parts;


/**
 * @author dcarpus
 *
 * 
 */
public class PartListRpt implements Comparator {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        if(!(o1 instanceof Parts)) throw new ClassCastException();
        if(!(o2 instanceof Parts)) throw new ClassCastException();
        Parts inv1 = (Parts)o1;
        Parts inv2 = (Parts)o2;
        int diff;


		int partType1 = inv1.getPartType();
		int partType2 = inv2.getPartType();
		if(partType1 == 10 && partType2 != 10)
			return -1;
		if(partType1 != 10 && partType2 == 10)
			return 1;
//        if(!inv1.isBladeItem() && inv2.isBladeItem()) return 1;
//        if(inv1.isBladeItem() && !inv2.isBladeItem()) return -1;
//
        diff = inv1.getPartType() - inv2.getPartType();
        if(diff != 0) return diff;

        diff = inv1.getPartCode().compareTo(inv2.getPartCode());
        return diff;
    }

    public static void main(String[] args) {
    }
}
