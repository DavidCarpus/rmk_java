package rmk;

import java.util.Vector;

import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.DBObject;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.gui.*;

import java.awt.*;

/**
 * Describe class <code>ScreenController</code> here.
 * 
 * @author <a href="mailto:carpus@cs.ucf.edu">David Carpus </a>
 * @version 1.0
 */
public class ScreenController {
	rmk.DataModel sys = rmk.DataModel.getInstance();
	public static final int UPDATE_UNKNOWN=0;
	public static final int UPDATE_EDIT=1;
	public static final int UPDATE_CHANGE=2;
	public static final int UPDATE_REMOVE=3;
	public static final int UPDATE_ADD=4;
	public static final int UPDATE_CANCELED=5;

	public static final int LIST_ITEM_SELECTED=6;	
	public static final int UPDATE_SAVE=7;
	public static final int ENTER_KEY=8;
	
	public static final int VALIDATION_FAILED=9;

	public static final String[] updateTxt = {"UPDATE_UNKNOWN", "UPDATE_EDIT", "UPDATE_CHANGE", "UPDATE_ADD", "UPDATE_CANCELED", 
			"LIST_ITEM_SELECTED", "UPDATE_SAVE", "ENTER_KEY", "VALIDATION_FAILED" };
	
	
	public static final int BUTTON_SELECTION_UNKNOWN=0;
	public static final int BUTTON_SELECTION_DETAILS=1;
	public static final int BUTTON_CANCEL=2;
	public static final int BUTTON_ADD=3;
	public static final int BUTTON_REMOVE=4;
	public static final int BUTTON_SAVE=5;
	public static final int BUTTON_KNIFE_COUNT=6;
	public static final int BUTTON_DISPLAY_INVOICE=7;
	public static final int BUTTON_DISPLAY_ACK_RPT=8;
	public static final int BUTTON_SHIP=9;
	
	
	public static final int BUTTON_F1=101;
	public static final int BUTTON_F2=102;
	public static final int BUTTON_F3=103;
	public static final int BUTTON_F4=104;
	public static final int BUTTON_F5=105;
	public static final int BUTTON_F6=106;
	public static final int BUTTON_F7=107;
	public static final int BUTTON_F8=108;
	public static final int BUTTON_F9=109;
	public static final int BUTTON_F10=110;
	public static final int BUTTON_F11=111;
	public static final int BUTTON_F12=112;
	
	static int cntr = 0;

	private static ScreenController instance = new ScreenController();

	IScreen screen[] = new IScreen[4];

	//      CustomerSelectionScreen custList;

	public static ScreenController getInstance() {
//		long mem1 = Runtime.getRuntime().freeMemory();
		Runtime.getRuntime().gc();

		//		ErrorLogger.getInstance().logMessage(cntr++ + " FreeMem " + mem1 +"
		// -> " + Runtime.getRuntime().freeMemory());
		return instance;
	}

	public rmk.gui.IScreen bringToFront(String title) {
		rmk.gui.IScreen screen = rmk.gui.ApplicationMenu.getInstance()
				.findScreen(title);
		if (screen != null) { // found screen with that title
			screen.bringToFront();
			return screen;
		}
		return null;
	}

