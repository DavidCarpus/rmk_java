package rmk.gui.ScreenComponents;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EtchedBorder;

import rmk.ErrorLogger;
import rmk.database.dbobjects.Parts;
import rmk.database.dbobjects.PartPrices;

import carpus.gui.*;

//===============================================================
//===============================================================
public class PartsPanel
    extends carpus.gui.DataEntryPanel
    implements ActionListener, ChangeListener
//===============================================================
{
    rmk.DataModel sys = rmk.DataModel.getInstance();
    Parts part;
    boolean loading = false;

    static final int FIELD_PARTID=0;
    static final int FIELD_PARTCODE=1;
    static final int FIELD_DESCRIPTION=2;

    JCheckBox discountable =  new JCheckBox();
    JCheckBox bladeItem =  new JCheckBox();
    JCheckBox taxable =  new JCheckBox();
    JCheckBox sheath =  new JCheckBox();
    JCheckBox active =  new JCheckBox();
    JCheckBox askPrice =  new JCheckBox();
    JComboBox partTypes = new JComboBox(sys.partInfo.getPartTypes());

    JComponent[] txtFields = new JComponent[3];
    JPanel subPanels[] = new JPanel[3];
    PartPricePanel pricePanel = new PartPricePanel();

//-----------------------------------------------------------------
    public PartsPanel(){
	subPanels[0] = descPanel();
	add(subPanels[0]);
	subPanels[1] = pricePanel;
	add(subPanels[1]);
  	subPanels[2] = ynPanel();
	add(subPanels[2]);
	pricePanel.addActionListener(this);
	partTypes.addActionListener(this);

	setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	
	setFieldEditCheck(txtFields, "PartsChange", this);
//        	setPreferredSize(new Dimension(40,240));
      	setPreferredSize(new Dimension(140,220));
    }
//-----------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase().trim();	
	if(loading) return;
    ErrorLogger.getInstance().logDebugCommand(command);


	if(command.equals("COMBOBOXCHANGED"))
	    notifyListeners(new ActionEvent(this,0,"PartsChange"));
	else
	    notifyListeners(e);
//  	System.out.println(this.getClass().getName() + ":Undefined:" + command + "|");
//  	System.out.println(e);   
    }
//-----------------------------------------------------------------
    public boolean pricesEdited(){
	return pricePanel.isEdited();
    }
    public boolean isEdited(){
	return super.isEdited() || pricesEdited();
    }
//-----------------------------------------------------------------
    public void stateChanged(javax.swing.event.ChangeEvent e){	
	if(!loading){
	    ActionEvent event = new ActionEvent(this,1,"PartsChange");
	    notifyListeners(event);
	}
    }
    public PartPrices getPriceChange(){	
		return pricePanel.getPriceChange();
    }
//-----------------------------------------------------------------
    public Parts getData(){
	part.setPartCode(((LabeledTextField)txtFields[FIELD_PARTCODE]).getValue());
	part.setDescription(((LabeledTextField)txtFields[FIELD_DESCRIPTION]).getValue());
	part.setDiscountable( discountable.isSelected());
	part.setBladeItem( bladeItem.isSelected());
	part.setTaxable( taxable.isSelected());
	part.setSheath( sheath.isSelected());
	part.setActive( active.isSelected());
	part.setAskPrice(askPrice.isSelected());

	int ptype = sys.partInfo.getPartTypeID(""+partTypes.getSelectedItem());
	part.setPartType(ptype);
	return part;
   }
