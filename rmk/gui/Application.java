package rmk.gui;

import javax.swing.*;

import Configuration.Config;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import carpus.gui.BasicToolBar;
import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.SignalProcessor;
import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.Invoice;
import rmk.reports.MergeFiles;

public class Application extends JFrame implements ActionListener, KeyListener {
    Desktop desktop;
    rmk.DataModel sys;
    BasicToolBar toolbar;
    public static final String version="2007_12_29";

    //============================================================================
    public Application() throws Exception{
	final JMenuBar mb = new JMenuBar();

        desktop = Desktop.getInstance();
	desktop.setFrame(this);
	JPanel panel = new JPanel();

	toolbar = new BasicToolBar(null, new String[] {"General Search", "Dealers", "Ship", "Parts", "History", "RecentlyDone", "Preferences", "Log an Error"}, 
			new String[] {"generalsearch", "dealerlist", "ship", "parts", "history", "RecentlyDone", "Preferences", "ERROR"},
			new String[] {"General Search",	"Dealers", "Ship Invoices", "Part Configuration", "Invoice Access History", "RecentlyDone", "", "ERROR"}
	);
	toolbar.setFloatable(false);
	toolbar.addActionListener(this);
	toolbar.getButton(0).setMnemonic(KeyEvent.VK_S);
	toolbar.getButton(4).setMnemonic(KeyEvent.VK_H);
	
//	KeyStroke kESC= KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
//	desktop.registerKeyboardAction(this, "ESCAPE", kESC, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//	this.registerKeyboardAction(this, "ESCAPE", kESC, JComponent.WHEN_IN_FOCUSED_WINDOW);

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
    		//	    DBGuiModel model = new rmk.gui.DBGuiModel(rmk.gui.DBGuiModel.CUSTOMER_DATA, lst);
    		rmk.ScreenController.getInstance().displayCustomerList(lst);
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
    		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "customer");
    		if(items.size() == 1){
    			rmk.ScreenController.getInstance().displayCustomer(((Customer)items.get(0)).getCustomerID());
    		} else {
//    			DBGuiModel model = new rmk.gui.DBGuiModel(rmk.gui.DBGuiModel.CUSTOMER_DATA, items);
    			rmk.ScreenController.getInstance().displayCustomerList(items);
    		}
    	} else if (type == rmk.database.dbobjects.Invoice.class){ // invoice
    		DBGuiModel model = new DBGuiModel();
    		rmk.ScreenController.getInstance().displayInvoiceDetails((Invoice)items.get(0));
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
    		rmk.ScreenController.getInstance().displayDealerList(sys.customerInfo.getDealers());
    	} else if (command.equals("SHIP")){
    		shipInvoices();
    	} else if (command.equals("RECENTLYDONE")){
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
    	} else if (command.equals("ERROR")){
    		rmk.gui.Dialogs.getAndLogAnErrorReport();
    	} else if (command.equals("ESCAPE")){
    		ApplicationMenu.getInstance().notifyLastScreen(ScreenController.BUTTON_CANCEL, 0);
    		e = null;
    	}else{
    		ErrorLogger.getInstance().logMessage("Application:actionPerformed():" + command);
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

	ErrorLogger.getInstance().logMessage("App Version " + version + " Started:" + new Date() );

	Desktop.getInstance().repaint();
	//  	Desktop.getInstance().paintComponent(Desktop.getInstance().getGraphics());
    }
    //============================================================================

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent arg0) {
		ErrorLogger.getInstance().logMessage("Pressed Key :" + arg0);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
