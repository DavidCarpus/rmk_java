package rmk.gui.ScreenComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import carpus.gui.*;
import rmk.ErrorLogger;
import rmk.database.dbobjects.Address;

public class CustomerAddressPanel extends carpus.gui.DataEntryPanel{
//      Address address = null;
    rmk.gui.DBGuiModel model= null;
    static final int FIELD_ADDRESS=0;
    static final int FIELD_ADDRESS2=1;
    static final int FIELD_ADDRESS3=2;
    static final int FIELD_CITY=3;
    static final int FIELD_STATE=4;
    static final int FIELD_ZIP=5;
    static final int FIELD_COUNTRY=6;

    LabeledTextField[] txtFields = new LabeledTextField[7];

    public CustomerAddressPanel(){	
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	LabeledTextField field;

	setLayout(gridbag);

  	c.fill = GridBagConstraints.NONE;
	c.anchor = GridBagConstraints.WEST;

	c.gridy = 0;
	c.gridx = 0;
	c.gridwidth = 3;
	field = new LabeledTextField("Address    ",new JTextField("", 30));
	gridbag.setConstraints(field, c);
	add(field);
	txtFields[FIELD_ADDRESS] = field;

	c.gridy++;
	field = new LabeledTextField("Address 2 ",new JTextField("", 30));
	gridbag.setConstraints(field, c);
	add(field);
	txtFields[FIELD_ADDRESS2] = field;

	c.gridy++;
	field = new LabeledTextField("Address 3 ",new JTextField("", 30));
	gridbag.setConstraints(field, c);
	add(field);
	txtFields[FIELD_ADDRESS3] = field;

	c.gridy++;
	c.gridwidth = 1;
	field = new LabeledTextField("City         ",new JTextField("", 20));
	gridbag.setConstraints(field, c);
	add(field);
	txtFields[FIELD_CITY] = field;

	c.gridx++;
	field = new LabeledTextField("   State ",new JTextField("", 5));
	gridbag.setConstraints(field, c);
	add(field);
	txtFields[FIELD_STATE] = field;

	c.gridx++;
	field = new LabeledTextField("   Zip Code ",new JTextField("", 10));
	gridbag.setConstraints(field, c);
	add(field);
	txtFields[FIELD_ZIP] = field;

	c.gridy++;
	c.gridx = 0;
	c.gridwidth = 3;
	field = new LabeledTextField("Country  ",new JTextField("", 30));
	gridbag.setConstraints(field, c);
	add(field);
	txtFields[FIELD_COUNTRY] = field;

	setFieldEditCheck(txtFields, "AddressChanged", this);

	setMaximumSize(new Dimension(825,150));
    }

    CustomerAddressPanel(Address address){
	this();
	setData(address);
    }

/*    public void setData(rmk.gui.DBGuiModel model){
	this.model = model;
	java.util.Vector data = model.getAddressData();
	if(data != null)
	    setData(data.get(0));
    }
*/
    public void actionPerformed(ActionEvent e) {}
    //========================================================
    public void processFocusEvent(FocusEvent e){
		txtFields[FIELD_ADDRESS].requestFocus();
    }

    //========================================================
    public carpus.database.DBObject getData(){
	java.util.Vector data;
	Address address;
	data = model.getAddressData();
	if(data == null){
	    data = new java.util.Vector();
	    address = new Address(0);
	    data.add(address);
	    model.setAddressData(data);
	} else{
	    address = (Address )model.getAddressData().get(0);
	}

	int i=0;
	String txt="";
	txt = txtFields[FIELD_ADDRESS].getValue();
	if(txt != null) txt = txt.toUpperCase();
	address.setAddress0(txt);

	txt = txtFields[FIELD_ADDRESS2].getValue();
	if(txt != null) txt = txt.toUpperCase();
	address.setAddress1(txt);

	txt = txtFields[FIELD_ADDRESS3].getValue();
	if(txt != null) txt = txt.toUpperCase();
	address.setAddress2(txt);

	txt = txtFields[FIELD_CITY].getValue();
	if(txt != null) txt = txt.toUpperCase();
	address.setCITY(txt);

	txt = txtFields[FIELD_STATE].getValue();
	if(txt != null) txt = txt.toUpperCase();
	address.setSTATE(txt);

	txt = txtFields[FIELD_ZIP].getValue();
	if(txt != null) txt = txt.toUpperCase();
	address.setZIP(txt);

	txt = txtFields[FIELD_COUNTRY].getValue();
	if(txt != null) txt = txt.toUpperCase();
	address.setCOUNTRY(txt);
	
	model.setAddressData(data);
	return address;

    }
    
	public void setData(Address address) {

//		Address address = (Address) data;
		if (address == null) {
			errorLog.logError(
				this.getClass().getName()
					+ ":"
					+ "void setData(DBObject data)"
					+ ":\n"
					+ "Address Missing.",
				new Exception("Design Error"));
			ErrorLogger.getInstance().logMessage(
				this.getClass().getName()
					+ ":" + rmk.ErrorLogger.getInstance().stkTrace(""));

			return;
		}

		txtFields[FIELD_ADDRESS].setValue("" + address.getAddress(0));
		txtFields[FIELD_ADDRESS2].setValue("" + address.getAddress(1));
		txtFields[FIELD_ADDRESS3].setValue("" + address.getAddress(2));
		txtFields[FIELD_CITY].setValue("" + address.getCITY());
		String state = address.getSTATE();
		if (state != null)
			state = state.toUpperCase();
		txtFields[FIELD_STATE].setValue("" + state);
		txtFields[FIELD_ZIP].setValue("" + address.getZIP());
		txtFields[FIELD_COUNTRY].setValue("" + address.getCOUNTRY());
		setPrimaryDataItem(address);
		setEdited(false);
	}
}
