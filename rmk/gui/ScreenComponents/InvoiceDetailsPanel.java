package rmk.gui.ScreenComponents;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EtchedBorder;

import Configuration.Config;

import java.text.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

import carpus.gui.*;
import carpus.util.DateFunctions;
import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.Invoice;

public class InvoiceDetailsPanel 
extends carpus.gui.DataEntryPanel
implements ActionListener
//, ChangeListener
{
    Invoice invoice = null;
    boolean loading=false;
    rmk.DataModel sys = rmk.DataModel.getInstance();
    boolean manuallyChangedTax=false;
    
    static final SimpleDateFormat dateFormatter = new SimpleDateFormat ("MM/dd/yy");
    JButton paymentButton = new JButton("Payments");
    JButton knifeCountButton = new JButton("KnifeCounts");
    JButton notesButton = new JButton("Notes");
    JButton discountButton = new JButton("Discount");
    JButton taxRateButton = new JButton("TaxRate");
    
    String currentNotes=null;
    double discountPercentage=0;
    double taxRate=0;
    double taxChange=0;
    
    static final int FIELD_INVOICE=0;
    static final int FIELD_DATEORDERED=1;
    static final int FIELD_DATEESTIMATED=2;
    static final int FIELD_DATESHIPPED=3;
    static final int FIELD_TOTALRETAIL = 4;
    static final int FIELD_TOTALKNIVES = 5;
    static final int FIELD_SHIPPINGINFO=6;
    static final int FIELD_SHIPPINGCOST=7;
    static final int FIELD_SHIPPINGINSTRUCTIONS=8;
    static final int FIELD_PONUMBER=9;
    static final int FIELD_NOTES=10;
    
    JRadioButton ship = new JRadioButton("Shipping Address");
    JRadioButton shopSale = new JRadioButton("Shop Sale");
    JRadioButton sameShippingAddress = new JRadioButton("Same Ship Address");
    ButtonGroup group = new ButtonGroup();
    JPanel shipAddressPanel = new JPanel();
    JPanel shippingPanel = new JPanel();
    JTextArea shippingAddress = new JTextArea();
//  LabeledTextField[] txtFields = new LabeledTextField[10];
    JComponent[] txtFields = new JComponent[10];
    JFormattedTextField orderedField;
//  LabeledTextField[] txtFields = new LabeledTextField[10];
    InvoicePaymentsSummaryPanel paymentSummary = new InvoicePaymentsSummaryPanel();
    //========================================================
    public InvoiceDetailsPanel(){
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        LabeledTextField field;
        
        setLayout(gridbag);
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        
        c.gridy = 0;
        c.gridx = 0;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.CENTER;
        JTextField txt = new JTextField("", 6);
        txt.setDisabledTextColor(Color.white);
        txt.setForeground(Color.BLACK);
        txt.setBackground(Color.LIGHT_GRAY);
        txt.setEditable(false);
        txt.setFocusable(false);
        
        field = new LabeledTextField("Invoice ",txt);
        txt.setFont(new Font("Serif", Font.BOLD, 18));
//      c.gridheight = 2;
        gridbag.setConstraints(field, c);
        add(field);
        txtFields[FIELD_INVOICE] = field;
//      field.setBackground(Color.RED);
        
        c.gridwidth = 1;
        c.gridx=3;
        JPanel noteTaxPnl = new JPanel();
        notesButton.addActionListener(this);
        noteTaxPnl.add(notesButton);
        taxRateButton.addActionListener(this);
        noteTaxPnl.add(taxRateButton);
        
//      gridbag.setConstraints(taxRateButton, c);
        gridbag.setConstraints(noteTaxPnl, c);
        add(noteTaxPnl);
        
        c.gridx=0;
        c.gridwidth = 1;
        c.gridy++;
        orderedField = carpus.gui.FormattedTextFields.getDateField();
        field = new LabeledTextField("Ordered    ",orderedField);
//      txt = new JTextField("", 10);
//      field = new LabeledTextField("Ordered    ",txt);
//      field = new LabeledTextField("Ordered         ",new JTextField("", 8));
        gridbag.setConstraints(field, c);
        add(field);
        txtFields[FIELD_DATEORDERED] = field;
        
        
        c.gridx++;
        
        txt = new JTextField("", 6);
        field = new LabeledTextField("  Estimated ",txt);
        JPanel panel = getEstDatePanel(field);	
        gridbag.setConstraints(panel, c);
        add(panel);
        txtFields[FIELD_DATEESTIMATED] = field;
        
        c.gridx++;
        knifeCountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evnt) {
            	parentScreen.buttonPress(ScreenController.BUTTON_KNIFE_COUNT,0);
            }
        }
        );
        c.gridwidth = 1;
        gridbag.setConstraints(knifeCountButton, c);
        add(knifeCountButton);
        
        JPanel financeButtons = new JPanel();
        paymentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evnt) {
            	parentScreen.buttonPress(ScreenController.BUTTON_F7,0);
            }});
        
        discountButton.addActionListener(this);
        financeButtons.add(paymentButton);
        financeButtons.add(discountButton);
        
        c.gridwidth = 1;
        c.gridx++;