	public void displayInvoiceDetails(
			rmk.database.dbobjects.Invoice selectedInvoice
//			, DBGuiModel model
			) {
//		ErrorLogger.getInstance().logDebug("" + selectedInvoice);
		Desktop.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {

			if (selectedInvoice == null)
				throw new Exception("No invoice?");

//			Vector invoices = model.getInvoiceData();

//			if (invoices == null) {
//				ErrorLogger.getInstance().logMessage("New inv Vector - null");
//				invoices = new Vector();
//			} else if (invoices.size() == 0) {
//				ErrorLogger.getInstance().logMessage(
//						"New inv Vector - size() = 0");
//				invoices = new Vector();
//			} else {
//				boolean found = false;
//				for (java.util.Enumeration enum = invoices.elements(); enum
//						.hasMoreElements();) {
//					rmk.database.dbobjects.Invoice item = (rmk.database.dbobjects.Invoice) enum
//							.nextElement();
//					if (item.getInvoice() == selectedInvoice.getInvoice()) {
//						found = true;
//						break;
//					}
//				}
//				if (!found) {
//					ErrorLogger.getInstance().logMessage(
//							"New inv Vector - non matched inv#");
//					invoices = new Vector();
//				}
//			}

//			if (!invoices.contains(selectedInvoice)) {
//				invoices.add(selectedInvoice);
//			} else {
//				invoices.remove(selectedInvoice);
//				invoices.add(selectedInvoice);
//			}
//			model.setInvoiceData(invoices);

			selectedInvoice.setParent(sys.customerInfo.getCustomerByID(selectedInvoice
					.getCustomerID()));
			selectedInvoice.setItems(sys.invoiceInfo
					.getInvoiceEntries(selectedInvoice.getInvoice()));

			InvoiceDetailsScreen screen = new InvoiceDetailsScreen();
			String title = "Invoice : ";
			if (selectedInvoice != null) {
				title += selectedInvoice.getInvoice();
			}
			screen.setTitle(title);

			Desktop.getInstance().add(screen);
			screen.setData(selectedInvoice);
//			ErrorLogger.getInstance().logDebug("" + selectedInvoice, true);
			ErrorLogger.getInstance().logDebug(ErrorLogger.getCallerFunction() + ":" + selectedInvoice, false);
			select(screen);

			//			screen.setVisible(true);
			//			screen.toFront();
			//			screen.grabFocus();
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayInvoiceDetails", e);
		} // end of try-catch
		Desktop.getInstance().setCursor(null);
		return;
	}

