package rmk.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.GregorianCalendar;

import rmk.DataModel;
import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.SignalProcessor;
import rmk.database.dbobjects.DBObject;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import rmk.database.dbobjects.Customer;

public class InvoiceDetailsScreen extends Screen implements ActionListener {
	//      ActionListener parentFrame;    
	rmk.gui.ScreenComponents.CustomerInfoPanel customerPnl;
	
	rmk.gui.ScreenComponents.InvoiceDetailsPanel invoiceDetailPnl;
	
	rmk.gui.ScreenComponents.InvoiceEntriesListPanel invoiceEntriesList;
	
	boolean editedCustomer = false;
	
	boolean editedInvoice = false;
	
	boolean expandedList = false;
	
	long invoiceNumber;
	
	long lastItemID = 0;
	
	String lastComment="";
	
	//    double originalInvoiceTaxes=0;
	
	//==========================================================
	public InvoiceDetailsScreen() {
		super("Invoice Details");
		
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		customerPnl = new rmk.gui.ScreenComponents.CustomerInfoPanel();
		customerPnl.setParent(this);
		//		customerPnl.addActionListener(this);
		getContentPane().add(customerPnl);
		
		invoiceDetailPnl = new rmk.gui.ScreenComponents.InvoiceDetailsPanel();
		invoiceDetailPnl.onPaymentsScreen(false);
		invoiceDetailPnl.setParent(this);
		//		invoiceDetailPnl.addActionListener(this);
		getContentPane().add(invoiceDetailPnl);
		invoiceDetailPnl.setEnabled(false);
		
		invoiceEntriesList = new rmk.gui.ScreenComponents.InvoiceEntriesListPanel();
		invoiceEntriesList.setParent(this);
		//		invoiceEntriesList.addActionListener(this);
		getContentPane().add(invoiceEntriesList);
		
		getContentPane().add(buttonBar);
		buttonBar.addButton(null, "Invoice", "Invoice", "Invoice");
		buttonBar.addButton(null, "Acknowledgment", "Acknowledgment",
		"Acknowledgment");
		buttonBar.addButton(null, "Ship", "Ship", "Ship This Invoice");
		buttonBar.getButton(0).setForeground(new Color(255, 12, 11));
		buttonBar.getButton(2).setMnemonic(KeyEvent.VK_I); // Invoice Button
		buttonBar.getButton(3).setMnemonic(KeyEvent.VK_K); // Acknowledgment Button
		buttonBar.getButton(4).setMnemonic(KeyEvent.VK_P); // Ship Button
//		ButtonBarTranslator translator = new ButtonBarTranslator(this, buttonBar);
		
		
		KeyStroke listExpand = KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F11, 0);
		this.registerKeyboardAction(this, "listExpand", listExpand,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		

		//  	customerPnl.setBackground(Color.RED);
    	SignalProcessor.getInstance().addScreen(this);

		setPreferredSize(new Dimension(915, 640));
		pack();
	}
	
	public InvoiceDetailsScreen(DBGuiModel data) {
		super("Invoice Details");
		setData(data);
	}

	//==========================================================
	public boolean isEdited() {
		return (editedInvoice || editedCustomer);
	}
	
	//==========================================================
	public void setData(DBGuiModel model) {
		this.model = model;
		//		model.removeActionListener(this);
		
		java.util.Vector data = model.getInvoiceData();
		if (data == null) {
			carpus.util.Logger.getInstance().logError(
					this.getClass().getName() + ":"
					+ "void setData(DBGuiModel model)" + ":\n"
					+ "Invoice data missing from model.\n",
					new Exception("Design Error"));
		}
		int index = 0;
		if (data.size() > 1) { // passed several invoices, last one is relevent
			index = data.size() - 1;
		}
		
		Invoice invoice = (Invoice) data.get(index);

		if (invoice.getInvoice() > 0) {
			sys.invoiceInfo.logInvoiceAccess(invoice);
		}
		
		setInvoice(invoice);
		
		buttonBar.getButton(2).setMnemonic(KeyEvent.VK_I);
		buttonBar.getButton(0).setMnemonic(KeyEvent.VK_V);
		
		buttonBar.enableButton(2, (invoiceNumber != 0));
		buttonBar.enableButton(3, (invoiceNumber != 0));
	}
	
