package rmk.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Vector;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.SignalProcessor;
import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.Address;
import rmk.database.dbobjects.DBObject;
import rmk.database.dbobjects.Invoice;

public class CustomerScreen extends Screen{
	static int counter;
	ActionListener parentFrame;    
	rmk.gui.ScreenComponents.CustomerInfoPanel custPanel;
	rmk.gui.ScreenComponents.CustomerAddressPanel custAddPanel;
	rmk.gui.ScreenComponents.InvoiceListPanel invoicePanel;
	rmk.gui.ScreenComponents.CustomerDetailPanel detailPanel;
	boolean editedInfo, editedAddress,editedDetail;
	Customer customer=null;
	
	public CustomerScreen() {
		super("CustomerScreen"); 
		
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		custPanel = new rmk.gui.ScreenComponents.CustomerInfoPanel();
		custPanel.setParent(this);
		//	custPanel.addActionListener(this);
		getContentPane().add(custPanel);
		
		custAddPanel = new rmk.gui.ScreenComponents.CustomerAddressPanel();
		custAddPanel.setParent(this);
		//	custAddPanel.addActionListener(this);
		getContentPane().add(custAddPanel);
		//    	getContentPane().add(custPanel);
		
		detailPanel = new rmk.gui.ScreenComponents.CustomerDetailPanel();
		detailPanel.setParent(this);
		//	detailPanel.addActionListener(this);
		getContentPane().add(detailPanel);
		
		invoicePanel = new rmk.gui.ScreenComponents.InvoiceListPanel();
		invoicePanel.setParent(this);
		//	invoicePanel.addActionListener(this);
		getContentPane().add(invoicePanel);
		//  	invoicePanel.addFocusListener(this);
		
		getContentPane().add(buttonBar);
		
		setPreferredSize(new Dimension(825,640));
	}
	CustomerScreen(DBGuiModel model){
		this();
		setData(model);
	}
	
	public boolean isEdited(){return editedInfo || editedAddress;}
	public Vector getEditedData(){
		Vector results = new Vector();
		if(editedAddress) results.add(custAddPanel.getData());
		if(editedInfo) results.add(custPanel.getData());
		return results;
	}
	
	public void setData(DBGuiModel model) {
		this.model = model;
		customer = (Customer)model.getCustomerData().get(0);
		if(customer.getInvoices() == null){
			customer.setInvoices(model.getInvoiceData());
		} else {
			int custInvCnt = customer.getInvoices().size();
			int modInvCnt = model.getInvoiceData().size();
			Invoice custInv = null;
			Invoice modInv = null;
			if(customer.getInvoices() != null && customer.getInvoices().size() >=1)
				custInv = (Invoice)customer.getInvoices().get(0);
			if(model.getInvoiceData() != null && model.getInvoiceData().size() >=1)
				modInv = (Invoice)model.getInvoiceData().get(0);
			if (custInv != null && modInv!= null 
					&& custInv.getCustomerID() == modInv.getCustomerID()
					&& modInvCnt > custInvCnt)
				customer.setInvoices(model.getInvoiceData());
		}
		custPanel.setData(model);
		custAddPanel.setData(model);
		invoicePanel.setData(model);
		detailPanel.setData(model);
		//		model.addActionListener(this);
		this.pack();
	}
	public void updateInvPrice(Invoice invoice){
		if(model.getInvoiceData() != null){
			Vector invoiceData = model.getInvoiceData();
			for (java.util.Enumeration enum = invoiceData.elements(); enum
			.hasMoreElements();) {
				Invoice listInvoice=(Invoice) enum.nextElement();
				if(listInvoice.getInvoice() == invoice.getInvoice()){
					if(listInvoice == invoice){ // same reference
						invoicePanel.setData(model);
						return;
					} else{
						invoiceData.remove(listInvoice);
						invoiceData.add(invoice);
						invoicePanel.setData(model);
					}
					break;
				}
			}
		}
	}
	
