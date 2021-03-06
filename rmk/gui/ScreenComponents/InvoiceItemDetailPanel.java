package rmk.gui.ScreenComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.DBObject;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import rmk.database.dbobjects.Parts;

import java.text.NumberFormat;
import java.util.Vector;
import carpus.gui.*;
import rmk.gui.IScreen;

//===============================================================
//===============================================================
class InvoiceItemDetailPanel extends carpus.gui.DataEntryPanel implements
		ActionListener
//===============================================================
{
	InvoiceItemFeaturesPanel selectionPanel = new InvoiceItemFeaturesPanel();

	rmk.DataModel sys = rmk.DataModel.getInstance();

	InvoiceEntries knife = null;

	Invoice invoice = null;

	boolean loading = false;

	//      Customer customer=null;
	//      Vector listeners=null;
	double discountPercentage = 0;

	static final int FIELD_QUANTITY = 0;

	static final int FIELD_PRICE = 1;

	static final int FIELD_DISCOUNT = 2;

	static final int FIELD_MODEL = 3;

	LabeledTextField[] txtFields = new LabeledTextField[4];

	JTextPane comments = new JTextPane();
	int lastQty=0;
	
	rmk.database.PartPriceTable priceTable = rmk.DataModel.getInstance().pricetable;
	InvoiceItemFeatureEntryPanel entryPanel = new InvoiceItemFeatureEntryPanel();
	
	//-----------------------------------------------------------------
	public InvoiceItemDetailPanel() {
		//    	fieldPanel.setBackground(Color.BLUE);
		LabeledTextField field;
		int fieldIndex = 0;

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		//---------------------------------
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new FlowLayout());

		JTextField txtField = new JTextField("", 3);
		field = new LabeledTextField("Quantity ", txtField);
		txtFields[FIELD_QUANTITY] = field;
		txtField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				int qty = getQuantity();
				if(qty != lastQty)
				actionPerformed(new ActionEvent(txtFields[FIELD_QUANTITY], 1,
						"QUANTITY_CHANGED"));
			}
		});
		fieldPanel.add(field);

		//  	field = new LabeledTextField("Price
		// ",carpus.gui.FormattedTextFields.getCurrencyField(0));
		field = new LabeledTextField("Price $", new JTextField("", 8));
		txtFields[FIELD_PRICE] = field;
		fieldPanel.add(field);
		JFormattedTextField discountDisp = carpus.gui.FormattedTextFields
				.getPercentageField(0);
		discountDisp.setEditable(false);
		field = new LabeledTextField("Discount ", discountDisp);
		txtFields[FIELD_DISCOUNT] = field;
		fieldPanel.add(field);

		JTextField modelField = new JTextField("", 8);
		field = new LabeledTextField("Model ", modelField);
		modelField.setEditable(false);
		txtFields[FIELD_MODEL] = field;
		fieldPanel.add(field);
		fieldPanel.setMinimumSize(new Dimension(220, 75));
		fieldPanel.setPreferredSize(new Dimension(220, 80));

		mainPanel.add(fieldPanel);
		//---------------------------------
		JPanel commentsPanel = new JPanel();
		commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
		comments.setPreferredSize(new Dimension(175, 70));

		JScrollPane commentPane = new JScrollPane(comments);
		setFieldEditCheck(comments, "InvoiceItemDetailsChange", this);
		commentsPanel.add(new JLabel("Comments:"));
		commentsPanel.add(commentPane);
		mainPanel.add(commentsPanel);
		//---------------------------------

		JPanel featurePanel = new JPanel();
		featurePanel.add(selectionPanel);		
		featurePanel.add(entryPanel);
		// TODO: Need to go through parent screen with messages