	void setInvoice(Invoice invoice){
		customerPnl.setData(model);
		invoiceDetailPnl.setData(model);
		
		String newTitle = "Invoice:" + invoice.getID();
		newTitle += " (" + invoiceEntriesList.getTotalKnives() + ") Knives";
		updateTitle(newTitle);
		
		loadItemList(model);
		invoiceDetailPnl.updatePaymentInfo(invoice);
		
		String shipButtonLabel = "Ship";
		if (invoice.getDateShipped() != null)
			shipButtonLabel = "'Un'Ship";
		buttonBar.getButton(4).setText(shipButtonLabel);
	}
	
	//----------------------------------------------------------
	void loadItemList(DBGuiModel model) {
		java.util.Vector data = model.getInvoiceItemsData();
		removeBlankItems(data);
		// sort by invEntryID
		//		InvoiceEntries
		if(data != null){
			Object[] items = data.toArray();
			Arrays.sort(items, new rmk.comparators.InvoiceEntries());		
			Vector sortedData = new Vector();
			for(int i=0; i< items.length; i++){
				sortedData.add(items[i]);
			}
			data = sortedData;
			model.setInvoiceItemsData(data);
		}
		
		invoiceEntriesList.setData(model);
		Invoice inv = null;
		Vector invList = model.getInvoiceData();
		if (invList.size() > 0) {
			// get last entry, it will have id==0 if new
			inv = (Invoice) invList.get(invList.size() - 1);
			invoiceNumber = inv.getInvoice();
			
			if (inv.getInvoice() == 0) {
				buttonBar.enableButton(0, true);
				editedInvoice = true;
			}
		}
		int knifeCnt = invoiceEntriesList.getTotalKnives();
		if (inv != null) {
			invoiceDetailPnl.setData(inv);
		}
		invoiceDetailPnl.setTotalKnives(knifeCnt);
		
		String newTitle = "Invoice:" + inv.getID();
		newTitle += " (" + knifeCnt + ") Knives";
		updateTitle(newTitle);
		
		buttonBar.enableButton(0, editedInvoice || editedCustomer);
	}
	
	void removeBlankItems(Vector items) {
		if (items == null)
			return;
		for (Enumeration enum = items.elements(); enum.hasMoreElements();) {
			InvoiceEntries entry = (InvoiceEntries) enum.nextElement();
			long id = entry.getInvoiceEntryID();
			long partID = entry.getPartID();
			if (id <= 0 || partID <= 0)
				items.remove(entry);
		}
	}
	
	//	void updatePaymentSummary(Invoice inv) {
	//		if (inv.getInvoice() == 0)
	//			return;
	//		invoiceDetailPnl.updatePaymentInfo(inv);
	//
	//		rmk.gui.IScreen screen = rmk.ScreenController.getInstance()
	//				.getCustomerScreen(inv);
	//		if (screen != null){
	//			((CustomerScreen) screen).updateInvPrice(inv);
	//			this.requestFocus();
	////			((CustomerScreen) screen).setData(model);
	//		}
	//	}
	