//      c.gridx=4;
        gridbag.setConstraints(financeButtons, c);
        add(financeButtons);
        
        c.fill = GridBagConstraints.NONE;
        
//      c.gridx++;
//      c.gridheight=3;
//      c.fill = GridBagConstraints.VERTICAL;
//      gridbag.setConstraints(paymentSummary, c);
//      add(paymentSummary);
        c.gridheight=1;
        
        c.gridx++;
        c.gridy++;
        c.gridx = 0;
        JTextField retail = carpus.gui.FormattedTextFields.getCurrencyField(0);
        retail.setEditable(false);
        retail.setForeground(Color.BLUE);
        retail.setFocusable(false);
        
        field = new LabeledTextField("TotalRetail   ",retail);
        gridbag.setConstraints(field, c);
        add(field);
        txtFields[FIELD_TOTALRETAIL] = field;
        
        
        c.gridx++;
        JTextField kniveCnt = new JTextField("", 3);
//      kniveCnt.setDisabledTextColor(Color.BLUE);
        kniveCnt.setForeground(Color.BLUE);
//      kniveCnt.setBackground(Color.black);
        kniveCnt.setEditable(false);
        kniveCnt.setFocusable(false);
        field = new LabeledTextField("Knives ",kniveCnt);
        gridbag.setConstraints(field, c);
        add(field);
        txtFields[FIELD_TOTALKNIVES] = field;
        
        c.gridx++;
        JTextField poField = new JTextField("", 12);
        field = new LabeledTextField("             PO# ", poField);
//      field = new LabeledTextField("  Estimated   ",new JTextField("", 8));
        gridbag.setConstraints(field, c);
        add(field);
        txtFields[FIELD_PONUMBER] = field;
        
        c.gridx++;
        c.gridheight=3;
        c.fill = GridBagConstraints.VERTICAL;
        gridbag.setConstraints(paymentSummary, c);
        add(paymentSummary);
        
        c.fill = GridBagConstraints.NONE;
        c.gridheight=1;
        
        
        c.gridy++;
        c.gridx = 0;
        JTextField shipping = new JTextField(7);
        field = new LabeledTextField("Shipping    $",shipping);
        gridbag.setConstraints(field, c);
        add(field);
        txtFields[FIELD_SHIPPINGCOST] = field;
        
        
        c.gridx++;
        JTextField shipped = new JTextField("", 8);
        shipped.setEditable(false);
        shipped.setFocusable(false);
        
        field = new LabeledTextField("Shipped ",shipped);
        gridbag.setConstraints(field, c);
        add(field);
        txtFields[FIELD_DATESHIPPED] = field;
        
        c.gridx++;
        field = new LabeledTextField("Shipping Info ",new JTextField("", 12));
        gridbag.setConstraints(field, c);
        add(field);
        txtFields[FIELD_SHIPPINGINSTRUCTIONS] = field;
        
        
        
        //------------------
