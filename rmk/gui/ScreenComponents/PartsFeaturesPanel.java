package rmk.gui.ScreenComponents;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EtchedBorder;

import rmk.DataModel;
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
//  	subPanels[0] = descPanel();
//  	add(subPanels[0]);
//  	subPanels[1] = pricePanel;
//  	add(subPanels[1]);
//    	subPanels[2] = ynPanel();
//  	add(subPanels[2]);
//  	pricePanel.addActionListener(this);
//  	partTypes.addActionListener(this);
	add(features);
	features.setPreferredSize(new Dimension(300,100));
      	BoxLayout layout = new BoxLayout(this,BoxLayout.Y_AXIS);
	setLayout(layout);
	features.setBackground(Color.YELLOW);
	features.addActionListener(this);

	setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	
//  	setFieldEditCheck(txtFields, "PartsChange", this);
//        	setPreferredSize(new Dimension(40,240));
      	setPreferredSize(new Dimension(140,220));
    }
//-----------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase().trim();
	if(loading) return;

	if(command.equals("COMBOBOXCHANGED")){
	    notifyListeners(new ActionEvent(this,0,"PartsChange"));

	} else if(command.equals("ADDFEATURE")){ // actually, this is a remove, (code reuse)
	    System.out.println(this.getClass().getName() + ":"+ "Remove Feature?");

	} else{
	    System.out.println(this.getClass().getName() + ":Undefined:" + command + "|");
	    notifyListeners(e);
	}
//  	System.out.println(e);   
    }
//-----------------------------------------------------------------
    public void stateChanged(javax.swing.event.ChangeEvent e){	
	if(!loading){
	    ActionEvent event = new ActionEvent(this,1,"PartsChange");
	    notifyListeners(event);
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
//-----------------------------------------------------------------

//-----------------------------------------------------------------
//-----------------------------------------------------------------
      public static void main(String args[]) throws Exception{
  	rmk.gui.Application.main(args);
      }
}
