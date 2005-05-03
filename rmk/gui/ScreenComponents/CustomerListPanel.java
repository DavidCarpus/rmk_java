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
import rmk.gui.Dialogs;
import rmk.gui.IScreen;


public class CustomerListPanel 
	extends DataListPanel 
	implements ActionListener
{
	
    Vector listeners=null;
    Vector customerList;
    long selectedCustomer;
    carpus.gui.BasicToolBar buttonBar;
    IScreen parent = null;
    
    public CustomerListPanel(){
    	dataModel = new CustomerListTableModel(customerList);
    	setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    	setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    	addTable(dataModel);

//	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
//      	this.registerKeyboardAction(this, "Cancel", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

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
//	sorter.addMouseListenerToHeaderInTable(table);
	
	//following code specifies that only one row at a time can be selected
//	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//	ListSelectionModel rowSM = table.getSelectionModel();
//	rowSM.addListSelectionListener(new ListSelectionListener(){
//		public void valueChanged(ListSelectionEvent e) {
//		    if (e.getValueIsAdjusting()) return; //Ignore extra messages.
//		    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
//		    if (!lsm.isSelectionEmpty()) {
//			int row=lsm.getMinSelectionIndex();
//				selectedCustomer = ((Long)sorter.getValueAt(row,0)).longValue();
//				buttonBar.enableButton(1, false);
////				buttonBar.enableButton(2, false);
//				try {
//                    Customer cust = rmk.DataModel.getInstance().customerInfo.getCustomerByID(selectedCustomer);
//                	buttonBar.enableButton(2, cust.isDealer());
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//		    }
//		    buttonBar.enableButton(0, !lsm.isSelectionEmpty());
//		    buttonBar.enableButton(1, !lsm.isSelectionEmpty());
//		}
//	    });
//	table.addMouseListener(new MouseAdapter(){
//		public void mouseClicked(MouseEvent e){
//		    if (e.getClickCount() == 2){
//			actionPerformed(new ActionEvent(this,1,"CustomerDetails"));
//		    }
//		}
//	    } );

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
//    	setPreferredSize(new Dimension(325,125));
    }
    
	//==========================================================
	protected void doubleClick() {
		parent.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS, (int) selectedItem);
//		actionPerformed(new ActionEvent(this, 1, "InvoiceDetails"));
	}
	
	protected long selectedItem(int row) {
		long val = ((Long) sorter.getValueAt(row, 0)).longValue();
		buttonBar.enableButton(0, true);
		buttonBar.enableButton(1, true);
		buttonBar.enableButton(2, true);
		return val;
	}
	public void setParent(IScreen screen){
		parent = screen;
	}
	
    public long getSelectedItemID(){
        return selectedCustomer;
    }
    public Customer getSelectedCustomer() throws Exception{
        return rmk.DataModel.getInstance().customerInfo.getCustomerByID(selectedCustomer);        
    }

    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase();
	
	
	
    ErrorLogger.getInstance().logDebugCommand(command);

	ActionEvent event=null;

	if(command.equals("CUSTOMERDETAILS")){
	    event = new ActionEvent(this,1,command);
	} else if(command.equals("CANCEL")){
	    event = new ActionEvent(this,1,command);
	} else if(command.equals("MERGE")){
	    event = new ActionEvent(this,1,command);
	} else if(command.equalsIgnoreCase("QuickDealer")){
	    try {
		    Dialogs.generateBlankDealerInvoice(getSelectedItemID());
	    } catch (Exception err) {
	    	// TODO: handle exception
	    }
	} else {
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "?");
	}

	if(event != null && listeners != null){
	    for(Enumeration enum=listeners.elements(); enum.hasMoreElements();){
		((ActionListener)enum.nextElement()).actionPerformed(event);
	    }
	}
    }
    public void addActionListener(ActionListener listener){
	if(listeners == null) listeners = new Vector();
	if(!listeners.contains(listener)) listeners.addElement(listener);
    }
    public void setData(rmk.gui.DBGuiModel model){
    	Vector custData = model.getCustomerData();
    	setData(custData);
    }


    public static void main(String args[])
	throws Exception
    {
	rmk.gui.Application.main(args);
    }

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
