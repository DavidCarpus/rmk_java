package rmk.gui.ScreenComponents;

import java.awt.*;
import java.awt.event.*;
import carpus.gui.*;
import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.gui.IScreen;

import java.util.*;
import javax.swing.table.TableColumn;

public class InvoiceListPanel 
extends 
//  JPanel 
carpus.gui.DataListPanel
implements ActionListener, FocusListener{
	public static final int INVOICE_COL_WIDTH = 48;
	IScreen parent = null;
    Vector invoiceList;

//    int customerID=0;
    Customer customer;
    Vector invList=null;
    
//    rmk.gui.DBGuiModel model;

	public InvoiceListPanel() {

		dataModel = new InvoiceListTableModel(invoiceList);
		addTable(dataModel);
		setColumnFormats();

		TableColumn column = null;
		for (int i = 0; i < dataModel.getColumnCount(); i++) {
			column = table.getColumnModel().getColumn(i);
			if (i == 0 || i == 1) { // Item && ID column, HIDE THEM
				column.setMaxWidth(0);
				column.setMinWidth(0);
				column.setWidth(0);
				column.setPreferredWidth(0);
			} else if (i == 2) { // Invoice column
				column.setMaxWidth(InvoiceListPanel.INVOICE_COL_WIDTH );
				column.setMinWidth(InvoiceListPanel.INVOICE_COL_WIDTH );
				column.setWidth(InvoiceListPanel.INVOICE_COL_WIDTH );
				column.setPreferredWidth(InvoiceListPanel.INVOICE_COL_WIDTH );
			} else {
				column.setPreferredWidth(100);
			}
		}

		setTableSelectionListeners();

		buttonBar =
			new carpus.gui.BasicToolBar(
				null,
				new String[] {
					"Shipped",
					"Details",
					"Add",
					"Payments",
					"Invoice",
					"Reload" },
				new String[] {
					"Shipped",
					"InvoiceDetails",
					"Add",
					"Payments",
					"Invoice",
					"Reload" },
				new String[] {
					"Shipped",
					"Details",
					"Add New Invoice",
					"Payments",
					"Invoice Report",
					"Reload" });

		buttonBar.getButton(4).setMnemonic(KeyEvent.VK_I); // Invoice Button
		buttonBar.getButton(2).setMnemonic(KeyEvent.VK_A); // Add button
		buttonBar.getButton(1).setMnemonic(KeyEvent.VK_E);
		// Details/Edit button
		buttonBar.addActionListener(this);
		buttonBar.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonBar.enableButton(0, true);
		buttonBar.enableButton(1, false);
		buttonBar.enableButton(2, false);
		buttonBar.enableButton(3, false);
		buttonBar.enableButton(4, false);
		buttonBar.enableButton(5, false);
		add(buttonBar);
		//    	setPreferredSize(new Dimension(325,125));
	}
	public void setParent(IScreen screen){
		parent = screen;
	}
	//-------------------------------------------
	public void doubleClick() {
		parent.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS, (int) getSelectedItemID());
//		actionPerformed(new ActionEvent(this, 1, "InvoiceDetails"));
	}
	//-------------------------------------------
	public long selectedItem(int row) {
		long val = ((Long) sorter.getValueAt(row, 2)).longValue();
		// col 0 is hidden ID field
		buttonBar.enableButton(1, true);
		buttonBar.enableButton(2, true);
		buttonBar.enableButton(3, true);
		buttonBar.enableButton(4, true);
		return val;
	}
	//-------------------------------------------
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand().toUpperCase();

		ActionEvent event = null;

		if (command.equals("INVOICEDETAILS") || command.equals("CTRL_ENTERKEY")) {
			parent.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS, (int) getSelectedItemID());
			return;
		} else if (command.equals("INVOICE")) {
			parent.buttonPress(ScreenController.BUTTON_DISPLAY_INVOICE,0);
			return;
		}else if (command.equals("SHIPPED")) { //PaymentInfo Display
			if (buttonBar.getButtonLabel(0).equals("Shipped")) {
				setData(sys.invoiceInfo.getShippedInvoices(customer));
				buttonBar.setButtonLabel(0, "Basic");
			} else {
				setData(sys.invoiceInfo.getInitialInvoices(customer));
				buttonBar.setButtonLabel(0, "Shipped");
			}
			return;
		} else if (command.equals("ADD")) {
			parent.updateOccured(null,ScreenController.UPDATE_ADD ,null);
			return;
		} else if(command.equals("CANCEL")){
			parent.buttonPress(ScreenController.BUTTON_CANCEL, 0);
			return;
		} else if (command.equals("PAYMENTS")){
			parent.buttonPress(ScreenController.BUTTON_F7, (int) getSelectedItemID());
			return;
		} else if (command.equals("RELOAD")) { //PaymentInfo Display
//			model.setInvoiceData(sys.invoiceInfo.getInitialInvoices(customer));
//			setData(model.getInvoiceData());
			buttonBar.setButtonLabel(0, "Shipped");
			return;
		}
		
		ErrorLogger.getInstance().logDebugCommand(command);
