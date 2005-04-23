package rmk;
import java.util.Enumeration;
import java.util.Vector;

import rmk.database.dbobjects.Invoice;
import rmk.gui.*;

import java.awt.*;

/**
 * Describe class <code>ScreenController</code> here.
 *
 * @author <a href="mailto:carpus@cs.ucf.edu">David Carpus</a>
 * @version 1.0
 */
public class ScreenController{
	rmk.DataModel sys = rmk.DataModel.getInstance();
	static int cntr=0;

    private static ScreenController instance = new ScreenController();

    IScreen screen[] = new IScreen[4];
//      CustomerSelectionScreen custList;

    public static ScreenController getInstance(){
        long mem1 = Runtime.getRuntime().freeMemory();
		Runtime.getRuntime().gc();
		
//		ErrorLogger.getInstance().logMessage(cntr++ + " FreeMem " + mem1 +" -> " + Runtime.getRuntime().freeMemory());
		return instance;
    }
	public rmk.gui.IScreen bringToFront(String title) {
		rmk.gui.IScreen screen =
			rmk.gui.ApplicationMenu.getInstance().findScreen(title);
		if (screen != null) { // found screen with that title
			screen.bringToFront();
			return screen;
		}
	return null;
    }

	public void displayInvoiceDetails(rmk.database.dbobjects.Invoice selectedInvoice, DBGuiModel model) {
		Desktop.getInstance().setCursor(
			Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {

			if (selectedInvoice == null)
				throw new Exception("No invoice?");
				
			Vector invoices = model.getInvoiceData();
			
			if (invoices == null) {
				ErrorLogger.getInstance().logMessage(
					this.getClass().getName() + ":New inv Vector - null");
				invoices = new Vector();
			} else if (invoices.size() == 0) {
				ErrorLogger.getInstance().logMessage(
					this.getClass().getName()
						+ ":New inv Vector - size() = 0");
				invoices = new Vector();
			} else {
				boolean found = false;
				for (java.util.Enumeration enum = invoices.elements();enum.hasMoreElements();
					) {
					rmk.database.dbobjects.Invoice item =(rmk.database.dbobjects.Invoice) enum.nextElement();
					if (item.getInvoice() == selectedInvoice.getInvoice()) {
						found = true;
						break;
					}
				}
				if (!found) {
					ErrorLogger.getInstance().logMessage(
						this.getClass().getName()
							+ ":"
							+ "New inv Vector - non matched inv#");
					invoices = new Vector();
				}
			}

			if (!invoices.contains(selectedInvoice)){
				invoices.add(selectedInvoice);
			} else{
				invoices.remove(selectedInvoice);
				invoices.add(selectedInvoice);
			}
			model.setInvoiceData(invoices);

			Vector customer = new Vector();
			customer.add(
				sys.customerInfo.getCustomerByID(
					selectedInvoice.getCustomerID()));
			model.setCustomerData(customer);

			model.setInvoiceItemsData(
				sys.invoiceInfo.getInvoiceEntries(selectedInvoice.getInvoice()));

			InvoiceDetailsScreen screen = new InvoiceDetailsScreen();
//			String title = "Invoice : ";
//			if (selectedInvoice != null) {
//				title += selectedInvoice.getInvoice();
//			}
//			screen.setTitle(title);

			Desktop.getInstance().add(screen);
			screen.setData(model);
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":DispInv:" + selectedInvoice);
			select(screen);

//			screen.setVisible(true);
//			screen.toFront();
//			screen.grabFocus();
		} catch (Exception e) {
			ErrorLogger.getInstance().logError(
				"ScreenController:displayInvoiceDetails",
				e);
		} // end of try-catch
		Desktop.getInstance().setCursor(null);
		return;
	}

