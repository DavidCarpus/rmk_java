package rmk.gui.ScreenComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;

import carpus.gui.*;
import rmk.database.FinancialInfo;
import rmk.database.dbobjects.Customer;

public class CustomerDetailPanel 
    extends carpus.gui.DataEntryPanel 
    implements ActionListener
	       , ItemListener
{
//      Detail detail = null;
    rmk.gui.DBGuiModel model= null;
    Customer customer = null;;
    JButton memoButton = new JButton("Memo");
    JCheckBox flagged =  new JCheckBox("Flagged");
    JCheckBox dealer =  new JCheckBox("Dealer");
    JButton bladeButton = new JButton("BladeList");
    JLabel flaggedLabel = new JLabel("  Flagged");
    String currentMemo=null;
    String currentBladeList=null;

    static final int FIELD_TAXNUMBER  =0;
    static final int FIELD_TERMS  =1;
    static final int FIELD_CCNUMBER  =2;
    static final int FIELD_CCEXPIRATION_DATE  =3;
    

    JComponent[] txtFields = new JComponent[4];
    carpus.util.Logger errLog = carpus.util.Logger.getInstance();

    public CustomerDetailPanel(){	
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	JComponent field;

	setEdited(true);
	setLayout(gridbag);

  	c.fill = GridBagConstraints.NONE;
	c.anchor = GridBagConstraints.WEST;

//  	c.gridwidth = 3;
	//================================
	c.gridy = 0;
	c.gridx = 0;
	field = new LabeledTextField("TaxID",new JTextField("", 12));
	gridbag.setConstraints(field, c);
	add(field);
	txtFields[FIELD_TAXNUMBER] = field;
	setFieldEditCheck(field, "CUSTOMER DETAIL CHANGED ", this);

	c.gridx++;
	field = new LabeledTextField("  Terms",new JTextField("", 2));
	gridbag.setConstraints(field, c);
	add(field);
	txtFields[FIELD_TERMS] = field;
	setFieldEditCheck(field, "CUSTOMER DETAIL CHANGED ", this);

	c.gridx++;
	flagged.setMnemonic(KeyEvent.VK_F); 
	flagged.addItemListener(this);
	gridbag.setConstraints(flagged, c);
	add(flagged);

	c.gridx++;
	dealer.addItemListener(this);
	dealer.setMnemonic(KeyEvent.VK_D); 
	gridbag.setConstraints(dealer, c);
	add(dealer);

	c.gridx++;
	field = memoButton;
	memoButton.addActionListener(this);
	gridbag.setConstraints(field , c);
	add(field);

	c.gridx++;
	field = bladeButton;
	bladeButton.addActionListener(this);
	gridbag.setConstraints(field , c);
	add(field);
	//================================
	c.gridy++;
  	c.gridwidth = 2;
	c.gridx = 0;
	field = new LabeledTextField("Credit Card #:",new JTextField("", 18));
	gridbag.setConstraints(field, c);
	add(field);
	txtFields[FIELD_CCNUMBER] = field;
	setFieldEditCheck(field, "CUSTOMER DETAIL CHANGED ", this);

	c.gridx += c.gridwidth;
  	JTextField expiration = new JTextField("", 7);
//  	JTextField expiration = carpus.gui.FormattedTextFields.getDateField();
//      	java.text.DateFormat format = new java.text.SimpleDateFormat("MM/yy");
//      	java.text.DateFormat format = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT);
//  //  	format.setLenient(true);
//    	javax.swing.text.DateFormatter formatter = new javax.swing.text.DateFormatter(format);
//  	JFormattedTextField expiration = new JFormattedTextField(formatter);
//  	expiration.setMinimumSize(new Dimension(80,20));
//  	expiration.setPreferredSize(new Dimension(80,20));

	field = new LabeledTextField("Expiration:",expiration);
	gridbag.setConstraints(field, c);
  	add(field);

	txtFields[FIELD_CCEXPIRATION_DATE] = field;
	setFieldEditCheck(field, "CUSTOMER DETAIL CHANGED ", this);

	setMaximumSize(new Dimension(825,150));
    }

    CustomerDetailPanel(Customer customer){
	this();
	setData(customer);
    }

    //----------------------------------------------------------
    public void itemStateChanged(ItemEvent e) {
	Object source = e.getItemSelectable();
	if (source == dealer) {
	    if(! isEdited())
		actionPerformed(new ActionEvent(this, 1, "FLAG_CHANGED"));
	} else if (source == flagged) {
	    if(flagged.isSelected())
		flagged.setForeground(Color.RED);
	    else
		flagged.setForeground(Color.BLACK);
	    if(! isEdited())
		actionPerformed(new ActionEvent(this, 1, "FLAG_CHANGED"));
	} else{
	    errLog.logError(this.getClass().getName() + ":" + "void itemStateChanged(ItemEvent e)" + ":\n" +
			    "unhandled source.\n" + 
			    ((Class)source.getClass()).getName(), 
			    new Exception("Design Error"));
	}
    }
    //----------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase();

	ActionEvent event=null;

  	if(command.equals("EDIT MEMO") || command.equals("ADD MEMO")){
	    if(currentMemo == null) currentMemo = "";
	    String text = rmk.gui.Dialogs.getEditNote(currentMemo, "Notes", rmk.gui.Dialogs.MAX_LEN_USER_NOTES, true);
	    if(text == null) return; // NO change
	    text = text.replace('\n', '|');
	    currentMemo = text;
	    event = new ActionEvent(this, 1, "CUSTOMER DETAIL CHANGED");
	    //----------------------------------------------------------
  	}else if(command.equals("EDIT BLADELIST") || command.equals("ADD BLADELIST")){
	    if(currentBladeList == null) currentBladeList = "";
	    String text = rmk.gui.Dialogs.getEditNote(currentBladeList, "BladeList", rmk.gui.Dialogs.MAX_LEN_BLADELIST_NOTES, true);
	    if(text == null) return; // NO change
	    currentBladeList = text;
	    event = new ActionEvent(this, 1, "CUSTOMER DETAIL CHANGED");
	    //----------------------------------------------------------
  	}else if(command.equals("FLAG_CHANGED")){
	    System.out.println(this.getClass().getName() + ":" + command + "|");
	    setEdited(true);
	    event = new ActionEvent(this, 1, "CUSTOMER DETAIL CHANGED");
	    //----------------------------------------------------------
  	} else {
	    System.out.println(this.getClass().getName() + ":Undefined:" + command + "|");
	    event = new ActionEvent(this, 1, "CUSTOMER DETAIL CHANGED");
  	}

	if(event != null){
	    System.out.println(this.getClass().getName() + ":"+ "notify listners");	    
	    notifyListeners(event);
	}
    }
    //----------------------------------------------------------

    public void setData(rmk.gui.DBGuiModel model){
	this.model = model;
	java.util.Vector data = model.getCustomerData();
	if(data != null)
	    setData((carpus.database.DBObject)data.get(0));
	else
	    ((LabeledTextField)txtFields[FIELD_TERMS]).setValue("1");
    }

    //========================================================
    public carpus.database.DBObject getData(){
	java.util.Vector data = model.getCustomerData();
	Customer customer=null;
	if(data != null){
	    customer = (Customer )model.getCustomerData().get(0);
	} else{
	    errLog.logError(this.getClass().getName() + ":" + "DBObject getData()" + ":\n" +
			    "customer Missing.\n", new Exception("Design Error"));
	}

	customer.setDealer( (dealer.isSelected()? 1: 0));
	customer.setFlag( flagged.isSelected());
	customer.setTaxNumber(((LabeledTextField)txtFields[FIELD_TAXNUMBER]).getValue());
	customer.setTerms(((LabeledTextField)txtFields[FIELD_TERMS]).getValue());
	customer.setMemo(currentMemo);
	customer.setBladeList(currentBladeList);


  	String ccnum = ((LabeledTextField)txtFields[FIELD_CCNUMBER]).getValue();

	long vcode = rmk.database.FinancialInfo.getVCode(ccnum);
	
  	ccnum = rmk.database.FinancialInfo.removeCardNumberDashes(ccnum);
	ccnum = rmk.database.FinancialInfo.getBaseCCNumber(ccnum);
	if(vcode > 0){
		String strVCode = ""+vcode;
		while(strVCode.length() < FinancialInfo.VCODE_LENGTH){
			strVCode = "0" + strVCode;
		}
	    ccnum += "*" + strVCode;
	}

	customer.setCreditCardNumber(ccnum);
	
	String expiration = ((LabeledTextField)txtFields[FIELD_CCEXPIRATION_DATE]).getValue();
	if(ccnum == null || ccnum.trim().length() == 0) // clear expiration date if CC# was cleared
	    expiration = "";

	if(expiration.length() > 0){
	    java.text.DateFormat format = new java.text.SimpleDateFormat("MM/yyyy");
	    format.setLenient(true);
	    java.util.GregorianCalendar expDate = new java.util.GregorianCalendar();
	    try {
		expDate.setTime(format.parse(expiration));
		int year = expDate.get(Calendar.YEAR);
		
		if(year < 50)
		    year += 2000;
		if(year > 50 && year < 1000)
		    year += 1900;

		expDate.set(Calendar.YEAR, year);

		customer.setCreditCardExpiration(expDate);
	    } catch (Exception e){
		System.out.println(this.getClass().getName() + ":"+ e);
	    } // end of try-catch
	} else{
	    customer.setCreditCardExpiration(null);
	}

  	model.setCustomerData(data);
	return customer;
    }
    //========================================================
    private void setData(carpus.database.DBObject data){
	if(model == null){
	    errLog.logError(
			    this.getClass().getName() + ":" +
			    "void setData(DBObject data)" + ":\n" +
			    "Model Missing.\n",
			    new Exception("Design Error")
						      );
	}
	customer = (Customer) data;
	if(customer == null){
	    errLog.logError(this.getClass().getName() + ":" + "void setData(DBObject data)" + ":\n" +
			    "customer Missing.\n", new Exception("Design Error"));
	    return;
	}
	((LabeledTextField)txtFields[FIELD_TAXNUMBER]).setValue(customer.getTaxNumber()!= null?
								customer.getTaxNumber()
								: "");
	((LabeledTextField)txtFields[FIELD_TERMS]).setValue(customer.getTerms()!= null?
								customer.getTerms()
								: "");

	currentMemo=customer.getMemo();
	String memoButtonLabel = "Add Memo";
	if(currentMemo != null && currentMemo.length() <=0 ) currentMemo = null;
	if(currentMemo != null){
	    memoButtonLabel = "Edit Memo";
	    memoButton.setForeground(rmk.gui.CustomerScreen.DK_GREEN);
	} else{
	    memoButton.setForeground(Color.BLACK);
	}
	memoButton.setText(memoButtonLabel);

	currentBladeList=customer.getBladeList();
	if(currentBladeList != null && currentBladeList.length() <=0) currentBladeList=null;
	String bladeButtonLabel = "Add BladeList";
	if(currentBladeList != null){
	    bladeButtonLabel = "Edit BladeList";
	    bladeButton.setForeground(rmk.gui.CustomerScreen.DK_GREEN);
	} else{
	    bladeButton.setForeground(Color.BLACK);
	}
	bladeButton.setText(bladeButtonLabel);

	dealer.setSelected(customer.isDealer());
	flagged.setSelected(customer.isFlag());
	if(customer.isFlag())
	    flaggedLabel.setForeground(Color.RED);
	else
	    flaggedLabel.setForeground(Color.BLACK);

  	String ccnum = customer.getCreditCardNumber();
	long vcode = rmk.database.FinancialInfo.getVCode(ccnum);
	
  	ccnum = rmk.database.FinancialInfo.removeCardNumberDashes(ccnum);
  	ccnum = rmk.database.FinancialInfo.addCardNumberDashes(ccnum);
	ccnum = rmk.database.FinancialInfo.getBaseCCNumber(ccnum);
	if(vcode > 0){
		String strVCode = ""+vcode;
		while(strVCode.length() < FinancialInfo.VCODE_LENGTH){
			strVCode = "0" + strVCode;
		}
	    ccnum += "*" + strVCode;
	}

	((LabeledTextField)txtFields[FIELD_CCNUMBER]).setValue(ccnum != null? ccnum:"");

    	java.util.GregorianCalendar expiration = customer.getCreditCardExpiration();
      	java.text.DateFormat format = new java.text.SimpleDateFormat("MM/yyyy");

//  	System.out.println(this.getClass().getName() + ":"+ format.format(expiration.getTime()));

	format.setLenient(true);
  	((LabeledTextField)txtFields[FIELD_CCEXPIRATION_DATE]).setValue(expiration != null? 
									""+format.format(expiration.getTime()):"");

//  	String expiration = ((LabeledTextField)txtFields[FIELD_CCEXPIRATION_DATE]).getValue();
//  	System.out.println(this.getClass().getName() + ":"+ expiration);

	setEdited(false);
    }

    //========================================================
    public static void main(String args[]) throws Exception {
	rmk.gui.Application.main(args);
    }

}