	//==========================================================
	//==========================================================
	private void saveData() {
		//		model.removeActionListener(this);
		Customer cust = (Customer) customerPnl.getData();
		Invoice inv = invoiceDetailPnl.getData();
		if (editedInvoice) {
			if (inv.getDateShipped() != null
					&& !rmk.gui.Dialogs
					.yesConfirm("Confirm Changing shipped Invoice\nRemember: Information will not be checked for correctness."))
				return;
			
			if (rmk.gui.Dialogs.getInstance().dataErrors(
					sys.invoiceInfo.validate(inv)))
				return; // invalid data, don't save/continue
			
			Vector outputList = model.getInvoiceData();
			if (outputList == null || outputList.size() == 0) {
				ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
						+ "Generate new InvoiceDataVector ***");
				outputList = new Vector();
			}
			if (!outputList.contains(inv))
				outputList.add(inv);
			boolean newInv = false;
			if (inv.getID().intValue() == 0)
				newInv = true;
			
			Configuration.Config.getDB().saveItems("Invoice", outputList);
			
			String newTitle = "Invoice:" + inv.getID();
			newTitle += " (" + invoiceEntriesList.getTotalKnives() + ") Knives";
			updateTitle(newTitle);
			
			model.setInvoiceData(outputList);
			
			if (inv.getID().intValue() > 0) {
				loadItemList(model);
				editedInvoice = false;
				invoiceDetailPnl.setData(model);
				invoiceDetailPnl.updatePaymentInfo(inv);
				
				invoiceEntriesList.setData(model);
				invoiceDetailPnl.setEdited(false);
			}

			if (newInv)
				addEntry();
			
		}
		if (editedCustomer) {
			Vector outputList = new Vector();
			if (!outputList.contains(cust))
				outputList.add(cust);
			Configuration.Config.getDB().saveItems("Customers", outputList);
			double custDiscount = cust.getDiscount();
			if (custDiscount < 1)
				custDiscount *= 100;
			custDiscount = (int) custDiscount;
			double invDiscount = inv.getDiscountPercentage();
			if (invDiscount < 1)
				invDiscount *= 100;
			invDiscount = (int) invDiscount;
			if (custDiscount != invDiscount) {
				if (!Dialogs
						.yesConfirm("Confirm Changing INVOICE Discount from %"
								+ invDiscount + " to %" + custDiscount)) {
					ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
							+ "NOT changing discount");
					
				} else {
					//  		    invoiceDetailPnl.setDiscount(custDiscount);
					inv.setDiscountPercentage(custDiscount);
					ErrorLogger.getInstance().logMessage(this.getClass().getName()
							+ ":disc now %" + inv.getDiscountPercentage());
					
					outputList = model.getInvoiceData();
					if (outputList == null || outputList.size() == 0) {
						ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
								+ "Generate new InvoiceDataVector ***");
						outputList = new Vector();
					}
					if (!outputList.contains(inv))
						outputList.add(inv);
					Configuration.Config.getDB().saveItems("Invoice",
							outputList);
					
					model.setInvoiceData(outputList);
					invoiceDetailPnl.setData(model);
					invoiceEntriesList.setData(model);
					invoiceDetailPnl.setEdited(false);
				}
			}
			
			customerPnl.setData(model);
			editedCustomer = false;
			customerPnl.setEdited(false);
		}
		//TODO: Is this still needed?
