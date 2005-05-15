package rmk.gui;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.SignalProcessor;
import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.DBObject;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import rmk.database.dbobjects.Invoice;

public class InvoiceItemScreen extends Screen{
	rmk.gui.ScreenComponents.InvoiceItemPanel itemPanel;
	rmk.gui.ScreenComponents.KnifeFeaturesPanel featurePanel;

	boolean loading = false;
	InvoiceEntries currentKnife=null;
	Invoice invoice = null;
	Vector originalFeatures=null;
	
	//==========================================================
	public InvoiceItemScreen(){
		super("Invoice Item");
		
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		itemPanel = new rmk.gui.ScreenComponents.InvoiceItemPanel();	
		featurePanel = new rmk.gui.ScreenComponents.KnifeFeaturesPanel();
		
		((JButton)buttonBar.getButton(0)).setEnabled(false);
		
		itemPanel.setParentScreen(this);
		getContentPane().add(itemPanel);
		
		featurePanel.setParent(this);
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
	public void setData(DBObject item){
		if(loading) 
			return;
		loading = true;
		if(item == null){ ErrorLogger.getInstance().TODO(); }
		
		currentKnife = (InvoiceEntries)item;
		Vector additions = currentKnife.getFeatures();
		if(additions == null){
			additions = sys.invoiceInfo.getInvoiceEntryAdditions(currentKnife.getInvoiceEntryID());
			currentKnife.setFeatures(additions);
		}
	
		if(additions != null)
			originalFeatures = (Vector) additions.clone();
		else
			originalFeatures = null;
		
		invoice = currentKnife.getParent();
		Customer customer = invoice.getParent();
		
		itemPanel.setData(item);
		loading = false;
		pack();
	}


	
	//==========================================================
	Vector getFeaturesChanged(Vector original, Vector current){
		Vector results = new Vector();
		if(original == null && current == null) return null;		
		
		if(original == null) // nothing to begin with
			return current;
		if(current == null) // nothing now
			return original;
		
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
		if(original == null || original.size()==0) return null; // nothing to start with, nothing could be removed
		if(current == null || current.size()==0) return original; // nothing left, everything was removed
		if(original == current) return null; // same exact vector
		
		Vector results = new Vector();
		for(int originalIndex=0; originalIndex < original.size(); originalIndex++){
			InvoiceEntryAdditions feature = (InvoiceEntryAdditions)original.get(originalIndex);
			InvoiceEntryAdditions match = matchingFeature(feature, current);
			if(match == null) // WAS in original, not in current
				results.add(feature);
			else{
				if(match.getAdditionID() != feature.getAdditionID()){ // feature removed and added
					results.add(feature);
					//  		    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ match + ":" + feature);
				}
				if(match.getEntryID() != feature.getEntryID()){
					// Knife change, feature same
					ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Knife change, feature same");
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
		
		if(match.getPrice() != feature.getPrice()){ // Price change
			return true;
		}
		
//		if(feature.isEdited()) return true;
		
		return false;
	}
	
	//==========================================================
	public void internalFrameActivated(javax.swing.event.InternalFrameEvent e) {
		//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Window Activated.");
		//  	("Internal frame activated", e);
	}
	
	
	//==========================================================
	public void actionPerformed(ActionEvent e) {
		if(!processHotKeys(e)){
			ErrorLogger.getInstance().TODO();
		}
		
	}
	//==========================================================
	
	public void processCommand(String command, Object from){
		ErrorLogger.getInstance().logDebugCommand(command);
		
		if (command.equals("CANCEL")) { //cancel
			defaultCancelAction();
		} else if (command.equals("INVOICEFEATURECHANGE")) { //INVOICE FEATURE CHANGE
			//  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":INVOICEFEATURECHANGE" );
			buttonBar.enableButton(0,isEdited());
		} else if (command.equals("SAVE")){
			saveData();
		} else{
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command);
		}
	}
	//==========================================================
	public void saveData() {
		InvoiceEntries possibleNewKnife = itemPanel.getData();
		InvoiceEntries currKnife = currentKnife;

		long oldPartID = possibleNewKnife.getPartID();
		long newPartID = (currKnife != null?currKnife.getPartID():0);
		if (oldPartID != newPartID) { // save knife
			Vector invoiceEntries = new Vector();
			invoiceEntries.add(possibleNewKnife);
			Configuration.Config.getDB().saveItems( "InvoiceEntries", invoiceEntries);
			currentKnife.getParent().getItems().remove(currentKnife);
			currentKnife.getParent().getItems().add(possibleNewKnife);
		}
		possibleNewKnife.setParent(currentKnife.getParent());
		
		Vector changedFeatures = getFeaturesChanged(currentKnife.getFeatures(), originalFeatures);
		Vector removedFeatures = getFeaturesRemoved(originalFeatures, currentKnife.getFeatures());
		
		if(removedFeatures != null){ //remove features
			for(Enumeration items = removedFeatures.elements(); items.hasMoreElements();){
				InvoiceEntryAdditions feature = (InvoiceEntryAdditions)items.nextElement();
				sys.invoiceInfo.removeAdditionID(feature.getAdditionID());
			}			
		}
		
		itemPanel.setEdited(false);
		Configuration.Config.getDB().saveItems( "InvoiceEntryAdditions", possibleNewKnife.getFeatures());
		SignalProcessor.getInstance().notifyUpdate(this,possibleNewKnife,ScreenController.UPDATE_EDIT,possibleNewKnife.getParent());
		defaultCancelAction();
	}
	
	boolean differentFeatures(Vector features1, Vector features2){
		boolean results=false;
		if(features1 == null && features2 == null) return false;		
		if(features1 == null || features2 == null) return true;
		
		// TODO: loop through to find differences
		return results;
	}
	
	void updateTitle(String newTitle){
		String oldTitle=this.getTitle();
		if(!oldTitle.equalsIgnoreCase(newTitle)){
			setTitle(newTitle);
			ApplicationMenu.getInstance().updateScreenTitle(oldTitle, newTitle);
		}
	}
	public InvoiceEntries getItem(){
		return currentKnife;
	}
	
	public void updateOccured(DBObject itemChanged, int changeType, DBObject parentItem){
		String parentName="";
		if(parentItem != null) parentName = parentItem.getClass().getName();
		String itemName="";
		if(itemChanged != null) itemName = itemChanged.getClass().getName();
		
		ErrorLogger.getInstance().logUpdate(itemChanged,changeType,parentItem);

		
		switch(changeType){
		case ScreenController.UPDATE_CANCELED:
		{
			defaultCancelAction();
		}
		break;
		case ScreenController.LIST_ITEM_SELECTED: // SET_KNIFE_MODEL
		{
			if(itemName.indexOf("InvoiceEntries") > 0){
				InvoiceEntries entry = (InvoiceEntries)itemChanged;
				int year = sys.invoiceInfo.getPricingYear(invoice);
				featurePanel.setKnifeModel((int) entry.getPartID(),year);
				itemPanel.selectListItem(entry.getPartID());
			} else if(itemName.indexOf("InvoiceEntryAdditions") > 0){
				InvoiceEntryAdditions feature = (InvoiceEntryAdditions)itemChanged;
				feature.setEntryID(currentKnife.getInvoiceEntryID());
				currentKnife.addFeature(feature);
				if(itemPanel.addFeature(feature)){
					((JButton)buttonBar.getButton(0)).setEnabled(true);
				} else{
					ErrorLogger.getInstance().TODO();
				}
			}else{
				ErrorLogger.getInstance().TODO();
			}
		}
		break;
		case ScreenController.UPDATE_ADD:
		{
			if(itemName.indexOf("InvoiceEntryAdditions") > 0){
				InvoiceEntryAdditions feature = (InvoiceEntryAdditions)itemChanged;
				feature.setEntryID(currentKnife.getInvoiceEntryID());
				
				currentKnife.addFeature(feature);
				if(itemPanel.addFeature(feature)){
					// price update query
					itemPanel.moveBackToFeatureEntry();
				}
				((JButton)buttonBar.getButton(0)).setEnabled(true);
			} else if(itemName.indexOf("InvoiceEntries") > 0){
				ErrorLogger.getInstance().logDebug("Entry?:" + itemChanged, false);
			}else{
				ErrorLogger.getInstance().TODO();
			}
		}
		break;
		case ScreenController.UPDATE_REMOVE:
		{
			InvoiceEntryAdditions feature = (InvoiceEntryAdditions)itemChanged;	
//			feature.getParent().getFeatures().remove(feature);
//			currentKnife.getFeatures().remove(feature);
			if(itemPanel.featureChange()){
				((JButton)buttonBar.getButton(0)).setEnabled(true);
				itemPanel.moveBackToFeatureEntry();
			}else{
				ErrorLogger.getInstance().TODO();
			}
		}
		
		break;
		case ScreenController.UPDATE_EDIT:
		{
			if(itemName.indexOf("InvoiceEntryAdditions") > 0){
				if(itemPanel.featureChange()){
					((JButton)buttonBar.getButton(0)).setEnabled(true);
				}else{
					ErrorLogger.getInstance().TODO();
				}				
			}
			//			InvoiceEntryAdditions feature = (InvoiceEntryAdditions)itemChanged;
			((JButton)buttonBar.getButton(0)).setEnabled(true);
		}
		
		break;
		default:
			ErrorLogger.getInstance().TODO();
		System.out.println("Screen buttonPress processing unimplemented");
		}
	}
	
	public void buttonPress(int button, int id) {

		ErrorLogger.getInstance().logButton(button, id);
		
		switch(button){
		case ScreenController.BUTTON_CANCEL:
		{			
			defaultCancelAction();
		}
		break;
		case ScreenController.BUTTON_SAVE:
			saveData();
		break;
		default:
	       	ErrorLogger.getInstance().logButton(button, id);
		}
	}
	
	
}
