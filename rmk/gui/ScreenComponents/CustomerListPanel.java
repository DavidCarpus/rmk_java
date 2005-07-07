package rmk.gui.ScreenComponents;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import carpus.gui.*;

import java.util.*;

import javax.swing.table.TableColumn;
import javax.swing.border.EtchedBorder;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.Customer;


public class CustomerListPanel 
	extends DataListPanel 
	implements ActionListener
{
	
    Vector customerList;
//    long selectedCustomer;
    carpus.gui.BasicToolBar buttonBar;
    
    public CustomerListPanel(){
    	dataModel = new CustomerListTableModel(customerList);
    	setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    	setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    	addTable(dataModel);

	TableColumn column = null;
	for (int i = 0; i < dataModel.getColumnCount(); i++) {
		column = table.getColumnModel().getColumn(i);
	    if (i == 0) { // ID column, hide
		column.setMaxWidth(0);
		column.setMinWidth(0);
		column.setWidth(0); 
  		column.setPreferredWidth(0);
	    }else if (i == 1) { // Name column
		column.setPreferredWidth(250);
	    }else if (i > dataModel.getColumnCount()-3) { // last 2 columns, flags
			column.setPreferredWidth(10);
	    }else {
		column.setPreferredWidth(100);
	    }
	}

	setTableSelectionListeners();

      	JScrollPane scrollPane = new JScrollPane(table);

  	add(scrollPane);

	buttonBar = new carpus.gui.BasicToolBar(null, new String[] {"Details","Merge", "QuickDealer"}, 
				   new String[] {"CustomerDetails","Merge", "QuickDealer"},
				   new String[] {"Details","Merge", "QuickDealer"});

	buttonBar.getButton(0).setMnemonic(KeyEvent.VK_E); // Details/Edit button
	buttonBar.addActionListener(this);
	buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));
	buttonBar.enableButton(0, false);
	buttonBar.enableButton(1, false);
	buttonBar.enableButton(2, false);
  	add(buttonBar);	
    }
    
	//==========================================================
	protected void doubleClick() {
		parentScreen.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS, (int) selectedItem);
		lastSelectedItem = selectedItem;
		ErrorLogger.getInstance().logMessage("selectedItem:" + lastSelectedItem);

//		actionPerformed(new ActionEvent(this, 1, "InvoiceDetails"));
	}
	
	protected long selectedItem(int row) {
		long val = ((Long) sorter.getValueAt(row, 0)).longValue();
		buttonBar.enableButton(0, true);
		buttonBar.enableButton(1, true);
		buttonBar.enableButton(2, true);
		return val;
	}

    public long getSelectedItemID(){
//        return selectedCustomer;
        return selectedItem;
    }
    public Customer getSelectedCustomer() throws Exception{
//        return sys.customerInfo.getCustomerByID(selectedCustomer);        
        return sys.customerInfo.getCustomerByID(selectedItem);        
    }

    public void actionPerformed(ActionEvent e) {
    	String command = e.getActionCommand().toUpperCase();
		
    	if(processHotKeyCommands(command) ){
    		return;
    	}
	
	
    	ErrorLogger.getInstance().logDebugCommand(command);
    	
    	ActionEvent event=null;
    	
    	if(command.equals("CUSTOMERDETAILS")){
    		parentScreen.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS, (int) getSelectedItemID());
    		return;
    	} else if(command.equals("MERGE")){
    		parentScreen.buttonPress(ScreenController.BUTTON_CUSTOMER_MERGE, (int) getSelectedItemID());
    		return;
    	} else if(command.equalsIgnoreCase("QuickDealer")){
    		parentScreen.buttonPress(ScreenController.BUTTON_QUICK_DEALER_INVOICE, (int) getSelectedItemID());
    		return;
    		
//    		try {
//    			Dialogs.generateBlankDealerInvoice(getSelectedItemID());
//    		} catch (Exception err) {
//    			// TODO: handle exception
//    		}
    	} else {
    		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "?");
    	}
    }

//	if(event != null && listeners != null){
//	    for(Enumeration enum=listeners.elements(); enum.hasMoreElements();){
//		((ActionListener)enum.nextElement()).actionPerformed(event);
//	    }
//	}
//    }
//    public void addActionListener(ActionListener listener){
//	if(listeners == null) listeners = new Vector();
//	if(!listeners.contains(listener)) listeners.addElement(listener);
//    }
}


//===========================================================
class CustomerListTableModel extends carpus.gui.DataListPanelTableModel {
    String[] columnNames= new String[]{"Index", "CustomerName", "PhoneNumber", "Dealer", "Flagged"};
    Object[][] data= new Object[0][columnNames.length];
    boolean[] dateColumn = new boolean[]{false, false, false, false, false};

    CustomerListTableModel(Vector lst){
	if(lst != null){
	    setValues(lst);
	}
    }
    public void setValues(Vector lst){
	if(lst == null){
	    data = new Object[columnNames.length][1];
	    return;
	}
	data = new Object[lst.size()][columnNames.length];
	for(int i=0; i< lst.size(); i++){
	    rmk.database.dbobjects.Customer item = 
		(rmk.database.dbobjects.Customer)lst.get(i);
	    data[i][0] = new Long(item.getCustomerID());
	    data[i][1] = "" + item.getLastName() + 
		(item.getFirstName() != null? "," + item.getFirstName(): "");
	    data[i][2] = "" + item.getPhoneNumber();
	    data[i][3] = new Boolean(item.getDealer() != 0);
	    data[i][4] = new Boolean(item.isFlag());
	}
    }
    public boolean isDateColumn(int i){	return dateColumn[i];}

    public int getColumnCount() { return columnNames.length; }        
    public int getRowCount() {return data.length;}    
    public String getColumnName(int col) {return columnNames[col];}
    public Object getValueAt(int row, int col) {return data[row][col];}

    public Class getColumnClass(int c) {
	if(getValueAt(0, c) != null)
	    return getValueAt(0, c).getClass();
	else
	    return null;
    }
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return false;
    }
}
