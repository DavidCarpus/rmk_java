/*
 * Created on Apr 18, 2005
 *
 */
package rmk.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;

import rmk.ErrorLogger;

import carpus.gui.FormattedTextFields;
import carpus.gui.LabeledTextField;

public class InvoiceSearchScreen extends Screen {
    static final int FIELD_ORDERED_START=0;
    static final int FIELD_ORDERED_END=1;
    static final int FIELD_ESTIMATED_START=2;
    static final int FIELD_ESTIMATED_END=3;
    static final int FIELD_SHIPPED_START=4;
    static final int FIELD_SHIPPED_END=5;
    static final String labels[] = {"Ordered", "Estimated", "Shipped"};

    LabeledTextField[] txtFields = new LabeledTextField[FIELD_SHIPPED_END + 1];
    DefaultListModel listData = new DefaultListModel();
    JList list = new JList(listData);

    
	public InvoiceSearchScreen(){		
		super("InvoiceSearch");
	    getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));

		for(int i=0; i<= FIELD_SHIPPED_END/2; i++){
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(1,2));
			panel.setPreferredSize(new Dimension(50,320));
			JFormattedTextField date1 = FormattedTextFields.getDateField();
			JFormattedTextField date2 = FormattedTextFields.getDateField();
			txtFields[i] = new LabeledTextField("Date:" + labels[i], date1,"");
			txtFields[i*2+1] = new LabeledTextField("Date:"+labels[i], date2,"");
			panel.add(txtFields[i]);
			panel.add(txtFields[i*2+1]);
			getContentPane().add(panel);
		}
		getContentPane().add(list);
		
		getContentPane().add(buttonBar);
	  	setPreferredSize(new Dimension(400,120));
    	pack();

	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand().toUpperCase().trim();
		//-----------------------------
		if (command.equals("CANCEL")) { //cancel
		    defaultCancelAction();
		} else {  // Undefined
		    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
		}

	}

	public void internalFrameActivated(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
	}

	public boolean isEdited() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setData(DBGuiModel model) {
		// TODO Auto-generated method stub

	}

}
