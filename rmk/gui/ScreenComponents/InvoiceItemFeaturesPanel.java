package rmk.gui.ScreenComponents;

import javax.swing.*;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import rmk.database.dbobjects.Parts;

import java.util.Vector;
import java.util.Enumeration;
import rmk.gui.DBGuiModel;
import rmk.gui.Dialogs;
import rmk.gui.IScreen;

import java.awt.Dimension;
//import java.awt.event.*;

public class InvoiceItemFeaturesPanel extends JPanel{
	
	DefaultListModel selectedItems = new DefaultListModel();
	rmk.DataModel sys = rmk.DataModel.getInstance();
	//    DBGuiModel model;
	Vector currentFeatures;
	Vector listeners;
	IScreen parent=null;
	carpus.gui.DataEntryPanel parentPanel=null;
	boolean loading=false;
	
	public InvoiceItemFeaturesPanel(){
		JList selections = new JList(selectedItems);
		selections.setVisibleRowCount(6);
		SingleSelectionModel selectionModel = new SingleSelectionModel(0) {
			public void updateSingleSelection(int oldIndex, int newIndex) {
				Object[] options = {"Remove",
						"Edit Price",
				"Cancel"};
				InvoiceEntryAdditions feature = ((ListObject)selectedItems.get(newIndex)).getAddition();
				String question = "Remove Feature or Edit Price:  ";
				question += sys.partInfo.getPartCodeFromID(feature.getPartID());
				question += " ?";
				
				int dialogSelection = JOptionPane.showOptionDialog(rmk.gui.Desktop.getInstance().getFrame(),
						question,
						"Options",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,     //don't use a custom Icon
						options,  //the titles of buttons
						options[2]); //default button title
				switch(dialogSelection){
				case 0:
					removeFeature(newIndex);
					break;
				case 1:
					editFeaturePrice(newIndex);
					break;
					//			parent.moveBackToFeatureEntry();
				default:
					// do nothing
				}
				this.clearSelection();
			}
		};
		selections.setSelectionModel(selectionModel);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
		mainPanel.add(new JLabel("Features"));
		mainPanel.add(selections);
		
		
		JScrollPane  scrollPane = new JScrollPane(mainPanel);
		add(scrollPane);
		
		scrollPane.setPreferredSize(new Dimension(180,150));
	}
	
	public void setParent(IScreen screen){
		parent = screen;
	}
	
