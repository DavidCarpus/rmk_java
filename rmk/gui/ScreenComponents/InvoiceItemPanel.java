package rmk.gui.ScreenComponents;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.DBObject;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import rmk.database.dbobjects.Parts;
import rmk.database.dbobjects.Customer;
import java.util.Vector;
import java.util.Enumeration;
//import rmk.gui.DBGuiModel;
import rmk.gui.IScreen;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

//===============================================================
//===============================================================
public class InvoiceItemPanel extends carpus.gui.DataEntryPanel 
implements ActionListener,  ListSelectionListener
//===============================================================
{
    rmk.database.PartPriceTable priceTable = null;
    DefaultListModel listData = new DefaultListModel();
    JList list = new JList(listData);
    int lastIndex=-1;
    boolean loading=false;
    
    static InvoiceItemDetailPanel detailPanel = new InvoiceItemDetailPanel();
    static rmk.DataModel sys = rmk.DataModel.getInstance();
    InvoiceEntries originalKnife=null;
    InvoiceEntries currentKnife=null;
    Invoice invoice;
    Customer customer;
    
//  -----------------------------------------------------------------
    public InvoiceItemPanel(){
        priceTable = sys.pricetable;
        list.setVisibleRowCount(6);
        list.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        listData.clear();
        
        SingleSelectionModel selectionModel = new KnifeSelectionModel(0);
        selectionModel.addListSelectionListener(this);
        list.setSelectionModel(selectionModel);
        
        //---------------------------------------------------
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        
        JPanel listPanel = new JPanel();
        JScrollPane  scrollPane = new JScrollPane(list);
        listPanel.setLayout(new BoxLayout(listPanel,BoxLayout.Y_AXIS));
        listPanel.add(new JLabel("Knife"));
        listPanel.add(scrollPane);
        listPanel.setPreferredSize(new Dimension(100,650));
        //---------------------------------------------------

        add(listPanel);
        add(detailPanel);
        
        setPreferredSize(new Dimension(530,470));
    }
    //---------------------------------------------------
    
    public void moveBackToFeatureEntry(){
    	detailPanel.moveBackToFeatureEntry();
    }
     public void setParentScreen(IScreen screen){
    	parentScreen = screen;
    	detailPanel.setParentScreen(parentScreen);
    }
     
     public boolean addFeature(InvoiceEntryAdditions feature){
     	setEdited(true);
        return detailPanel.addFeature(feature);
     }
     public boolean featureChange(){
     	boolean changed = detailPanel.featureChange();
     	setEdited(changed);
     	return changed;
     }
     
//  -----------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand().toUpperCase().trim();
        ErrorLogger.getInstance().logDebugCommand(command);

        if (command.equals("INVOICEITEMDETAILSCHANGE")) { //INVOICE FEATURE CHANGE
            setEdited(true);
            if(!loading)
            	parentScreen.updateOccured(originalKnife,ScreenController.UPDATE_EDIT, null);
            else
            	parentScreen.updateOccured(originalKnife,ScreenController.UPDATE_UNKNOWN, null);
            return;
        } else if (command.startsWith("ADDFEATURE")){
        	addFeature((InvoiceEntryAdditions)e.getSource());
        	return;
        } else if (command.equals("INVOICEFEATURECHANGE")) { //INVOICE FEATURE CHANGE
            setEdited(true);
            return;
        } else if (command.equals("ENTERKEY")) { // ENTER KEY
            ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command);
            // TODO: ignore for now?
        }
        
        ErrorLogger.getInstance().TODO();
    }
