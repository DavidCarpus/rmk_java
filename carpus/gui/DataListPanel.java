package carpus.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.table.TableColumn;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.EtchedBorder;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.HistoryItems;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.gui.IScreen;

import carpus.database.DBObject;

public abstract class DataListPanel extends JPanel implements ActionListener,
		FocusListener {

	Vector listeners = null;

	protected DataListPanelTableModel dataModel;

	protected carpus.util.TableSorter sorter;

	protected Vector data;

	protected JTable table;
	JScrollPane scrollPane;
	
	protected long selectedItem;
	
	protected long lastSelectedItem;

	protected carpus.gui.BasicToolBar buttonBar;

	protected rmk.DataModel sys = rmk.DataModel.getInstance();

	protected IScreen parentScreen=null;
	
	//      LabeledTextField subTotal;

	//==========================================================
	//==========================================================
	public void setTableSelectionListeners() {
		//following code specifies that only one row at a time can be selected
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return; //Ignore extra messages.
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (!lsm.isSelectionEmpty()) {
					int index = lsm.getMinSelectionIndex();
					selectedItem = selectedItem(index);
					setCellVisible(table, index, index);
				}
			}
		});
		table.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doubleClick();
					//  			actionPerformed(new
					// ActionEvent(this,1,"EditInvoiceEntry"));
				}
			}
		});
		KeyAdapter ka = new keyCheck(this);
		table.addKeyListener(ka);
	}

	protected void addTable(DataListPanelTableModel dataModel) {
		this.dataModel = dataModel;
		sorter = new carpus.util.TableSorter(dataModel);
		addFocusListener(this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

		table = new JTable(sorter);
		table.setFont(new Font("Serif", Font.BOLD, 14));
		table.setSelectionBackground(new Color(0, 178, 238));

//		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
//		this.registerKeyboardAction(this, "Cancel", stroke,
//				JComponent.WHEN_IN_FOCUSED_WINDOW);

//		table.getInputMap().put(KeyStroke.getKeyStroke("F2"), "none");
//		Action doNothing = new AbstractAction() {
//
//			public void actionPerformed(ActionEvent e) {
//				notifyListeners(new ActionEvent(this, 1, "F2"));
//				//    		    System.out.println(this.getClass().getName() + ":"+
//				// "doNothing?");
//			} //do nothing
//		};
//		table.getInputMap().put(KeyStroke.getKeyStroke("F2"), "doNothing");
//		table.getActionMap().put("doNothing", doNothing);

		sorter.addMouseListenerToHeaderInTable(table);

		scrollPane = new JScrollPane(table);
		add(scrollPane);
	}

	public void initialSelection(){
		lastSelectedItem = 0;
	}
	
	protected void setColumnFormats() {
		TableColumn column = null;
		int colCnt = dataModel.getColumnCount();
		for (int i = 0; i < colCnt; i++) {
			column = table.getColumnModel().getColumn(i);
			if (dataModel.isCurrencyColumn(i)) { // currency
																	   // fields
				table.getColumnModel().getColumn(i).setCellRenderer(
						new TableCurrencyCellRenderer(table
								.getSelectionBackground()));

			}
			if (dataModel.isDateColumn(i)) { // date fields
				table.getColumnModel().getColumn(i).setCellRenderer(
						new TableDateCellRenderer(table
								.getSelectionBackground()));

			}
		}
	}

	protected abstract void doubleClick();

	public void selectLastSelected(){
		long searchID = lastSelectedItem;
		ListSelectionModel rowSM = table.getSelectionModel();
		long minIndex = rowSM.getMinSelectionIndex();
		long maxIndex = rowSM.getMaxSelectionIndex();
//		if (minIndex < 0)
//			return;
		int row=0;
		Object valueAt="";
		for(row = 0; row < table.getRowCount(); row++){
			valueAt = table.getValueAt((int) row,0);
			String type = valueAt.getClass().getName().toUpperCase();
			if(type.endsWith("HISTORYITEMS")){
				if(((HistoryItems)valueAt).getInvoice() == searchID){
					break;
				}
			}else if(type.endsWith("INVOICEENTRIES")){
				if(((InvoiceEntries)valueAt).getInvoiceEntryID() == searchID){
					break;
				}
			}else if(type.endsWith("INVOICE")){
				if(((Invoice)valueAt).getInvoice() == searchID){
					break;
				}
			}else{
				ErrorLogger.getInstance().logDebug("Unknown list item type:" + type, true);
//				ErrorLogger.getInstance().logWarning("Unknown list item type:" + type);
			}
		}	
		if(row == table.getRowCount()){ // not found, select first one
			ErrorLogger.getInstance().logDebug("********* Will select DataListPanel row:" + 0 + " " + valueAt, false);
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int rowToSelect = 0;
				ErrorLogger.getInstance().logMessage("Selecting DataListPanel row:" + 0);
				table.requestFocusInWindow();
				table.changeSelection(rowToSelect, rowToSelect, false, false);
			}
		});
		}else{
			final int selectedRow=row;
			ErrorLogger.getInstance().logDebug("********* Will select DataListPanel row:" + selectedRow + " " + valueAt, false);
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int rowToSelect = selectedRow;
				ErrorLogger.getInstance().logMessage("Selecting DataListPanel row:" + selectedRow);
				table.requestFocusInWindow();
				table.changeSelection(rowToSelect, rowToSelect, false, false);
			}
		});
		}
		return;
	}
	
	protected abstract long selectedItem(int row);

	public long getSelectedItemID() {
		return selectedItem;
	}

	//	public abstract Object getSelectedItem();

	public Object getSelectedItem() {
		ListSelectionModel rowSM = table.getSelectionModel();
		if (rowSM.getMinSelectionIndex() < 0)
			return null;
		return sorter.getValueAt(rowSM.getMinSelectionIndex(), 0);
	}

	public void addActionListener(ActionListener listener) {
		if (listeners == null)
			listeners = new Vector();
		if (!listeners.contains(listener))
			listeners.addElement(listener);
	}

	public void notifyListeners(ActionEvent e) {
		if (e == null || listeners == null)
			return;
		Vector sentTo = new Vector();
		for (Enumeration enum = listeners.elements(); enum.hasMoreElements();) {
			ActionListener listener = ((ActionListener) enum.nextElement());
			if (sentTo.indexOf(listener) < 0) {
				sentTo.add(listener);
				listener.actionPerformed(e);
			} else {
				System.out.println("'Eating' event....");
			}
		}
	}
	
    protected boolean processHotKeyCommands(String command){
    	if(command.equals("F1")){
    		parentScreen.buttonPress(ScreenController.BUTTON_F1, 0);
    		return true;
    	}else if(command.equals("F2")){
    		parentScreen.buttonPress(ScreenController.BUTTON_F2, 0);
    		return true;
    	}else if(command.equals("F3")){
    		parentScreen.buttonPress(ScreenController.BUTTON_F3, 0);
    		return true;						
    	}else if(command.equals("CANCEL")){
    		parentScreen.buttonPress(ScreenController.BUTTON_CANCEL, 0);
    		return true;	
    	}
    	return false;
    }

	//----------------------------------------------------------
	public void focusGained(FocusEvent e) {
		//  	System.out.println(this.getClass().getName() + ":"+ "focusGained");
		if(lastSelectedItem > 0){
			selectLastSelected();
		} else {
			selectFirst();
		}
	}

	public void focusLost(FocusEvent e) {
	}

	public long selectFirst() {
		if (table.getRowCount() > 0) {
			//  	    System.out.println(this.getClass().getName() + ":"+
			// "selectFirst");
			table.clearSelection();
			table.addRowSelectionInterval(0, 0);
			table.grabFocus();
			setCellVisible(table, 0, 0);
			//  	    return ((Long)sorter.getValueAt(0,1)).longValue();
			return 0;
		}
		return 0;
		//  	selectedInvoice = ((Long)sorter.getValueAt(row,1)).longValue();
	}
	
    public void makeVisible(int row){
    	setCellVisible(table, row-1, 2);
    }

	public long selectLast() {
		if (table.getRowCount() > 0) {
			table.clearSelection();
			table.addRowSelectionInterval(table.getRowCount() - 1, table
					.getRowCount() - 1);
			table.grabFocus();
			setCellVisible(table, table.getRowCount()-1, 2);
			table.grabFocus();
			return table.getRowCount();
		}
		return 0;
		//  	selectedInvoice = ((Long)sorter.getValueAt(row,1)).longValue();
	}
	
	static void setCellVisible(JTable table, int row, int col) {
		Container p = table.getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				//     Make sure the table is the main viewport
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != table) {
					return;
				}

				Rectangle cellrect = table.getCellRect(row, col, true);
				Rectangle viewrect = viewport.getViewRect();
				if (viewrect.contains(cellrect))
					return;
				Rectangle union = viewrect.union(cellrect);
				int x = (int) (union.getX() + union.getWidth() - viewrect
						.getWidth());
				int y = (int) (union.getY() + union.getHeight() - viewrect
						.getHeight());
				viewport.setViewPosition(new Point(x, y));
			}
		}

	}

	//----------------------------------------------------------