//      "Shipping Address "
        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        
        shippingAddress.setFont(new Font("Serif", Font.BOLD, 14));
        
        shippingAddress.setPreferredSize(new Dimension(320,80));
        JScrollPane  shippingPane = new JScrollPane(shippingAddress);
        
//      JPanel pnl = new JPanel();
        shippingPanel.setLayout(new BoxLayout(shippingPanel,BoxLayout.X_AXIS));
        
        JPanel pnl2 = new JPanel();
        ButtonGroup group = new ButtonGroup();
        group.add(shopSale);
        group.add(sameShippingAddress);
        group.add(ship);
        
        shopSale.addActionListener(this);
        sameShippingAddress.addActionListener(this);
        ship.addActionListener(this);
//      shopSale.addChangeListener(this);
//      sameShippingAddress.addChangeListener(this);
//      ship.addChangeListener(this);
        
        pnl2.add(shopSale);
        pnl2.add(sameShippingAddress);
        pnl2.add(ship);
        pnl2.setLayout(new BoxLayout(pnl2,BoxLayout.Y_AXIS));
        shippingPanel.add(pnl2);
        
        
        shipAddressPanel.add(new JLabel("Shipping Address"));
        shipAddressPanel.add(shippingPane);
        shippingPanel.add(shipAddressPanel);
//      txtFields[FIELD_SHIPPINGINFO] = field;
//      gridbag.setConstraints(shippingPane, c);
//      add(shippingPane);
        txtFields[FIELD_SHIPPINGINFO] = shippingAddress;
        gridbag.setConstraints(shippingPanel, c);
        add(shippingPanel);
        //------------------
        
        
        txtFields[FIELD_DATEESTIMATED].setNextFocusableComponent(poField);
        
        setFieldEditCheck(txtFields, "InvoiceChanged", this);
//      setMaximumSize(new Dimension(725,150));
        setPreferredSize(new Dimension(925,250));
    }
    
    //--------------------------------------------------------
    InvoiceDetailsPanel(Invoice invoice) throws Exception{
        this();
        setData(invoice);
    }
    JPanel getEstDatePanel(LabeledTextField dateField){
        JPanel results = new JPanel();
        ((JTextField)dateField.getField()).addActionListener(this);
        dateField.addKeyListener(new EstimatedDateKeyCheck("EST_DATE_CHANGE", this, dateField) );
        
        JButton backWeekButton = new JButton("<");
        backWeekButton.setMargin(new Insets(0,0,0,0));
        backWeekButton.addActionListener(this);
        
        JButton forwardWeekButton = new JButton(">");
        forwardWeekButton.setMargin(new Insets(0,0,0,0));
        forwardWeekButton.addActionListener(this);
        
        results.add(dateField);
        results.add(backWeekButton); 
        results.add(forwardWeekButton); 
        return results;
    }
    boolean updateTaxRate(Invoice invoice){
        // lock in tax rate when invoice is created
        // If new invoice, just set the taxrate to what it should be,
        // otherwise, ask to change...
        if(invoice.getInvoice() == 0){
            double currentTaxRate = rmk.DataModel.getInstance().financialInfo.getInvoiceTaxRate(invoice);
            invoice.setTaxPercentage(currentTaxRate);
            taxRate = invoice.getTaxPercentage();
            return true;
        } else{
            taxRate = invoice.getTaxPercentage();
        }

        return false;
    }
    //========================================================
    public void onPaymentsScreen(boolean val){
        paymentButton.setVisible(!val);
        shippingPanel.setVisible(!val);
    }
    public void processFocusEvent(FocusEvent e){
        txtFields[FIELD_DATEESTIMATED].grabFocus();
    }
    public boolean isShippingAddressField(Component cmpt){
    	return cmpt == shippingAddress;
    }
    //========================================================
    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand().toUpperCase();