//		entryPanel.setParent(this);

		mainPanel.add(featurePanel);
		//---------------------------------

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(mainPanel);
		selectionPanel.setParentPanel(this);
		add(scrollPane);

		setFieldEditCheck(txtFields, "InvoiceItemDetailsChange", this);
		setPreferredSize(new Dimension(250, 300));
	}
    public void moveBackToFeatureEntry(){
    	entryPanel.grabFocus();
    }
    
	public void setParentScreen(IScreen screen){
		entryPanel.setParentScreen(screen);
		selectionPanel.setParent(screen);
		parentScreen=screen;
	}

	//-----------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand().toUpperCase().trim();
        ErrorLogger.getInstance().logDebugCommand(command);

        if (command.equals("QUANTITY_CHANGED")){
        	featureChange();
        	parentScreen.updateOccured(knife,ScreenController.UPDATE_EDIT, null);
        	return;
        }
        		
		if (command.equals("INVOICEFEATUREREMOVED")) { //removed feature
			featureChange();
		} else if (command.equals("INVOICEFEATUREADDED")) { // added feature
			featureChange();
		} else if (command.equals("QUANTITY_CHANGED")) { // QUANTITY_CHANGED
			featureChange();
		} else if (command.startsWith("ENTEREDNEWENTRY")) { // added feature
															// manual enter
			String enteredFeature = command.substring("ENTEREDNEWENTRY"
					.length() + 1);

			Parts part = rmk.DataModel.getInstance().partInfo.getPartFromCode(enteredFeature);
			long partID = (part != null? part.getPartID():0);
			if (partID <= 0) {
				JOptionPane.showMessageDialog(null, "Invalid Part Code\n"
						+ enteredFeature, "Invalid Entry",
						JOptionPane.WARNING_MESSAGE);
				return;		
			} else if(part.isBladeItem()){
				JOptionPane.showMessageDialog(null, "Invalid Part Code\nModel #, not feature\n"
						+ enteredFeature, "Invalid Entry",
						JOptionPane.WARNING_MESSAGE);
				return;		
			} else {
				InvoiceEntryAdditions feature = new InvoiceEntryAdditions(0);
				feature.setPartID(partID);

				int year = sys.invoiceInfo.getPricingYear(invoice);			
				double price = rmk.DataModel.getInstance().pricetable
						.getPartPrice(year, (int) partID);

				feature.setPrice(price);
				enteredFeature = e.getActionCommand().trim().substring(
						"ENTEREDNEWENTRY".length() + 1);
				if (!enteredFeature.toUpperCase().equals(enteredFeature))
					feature.setPrice(0);
				if (knife != null) {
					feature.setEntryID(knife.getInvoiceEntryID());
					//  		    ErrorLogger.getInstance().logMessage(this.getClass().getName() +
					// ":feature:"+ feature);
					Vector features = knife.getFeatures();
					if (features == null) {
						features = new Vector();
						knife.setFeatures(features);
					}
					features.addElement(feature);
					addFeature(feature);
				} else {
					JOptionPane.showMessageDialog(null, "Select model first",
							"Invalid Entry", JOptionPane.WARNING_MESSAGE);
				}
			}
			featureChange();
		} else if (command.equals("INVOICEFEATUREEDITED")) { // price edited
			featureChange();
		} else {
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:"
					+ command + "|" + e);
		}
		ErrorLogger.getInstance().TODO();
	}

	//-----------------------------------------------------------------
	public boolean featureChange() {
		if (loading)
			return false;

		if (knife == null)
			return false;

		long partID = knife.getPartID();
		txtFields[FIELD_MODEL].setValue(sys.partInfo.getPartCodeFromID(partID));

		double oldPrice = knife.getPrice();
		int year = sys.invoiceInfo.getPricingYear(invoice);			
		double price = priceTable.getPartPrice(year, (int) partID);
		lastQty	= getQuantity();

		price += selectionPanel.getFeaturesTotalCosts(0); // want to display
														  // retail discount=0%
		double oldTotal = oldPrice * lastQty;
		double total = price * lastQty;
		if (oldTotal > 0 && oldTotal != total
				&& !rmk.gui.Dialogs.yesConfirm("Update Price?"))
			return false;
		//  	price += selectionPanel.getFeaturesTotalCosts(discountPercentage);
		txtFields[FIELD_PRICE].setValue("" + total);
		return true;
	}

	//-----------------------------------------------------------------
	public boolean addFeature(InvoiceEntryAdditions newFeature) {
		if(selectionPanel.addFeature(newFeature)){
			featureChange();
			return true;
		} else{
//			featureChange();
			return false;
		}
	}

	//-----------------------------------------------------------------
	public int getFeatureCount() {
		return selectionPanel.getFeatureCount();
	}

	public int getQuantity() {
		int quantity = 0;
		try {
			quantity = Integer.parseInt(txtFields[FIELD_QUANTITY].getValue());
		} catch (Exception e) {
		}

		if ((knife != null && knife.getID().intValue() == 0) && quantity == 0) {
			quantity = 1;
			txtFields[FIELD_QUANTITY].setValue("" + quantity);
		}
		return quantity;
	}

	//-----------------------------------------------------------------
	public InvoiceEntries getData() {
		knife.setComment(comments.getText());
		String price = txtFields[FIELD_PRICE].getValue();
		price = price.replaceAll("$",""); // remove $
		price = price.replaceAll(",",""); // remove commas
		knife.setPrice(Double.parseDouble(price));
		String qty = txtFields[FIELD_QUANTITY].getValue();
		lastQty = Integer.parseInt(qty);
		knife.setQuantity(lastQty);
		
		return knife;
	}

	public void setData(DBObject item){
		loading = true;
		InvoiceEntries knifeItem = (InvoiceEntries) item;
//		ErrorLogger.getInstance().TODO();
		invoice = knifeItem.getParent();
		Vector features = knifeItem.getFeatures();
		selectionPanel.setData(features);
		
		updateFields(knifeItem);
		
		featureChange();

		knife = knifeItem;
		loading = false;
		setEdited(false);
	}
	
	void updateFields(InvoiceEntries knifeItem){
		lastQty = knifeItem.getQuantity();		
		txtFields[FIELD_QUANTITY].setValue("" + lastQty);
		
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(2);
		formatter.setMinimumFractionDigits(2);
		txtFields[FIELD_PRICE].setValue(""
				+ formatter.format(knifeItem.getPrice()));
		txtFields[FIELD_DISCOUNT].setValue(discountPercentage);
		comments.setText(knifeItem.getComment());
		//  	txtFields[FIELD_COMMENT].setValue(knife.getComment());
		long partID = knifeItem.getPartID();
		txtFields[FIELD_MODEL].setValue(sys.partInfo.getPartCodeFromID(partID));		
	}
	
	//-----------------------------------------------------------------
