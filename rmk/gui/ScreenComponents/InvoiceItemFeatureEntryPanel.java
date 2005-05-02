package rmk.gui.ScreenComponents;

import javax.swing.*;
import java.awt.event.*;

import rmk.DataModel;
import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import rmk.database.dbobjects.Parts;
import rmk.gui.DBGuiModel;

//===============================================================
//===============================================================
public class InvoiceItemFeatureEntryPanel 
    extends carpus.gui.DataEntryPanel 
//      extends JPanel
    implements ActionListener
//===============================================================
{
    DBGuiModel model;
    rmk.DataModel sys = rmk.DataModel.getInstance();
    JTextField field = new JTextField("",3);

//-----------------------------------------------------------------
    public InvoiceItemFeatureEntryPanel(){
	setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
	JLabel label = new JLabel("Feature");
	
	add(label);
	add(field);
	KeyAdapter ka = new KeyAdapter(){
		public void keyPressed(KeyEvent e){
		    int code = e.getKeyCode();	
		    if(code == KeyEvent.VK_ENTER){
			//Key pressed is the Enter key. 
			actionPerformed(new ActionEvent(this, 0, "EnteredNewEntry"));
		    }
		}
	    };
	label.setDisplayedMnemonic('e');
	field.setFocusAccelerator('E');
	field.addKeyListener(ka);
    }
//-----------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase().trim();
    ErrorLogger.getInstance().logDebugCommand(command);

    if (command.equals("ENTEREDNEWENTRY")) { //removed feature
		InvoiceEntryAdditions newFeature = new InvoiceEntryAdditions(0);
		String enteredCode = field.getText();
		Parts part = DataModel.getInstance().partInfo.getPartFromCode(enteredCode);		
		newFeature.setPartID(part.getPartID());
		newFeature.setShortDescription(enteredCode.toUpperCase());
		setEdited(true);
    	parent.updateOccured(newFeature, ScreenController.UPDATE_ADD, null);
    	field.setText("");
//	    ActionEvent event = new ActionEvent(this, 0, "EnteredNewEntry-" + field.getText());
//	    field.setText("");
//	    notifyListeners(event);
    	
	} else{
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
	}
    }
//-----------------------------------------------------------------
    public void setData(DBGuiModel model ){}

	
    
}

