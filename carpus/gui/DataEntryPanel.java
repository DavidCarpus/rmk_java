package carpus.gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;

public abstract class DataEntryPanel extends JPanel{
    boolean edited = false;
    protected Vector listeners=null;
	protected carpus.util.Logger errorLog = carpus.util.Logger.getInstance();
//      protected abstract void setData(carpus.database.DBObject item);

    public void addActionListener(ActionListener listener){
	if(listeners == null) listeners = new Vector();
	if(!listeners.contains(listener)) listeners.addElement(listener);
    }
    public boolean isEdited(){
	return edited;
    }
    public void setEdited(boolean value){
	edited = value;
    }
//-----------------------------------------------------------------
    public void notifyListeners(String msg){
	if(listeners == null) return;
	notifyListeners(new ActionEvent(this,1,msg));
    }
    public void notifyListeners(String msg, DataEntryPanel panel){
	if(listeners == null) return;
	notifyListeners(new ActionEvent(panel,1,msg));
    }
    public void notifyListeners(ActionEvent event){
	if(listeners == null || event == null) return;
//  	System.out.println(this.getClass().getName() + ":notify:" + event);
	for(Enumeration enum=listeners.elements(); enum.hasMoreElements();){
	    ((ActionListener)enum.nextElement()).actionPerformed(event);
	}
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

//      public void setFieldEditCheck(JTextComponent[] txtFields, 
//  				  String msg, DataEntryPanel panel){
//  	KeyAdapter ka = getFieldEditCheck(msg, panel);
//  	for(int i=0; i<txtFields.length; i++){	    
//  	    txtFields[i].addKeyListener(ka);
//  	}
//      }
//      public void setFieldEditCheck(JTextComponent txtField, String msg, DataEntryPanel panel){
//  	KeyAdapter ka = getFieldEditCheck(msg, panel);
//  	txtField.addKeyListener(ka);
//      }
    public void setFieldEditCheck(JComponent txtField, String msg, DataEntryPanel panel){
	KeyAdapter ka = getFieldEditCheck(msg, panel);
	txtField.addKeyListener(ka);
    }
    public abstract void actionPerformed(ActionEvent e);

    public void performEnterAction(){
    }

    //========================================================
    public static void main(String args[]) throws Exception {
	rmk.gui.Application.main(args);
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
	if (!pnl.isEdited() && !e.isAltDown()){
//  	    System.out.println(this.getClass().getName() + ":keyTyped:" + e);
	    pnl.notifyListeners(msg, pnl);
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
	    pnl.notifyListeners("Cancel", pnl);
	} else if(code == KeyEvent.VK_ENTER){
	    //Key pressed is the Enter key. 
	    if(e.isControlDown())
		pnl.notifyListeners("CTRL_ENTERKEY", pnl);
	    else
		pnl.notifyListeners("ENTERKEY", pnl);
	//  } else if(code == KeyEvent.VK_ENTER){
//  	    //Key pressed is the Enter key. 
//    	    pnl.notifyListeners("ENTERKEY", pnl);
  	}
    }
}
