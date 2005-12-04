package rmk.gui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import rmk.ErrorLogger;
import rmk.ScreenController;

import carpus.gui.BasicToolBar;

/*
 * Created on Apr 30, 2005
 */

/**
 * @author David
 *
 */
public class ButtonBarTranslator implements ActionListener {
	BasicToolBar toolBar=null;
	IScreen parent=null;
	
	public ButtonBarTranslator(IScreen screen, BasicToolBar buttonBar){
		toolBar = buttonBar;
		toolBar.addActionListener(this);
		parent = screen;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		if(command.equalsIgnoreCase("Details")){
			parent.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS, arg0.getID());
		}else if(command.equalsIgnoreCase("Cancel")){
			parent.buttonPress(ScreenController.BUTTON_CANCEL, arg0.getID());
		}else if(command.equalsIgnoreCase("SAVE")){
			parent.buttonPress(ScreenController.BUTTON_SAVE, arg0.getID());
		}else if(command.equalsIgnoreCase("INVOICE")){
			parent.buttonPress(ScreenController.BUTTON_DISPLAY_INVOICE, arg0.getID());
		}else if(command.equalsIgnoreCase("Acknowledgment")){
			parent.buttonPress(ScreenController.BUTTON_DISPLAY_ACK_RPT, arg0.getID());
		}else if(command.equalsIgnoreCase("SHIP")){
			parent.buttonPress(ScreenController.BUTTON_SHIP, arg0.getID());
		}else if(command.equalsIgnoreCase("Add")){
			parent.buttonPress(ScreenController.BUTTON_ADD, arg0.getID());
		}else if(command.equalsIgnoreCase("Pricing")){
			parent.buttonPress(ScreenController.BUTTON_PART_PRICING, arg0.getID());
		}else if(command.equalsIgnoreCase("Inactive") || command.equalsIgnoreCase("Active")){
			parent.buttonPress(ScreenController.BUTTON_PARTS_ACTIVE_TOGGLE, arg0.getID());			
		}else{
			System.out.println("Need to add translation code:" + command);
			ErrorLogger.getInstance().TODO();
		}
	}

}