	public void displayCustomer(long customerID) {
		Desktop.getInstance().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			//  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() +
			// ":"+ "Disp cust:" + customerID);

//			DBGuiModel model = new DBGuiModel();
			rmk.DataModel sys = rmk.DataModel.getInstance();

			rmk.database.dbobjects.Customer customer;
			Vector customers = new Vector();
			customer = sys.customerInfo.getCustomerByID(customerID);

//			ErrorLogger.getInstance().logDebug("" + customer, true);
			ErrorLogger.getInstance().logDebug(ErrorLogger.getCallerFunction() + ":" + customer, false);
			
			customers.add(customer);
//			model.setCustomerData(customers);

			Vector addressVect = new Vector();
			rmk.database.dbobjects.Address address = sys.customerInfo
					.getCurrentAddress(customerID);
			if (address == null)
				address = new rmk.database.dbobjects.Address(0);
			addressVect.add(address);
//			model.setAddressData(addressVect);

			Vector invoice = new Vector();
//			model.setInvoiceData(sys.invoiceInfo.getInitialInvoices(customer));

			CustomerScreen screen = new CustomerScreen();
			String title = "Customer : " + customerID;
			screen.setTitle(title);
			Desktop.getInstance().add(screen);
			
			screen.setData(customer,sys.invoiceInfo.getInitialInvoices(customer));
			select(screen);

			//	    screen.setVisible(true);
			//	    screen.toFront();
			//		screen.grabFocus();
			Desktop.getInstance().repaint();
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayCustomer", e);
		} // end of try-catch
		Desktop.getInstance().setCursor(null);
	}

	public void displayPartsList() {
		ErrorLogger.getInstance().logDebug("", true);
		try {
			PartsScreen screen = new PartsScreen();
			Desktop.getInstance().add(screen);

			screen.setData((DBObject)null);
			select(screen);
			//
			//	    screen.setVisible(true);
			//	    screen.toFront();
			//		screen.grabFocus();
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayPartsList", e);
		} // end of try-catch
	}

	public void displayHistoryList() {
//		ErrorLogger.getInstance().logDebug("", true);
		ErrorLogger.getInstance().logDebug(ErrorLogger.getCallerFunction() + ":displayHistoryList()", false);
		try {
			InvoiceHistoryScreen screen = new InvoiceHistoryScreen();
			Desktop.getInstance().add(screen);
			DBGuiModel model = new DBGuiModel();
			Vector invoice = new Vector();
//			model.setInvoiceData(sys.invoiceInfo.getHistory(7));

			screen.setData(sys.invoiceInfo.getHistory(7));
			select(screen);

			//		screen.setVisible(true);
			//		screen.toFront();
			//		screen.grabFocus();
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayPartsList", e);
		} // end of try-catch
	}

	public void displayCustomerList(Vector customerLst) {
		ErrorLogger.getInstance().logDebug("", true);
		try {
			CustomerSelectionScreen screen = new CustomerSelectionScreen();
			Desktop.getInstance().add(screen);

			screen.setData(customerLst);
			select(screen);
			//	    screen.setVisible(true);
			//	    screen.toFront();
			//		screen.grabFocus();

		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayCustomerList", e);
		} // end of try-catch
	}

	public void displayDealerList(Vector dealerLst) {
		ErrorLogger.getInstance().logDebug("", true);
		try {
			CustomerSelectionScreen screen = new CustomerSelectionScreen();
			screen.setTitle("Dealers");
			Desktop.getInstance().add(screen);

			screen.setData(dealerLst);
			select(screen);
			//	    screen.setVisible(true);
			//	    screen.toFront();
			//		screen.grabFocus();

		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayDealerList", e);
		} // end of try-catch
	}

	public InvoiceDetailsScreen newInvoice(Invoice invoice) {
		InvoiceDetailsScreen screen = null;
		ErrorLogger.getInstance().logDebug("" + invoice, false);
		try {
			screen = new InvoiceDetailsScreen();
			String title = "Invoice : ";
			if (invoice != null){
				title += invoice.getInvoice();
			}
			screen.setTitle(title);
			Desktop.getInstance().add(screen);
			((InvoiceDetailsScreen)screen).setData(invoice);
			select(screen);

			return screen;
		} catch (Exception e) {
			ErrorLogger.getInstance()
					.logError("ScreenController:newInvoice", e);
		} // end of try-catch
		return null;
	}

	public IScreen invoiceItem(InvoiceEntries item, long invoiceNum, long itemNum) {
		//      public void invoiceItem(DBGuiModel data, ActionListener parent){
		InvoiceItemScreen screen = null;
		Desktop.getInstance().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		Invoice inv = item.getParent();
		if(inv == null)
			sys.invoiceInfo.getInvoice(invoiceNum);
		
		try {
			screen = new InvoiceItemScreen();
//			Vector invoiceLst = data.getInvoiceData();

//			if (invoiceNum > 0) {
//				for (Enumeration lst = invoiceLst.elements(); lst
//						.hasMoreElements();) {
//					inv = (Invoice) lst.nextElement();
//					if (invoiceNum == inv.getInvoice())
//						break;
//				}
//			}
			if (invoiceNum != inv.getInvoice())
				inv = null;

			String title = "Invoice : ";
			if (inv != null)
				title += inv.getInvoice();

			title += " Item: ";
//			Vector invoiceItems = data.getKnifeData();
//			InvoiceEntries item = null;
//			if (invoiceItems != null) {
//				if(itemNum > 0){
//					for (java.util.Enumeration enum = invoiceItems.elements(); enum.hasMoreElements();) {
//						InvoiceEntries currItem = (InvoiceEntries) enum.nextElement();
//						if(currItem.getInvoiceEntryID() == itemNum){
//							item=currItem;
//							break;
//						}
//						
//					}
//				}
//				ErrorLogger.getInstance().logDebug(ErrorLogger.getCallerFunction()  + ":invoiceItem()" + ":" + invoiceItems, false);
//			} else {
//				ErrorLogger.getInstance().logDebug("No item (New?)", false);
//			}
			if(item != null){
				title += item.getInvoiceEntryID();
				ErrorLogger.getInstance().logMessage(
						"DispInvItem:" + item);
			}
			
			int year = sys.invoiceInfo.getPricingYear(inv.getInvoice());
			title += " Year-" + year;

//			invoiceLst.remove(inv);
//			invoiceLst.insertElementAt(inv, 0);

//			Customer customer = (Customer)data.getCustomerData().get(0);
			Customer customer = inv.getParent();
			
			screen.setTitle(title);

			Desktop.getInstance().add(screen);

			if(item == null) // new item
				item = new InvoiceEntries(0);
			
			item.setParent(inv);
			inv.setParent(customer);
			((InvoiceItemScreen)screen).setData(item);
			select(screen);
			//  	    screen.addActionListener(parent);
		} catch (Exception e) {
			ErrorLogger.getInstance().logError("ScreenController:invoiceItem",
					e);
			//  	    e.printStackTrace();
		} // end of try-catch
		Desktop.getInstance().setCursor(null);
		return (IScreen) screen;
	}

	public void newCustomer() {
		ErrorLogger.getInstance().logDebug("", true);
		try {
//			DBGuiModel model = new DBGuiModel();
			CustomerScreen screen = new CustomerScreen();
//			Vector custVector = new Vector();
//			custVector.add(new rmk.database.dbobjects.Customer(0));
//			model.setCustomerData(custVector);
			Desktop.getInstance().add(screen);
			screen.setData(new rmk.database.dbobjects.Customer(0), null);
			select(screen);
		} catch (Exception e) {
			ErrorLogger.getInstance().logError("ScreenController:newCustomer",
					e);
		} // end of try-catch
	}

	public void invoicePayments(Invoice invoice, Vector payments) {
		try {
			InvoicePaymentsScreen screen = new InvoicePaymentsScreen();
//			Vector invoices = data.getInvoiceData();
//			rmk.database.dbobjects.Invoice invoice = (rmk.database.dbobjects.Invoice) invoices
//					.get(invoices.size() - 1);
			String title = "Invoice Payments : ";
			if (invoice != null) {
				ErrorLogger.getInstance().logDebug("" + invoice, true);
				title += invoice.getInvoice();
			} else {
				ErrorLogger.getInstance().logDebug("No Invoice?", false);
			}

			screen.setTitle(title);

			Desktop.getInstance().add(screen);
			screen.setData(invoice,payments);
			select(screen);
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:invoicePayments", e);
		} // end of try-catch
	}

	public void invoicePayments(Customer customer, Invoice invoice){
		try {
			InvoicePaymentsScreen screen = new InvoicePaymentsScreen();
//			DBGuiModel model = new DBGuiModel();
			
//			Vector invoices = new Vector();
//			invoices.add(invoice);
//			model.setInvoiceData(invoices);

//			Vector custVector = new Vector();
//			custVector.add(customer);
//			model.setCustomerData(custVector);
	
			Vector payments = new Vector();
			invoicePayments(invoice, payments);
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:invoicePayments", e);
		} // end of try-catch

	}
	
	
	public void invoiceSearch() {
		ErrorLogger.getInstance().logDebug("", true);

		try {
			InvoiceSearchScreen screen = new InvoiceSearchScreen();
			DBGuiModel model = new DBGuiModel();
			rmk.DataModel sys = rmk.DataModel.getInstance();
			screen.setTitle("Invoice Query");

			Desktop.getInstance().add(screen);
			screen.setData(model);
			select(screen);

		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:invoiceSearch", e);
		} // end of try-catch
	}

	public rmk.gui.IScreen getInvoiceScreen(
			rmk.database.dbobjects.Invoice invoice) {
		ErrorLogger.getInstance().logDebug("" + invoice, true);

		rmk.gui.IScreen screen;
		String title = "";

		title = "Invoice : " + invoice.getInvoice();
		screen = rmk.gui.ApplicationMenu.getInstance().findScreen(title);
		if (screen != null)
			return screen;

		title = "Invoice:" + invoice.getInvoice();
		screen = rmk.gui.ApplicationMenu.getInstance().findScreen(title);
		if (screen != null)
			return screen;

		return null;
	}

	public rmk.gui.IScreen getCustomerScreen(
			rmk.database.dbobjects.Invoice invoice) {
		rmk.gui.IScreen screen;
		String title = "";
//		ErrorLogger.getInstance().logDebug("" + invoice, true);
		ErrorLogger.getInstance().logDebug(ErrorLogger.getCallerFunction()   + ":getCustomerScreen()" + ":" + invoice, false);
		
		title = "Customer : " + invoice.getCustomerID();
		return rmk.gui.ApplicationMenu.getInstance().findScreen(title);
	}

	public rmk.gui.IScreen getPaymentsScreen(
			rmk.database.dbobjects.Invoice invoice) {
		ErrorLogger.getInstance().logDebug("" + invoice, true);

		rmk.gui.IScreen screen;
		String title = "";
		title = "Invoice Payments : " + invoice.getInvoice();
		return rmk.gui.ApplicationMenu.getInstance().findScreen(title);
	}

	public void displayPreferencesScreen() {
		ErrorLogger.getInstance().logDebug("", true);
		try {
			PreferencesScreen screen = new PreferencesScreen();
			screen.setTitle("Preferences");
			Desktop.getInstance().add(screen);
			select(screen);
			//    	    screen.setVisible(true);
			//    	    screen.toFront();
			//    		screen.grabFocus();
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayDealerList", e);
		} // end of try-catch
	}

	void select(IScreen screen) {
		screen.setVisible(true);
		screen.toFront();
		screen.grabFocus();
		screen.bringToFront();
	}

}