package rmk.gui.ScreenComponents;

import java.awt.*;
import java.awt.event.*;
import carpus.gui.*;
import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.InvoiceEntryAdditions;

import java.util.*;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;


public class InvoiceEntriesListPanel 
extends 
carpus.gui.DataListPanel
implements ActionListener, FocusListener{
    Vector invoiceEntriesList;
    boolean expanded = false;
    
//==========================================================
//==========================================================
    public InvoiceEntriesListPanel(){
        dataModel = new InvoiceEntriesListTableModel(invoiceEntriesList);
	addTable(dataModel);
	setColumnFormats();

	TableColumn column = null;
	int colCnt = dataModel.getColumnCount();
	for (int i = 0; i < colCnt; i++) {
	    column = table.getColumnModel().getColumn(i);

	    if(i==colCnt-1){ // Comment
		column.setPreferredWidth(350);
	    }else if(i==0 || i==1){ // Object && ID column -- HIDE THEM
		column.setMaxWidth(0);
		column.setMinWidth(0);
		column.setWidth(0); 
		column.setPreferredWidth(0);
	    }else if(i==2){ // Item #
		column.setMaxWidth(40);
		column.setPreferredWidth(30);
	    }else if(i==3){ // Part Desc , made wider per val req 1/20
		column.setMaxWidth(110);
		column.setPreferredWidth(100);
	    }else if(i==4){ // Quantity
		column.setMaxWidth(30);
		column.setPreferredWidth(30);
	    }else if(i==5){ // Price
		column.setMaxWidth(70);
		column.setPreferredWidth(70);
	    }else if(i==6){ // Feature List
		column.setPreferredWidth(200);
	    } else {
//  		column.setMaxWidth(35);
		column.setPreferredWidth(30);
	    }
	}

	setTableSelectionListeners();

	buttonBar = new carpus.gui.BasicToolBar(null, new String[] {"Edit", "Add", "Remove"}, 
				   new String[] {"EditInvoiceEntry", "AddInvoiceEntry", "RemoveInvoiceEntry"},
				   new String[] {"Edit", "Add", "Remove" });

	buttonBar.getButton(1).setMnemonic(KeyEvent.VK_A);
	buttonBar.getButton(0).setMnemonic(KeyEvent.VK_E); // Details/Edit button
	buttonBar.addActionListener(this);
	buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));
	buttonBar.enableButton(0, false);
	buttonBar.enableButton(1, false);
	buttonBar.enableButton(2, false);
	
	KeyStroke kF2 = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0);
	registerKeyboardAction(this, "F2", kF2,
			JComponent.WHEN_IN_FOCUSED_WINDOW);
	
  	add(buttonBar);
    }
    //----------------------------------------------------------
//    public void focusGained(FocusEvent e) {
//        selectLast();
//        selectLastSelected();
//    }
    //----------------------------------------------------------
    public void doubleClick(){
		actionPerformed(new ActionEvent(this,1,"EditInvoiceEntry"));
		lastSelectedItem = selectedItem;
    }
    public long selectedItem(int row){
    	long val =0;
    	int rows = sorter.getRowCount();
    	int cols = sorter.getColumnCount();
    	if(rows < row ){
    		System.out.println(""+ErrorLogger.getInstance().stkTrace(this.getName()));
    		row = rows -1;
    	}
       	if(rows > row && cols > 1){
    		val = ((Long)sorter.getValueAt(row,1)).longValue();
    		makeVisible(row+1);
    	} else{
    		ErrorLogger.getInstance().logError("Invalid row to select:", new Exception());
    	}
		buttonBar.enableButton(0, true);
		buttonBar.enableButton(2, true);
		return val;
    }

//==========================================================
//==========================================================
    //----------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase();
//    ErrorLogger.getInstance().logDebugCommand(command);
	if(processHotKeyCommands(command) ){
		return;
	}
	ErrorLogger.getInstance().logDebugCommand(command);
	ActionEvent event=null;


	if(command.equals("ADDINVOICEENTRY")){
		parentScreen.buttonPress(ScreenController.BUTTON_ADD, 0);
		return;
	} else if(command.equals("EDITINVOICEENTRY") || command.equals("CTRL_ENTERKEY")){
		parentScreen.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS, (int)selectedItem);
		return;
	} else if(command.equals("REMOVEINVOICEENTRY")){
		parentScreen.buttonPress(ScreenController.BUTTON_REMOVE, (int)selectedItem);
		return;
	} else {
	    System.out.println(this.getClass().getName() + ":Undefined:" + command + "|");
	}
	ErrorLogger.getInstance().TODO();
    }

    //----------------------------------------------------------
    public double getTotalRetail(){
		double total = dataModel.getColTotal(InvoiceEntriesListTableModel.COL_PRICE);
//  	System.out.println(this.getClass().getName() + ":"+ total);	
		return total;
    }
    
    public int getTotalKnives(){
		return (int)dataModel.getColTotal(InvoiceEntriesListTableModel.COL_QUANTITY);
    }

    public void setDataInvItems(Invoice inv, Vector invItems){
		setData(invItems);
		sorter.sortByColumn(1, true);
		
		if (inv.getInvoice() != 0)
			buttonBar.enableButton(1, true);

		buttonBar.enableButton(0, false);
		buttonBar.enableButton(2, false);
   	
    }
    
	//----------------------------------------------------------
    public void setMore(boolean more){
	buttonBar.enableButton(0, more);
    }

