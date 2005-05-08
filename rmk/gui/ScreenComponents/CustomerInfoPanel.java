package rmk.gui.ScreenComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EtchedBorder;

import carpus.gui.*;
import rmk.ErrorLogger;
import rmk.database.dbobjects.Customer;

public class CustomerInfoPanel extends carpus.gui.DataEntryPanel{
	BasicToolBar buttonBar;
	JLabel dealerLabel;
	Customer customer=null;
	rmk.gui.DBGuiModel model=null;
	static final int FIELD_PREFIX=0;
	static final int FIELD_FIRSTNAME=1;
	//      static final int FIELD_MIDDLE=2;
	
	static final int FIELD_LASTNAME=2;
	static final int FIELD_SUFFIX=3;
	
	static final int FIELD_PHONENUMBER=4;
	static final int FIELD_DISCOUNT=5;
	static final int FIELD_EMAIL=6;
	
	//      static final int FIELD_BALANCE=5;
	
	LabeledTextField[] 	txtFields = new LabeledTextField[7];
	
	//      Customer customer;
	
	public CustomerInfoPanel(Customer customer){
		this();
		setData(customer);
	}
	public CustomerInfoPanel(){
		LabeledTextField field;
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		
		c.gridwidth = 1;
		//++++++++++++++++++++++
		c.gridy = 0;
		c.gridx=0;
		
		field = new LabeledTextField("Title            ",FormattedTextFields.getNameField("", 5));
		gridbag.setConstraints(field, c);
		add(field);
		txtFields[FIELD_PREFIX] = field;
		
		c.gridx += 2;
		dealerLabel = new JLabel("<html><I><B><font size=+2 color='006633'>Dealer</font></B></I></html>");
		gridbag.setConstraints(dealerLabel, c);
		add(dealerLabel);
		
		//++++++++++++++++++++++
		c.gridy++;
		c.gridx=0;
		
		field = new LabeledTextField("First Name ", FormattedTextFields.getNameField("", 20));
		gridbag.setConstraints(field, c);
		add(field);
		txtFields[FIELD_FIRSTNAME] = field;
		
		//    	c.gridx+=c.gridwidth;
		//  	field = new LabeledTextField("MI       ", FormattedTextFields.getNameField("", 2));
		//  	gridbag.setConstraints(field, c);
		//  	add(field);
		//  	txtFields[FIELD_MIDDLE] = field;
		
		
		//++++++++++++++++++++++
		c.gridy++;
		c.gridx=0;
		
		//  	c.gridwidth=2;
		field = new LabeledTextField("Last Name ", FormattedTextFields.getNameField("",20));
		gridbag.setConstraints(field, c);
		add(field);
		txtFields[FIELD_LASTNAME] = field;
		c.gridwidth=1;
		
		c.gridx++;
		field = new LabeledTextField("Suffix ", FormattedTextFields.getNameField("",6));
		gridbag.setConstraints(field, c);
		add(field);
		txtFields[FIELD_SUFFIX] = field;
		
		c.gridx+=c.gridwidth;
		field = new LabeledTextField("  Discount ",new JTextField("0", 8));
		//  	field = new LabeledTextField("  Discount ",FormattedTextFields.getPercentageField(0));
		gridbag.setConstraints(field, c);
		add(field);
		txtFields[FIELD_DISCOUNT] = field;
		
		//++++++++++++++++++++++
		c.gridy++;
		c.gridx=0;
		
		//  	c.gridwidth = 1;
		//  	c.gridy++;
		//  	c.gridx=0;
		//  	field = new LabeledTextField("Balance      ", FormattedTextFields.getCurrencyField(0));
		//  	gridbag.setConstraints(field, c);
		//  	add(field);
		//  	txtFields[FIELD_BALANCE] = field;
		
		
		field = new LabeledTextField("Phone #      ", FormattedTextFields.getNameField("",10));
		gridbag.setConstraints(field, c);
		add(field);
		txtFields[FIELD_PHONENUMBER] = field;
		
		c.gridx+=c.gridwidth;
		c.gridwidth = 2;
		//  	c.gridx++;
		field = new LabeledTextField("EMail  ", FormattedTextFields.getNameField("",20));
		gridbag.setConstraints(field, c);
		add(field);
		txtFields[FIELD_EMAIL] = field;
		
		
		setFieldEditCheck(txtFields, "InfoChanged ", this);
		setPreferredSize(new Dimension(740,140));
		//  	setMaximumSize(new Dimension(975,150));
	}
	public void processFocusEvent(FocusEvent e){
		//    	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ e);
		if(customer == null || customer.getCustomerID() == 0){
			txtFields[FIELD_PREFIX].requestFocus();
		} else{
			txtFields[FIELD_FIRSTNAME].requestFocus();
		}
	}
	public void actionPerformed(ActionEvent e){};
	//========================================================
	public carpus.database.DBObject getData(){
		java.util.Vector data = model.getCustomerData();
		Customer customer;
		if(data != null){
			customer = (Customer )model.getCustomerData().get(0);
		} else{
			data = new java.util.Vector();
			customer = new Customer(0);
			data.add(customer);
			model.setCustomerData(data);
		}
		JFormattedTextField numField;
		String txt="";
		txt = txtFields[FIELD_PREFIX].getValue();
		if(txt != null) txt = txt.toUpperCase();
		customer.setPrefix(txt);
		txt = txtFields[FIELD_FIRSTNAME].getValue();
		if(txt != null) txt = txt.toUpperCase();
		customer.setFirstName(txt);
		txt = txtFields[FIELD_LASTNAME].getValue();
		if(txt != null) txt = txt.toUpperCase();
		customer.setLastName(txt);
		txt = txtFields[FIELD_SUFFIX].getValue();
		if(txt != null) txt = txt.toUpperCase();
		customer.setSuffix(txt);
		String phone = txtFields[FIELD_PHONENUMBER].getValue();
		phone = rmk.DataModel.getFixedPhoneNumber(phone);
		customer.setPhoneNumber(phone);
		
		customer.setEMailAddress(txtFields[FIELD_EMAIL].getValue());
		
		double savedDiscount = customer.getDiscount();
		double newDiscount = getDiscount();
		if(newDiscount != customer.getDiscount())
			if(!rmk.gui.Dialogs.yesConfirm("Confirm Changing CUSTOMER Discount from %" 
					+  savedDiscount
					+ " to %" + newDiscount )){
				ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "NOT changing discount");
				newDiscount = savedDiscount;
			}
		customer.setDiscount(newDiscount);
		
