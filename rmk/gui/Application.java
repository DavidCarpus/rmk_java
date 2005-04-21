package rmk.gui;

import javax.swing.*;

import Configuration.Config;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import carpus.gui.BasicToolBar;
import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.Invoice;
import rmk.reports.MergeFiles;

public class Application extends JFrame implements ActionListener {
    Desktop desktop;
    rmk.DataModel sys;
    BasicToolBar toolbar;
    public static final String version="2005_04_20 B";

    //============================================================================
    public Application() throws Exception{
	final JMenuBar mb = new JMenuBar();

        desktop = Desktop.getInstance();
	desktop.setFrame(this);
	JPanel panel = new JPanel();

	toolbar = new BasicToolBar(null, new String[] {"General Search", "Dealers", "Ship", "Parts", "History", "ToDo", "Preferences"}, 
				   new String[] {"generalsearch", "dealerlist", "ship", "parts", "history", "ToDo", "Preferences"},
				   new String[] {"General Search",
						 "Dealers", "Ship Invoices", "Part Configuration", "Invoice Access History", "ToDo", ""}
				   );
	toolbar.setFloatable(false);
	toolbar.addActionListener(this);
	toolbar.getButton(0).setMnemonic(KeyEvent.VK_S);
	toolbar.getButton(4).setMnemonic(KeyEvent.VK_H);

	GridBagLayout gridBag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(panel, c);

	panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
	panel.add(toolbar);
	panel.add(desktop);

        setContentPane(panel);

        setJMenuBar(ApplicationMenu.getInstance());
  	ApplicationMenu.getInstance().addActionListener(this);

    }
    
    public void init(){
    	sys = rmk.DataModel.getInstance();
    }
    
    //============================================================================
    void displayCustomers(Vector lst){
	if(lst.size() == 1){
	    rmk.ScreenController.getInstance().displayCustomer(((Customer)lst.get(0)).getCustomerID());
	} else {
	    DBGuiModel model = new rmk.gui.DBGuiModel(rmk.gui.DBGuiModel.CUSTOMER_DATA, lst);
	    rmk.ScreenController.getInstance().displayCustomerList(model);
	}
    }
//      //============================================================================
//      void displayInvoice(long invoiceNumber){
//  	rmk.ScreenController.getInstance().displayInvoiceDetails(invoiceNumber);
//      }
    //============================================================================
    void generalSearch(Vector items){
	if(items == null || items.size() ==0 || items.get(0) == null) 
	    return;
//  	if(items.get(0).
        Class type = items.get(0).getClass();	
	Component parent = Desktop.getInstance();;
	parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        if (type == rmk.database.dbobjects.Customer.class){ // customer
	    System.out.println(this.getClass().getName() + ":"+ "customer");
	    if(items.size() == 1){
		rmk.ScreenController.getInstance().displayCustomer(((Customer)items.get(0)).getCustomerID());
	    } else {
		DBGuiModel model = new rmk.gui.DBGuiModel(rmk.gui.DBGuiModel.CUSTOMER_DATA, items);
		rmk.ScreenController.getInstance().displayCustomerList(model);
	    }
	} else if (type == rmk.database.dbobjects.Invoice.class){ // invoice
	    DBGuiModel model = new DBGuiModel();
	    rmk.ScreenController.getInstance().displayInvoiceDetails((Invoice)items.get(0), model);
	}
	parent.setCursor(null);

    }
    //============================================================================
    //============================================================================
    //React to menu selections.
    public void actionPerformed(ActionEvent e) {
	String command = (""+e.getActionCommand()).toUpperCase();
	int index=0;

	if (command.equals("QUIT")){ //0
            quit();
        } else if (command.equals("NEW")){//1
	    rmk.ScreenController.getInstance().newCustomer();
	    return;
        } else if (command.equals("GENERALSEARCH")){//3
	    Vector items = rmk.gui.Dialogs.generalSearch();
	    generalSearch(items);
        } else if (command.equals("PARTS")){
	    	rmk.ScreenController.getInstance().displayPartsList();
		} else if (command.equals("HISTORY")){
			rmk.ScreenController.getInstance().displayHistoryList();
		} else if (command.equals("PREFERENCES")){
			rmk.ScreenController.getInstance().displayPreferencesScreen();
			
        } else if (command.startsWith("DEALERLIST")){
	    Vector lst = sys.customerInfo.getDealers();
	    DBGuiModel model = new rmk.gui.DBGuiModel(rmk.gui.DBGuiModel.CUSTOMER_DATA, lst);
	    rmk.ScreenController.getInstance().displayDealerList(model);
        } else if (command.equals("SHIP")){
	    shipInvoices();
	} else if (command.equals("TODO")){
		Dialogs.displayToDo();
	//-------------------------------
        } else if (command.equals("BLADELIST")){
	    Dialogs.bladeList(false, null);
        } else if (command.equals("TAXORDERED")){
    	    Dialogs.taxOrderedReport();
        } else if (command.equals("TAXSHIPPED")){
    	    Dialogs.taxShippedReport();
        } else if (command.equals("PARTLISTREPORT")){
    	    Dialogs.partListReport();
        } else if (command.equals("INVOICE_SEARCH")){
        	ScreenController.getInstance().invoiceSearch();
        	
        } else if (command.equals("SPEC_REQUEST")){
    	    String location = Configuration.Config.getMergeFileLocation() + "DealerSpec.txt";
    	    String message = "No data to save to:\n" + location;
    	    String heading = "Not Saved";
    	    if(MergeFiles.generateMergeFile(MergeFiles.MERGE_TYPE_DEALER_SPEC, location)){
    		message = "Data saved to:\n" + location;
    		heading = "Saved";
    	    }
    	    JOptionPane.showMessageDialog(null, message, heading, JOptionPane.INFORMATION_MESSAGE);
    	//-------------------------------
        } else if (command.equals("WIERD_INVOICES")){
    	    String location = Configuration.Config.getMergeFileLocation() + "WierdInvoices.txt";
    	    String message = "No data to save to:\n" + location;
    	    String heading = "Not Saved";
    	    if(MergeFiles.generateMergeFile(MergeFiles.MERGE_TYPE_WIERD_BALANCE_DUE, location)){
    		message = "Data saved to:\n" + location;
    		heading = "Saved";
    	    }
    	    JOptionPane.showMessageDialog(null, message, heading, JOptionPane.INFORMATION_MESSAGE);
    	//-------------------------------
        } else if (command.equals("BALANCE_DUE")){
	    String location = Configuration.Config.getMergeFileLocation() + "BalanceDue.txt";
	    String message = "No data to save to:\n" + location;
	    String heading = "Not Saved";

	    if(MergeFiles.generateMergeFile(MergeFiles.MERGE_TYPE_BALANCE_DUE, location)){
		message = "Data saved to:\n" + location;
		heading = "Saved";
	    }
	    JOptionPane.showMessageDialog(null, message, heading, JOptionPane.INFORMATION_MESSAGE);
	//-------------------------------
        } else if (command.equals("MULTI_INVOICE")){
    	    rmk.gui.Dialogs.multiInvoice();
        } else if (command.equals("TEST")){
//    	    try {
//                rmk.gui.Dialogs.test();
//            } catch (Exception err) {
//                err.printStackTrace();
//            }

	//-------------------------------
	}else{
	    System.out.println("Application:actionPerformed():" + command);
	    System.out.println(e);
	}
    }
    //============================================================================
    //============================================================================
    private void shipInvoices(){
	rmk.Processing.getInstance().shipInvoices();
    }

