package rmk.gui.ScreenComponents;

import java.awt.*;
import java.awt.event.*;
import carpus.gui.*;
import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.FinancialInfo;
import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.Payments;
import rmk.gui.IScreen;

import java.util.*;
import javax.swing.table.TableColumn;

public class InvoicePaymentsListPanel 
extends 
//  JPanel  
carpus.gui.DataListPanel
implements ActionListener{
	
	Vector invoicePaymentsList;
	IScreen parent=null;
	//================================================
	public InvoicePaymentsListPanel(){
		dataModel = new InvoicePaymentsListTableModel(invoicePaymentsList);
		addTable(dataModel);
		setColumnFormats();
		
		TableColumn column = null;
		for (int i = 0; i < dataModel.getColumnCount(); i++) {
			column = table.getColumnModel().getColumn(i);
			if (i == 0 || i == 1) { // Item && ID column
				column.setMaxWidth(0);
				column.setMinWidth(0);
				column.setWidth(0); 
				column.setPreferredWidth(0);
			}else if (i == 2) { // Invoice column
				column.setPreferredWidth(20);
			}else {
				column.setPreferredWidth(100);
			}
		}
		setTableSelectionListeners();
		
		buttonBar = new carpus.gui.BasicToolBar(null, 
				new String[] {"Add", "Delete"}, 
				new String[] {"Add", "Delete"}, 
				new String[] {"Add", "Delete"});
		buttonBar.addActionListener(this);
		buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));
		buttonBar.enableButton(0, false);
		buttonBar.enableButton(1, false);
		buttonBar.getButton(0).setMnemonic(KeyEvent.VK_A); // Add button
		
		
		
		add(buttonBar);
	}
	//-------------------------------------------
	public void doubleClick(){
		actionPerformed(new ActionEvent(this,1,"PaymentDetail"));
	}
	//-------------------------------------------
	public long selectedItem(int row){
		long val = Long.parseLong(""+sorter.getValueAt(row,2)); // col 1 is hidden ID field
		buttonBar.enableButton(0, true);
		buttonBar.enableButton(1, true);
		return val;
	}
	//================================================
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand().toUpperCase();
		ErrorLogger.getInstance().logDebugCommand(command);
		
		ActionEvent event=null;
		//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "|");
		
		if(command.equals("DELETE")){
			parent.buttonPress(ScreenController.BUTTON_REMOVE, (int) getSelectedItemID());
			return;
		}else if(command.equals("CANCEL")){
			parent.buttonPress(ScreenController.BUTTON_CANCEL, 0);
			return;
		}else if(command.equals("ADD")){
			parent.buttonPress(ScreenController.BUTTON_ADD, 0);
			return;

		
//		}else if(command.equals("ADD")){
//			event = e;
//
//		}else if(command.equals("F1") || command.equals("F2") || command.equals("F3")){
//			event = e;
//		} else {  // Undefined
//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
		}
		ErrorLogger.getInstance().logMessage("*** Undefined:" + command + "|");
		ErrorLogger.getInstance().TODO();
	}
	//================================================
	public void setData(Invoice inv, Vector payments){
		//  	Vector data;
		//  	data = model.getPaymentsData();
		super.setData(payments);
		//  	invoicePaymentsData.setValues(data);
		sorter.tableChanged(new javax.swing.event.TableModelEvent(sorter));
		
		if(data != null && data.size() > 0){
			sorter.sortByColumn(5, false); // default - sort by date descending
		}
		Customer cust = inv.getParent();
		if(cust != null && cust.getCustomerID() > 0){
			buttonBar.enableButton(0, true);
		}
		setVisible(true);
	}
	
	public void setParent(IScreen screen){
		parent = screen;
	}
	
	//================================================
	//================================================
	//================================================
	class InvoicePaymentsListTableModel
	extends carpus.gui.DataListPanelTableModel {
		rmk.DataModel sys = rmk.DataModel.getInstance();
		
		InvoicePaymentsListTableModel(Vector lst) {
			columnNames =
				new String[] {
					"Item",
					"Index",
					"Payment#",
					"Invoice",
					"Payment",
					"Check/Card Number",
			"Date" };
			this.flags = new int[columnNames.length];
			flags[4] |= DataListPanelTableModel.FLAGS_CURRENCY;
			flags[6] |= DataListPanelTableModel.FLAGS_DATE;
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
				Payments item = (Payments) lst.get(i);
				data[i][colIndex++] = item;
				data[i][colIndex++] = new Integer(i);
				data[i][colIndex++] = "" + item.getPaymentID();
				data[i][colIndex++] = "" + (int) item.getInvoice();
				//  	    data[i][colIndex++] = new Long((int)item.getInvoice());
				data[i][colIndex++] = new Double(item.getPayment());
				
				if (item.getCheckNumber() != null) {
					String number = item.getCheckNumber();
					if (FinancialInfo.isValidCCNumber(number))
						number = FinancialInfo.addCardNumberDashes(number);
					data[i][colIndex++] = "" + number;
				} else {
					data[i][colIndex++] = "";
				}
				
				java.util.Date date;
				date =
					carpus.util.DateFunctions.javaDateFromGregorian(
							item.getPaymentDate());
				data[i][colIndex++] = date;
			}
		}
	}
}
