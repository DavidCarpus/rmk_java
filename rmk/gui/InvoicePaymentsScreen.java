package rmk.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Vector;
import java.util.GregorianCalendar;

//import rmk.DataModel;
import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.SignalProcessor;
import rmk.database.dbobjects.DBObject;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.Payments;

public class InvoicePaymentsScreen extends Screen {

    //      ActionListener parentFrame;
    rmk.gui.ScreenComponents.CustomerInfoPanel customerPnl;

    rmk.gui.ScreenComponents.InvoiceDetailsPanel invoiceDetailPnl;

    rmk.gui.ScreenComponents.InvoicePaymentsListPanel invoicePaymentsPnl;

    boolean editedCustomer = false;

    boolean editedInvoice = false;

    Invoice invoice=null;
    Vector paymentList=null;

    public InvoicePaymentsScreen() {
        super("Invoice Details");

        getContentPane().setLayout(
                new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        customerPnl = new rmk.gui.ScreenComponents.CustomerInfoPanel();
        customerPnl.setParentScreen(this);
		customerPnl.isOnCustomerPanel(false);
        getContentPane().add(customerPnl);

        invoiceDetailPnl = new rmk.gui.ScreenComponents.InvoiceDetailsPanel();
        invoiceDetailPnl.onPaymentsScreen(true);
        invoiceDetailPnl.setParentScreen(this);
        getContentPane().add(invoiceDetailPnl);
        JPanel msgPanel = new JPanel();
        msgPanel.add(new JLabel("$$$$$$$$ Payments $$$$$$$$"));
        getContentPane().add(msgPanel);
        msgPanel.setBackground(new Color(0, 182, 51));

        invoiceDetailPnl.setEnabled(false);

        invoicePaymentsPnl = new rmk.gui.ScreenComponents.InvoicePaymentsListPanel();
        invoicePaymentsPnl.setParent(this);
        getContentPane().add(invoicePaymentsPnl);

        getContentPane().add(buttonBar);
        buttonBar.addButton(null, "Invoice", "Invoice", "Invoice");
        buttonBar.addButton(null, "Acknowledgment", "Acknowledgment",
                "Acknowledgment");
        buttonBar.addButton(null, "Ship", "Ship", "Ship This Invoice");

        buttonBar.getButton(2).setMnemonic(KeyEvent.VK_I); // Invoice Button
        buttonBar.getButton(3).setMnemonic(KeyEvent.VK_K); // Acknowledgment
                                                           // Button
        buttonBar.getButton(4).setMnemonic(KeyEvent.VK_P); // Acknowledgment
                                                           // Button

        //    	buttonBar.addButton(null, "Add","Add","Add");
        dataPanels[0] = customerPnl;
        dataPanels[1] = invoiceDetailPnl;
        dataPanels[2] = invoicePaymentsPnl;
        
        setPreferredSize(new Dimension(900, 640));
        pack();
    }

//    public InvoicePaymentsScreen(DBGuiModel data) {
//        super("Invoice Details");
//        setData(data);
//    }

    public boolean isEdited() {
        return (editedInvoice || editedCustomer);
    }

    //------------------------------------------------------------------
    public Vector getEditedData() {
        Vector data = new Vector();
        return data;
    }

    //------------------------------------------------------------------
    public void setData(Invoice inv, Vector payments) {
    	paymentList = payments;
    	invoice = inv;
//        this.model = model;
        //  	ErrorLogger.getInstance().logMessage(model);
        customerPnl.setData(invoice.getParent());
        invoiceDetailPnl.setData(invoice);
        invoicePaymentsPnl.setData(invoice,paymentList);
        
//        Vector invList = model.getInvoiceData();
//        if (invList.size() > 0) {
            // get last entry, it will have id==0 if new
//            invoice = (Invoice) invList.get(invList.size() - 1);
            if (invoice.getInvoice() == 0) {
                buttonBar.enableButton(0, true);
                editedInvoice = true;
            } else {
                int knifeCnt = rmk.DataModel.getInstance().invoiceInfo
                        .getKnifeCount(invoice.getInvoice());
                invoiceDetailPnl.setTotalKnives(knifeCnt);
            }
            String shipButtonLabel = "Ship";
            if (invoice.getDateShipped() != null) shipButtonLabel = "'Un'Ship";
            buttonBar.getButton(4).setText(shipButtonLabel);

//        }
    }
    public void setData(DBObject item){
    	ErrorLogger.getInstance().TODO();
    }
    //------------------------------------------------------------------
    private void saveData() {
        if (editedInvoice) {
//            Vector modelLst = model.getInvoiceData();
//            Invoice newData = invoiceDetailPnl.getData();
//            for(Enumeration enum = modelLst.elements(); enum.hasMoreElements();){
//                Invoice lstInv = (Invoice) enum.nextElement();
//                if(lstInv.getInvoice() == newData.getInvoice()){
//                    modelLst.remove(lstInv);
//                    modelLst.add(newData);
//                    newData.setItems(lstInv.getItems());
//                    continue;
//                }
//            }
            Vector invList = new Vector();
            invList.add(invoice);
            Configuration.Config.getDB().saveItems("Invoice", invList);
            invoiceDetailPnl.setData(invoice);
            invoicePaymentsPnl.setData(invoice, paymentList);
            editedInvoice = false;
        }
        if (editedCustomer) {
        	Customer customer = invoice.getParent();
            Vector custList = new Vector();
            custList.add(customer);
            Configuration.Config.getDB().saveItems("Customers", custList);
            customerPnl.setData(customer);
            editedCustomer = false;
        }
        buttonBar.enableButton(0, editedInvoice || editedCustomer);
    }

    //==========================================================
    private void removeEntry(long invoiceNum, long paymentID) {

    	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":removeEntry("
                + invoiceNum + "," + paymentID + ")");
        rmk.DataModel sys = rmk.DataModel.getInstance();
        
        for (Iterator iterator = paymentList.iterator(); iterator.hasNext();) {
			Payments payment = (Payments) iterator.next();
            if (payment.getPaymentID() == paymentID) {
            	paymentList.remove(payment);
            }
        }
        sys.financialInfo.removePayment(paymentID);
        invoiceDetailPnl.setData(this.invoice);
        invoicePaymentsPnl.setData(invoice,paymentList);
        rmk.Processing.updateScreens_Shipping(invoice);

    }

