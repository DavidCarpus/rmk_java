package rmk;

import java.util.Vector;
//import java.util.Enumeration;
//import java.awt.event.ActionListener;

public class DBModel{
    public static final int CUSTOMER_DATA = 0;
    public static final int INVOICE_DATA = 1;
    public static final int INVOICE_ITEMS_DATA = 2;
    public static final int INVOICE_ITEM_ATTRIBUTES_DATA = 3;
    public static final int ADDRESS_DATA = 4;
    public static final int KNIFE_DATA = 5;    
    public static final int PAYMENTS_DATA=6;
    public static final int CNT = 7;

    public static final String[] dataTypes = {
	"CustomerData",   
	"InvoiceData",  
	"InvoiceItemsData",  
	"InvoiceItemAttributesData",  
	"AddressData",  
	"KnifeData",  
	"PaymentsData"
    };

    Vector data;
    rmk.database.DBAccess db = rmk.database.DBAccess.getInstance();
    Vector listeners = new Vector();

    public DBModel(){
	data = new Vector(CNT);
	for(int i=0; i<CNT; i++)
	    data.add(null);
    }
    protected void setData(int type, Vector lst){
	data.set(type, lst);
	if(lst == null || lst.size() == 0) return;

//  	notifyListeners("DBModelChanged-" + dataTypes[type]);
    }
    protected Vector getData(int type){
	return (Vector)data.get(type);
    }

    public void setCustomerData(Vector lst){
	setData(CUSTOMER_DATA, lst);
    }
    public Vector getCustomerData(){	
	return (Vector)data.get(CUSTOMER_DATA);
    }
    public void setAddressData(Vector lst){
	setData(ADDRESS_DATA, lst);
    }
    public Vector getAddressData(){	
	return (Vector)data.get(ADDRESS_DATA);
    }
//    public void setInvoiceData(Vector lst){
//	setData(INVOICE_DATA, lst);
//    }
//    public Vector getInvoiceData(){
//	return (Vector)data.get(INVOICE_DATA);
//    }
    public void setInvoiceItemsData(Vector lst){
	setData(INVOICE_ITEMS_DATA, lst);
    }
    public Vector getInvoiceItemsData(){
	return (Vector)data.get(INVOICE_ITEMS_DATA);
    }
    public void setInvoiceItemAttributesData(Vector lst){
	setData(INVOICE_ITEM_ATTRIBUTES_DATA, lst);
    }
    public Vector getInvoiceItemAttributesData(){
	return (Vector)data.get(INVOICE_ITEM_ATTRIBUTES_DATA);
    }

    public void setKnifeData(Vector lst){
	setData(KNIFE_DATA, lst);
    }
    public Vector getKnifeData(){
	return (Vector)data.get(KNIFE_DATA);
    }

    public void setPaymentsData(Vector lst){
	setData(PAYMENTS_DATA, lst);
    }
    public Vector getPaymentsData(){
	return (Vector)data.get(PAYMENTS_DATA);
    }

//-----------------------------------------------------------------
//    public void addActionListener(java.awt.event.ActionListener listener){
//	if(listeners == null) listeners = new Vector();
//	if(!listeners.contains(listener)) listeners.addElement(listener);
//    }
//    public void removeActionListener(java.awt.event.ActionListener listener){
//	if(listeners == null) return;
//	listeners.remove(listener);
//    }
//
//    private void notifyListeners(String msg){
//	if(listeners == null) return;
//	notifyListeners(new java.awt.event.ActionEvent(this,1,msg));
//    }
//    private void notifyListeners(String msg, carpus.gui.DataEntryPanel panel){
//	if(listeners == null) return;
//	notifyListeners(new java.awt.event.ActionEvent(panel,1,msg));
//    }
//    private void notifyListeners(java.awt.event.ActionEvent event){
//	if(listeners == null) return;
//	for(Enumeration enum=listeners.elements(); enum.hasMoreElements();){
//	    ActionListener listener = (ActionListener)enum.nextElement();
////      	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":notify:" + listener);
//	    listener.actionPerformed(event);
//	}
//    }
//-----------------------------------------------------------------
}