//-----------------------------------------------------------------
    public void setData(Parts part){
	loading = true;
	this.part = part;

  	((LabeledTextField)txtFields[FIELD_PARTID]).setValue(""+part.getPartID());
  	((LabeledTextField)txtFields[FIELD_PARTCODE]).setValue(part.getPartCode());
  	((LabeledTextField)txtFields[FIELD_DESCRIPTION]).setValue(part.getDescription());	
	discountable.setSelected(part.isDiscountable());
	bladeItem.setSelected(part.isBladeItem());
	taxable.setSelected(part.isTaxable());
	sheath.setSelected(part.isSheath());
	active.setSelected( part.isActive());
	askPrice.setSelected(part.askPrice());
	
	String ptype = sys.partInfo.getPartTypeDesc(part.getPartType());
	
	for(int i=0; i< partTypes.getModel().getSize(); i++){
//  	    System.out.println(this.getClass().getName() + ":"+ ptype + ":" + partTypes.getItemAt(i));
	    if(ptype.equals(""+partTypes.getItemAt(i))){
		partTypes.setSelectedIndex(i);
	    }
	}

//    	((LabeledTextField)txtFields[FIELD_PARTTYPE]).setValue(sys.partInfo.getPartTypeDesc(part.getPartType()));

	pricePanel.setData(part);

	setEdited(false);
	loading = false;
    }
//-----------------------------------------------------------------
    JPanel descPanel(){
	JPanel results = new JPanel();

//  	GridLayout layout = new GridLayout();
	BoxLayout layout = new BoxLayout(results,BoxLayout.Y_AXIS);
	results.setBackground(Color.RED);
	results.setLayout(layout);
	results.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

	LabeledTextField field;
	JTextField txt = new JTextField("", 6);
	txt.setDisabledTextColor(Color.white);
	txt.setForeground(Color.white);
	txt.setBackground(Color.black);
	txt.setEditable(false);
	txt.setFocusable(false);
	field = new LabeledTextField("PartID       ",txt);
	txt.setFont(new Font("Serif", Font.BOLD, 18));
	results.add(field);
	txtFields[FIELD_PARTID] = field;

	//===========================================================
	field = new LabeledTextField("Part Code    ",new JTextField("", 8));
	results.add(field);
	txtFields[FIELD_PARTCODE] = field;
	//===========================================================
  	field = new LabeledTextField("Desc. ",new JTextField("", 30));
	results.add(field);
	txtFields[FIELD_DESCRIPTION] = field;
	//===========================================================
//    	field = new LabeledTextField("Part Type    ",new JTextField("", 5));
//  //  	results.add(field);
//  	txtFields[FIELD_PARTTYPE] = field;

	JPanel typePnl = new JPanel();
	typePnl.add(new JLabel("Part Type"));
	typePnl.add(partTypes);
	results.add(typePnl);

	results.setPreferredSize(new Dimension(360,110));
	return results;
    }
    //---------------------------------------
    JPanel ynPanel(){
	JPanel results = new JPanel();

//        	GridLayout layout = new GridLayout(4,1);
      	BoxLayout layout = new BoxLayout(results,BoxLayout.X_AXIS);
	results.setBackground(Color.BLUE);
	results.setLayout(layout);
	results.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	JPanel pnl;
	pnl = new JPanel();
	pnl.add(new JLabel("Discountable"));
	pnl.add(discountable);
	results.add(pnl);

	pnl = new JPanel();
	pnl.add(new JLabel("Blade Item"));
	pnl.add(bladeItem);
	results.add(pnl);

	pnl = new JPanel();
	pnl.add(new JLabel("Taxable" ));
	pnl.add(taxable);
	results.add(pnl);

	pnl = new JPanel();
	pnl.add(new JLabel("Sheath"));
	pnl.add(sheath);
	results.add(pnl);
	
	pnl = new JPanel();
	pnl.add(new JLabel("Active"));
	pnl.add(active);
	results.add(pnl);
	
	pnl = new JPanel();
	pnl.add(new JLabel("Ask Price"));
	pnl.add(askPrice);
	results.add(pnl);
	
	

	discountable.addChangeListener(this);
	taxable.addChangeListener(this);
	bladeItem.addChangeListener(this);
	sheath.addChangeListener(this);
	active.addChangeListener(this);
	askPrice.addChangeListener(this);
//        	results.setPreferredSize(new Dimension(180,90));

	return results;
    }

//-----------------------------------------------------------------
//-----------------------------------------------------------------
      public static void main(String args[]) throws Exception{
  	rmk.gui.Application.main(args);
      }
}
