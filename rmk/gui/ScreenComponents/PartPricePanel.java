package rmk.gui.ScreenComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EtchedBorder;

import rmk.ErrorLogger;
import rmk.database.dbobjects.Parts;
import rmk.database.dbobjects.PartPrices;

import carpus.gui.*;

//===============================================================
//===============================================================
public class PartPricePanel
    extends carpus.gui.DataEntryPanel
    implements ActionListener
//===============================================================
{
    JComponent[] txtFields = new JComponent[1];
    JTextField[] pastPrices = new JTextField[4];
    JComboBox years = new JComboBox();
    JButton saveButton = new JButton("Update Price");
    int startYear;
    int endYear;
    Parts part;
//    rmk.database.PartPriceTable priceTable = rmk.database.PartPriceTable.getInstance();

    boolean loading=false;
    //---------------------------------------
    PartPricePanel(){
	LabeledTextField field;

//      	GridLayout layout = new GridLayout(2,1);
    	BoxLayout layout = new BoxLayout(this,BoxLayout.Y_AXIS);
  	setLayout(layout);
	setBackground(Color.GREEN);
	setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));


	add(years);
	startYear = rmk.database.PartPriceTable.getMinYear();
	endYear = rmk.database.PartPriceTable.getMaxYear();
	for(int year = endYear; year >= startYear; year--){
	    years.addItem(""+year);
	}

	JTextField txt = new JTextField("", 8);
	field = new LabeledTextField("Price $ ",txt);
	add(field);
	txtFields[0] = field;
//  	txt.addActionListener(this);

	add(pastPricesPnl());

//  	saveButton.addActionListener(this);
//  	saveButton.setEnabled(false);
//  	add(saveButton);

	setFieldEditCheck(txtFields, "PartPriceChange", this);
//        	setPreferredSize(new Dimension(100,50));
    }

//-----------------------------------------------------------------
    public void setData(Parts part){
    	this.part = part;
    	loading = true;
    	int year=0;
    	double price =0;
    	try{
    	for(year = endYear; year >= startYear; year--){
    		price = 0;
    		try{
    			price =  rmk.DataModel.getInstance().pricetable.getPartPrice(year, (int)part.getPartID());
    		} catch (Exception e){
    		}
    		int index=endYear - year;
    		if(index>=0 && index < pastPrices.length)
    			pastPrices[index].setText(""+price);
    		else
    			ErrorLogger.getInstance().logWarning("Invalid index" + index);
    	}
    	} catch (Exception e) {
			ErrorLogger.getInstance().logError("Setting PartPricePanel Data", e);
		}
    	setEdited(false);
    	loading = false;
    }
	public PartPrices getPriceChange() {
		//	rmk.database.PartPriceTable priceTable = rmk.database.PartPriceTable.getInstance();
		int year = (int) Integer.parseInt("" + years.getSelectedItem());
		PartPrices price = rmk.DataModel.getInstance().pricetable.getPartPriceObject(
				year, (int) part.getPartID());
		double newPrice = (double) Double.parseDouble(
		((LabeledTextField) txtFields[0]).getValue());

		((LabeledTextField) txtFields[0]).setValue(""); // clear field
		if(price == null){
			price = new PartPrices(0);
			price.setPartID(part.getPartID());
			price.setYear(year);
		}
		price.setPrice(newPrice);
		return price;
	}
//-----------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase().trim();

  	System.out.println(this.getClass().getName() + ":Undefined:" + command + "|");

	notifyListeners(e);
//  	saveButton.setEnabled(true);
//  	System.out.println(e);   
    }

//-----------------------------------------------------------------
//-----------------------------------------------------------------
      public static void main(String args[]) throws Exception{
  	rmk.gui.Application.main(args);
      }

    JPanel pastPricesPnl(){
	JPanel results = new JPanel();
      	GridLayout layout = new GridLayout(2,2);
  	results.setLayout(layout);

	for(int i=0; i< pastPrices.length; i++){
	    pastPrices[i] = new JTextField(5);
	    pastPrices[i].setEnabled(false);
	    JPanel pnl = new JPanel();
	    pnl.add(new JLabel(""+(endYear-i)));
	    pnl.add(pastPrices[i]);
	    results.add(pnl);
	}

	return results;
    }

}