//	public void setData(DBGuiModel model) {		
	public void setData(InvoiceEntries knife, Vector knifeFeatures) {		
		InvoiceEntries currKnife = knife;
		loading = true;
		
		if (currKnife == null)
			currKnife = new InvoiceEntries(0);
		
		invoice = knife.getParent();
		if(invoice == null) {
			ErrorLogger.getInstance().logError("Invoice == null", new Exception());
			return;
		}

		Vector features = null;
		if (currKnife.getFeatures() != null) {
			features = (Vector) currKnife.getFeatures();
		}
//		ErrorLogger.getInstance().logMessage("Updating ItemDetailFeatures:" + features);
//		model.setInvoiceItemAttributesData(features);
		selectionPanel.setData(knifeFeatures);

		lastQty = currKnife.getQuantity();		
		txtFields[FIELD_QUANTITY].setValue("" + lastQty);
		
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(2);
		formatter.setMinimumFractionDigits(2);
		txtFields[FIELD_PRICE].setValue(""
				+ formatter.format(currKnife.getPrice()));
		txtFields[FIELD_DISCOUNT].setValue(discountPercentage);
		comments.setText(currKnife.getComment());
		//  	txtFields[FIELD_COMMENT].setValue(knife.getComment());
		long partID = currKnife.getPartID();
		txtFields[FIELD_MODEL].setValue(sys.partInfo.getPartCodeFromID(partID));

		featureChange();

		knife = currKnife;
		loading = false;
		setEdited(false);
	}

	//-----------------------------------------------------------------
	public void clearData() {
		txtFields[FIELD_QUANTITY].setValue("1");
		txtFields[FIELD_PRICE].setValue("0.00");
		txtFields[FIELD_DISCOUNT].setValue("" + discountPercentage);
		comments.setText("");
		selectionPanel.blankOutFeatures();
//		selectionPanel.clearFeatures();
	}
	public void setPricingYear(int year){
		entryPanel.setPricingYear(year);
	}
}