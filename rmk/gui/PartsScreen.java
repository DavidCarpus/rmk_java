package rmk.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.SignalProcessor;
import rmk.database.dbobjects.DBObject;
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
	partPnl.setParentScreen(this);
	partPnl.setEnabled(false);
	partPnl.setParentScreen(this);
  	getContentPane().add(partPnl);

	featuresPnl = new rmk.gui.ScreenComponents.PartsFeaturesPanel();
	featuresPnl.setParentScreen(this);
	featuresPnl.setEnabled(false);
  	getContentPane().add(featuresPnl);
	featuresPnl.setVisible(false);
	featuresPnl.setParentScreen(this);

	partsList = new rmk.gui.ScreenComponents.PartsListPanel();
	partsList.setParent(this);
  	getContentPane().add(partsList);
  	partsList.setParent(this);

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
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ e);
	
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
    	setData((DBObject)null);
    }
    public void setData(DBObject item){	
    	setActive(active);
    	buttonBar.enableButton(0, isEdited());
//    	 null method, load data from DB
    }
    //==========================================================
    private void saveData(){
	Parts part = (Parts)partPnl.getData();

	Vector lst = new Vector();
	lst.add(part);
	Configuration.Config.getDB().saveItems("Parts", lst);
	if(priceChange){
	    PartPrices newPrice = partPnl.getPriceChange();
//  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ newPrice);
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
//    	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Window Activated.");
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
    ErrorLogger.getInstance().logDebugCommand(command);

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
	    int partID= 0;
	    
	    // TODO: get ID from command
//	    partID = e.getID();
	    
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
  		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
  	}

	buttonBar.enableButton(3, (part != null && part.getPartType() == sys.partInfo.getPartTypeID("Knives")));
	featuresPnl.setData(part);
    }

	public void updateOccured(DBObject itemChanged, int changeType, DBObject parentItem){
		String parentName="";
		if(parentItem != null) parentName = parentItem.getClass().getName();
		String itemName="";
		if(itemChanged != null) itemName = itemChanged.getClass().getName();

		switch(changeType){
		case ScreenController.UPDATE_EDIT:
		{
		    buttonBar.enableButton(0, true);
		    if(itemName.indexOf("PartPrices")>0){
		    	priceChange = true;
		    }
		}
		break;
		default:
			ErrorLogger.getInstance().TODO();
		}
	}
    
    
	public void buttonPress(int button, int id) {
		switch(button){
		case ScreenController.BUTTON_CANCEL:
		{
			defaultCancelAction();
			SignalProcessor.getInstance().removeScreen(this);
		}
		break;

		case ScreenController.BUTTON_SELECTION_DETAILS:
		{
		    int partID= 0;
		    partID = id;
		    partPnl.setEnabled(true);
		    part = sys.partInfo.getPart(partID);
		    if(part == null){
		    	ErrorLogger.getInstance().logError("**** Unable to retrieve part: "+ partID, new Exception());
		    	return;
		    }
		    partPnl.setData(part);
		}
		break;
		
		case ScreenController.BUTTON_SAVE:
		{
			saveData();
		}
		break;
		
		case ScreenController.BUTTON_PARTS_ACTIVE_TOGGLE:
		{
		    setActive(!active);
		    buttonBar.setButtonLabel(2, active?"InActive":"Active");
		}
		break;
		
		case ScreenController.BUTTON_PART_PRICING:
		{
	  		Dialogs.updatePricing();
		}
		break;
		
		default:
	       	ErrorLogger.getInstance().logButton(button, id);
		}
	}
	

}




