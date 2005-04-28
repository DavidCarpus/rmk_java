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

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;

import rmk.ErrorLogger;
import rmk.database.dbobjects.Invoice;
import rmk.gui.ScreenComponents.InvoiceHistoryListPanel;

/**
 * @author carpus
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InvoiceHistoryScreen extends Screen {
	InvoiceHistoryListPanel invoiceHistoryListPanel;

	public InvoiceHistoryScreen(){
		super("History");
		getContentPane().setLayout(new BorderLayout());

		invoiceHistoryListPanel = new InvoiceHistoryListPanel();
		invoiceHistoryListPanel.addActionListener(this);
		getContentPane().add(invoiceHistoryListPanel);
		buttonBar = new carpus.gui.BasicToolBar(null, 
							new String[]{"View", "Cancel"},
							new String[]{"Details", "Cancel"},
							new String[]{"View", "Cancel"});
		buttonBar.addActionListener(this);
		buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));

		buttonBar.getButton(0).setMnemonic(KeyEvent.VK_V); // View Button

//		buttonBar.addActionListener(this);
		getContentPane().add( buttonBar, BorderLayout.SOUTH);
		
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
		this.registerKeyboardAction(this,"Cancel",stroke,JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setSize(400,400); //...Then set the window size or call pack...
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand().toUpperCase().trim();
        ErrorLogger.getInstance().logDebugCommand(command);

		if (command.equals("CANCEL")) { //cancel
			defaultCancelAction();
		} else if(command.equals("DETAILS")){
			long invoiceID = invoiceHistoryListPanel.getSelectedItemID();
			Invoice inv = sys.invoiceInfo.getInvoice(invoiceID);

//			if( model == null) 
		    model = new DBGuiModel();
			rmk.ScreenController.getInstance().displayInvoiceDetails(inv, model);

		} else {  // Undefined
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":UndefinedAction:" + command + "|");
		}
		// TODO Finish actionPerformed function (Filter?)
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
	public void setData(DBGuiModel model) {
		invoiceHistoryListPanel.setData(model);
	}

}
