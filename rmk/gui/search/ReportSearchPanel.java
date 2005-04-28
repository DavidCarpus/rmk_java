package rmk.gui.search;

import javax.swing.*;

import rmk.ErrorLogger;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class ReportSearchPanel 
//      extends carpus.gui.DataEntryPanel 
    extends CriteriaPanel
    implements ActionListener
//  	       , ItemListener
{
      static final String[] rpttypes = {"InvoiceEntries", "Invoices"};
//      static final String[] rpttypes = {"Entries"};

    CriteriaPanel panels[] = new CriteriaPanel[rpttypes.length];

    carpus.util.Logger errLog = carpus.util.Logger.getInstance();

    JComboBox rpts = new JComboBox(rpttypes);

    public ReportSearchPanel(){	
	BoxLayout layout = new BoxLayout(this,BoxLayout.Y_AXIS);
	setLayout(layout);

	add(rpts);

	panels[0] =  new InvoicesSearchPanel(); // EntrySearchPanel
  	panels[1] =  new InvoicesSearchPanel();

	panels[0].setBackground(Color.GREEN);
	panels[1].setBackground(Color.RED);


	for(int i=0; i< panels.length; i++){
	    if(panels[i] != null){
		add(panels[i]);
		panels[i].setVisible(true);
	    }
	}
	selectPanel(1);

	rpts.addActionListener(this);
    }
    void selectPanel(int index){
	for(int i=0; i< panels.length; i++){
	    if(panels[i] != null){
		panels[i].setVisible(i == index);
	    }
	}
    }
    //----------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase();
    ErrorLogger.getInstance().logDebugCommand(command);

	ActionEvent event=null;

	if(command.equals("COMBOBOXCHANGED")){
//    	    event = new ActionEvent(this,0,"RptChange");
	    selectPanel(rpts.getSelectedIndex());

	} else{
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
	}
    }
    public Vector getCriteria(){
	Vector results = new Vector();
	for(int i=0; i< panels.length; i++){
	    if(panels[i] != null){
		Vector tmp = panels[i].getCriteria();
		if(tmp != null){
		    results.addAll(tmp);
		}
	    }
	}
	return results;
    }

    public String getType(){
	return (String)rpts.getSelectedItem();
    }
}