//		rmk.Processing.updateScreens_Shipping(inv);
		DataModel.getInstance().invoiceInfo.logInvoiceAccess(inv);
		buttonBar.enableButton(0, editedInvoice || editedCustomer);
		buttonBar.enableButton(2, (invoiceNumber != 0));
		buttonBar.enableButton(3, (invoiceNumber != 0));
		loadItemList(model);
		setInvoice(inv);
		//		model.addActionListener(this);
	}
	
	private void saveEntry(Invoice inv, InvoiceEntries entry) {
		//		try {
		//			if (entry.getInvoiceEntryID() == 0)
		//				sys.financialInfo.addFeaturesToEntryPrice(entry);
		//		} catch (Exception e) {} // !!!!! If this happens we have real problems
		
		Vector entryVect = new Vector();
		entryVect.add(entry);
		entryVect = Configuration.Config.getDB().saveItems("InvoiceEntries",
				entryVect);
		
		// set features.entryID to match entries
		Vector features = entry.getFeatures();
		if (features != null && features.size() > 0) {
			boolean updates = false;
			for (java.util.Enumeration enum = features.elements(); enum
			.hasMoreElements();) {
				InvoiceEntryAdditions addition = (InvoiceEntryAdditions) enum
				.nextElement();
				if (addition.getEntryID() != entry.getInvoiceEntryID()) {
					// if not already set
					addition.setEntryID(entry.getInvoiceEntryID());
					updates = true;
				}
			}
			if (updates)
				Configuration.Config.getDB().saveItems("InvoiceEntryAdditions",
						features);
		}
		Vector invEntries = inv.getItems(); // first try to get from invoice
		if (invEntries == null)
			invEntries = model.getInvoiceItemsData(); // then from model
		
		if (invEntries == null)
			invEntries = new Vector(); // if all else fails, create new vector
		
		if (!invEntries.contains(entry)) // only add if not already there
			invEntries.addElement(entry);
		
		model.setInvoiceItemsData(invEntries); // update model
		inv.setItems(invEntries); // and invoice
		
		updateOccured((DBObject) inv, ScreenController.UPDATE_CHANGE, null );
		//		updatePaymentSummary(inv);
	}
	
	//----------------------------------------------------------
	private void addEntry() {
		Vector outputList = model.getInvoiceData();
		
		//		model.removeActionListener(this);
		
		model.setKnifeData(null);
		model.setInvoiceItemAttributesData(null);
		
		if (outputList.size() > 0) {
			Invoice inv = (Invoice) outputList.get(outputList.size() - 1);
			int dialogSelection = 0;
			if (inv.getDateShipped() != null
					&& !rmk.gui.Dialogs.shippedItemEditConfirm("Adding item")) {
				return;
			} else {
				// get/set initial model entry?...
				Vector newEntries;
				newEntries = rmk.gui.Dialogs.initialNewInvoiceEntry(this, inv,
						(Customer) customerPnl.getData(),
				"Model,feature,feature,...?");
				if (newEntries == null) {
					model.setKnifeData(null);
					IScreen itemScreen = rmk.ScreenController.getInstance().invoiceItem(
							inv.getInvoice(), 0, model);
					//					itemScreen.addActionListener(this);
				} else if (((InvoiceEntries) newEntries.get(0)).getPartID() == 0) {
					//					model.addActionListener(this);
					return;
				} else {
					invoiceDetailPnl.setVisible(false);
					invoiceEntriesList.expand(true);
					pack();
					Vector invEntries = inv.getItems(); // first try to get from invoice
					if (invEntries == null)
						invEntries = model.getInvoiceItemsData(); // then from model
					
					if (invEntries == null)
						invEntries = new Vector(); // if all else fails, create new vector
					
					// get entries
					while (newEntries != null) {
						InvoiceEntries entry = (InvoiceEntries) newEntries
						.get(0);
						if (entry.getPartID() == 0) // cancelled
							break;
						saveEntry(inv, entry);
						updateOccured((DBObject) entry, ScreenController.UPDATE_ADD, inv );
						//						updatePaymentSummary(inv);
						loadItemList(model);
						invoiceEntriesList.selectLast();
						
						model.setKnifeData(null);
						model.setInvoiceItemAttributesData(null);
						newEntries = rmk.gui.Dialogs
						.initialNewInvoiceEntry(this, inv,
								(Customer) customerPnl.getData(),
						"Model,feature,feature,...? (Enter Blank entry to quit.)");
						invoiceEntriesList.selectLast();
						
						String newTitle = "Invoice:" + inv.getID();
						newTitle += " (" + invoiceEntriesList.getTotalKnives() + ") Knives";
						updateTitle(newTitle);
						
						invoiceEntriesList.selectLast();
					}
					// reset info screen sizes
					invoiceDetailPnl.setVisible(true);
					invoiceEntriesList.expand(false);
					
					pack();
					invoiceEntriesList.requestFocus();
				}
			}
		}
		//		model.addActionListener(this);
	}
	
	//----------------------------------------------------------
	void editEntry(long entryID) {
		//		model.removeActionListener(this);
		InvoiceEntries item=null;
		Vector invoicesItems = model.getInvoiceItemsData();
		for (int invoicesItemsIndex = 0; invoicesItemsIndex < invoicesItems
		.size(); invoicesItemsIndex++) {
			item = (InvoiceEntries) invoicesItems
			.get(invoicesItemsIndex);
			if (item.getInvoiceEntryID() == entryID) {
				Vector data = new Vector();
				data.add(item);
				model.setKnifeData(data);
				break;
				//  		ErrorLogger.getInstance().logMessage(this.getClass().getName() + " edit:"+ data);
			}
		}
		
		//  	    addEntry(((Invoice)outputList.get(0)).getInvoice());
		//    	rmk.ScreenController.getInstance().invoiceItem(model);
		if(item != null){
			IScreen itemScreen = rmk.ScreenController.getInstance().invoiceItem(
					(long)item.getInvoice(), item.getInvoiceEntryID(), model);
			//			TODO: fix message passing
			//			itemScreen.addActionListener(this);
			
			//			model.addActionListener(this);
			itemScreen.bringToFront();
			itemScreen.grabFocus();
		} else{ 
			ErrorLogger.getInstance().logError("Unknown Invoice item #:" + entryID, new Exception());
		}
	}
	//------------------------------------------------------
	void removeEntry(long entryID) {
		Vector outputList = model.getInvoiceData();
		Invoice inv = null;
		if (outputList.size() > 0) {
			// get last entry, it will have id==0 if new
			inv = (Invoice) outputList.get(outputList.size() - 1);
			int dialogSelection = 0;
			if (inv.getDateShipped() != null
					&& !rmk.gui.Dialogs.shippedItemEditConfirm("REMOVING item"))
				return;
		}
		if (!rmk.gui.Dialogs
				.yesConfirm("Are you sure you wish to remove this item?"))
			return;
		//		model.removeActionListener(this);
		
		Vector invoicesItems = model.getInvoiceItemsData();
		for (int invoicesItemsIndex = 0; invoicesItemsIndex < invoicesItems
		.size(); invoicesItemsIndex++) {
			InvoiceEntries item = (InvoiceEntries) invoicesItems
			.get(invoicesItemsIndex);
			if (item.getInvoiceEntryID() == entryID) {
				sys.invoiceInfo.removeInvoiceEntryAndAdditions(entryID);
				invoicesItems.remove(item);
				if (inv != null) {
					Vector items = inv.getItems();
					items.remove(item);
				}
				loadItemList(model);
				model.setKnifeData(invoicesItems);
				inv.setItems(invoicesItems);
				updateOccured((DBObject) item, ScreenController.UPDATE_REMOVE, inv );
				//				updatePaymentSummary(inv);
			}
		}
		//		model.addActionListener(this);
	}
	
	//==========================================================
	public void internalFrameActivated(InternalFrameEvent e) {
		ApplicationMenu.getInstance().pushScreenToTopOfStack(this);
		//    	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Window Activated.");
		if (invoiceNumber == 0)
			invoiceDetailPnl.grabFocus();
		else {
			invoiceEntriesList.grabFocus();
			Invoice invoice = null;
			if (model.getInvoiceData() != null
					&& model.getInvoiceData().size() > 0) {
				invoice = (Invoice) model.getInvoiceData().get(
						model.getInvoiceData().size() - 1);
			}
			if (invoice != null) {
				String comment = sys.financialInfo.substituteInCCNum(invoice);
				String dispStr = "";
				int currIndex = 30;
				dispStr = comment;
				
				if (dispStr != null && dispStr.length() > 0){
					if(dispStr.equals(lastComment))
						return;
					JOptionPane.showMessageDialog(this, dispStr);
					lastComment = dispStr;
				}
			}
		}
		//  	("Internal frame activated", e);
	}
	
	//==========================================================
	public void actionPerformed(ActionEvent e) {
		if(!processHotKeys(e)){
			ErrorLogger.getInstance().TODO();
		}
	}

	public void processCommand(String command, Object from){
		boolean inShippingAddressField=false;
		ErrorLogger.getInstance().logDebugCommand(command);
		
		//-----------------------------
		if (command.equals("CANCEL")) { //cancel
			if (invoiceNumber == 0) { // remove 0 invoice from model
				java.util.Vector invoices = model.getInvoiceData();
				for (java.util.Enumeration enum = invoices.elements(); enum
				.hasMoreElements();) {
					Invoice inv = (Invoice) enum.nextElement();
					if (inv.getInvoice() == 0) {
						ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
								+ "removed Invoice:" + inv);
						
						invoices.remove(inv);
						break;
					}
				}
			}
			defaultCancelAction();
			//-----------------------------
//		} else if (command.equals("F1")) { //F1 - Panel1
//			customerPnl.requestFocus();
//		} else if (command.equals("F2")) { //F2 - Panel2
//			invoiceDetailPnl.requestFocus();
//		} else if (command.equals("F3")) { //F3 - Panel3
//			invoiceEntriesList.requestFocus();
//			
			//-----------------------------
		} else if (command.equals("INVOICECHANGED")) { //INVOICE CHANGED
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
					+ "Invoicechanged");
			editedInvoice = true;
			buttonBar.enableButton(0, true);
			return;
			//-----------------------------
		} else if (command.equals("INFOCHANGED")) { //Customer CHANGED
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
					+ "CustomerChanged");
			editedCustomer = true;
			buttonBar.enableButton(0, true);
			return;
			//-----------------------------
		} else if (command.equals("ADDINVOICEENTRY")) { // ADD INVOICE ENTRY
			ApplicationMenu.getInstance().pushScreenToTopOfStack(this);
			addEntry();
			//-----------------------------
		} else if (command.startsWith("EDITINVOICEENTRY")) {
			ApplicationMenu.getInstance().pushScreenToTopOfStack(this);
			long id = 0;
			//			TODO: get ID from command
			//			id = e.getID();
			editEntry(id);
			return;
			//-----------------------------
		} else if (command.equals("REMOVEINVOICEENTRY")) {
			long id = 0;
			//			TODO: get ID from command
			//			id = e.getID();
			removeEntry(id);
			//-----------------------------
		} else if (command.equals("INVOICE")) { //Invoice Display
			int format = HtmlReportDialog.LONG_FORMAT;
			Customer cust = (Customer) customerPnl.getData();
			if (cust.isDealer())
				format = HtmlReportDialog.SHORT_FORMAT;
			
			rmk.gui.Dialogs.report(HtmlReportDialog.INVOICE_REPORT, format,
					(int) invoiceNumber);
			//-----------------------------
		} else if (command.equals("ACKNOWLEDGMENT")) { //Acknowledgement Display
			int format = HtmlReportDialog.LONG_FORMAT;
			Customer cust = (Customer) customerPnl.getData();
			if (cust.isDealer())
				format = HtmlReportDialog.SHORT_FORMAT;
			
			rmk.gui.Dialogs.report(HtmlReportDialog.ACKNOWLEDGE_REPORT, format,
					(int) invoiceNumber);
			//-----------------------------
		} else if (command.equals("SAVE")) { //INFO CHANGED
			saveData();
			notifyListeners("INVOICE_SAVED");
			//-----------------------------
		} else if (command.equals("CTRL_ENTERKEY")) { // Force Save
			if (buttonBar.getButton(0).isEnabled()) {
				saveData();
				notifyListeners("INVOICE_SAVED");
			}
			//-----------------------------
		} else if (command.equals("PAYMENTS")) { //PaymentInfo Display
			gotoPaymentsScreen();
			//-----------------------------
		} else if (command.equals("INVOICEENTRYADDED")) { //Entry Added, reload list
			Vector invList = model.getInvoiceData();
			Invoice inv = (Invoice) invList.get(invList.size() - 1);
			Vector attList = inv.getItems();
			if (attList == null) {
				attList = new Vector();
				inv.setItems(attList);
			}
			//			InvoiceEntries entry = (InvoiceEntries) e.getSource();
			// TODO: verify this works
			InvoiceEntries entry = (InvoiceEntries) from;
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":attList.indexOf:"
					+ attList.indexOf(entry));
			
			attList.addElement(entry);
			updateOccured((DBObject) entry, ScreenController.UPDATE_ADD, inv );
			//			updatePaymentSummary(inv);
			
			loadItemList(model);
			//-----------------------------
		} else if (command.equals("INVOICEENTRYCHANGED")
				|| command.equals("ITEMSAVE")) { //INFO CHANGED
			Vector invList = model.getInvoiceData();
			//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Change - cnt "
			//					+ invList.size());
			Vector items = model.getInvoiceItemsData();
			removeBlankItems(items);
			loadItemList(model);
			Invoice detailInv = invoiceDetailPnl.getData();
			Invoice inv=null;
			for(Enumeration list = invList.elements(); list.hasMoreElements();){
				inv = (Invoice) list.nextElement();
				if(inv.getInvoice() == detailInv.getInvoice())
					break;
			}
			//			Invoice inv = (Invoice) invList.get(invList.size() - 1);
			inv.setItems(items);
			
			//			InvoiceEntries entry = ((InvoiceItemScreen) e.getSource()).getItem();
			// TODO: verify this works
			InvoiceEntries entry = (InvoiceEntries) from;
			updateOccured((DBObject) entry, ScreenController.UPDATE_EDIT, inv );
			//			updatePaymentSummary(inv);
			this.grabFocus();
			if (invoiceNumber == 0)
				invoiceDetailPnl.grabFocus();
			else
				invoiceEntriesList.grabFocus();
			//			invoiceEntriesList.selectedItem(lastItemID);
			
			//-----------------------------
		} else if (command.equals("DBMODELCHANGED-KNIFEDATA")) { //INFO CHANGED
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "|");
			loadItemList(model);

			//-----------------------------
		} else if (command.equals("LISTEXPAND")) {
			expandedList = !expandedList;
			//-----------------------------
		} else if (command.equals("ENTERKEY")) {
			Component currFocus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			inShippingAddressField = invoiceDetailPnl.isShippingAddressField(currFocus);
			//-----------------------------
		} else { // Undefined
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":UndefinedAction:"
					+ command + "|");
		}
		invoiceEntriesList.expand(expandedList);
		invoiceDetailPnl.setVisible(!expandedList);
		
		if(inShippingAddressField){
			Component currFocus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			currFocus.requestFocus();
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Move back to AddressField???");
		}
		
	}
	
	void shipInvoice(){
		Invoice inv = invoiceDetailPnl.getData();
		if (inv == null)
			return;
		GregorianCalendar shipDate = inv.getDateShipped();
		if (shipDate == null) { // need to ship
			shipDate = rmk.gui.Dialogs.getDate("Shipping Date",
					new GregorianCalendar());
			long validation = rmk.Processing.getInstance().validateInvoice(inv.getInvoice());
			if(validation != 0){
				rmk.Processing.getInstance().shipInvoice(inv, shipDate);
				buttonBar.setButtonLabel(4, "Un'Ship");
			}
		} else {
			rmk.Processing.getInstance().unShipInvoice(inv);
			buttonBar.setButtonLabel(4, "Ship");
		}
	}
	
	void updateTitle(String newTitle){
		String oldTitle=this.getTitle();
		if(!oldTitle.equalsIgnoreCase(newTitle)){
			setTitle(newTitle);
			ApplicationMenu.getInstance().updateScreenTitle(oldTitle, newTitle);
		}
	}
	
	public void gotoPaymentsScreen(){
		Vector invList = model.getInvoiceData();
		Invoice invoice = (Invoice) invList.get(invList.size() - 1);
		
		rmk.gui.IScreen screen = rmk.ScreenController.getInstance()
		.getPaymentsScreen(invoice);
		Vector paymentInfo = rmk.DataModel.getInstance().financialInfo
		.getInvoicePayments((int) invoiceNumber);
		model.setPaymentsData(paymentInfo);
		if (screen == null) {
			rmk.ScreenController.getInstance().invoicePayments(model);
		} else {
			screen.setData(model);
			screen.bringToFront();
		}
	}
	
	
	public void updateOccured(DBObject itemChanged, int changeType, DBObject parentItem){
		String parentName="";
		if(parentItem != null) parentName = parentItem.getClass().getName();
		String itemName="";
		if(itemChanged != null) itemName = itemChanged.getClass().getName();

		switch(changeType){
		case ScreenController.UPDATE_EDIT:
		{				
			if(parentName.indexOf(".Invoice") > 0 || itemName.indexOf(".Invoice") > 0){
				Invoice invoice = (Invoice) parentItem;
				editedInvoice = invoice.isEdited();
				setInvoice(invoice);
			}else
				ErrorLogger.getInstance().TODO();
		}
		break;
		
		case ScreenController.UPDATE_CHANGE:
		{
			Vector invList = model.getInvoiceData();
			Vector items = model.getInvoiceItemsData();
			removeBlankItems(items);
			loadItemList(model);
			Invoice detailInv = invoiceDetailPnl.getData();
			Invoice inv=null;
			for(Enumeration list = invList.elements(); list.hasMoreElements();){
				inv = (Invoice) list.nextElement();
				if(inv.getInvoice() == detailInv.getInvoice())
					break;
			}
			inv.setItems(items);
			
			setInvoice(inv);

			// TODO: notify other screens?
//			updateOccured((DBObject) inv, ScreenController.UPDATE_EDIT, (DBObject) customerPnl.getData() );
			this.grabFocus();
			if (invoiceNumber == 0)
				invoiceDetailPnl.grabFocus();
			else
				invoiceEntriesList.grabFocus();
		}
		break;
		case ScreenController.UPDATE_REMOVE:
			if(itemName.indexOf("InvoiceEntries") > 0 && parentName.indexOf("Invoice") > 0)
				setInvoice((Invoice) parentItem);
			else
				ErrorLogger.getInstance().TODO();
		break;
		case ScreenController.UPDATE_ADD:
			if(itemChanged.getClass().getName().indexOf("InvoiceEntries") > 0)				
				setInvoice((Invoice) parentItem);
			else
				ErrorLogger.getInstance().TODO();
		break;
		

		default:
			ErrorLogger.getInstance().TODO();
		}
	}
	
	public void buttonPress(int button, int id) {
		Invoice invoice = invoiceDetailPnl.getData();

		switch(button){
		case ScreenController.BUTTON_CANCEL:
			if (invoiceNumber == 0) { // remove 0 invoice from model
				java.util.Vector invoices = model.getInvoiceData();
				for (java.util.Enumeration enum = invoices.elements(); enum
				.hasMoreElements();) {
					Invoice inv = (Invoice) enum.nextElement();
					if (inv.getInvoice() == 0) {
						ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
								+ "removed Invoice:" + inv);
						
						invoices.remove(inv);
						break;
					}
				}
			}
			defaultCancelAction();
			SignalProcessor.getInstance().removeScreen(this);
		break;
		case ScreenController.BUTTON_ADD:
		{
			addEntry();
			buttonBar.enableButton(0, true);
		}
		break;
		case ScreenController.BUTTON_REMOVE:
		{
			removeEntry(id);
			buttonBar.enableButton(0, true);
		}
		break;
		
		case ScreenController.BUTTON_SELECTION_DETAILS:
		{
			ApplicationMenu.getInstance().pushScreenToTopOfStack(this);
			editEntry(id);
		}
		break;
		
		case ScreenController.BUTTON_SAVE:
		{
			saveData();
		}
		break;
		
		case ScreenController.BUTTON_KNIFE_COUNT:
		{
			Dialogs.showKnifeCounts(invoice.getDateEstimated());
		}
		break;
		
		case ScreenController.BUTTON_DISPLAY_INVOICE:
		{
			int format = HtmlReportDialog.LONG_FORMAT;
			Customer cust = (Customer) customerPnl.getData();
			if (cust.isDealer())
				format = HtmlReportDialog.SHORT_FORMAT;
			
			rmk.gui.Dialogs.report(HtmlReportDialog.INVOICE_REPORT, format,
					(int) invoiceNumber);
		}
		break;
		
		case ScreenController.BUTTON_DISPLAY_ACK_RPT:
		{
			int format = HtmlReportDialog.LONG_FORMAT;
			Customer cust = (Customer) customerPnl.getData();
			if (cust.isDealer())
				format = HtmlReportDialog.SHORT_FORMAT;
			
			rmk.gui.Dialogs.report(HtmlReportDialog.ACKNOWLEDGE_REPORT, format,
					(int) invoiceNumber);
		}
		break;
		
		case ScreenController.BUTTON_SHIP:
		{
			shipInvoice();
		}
		break;
		
		
		case ScreenController.BUTTON_F1:
			customerPnl.requestFocus();
		break;
		case ScreenController.BUTTON_F2:
			invoiceDetailPnl.requestFocus();
		break;
		case ScreenController.BUTTON_F3:
			invoiceEntriesList.requestFocus();
		case ScreenController.BUTTON_F5: // display customer info
		{
			rmk.gui.IScreen screen = rmk.ScreenController.getInstance().getCustomerScreen(invoice);
			
			if (screen == null) {
				rmk.ScreenController.getInstance().displayCustomer(
						invoice.getCustomerID());
			} else {
				screen.setData(model);
				screen.bringToFront();
			}
		}
		break;
		
		case ScreenController.BUTTON_F6: // display customer info
		{
			gotoPaymentsScreen();
		}		
		break;
		
		default:
			System.out.println("InvoiceDetailsScreen buttonPress processing unimplemented:" + button);
		}
	}
	
	
	
}