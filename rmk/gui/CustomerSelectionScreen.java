package rmk.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CustomerSelectionScreen extends Screen{
    rmk.gui.ScreenComponents.CustomerListPanel customerListPanel;

    public CustomerSelectionScreen(){
        super("Customers");
    	getContentPane().setLayout(new BorderLayout());

	customerListPanel = new rmk.gui.ScreenComponents.CustomerListPanel();
	customerListPanel.addActionListener(this);
	getContentPane().add(customerListPanel);

	buttonBar = new carpus.gui.BasicToolBar(null, 
						new String[] 
	    {"Add", "Cancel"},
						new String[] 
	    {"Add", "Cancel"},
						new String[] 
	    {"Add", "Cancel"});
	buttonBar.addActionListener(this);
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
//  	System.out.println(this.getClass().getName() + ":"+ "Window Activated.");
//  	("Internal frame activated", e);
    }


    //==========================================================
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase();
	
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
	    System.out.println(this.getClass().getName() + ":" + command + "|");
	}
	
    }
    public void setData(DBGuiModel model){
		customerListPanel.setData(model);
		buttonBar.enableButton(3,true);
    }

    public void addActionListener(ActionListener listener){
	if(listeners == null) listeners = new Vector();
	if(!listeners.contains(listener)) listeners.addElement(listener);
    }
    public static void main(String args[]) throws Exception{
	Application.main(args);
    }

}