    //============================================================================
    protected void quit() {
    	if(!ApplicationMenu.getInstance().unsavedScreens())
			System.exit(0);
		else
			JOptionPane.showMessageDialog(null, "Data not saved", "Cannot Exit"
					  , JOptionPane.ERROR_MESSAGE);
    }

    //============================================================================
    private void test(){
	//    	newCustomer();
	//  	rmk.ScreenController.getInstance().newCustomer();

	//  	    java.util.Vector lst = sys.getInvoiceItem(94148);
	//  	    DBGuiModel model = new rmk.gui.DBGuiModel(rmk.gui.DBGuiModel.KNIFE_DATA, lst);
	//  	    rmk.ScreenController.getInstance().invoiceItem(model);


//        	rmk.ScreenController.getInstance().displayCustomer(230); // 18422, 1, 5, 7, 80000, 23705
	//1005 - sullivan's
	rmk.ScreenController.getInstance().displayHistoryList();
	
//	DBGuiModel model = new DBGuiModel();
//	Invoice inv = sys.invoiceInfo.getInvoice(44417);
//	rmk.ScreenController.getInstance().displayInvoiceDetails(inv, model);
	// 44469, 41859, 42498, 60000, 60001, 44575, 44800
	// 42682, 50004, 42563, 42684, 44827, 53163, 53382

//  	rmk.ScreenController.getInstance().displayPartsList();

//        	    DBGuiModel model = new DBGuiModel();
//  	    Invoice inv = sys.invoiceInfo.getInvoice(35906);
//  	    Vector invoice = new Vector();
//  	    invoice.add(inv);
//        	    model.setInvoiceData(invoice);
//  	    Vector custData = new Vector();
//  	    try {
//  		custData.add(sys.customerInfo.getCustomerByID(inv.getCustomerID()));
//  		model.setCustomerData(custData);
//  	    } catch (Exception e){
//  	    } // end of try-catch
//  //  	    getInvoiceItemsData
//        	    model.setKnifeData(sys.invoiceInfo.getInvoiceItem(300025)); // 111979, 112952, 112994
//        	    rmk.ScreenController.getInstance().invoiceItem(model);

	//    	    model.setKnifeData(null);
	//    	    rmk.ScreenController.getInstance().invoiceItem(model);

	//  	Vector lst = sys.getCustomersFromPhone("(407)");
	//      	Vector lst = sys.getCustomersByLastName("sports");
	//      	displayCustomers(sys, lst);
    }
    //============================================================================

    //============================================================================
    public static void main(String args[]) throws Exception {
	Application app = new Application();
	String lookAndFeel = Config.getLookAndFeel();
    if(lookAndFeel != null)
        UIManager.setLookAndFeel(lookAndFeel);
	if(carpus.util.SystemPrefrences.runningOnWindows()){
	    carpus.gui.GJApp.launch(app, "RMK System",0,0,1100,740);
	}else{
	    //      	    carpus.gui.GJApp.launch(new Application(), "RMK System",100,100,500,650);
	    carpus.gui.GJApp.launch(app, "RMK System",50,50,930,900);
	}
	try {Thread.sleep(500);}
	catch (Exception e) {}
	app.init();

	try {Thread.sleep(2000);}
	catch (Exception e) {}
//	if(Configuration.Config.IDE)
//	    app.test();	
	ErrorLogger.getInstance().logMessage("App Version " + version + " Started:" + new Date() );

	Desktop.getInstance().repaint();
	//  	Desktop.getInstance().paintComponent(Desktop.getInstance().getGraphics());
    }
    //============================================================================

}