	private void saveData(){
		carpus.database.DBInterface db = Configuration.Config.getDB();
		Customer cust = (Customer)custPanel.getData();
		java.util.Vector outputLst = new java.util.Vector();
		
		if(editedInfo || editedDetail){
			//  	    if(editedDetail){
			//  		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "merging detailInfo");
			
			Customer cust2 = (Customer)detailPanel.getData();
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":terms:"+ cust2.getTerms());
			
			cust.setFlag(cust2.isFlag());
			cust.setDealer(cust2.isDealer()?1:0);
			cust.setMemo(cust2.getMemo());
			cust.setBladeList(cust2.getBladeList());
			cust.setTaxNumber(cust2.getTaxNumber());
			cust.setTerms(cust2.getTerms());
			//  	    }
			try{
				outputLst = new java.util.Vector();
				if(rmk.gui.Dialogs.getInstance().dataErrors(sys.customerInfo.validate(cust)))
					return; // invalid data, don't save/continue
				outputLst.add(cust);
				outputLst = db.saveItems("Customers", outputLst);
				model.setCustomerData(outputLst);
//				updateOccured((DBObject) cust, ScreenController.UPDATE_CHANGE, cust );
				custPanel.setData(model);
				detailPanel.setData(model);
				editedInfo = false;
				editedDetail = false;
				rmk.ErrorLogger.getInstance().logMessage("Saved CustomerInfo" + outputLst);
				//  		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ cust.getEMailAddress());
				
			} catch (java.lang.Exception excep){
				rmk.ErrorLogger.getInstance().logError("ScreenController:displayInvoiceDetails", excep);
				//  		carpus.database.Logger.getInstance().logError("CustomerInfo:", excep);
			}
			invoicePanel.setData(model);
		}
		if(editedAddress){
			try{
				carpus.database.DBObject address = custAddPanel.getData();
				
				((Address)address).setCustomerID(cust.getCustomerID());
				outputLst = new java.util.Vector();		
				outputLst.add(address);		
				outputLst = db.saveItems("Address", outputLst);
				model.setAddressData(outputLst);
				
				if(cust.getCurrentAddress() != ((Address)address).getAddressID()){
					cust.setCurrentAddress(((Address)address).getAddressID());
					outputLst = new java.util.Vector();
					outputLst.add(cust);
					outputLst = db.saveItems("Customers", outputLst);		
					model.setCustomerData(outputLst);
					updateOccured((DBObject) address, ScreenController.UPDATE_CHANGE, cust );
				}
				
				custAddPanel.setData(model);
				editedAddress = false;
			} catch (java.lang.Exception excep){
				carpus.database.Logger.getInstance().logError("AddressInfo:",excep);
			}
		}
		buttonBar.enableButton(0,editedInfo || editedAddress);
	}
	
	//==========================================================
	public void internalFrameActivated(
			javax.swing.event.InternalFrameEvent e) {
		//			ErrorLogger.getInstance().logMessage(
		//				this.getClass().getName() + ":" + "Window Activated.");
		int id = (int) ((Customer) custPanel.getData()).getCustomerID();
		if (id == 0)
			custPanel.grabFocus();
		else{
			if(((Customer) custPanel.getData()).isDealer())
				invoicePanel.grabFocus();
			else
				custAddPanel.grabFocus();
		}
		ApplicationMenu.getInstance().pushScreenToTopOfStack(this);
		
		//		  	("Internal frame activated", e);
	}

	public void addNewInvoice(){
		Invoice inv = new Invoice(0);
		Customer cust = (Customer) custPanel.getData();
		inv.setCustomerID(cust.getCustomerID());
		inv.setDiscountPercentage(cust.getDiscount());
		java.util.GregorianCalendar estimated = (java.util.GregorianCalendar) inv
		.getDateOrdered().clone();
		estimated.add(Calendar.MONTH, Configuration.Config
				.getMonthsBacklogged());
		while (estimated.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
			estimated.add(Calendar.DATE, 1);
		}
		inv.setDateEstimated(estimated);
		
		Vector invList = model.getInvoiceData();
		if (invList == null) invList = new Vector();
		invList.add(inv);
		//            ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + invList.size() + ":"
		//                    + ((Invoice) invList.get(invList.size() - 1)).getInvoice());
		
		model.setInvoiceData(invList);
		model.setInvoiceItemsData(null);

		Screen screen = rmk.ScreenController.getInstance().newInvoice(model);
		
//		updateOccured((DBObject) inv, ScreenController.UPDATE_EDIT, cust );
		//            if(screen != null)
		//            	screen.addActionListener(this);

	}
	
	//==========================================================
    //==========================================================
	public void actionPerformed(ActionEvent e) {
		if(!processHotKeys(e)){
			ErrorLogger.getInstance().TODO();
		}
	}
    //==========================================================
	public void processCommand(String command, Object from){
		//	    public void actionPerformed(ActionEvent e) {
		//		String command = e.getActionCommand().toUpperCase().trim();
		ErrorLogger.getInstance().TODO();
		
//		if (command.equals("CANCEL")) { //cancel
//			Vector invList = model.getInvoiceData();
//			if (invList != null) {
//				for (java.util.Enumeration enum = invList.elements(); enum
//				.hasMoreElements();) {
//					Invoice inv = (Invoice) enum.nextElement();
//					if (inv.getInvoice() == 0) {
//						invList.remove(inv);
//					}
//				}
//			}
//			defaultCancelAction();
//			//  	    return;
//		} else if (command.equals("F1")) { //F1 - Panel1
//			custPanel.requestFocus();
//		} else if (command.equals("F2")) { //F2 - Panel2
//			custAddPanel.requestFocus();
//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + "F2????");
//		} else if (command.equals("F3")) { //F3 - Panel3
//			invoicePanel.grabFocus();
//		} else if (command.equals("F11")) { //F11 - Toggle address/detail panel visibilities
//			custAddPanel.setVisible(!custAddPanel.isVisible());
//			detailPanel.setVisible(!detailPanel.isVisible());
//			invoicePanel.grabFocus();
//			//  	    invoicePanel.selectFirst();
//		} else if (command.equals("ADDRESSCHANGED")) { //ADDRESS CHANGED
//			editedAddress = true;
//		} else if (command.equals("INFOCHANGED")) { //INFO CHANGED
//			editedInfo = true;
//		} else if (command.equals("CUSTOMER DETAIL CHANGED")) { //INFO CHANGED
//			editedDetail = true;
//			
//		} else if (command.equals("INVOICEDETAILS")) { // INVOICE DETAILS
//			// request
//			long invoiceID = invoicePanel.getSelectedItemID();
//			if (invoiceID == 0) {
//				invoicePanel.grabFocus();
//				return;
//			}
//			//		Invoice inv = sys.invoiceInfo.getInvoice(invoiceID);
//			Invoice inv = (Invoice) invoicePanel.getSelectedItem();
//			if (model == null) model = new DBGuiModel();
//			rmk.ScreenController.getInstance()
//			.displayInvoiceDetails(inv, model);
//			
//		} else if (command.equals("INVOICEREPORT")) { // INVOICE DETAILS request
//			long invoiceNumber = invoicePanel.getSelectedItemID();
//			
//			int format = HtmlReportDialog.LONG_FORMAT;
//			Customer cust = (Customer) custPanel.getData();
//			if (cust.isDealer()) format = HtmlReportDialog.SHORT_FORMAT;
//			
//			rmk.gui.Dialogs.report(HtmlReportDialog.INVOICE_REPORT, format,
//					(int) invoiceNumber);
//			
//		} else if (command.equals("ADDINVOICE")) { // New INVOICE request
//			addNewInvoice();
//			return;
//		} else if (command.equals("PAYMENTS")) { //PaymentInfo Display
//			//  	    long invoiceID = invoicePanel.getSelectedItem();
//			Invoice invoice = (Invoice) invoicePanel.getSelectedItem();
//			if (invoice == null) {
//				invoicePanel.grabFocus();
//				return;
//			}
//			
//			//	    rmk.database.dbobjects.Invoice selectedInvoice =
//			// rmk.DataModel.getInstance().invoiceInfo.getInvoice(invoiceID);
//			Vector invoices = null;
//			if (customer != null) invoices = customer.getInvoices();
//			if (invoices == null) {
//				invoices = model.getInvoiceData();
//				if (invoices != null
//						&& invoices.size() > 0
//						&& ((Invoice) invoices.get(0)).getInvoice() != invoice
//						.getInvoice()) {
//					invoices = new Vector();
//				}
//			}
//			if (invoices == null) invoices = new Vector();
//			if (!invoices.contains(invoice)) invoices.add(invoice);
//			if (invoices == null || invoices.size() == 1) // empty or single invoice
//				model.setInvoiceData(invoices);
//			else { // move to end?	    
//				invoices.remove(invoice);
//				invoices.add(invoice);
//			}
//			
//			Vector paymentInfo = rmk.DataModel.getInstance().financialInfo
//			.getInvoicePayments(invoice.getInvoice());
//			model.setPaymentsData(paymentInfo);
//			rmk.ScreenController.getInstance().invoicePayments(model);
//		} else if (command.equals("SAVE")) { // Save INFO
//			saveData();
//		} else if (command.equals("INVOICE_SAVED")) { // INVOICE DETAILS request
//			invoicePanel.setData(model);
//			// -------------------------
//		} else { // Undefined, as yet
//			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "|");
//			
//			//  	    parentFrame.actionPerformed(e);
//		}
//		buttonBar.enableButton(0, editedInfo || editedAddress || editedDetail);
//		//  	parentFrame.actionPerformed(e);
	}
	
	public void updateOccured(DBObject itemChanged, int changeType, DBObject parentItem){
		String parentName="";
		if(parentItem != null) parentName = parentItem.getClass().getName();
		String itemName="";
		if(itemChanged != null) itemName = itemChanged.getClass().getName();

		switch(changeType){
		case ScreenController.UPDATE_ADD:
		{
			addNewInvoice();
		}
		break;
		case ScreenController.UPDATE_EDIT:
		{
			if(itemName.indexOf("Customer")>0){
				editedDetail = true;
				buttonBar.enableButton(0, true);
			}else if(itemName.indexOf("Address")>0){
				editedAddress = true;
				buttonBar.enableButton(0, true);
			} else{
				ErrorLogger.getInstance().TODO();
//				addNewInvoice();
			}
		}
		break;
		
		case ScreenController.UPDATE_CANCELED:
		{
			defaultCancelAction();
			SignalProcessor.getInstance().removeScreen(this);
		}
		break;
		
		default:
			ErrorLogger.getInstance().TODO();
		}
		
	}
	
	public void buttonPress(int button, int id) {
		switch(button){
		case ScreenController.BUTTON_CANCEL:
			defaultCancelAction();
			SignalProcessor.getInstance().removeScreen(this);
		break;
		case ScreenController.BUTTON_SELECTION_DETAILS:
		case ScreenController.BUTTON_F6: // same as SELECTION_DETAILS, goto invoice if selected
		{
			ApplicationMenu.getInstance().pushScreenToTopOfStack(this);
			if (id == 0) {
				invoicePanel.grabFocus();
				return;
			}
			Invoice inv = (Invoice) invoicePanel.getSelectedItem();
			if (model == null) model = new DBGuiModel();
			rmk.ScreenController.getInstance().displayInvoiceDetails(inv, model);
		}
		break;
		
		case ScreenController.BUTTON_DISPLAY_INVOICE:
		{		
			long invoiceNumber = invoicePanel.getSelectedItemID();
			
			int format = HtmlReportDialog.LONG_FORMAT;
			Customer cust = (Customer) custPanel.getData();
			if (cust.isDealer()) format = HtmlReportDialog.SHORT_FORMAT;
			
			rmk.gui.Dialogs.report(HtmlReportDialog.INVOICE_REPORT, format,
					(int) invoiceNumber);			
		}
		break;
		
		case ScreenController.BUTTON_SAVE:
		{		
			saveData();
		}
		break;
		default:
			ErrorLogger.getInstance().TODO();
		}
	}
	
}
