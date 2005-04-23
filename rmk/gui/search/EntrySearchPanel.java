package rmk.gui.search;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import rmk.ErrorLogger;

public class EntrySearchPanel 
    extends CriteriaPanel
{
    static final int FIELD_TAXNUMBER  =0;
    static final int FIELD_TERMS  =1;

    public EntrySearchPanel(){
	setBackground(Color.GREEN);
	setPreferredSize(new Dimension(300,100));
    }

    //----------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase();

	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
    }
    //========================================================
    public Vector getCriteria(){
	Vector results = new Vector();

	return results;
    }

}
