package rmk.gui.ScreenComponents;
//  import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import java.text.NumberFormat;

//===============================================================
//===============================================================
class ListObject{
    InvoiceEntryAdditions addition=null;
    int listIndex;
    static final NumberFormat currencyFormatter    = NumberFormat.getCurrencyInstance();
    static rmk.DataModel sys = rmk.DataModel.getInstance();

    ListObject(InvoiceEntryAdditions  addition, int listIndex){
	this.addition = addition;
	this.listIndex = listIndex;
    }

    public String toString(){
	String results="";
	if(addition == null) return results;
	results += sys.partInfo.getPartCodeFromID(addition.getPartID());
	while(results.length() < 6)
	    results += " ";
	results += "  ";
	results += currencyFormatter.format(addition.getPrice());
//  	results = addition.getDescription();
//    	results = addition.getPartCode();
//  	results = sys.getPartDescFromID(addition.getPartID()) + "   " + currencyFormatter.format(addition.getPrice());

	return results;
    }
    public int getID(){
	return (int)addition.getPartID();
    }
    public int getIndex(){return listIndex;}
    public InvoiceEntryAdditions  getAddition(){return addition;}
}