	//-----------------------------------------------------------------
	public void editFeaturePrice(int index){
		InvoiceEntryAdditions feature = ((ListObject)selectedItems.get(index)).getAddition();
		
		String newPrice=JOptionPane.showInputDialog(rmk.gui.Desktop.getInstance().getFrame(),
				"New Price?",
				""+feature.getPrice()
		);
		if(newPrice == null) return;
		feature.setPrice(Double.parseDouble(newPrice));	
		parent.updateOccured(feature, ScreenController.UPDATE_EDIT, null);
	}
	//-----------------------------------------------------------------
	public boolean addFeature(InvoiceEntryAdditions newFeature){
		// make sure it's not already in list
		if(skipDuplicateEntry(newFeature))
			return false;
		
		// not in displayList... Add it
		Parts part = sys.partInfo.getPart(newFeature.getPartID());
		if(part.getPartType() == 10){
            JOptionPane.showMessageDialog(null, "Code " + part.getPartCode() + " is a model Number.", "Invalid feature:",
                    JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(newFeature.getAdditionID() == 0 && part.askPrice()){
			double price = Dialogs.askPriceOfPart(newFeature.getPrice(), part.getPartCode(), null);
			if(price > 0 )
				newFeature.setPrice(price);
		}
		
		selectedItems.addElement(new ListObject(newFeature, selectedItems.size()+1));
		if(newFeature.getID().intValue() == 0){  // it's not in database == initial load
			sortFeatureList();
		}
		
		for(Enumeration enum=currentFeatures.elements(); enum.hasMoreElements();){
			InvoiceEntryAdditions addition = (InvoiceEntryAdditions)enum.nextElement();
			if(addition.getPartID()  == newFeature.getPartID()){ // already in list...
				if(addition == newFeature)
					return true;
				else
					return false;
			}
		}

		currentFeatures.add(newFeature); // add it to the vector of knife's feature
		
		if(!loading){
			parent.updateOccured(newFeature, ScreenController.UPDATE_ADD, null);
		}
		return true;
	}
	//-----------------------------------------------------------------
	void sortFeatureList(){
		Vector items = new Vector();
		// copy all elements to vector - items
		for(int type=0; type < 100; type++){
			for(Enumeration elements = selectedItems.elements(); elements.hasMoreElements(); ){
				ListObject item =  ((ListObject)elements.nextElement());
				InvoiceEntryAdditions addition =  item.getAddition();
				if(sys.partInfo.getPartTypeFromID((int)addition.getPartID()) == type)
					items.add(item);
			}
		}
		selectedItems.clear();
		for(Enumeration elements = items.elements(); elements.hasMoreElements(); ){
			selectedItems.addElement(elements.nextElement());
		}
	}
	//-----------------------------------------------------------------
	
	public void removeFeature(int index){
		int partID = (int)((ListObject)selectedItems.get(index)).getID();
		
		//	Vector features = model.getInvoiceItemAttributesData();
		InvoiceEntryAdditions addition=null;
		for(int featureIndex=0;featureIndex < currentFeatures.size(); featureIndex++){
			addition = (InvoiceEntryAdditions)currentFeatures.get(featureIndex);
			if(addition.getPartID()  == partID){ // remove from list...
				currentFeatures.remove(featureIndex);
				//  	    	model.setInvoiceItemAttributesData(features);
				break;
			}
		}
		selectedItems.removeElementAt(index);
		if(addition != null){
			parent.updateOccured(addition, ScreenController.UPDATE_REMOVE, null);
			//  		notifyListeners("INVOICEFEATUREREMOVED");
		}
	}
	//-----------------------------------------------------------------
	public int getFeatureCount(){
		return selectedItems.size();
	}	
	
	//	public void setData(DBObject item){
	public void setData(Vector features){
		//		InvoiceEntries knife = (InvoiceEntries) item;
		//		Vector features=knife.getFeatures();
		if(features == null) return;
		loading=true;
		currentFeatures = features;
		blankOutFeatures();
		for(Enumeration lst=features.elements(); lst.hasMoreElements();){
			InvoiceEntryAdditions feature = (InvoiceEntryAdditions)lst.nextElement();
			if(feature.getPartID() > 0){
				//				feature.setEntryID(knife.getInvoiceEntryID());
				addFeature(feature);
			}else{
				ErrorLogger.getInstance().logMessage("Feature missing partID?:" + feature);
			}
		}
		sortFeatureList();
		loading=false;
		//		ErrorLogger.getInstance().TODO();
	}
	
	void blankOutFeatures(){
		selectedItems.clear();
	}
	
//	public void clearFeatures(){
//		if(currentFeatures == null)
//			currentFeatures=new Vector();
//		currentFeatures.clear(); 
//		selectedItems.clear();
//	}
	
	//-----------------------------------------------------------------
	public void setData(DBGuiModel model){
		//	this.model=model;
		//  	InvoiceEntries knife= (InvoiceEntries )model.getKnifeData().get(0);
		Vector lst = model.getInvoiceItemAttributesData();
		if(lst == null) return;
		
		selectedItems.clear();
		
		InvoiceEntries knife= (InvoiceEntries )model.getKnifeData().get(0);
		for(int featureIndex=0; featureIndex < lst.size(); featureIndex++){
			InvoiceEntryAdditions feature = (InvoiceEntryAdditions)lst.get(featureIndex);
			if(feature.getPartID() > 0){
				feature.setEntryID(knife.getInvoiceEntryID());
				addFeature(feature);
			}else{
				ErrorLogger.getInstance().logMessage("Feature missing partID?:" + feature);
				//  	    InvoiceEntryAdditions addition = (InvoiceEntryAdditions)lst.get(featureIndex);
				//  	    selectedItems.addElement(new ListObject(addition, selectedItems.size()+1));
			}
		}
		sortFeatureList();
	}
	
	//-----------------------------------------------------------------
	boolean skipDuplicateEntry(InvoiceEntryAdditions newFeature){
		// make sure it's not already in list
		boolean duplicateEntry=false;
		for(Enumeration enum=selectedItems.elements(); enum.hasMoreElements();){
			InvoiceEntryAdditions feature = ((ListObject)enum.nextElement()).getAddition();
			if(feature.getPartID() == newFeature.getPartID()){ 
				// already in list...
				if(newFeature.getID().intValue() == 0){ 
					// and it's not in database (allows for original loading of DB entry)
					duplicateEntry = true;
				}
			}
		}
		if(duplicateEntry){
			String question="Feature is already added, Duplicate?";
			int dialogSelection = JOptionPane.showOptionDialog(rmk.gui.Desktop.getInstance().getFrame(),
					question,
					"Options",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,     //don't use a custom Icon
					null,  //the titles of buttons
					null); //default button title
			
			if(dialogSelection == 1)
				return true;
			else
				return false;
		}
		return false;
	}
	
	public void setParentPanel(carpus.gui.DataEntryPanel parentPnl){
		parentPanel = parentPnl;
	}
	
	//-----------------------------------------------------------------
	//    public void notifyListeners(String msg){
	//	notifyListeners(new ActionEvent(this,1,msg));
	//    }
	//    public void notifyListeners(ActionEvent event){
	//	if(listeners == null) return;
	//	for(Enumeration enum=listeners.elements(); enum.hasMoreElements();){
	//	    ((ActionListener)enum.nextElement()).actionPerformed(event);
	//	}
	//    }
	//    public void addActionListener(ActionListener listener){
	//	if(listeners == null) listeners = new Vector();
	//	if(!listeners.contains(listener)) listeners.addElement(listener);
	//    }
	//-----------------------------------------------------------------
	public double getFeaturesTotalCosts(double discount){
		double results=0;
		
		for(Enumeration parts=selectedItems.elements(); parts.hasMoreElements();){
			InvoiceEntryAdditions feature = ((ListObject)parts.nextElement()).getAddition();	    
			double price = feature.getPrice();
			
			if(discount != 0 && sys.partInfo.partIsDiscountable(feature.getPartID()))
				price *= (1.0-discount);
			results += price;
		}
		return results;
	}
	
	//-----------------------------------------------------------------
	//    public void clear(){
	//	selectedItems.clear();
	//    }
}