//==========================================================
//==========================================================
//==========================================================
//==========================================================
	public void expand(boolean b) {
		expanded = b;	
		sorter.sortByColumn(1, true);
		selectLast();
	}

}


//==========================================================
//==========================================================
class InvoiceEntriesListTableModel extends carpus.gui.DataListPanelTableModel {
    final static int COL_QUANTITY=4;
    final static int COL_PRICE=5;

    InvoiceEntriesListTableModel(Vector lst){
	columnNames= new String[]{"Item", "ID", "Item", "Part", "Qty.",  "Price", "Feature List", "Comment"};
	this.flags = new int[columnNames.length];
	flags[COL_PRICE] |=  DataListPanelTableModel.FLAGS_CURRENCY;
	setValues(lst);
    }
    //----------------------------------------------------------
    public double getTotalRetail(){	return getColTotal(COL_PRICE);}
    public int    getTotalKnives(){	return (int)getColTotal(COL_QUANTITY);}
    //----------------------------------------------------------
    public void setValues(Vector lst){
	if(lst == null || lst.size() == 0){
	    data = new Object[0][columnNames.length];
	    return;
	}
	data = new Object[lst.size()][columnNames.length];
  	rmk.DataModel sys = rmk.DataModel.getInstance();

	Object[] sortedLst = lst.toArray();
	Arrays.sort(sortedLst, new LineItemSorter());
	minID=((InvoiceEntries)lst.get(0)).getInvoiceEntryID();
	maxID=((InvoiceEntries)lst.get(lst.size()-1)).getInvoiceEntryID();
	
	Vector features;
	InvoiceEntries item;
	for(int i=0; i< lst.size(); i++){
	    int colIndex=0;
	    item = (InvoiceEntries)lst.get(i);
	    features = sys.invoiceInfo.getInvoiceEntryAdditions(item.getInvoiceEntryID());
	    String featureList="";
	    if(features != null){
		Object[] entries = features.toArray();
		Arrays.sort(entries, new rmk.comparators.InvoiceEntryAdditions());

		for(int featureInxed=0; featureInxed< entries.length; featureInxed++){
		    InvoiceEntryAdditions feature;
		    feature =(InvoiceEntryAdditions)entries[featureInxed];
		    String code = sys.partInfo.getPartCodeFromID(feature.getPartID());
		    if(feature.getPrice() == 0)
			code = code.toLowerCase();
		    featureList += code;
		    featureList += ", ";
		}
	    }
	    
	    if(featureList.endsWith(", "))
		featureList = featureList.substring(0,featureList.length() -2);
		
		data[i][colIndex++] = item;
		data[i][colIndex++] = new Long(item.getInvoiceEntryID());
	    data[i][colIndex++] = new Integer(i+1); // item #
	    String partDesc="";
	    partDesc = "  "+sys.partInfo.getPartCodeFromID(item.getPartID());
	    data[i][colIndex++] = partDesc;

	    data[i][colIndex++] = new Integer(item.getQuantity());
	    data[i][colIndex++] = new Double(item.getPrice());
	    data[i][colIndex++] = "   " + featureList;
	    data[i][colIndex++] = item.getComment() !=null?" "+item.getComment():"";
	}
    }
}
//==========================================================
//==========================================================


//==========================================================
//==========================================================
class LineItemSorter implements Comparator{
  public int compare(
                  Object o1,Object o2){
    if(!(o1 instanceof InvoiceEntries)) throw new ClassCastException();
    if(!(o2 instanceof InvoiceEntries)) throw new ClassCastException();

    if(((InvoiceEntries)o1).getInvoiceEntryID() < ((InvoiceEntries)o2).getInvoiceEntryID())
	return -1;
    return 1;
  }//end compare()
    
  public boolean equals(Object o){
    if(!(o instanceof LineItemSorter))
        return false;
    else return true;
  }//end overridden equals()
}//end class
//==========================================================
//==========================================================