//        boolean optionChanged = false;
        
        if(command.equals("EDIT NOTES") || command.equals("ADD NOTES")){
            String text = rmk.gui.Dialogs.getEditNote(currentNotes, "Notes", rmk.gui.Dialogs.MAX_LEN_INVOICE_NOTES, true);
            if(text == null || (currentNotes != null && currentNotes.equalsIgnoreCase(text))) return; // NO change
            text =  text.replace('\n', '|');
            if(currentNotes != null && currentNotes.equalsIgnoreCase(text)) return; // NO change
            currentNotes = text;
            invoice.setComment(currentNotes);
            notesButton.setToolTipText(text);
//            event = new ActionEvent(this, 1, "INVOICECHANGED");
            parentScreen.updateOccured(invoice,ScreenController.UPDATE_EDIT, invoice);
            return;
    	}else if(command.equals("F1")){
    		parentScreen.updateOccured(null,ScreenController.BUTTON_F1, null);
    		return;
    	}else if(command.equals("F2")){
    		parentScreen.updateOccured(null,ScreenController.BUTTON_F2, null);
    		return;
    	}else if(command.equals("F3")){
    		parentScreen.updateOccured(null,ScreenController.BUTTON_F3, null);
    		return;
        } else if(command.equals("SHOP SALE")){
        	if(switchToShopSale())
        		parentScreen.updateOccured(invoice,ScreenController.UPDATE_EDIT, invoice);        
            return;
            
        } else if(command.equals("TAXRATE")){
            if(taxRate < 1) taxRate *= 100.0;
            double oldRate = taxRate;
            String reply=JOptionPane.showInputDialog("New Tax Rate?", ""+taxRate);
            if(reply == null || reply.equals("")) return;  // NO change	    
            try {
                taxRate = Double.parseDouble(reply);
                manuallyChangedTax=true;
                ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "newTaxRate:" + taxRate);
            } catch (Exception err){
            } // end of try-catch
            if(taxRate > 1) taxRate /= 100.0;
            if(taxRate != oldRate){
            	invoice.setTaxPercentage(taxRate);
            	parentScreen.updateOccured(invoice,ScreenController.UPDATE_EDIT, invoice);            	
            }else
            	ErrorLogger.getInstance().logMessage("TaxRate not changed");
            return;
            
            
        } else if(command.equals("DISCOUNT")){
            double newDisc = discountPercentage;
            if(newDisc < 1) newDisc *= 100;
            String reply=JOptionPane.showInputDialog("NewDiscount?", ""+newDisc);
            if(reply == null || reply.equals("")) return;  // NO change	    
            try {
                newDisc = Double.parseDouble(reply);
                ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "newDisc:" + newDisc);
                
                if(discountPercentage != newDisc){
                    discountPercentage = newDisc;
                    invoice.setDiscountPercentage(discountPercentage);
                    parentScreen.updateOccured(invoice,ScreenController.UPDATE_EDIT, invoice);
//                    event = new ActionEvent(this, 1, "INVOICECHANGED");
                }
            } catch (Exception err){
            } // end of try-catch
            return;            
            //---------------------------------
        } else if(command.equals("SAME SHIP ADDRESS")){
            shipAddressPanel.setVisible(false);
            invoice.setShopSale(false);
            invoice.setShippingInfo("");
            ((JTextArea)txtFields[FIELD_SHIPPINGINFO]).setText("");
            parentScreen.updateOccured(invoice,ScreenController.UPDATE_EDIT, invoice);
//            optionChanged = true;
            return;
            //---------------------------------
        } else if(command.equals("SHIPPING ADDRESS")){
            shipAddressPanel.setVisible(true);
            invoice.setShopSale(false);
            ((JTextArea)txtFields[FIELD_SHIPPINGINFO]).setText(" ");
            parentScreen.updateOccured(invoice,ScreenController.UPDATE_EDIT, invoice);
//            optionChanged = true;
            return;
            //---------------------------------
            
//        } else if(command.equals("EST_UP_WEEK")){
//            adjustEstDate(-7);
//            event = new ActionEvent(this, 1, "INVOICECHANGED");
//        } else if(command.equals("EST_DOWN_WEEK")){
//            adjustEstDate(7);
//            event = new ActionEvent(this, 1, "INVOICECHANGED");

            
        } else if(command.equals("<")){
            GregorianCalendar date = DateFunctions.gregorianFromString(((LabeledTextField)txtFields[FIELD_DATEESTIMATED]).getValue());
            date.add(GregorianCalendar.WEEK_OF_YEAR, -1);
            ((LabeledTextField)txtFields[FIELD_DATEESTIMATED]).setValue(
                    dateFormatter.format(date.getTime()));
            invoice.setDateEstimated(date);
            parentScreen.updateOccured(invoice,ScreenController.UPDATE_EDIT, invoice);
            return;
//            event = new ActionEvent(this, 1, "INVOICECHANGED");
        } else if(command.equals(">")){
            GregorianCalendar date = DateFunctions.gregorianFromString(((LabeledTextField)txtFields[FIELD_DATEESTIMATED]).getValue());
            date.add(GregorianCalendar.WEEK_OF_YEAR, 1);
            ((LabeledTextField)txtFields[FIELD_DATEESTIMATED]).setValue(
                    dateFormatter.format(date.getTime()));
            invoice.setDateEstimated(date);
//            event = new ActionEvent(this, 1, "INVOICECHANGED");
            parentScreen.updateOccured(invoice,ScreenController.UPDATE_EDIT, invoice);
            return;
        }
        
        ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "|");
        ErrorLogger.getInstance().TODO();
    }
    
    //--------------------------------------------------------
    boolean switchToShopSale(){
        shipAddressPanel.setVisible(false);
        long difference=0;
        if(invoice.getDateEstimated() != null)
            difference = invoice.getDateEstimated().getTimeInMillis()
            - (new java.util.GregorianCalendar()).getTimeInMillis();
        long days = difference / (1000 * 60 * 60 * 24);
        ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ difference + ":" + days);
        if(days > 7){
            invoice.setDateEstimated(new java.util.GregorianCalendar());
            ((LabeledTextField)txtFields[FIELD_DATEESTIMATED]).setValue(
                    dateFormatter.format(invoice.getDateEstimated().getTime()));
            
        }
        invoice.setShopSale(true);
        updateTaxRate(invoice);
        double storedTaxrate = invoice.getTaxPercentage();
        if(storedTaxrate <= 0){
            taxRate = sys.financialInfo.getInvoiceTaxRate(invoice);
        }
        
        return true;
    }
    //--------------------------------------------------------
    void adjustEstDate(int days){
        LabeledTextField field = (LabeledTextField)txtFields[FIELD_DATEESTIMATED];
        java.util.GregorianCalendar date = DateFunctions.gregorianFromString(field.getValue());
        date.add(Calendar.DATE, days);
        field.setValue(dateFormatter.format(date.getTime()));
    }
    //--------------------------------------------------------
    public Invoice getData(){
        JFormattedTextField numField;
        
        invoice.setInvoice(Long.parseLong(((LabeledTextField)txtFields[FIELD_INVOICE]).getValue()));
        invoice.setDateOrdered(DateFunctions.gregorianFromString(((LabeledTextField)txtFields[FIELD_DATEORDERED]).getValue()));	
        invoice.setDateEstimated(DateFunctions.gregorianFromString(((LabeledTextField)txtFields[FIELD_DATEESTIMATED]).getValue()));	
        invoice.setDateShipped(DateFunctions.gregorianFromString(((LabeledTextField)txtFields[FIELD_DATESHIPPED]).getValue()));
        numField = (JFormattedTextField)((LabeledTextField)txtFields[FIELD_TOTALRETAIL]).getField();
        invoice.setTotalRetail(Double.parseDouble(""+numField.getValue()));
        
        String info = ((JTextArea)txtFields[FIELD_SHIPPINGINFO]).getText();
        info =  info.replace('\n', '|');
        info = info.toUpperCase();
        invoice.setShippingInfo(info);	
        
        
        invoice.setShippingInstructions(getTranslatedShippingInstructions());
        
//      numField = (JFormattedTextField)((LabeledTextField)txtFields[FIELD_SHIPPINGCOST]).getField();
        String txt = ((LabeledTextField)txtFields[FIELD_SHIPPINGCOST]).getValue();
        if(txt == null || txt.length() == 0)	    txt = "0";
        double shipAmt=0;
        try{
        	shipAmt = Double.parseDouble(txt);
        }catch (Exception e) {
        	String msg = "Invalid shipping amount:\n" + txt; 
            JOptionPane.showMessageDialog(null, msg, "Data Entry Error:",
                    JOptionPane.ERROR_MESSAGE);
            return null;
		}
    	invoice.setShippingAmount(shipAmt);
        
        invoice.setPONumber(((LabeledTextField)txtFields[FIELD_PONUMBER]).getValue());	
        if(currentNotes != null)
            currentNotes =  currentNotes.replace('\n', '|');
        invoice.setComment(currentNotes);
//        ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "discountPercentage:" + discountPercentage);
        
        invoice.setDiscountPercentage(discountPercentage);
        invoice.setTaxPercentage(taxRate);
        invoice.setShopSale(shopSale.isSelected());
        
//        ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ group.getSelection());
        
        return invoice;
    }
    String getTranslatedShippingInstructions(){
        String results="";
        results = ((LabeledTextField)txtFields[FIELD_SHIPPINGINSTRUCTIONS]).getValue();
        results = Config.getShippingCodeTranslation(results); 
        return results;
    }

    public void setTotalRetail(double total){
        ((LabeledTextField)txtFields[FIELD_TOTALRETAIL]).setValue(total);
//      if(invoice != null)
//      paymentSummary.setInvoice(invoice.getInvoice());
    }
    public void setTotalKnives(int knifeCnt){
        ((LabeledTextField)txtFields[FIELD_TOTALKNIVES]).setValue(""+knifeCnt);
    }
    
    public double getTotalRetail(){
        JFormattedTextField numField;
        numField = (JFormattedTextField)((LabeledTextField)txtFields[FIELD_TOTALRETAIL]).getField();
        return Double.parseDouble(""+numField.getValue());
    }
    
    //--------------------------------------------------------
    public void setData(Invoice invData){
    	invoice = invData;
    	loading = true;
    	
    	JFormattedTextField numField;
    	
    	((LabeledTextField)txtFields[FIELD_INVOICE]).setValue(""+invoice.getInvoice());	
    	((LabeledTextField)txtFields[FIELD_DATEORDERED]).setValue(invoice.getDateOrdered()!= null?
    			dateFormatter.format(invoice.getDateOrdered().getTime())
				: "");
    	((LabeledTextField)txtFields[FIELD_DATEESTIMATED]).setValue(invoice.getDateEstimated()!= null?
    			dateFormatter.format(invoice.getDateEstimated().getTime())
				: "");
    	((LabeledTextField)txtFields[FIELD_DATESHIPPED]).setValue(invoice.getDateShipped()!= null?
    			dateFormatter.format(invoice.getDateShipped().getTime())
				: "");
    	//    ((LabeledTextField)txtFields[FIELD_TOTALRETAIL]).setValue(invoice.getTotalRetail());
    	((LabeledTextField)txtFields[FIELD_TOTALRETAIL]).setValue(sys.financialInfo.getTotalRetail(invoice));
    	
    	String info = invoice.getShippingInfo();
    	if(info!= null)
    		info =  info.replace('|', '\n');
    	
    	((JTextArea)txtFields[FIELD_SHIPPINGINFO]).setText(info!= null? info:"");
    	
    	((LabeledTextField)txtFields[FIELD_SHIPPINGINSTRUCTIONS]).setValue(invoice.getShippingInstructions()!= null?
    			invoice.getShippingInstructions():"");
    	
    	//    ((JTextField)txtFields[FIELD_SHIPPINGCOST]).setText(""+invoice.getShippingAmount());
    	((LabeledTextField)txtFields[FIELD_SHIPPINGCOST]).setValue(""+invoice.getShippingAmount());
    	
    	((LabeledTextField)txtFields[FIELD_PONUMBER]).setValue(invoice.getPONumber());
    	
    	taxRate = invoice.getTaxPercentage();
    	
    	if(updateTaxRate(invoice)){
    		setEdited(true);
    		setPrimaryDataItem(invoice);
    		if(! loading)
    			editingOccured();
    	}
    	
    	setShopOptions(invoice);
    	discountPercentage = invoice.getDiscountPercentage();               
    	
    	loading = false;
    	
    	invoice.setDiscountPercentage(discountPercentage);
    	updatePaymentInfo(invoice);
    	setPrimaryDataItem(invoice);
    	
    	//      // paymentButton not relevent if invoice hasn't been saved
    	paymentButton.setEnabled((invoice.getInvoice() != 0));
    	
    	currentNotes=invoice.getComment();
    	notesButton.setToolTipText(currentNotes);
    	
    	String notesButtonLabel = "Add Notes";
    	if(currentNotes != null && currentNotes.length() <=0 ) currentNotes = null;
    	if(currentNotes != null){
    		notesButtonLabel = "Edit Notes";
    		notesButton.setForeground(rmk.gui.InvoiceDetailsScreen.DK_GREEN);
    	} else{
    		notesButton.setForeground(Color.BLACK);
    	}
    	notesButton.setText(notesButtonLabel);	
    }
    //--------------------------------------------------------
    public void updatePaymentInfo(Invoice invoice){
        if(invoice.getInvoice() == 0){ 
            // paymentButton not relevent if invoice hasn't been saved
            paymentButton.setEnabled(false);
//          } else{
//          paymentSummary.setInvoice(invoice);
        }
        paymentSummary.setInvoice(invoice);
    }
    //--------------------------------------------------------
