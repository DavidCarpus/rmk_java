package rmk.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.GregorianCalendar;

//import rmk.DataModel;
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
	
	Invoice currInvoice=null;
	
	//    double originalInvoiceTaxes=0;
	
	//==========================================================
	public InvoiceDetailsScreen() {
		super("Invoice Details");
		
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		customerPnl = new rmk.gui.ScreenComponents.CustomerInfoPanel();
		customerPnl.setParentScreen(this);
		//		customerPnl.addActionListener(this);
		getContentPane().add(customerPnl);
		
		invoiceDetailPnl = new rmk.gui.ScreenComponents.InvoiceDetailsPanel();
		invoiceDetailPnl.onPaymentsScreen(false);
		invoiceDetailPnl.setParentScreen(this);
		//		invoiceDetailPnl.addActionListener(this);
		getContentPane().add(invoiceDetailPnl);
		invoiceDetailPnl.setEnabled(false);
		
		invoiceEntriesList = new rmk.gui.ScreenComponents.InvoiceEntriesListPanel();
		invoiceEntriesList.setParentScreen(this);
		//		invoiceEntriesList.addActionListener(this);
		getContentPane().add(invoiceEntriesList);
		
		getContentPane().add(buttonBar);
		buttonBar.addButton(null, "Invoice", "Invoice", "Invoice");
		buttonBar.addButton(null, "Acknowledgment", "Acknowledgment",
		"Acknowledgment");
		buttonBar.addButton(null, "Ship", "Ship", "Ship This Invoice");
		buttonBar.getButton(0).setForeground(new Color(255, 12, 11));
		buttonBar.getButton(0).setMnemonic(KeyEvent.VK_V); // Save Button
		buttonBar.getButton(2).setMnemonic(KeyEvent.VK_I); // Invoice Button
		buttonBar.getButton(3).setMnemonic(KeyEvent.VK_K); // Acknowledgment Button
		buttonBar.getButton(4).setMnemonic(KeyEvent.VK_P); // Ship Button
//		ButtonBarTranslator translator = new ButtonBarTranslator(this, buttonBar);
		
		KeyStroke listExpand = KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F11, 0);
		this.registerKeyboardAction(this, "F11", listExpand,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		
    	SignalProcessor.getInstance().addScreen(this);

        dataPanels[0] = customerPnl;
        dataPanels[1] = invoiceDetailPnl;
        dataPanels[2] = invoiceEntriesList;
 
		setPreferredSize(new Dimension(915, 640));
		pack();
	}

	//==========================================================
	public boolean isEdited() {
		return (editedInvoice || editedCustomer);
	}
	
	//==========================================================
	public void setData(DBObject item){
		if(((Invoice) item).getParent() == null)
			ErrorLogger.getInstance().TODO();
		setInvoice((Invoice) item);
	}
	
	public void setInvoice(Invoice invoice){

		invoiceNumber = invoice.getInvoice();
		currInvoice = invoice;
		
		customerPnl.setData(invoice.getParent());
		invoiceDetailPnl.setData(invoice);
		
		String newTitle = "Invoice:" + invoice.getID();
		newTitle += " (" + invoiceEntriesList.getTotalKnives() + ") Knives";
		updateTitle(newTitle);
		
		loadItemList(invoice, invoice.getItems());
//		loadItemList(model.getInvoiceItemsData());
		invoiceDetailPnl.updatePaymentInfo(invoice);
		
		String shipButtonLabel = "Ship";
		if (invoice.getDateShipped() != null)
			shipButtonLabel = "'Un'Ship";
		buttonBar.getButton(4).setText(shipButtonLabel);
		
		buttonBar.enableButton(2, (invoiceNumber != 0));
		buttonBar.enableButton(3, (invoiceNumber != 0));
		if (invoiceNumber > 0) {
			sys.invoiceInfo.logInvoiceAccess(invoice);
		}
	}
	
	//----------------------------------------------------------
