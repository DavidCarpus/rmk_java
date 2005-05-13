/*
 * Created on Jan 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package rmk.gui.ScreenComponents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.table.TableColumn;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.HistoryItems;
import rmk.gui.DBGuiModel;
import rmk.gui.IScreen;
import carpus.gui.DataListPanel;
import carpus.gui.DataListPanelTableModel;

/**
 * @author carpus
 */
public class InvoiceHistoryListPanel 
extends DataListPanel 
implements ActionListener, FocusListener {
	Vector invoiceList = new Vector();
//	DBGuiModel model;
	IScreen parent = null;
	
	public InvoiceHistoryListPanel() {
		dataModel = new InvoiceHistoryListTableModel(invoiceList);
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
			} else if (i == 2) { // Invoice column
				column.setPreferredWidth(20);
			} else {
				column.setPreferredWidth(100);
			}
		}
		setTableSelectionListeners();
		//    	setPreferredSize(new Dimension(325,125));
	}
	
	/* (non-Javadoc)
	 * @see carpus.gui.DataListPanel#doubleClick()
	 */
	protected void doubleClick() {
		parent.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS, 0);
//		actionPerformed(new ActionEvent(this, 1, "InvoiceDetails"));
	}

	/* (non-Javadoc)
	 * @see carpus.gui.DataListPanel#selectedItem(int)
	 */
	protected long selectedItem(int row) {
		long val = ((Long) sorter.getValueAt(row, 2)).longValue();
		// col 1 is hidden ID field
		return val;
	}

	/* (non-Javadoc)
	 * @see carpus.gui.DataListPanel#setData(rmk.gui.DBGuiModel)
	 */
//	public void setData(DBGuiModel model) {
//		this.model = model;
//
//		if(setData(model.getInvoiceData())){
//			sorter.sortByColumn(3, false);
//		} else{
//			ErrorLogger.getInstance().logMessage("Null data?");
//		}
//
//		setVisible(true);
//	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand().toUpperCase();

		if (command.equals("INVOICEDETAILS") || command.equals("CTRL_ENTERKEY")) {
			parent.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS,0);
			return;
		}else if(command.equals("CANCEL")){
			parent.buttonPress(ScreenController.BUTTON_CANCEL, 0);
			return;
			
			
		} else {  // Undefined
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":UndefinedAction:" + command + "|");
		}
        ErrorLogger.getInstance().logDebugCommand(command);
	}
    
	public void setParent(IScreen screen){
		parent = screen;
	}
}
//======================================================
//======================================================
class InvoiceHistoryListTableModel extends carpus.gui.DataListPanelTableModel {
	rmk.DataModel sys = rmk.DataModel.getInstance();

	InvoiceHistoryListTableModel(Vector lst) {
		columnNames =
			new String[] {
				"Item",
				"Index",
				"Invoice",
				"Viewed",
				"Customer"
		};
		this.flags = new int[columnNames.length];
		flags[3] |= DataListPanelTableModel.FLAGS_DATE;
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
			HistoryItems item = (HistoryItems ) lst.get(i);
			data[i][colIndex++] = item;
			data[i][colIndex++] = new Integer(i);
			data[i][colIndex++] = new Long(item.getInvoice());
			java.util.Date date;
			date = carpus.util.DateFunctions.javaDateFromGregorian(
					item.getDate());
			data[i][colIndex++] = date;
			data[i][colIndex++] = item.getCustomerName();
		}
	}
}
