package rmk.gui.ScreenComponents;

import javax.swing.*;

import rmk.ErrorLogger;
import rmk.database.dbobjects.Parts;
import rmk.database.dbobjects.InvoiceEntryAdditions;

import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.event.*;

//============================================================
//============================================================
public class KnifeFeaturesPanel 
extends JPanel 
implements ActionListener
{
//============================================================
    rmk.DataModel sys = rmk.DataModel.getInstance();
    DefaultListModel options[] = new DefaultListModel[sys.partInfo.mainPartTypeCnt()];
    Vector listeners;
//-----------------------------------------------------------------
    public KnifeFeaturesPanel(){
	setLayout(new BoxLayout(this,BoxLayout.X_AXIS));

	JList fields[] = new JList[sys.partInfo.mainPartTypeCnt()];
	for(int fieldIndex=1; fieldIndex < fields.length; fieldIndex++){
	    options[fieldIndex] = new DefaultListModel();
	    fields[fieldIndex] = new JList(options[fieldIndex]);
	    fields[fieldIndex].setVisibleRowCount(6);
	    SingleSelectionModel selectionModel = new SingleSelectionModel(fieldIndex) {
		    public void updateSingleSelection(int oldIndex, int newIndex) {
//  			ErrorLogger.getInstance().logMessage(index + ":" + newIndex);
			ListObject item = ((ListObject)options[id].get(newIndex));
			notifyListeners(new ActionEvent(item.getAddition(),
							(int)item.getID(),
							"ADDFEATURE"));
		    }
		};
	    fields[fieldIndex].setSelectionModel(selectionModel);


	    JPanel listPanel = new JPanel();
	    JScrollPane  scrollPane = new JScrollPane(fields[fieldIndex]);
	    listPanel.setLayout(new BoxLayout(listPanel,BoxLayout.Y_AXIS));
	    if(fieldIndex == 1)
		listPanel.add(new JLabel("Blade"));
	    else
		listPanel.add(new JLabel(sys.partInfo.getPartTypeDesc(fieldIndex*10)));
	    listPanel.add(scrollPane);
	    listPanel.setPreferredSize(new Dimension(90,100));

	    add(listPanel);
	}
	setPreferredSize(new Dimension(80,300));
    }
//-----------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase().trim();
    ErrorLogger.getInstance().logDebugCommand(command);

  	if (command.startsWith("SET_KNIFE_MODEL|")) { //SET_KNIFE_MODEL
	    int year = Integer.parseInt(command.substring(command.indexOf("|")+1));
	    setKnifeModel(e.getID(),year);
//  	    sys.invoiceInfo.getInvoiceFromEntryID

  	} else if (command.startsWith("INVOICEFEATURECHANGE")){
	    // ignore this message
  	} else {
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command);
  	}
    }
//-----------------------------------------------------------------
    public void addActionListener(ActionListener listener){
	if(listeners == null) listeners = new Vector();
	if(!listeners.contains(listener)) listeners.addElement(listener);
    }
//-----------------------------------------------------------------
    public void setKnifeModel(int model, int year){
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":setKnifeModel(int model, int year)"+ model + ":" + year);
	for (int type = 1; type < options.length; type++){
	    loadFeatureField(model, year, options[type], type*10);
	}
//  	loadFeatureField(model, year, options[options.length-1], 99); // load misc lst
    }
//-----------------------------------------------------------------
    public void notifyListeners(ActionEvent event){
	if(listeners == null) return;
	for(Enumeration enum=listeners.elements(); enum.hasMoreElements();){
	    ((ActionListener)enum.nextElement()).actionPerformed(event);
	}
    }
//-----------------------------------------------------------------
    public void loadFeatureField(int knifeModel, int year, DefaultListModel fieldModel, int type){
	rmk.DataModel sys = rmk.DataModel.getInstance();
	Vector typeLst;
	Vector parts = new Vector();

	if(type == 10) type++;
	int endType = type+10;
	while (endType%10 > 0) endType--;
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ type + "," + endType);
	for(int i=type ; i< endType; i++){
	    typeLst = sys.partInfo.getParts(i);
	    parts.addAll(typeLst);
	}
	Object partArray[] = parts.toArray();
	java.util.Arrays.sort(partArray, new rmk.comparators.KnifeListComparator());

	fieldModel.clear();
	int listIndex=0;
//	rmk.database.PartPriceTable priceTable = rmk.database.PartPriceTable.getInstance();

	for(int partIndex=0; partIndex < partArray.length; partIndex++){
	    Parts part = (Parts)partArray[partIndex];
//  	    Parts part = (Parts)parts.get(partIndex);
	    if(sys.partInfo.validPartType(knifeModel, (int)part.getPartID())){
		InvoiceEntryAdditions feature = new InvoiceEntryAdditions(0);
		feature.setPartID(part.getPartID());
//  		feature.setEntryID(knife.getInvoiceEntryID());
		double price = rmk.DataModel.getInstance().pricetable.getPartPrice(year, (int)part.getPartID());
		feature.setPrice(price);

		fieldModel.addElement(new ListObject(feature,0));
	    }
	}
    }
//-----------------------------------------------------------------
//-----------------------------------------------------------------
      public static void main(String args[]) throws Exception{
  	rmk.gui.Application.main(args);
      }
//-----------------------------------------------------------------
}