    //------------------------------------------------------------------
    private void addEntry(long invoiceNum, long customerID) {
    	ErrorLogger.getInstance().logDebug("Adding payment(s) to invoice :" + invoiceNum, true);
    	while (true) { // keep getting payments until valid one is not entered
            Payments payment = rmk.gui.Dialogs.getPayment(invoiceNum, customerID);
            if (payment == null) {
                invoicePaymentsPnl.requestFocus();
                return; // valid payment was not entered
            }
            
//            Vector existingPayments = sys.financialInfo
//                    .getInvoicePayments(invoiceNum);
            
            if (paymentList == null) // no payments before this one
            	paymentList = new Vector();
            if(payment.getVCODE() != null && payment.getVCODE().equals("000"))
                payment.setVCODE("");
            if (!paymentList.contains(payment))
            	paymentList.add(payment);
            // save it and update dependent screens
            paymentList = sys.invoiceInfo.savePayments(paymentList); 
            // update cust CC# info IF it's a CC
            if (rmk.database.FinancialInfo.isValidCCNumber(payment
                    .getCheckNumber())) {
            	String ccNum = payment.getCheckNumber() + "*" + payment.getVCODE();
                sys.customerInfo
                        .setCustCCNumber(customerID, ccNum , payment
                                .getExpirationDate());
            }

//            model.setPaymentsData(existingPayments); // update model
            invoice.setPayments(paymentList);
            invoicePaymentsPnl.setData(invoice,paymentList);
            invoiceDetailPnl.setData(this.invoice);
            invoice.setPayments(paymentList);
            pack();
            rmk.Processing.updateScreens_Shipping(invoice);
        }
    }

    //==========================================================
    public void internalFrameActivated(javax.swing.event.InternalFrameEvent e) {
//        Vector outputList = model.getInvoiceData();
//        Invoice invoice = (Invoice) outputList.get(outputList.size() - 1);
        addEntry(invoice.getInvoice(), invoice.getCustomerID());

        //  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Window
        // Activated.");
        //  	("Internal frame activated", e);
    }