//	void loadItemList(DBGuiModel model) {
	void loadItemList(Invoice inv, java.util.Vector items) {
//		java.util.Vector items = model.getInvoiceItemsData();
		removeBlankItems(items);
		// sort by invEntryID
		//		InvoiceEntries
		if(items != null){
			Object[] itemArray = items.toArray();
			Arrays.sort(itemArray, new rmk.comparators.InvoiceEntries());		
			Vector sortedData = new Vector();
			for(int i=0; i< itemArray.length; i++){
				sortedData.add(itemArray[i]);
			}
			items = sortedData;
//			model.setInvoiceItemsData(items);
		}
		
		invoiceEntriesList.setDataInvItems(inv, items);
		
//		invoiceEntriesList.setData(model);
//		Invoice inv = null;
//		Invoice inv = ((InvoiceEntries)items.get(0)).getParent();
		if (inv == null || inv.getInvoice() == 0) {
			buttonBar.enableButton(0, true);
			editedInvoice = true;
		}
		
		if (inv != null) {
			int knifeCnt = invoiceEntriesList.getTotalKnives();
			invoiceDetailPnl.setData(inv);
			invoiceDetailPnl.setTotalKnives(knifeCnt);
			
			String newTitle = "Invoice:" + inv.getID();
			newTitle += " (" + knifeCnt + ") Knives";
			updateTitle(newTitle);
		}
		
		buttonBar.enableButton(0, editedInvoice || editedCustomer);
		pack();
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
	
	//==========================================================
	//==========================================================
	private void saveData(Invoice inv) {
		//		model.removeActionListener(this);
		Customer cust;
		if(currInvoice != null && currInvoice.getParent() != null)
			cust = currInvoice.getParent();
		else
			cust = (Customer) customerPnl.getData();

		if (editedInvoice) {
			if (inv.getDateShipped() != null
					&& !rmk.gui.Dialogs
					.yesConfirm("Confirm Changing shipped Invoice\nRemember: Information will not be checked for correctness."))
				return;
			
			if (rmk.gui.Dialogs.getInstance().dataErrors(
					sys.invoiceInfo.validate(inv)))
				return; // invalid data, don't save/continue
				
			boolean newInv = false;
			if (inv.getID().intValue() == 0)
				newInv = true;
			
			Vector saveList = new Vector();
			saveList.add(inv);
			Configuration.Config.getDB().saveItems("Invoice", saveList);
			
			currInvoice = inv;
			
			String newTitle = "Invoice:" + currInvoice.getID();
			newTitle += " (" + invoiceEntriesList.getTotalKnives() + ") Knives";
			updateTitle(newTitle);
			
//			model.setInvoiceData(outputList);
			
			if (currInvoice.getInvoice() > 0) {
				loadItemList(currInvoice, currInvoice.getItems());
				editedInvoice = false;
				invoiceDetailPnl.setData(currInvoice);
				invoiceDetailPnl.updatePaymentInfo(currInvoice);
				invoiceEntriesList.setDataInvItems( currInvoice,  currInvoice.getItems());
//				invoiceEntriesList.setData(model);
				invoiceDetailPnl.setEdited(false);
			} else{
				ErrorLogger.getInstance().logMessage("Error creating invoice for :" + cust);
			}

			Vector custInvoices = cust.getInvoices();
			if(custInvoices == null || !custInvoices.contains(currInvoice)){
				cust.addInvoice(currInvoice);
//				custInvoices.add(currInvoice);
			}
			
			updateCustomerScreen(cust, currInvoice);

			if (newInv){
				addEntry(currInvoice);
			}
		}
		if (editedCustomer) {
//			Vector outputList = new Vector();
			{
				Vector saveList = new Vector();
				saveList.add(cust);
//				if (!outputList.contains(cust))
//					outputList.add(cust);
				Configuration.Config.getDB().saveItems("Customers", saveList);
			}

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
					
					Vector saveList = new Vector();
					saveList.add(inv);
					Configuration.Config.getDB().saveItems("Invoice",
							saveList);
					
//					model.setInvoiceData(outputList);
					invoiceDetailPnl.setData(inv);
					invoiceEntriesList.setData(inv.getItems());
					invoiceDetailPnl.setEdited(false);
				}
			}
			
			customerPnl.setData(cust);
			editedCustomer = false;
			customerPnl.setEdited(false);
		}

		sys.invoiceInfo.logInvoiceAccess(currInvoice);
		buttonBar.enableButton(0, editedInvoice || editedCustomer);
		buttonBar.enableButton(2, (invoiceNumber != 0));
		buttonBar.enableButton(3, (invoiceNumber != 0));

		loadItemList(currInvoice, currInvoice.getItems());
		setInvoice(inv);
	}
	
	void updateCustomerScreen(Customer cust, Invoice inv){
		CustomerScreen screen = (CustomerScreen) rmk.ScreenController.getInstance().getCustomerScreen(currInvoice.getCustomerID());
		
		if (screen != null) {
			screen.setData(cust, cust.getCurrentAddressItem(), cust.getInvoices());
		}		
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
//		if (invEntries == null)
//			invEntries = model.getInvoiceItemsData(); // then from model
		
		if (invEntries == null)
			invEntries = new Vector(); // if all else fails, create new vector
		
		if (!invEntries.contains(entry)) // only add if not already there
			invEntries.addElement(entry);
		
//		model.setInvoiceItemsData(invEntries); // update model
		inv.setItems(invEntries); // and invoice
		
		updateOccured((DBObject) inv, ScreenController.UPDATE_EDIT, null );
		//		updatePaymentSummary(inv);
	}
	
	//----------------------------------------------------------
	private void addEntry(Invoice inv) {
//		Vector outputList = model.getInvoiceData();
		
		//		model.removeActionListener(this);
		
//		model.setKnifeData(null);
//		model.setInvoiceItemAttributesData(null);
		
		if (inv != null) {
			int dialogSelection = 0;
			if (inv.getDateShipped() != null
					&& !rmk.gui.Dialogs.shippedItemEditConfirm("Adding item")) {
				return;
			} else {
				// get/set initial model entry?...
				InvoiceEntries newEntry = rmk.gui.Dialogs.initialNewInvoiceEntry(this, inv,
						(Customer) customerPnl.getData(),
				"Model,feature,feature,...?");
				if (newEntry == null) {
					return;
				}
					//					itemScreen.addActionListener(this);
				if (newEntry.getPartID() == 0) { 
					// No entry but also NOT cancelled 
					newEntry.setParent(inv);
					ScreenController.getInstance().invoiceItem(newEntry, inv.getInvoice(),newEntry.getInvoiceEntryID());
					return;
				} else {
					invoiceDetailPnl.setVisible(false);
					invoiceEntriesList.expand(true);
					pack();
					Vector invEntries = inv.getItems(); // first try to get from invoice
//					if (invEntries == null)
//						invEntries = model.getInvoiceItemsData(); // then from model
					
					if (invEntries == null)
						invEntries = new Vector(); // if all else fails, create new vector
					
					// get entries
					while (newEntry != null) {

						if (newEntry.getPartID() == 0) // cancelled
							break;
						saveEntry(inv, newEntry);
						updateOccured((DBObject) newEntry, ScreenController.UPDATE_ADD, inv );
						//						updatePaymentSummary(inv);
//						loadItemList(model);
						loadItemList(inv, inv.getItems());
						invoiceEntriesList.selectLast();
						
//						model.setKnifeData(null);
//						model.setInvoiceItemAttributesData(null);
						newEntry = rmk.gui.Dialogs
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
		Vector invoicesItems = currInvoice.getItems();
		for (int invoicesItemsIndex = 0; invoicesItemsIndex < invoicesItems
		.size(); invoicesItemsIndex++) {
			item = (InvoiceEntries) invoicesItems
			.get(invoicesItemsIndex);
			if (item.getInvoiceEntryID() == entryID) {
				Vector data = new Vector();
				data.add(item);
//				model.setKnifeData(data);
				break;
			}
		}
		
		if(item != null){
			IScreen itemScreen = rmk.ScreenController.getInstance().invoiceItem(item,
					(long)item.getInvoice(), item.getInvoiceEntryID());
			itemScreen.bringToFront();
			itemScreen.grabFocus();
		} else{ 
			ErrorLogger.getInstance().logError("Unknown Invoice item #:" + entryID, new Exception());
		}
	}
	//------------------------------------------------------
	void removeEntry(long entryID) {
//		int dialogSelection = 0;
		if (currInvoice.getDateShipped() != null
				&& !rmk.gui.Dialogs.shippedItemEditConfirm("REMOVING item"))
				return;

		if (!rmk.gui.Dialogs
				.yesConfirm("Are you sure you wish to remove this item?"))
			return;
		//		model.removeActionListener(this);
		
		Vector invoicesItems = currInvoice.getItems();
		for (int invoicesItemsIndex = 0; invoicesItemsIndex < invoicesItems
		.size(); invoicesItemsIndex++) {
			InvoiceEntries item = (InvoiceEntries) invoicesItems
			.get(invoicesItemsIndex);
			if (item.getInvoiceEntryID() == entryID) {
				sys.invoiceInfo.removeInvoiceEntryAndAdditions(entryID);
				invoicesItems.remove(item);
//				if (inv != null) {
//					Vector items = inv.getItems();
//					items.remove(item);
//				}
//				loadItemList(model);
				loadItemList(currInvoice, currInvoice.getItems());
//				model.setKnifeData(invoicesItems);
				currInvoice.setItems(invoicesItems);
				updateOccured((DBObject) item, ScreenController.UPDATE_REMOVE, currInvoice );
				//				updatePaymentSummary(inv);
			}
		}
		updateCustomerScreen(currInvoice.getParent(), currInvoice);

		//		model.addActionListener(this);
	}
	
	//==========================================================
	public void internalFrameActivated(InternalFrameEvent e) {
		ApplicationMenu.getInstance().pushScreenToTopOfStack(this);
		
		Invoice invoice = invoiceDetailPnl.getData();
		
		if (invoiceNumber == 0)
			invoiceDetailPnl.grabFocus();
		else {
			invoiceEntriesList.grabFocus();
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
	}
	
	//==========================================================
	public void actionPerformed(ActionEvent e) {
		if(!processHotKeys(e)){
			ErrorLogger.getInstance().TODO();
			ErrorLogger.getInstance().logMessage("" + e.getActionCommand().toUpperCase().trim());
		}
	}

	public void processCommand(String command, Object from){
		boolean inShippingAddressField=false;
		ErrorLogger.getInstance().logDebugCommand(command);
		ErrorLogger.getInstance().TODO();

////			
//			//-----------------------------
//		} else if (command.equals("INVOICECHANGED")) { //INVOICE CHANGED
//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
//					+ "Invoicechanged");
//			editedInvoice = true;
//			buttonBar.enableButton(0, true);
//			return;
//			//-----------------------------
//		} else if (command.equals("INFOCHANGED")) { //Customer CHANGED
//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
//					+ "CustomerChanged");
//			editedCustomer = true;
//			buttonBar.enableButton(0, true);
//			return;
//			//-----------------------------
//		} else if (command.equals("ADDINVOICEENTRY")) { // ADD INVOICE ENTRY
//			ApplicationMenu.getInstance().pushScreenToTopOfStack(this);
//			addEntry();
//			//-----------------------------
//		} else if (command.startsWith("EDITINVOICEENTRY")) {
//			ApplicationMenu.getInstance().pushScreenToTopOfStack(this);
//			long id = 0;
//			//			TODO: get ID from command
//			//			id = e.getID();
//			editEntry(id);
//			return;
//			//-----------------------------
//		} else if (command.equals("REMOVEINVOICEENTRY")) {
//			long id = 0;
//			//			TODO: get ID from command
//			//			id = e.getID();
//			removeEntry(id);
//			//-----------------------------
//		} else if (command.equals("INVOICE")) { //Invoice Display
//			int format = HtmlReportDialog.LONG_FORMAT;
//			Customer cust = (Customer) customerPnl.getData();
//			if (cust.isDealer())
//				format = HtmlReportDialog.SHORT_FORMAT;
//			
//			rmk.gui.Dialogs.report(HtmlReportDialog.INVOICE_REPORT, format,
//					(int) invoiceNumber);
//			//-----------------------------
//		} else if (command.equals("ACKNOWLEDGMENT")) { //Acknowledgement Display
//			int format = HtmlReportDialog.LONG_FORMAT;
//			Customer cust = (Customer) customerPnl.getData();
//			if (cust.isDealer())
//				format = HtmlReportDialog.SHORT_FORMAT;
//			
//			rmk.gui.Dialogs.report(HtmlReportDialog.ACKNOWLEDGE_REPORT, format,
//					(int) invoiceNumber);
//			//-----------------------------
//		} else if (command.equals("SAVE")) { //INFO CHANGED
//			saveData();
//			notifyListeners("INVOICE_SAVED");
//			//-----------------------------
//		} else if (command.equals("CTRL_ENTERKEY")) { // Force Save
//			if (buttonBar.getButton(0).isEnabled()) {
//				saveData();
//				notifyListeners("INVOICE_SAVED");
//			}
//			//-----------------------------
//		} else if (command.equals("PAYMENTS")) { //PaymentInfo Display
//			gotoPaymentsScreen();
//			//-----------------------------
//		} else if (command.equals("INVOICEENTRYADDED")) { //Entry Added, reload list
//			Vector invList = model.getInvoiceData();
//			Invoice inv = (Invoice) invList.get(invList.size() - 1);
//			Vector attList = inv.getItems();
//			if (attList == null) {
//				attList = new Vector();
//				inv.setItems(attList);
//			}
//			//			InvoiceEntries entry = (InvoiceEntries) e.getSource();
//			// TODO: verify this works
//			InvoiceEntries entry = (InvoiceEntries) from;
//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":attList.indexOf:"
//					+ attList.indexOf(entry));
//			
//			attList.addElement(entry);
//			updateOccured((DBObject) entry, ScreenController.UPDATE_ADD, inv );
//			//			updatePaymentSummary(inv);
//			
////			loadItemList(model);
//			loadItemList(inv.getItems());
//			//-----------------------------
//		} else if (command.equals("INVOICEENTRYCHANGED")
//				|| command.equals("ITEMSAVE")) { //INFO CHANGED
//			Vector invList = model.getInvoiceData();
//			//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Change - cnt "
//			//					+ invList.size());
//			Vector items = model.getInvoiceItemsData();
//			removeBlankItems(items);
////			loadItemList(model);
//			loadItemList(items);
//			
//			Invoice detailInv = invoiceDetailPnl.getData();
//			Invoice inv=null;
//			for(Enumeration list = invList.elements(); list.hasMoreElements();){
//				inv = (Invoice) list.nextElement();
//				if(inv.getInvoice() == detailInv.getInvoice())
//					break;
//			}
//			//			Invoice inv = (Invoice) invList.get(invList.size() - 1);
//			inv.setItems(items);
//			
//			//			InvoiceEntries entry = ((InvoiceItemScreen) e.getSource()).getItem();
//			// TODO: verify this works
//			InvoiceEntries entry = (InvoiceEntries) from;
//			updateOccured((DBObject) entry, ScreenController.UPDATE_EDIT, inv );
//			//			updatePaymentSummary(inv);
//			this.grabFocus();
//			if (invoiceNumber == 0)
//				invoiceDetailPnl.grabFocus();
//			else
//				invoiceEntriesList.grabFocus();
//			//			invoiceEntriesList.selectedItem(lastItemID);
//			
//			//-----------------------------
//		} else if (command.equals("DBMODELCHANGED-KNIFEDATA")) { //INFO CHANGED
//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "|");
////			loadItemList(model);
//			Invoice detailInv = invoiceDetailPnl.getData();
//			loadItemList(detailInv.getItems());
////			loadItemList(model);
//
//			//-----------------------------
//		} else if (command.equals("LISTEXPAND")) {
//			expandedList = !expandedList;
//			//-----------------------------
//		} else if (command.equals("ENTERKEY")) {
//			Component currFocus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
//			inShippingAddressField = invoiceDetailPnl.isShippingAddressField(currFocus);
//			//-----------------------------
//		} else { // Undefined
//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":UndefinedAction:"
//					+ command + "|");
//		}
//		invoiceEntriesList.expand(expandedList);
//		invoiceDetailPnl.setVisible(!expandedList);
//		
//		if(inShippingAddressField){
//			Component currFocus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
//			currFocus.requestFocus();
//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Move back to AddressField???");
//		}
		
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
	
	public void gotoPaymentsScreen(Invoice invoice, Vector payments){
//		Vector invList = model.getInvoiceData();
//		Invoice invoice = (Invoice) invList.get(invList.size() - 1);
		
		rmk.gui.IScreen screen = rmk.ScreenController.getInstance()
		.getPaymentsScreen(invoice);
//		Vector payments = sys.financialInfo.getInvoicePayments((int) invoiceNumber);
//		model.setPaymentsData(paymentInfo);
		if (screen == null) {
			rmk.ScreenController.getInstance().invoicePayments(invoice, payments);
		} else {
			((InvoicePaymentsScreen)screen).setData(invoice, payments);
			screen.bringToFront();
		}
	}
	
	
	public void updateOccured(DBObject itemChanged, int changeType, DBObject parentItem){
		String parentName="";
		if(parentItem != null) parentName = parentItem.getClass().getName();
		String itemName="";
		if(itemChanged != null) itemName = itemChanged.getClass().getName();

		ErrorLogger.getInstance().logUpdate(itemChanged,changeType,parentItem);

		switch(changeType){
		case ScreenController.UPDATE_EDIT:
		{				
			if(parentName.indexOf(".Invoice") > 0 || itemName.indexOf(".Invoice") > 0){
				Invoice changedInvoice = (Invoice) parentItem;
				if(changedInvoice == null) changedInvoice = (Invoice) itemChanged;
				
				if(itemName.indexOf("InvoiceEntries") > 0){
//					changedInvoice.getItems().remove(itemChanged);
					// item changed was knive entry, don't need to force a save just for this
				} else{
					editedInvoice = changedInvoice.isEdited();
				}
				Invoice thisScreensInvoice = invoiceDetailPnl.getData();
				if(changedInvoice.getInvoice() == thisScreensInvoice.getInvoice())
					setInvoice(changedInvoice);
			} else if(parentName.indexOf(".Customer") > 0 ){
				editedCustomer = true;
				buttonBar.getButton(0).setEnabled(true);
			}else
				ErrorLogger.getInstance().TODO();
		}
		break;
		
		case ScreenController.UPDATE_CHANGE:
		{
//			Vector invList = model.getInvoiceData();
//			Vector items = model.getInvoiceItemsData();
			Vector items = currInvoice.getItems();
			removeBlankItems(items);
			Invoice invoice=null;
			if(items != null && items.size()>0) invoice=((InvoiceEntries) items.get(0)).getParent();
			if(invoice == null) invoice = currInvoice;
//			loadItemList(model);
			loadItemList(invoice, items);
			Invoice detailInv = invoiceDetailPnl.getData();
//			Invoice inv=null;
//			for(Enumeration list = invList.elements(); list.hasMoreElements();){
//				inv = (Invoice) list.nextElement();
//				if(inv.getInvoice() == detailInv.getInvoice())
//					break;
//			}
			currInvoice.setItems(items);
			
			setInvoice(currInvoice);

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
			if(itemName.indexOf("InvoiceEntries") > 0)				
				setInvoice((Invoice) parentItem);
			else
				ErrorLogger.getInstance().TODO();
		break;
		
		case ScreenController.ENTER_KEY:
		{
			Component currFocus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			if(invoiceDetailPnl.isShippingAddressField(currFocus)){					
				currFocus.requestFocus();
				ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Move back to AddressField???");
			} else{
				KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(currFocus);
//				ErrorLogger.getInstance().TODO();
			}
		}
		break;

		case ScreenController.UPDATE_SAVE:
		{
			saveData(invoiceDetailPnl.getData());
		}
		break;

		default:
			ErrorLogger.getInstance().TODO();
		}
	}
	
	public void buttonPress(int button, int id) {
//		Invoice invoice = invoiceDetailPnl.getData();

		ErrorLogger.getInstance().logButton(button, id);
		
		switch(button){
		case ScreenController.BUTTON_CANCEL:
			if (invoiceNumber == 0) { // remove 0 invoice from model
//				java.util.Vector invoices = model.getInvoiceData();				
//				for (java.util.Enumeration enum = invoices.elements(); enum
//				.hasMoreElements();) {
//					Invoice inv = (Invoice) enum.nextElement();
//					if (inv.getInvoice() == 0) {
//						ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
//								+ "removed Invoice:" + inv);
//						
//						invoices.remove(inv);
//						break;
//					}
//				}
				currInvoice.getParent().getInvoices().remove(currInvoice);
			}
			defaultCancelAction();
			SignalProcessor.getInstance().removeScreen(this);
		break;
		case ScreenController.BUTTON_ADD:
		{
			addEntry(currInvoice);
//			buttonBar.enableButton(0, true);
		}
		break;
		case ScreenController.BUTTON_REMOVE:
		{
			removeEntry(id);
//			buttonBar.enableButton(0, true);
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
			currInvoice = invoiceDetailPnl.getData();
			saveData(currInvoice);
		}
		break;
		
		case ScreenController.BUTTON_KNIFE_COUNT:
		{
			Dialogs.showKnifeCounts(currInvoice.getDateEstimated());
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
		
		
//		case ScreenController.BUTTON_F1:
//			customerPnl.requestFocus();
//		break;
//		case ScreenController.BUTTON_F2:
//			invoiceDetailPnl.requestFocus();
//		break;
//		case ScreenController.BUTTON_F3:
//			invoiceEntriesList.requestFocus();
//		break;
		
		case ScreenController.BUTTON_F5: // display customer info
		{
			rmk.gui.IScreen screen = rmk.ScreenController.getInstance().getCustomerScreen(currInvoice.getCustomerID());
			
			if (screen == null) {
				rmk.ScreenController.getInstance().displayCustomer(
						currInvoice.getCustomerID());
			} else {
				Customer customer;
				try {
					customer = sys.customerInfo.getCustomerByID(currInvoice.getCustomerID());
					rmk.database.dbobjects.Address address=null;
					if(address == null && customer.getCurrentAddress() > 0)
						address = sys.customerInfo.getCustomerAddress(customer.getCurrentAddress());
					if(address == null )
						address = sys.customerInfo.getCurrentAddress(customer.getCustomerID());			
					if (address == null)
						address = new rmk.database.dbobjects.Address(0);
					
					customer.setCurrentAddressItem(address);
					((CustomerScreen)screen).setData(customer, customer.getCurrentAddressItem(), customer.getInvoices());
					screen.bringToFront();
				} catch (Exception e) {
					ErrorLogger.getInstance().logError("Fetching customer:"+currInvoice.getCustomerID(), e);
				}
			}
		}
		break;
		
		case ScreenController.BUTTON_F6:
			; // ignore, already on invoice screen
		break;
		
		case ScreenController.BUTTON_F7: // display customer info
		{
			Vector payments = sys.financialInfo.getInvoicePayments(currInvoice.getInvoice());
			gotoPaymentsScreen(currInvoice,payments);
		}		
		break;
		
		case ScreenController.BUTTON_F11: // display customer info
		{
			expandedList = !expandedList;
			invoiceEntriesList.expand(expandedList);
			invoiceDetailPnl.setVisible(!expandedList);
		}		
		break;
		
		default:
	       	ErrorLogger.getInstance().logButton(button, id);
		}
	}

	public Invoice getInvoice() {
		return currInvoice;
	}
	
	
	
}