//		if (command.equals("PAYMENTS")) { //PaymentInfo Display
//			event = e;
//
//		} else if (command.equals("RELOAD")) { //PaymentInfo Display
//			model.setInvoiceData(sys.invoiceInfo.getInitialInvoices(customer));
//			setData(model.getInvoiceData());
//			buttonBar.setButtonLabel(0, "Shipped");
//
//		} else { // Undefined
//			ErrorLogger.getInstance().logMessage(
//				this.getClass().getName() + ":Undefined:" + command + "|");
//		}
//
//		notifyListeners(event);
	}
	//------------------------------------------
//	public void setData(rmk.gui.DBGuiModel model) {
	public boolean setData(Vector invList) {
//		this.model = model;
//
//		if (model.getCustomerData() != null) {
//			Customer currentCustomer =
//				(Customer) model.getCustomerData().get(0);
//			customer = currentCustomer;
//		}
		
		customer = ((Invoice)invList.get(0)).getParent();
		
		// Invoice data processing
		if(super.setData(invList)){
			buttonBar.enableButton(0, true);
		} else{
			ErrorLogger.getInstance().logMessage(
				this.getClass().getName() + ":" + "Null data?:" + data);
			buttonBar.enableButton(0, false);
		}

		// Customer data processing
		if(customer != null && customer.getCustomerID() > 0) 
			buttonBar.enableButton(2, true);

		buttonBar.enableButton(5, customer.getCustomerID() != 0);

		setVisible(true);
		
		return true;
	}
}


	//======================================================
	//======================================================
	class InvoiceListTableModel extends carpus.gui.DataListPanelTableModel {
		rmk.DataModel sys = rmk.DataModel.getInstance();

		InvoiceListTableModel(Vector lst) {
			columnNames =
				new String[] {
					"Item",
					"Index",
					"Invoice",
					"PO #",
					"Ordered",
					"Estimated",
					"Shipped",
					"Invoice" ,
					"Due"};
			this.flags = new int[columnNames.length];
			flags[4] |= DataListPanelTableModel.FLAGS_DATE;
			flags[5] |= DataListPanelTableModel.FLAGS_DATE;
			flags[6] |= DataListPanelTableModel.FLAGS_DATE;
			flags[7] |= DataListPanelTableModel.FLAGS_CURRENCY;
			flags[8] |= DataListPanelTableModel.FLAGS_CURRENCY;

			setValues(lst);
		}

		public void setValues(Vector lst) {
			if (lst == null || lst.size() == 0) {
				data = null;
				return;
			}
			data = new Object[lst.size()][columnNames.length];

			for (int i = 0; i < lst.size(); i++) {
				for (int j = 0; j < columnNames.length; j++)
					data[i][j] = "";

				int colIndex = 0;
				Invoice item = (Invoice) lst.get(i);
				data[i][colIndex++] = item;
				data[i][colIndex++] = new Integer(i);
				data[i][colIndex++] = new Long(item.getInvoice());
				if (item.getPONumber() != null)
					data[i][colIndex++] = "  " + item.getPONumber();
				else
					data[i][colIndex++] = "";

				java.util.Date date;
				date =
					carpus.util.DateFunctions.javaDateFromGregorian(
						item.getDateOrdered());
				data[i][colIndex++] = date;
				//  	    data[i][colIndex++] = item.getDateOrdered();
				date =
					carpus.util.DateFunctions.javaDateFromGregorian(
						item.getDateEstimated());
				data[i][colIndex++] = date;
				date =
					carpus.util.DateFunctions.javaDateFromGregorian(
						item.getDateShipped());
				data[i][colIndex++] = date;
				
//				data[i][colIndex++] =
//					new Double(sys.financialInfo.getTotalRetail(item));
				// change col from total retail to total invoice (incorporate discount)
				double discount = sys.financialInfo.getTotalInvoiceDiscount(item);
				double retail = sys.financialInfo.getTotalRetail(item);
				double shipping = item.getShippingAmount();
				double taxesDue = sys.financialInfo.getInvoiceTaxes(item);

				double invAmt = retail-discount + shipping + taxesDue;
				    
				data[i][colIndex++] =	new Double(invAmt);
				
				double due = sys.financialInfo.getInvoiceDue(item);
				data[i][colIndex++] =
					new Double(due);
				
			}
		}
	}
