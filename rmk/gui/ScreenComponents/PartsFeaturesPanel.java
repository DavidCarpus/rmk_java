package rmk.gui.ScreenComponents;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EtchedBorder;

import rmk.DataModel;
import rmk.ErrorLogger;
import rmk.database.dbobjects.Parts;

//===============================================================
//===============================================================
public class PartsFeaturesPanel
    extends carpus.gui.DataEntryPanel
    implements ActionListener, ChangeListener
//===============================================================
{
    rmk.DataModel sys = rmk.DataModel.getInstance();
    Parts part;
    boolean loading = false;
    KnifeFeaturesPanel features = new KnifeFeaturesPanel();

//      rmk.database.PartPriceTable prices = rmk.database.PartPriceTable.getInstance();
//      JPanel subPanels[] = new JPanel[3];

//-----------------------------------------------------------------
    public PartsFeaturesPanel(){
	add(new JLabel("Valid Features"));

	add(features);
	features.setPreferredSize(new Dimension(300,100));
      	BoxLayout layout = new BoxLayout(this,BoxLayout.Y_AXIS);
	setLayout(layout);
	features.setBackground(Color.YELLOW);
//	features.addActionListener(this);

	setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	
//  	setFieldEditCheck(txtFields, "PartsChange", this);
//        	setPreferredSize(new Dimension(40,240));
      	setPreferredSize(new Dimension(140,220));
    }
//-----------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase().trim();
    ErrorLogger.getInstance().logDebugCommand(command);

	if(loading) return;

	if(command.equals("COMBOBOXCHANGED")){
		//TODO: Neet to go through parent screen with messages
		//	    notifyListeners(new ActionEvent(this,0,"PartsChange"));

	} else if(command.equals("ADDFEATURE")){ // actually, this is a remove, (code reuse)
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Remove Feature?");

	} else{
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
		//TODO: Neet to go through parent screen with messages
	    //	    notifyListeners(e);
	}
//  	ErrorLogger.getInstance().logMessage(e);   
    }
//-----------------------------------------------------------------
    public void stateChanged(javax.swing.event.ChangeEvent e){	
	if(!loading){
	    ActionEvent event = new ActionEvent(this,1,"PartsChange");
		//TODO: Neet to go through parent screen with messages
	    //	    notifyListeners(event);
	}
    }
//-----------------------------------------------------------------
    public Parts getData(){
	return null;
   }
//-----------------------------------------------------------------
    public void setData(Parts part){
	loading = true;
	this.part = part;
	if(part != null){
	    ActionEvent evnt = new ActionEvent(this, (int)part.getPartID(),"SET_KNIFE_MODEL|" + DataModel.getCurrentYear());
	    features.actionPerformed(evnt);
	}
	loading = false;
    }

}