		return customer;
	}
	//--------------------------------------------------------
	public double getDiscount(){
		double newDiscount = Double.parseDouble(txtFields[FIELD_DISCOUNT].getValue());
		if(newDiscount < 1) 
			newDiscount *= 100;
		return newDiscount;
	}
	//--------------------------------------------------------
	public void setData(rmk.gui.DBGuiModel model){
		this.model = model;
		java.util.Vector data = model.getCustomerData();
		dealerLabel.setVisible(false);
		if(data != null){
			customer = (Customer)data.get(0);
			setData((carpus.database.DBObject)customer);
		}
	}
	//--------------------------------------------------------
	public void setData(carpus.database.DBObject data){
		Customer customer = (Customer) data;
		setEdited(false);
		if(customer == null) return;
		
		setPrimaryDataItem(customer);
		
		dealerLabel.setVisible(customer.isDealer());
		txtFields[FIELD_PREFIX].setValue(customer.getPrefix());
		txtFields[FIELD_FIRSTNAME].setValue(customer.getFirstName());
		txtFields[FIELD_LASTNAME].setValue(customer.getLastName());
		//  	txtFields[FIELD_MIDDLE].setValue(customer.getMiddleName());
		txtFields[FIELD_SUFFIX].setValue(customer.getSuffix());
		txtFields[FIELD_PHONENUMBER].setValue(customer.getPhoneNumber());
		txtFields[FIELD_EMAIL].setValue(customer.getEMailAddress());
		
		//  	numField = (JFormattedTextField)txtFields[FIELD_BALANCE].getField();
		//  	numField.setValue(new Double(customer.getBalance()));
		//  	txtFields[FIELD_DISCOUNT]numField = (LabeledTextField)txtFields[FIELD_DISCOUNT].getField();
		double discount = customer.getDiscount();
		if(discount < 1)
			discount *= 100;
		txtFields[FIELD_DISCOUNT].setValue(""+new Double(discount));
	}
	//========================================================
	public static void main(String args[])throws Exception{
		rmk.gui.Application.main(args);
	}
}
