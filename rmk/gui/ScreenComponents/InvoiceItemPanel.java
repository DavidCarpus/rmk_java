package rmk.gui.ScreenComponents;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import rmk.DataModel;
import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import rmk.database.dbobjects.Parts;
import rmk.database.dbobjects.Customer;
import java.util.Vector;
import java.util.Enumeration;
import rmk.gui.DBGuiModel;
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
    DBGuiModel model;
    static rmk.DataModel sys = rmk.DataModel.getInstance();
    InvoiceEntries originalKnife=null;
    Invoice invoice;
    Customer customer;
    
//  -----------------------------------------------------------------
    public InvoiceItemPanel(){
        priceTable = sys.pricetable;
//      priceTable = rmk.database.PartPriceTable.getInstance();
        list.setVisibleRowCount(6);
        list.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        listData.clear();
        if(model != null){
            if(model.getInvoiceData() != null)
                invoice = (Invoice)model.getInvoiceData().get(0);
            if(model.getCustomerData() != null)
                customer = (Customer)model.getCustomerData().get(0);
        }

        //TODO: Neet to go through parent screen with messages
//        detailPanel.addActionListener(this);
        
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

        //TODO: Neet to go through parent screen with messages
//        detailPanel.addActionListener(this);
        
        add(listPanel);
        add(detailPanel);
        
        setPreferredSize(new Dimension(530,470));
    }
    
     public void setParent(IScreen screen){
    	parent = screen;
    	detailPanel.setParent(parent);
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
    		//TODO: Neet to go through parent screen with messages
            if(!loading)
            	parent.updateOccured(originalKnife,ScreenController.UPDATE_EDIT, null);
            else
            	parent.updateOccured(originalKnife,ScreenController.UPDATE_UNKNOWN, null);
//            notifyListeners("INVOICEFEATURECHANGE", this);
        } else if (command.startsWith("ADDFEATURE")){
        	addFeature((InvoiceEntryAdditions)e.getSource());
        	return;
    		//TODO: Neet to go through parent screen with messages
            //            notifyListeners("INVOICEFEATURECHANGE", this);
        } else if (command.equals("INVOICEFEATURECHANGE")) { //INVOICE FEATURE CHANGE
            setEdited(true);
    		//TODO: Neet to go through parent screen with messages
            //            notifyListeners(e);
        } else if (command.equals("ENTERKEY")) { // ENTER KEY
            ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command);
            // ignore for now
        } else{
            ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command);
        }
    }