//    public void setData(carpus.database.DBObject data){

//    }
    
    public void setShopOptions(Invoice invoice){
        String shipAddress = invoice.getShippingInfo();
        if(shipAddress == null) shipAddress = "";
        
        shopSale.setSelected(invoice.isShopSale());
        
        if(invoice.isShopSale()){
//          ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "shopSale");	    
            shopSale.setSelected(true);
            shipAddressPanel.setVisible(false);
        } else if(shipAddress.length() > 0){ // there is a shipping address
            ship.setSelected(true);
            shipAddressPanel.setVisible(true);
        } else{
            sameShippingAddress.setSelected(true);
            shipAddressPanel.setVisible(false);
        }
//        shipAddressPanel.setVisible(!sameShippingAddress.isSelected());
    }
        
    public double getTaxChange(){
        return taxChange;
    }    
}
class EstimatedDateKeyCheck extends KeyAdapter{
    String msg="";
    JComponent field;
    DataEntryPanel pnl;
    EstimatedDateKeyCheck(String message, DataEntryPanel panel,JComponent field){
        pnl = panel;
        this.field = field;
        msg = message;
    }
    public void keyPressed(KeyEvent e){
        int code = e.getKeyCode();	
//      if(code == KeyEvent.VK_UP){
//      pnl.notifyListeners("EST_UP_WEEK", pnl);
//      } else if(code == KeyEvent.VK_DOWN){
//      pnl.notifyListeners("EST_DOWN_WEEK", pnl);
//      }
        if(code == KeyEvent.VK_UP){
            ActionEvent evnt = new ActionEvent(this, 1, "EST_UP_WEEK");
//          pnl.notifyListeners(evnt);
            pnl.actionPerformed(evnt);
//          pnl.actionPerformed("EST_UP_WEEK", pnl);
        } else if(code == KeyEvent.VK_DOWN){
            ActionEvent evnt = new ActionEvent(this, 1, "EST_DOWN_WEEK");
            pnl.actionPerformed(evnt);
//          pnl.actionPerformed(evnt);
        }
    }
}
