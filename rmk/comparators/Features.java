package rmk.comparators;

import java.util.Comparator;

import rmk.database.dbobjects.InvoiceEntryAdditions;
import java.text.SimpleDateFormat;

public class Features
    implements Comparator
{

    static rmk.DataModel sys = rmk.DataModel.getInstance();

    static carpus.util.DateFunctions dateFunctions = new carpus.util.DateFunctions();
    static SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yy");
      
  public int compare(
                  Object o1,Object o2){
    if(!(o1 instanceof InvoiceEntryAdditions)) throw new ClassCastException();
    if(!(o2 instanceof InvoiceEntryAdditions)) throw new ClassCastException();
    InvoiceEntryAdditions item1 = (InvoiceEntryAdditions)o1;
    InvoiceEntryAdditions item2 = (InvoiceEntryAdditions)o2;
    int diff;
    int type1=sys.partInfo.getPartTypeFromID((int)item1.getPartID());
    int type2=sys.partInfo.getPartTypeFromID((int)item2.getPartID());

    diff = (int)(type1-type2);

    return diff;
  }//end compare()
    
  public boolean equals(Object o){
    if(!(o instanceof BladeList))
        return false;
    else return true;
  }//end overridden equals()
}//end class TheComparator