//  -----------------------------------------------------------------
    public void valueChanged(ListSelectionEvent e) {
        int newIndex = list.getSelectedIndex();
        
        if(e.getValueIsAdjusting()){
            return;
        }
        InvoiceEntries currKnife = originalKnife;

//        boolean isNew = (currKnife.isEdited());
//        
        if(lastIndex == -1){
            lastIndex = newIndex;
        }
        
        if(lastIndex == newIndex && currKnife != null)
            return; // nothing changed, || new
        
        ListObject item = (ListObject)list.getSelectedValue();
        InvoiceEntries generatedKnife = getKnife(item);

        // if changing an existing knife
        if(originalKnife != null){ 
            // get old features, and change the entryID on them
            // AND total price up
            Vector existingFeatures = originalKnife.getFeatures();
            if(existingFeatures != null && existingFeatures.size() > 0 && !loading){
                if(rmk.gui.Dialogs.yesConfirm("Clear Features")){
                    generatedKnife.setFeatures(originalKnife.getFeatures());
                    for(Enumeration enum=existingFeatures.elements(); enum.hasMoreElements();){
                        InvoiceEntryAdditions feature = (InvoiceEntryAdditions)enum.nextElement();
                        feature.setEntryID(generatedKnife.getInvoiceEntryID());
                    }
                } else{
                    double price=0;
                    price = priceTable.getPartPrice(DataModel.getCurrentYear(), (int)generatedKnife.getPartID());

                    for(Enumeration enum=existingFeatures.elements(); enum.hasMoreElements();){
                        InvoiceEntryAdditions feature = (InvoiceEntryAdditions)enum.nextElement();
                        price += feature.getPrice();
                        feature.setEntryID(originalKnife.getInvoiceEntryID());
//                      feature.setEntryID(0);
                    }
                    originalKnife.setPrice(price);
                }
            } else{
                double price=0;
                price = priceTable.getPartPrice(DataModel.getCurrentYear(), (int)generatedKnife.getPartID());
                originalKnife.setPrice(price);
            }
        } else{ // otherwise, new Knife
            Vector features = new Vector();;
            model.setInvoiceItemAttributesData(features);
            generatedKnife.setFeatures(features);
            detailPanel.clearData();
        }
        if(originalKnife != null)
            generatedKnife.setID(originalKnife.getID());

        
        originalKnife = generatedKnife;

        Vector knifeVector = new Vector();
        knifeVector.add(generatedKnife);
        model.setKnifeData(knifeVector);
        if(!loading)
            detailPanel.setData(model);
        
        if(!loading)
        	parent.updateOccured(generatedKnife,ScreenController.UPDATE_EDIT, null);
        else
        	parent.updateOccured(generatedKnife,ScreenController.LIST_ITEM_SELECTED, null);
//        actionPerformed(new ActionEvent(list, 1, "INVOICEITEMDETAILSCHANGE"));
        int year = sys.invoiceInfo.getPricingYear(invoice);

        //TODO: Neet to go through parent screen with messages
        //        notifyListeners(new ActionEvent(this, (int)generatedKnife.getPartID(), "SET_KNIFE_MODEL|" + year));
        
        lastIndex = newIndex;
    }
    
    boolean modelChange(){
        boolean results = false;
        Vector vector = model.getKnifeData();
        InvoiceEntries currKnife = (InvoiceEntries)vector.get(0);

        return results;
    }
    
    InvoiceEntries getKnife(ListObject item){
        long invoiceNumber=0;
        Vector features=null;
        if(originalKnife != null){
            originalKnife.setPartID(item.getID());
            return originalKnife;
        }
        if(invoiceNumber == 0) // never been saved knife?
            invoiceNumber = invoice.getInvoice();

        InvoiceEntries currKnife = new InvoiceEntries(0);
        currKnife.setPartID(item.getID());
        // set knife price to current years price ??????
        double price=0;
        price = priceTable.getPartPrice(DataModel.getCurrentYear(), item.getID());

        currKnife.setInvoice(invoiceNumber);
        currKnife.setPrice(price);
        currKnife.setQuantity(detailPanel.getQuantity());
        currKnife.setFeatures(features);

        return currKnife;
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
                
                feature.setPrice(price);
                
                listData.addElement(new ListObject(feature,0));
            }
        }
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
//  -----------------------------------------------------------------
    public void setData(DBGuiModel model ){
        if(loading) 
            return;
        loading = true;
        
        this.model = model;
        originalKnife=null;

        InvoiceEntries currKnife=null;
        invoice = (Invoice)model.getInvoiceData().get(0);
        customer = (Customer)model.getCustomerData().get(0);
        
        if(model.getKnifeData() != null)
            currKnife = (InvoiceEntries )model.getKnifeData().get(0);
        
        int year = sys.invoiceInfo.getPricingYear(invoice);
        long originalID = 0;
        if(currKnife != null)
            originalID = currKnife.getPartID();
        
        loadListData(originalID, year);
        selectListItem(originalID);
        
        
        // ***  load text field values
        originalKnife = currKnife;
        Vector knifeData = new Vector();
        knifeData.add(currKnife);
        model.setKnifeData(knifeData);
        detailPanel.setData(model);
        
		//TODO: Neet to go through parent screen with messages
        //        notifyListeners(new ActionEvent(this, (int)originalID, "SET_KNIFE_MODEL|" + year));
        
        loading = false;
        setEdited(false);
    }
}

