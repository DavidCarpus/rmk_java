package rmk.gui;

import javax.swing.*;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.SignalProcessor;
import rmk.database.dbobjects.DBObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class CustomerSelectionScreen extends Screen{
    rmk.gui.ScreenComponents.CustomerListPanel customerListPanel;

    public CustomerSelectionScreen(){
        super("Customers");
    	getContentPane().setLayout(new BorderLayout());

	customerListPanel = new rmk.gui.ScreenComponents.CustomerListPanel();
	customerListPanel.setParent(this);
//	customerListPanel.addActionListener(this);
	getContentPane().add(customerListPanel);

	buttonBar = new carpus.gui.BasicToolBar(null, 
						new String[] 
	    {"Add", "Cancel"},
						new String[] 
	    {"Add", "Cancel"},
						new String[] 
	    {"Add", "Cancel"});
	ButtonBarTranslator translator = new ButtonBarTranslator(this, buttonBar);
//	buttonBar.addActionListener(this);
	buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));
//  	buttonBar.enableButton(1, false);
//  	buttonBar.enableButton(1, false);
	buttonBar.enableButton(3,false);

	getContentPane().add( buttonBar, BorderLayout.SOUTH);

	setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	setSize(700,500); //...Then set the window size or call pack...
    }

    public boolean isEdited(){return false;}
    public Vector getEditedData(){return null;}

    //==========================================================
    public void internalFrameActivated(javax.swing.event.InternalFrameEvent e) {
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Window Activated.");
//  	("Internal frame activated", e);
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

        if (command.equals("CANCEL")) { //cancel
	    defaultCancelAction();
	    return;
	} else if (command.equals("CUSTOMERDETAILS")) { //
	    rmk.ScreenController.getInstance().displayCustomer(customerListPanel.getSelectedItemID());
	    return;
	} else if (command.equals("ADD")) { 
	    rmk.ScreenController.getInstance().newCustomer();
	    return;	    
	} else if (command.equals("MERGE")) { 
	    Dialogs.mergeIntoCustomer(customerListPanel.getSelectedItemID());
	    return;	    
	} else {
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "|");
	}
	
    }
    public void setData(DBGuiModel model){
		customerListPanel.setData(model);
		buttonBar.enableButton(3,true);
    }
    public void setData(DBObject item){
    	ErrorLogger.getInstance().TODO();
    }

    public void updateOccured(DBObject itemChanged, int changeType, DBObject parentItem){
		ErrorLogger.getInstance().TODO();
     }
    
	public void buttonPress(int button, int id) {
		switch (button) {
		case ScreenController.BUTTON_CANCEL:
			defaultCancelAction();
			SignalProcessor.getInstance().removeScreen(this);
		break;
		
		case ScreenController.BUTTON_SELECTION_DETAILS:
		{
			ApplicationMenu.getInstance().pushScreenToTopOfStack(this);
			rmk.ScreenController.getInstance().displayCustomer(id);
		}
		break;
		default:
			ErrorLogger.getInstance().TODO();
		break;
		}
	}
	
}
