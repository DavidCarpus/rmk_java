package carpus.gui;

import javax.swing.*;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.DBObject;
import rmk.gui.IScreen;

import java.awt.event.*;

public abstract class DataEntryPanel extends JPanel{
    boolean edited = false;
    protected IScreen parentScreen=null;
    carpus.database.DBObject data=null;
	protected carpus.util.Logger errorLog = carpus.util.Logger.getInstance();

	public void setParentScreen(IScreen screen){
		parentScreen = screen;
	}
	
    public boolean isEdited(){
	return edited;
    }
    public void setEdited(boolean value){
	edited = value;
    }
    
    boolean processUniversalCommands(String command){
    	if(command.equals("F1")){
    		parentScreen.buttonPress(ScreenController.BUTTON_F1, 0);
    		return true;
    	}else if(command.equals("F2")){
    		parentScreen.buttonPress(ScreenController.BUTTON_F2, 0);
    		return true;
    	}else if(command.equals("F3")){
    		parentScreen.buttonPress(ScreenController.BUTTON_F3, 0);
    		return true;						
    	}else if(command.equals("F11")){
    		parentScreen.buttonPress(ScreenController.BUTTON_F11, 0);
    		return true;	
    	}else if(command.equals("CANCEL")){
    		parentScreen.buttonPress(ScreenController.BUTTON_CANCEL, 0);
    		return true;	
    	}
    	return false;
    }
//-----------------------------------------------------------------
    public KeyAdapter getFieldEditCheck(String msg, DataEntryPanel panel){	
//  	System.out.println(this.getClass().getName() + ":"+ "getFieldEditCheck:" + msg);
  	return new FieldEditCheck(msg, panel);
    }
    public void setFieldEditCheck(LabeledTextField[] txtFields, 
				  String msg, DataEntryPanel panel){
	KeyAdapter ka = getFieldEditCheck(msg, panel);
	for(int i=0; i<txtFields.length; i++){
	    txtFields[i].addKeyListener(ka);
	}
    }
    public void setFieldEditCheck(JComponent[] txtFields, String msg, DataEntryPanel panel){
	KeyAdapter ka = getFieldEditCheck(msg, panel);
	for(int i=0; i<txtFields.length; i++){	    
	    txtFields[i].addKeyListener(ka);
	}
    }


    public void setFieldEditCheck(JComponent txtField, String msg, DataEntryPanel panel){
	KeyAdapter ka = getFieldEditCheck(msg, panel);
	txtField.addKeyListener(ka);
    }
    public abstract void actionPerformed(ActionEvent e);

    public void performEnterAction(){
    	parentScreen.updateOccured((DBObject) data, ScreenController.ENTER_KEY, (DBObject)data);
    }
    public void editingOccured(){
    	parentScreen.updateOccured((DBObject) data, ScreenController.UPDATE_EDIT, (DBObject)data);
    }
    public void cancelUpdate(){
    	parentScreen.updateOccured((DBObject) data, ScreenController.UPDATE_CANCELED, (DBObject)data);
    }
    public void saveUpdate(){
    	parentScreen.updateOccured((DBObject) data, ScreenController.UPDATE_SAVE, (DBObject)data);
    }


    
    public carpus.database.DBObject getPrimaryDataItem(){
    	return data;
    }
    public void setPrimaryDataItem(carpus.database.DBObject item){
    	data = item;
    }

}

class FieldEditCheck extends KeyAdapter{
    String msg="";
    DataEntryPanel pnl;
    FieldEditCheck(String message, DataEntryPanel panel){
	pnl = panel;
	msg = message;
    }
    public void keyTyped(KeyEvent e){
//    	System.out.println(this.getClass().getName() + ":keyTyped:" + e);
	if(e.isControlDown()) // ctrl key was held ... Not processes here
	    return;
	if(e.getKeyCode() >= KeyEvent.VK_F1 && e.getKeyCode() <= KeyEvent.VK_F12){
		ErrorLogger.getInstance().logDebug("Missing function key registration", true);
	}
	if (!pnl.isEdited() && !e.isAltDown()){
//  	    System.out.println(this.getClass().getName() + ":keyTyped:" + e);
		pnl.editingOccured();
//	    pnl.notifyListeners(msg, pnl);
	    pnl.setEdited(true);
	}
	if(e.isAltDown()){
//  	    System.out.println(this.getClass().getName() + ":keyTyped:" + e);
	}
    }
    public void keyPressed(KeyEvent e){
	int code = e.getKeyCode();
	
	if(code == KeyEvent.VK_ESCAPE){
	    //Key pressed is the Escape key. Hide this Dialog.
//		pnl.cancelUpdate();
//	    pnl.notifyListeners("Cancel", pnl);
	} else if(code == KeyEvent.VK_ENTER){
	    //Key pressed is the Enter key. 
	    if(e.isControlDown())
			pnl.saveUpdate();
//		pnl.notifyListeners("CTRL_ENTERKEY", pnl);
	    else
	    	pnl.performEnterAction();
//		pnl.notifyListeners("ENTERKEY", pnl);
	//  } else if(code == KeyEvent.VK_ENTER){
//  	    //Key pressed is the Enter key. 
//    	    pnl.notifyListeners("ENTERKEY", pnl);
  	}
    }
}
