/*
 * Created on Jan 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package rmk.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.SignalProcessor;
import rmk.database.dbobjects.DBObject;
import rmk.database.dbobjects.Invoice;
import rmk.gui.ScreenComponents.InvoiceHistoryListPanel;

/**
 * @author carpus
 *
 */
public class InvoiceHistoryScreen extends Screen {
	InvoiceHistoryListPanel invoiceHistoryListPanel;

	public InvoiceHistoryScreen(){
		super("History");
		getContentPane().setLayout(new BorderLayout());

		invoiceHistoryListPanel = new InvoiceHistoryListPanel();
		invoiceHistoryListPanel.setParent(this);
		getContentPane().add(invoiceHistoryListPanel);
		buttonBar = new carpus.gui.BasicToolBar(null, 
							new String[]{"View", "Cancel"},
							new String[]{"Details", "Cancel"},
							new String[]{"View", "Cancel"});
		ButtonBarTranslator translator = new ButtonBarTranslator(this, buttonBar);
		buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));

		buttonBar.getButton(0).setMnemonic(KeyEvent.VK_V); // View Button

		getContentPane().add( buttonBar, BorderLayout.SOUTH);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setSize(400,400); //...Then set the window size or call pack...
	}
	
    //==========================================================
	public void actionPerformed(ActionEvent e) {
		if(!processHotKeys(e)){
			ErrorLogger.getInstance().TODO();
		}
	}
    //==========================================================
	public void processCommand(String command, Object from){
		ErrorLogger.getInstance().TODO();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameActivated(InternalFrameEvent arg0) {
		invoiceHistoryListPanel.requestFocus();
	}

	/* (non-Javadoc)
	 * @see rmk.gui.IScreen#isEdited()
	 */
	public boolean isEdited() {
		return false;
	}

	/* (non-Javadoc)
	 * @see rmk.gui.IScreen#setData(rmk.gui.DBGuiModel)
	 */
	public void setData(Vector invoiceLst) {
		invoiceHistoryListPanel.setData(invoiceLst);
	}
    public void setData(DBObject item){
    	ErrorLogger.getInstance().TODO();
    }

    public void updateOccured(DBObject itemChanged, int changeType, DBObject parentItem){
		ErrorLogger.getInstance().TODO();
     }
    
	public void buttonPress(int button, int id) {
		switch(button){
		case ScreenController.BUTTON_CANCEL:
			defaultCancelAction();
			SignalProcessor.getInstance().removeScreen(this);
		break;
		
		case ScreenController.BUTTON_SELECTION_DETAILS:
			long invoiceID = invoiceHistoryListPanel.getSelectedItemID();
			Invoice inv = sys.invoiceInfo.getInvoice(invoiceID);
			rmk.ScreenController.getInstance().displayInvoiceDetails(inv);
			break;
			
		case ScreenController.BUTTON_F1: // ignore these for this screen
		case ScreenController.BUTTON_F2:
		case ScreenController.BUTTON_F3:
			ErrorLogger.getInstance().logButton(button, id);
			break;
		
		default:
	       	ErrorLogger.getInstance().logButton(button, id);
		}
	}
	

}