    public void displayCustomer(long customerID){
	Desktop.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	try {
//  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Disp cust:" + customerID);
	    
	    DBGuiModel model = new DBGuiModel();
	    rmk.DataModel sys = rmk.DataModel.getInstance();

	    rmk.database.dbobjects.Customer customer;
	    Vector customers = new Vector();
	    customer = sys.customerInfo.getCustomerByID(customerID);
	    
	    customers.add( customer);
	    model.setCustomerData(customers);

	    Vector addressVect = new Vector();
	    rmk.database.dbobjects.Address address = sys.customerInfo.getCurrentAddress(customerID);
	    if(address == null) address = new rmk.database.dbobjects.Address(0);
	    addressVect.add( address );
	    model.setAddressData(addressVect);

	    Vector invoice = new Vector();
	    model.setInvoiceData(sys.invoiceInfo.getInitialInvoices(customer));

	    CustomerScreen screen = new CustomerScreen();
	    String title = "Customer : " + customerID;
	    screen.setTitle(title);
	    Desktop.getInstance().add(screen);;
	    screen.setData(model);
		select(screen);

//	    screen.setVisible(true);
//	    screen.toFront();
//		screen.grabFocus();
	    Desktop.getInstance().repaint();
	} catch (Exception e){
	    ErrorLogger.getInstance().logError("ScreenController:displayCustomer", e);
	} // end of try-catch
	Desktop.getInstance().setCursor(null);
    }
    public void displayPartsList(){
	try {
	    PartsScreen screen = new PartsScreen();
	    Desktop.getInstance().add(screen);

	    screen.setData(null);
		select(screen);
//
//	    screen.setVisible(true);
//	    screen.toFront();
//		screen.grabFocus();
	} catch (Exception e){
	    ErrorLogger.getInstance().logError("ScreenController:displayPartsList", e);
	} // end of try-catch
    }
    
	public void displayHistoryList(){
	try {
		InvoiceHistoryScreen screen = new InvoiceHistoryScreen();
		Desktop.getInstance().add(screen);
		DBGuiModel model = new DBGuiModel();
		Vector invoice = new Vector();
		model.setInvoiceData(sys.invoiceInfo.getHistory(7));

		screen.setData(model);
		select(screen);

//		screen.setVisible(true);
//		screen.toFront();
//		screen.grabFocus();
	} catch (Exception e){
		ErrorLogger.getInstance().logError("ScreenController:displayPartsList", e);
	} // end of try-catch
	}

    public void displayCustomerList(DBGuiModel data){
	try {
	    CustomerSelectionScreen screen = new CustomerSelectionScreen();
	    Desktop.getInstance().add(screen);

	    screen.setData(data);
		select(screen);
//	    screen.setVisible(true);
//	    screen.toFront();
//		screen.grabFocus();

	} catch (Exception e){
	    ErrorLogger.getInstance().logError("ScreenController:displayCustomerList", e);
	} // end of try-catch
    }
    public void displayDealerList(DBGuiModel data){
	try {
	    CustomerSelectionScreen screen = new CustomerSelectionScreen();
	    screen.setTitle("Dealers");
	    Desktop.getInstance().add(screen);

	    screen.setData(data);
		select(screen);
//	    screen.setVisible(true);
//	    screen.toFront();
//		screen.grabFocus();

	} catch (Exception e){
	    ErrorLogger.getInstance().logError("ScreenController:displayDealerList", e);
	} // end of try-catch
    }