    //==========================================================
	public void actionPerformed(ActionEvent e) {
		if(!processHotKeys(e)){
			ErrorLogger.getInstance().TODO();
		}
	}
    //==========================================================
    //==========================================================
	public void processCommand(String command, Object from){
//	    public void actionPerformed(ActionEvent e) {
//		String command = e.getActionCommand().toUpperCase().trim();
		ErrorLogger.getInstance().logDebugCommand(command);

        // -------------------------
        if (command.equals("CANCEL")) { //cancel
        //  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Cancel");
            defaultCancelAction();
            //  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Canceled");
            // -------------------------
        } else if (command.equals("F1")) { //F1 - Panel1
            customerPnl.requestFocus();
        } else if (command.equals("F2")) { //F2 - Panel2
            invoiceDetailPnl.requestFocus();
        } else if (command.equals("F3")) { //F3 - Panel3
            invoicePaymentsPnl.grabFocus();
        } else if (command.equals("F11")) { //F11 - expand list, NOT this
                                            // screen
        //	    invoiceDetailPnl.setVisible(!invoiceDetailPnl.isVisible());
            // -------------------------
        } else if (command.equals("INVOICECHANGED")) { //INVOICE CHANGED
            editedInvoice = true;
            buttonBar.enableButton(0, true);
            // -------------------------
        } else if (command.equals("INFOCHANGED")) { //INVOICE CHANGED
            editedCustomer = true;
            buttonBar.enableButton(0, true);
            // -------------------------
        } else if (command.equals("ADD")) { // ADD Payment
//            Vector outputList = model.getInvoiceData();
//            Invoice invoice = (Invoice) outputList.get(outputList.size() - 1);
            addEntry(invoice.getInvoice(), invoice.getCustomerID());
            // -------------------------
        } else if (command.equals("DELETEPAYMENT")) { // Delete Payment
            if(!rmk.gui.Dialogs.yesConfirm("Are you sure you wish to remove this payment?")){
            	return;
            }

            long id = invoicePaymentsPnl.getSelectedItemID();
//            Vector outputList = model.getInvoiceData();
//            Invoice invoice = (Invoice) outputList.get(0);
            removeEntry(invoice.getInvoice(), id);
            // -------------------------

        } else if (command.equals("INVOICE")) { //Invoice Display
            int format = HtmlReportDialog.LONG_FORMAT;
            Customer cust = (Customer) customerPnl.getData();
            if (cust.isDealer()) format = HtmlReportDialog.SHORT_FORMAT;

            rmk.gui.Dialogs.report(HtmlReportDialog.INVOICE_REPORT, format,
                    (int) invoice.getInvoice());
            //-----------------------------
        } else if (command.equals("ACKNOWLEDGMENT")) { //Acknowledgement
                                                       // Display
            int format = HtmlReportDialog.LONG_FORMAT;
            Customer cust = (Customer) customerPnl.getData();
            if (cust.isDealer()) format = HtmlReportDialog.SHORT_FORMAT;

            rmk.gui.Dialogs.report(HtmlReportDialog.ACKNOWLEDGE_REPORT, format,
                    (int) invoice.getInvoice());
            // -------------------------
        } else if (command.equals("SHIP")) { // Ship it
            Invoice inv = invoiceDetailPnl.getData();
            if (inv == null) return;
            GregorianCalendar shipDate = inv.getDateShipped();
            if (shipDate == null) { // need to ship
                shipDate = rmk.gui.Dialogs.getDate("Shipping Date",
                        new GregorianCalendar());
                rmk.Processing.getInstance().shipInvoice(inv, shipDate);
            } else {
                rmk.Processing.getInstance().unShipInvoice(inv);
            }

            // -------------------------
        } else if (command.equals("SAVE")) { //Save INFO
            saveData();

            // -------------------------
        } else if (command.equals("INVOICEDETAILS")) {
            Invoice invoice = invoiceDetailPnl.getData();
            rmk.gui.IScreen screen = rmk.ScreenController.getInstance()
                    .getInvoiceScreen(invoice);

            if (screen == null) {
                rmk.ScreenController.getInstance().displayInvoiceDetails(
                        invoice);
            } else {
                ((InvoiceDetailsScreen)screen).setData(invoice);
                screen.bringToFront();
            }
            // -------------------------
        } else if (command.equals("CUSTOMERDETAILS")) {
            Invoice invoice = invoiceDetailPnl.getData();
            rmk.gui.IScreen screen = rmk.ScreenController.getInstance()
                    .getCustomerScreen(invoice.getCustomerID());

            if (screen == null) {
                rmk.ScreenController.getInstance().displayCustomer(
                        invoice.getCustomerID());
            } else {
            	((CustomerScreen)screen).setData(invoice.getParent(), invoice.getParent().getCurrentAddressItem(), paymentList);
            	((CustomerScreen)screen).selectInvoiceNumber(invoice.getInvoice());
                screen.bringToFront();
            }
            // -------------------------
        } else { // Undefined
        	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "|");
        }
    }

	public void updateOccured(DBObject itemChanged, int changeType, DBObject parentItem){
		switch(changeType){
		case ScreenController.UPDATE_ADD:
		{
//			Vector outputList = model.getInvoiceData();
//			Invoice invoice = (Invoice) outputList.get(outputList.size() - 1);
			addEntry(invoice.getInvoice(), invoice.getCustomerID());
		}
		break;
		
		default:
			ErrorLogger.getInstance().TODO();
		}
	}
    
    
	public void buttonPress(int button, int id) {
       	ErrorLogger.getInstance().logButton(button, id);

       	switch(button){
		//---------------------------------
		case ScreenController.BUTTON_CANCEL:
		{
			defaultCancelAction();
			SignalProcessor.getInstance().removeScreen(this);
		}
		break;
		//---------------------------------
		case ScreenController.BUTTON_REMOVE:
		{
            if(!rmk.gui.Dialogs.yesConfirm("Are you sure you wish to remove this payment?")){
            	return;
            }

//            long id = invoicePaymentsPnl.getSelectedItemID();
//            Vector outputList = model.getInvoiceData();
//            Invoice invoice = (Invoice) outputList.get(0);
            removeEntry(invoice.getInvoice(), id);
		}
		break;		
		//---------------------------------
		case ScreenController.BUTTON_DISPLAY_INVOICE:
		case ScreenController.BUTTON_F6:
		{		
			long invoiceNumber = invoice.getInvoice();
			
			int format = HtmlReportDialog.LONG_FORMAT;
			Customer cust = (Customer) customerPnl.getData();
			if (cust.isDealer()) format = HtmlReportDialog.SHORT_FORMAT;
			
			rmk.gui.Dialogs.report(HtmlReportDialog.INVOICE_REPORT, format,
					(int) invoiceNumber);			
		}
		break;
		//---------------------------------
		case ScreenController.BUTTON_F5:
		{		
		rmk.gui.IScreen screen = rmk.ScreenController.getInstance().getCustomerScreen(invoice.getCustomerID());
		
		if (screen == null) {
			rmk.ScreenController.getInstance().displayCustomer(
					invoice.getCustomerID());
		} else {
			Customer customer;
			try {
				customer = invoice.getParent();
//				if(customer == null)
//					customer = sys.customerInfo.getCustomerByID(invoice.getCustomerID());
				((CustomerScreen)screen).setData(customer, customer.getCurrentAddressItem(), customer.getInvoices());
				((CustomerScreen)screen).selectInvoiceNumber(invoice.getInvoice());
				screen.bringToFront();
			} catch (Exception e) {
				ErrorLogger.getInstance().logError("Fetching customer:"+invoice.getCustomerID(), e);
			}
		}
		}
		//---------------------------------
		case ScreenController.BUTTON_DISPLAY_ACK_RPT:
		{		
			long invoiceNumber = invoice.getInvoice();
			
			int format = HtmlReportDialog.LONG_FORMAT;
			Customer cust = (Customer) customerPnl.getData();
			if (cust.isDealer()) format = HtmlReportDialog.SHORT_FORMAT;

			
			rmk.gui.Dialogs.report(HtmlReportDialog.ACKNOWLEDGE_REPORT, format,
					(int) invoiceNumber);		
		}
		break;
		// ---------------------------------
		case ScreenController.BUTTON_ADD:
		{
			addEntry(invoice.getInvoice(), invoice.getCustomerID());
		}
		break;
		// ---------------------------------
		case ScreenController.BUTTON_SHIP:
		{
			Invoice inv = invoiceDetailPnl.getData();
			if (inv == null) return;
			GregorianCalendar shipDate = inv.getDateShipped();
			if (shipDate == null) { // need to ship
				shipDate = rmk.gui.Dialogs.getDate("Shipping Date",
						new GregorianCalendar());
				rmk.Processing.getInstance().shipInvoice(inv, shipDate);
			} else {
				rmk.Processing.getInstance().unShipInvoice(inv);
			}
		}
		break;
		// ---------------------------------
		default:
		{
			ErrorLogger.getInstance().TODO();
		}
		break;
       	}
	}

	public Invoice getInvoice() {
		return invoice;
	}
	public Vector getPaymentLst(){
		return paymentList;
	}
	

}