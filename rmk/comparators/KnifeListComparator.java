package rmk.comparators;

import java.util.Comparator;

import rmk.database.dbobjects.Parts;
import java.text.SimpleDateFormat;

public class KnifeListComparator  
    implements Comparator{
    static carpus.util.DateFunctions dateFunctions = new carpus.util.DateFunctions();
    static SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yy");
      
  public int compare(
                  Object o1,Object o2){
    if(!(o1 instanceof Parts)) throw new ClassCastException();
    if(!(o2 instanceof Parts)) throw new ClassCastException();
    Parts inv1 = (Parts)o1;
    Parts inv2 = (Parts)o2;
    int diff;
    diff = inv1.getPartType() - inv2.getPartType();
    if(diff != 0) return diff;

    diff = inv1.getPartCode().compareTo(inv2.getPartCode());
    return diff;
  }//end compare()
    
  public boolean equals(Object o){
    if(!(o instanceof KnifeListComparator))
        return false;
    else return true;
  }//end overridden equals()
}//end class TheComparator

