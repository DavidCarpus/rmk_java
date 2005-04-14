package rmk.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import rmk.ErrorLogger;
import rmk.database.dbobjects.Parts;
import rmk.database.dbobjects.PartPrices;

public class PartsScreen extends Screen{
//      ActionListener parentFrame;    
    rmk.gui.ScreenComponents.PartsPanel partPnl;
    rmk.gui.ScreenComponents.PartsFeaturesPanel featuresPnl;
    rmk.gui.ScreenComponents.PartsListPanel partsList;
//      long invoiceNumber;
    boolean active = true;
    boolean priceChange=false;
    rmk.DataModel sys = rmk.DataModel.getInstance();
    Parts part=null;

    //==========================================================
    public PartsScreen(){
	super("Parts");

	getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));


	partPnl = new rmk.gui.ScreenComponents.PartsPanel();
	partPnl.addActionListener(this);
	partPnl.setEnabled(false);
  	getContentPane().add(partPnl);

	featuresPnl = new rmk.gui.ScreenComponents.PartsFeaturesPanel();
	featuresPnl.addActionListener(this);
	featuresPnl.setEnabled(false);
  	getContentPane().add(featuresPnl);
	featuresPnl.setVisible(false);

	partsList = new rmk.gui.ScreenComponents.PartsListPanel();
	partsList.addActionListener(this);
  	getContentPane().add(partsList);

	buttonBar.addButton(null, "InActive","InActive","InActive Items");
	buttonBar.addButton(null, "Features","Features","Features");
	buttonBar.addButton(null, "New","New","Add New Part");
	buttonBar.addButton(null, "Pricing","Pricing","Quick Edit Prices");
	buttonBar.enableButton(3, false);
	getContentPane().add(buttonBar);

  	setPreferredSize(new Dimension(800,640));
    	pack();
    }
    //==========================================================
    public PartsScreen(DBGuiModel data){
	super("Invoice Details");
	setData(data);
    }
//      public void processKeyEvent(KeyEvent e){
//  	System.out.println(this.getClass().getName() + ":"+ e);
	
//      }
    //==========================================================
    public boolean isEdited(){	return (partPnl.isEdited());}

    public void setActive(boolean active){
	this.active = active;
	Vector lst = new Vector();
	for(java.util.Enumeration enum = sys.partInfo.getParts(); enum.hasMoreElements();){
	    Parts part = (Parts)enum.nextElement();
	    if(part.isActive() && active)
		lst.add(part);
	    if(!part.isActive() && !active)
		lst.add(part);
	}
//    	Vector parts = new Vector(sys.partInfo.getParts());
	partsList.setData((java.util.Collection)lst);
    }
    void addNewPart(){
        Parts part = Dialogs.getNewPart();
        if(part != null){
	    	Vector lst = new Vector();
	    	lst.add(part);
	    	Configuration.Config.getDB().saveItems("Parts", lst);
	    	sys.partInfo.newPart(part);
	    	partPnl.setData(part);	    	
	    	setActive(true);
        }
    }
    //----------------------------------------------------------
    public void setData(DBGuiModel model){
	setActive(active);
	buttonBar.enableButton(0, isEdited());
//  	partsList.setData(sys.partInfo.getParts());
	// null method, load data from DB
    }
    //==========================================================
    private void saveData(){
	Parts part = (Parts)partPnl.getData();

	Vector lst = new Vector();
	lst.add(part);
	Configuration.Config.getDB().saveItems("Parts", lst);
	if(priceChange){
	    PartPrices newPrice = partPnl.getPriceChange();
//  	    System.out.println(this.getClass().getName() + ":"+ newPrice);
	    lst = new Vector();
	    lst.add(newPrice);
	    Configuration.Config.getDB().saveItems("PartPrices", lst);
	    
	    priceChange = false;
	}

	setActive(active); // reload list
	partPnl.setData(part);
  	buttonBar.enableButton(0, isEdited());
    }
    //==========================================================
    public void internalFrameActivated(InternalFrameEvent e) {
//    	System.out.println(this.getClass().getName() + ":"+ "Window Activated.");
    }
    //==========================================================
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase().trim();

	//-----------------------------
	if (command.equals("CANCEL")) { //cancel
	    defaultCancelAction();
	    //-----------------------------
	} else if (command.equals("INACTIVE") || command.equals("ACTIVE") ) { //
	    setActive(!active);
	    buttonBar.setButtonLabel(2, active?"InActive":"Active");
	    //-----------------------------
	} else if (command.equals("NEW")) { //
	    addNewPart();
	    //-----------------------------
  	} else if (command.equals("PARTSCHANGE")) { //
	    buttonBar.enableButton(0, true);
	//-----------------------------
  	} else if (command.equals("PARTPRICECHANGE")) { //
	    buttonBar.enableButton(0, true);
	    priceChange = true;
	//-----------------------------
  	} else if (command.equals("FEATURES")) {
	    boolean state = !featuresPnl.isVisible();
	    featuresPnl.setVisible(state);
	    partPnl.setVisible(!state);
	//-----------------------------
  	} else if (command.equals("PARTSDETAILS")) { //
	    int partID= e.getID();
	    partPnl.setEnabled(true);
	    part = sys.partInfo.getPart(partID);
	    if(part == null){
	    	ErrorLogger.getInstance().logError("**** Unable to retrieve part: "+ partID, new Exception());
	    	return;
	    }
	    partPnl.setData(part);

	//-----------------------------
  	} else if (command.equals("SAVE")) { //INFO CHANGED
	    saveData();
	    notifyListeners("INVOICE_SAVED");
	//-----------------------------
  	} else if (command.equals("PRICING")) { //Yearly Price edit/change
  		Dialogs.updatePricing();
  	} else {  // Undefined
  		System.out.println(this.getClass().getName() + ":Undefined:" + command + "|");
  	}

	buttonBar.enableButton(3, (part != null && part.getPartType() == sys.partInfo.getPartTypeID("Knives")));
	featuresPnl.setData(part);
    }
    //==========================================================
    //==========================================================
    //==========================================================
    public static void main(String args[]) throws Exception{
	Application.main(args);
    }
    //==========================================================
    //==========================================================
    //==========================================================
}




