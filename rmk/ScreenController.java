package rmk;

import java.util.Vector;

import rmk.database.dbobjects.Address;
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
	public static final int CLEAR_FEATURES=9;	
	
	public static final int VALIDATION_FAILED=10;

	public static final String[] updateTxt = {"UPDATE_UNKNOWN", "UPDATE_EDIT", "UPDATE_CHANGE", "UPDATE_REMOVE", "UPDATE_ADD", "UPDATE_CANCELED", 
			"LIST_ITEM_SELECTED", "UPDATE_SAVE", "CLEAR_FEATURES", "ENTER_KEY", "VALIDATION_FAILED" };
	
	
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
	public static final int BUTTON_CUSTOMER_MERGE=10;
	public static final int BUTTON_QUICK_DEALER_INVOICE=11;
	public static final int BUTTON_PARTS_ACTIVE_TOGGLE=12;
	
	
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
	public static final String[] buttonTxt = {
			"BUTTON_SELECTION_UNKNOWN", "BUTTON_SELECTION_DETAILS", "BUTTON_CANCEL", 
			"BUTTON_ADD", "BUTTON_REMOVE", "BUTTON_SAVE", 
			"BUTTON_KNIFE_COUNT", "BUTTON_DISPLAY_INVOICE", "BUTTON_DISPLAY_ACK_RPT" , 
			"BUTTON_SHIP" };	
	
	static int cntr = 0;

	private static ScreenController instance = new ScreenController();

	IScreen screen[] = new IScreen[4];

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

	public void displayInvoiceDetails(rmk.database.dbobjects.Invoice selectedInvoice) {
		Desktop.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ErrorLogger.getInstance().logDebug(ErrorLogger.getCallerFunction() + ":" + selectedInvoice, false);
		try {

			if (selectedInvoice == null)
				throw new Exception("No invoice?");

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
			select(screen);

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

			rmk.database.dbobjects.Customer customer;
			Vector customers = new Vector();
			customer = sys.customerInfo.getCustomerByID(customerID);

			ErrorLogger.getInstance().logDebug(ErrorLogger.getCallerFunction() + ":" + customer, false);
			
			customers.add(customer);

			Vector addressVect = new Vector();
			rmk.database.dbobjects.Address address=null;
			if(address == null && customer.getCurrentAddress() > 0)
				address = sys.customerInfo.getCustomerAddress(customer.getCurrentAddress());
			if(address == null )
				address = sys.customerInfo.getCurrentAddress(customerID);			
			if (address == null)
				address = new rmk.database.dbobjects.Address(0);
			
			Vector invoice = new Vector();
			final CustomerScreen screen = new CustomerScreen();
			
			String title = CustomerScreen.getCustomerScreenTitle(customerID);
			screen.setTitle(title);
			Desktop.getInstance().add(screen);
			
			screen.setData(customer, address, sys.invoiceInfo.getInitialInvoices(customer));
			screen.initialSelection();
//			SwingUtilities.invokeLater(new Runnable() {
//				public void run() {
//					screen.initialSelection();
//				}
//			});			
			
			select(screen);

			Desktop.getInstance().repaint();
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayCustomer", e);
		} // end of try-catch
		Desktop.getInstance().setCursor(null);
	}

	public void displayPartsList() {
		ErrorLogger.getInstance().logDebug("", false);
		try {
			PartsScreen screen = new PartsScreen();
			Desktop.getInstance().add(screen);

			screen.setData((DBObject)null);
			select(screen);
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayPartsList", e);
		} // end of try-catch
	}

	public void displayHistoryList() {
		ErrorLogger.getInstance().logDebug(ErrorLogger.getCallerFunction() + ":displayHistoryList()", false);
		try {
			InvoiceHistoryScreen screen = new InvoiceHistoryScreen();
			Desktop.getInstance().add(screen);
			Vector invoice = new Vector();

			screen.setData(sys.invoiceInfo.getHistory(7));
			select(screen);
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayPartsList", e);
		} // end of try-catch
	}

	public void displayCustomerList(Vector customerLst) {
		ErrorLogger.getInstance().logDebug("", false);
		try {
			CustomerSelectionScreen screen = new CustomerSelectionScreen();
			Desktop.getInstance().add(screen);

			screen.setData(customerLst);
			select(screen);
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:displayCustomerList", e);
		} // end of try-catch
	}

	public void displayDealerList(Vector dealerLst) {
		ErrorLogger.getInstance().logDebug("displayDealerList()", false);
		try {
			CustomerSelectionScreen screen = new CustomerSelectionScreen();
			screen.setTitle("Dealers");
			Desktop.getInstance().add(screen);

			screen.setData(dealerLst);
			select(screen);
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
		InvoiceItemScreen screen = null;
		Desktop.getInstance().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		Invoice inv = item.getParent();
		if(inv == null)
			sys.invoiceInfo.getInvoice(invoiceNum);
		
		try {
			screen = new InvoiceItemScreen();

			if (invoiceNum != inv.getInvoice())
				inv = null;

			String title = "Invoice : ";
			if (inv != null)
				title += inv.getInvoice();

			title += " Item: ";
			
			if(item != null){
				title += item.getInvoiceEntryID();
				ErrorLogger.getInstance().logMessage(
						"DispInvItem:" + item);
			}
			
			int year = sys.invoiceInfo.getPricingYear(inv.getInvoice());
			title += " Year-" + year;

			Customer customer = inv.getParent();
			
			screen.setTitle(title);

			Desktop.getInstance().add(screen);

			if(item == null) // new item
				item = new InvoiceEntries(0);
			
			item.setParent(inv);
			inv.setParent(customer);
			((InvoiceItemScreen)screen).setData(item);
			select(screen);
		} catch (Exception e) {
			ErrorLogger.getInstance().logError("ScreenController:invoiceItem",
					e);
		} // end of try-catch
		Desktop.getInstance().setCursor(null);
		return (IScreen) screen;
	}

	public void newCustomer() {
		ErrorLogger.getInstance().logDebug("", false);
		try {
			CustomerScreen screen = new CustomerScreen();
			Desktop.getInstance().add(screen);
			Customer cust = new rmk.database.dbobjects.Customer(0);
			cust.setInvoices(new Vector());
			Address address = new Address(0);
			screen.setData(cust, address, cust.getInvoices());
			select(screen);
		} catch (Exception e) {
			ErrorLogger.getInstance().logError("ScreenController:newCustomer",
					e);
		} // end of try-catch
	}

	public void invoicePayments(Invoice invoice, Vector payments) {
		try {
			InvoicePaymentsScreen screen = new InvoicePaymentsScreen();

			String title = "Invoice Payments : ";
			if (invoice != null) {
				title += invoice.getInvoice();
				ErrorLogger.getInstance().logDebug(title, true);
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
//			InvoicePaymentsScreen screen = new InvoicePaymentsScreen();
	
			Vector payments = new Vector();
			invoicePayments(invoice, payments);
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:invoicePayments", e);
		} // end of try-catch

	}
	
	
	public void invoiceSearch() {
		ErrorLogger.getInstance().logDebug("", false);

		try {
			InvoiceSearchScreen screen = new InvoiceSearchScreen();
			screen.setTitle("Invoice Query");

			Desktop.getInstance().add(screen);
			select(screen);

		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
					"ScreenController:invoiceSearch", e);
		} // end of try-catch
	}

	public rmk.gui.IScreen getInvoiceScreen(
			rmk.database.dbobjects.Invoice invoice) {
//		ErrorLogger.getInstance().logDebug("getInvoiceScreen:" + invoice, false);

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

	public rmk.gui.IScreen getCustomerScreen(long custID) {
		rmk.gui.IScreen screen;
		String title = "";
//		ErrorLogger.getInstance().logDebug("" + invoice, true);
//		ErrorLogger.getInstance().logDebug(ErrorLogger.getCallerFunction()   + ":getCustomerScreen()" + ":" + custID, false);
		
		title = CustomerScreen.getCustomerScreenTitle(custID);
		return rmk.gui.ApplicationMenu.getInstance().findScreen(title);
	}
	
	public rmk.gui.IScreen getPaymentsScreen(
			rmk.database.dbobjects.Invoice invoice) {
		ErrorLogger.getInstance().logDebug(ErrorLogger.getCallerFunction() + ":getPaymentsScreen()" + invoice.getInvoice(), false);

		rmk.gui.IScreen screen;
		String title = "";
		title = "Invoice Payments : " + invoice.getInvoice();
		return rmk.gui.ApplicationMenu.getInstance().findScreen(title);
	}

	public void displayPreferencesScreen() {
		ErrorLogger.getInstance().logDebug("", false);
		try {
			PreferencesScreen screen = new PreferencesScreen();
			screen.setTitle("Preferences");
			Desktop.getInstance().add(screen);
			select(screen);
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