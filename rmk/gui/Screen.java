package rmk.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import carpus.gui.*;
import java.util.Vector;
import java.util.Enumeration;

abstract class Screen 
    extends JInternalFrame 
    implements ActionListener
	       ,InternalFrameListener
	       , IScreen
//      , WindowListener
{
    public static Color DK_GREEN = new Color(0,182,51);
	protected rmk.DataModel sys = rmk.DataModel.getInstance();


    protected BasicToolBar buttonBar = new BasicToolBar(null, new String[] {"Save", "Cancel"}, 
				     new String[] {"Save", "Cancel"},
				     new String[] {"Save", "Cancel"});
    DBGuiModel model=null;
    Vector listeners=null;
    String title="";
    public Screen(){};
//============================================================
    protected Screen(String title){
        super(title, 
              true, //resizable
              false, //closable
              false, //maximizable
              true);//iconifiable
	this.title = title;
//  	this.setFont(new Font("Serif", Font.BOLD, 16));
	UIManager.put("InternalFrame.titleFont", new Font("Serif", Font.BOLD, 20));
	SwingUtilities.updateComponentTreeUI(this);

	buttonBar.setFloatable(false);
	buttonBar.enableButton(0,false);
	buttonBar.addActionListener(this);
	buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));
//  	buttonBar.getButton(0).setForeground(new Color(0,102,51));
	buttonBar.getButton(0).setForeground(DK_GREEN);
	buttonBar.getButton(0).setMnemonic(KeyEvent.VK_V);

  	KeyStroke kF1=KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1,0);
  	this.registerKeyboardAction(this, "F1", kF1, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  	KeyStroke kF2=KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2,0);
  	this.registerKeyboardAction(this, "F2", kF2, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  	KeyStroke kF3=KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3,0);
  	this.registerKeyboardAction(this, "F3", kF3, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

  	KeyStroke kF11=KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11,0);
  	this.registerKeyboardAction(this, "F11", kF11, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

  	KeyStroke kF5=KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5,0);
  	this.registerKeyboardAction(this, "CUSTOMERDETAILS", kF5, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  	KeyStroke kF6=KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6,0);
  	this.registerKeyboardAction(this, "INVOICEDETAILS", kF6, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  	KeyStroke kF7=KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7,0);
  	this.registerKeyboardAction(this, "PAYMENTS", kF7, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	this.addInternalFrameListener(this);
    }
//============================================================
    public boolean bringToFront(){
	try{
	    grabFocus();
	    toFront();
	    setSelected(true);
	    setIcon(false);
	    return true;
	} catch (Exception e){
	    return false;
	}
    }
    public void setModel(DBGuiModel model){
	this.model = model;
    }
    public DBGuiModel getModel(){
	return model;
    }

//============================================================

//-----------------------------------------------------------------
//     abstract boolean isEdited();
//      abstract Vector getEditedData();

//-----------------------------------------------------------------
    public void defaultCancelAction(){
	if( isEdited() &&
	    (1 == JOptionPane.showConfirmDialog(null,
						"Confirm Exit without saving", 
						"Confirm", 
						JOptionPane.YES_NO_OPTION)))
	    return;
	this.setVisible(false);
	Desktop.getInstance().remove(this);;
    }
//============================================================
    public void addActionListener(ActionListener listener){
	if(listeners == null) listeners = new Vector();
	if(!listeners.contains(listener)) listeners.addElement(listener);
    }
//-----------------------------------------------------------------
    public void notifyListeners(String msg){	
	notifyListeners(new ActionEvent(this,1,msg));
    }
    public void notifyListeners(String msg, DataEntryPanel panel){
	notifyListeners(new ActionEvent(panel,1,msg));
    }
    public void notifyListeners(ActionEvent event){
	if(listeners == null) return;
	for(Enumeration enum=listeners.elements(); enum.hasMoreElements();){
	    ActionListener listener = (ActionListener)enum.nextElement();
	    listener.actionPerformed(event);
//  	    if(title.equals("Invoice Item"))
//  		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ listener);
	    
	}
    }
//============================================================
    public void internalFrameClosing(InternalFrameEvent e) {
//  	super.internalFrameClosing(e);
//  	displayMessage("Internal frame closing", e);
    }
    public void internalFrameClosed(InternalFrameEvent e) {
//  	displayMessage("Internal frame closed", e);
//  	listenedToWindow = null;
    }
    public void internalFrameOpened(InternalFrameEvent e) {
//  	displayMessage("Internal frame opened", e);
    }
    public void internalFrameIconified(InternalFrameEvent e) {
//  	displayMessage("Internal frame iconified", e);
    }
    public void internalFrameDeiconified(InternalFrameEvent e) {
//  	displayMessage("Internal frame deiconified", e);
    }
//      public void internalFrameActivated(InternalFrameEvent e) {
//  //  	displayMessage("Internal frame activated", e);
//      }
    public void internalFrameDeactivated(InternalFrameEvent e) {
//  	displayMessage("Internal frame deactivated", e);
    }
//============================================================
}
