package rmk.gui;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import rmk.database.dbobjects.Invoice;

public class InvoiceItemScreen extends Screen{
    rmk.gui.ScreenComponents.InvoiceItemPanel itemPanel;
    rmk.gui.ScreenComponents.KnifeFeaturesPanel featurePanel;
    DBGuiModel model;
    boolean loading = false;
    InvoiceEntries knife=null;
    Invoice invoice = null;
    Vector originalFeatures=null;
    rmk.DataModel sys = rmk.DataModel.getInstance();

    //==========================================================
    public InvoiceItemScreen(){
	super("Invoice Item");

    	getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
	itemPanel = new rmk.gui.ScreenComponents.InvoiceItemPanel();	
	featurePanel = new rmk.gui.ScreenComponents.KnifeFeaturesPanel();
	itemPanel.addActionListener(featurePanel);

	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
	this.registerKeyboardAction(this, "Cancel", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
      	
	((JButton)buttonBar.getButton(0)).setEnabled(false);
     
	itemPanel.addActionListener(this);
  	getContentPane().add(itemPanel);

	featurePanel.addActionListener(this);
	featurePanel.setPreferredSize(new Dimension(80,300));

	getContentPane().add(featurePanel);

	getContentPane().add(buttonBar);
  	setPreferredSize(new Dimension(550,650));
    }

    //==========================================================
    public boolean isEdited(){
        boolean itemEdited = itemPanel.isEdited();
        return itemEdited;
    }
    //==========================================================
    public void setData(DBGuiModel model){
        model.removeActionListener(this);

        if(loading) 
            return;
        loading = true;
        
        InvoiceEntries currKnife=null;
        this.model = model;
        Vector data = model.getKnifeData();
        if(data != null && data.size() > 0){
            currKnife = (InvoiceEntries)data.get(0);
        } else{
            currKnife = new InvoiceEntries(0);
        }
        
        data = model.getInvoiceData();
        
        originalFeatures = null;
        if(data != null){
            invoice = (Invoice)data.get(0);
            double percentage = -1;
            percentage = invoice.getDiscountPercentage();
            if(currKnife != null){
                Vector additions = currKnife.getFeatures();
                if(additions == null){
                    additions = sys.invoiceInfo.getInvoiceEntryAdditions(currKnife.getInvoiceEntryID());
                    model.setInvoiceItemAttributesData(additions);
                    currKnife.setFeatures(additions);
                }
                if(currKnife.getFeatures() != null)
                    originalFeatures = (Vector)currKnife.getFeatures().clone();
            }
        }
        double initialPrice=0;
        if(currKnife != null)
            initialPrice =currKnife.getPrice();
        itemPanel.clearData();

        data = model.getKnifeData();
        if(data != null && currKnife != null){
            data.remove(currKnife);
        	data.add(currKnife);
        }
    	model.setKnifeData(data);

        itemPanel.setData(model);
        
//      System.out.println(this.getClass().getName() + ":"+ "Change?" +
//      entry.getPrice() + " : " + initialPrice);
        
        int knifeID=0;
        if(currKnife != null)
            knifeID = (int)currKnife.getID().longValue();
        
        knife = currKnife;
//      boolean markEdited = (knifeID == 0 || entry.getPrice() != initialPrice);
//        InvoiceEntries entry = itemPanel.getData();
//        boolean markEdited = (currKnife.getPrice() != initialPrice);
//        itemPanel.setEdited(markEdited);
        buttonBar.enableButton(0,isEdited());
        
        loading = false;
        model.addActionListener(this);
        pack();
    }
    //==========================================================
    Vector getFeaturesChanged(Vector original, Vector current){
	Vector results = new Vector();
	if(original == null) // nothing to begin with
	    return current;
	for(int currentIndex=0; currentIndex < current.size(); currentIndex++){
  	    InvoiceEntryAdditions feature = (InvoiceEntryAdditions)current.get(currentIndex);
	    if(featureInArrayIsDifferent(feature, original)){
	        results.add(feature);
	    }
	}
	return results;
    }
    //----------------------------------------------------------
    Vector getFeaturesRemoved(Vector original, Vector current){
	if(original == null) return new Vector(); // nothing to start with, nothing could be removed
	if(current == null) return current; // nothing left, everything was removed

	Vector results = new Vector();
	for(int originalIndex=0; originalIndex < original.size(); originalIndex++){
  	    InvoiceEntryAdditions feature = (InvoiceEntryAdditions)original.get(originalIndex);
	    InvoiceEntryAdditions match = matchingFeature(feature, current);
	    if(match == null) // WAS in original, not in current
		results.add(feature);
	    else{
		if(match.getAdditionID() != feature.getAdditionID()){ // feature removed and added
		    results.add(feature);
//  		    System.out.println(this.getClass().getName() + ":"+ match + ":" + feature);
		}
		if(match.getEntryID() != feature.getEntryID()){
		    // Knife change, feature same
		    System.out.println(this.getClass().getName() + ":"+ "Knife change, feature same");
		    results.add(feature);
		}
	    }
	    
	}
	return results;
    }
    //----------------------------------------------------------
    InvoiceEntryAdditions matchingFeature(InvoiceEntryAdditions feature, Vector array){
	if(array == null) return null;
	for(int arrayIndex=0; arrayIndex < array.size(); arrayIndex++){
	    InvoiceEntryAdditions entry = (InvoiceEntryAdditions)array.get(arrayIndex);
	    if(entry.getPartID() == feature.getPartID() &&
	       entry.getAdditionID() == feature.getAdditionID())
		return entry;
	}
	return null;
    }
    //----------------------------------------------------------
    boolean featureInArrayIsDifferent(InvoiceEntryAdditions feature, Vector array){
	InvoiceEntryAdditions match = matchingFeature(feature, array);
	
	if(match == null) // same partid
	    return true;
	if(match.getPrice() != feature.getPrice()){
	    // Price change
	    return true;
	}

	if(feature.isEdited()) return true;

	return false;
    }

    //==========================================================
    public void internalFrameActivated(javax.swing.event.InternalFrameEvent e) {
//  	System.out.println(this.getClass().getName() + ":"+ "Window Activated.");
//  	("Internal frame activated", e);
    }


    //==========================================================
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase().trim();

        if (command.equals("CANCEL")) { //cancel
	    defaultCancelAction();
	} else if (command.equals("INVOICEFEATURECHANGE")) { //INVOICE FEATURE CHANGE
//  	    System.out.println(this.getClass().getName() + ":INVOICEFEATURECHANGE" );
	    buttonBar.enableButton(0,isEdited());
	} else if (command.equals("ADDFEATURE")) { //INVOICE FEATURE CHANGE
	    itemPanel.actionPerformed(e);
//  	    buttonBar.enableButton(0,isEdited());
  	} else if (command.startsWith("SET_KNIFE_MODEL")){
	    // ignore this message
  	} else if (command.equals("SAVE")){
	    saveData();
	} else{
	    System.out.println(this.getClass().getName() + ":" + command);
	}
    }
    //==========================================================
    public void saveData() {
        InvoiceEntries possibleNewKnife = itemPanel.getData();
        //  	System.out.println(this.getClass().getName() + ":"+ "Was ? : " +
        // knife);
        //  	System.out.println(this.getClass().getName() + ":"+ "Save ? : " +
        // possibleNewKnife);

        boolean newknife = false;
        boolean changedKnife=false;
        model.removeActionListener(this);

        InvoiceEntries currKnife = knife;
        long oldPartID = possibleNewKnife.getPartID();
        long newPartID = (currKnife != null?currKnife.getPartID():0);
        if (oldPartID != newPartID) { // NEW
            // knife?
            newknife = true;
        }
        if(possibleNewKnife.getInvoiceEntryID() > 0){
            changedKnife = true;
        }

        Vector invoiceEntries = model.getKnifeData();
        if(newknife)
            invoiceEntries = new Vector();
        
        if (newknife && changedKnife){
            System.out.println(this.getClass().getName() + ":Remove OLD knife and features.");
            long idOfOriginal = possibleNewKnife.getInvoiceEntryID();
            sys.invoiceInfo.removeInvoiceEntryAndAdditions(idOfOriginal);
            System.out.println(this.getClass().getName() + ":" + possibleNewKnife.getInvoice());
            
            Vector invData = model.getInvoiceItemsData();
            if(invData.contains(knife)){
                invData.remove(knife); // remove old knife from list
                model.setInvoiceItemsData(invData);
//                model.setInvoiceItemAttributesData(null);
            }
            knife = possibleNewKnife;
            knife.setInvoiceEntryID(0);
        }
        if(currKnife == null){
            currKnife = possibleNewKnife;
        }
        
        Vector currentFeatures = model.getInvoiceItemAttributesData();
        if (currentFeatures == null) {
            System.out.println(this.getClass().getName() + ":"
                    + "saveData:currentFeatures == null");
            currentFeatures = new Vector();
        }
        model.setInvoiceItemAttributesData(currentFeatures);

        //      	Vector invoiceEntries = model.getInvoiceItemsData();
        if (invoiceEntries == null) 
            invoiceEntries = new Vector();

        if (currKnife.getID().intValue() == 0) { // new knife
            invoiceEntries.remove(currKnife);
            invoiceEntries.add(currKnife);
        }
        invoiceEntries.remove(currKnife);
        invoiceEntries.remove(knife);
        invoiceEntries.add(currKnife);
        knife = currKnife;

        Vector savedInvoiceEntries;

        currKnife.setPartID(possibleNewKnife.getPartID());
        currKnife.setQuantity(possibleNewKnife.getQuantity());
        currKnife.setComment(possibleNewKnife.getComment());
        currKnife.setPrice(possibleNewKnife.getPrice());
        currKnife.setInvoice((long)possibleNewKnife.getInvoice());
        savedInvoiceEntries = Configuration.Config.getDB().saveItems(
                "InvoiceEntries", invoiceEntries);

        Vector removedFeatures = getFeaturesRemoved(originalFeatures,
                currentFeatures);
        if (removedFeatures.size() > 0) {
            Configuration.Config.getDB().removeItems("InvoiceEntryAdditions",
                    removedFeatures);
        }

        Vector changedFeatures = getFeaturesChanged(originalFeatures,
                currentFeatures);
        for (Enumeration enum = changedFeatures.elements(); enum
                .hasMoreElements();) {
            InvoiceEntryAdditions feature = (InvoiceEntryAdditions) enum
                    .nextElement();
            feature.setEntryID(knife.getInvoiceEntryID());
            System.out.print("setFeatureEID:" + knife.getInvoiceEntryID());
        }

        Configuration.Config.getDB().saveItems("InvoiceEntryAdditions",
                changedFeatures);

        setTitle("Invoice:" + (int) currKnife.getInvoice() + " Item:"
                + currKnife.getInvoiceEntryID());

        invoiceEntries = model.getInvoiceItemsData();
        if (!invoiceEntries.contains(currKnife)) // add knife to invoice entries if
                // not there
                invoiceEntries.add(currKnife);


        currKnife.setFeatures(currentFeatures);
        Configuration.Config.getDB().saveItems("InvoiceEntryAdditions",
                currentFeatures);
        //      	System.out.println(this.getClass().getName() +
        // ":model.setKnifeData:"+ savedInvoiceEntries);
        savedInvoiceEntries.remove(currKnife);
        savedInvoiceEntries.add(currKnife);
        model.setKnifeData(savedInvoiceEntries);

        Vector invItems = model.getInvoiceItemsData();
        invItems.remove(currKnife);
        invItems.add(currKnife);
        model.setInvoiceItemsData(invItems);
        
//        itemPanel.loading = true;
	    setData(model);
//        itemPanel.loading = false;
	    
        model.addActionListener(this);

        //  	System.out.println(this.getClass().getName() + ":knifeID:"+
        // knife.getID().longValue());
        itemPanel.setEdited(knife.getID().longValue() == 0);
        buttonBar.enableButton(0, isEdited());
        notifyListeners("ITEMSAVE");
        defaultCancelAction();
    }


    //==========================================================
    //==========================================================
    //==========================================================
    public static void main(String args[]) throws Exception{	
  	Application.main(args);
    }
    //==========================================================
    //==========================================================
    //==========================================================
}