//	public final void setData(rmk.gui.DBGuiModel model)
//	{
//		
//	}

	public boolean setData(Vector lst) {
		data = lst;
		if (lst != null) {
			for (Enumeration enum = lst.elements(); enum.hasMoreElements();) {
				DBObject item = (DBObject) enum.nextElement();
				if (item.getID() == null || item.getID().longValue() == 0)
					lst.remove(item);
			}
		}
		dataModel.setValues(lst);
		sorter.tableChanged(new javax.swing.event.TableModelEvent(sorter));
		sorter.sortByColumn(0, true);
		setVisible(true);
		if(lst == null || lst.size() <= 0 ) return false;
		else return true;
	}

	//----------------------------------------------------------
	public void setMore(boolean more) {
		buttonBar.enableButton(0, more);
	}

	class keyCheck extends KeyAdapter {
		DataListPanel lst;

		keyCheck(DataListPanel list) {
			lst = list;
		}
	    public void keyTyped(KeyEvent e){
	    	if(e.isControlDown()) // ctrl key was held ... Not processes here
	    		return;
	    	if(e.getKeyCode() >= KeyEvent.VK_F1 && e.getKeyCode() <= KeyEvent.VK_F12){
	    		ErrorLogger.getInstance().logDebug("Missing function key registration", true);
	    	}
	    }
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();

			if (code == KeyEvent.VK_ENTER && e.isControlDown()){
				long item = getSelectedItemID();
				int id=(int) item;
//				if(item.getClass().getName().indexOf(".Long")>0)
//					id = ((Long)item).intValue();
//				if(item.getClass().getName().indexOf(".Integer")>0)
//					id = ((Integer)item).intValue();
//				if(item.getClass().getName().indexOf(".Invoice")>0)
//					id = ((Invoice)item).();
//				int id = item;
				if(parentScreen==null)
					ErrorLogger.getInstance().TODO();
				
				parentScreen.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS, id);
			}
//				lst.actionPerformed(new ActionEvent(this, 1, "CTRL_ENTERKEY"));
		}
	}

	/**
	 * @return Returns the parentScreen.
	 */
	public IScreen getParentScreen() {
		return parentScreen;
	}
	/**
	 * @param parentScreen The parentScreen to set.
	 */
	public void setParentScreen(IScreen parentScreen) {
		this.parentScreen = parentScreen;
	}
}