    public InvoiceDetailsScreen newInvoice(DBGuiModel data){
    	InvoiceDetailsScreen screen = null;
    	Vector invoice =null;
	try {
	    screen = new InvoiceDetailsScreen();
	    invoice = data.getInvoiceData();
	    String title = "Invoice : ";
	    if(invoice != null)
		title += ((rmk.database.dbobjects.Invoice)invoice.get(0)).getInvoice();
	    screen.setTitle(title);
	    Desktop.getInstance().add(screen);
	    screen.setData(data);
		select(screen);
//	    screen.setVisible(true);
//	    screen.toFront();
//		screen.grabFocus();

	    return screen;
	} catch (Exception e){
	    ErrorLogger.getInstance().logError("ScreenController:newInvoice", e);
	} // end of try-catch
	return null;
    }
    public IScreen invoiceItem(long invoiceNum, long itemNum, DBGuiModel data){
//      public void invoiceItem(DBGuiModel data, ActionListener parent){
	 InvoiceItemScreen screen = null;
	 Desktop.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	try {
	    screen = new InvoiceItemScreen();
	    Vector invoiceLst = data.getInvoiceData();
	    Invoice inv = null;
	    if(invoiceNum > 0){
	    for(Enumeration lst	 = invoiceLst.elements(); lst.hasMoreElements();){
	    	inv = (Invoice) lst.nextElement();
	    		if(invoiceNum == inv.getInvoice())
	    			break;
	    	}
	    }
	    if(invoiceNum != inv.getInvoice())
	    	inv=null;
	    
	    String title = "Invoice : ";
	    if(inv != null)
			title += inv.getInvoice();

	    title += " Item: " ;
	    Vector invoiceItem = data.getKnifeData();
	    if(invoiceItem != null){
			title += ((rmk.database.dbobjects.InvoiceEntries)invoiceItem.get(0)).getInvoiceEntryID();
			int year = sys.invoiceInfo.getPricingYear(inv.getInvoice());
			title += " Year-" + year;
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":DispInvItem:" + invoiceItem);
	    }
	    invoiceLst.remove(inv);
	    invoiceLst.insertElementAt(inv,0);
	    
	    screen.setTitle(title);

	    Desktop.getInstance().add(screen);
	    screen.setData(data);
		select(screen);
//  	    screen.addActionListener(parent);
	} catch (Exception e){
	    ErrorLogger.getInstance().logError("ScreenController:invoiceItem", e);
//  	    e.printStackTrace();
	} // end of try-catch
	Desktop.getInstance().setCursor(null);
	return (IScreen)screen;
    }

    public void newCustomer(){
	try {
	    DBGuiModel model = new DBGuiModel();
	    CustomerScreen screen = new CustomerScreen();
	    Vector custVector = new Vector();
		custVector.add(new rmk.database.dbobjects.Customer(0));
		model.setCustomerData(custVector);
	    Desktop.getInstance().add(screen);
	    screen.setData(model);
		select(screen);
	} catch (Exception e){
	    ErrorLogger.getInstance().logError("ScreenController:newCustomer", e);
	} // end of try-catch
    }
    
    public void invoicePayments(DBGuiModel data){
	try {
	    InvoicePaymentsScreen screen = new InvoicePaymentsScreen();
	    Vector invoices = data.getInvoiceData();
		rmk.database.dbobjects.Invoice invoice = (rmk.database.dbobjects.Invoice)invoices.get(invoices.size()-1);
	    String title = "Invoice Payments : ";
	    if(invoice != null)
			title += invoice.getInvoice();

	    screen.setTitle(title);

	    Desktop.getInstance().add(screen);
	    screen.setData(data);
		select(screen);
//	    screen.setVisible(true);
//	    screen.toFront();
//		screen.grabFocus();
	} catch (Exception e){
	    ErrorLogger.getInstance().logError("ScreenController:invoicePayments", e);
	} // end of try-catch
    }
    
    public void invoiceSearch(){
    	try {
    		InvoiceSearchScreen screen = new InvoiceSearchScreen();
    		DBGuiModel model = new DBGuiModel();
	    	rmk.DataModel sys = rmk.DataModel.getInstance();
		    screen.setTitle("Invoice Query");

		    Desktop.getInstance().add(screen);
		    screen.setData(model);
			select(screen);
	    
    	} catch (Exception e){
    	    ErrorLogger.getInstance().logError("ScreenController:invoiceSearch", e);
    	} // end of try-catch
    }
    
    public rmk.gui.IScreen getInvoiceScreen(rmk.database.dbobjects.Invoice invoice){
	rmk.gui.IScreen screen;
	String title="";

	title = "Invoice : " + invoice.getInvoice();
	screen = rmk.gui.ApplicationMenu.getInstance().findScreen(title);
	if(screen != null) return screen;

	title = "Invoice:" + invoice.getInvoice();
	screen = rmk.gui.ApplicationMenu.getInstance().findScreen(title);
	if(screen != null) return screen;

	return null;
    }
    public  rmk.gui.IScreen getCustomerScreen(rmk.database.dbobjects.Invoice invoice){
	rmk.gui.IScreen screen;
	String title="";

	title = "Customer : " + invoice.getCustomerID();
	return rmk.gui.ApplicationMenu.getInstance().findScreen(title);
    }
    public  rmk.gui.IScreen getPaymentsScreen(rmk.database.dbobjects.Invoice invoice){
	rmk.gui.IScreen screen;
	String title="";
	title = "Invoice Payments : " + invoice.getInvoice();
	return rmk.gui.ApplicationMenu.getInstance().findScreen(title);
    }
    
    public  void displayPreferencesScreen(){
    	try {
    		PreferencesScreen screen = new PreferencesScreen();
    	    screen.setTitle("Preferences");
    	    Desktop.getInstance().add(screen);
    		select(screen);
//    	    screen.setVisible(true);
//    	    screen.toFront();
//    		screen.grabFocus();
    	} catch (Exception e){
    	    ErrorLogger.getInstance().logError("ScreenController:displayDealerList", e);
    	} // end of try-catch
    }
    
	void select(IScreen screen){
		screen.setVisible(true);
		screen.toFront();
		screen.grabFocus();	
		screen.bringToFront();
	}

}