//  -----------------------------------------------------------------
    public void valueChanged(ListSelectionEvent e) {
        int newIndex = list.getSelectedIndex();
        
        if(e.getValueIsAdjusting()){
            return;
        }

         if(lastIndex == -1){
            lastIndex = newIndex;
        }
        
        if(lastIndex == newIndex && currentKnife != null)
            return; // nothing changed, || new
        
        ListObject item = (ListObject)list.getSelectedValue();
        currentKnife = getKnife(item);

        // if changing an existing knife
        double price=0;
        if(originalKnife != null){ 
            // get old features, and change the entryID on them
            // AND total price up
            Vector existingFeatures = originalKnife.getFeatures();
            if(existingFeatures != null && existingFeatures.size() > 0 && !loading){
                if(rmk.gui.Dialogs.yesConfirm("Clear Features")){
                	currentKnife.setFeatures(new Vector());
                	currentKnife.setParent(originalKnife.getParent());
                	parentScreen.updateOccured(currentKnife,ScreenController.CLEAR_FEATURES, currentKnife);
                } else{
                	int year = sys.invoiceInfo.getPricingYear(invoice);
//                	int year = DataModel.getCurrentYear();
                    price = priceTable.getPartPrice(year, (int)currentKnife.getPartID());

                    currentKnife.setFeatures(new Vector());
                    for(Enumeration enum=existingFeatures.elements(); enum.hasMoreElements();){
                        InvoiceEntryAdditions feature = (InvoiceEntryAdditions)enum.nextElement();
                        InvoiceEntryAdditions featureCopy = new InvoiceEntryAdditions(feature);
                        
                        currentKnife.addFeature(featureCopy);
                        price += featureCopy.getPrice();
                    }
                    currentKnife.setPrice(price);
                }
            } else{ // loading or no existing features
            	int year = sys.invoiceInfo.getPricingYear(invoice);
//            	int year = DataModel.getCurrentYear();
                price = priceTable.getPartPrice(year, (int)currentKnife.getPartID());
                int qty = detailPanel.getQuantity();
                price *= qty;
                originalKnife.setPrice(price);
            }
        } else{ // otherwise, new Knife
            Vector features = new Vector();
            currentKnife.setFeatures(features);
            detailPanel.clearData();
        }
        if(originalKnife != null && !loading){
        	currentKnife.setID(originalKnife.getID());
        	Vector features = currentKnife.getFeatures();
        	if(features!=null){
        		for(Enumeration enum=features.elements(); enum.hasMoreElements();){
        			InvoiceEntryAdditions feature = (InvoiceEntryAdditions)enum.nextElement();
        			feature.setEntryID(originalKnife.getInvoiceEntryID());
        		}
        	}
        }else{        
        	originalKnife = currentKnife;
        }

        if(!loading){
        	currentKnife.setParent(invoice);
            detailPanel.setData(currentKnife);
        }
        
        if(!loading){
        	parentScreen.updateOccured(currentKnife,ScreenController.UPDATE_EDIT, null);
        	setEdited(true);
        }else
        	parentScreen.updateOccured(currentKnife,ScreenController.LIST_ITEM_SELECTED, null);

        int year = sys.invoiceInfo.getPricingYear(invoice);
        detailPanel.setPricingYear(year);
        
        lastIndex = newIndex;
    }
    //=============================================================
    //=============================================================

    
    //=============================================================
    //=============================================================
    InvoiceEntries getKnife(ListObject item){
        long invoiceNumber=0;
//        Vector features=null;
//        if(originalKnife != null){
//            originalKnife.setPartID(item.getID());
//            return originalKnife;
//        }
        if(invoiceNumber == 0) // never been saved knife?
            invoiceNumber = invoice.getInvoice();

        InvoiceEntries knife = new InvoiceEntries(0);
        knife.setPartID(item.getID());
    	int year = sys.invoiceInfo.getPricingYear(invoice);
        // set knife price to current years price ??????
//    	int year = DataModel.getCurrentYear();
        double price=0;
        price = priceTable.getPartPrice(year, item.getID());

        knife.setInvoice(invoiceNumber);
        knife.setPrice(price);
        knife.setQuantity(detailPanel.getQuantity());
//        knife.setFeatures(features);

        return knife;
    }
    
    public InvoiceEntries getData(){
        return detailPanel.getData();
    }
    public void clearData(){
        detailPanel.clearData();
    }
    
//  -----------------------------------------------------------------
    public void loadListData(long currentKnifePartID, int year){
        Vector knives = sys.partInfo.getParts(10); // model
        Vector misc = sys.partInfo.getParts(99); // misc
        knives.addAll(misc);
        // *** sort then load knives list
        Object[] sortedLst = knives.toArray();
        java.util.Arrays.sort(sortedLst, new rmk.comparators.KnifeListComparator());
        
        for(int partIndex=0; partIndex < sortedLst.length; partIndex++){
            Parts part = (Parts)sortedLst[partIndex];
            int partID = (int)part.getPartID();
//          if(part.isActive() || (originalKnife != null && part.getPartID() == originalKnife.getPartID())){
            if(part.isActive() || part.getPartID() == currentKnifePartID){
                InvoiceEntryAdditions feature = new InvoiceEntryAdditions(0);
                feature.setPartID(partID);
                if(originalKnife != null)
                    feature.setEntryID(originalKnife.getInvoiceEntryID());
                double price = priceTable.getPartPrice(year, partID);
                if(price < 0){
                	priceTable.warnIfBadLookup(false);
    				price = 0;
    			}

                feature.setPrice(price);
                
                listData.addElement(new ListObject(feature,0));
            }
        }
    	priceTable.warnIfBadLookup(true);
    }
    
    public void selectListItem(long partID) {
        int index = 1;
        
        for (Enumeration enum = listData.elements(); enum.hasMoreElements();) {
            ListObject item = (ListObject) enum.nextElement();
            if (item.getID() == partID) {
                index = listData.indexOf(item);
                break;
            }
        }
        
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
    }
    
    public void setData(DBObject item){
    	loading = true;

    	InvoiceEntries currKnife=(InvoiceEntries) item;
    	invoice = currKnife.getParent();
    	customer = invoice.getParent();
    	
    	int year = sys.invoiceInfo.getPricingYear(invoice);
    	loadListData(currKnife.getPartID(), year);
    	selectListItem(currKnife.getPartID());
    	originalKnife = currKnife;
    	
    	clearData();

    	detailPanel.setData(currKnife);
    	loading = false;
    	setEdited(false);
    }
	

}

