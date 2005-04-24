/*
 * Created on Apr 18, 2005
 *
 */
package rmk.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.GregorianCalendar;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;

import rmk.ErrorLogger;

import carpus.gui.BasicToolBar;
import carpus.gui.DataEntryPanel;
import carpus.gui.FormattedTextFields;
import carpus.util.DateFunctions;

public class InvoiceSearchScreen extends Screen {
    static final int FIELD_ORDERED_START=0;
    static final int FIELD_ORDERED_END=1;
    static final int FIELD_ESTIMATED_START=2;
    static final int FIELD_ESTIMATED_END=3;
    static final int FIELD_SHIPPED_START=4;
    static final int FIELD_SHIPPED_END=5;
    static final int FIELD_INVOICE_START=6;
    static final int FIELD_INVOICE_END=7;
    static final int FIELD_MODEL=8;
    static final int FIELD_FEATURES=8;
    static final int FIELD_INVOICE_NOTE=9;

    JFormattedTextField[] fields = new JFormattedTextField[FIELD_INVOICE_NOTE + 1];
    DefaultListModel listData = new DefaultListModel();
    boolean edited=false;
    
	public InvoiceSearchScreen(){		
		super("InvoiceSearch");
//	    getContentPane().setLayout(new FlowLayout());	    
	    getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));

		getContentPane().add(getDatePanel());
		getContentPane().add(getInvoicePanel());
		
//		for(int i=0; i< FIELD_INVOICE_NOTE; i++){
//			setFieldEditCheck(fields[i], "CRITERIA_EDITED", this);
//		}
		buttonBar = new BasicToolBar(null, new String[] {"Search", "Cancel"}, 
			     new String[] {"Search", "Cancel"},
			     new String[] {"Search", "Cancel"});
		buttonBar.getButton(0).setForeground(DK_GREEN);
		buttonBar.addActionListener(this);
		buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));
		buttonBar.enableButton(0, edited);
		
		getContentPane().add(buttonBar);
	  	setPreferredSize(new Dimension(400,300));
    	pack();

	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand().toUpperCase().trim();
		//-----------------------------
		if (command.equals("CANCEL")) { //cancel
		    defaultCancelAction();
		    return;
		}if (command.startsWith("DATE_")) { //DateEdited
			boolean validDate=false;
			String tempStr = command.substring(command.indexOf(":")+1);
			int index=Integer.parseInt(tempStr);
			tempStr = fields[index].getText();			
			GregorianCalendar date = DateFunctions.gregorianFromString(tempStr);
			if(date == null) return;
			int year = date.get(GregorianCalendar.YEAR);
			validDate = year>1900;
			edited=validDate;	
			buttonBar.enableButton(0, edited);
			
		} else {  // Undefined
		    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
		}

	}

	public void internalFrameActivated(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
	}

	public boolean isEdited() {
		// TODO Auto-generated method stub
		return edited;
	}

	public void setData(DBGuiModel model) {
		// TODO Auto-generated method stub

	}
	

	JPanel getDatePanel(){
		DatePanel datePanel = new DatePanel();
		datePanel.addActionListener(this);
		return datePanel;
	}
	
	JPanel getInvoicePanel(){
		JPanel results=new JPanel();

		JLabel label=new JLabel("  Invoice >= ");
		fields[FIELD_INVOICE_START] = FormattedTextFields.getNumberField();
		label.setFont(new Font("Serif", Font.BOLD, 14));
		fields[FIELD_INVOICE_START].setFont(new Font("Serif", Font.BOLD, 14));
		results.add(label);
		results.add(fields[FIELD_INVOICE_START]);
		
		label=new JLabel("  Invoice <= ");
		fields[FIELD_INVOICE_END] = FormattedTextFields.getNumberField();
		label.setFont(new Font("Serif", Font.BOLD, 14));
		fields[FIELD_INVOICE_END].setFont(new Font("Serif", Font.BOLD, 14));
		results.add(label);
		results.add(fields[FIELD_INVOICE_END]);
		results.setPreferredSize(new Dimension(100,20));

		results.add(getKnifePanel());
		results.add(getNotesPanel());
		
//		results.setBackground(Color.RED);
		return results;
	}
	
	JPanel getKnifePanel(){
		JPanel results=new JPanel();
		
		JLabel label=new JLabel("  Model ");
		fields[FIELD_MODEL] = new JFormattedTextField();
		label.setFont(new Font("Serif", Font.BOLD, 14));
		fields[FIELD_MODEL].setFont(new Font("Serif", Font.BOLD, 14));
		fields[FIELD_MODEL].setColumns(5);
		
		results.add(label);
		results.add(fields[FIELD_MODEL]);

		label=new JLabel("  Features ");
		fields[FIELD_FEATURES] = new JFormattedTextField();
		label.setFont(new Font("Serif", Font.BOLD, 14));
		fields[FIELD_FEATURES].setFont(new Font("Serif", Font.BOLD, 14));
		fields[FIELD_FEATURES].setColumns(7);
		
		results.add(label);
		results.add(fields[FIELD_FEATURES]);
		
		return results;
	}
	
	JPanel getNotesPanel(){
		JPanel results=new JPanel();
		
		JLabel label=new JLabel("  Notes ");
		fields[FIELD_INVOICE_NOTE] = new JFormattedTextField();
		label.setFont(new Font("Serif", Font.BOLD, 14));
		fields[FIELD_INVOICE_NOTE].setFont(new Font("Serif", Font.BOLD, 14));
		fields[FIELD_INVOICE_NOTE].setColumns(12);
		
		results.add(label);
		results.add(fields[FIELD_INVOICE_NOTE]);
		
		return results;
	}
	
	class DatePanel extends DataEntryPanel{

		DatePanel(){
		    final String labels[] = {"Ordered", "Scheduled", "Shipped"};
		    this.setLayout(new GridLayout(3,4));
		    
			for(int i=0; i<= FIELD_SHIPPED_END/2; i++){
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(1,2));
				panel.setPreferredSize(new Dimension(50,320));
				
				fields[i] = FormattedTextFields.getDateField();
				fields[i+1] = FormattedTextFields.getDateField();
				KeyAdapter ka = new FieldEditCheck("DATE_START:" + i, this);
				fields[i].addKeyListener(ka);
				KeyAdapter ka2 = new FieldEditCheck("DATE_END:" + i+1, this);
				fields[i].addKeyListener(ka2);

				JLabel label=new JLabel("  " + labels[i] + " >= ");
				label.setFont(new Font("Serif", Font.BOLD, 14));
				this.add(label);
				this.add(fields[i]);
				
				label=new JLabel("  " + labels[i] + " <= ");
				label.setFont(new Font("Serif", Font.BOLD, 14));
				this.add(label);
				this.add(fields[i+1]);
			}
			this.setPreferredSize(new Dimension(100,20));
		}
		/* (non-Javadoc)
		 * @see carpus.gui.DataEntryPanel#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		    notifyListeners(e);
		}
		
		class FieldEditCheck extends KeyAdapter{
		    String msg="";
		    DataEntryPanel pnl;

		    FieldEditCheck(String message, DataEntryPanel panel){
		    	pnl = panel;
		    	msg = message;
		    }
		    public void keyTyped(KeyEvent e){
		    	if(e.isControlDown()) // ctrl key was held ... Not processes here
		    		return;
		    	pnl.notifyListeners(msg, pnl);
		    	pnl.setEdited(true);
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
//		  	    //Key pressed is the Enter key. 
//		    	    pnl.notifyListeners("ENTERKEY", pnl);
			}
		    }
		    
		}
	}
	
}
