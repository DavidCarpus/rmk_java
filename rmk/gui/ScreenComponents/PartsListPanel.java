package rmk.gui.ScreenComponents;

import java.awt.event.*;
import java.util.*;
import javax.swing.table.TableColumn;

import rmk.ErrorLogger;
import rmk.ScreenController;
import rmk.database.dbobjects.Parts;
import rmk.gui.IScreen;

public class PartsListPanel extends carpus.gui.DataListPanel  implements ActionListener{
	Vector partsList;
	rmk.DataModel sys = rmk.DataModel.getInstance();
	IScreen parent=null;
	
	public PartsListPanel(){
		dataModel = new PartsListTableModel(partsList);
	addTable(dataModel);
	setColumnFormats();

	TableColumn column = null;
	for (int i = 0; i < dataModel.getColumnCount(); i++) {
		column = table.getColumnModel().getColumn(i);
		if (i == 0 || i == 1) { // ID column , Hide it
		column.setMaxWidth(0);
		column.setMinWidth(0);
		column.setWidth(0); 
		column.setPreferredWidth(0);
		}else if (i == 2) { // PartCode column
		column.setMinWidth(130);
		column.setMaxWidth(210);
		}else if (i == 4) { // PartType column
		column.setMinWidth(110);
		column.setPreferredWidth(110);
//		column.setMaxWidth(110);
		}else if (i > 4) { // Boolean columns
		column.setMaxWidth(60);
		column.setPreferredWidth(60);
		}else {
		column.setPreferredWidth(100);
		}
	}
	setTableSelectionListeners();

	buttonBar = null;
	}


	public void setParent(IScreen screen){
		parent = screen;
	}
	
	public void doubleClick(){
		//		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "selected:" + selectedItem);
		notifyListeners(new ActionEvent(this,(int)selectedItem,"PartsDetails"));
	}
	
	public long selectedItem(int row){
		long val = ((Long)sorter.getValueAt(row,1)).longValue();
		parent.buttonPress(ScreenController.BUTTON_SELECTION_DETAILS, (int)val );
		return val;
	}

	public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase();
    ErrorLogger.getInstance().logDebugCommand(command);

	ActionEvent event=null;

	if(command.equals("CANCEL")){
		event = new ActionEvent(this,1,"CANCEL");
	} else {  // Undefined
		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
		}
	notifyListeners(event);
	}

//	public void setData(rmk.gui.DBGuiModel model){};

	public void setData(Collection data) {
		dataModel.setValues(new Vector(data));
		sorter.tableChanged(new javax.swing.event.TableModelEvent(sorter));
		if (data != null && data.size() > 0) {
			sorter.sortByColumn(2, true);
		}
	}

}
//=================================================================================
class PartsListTableModel  extends carpus.gui.DataListPanelTableModel {
	rmk.DataModel sys = rmk.DataModel.getInstance();

	PartsListTableModel(Vector lst){
	columnNames= new String[]{"Item", "PartID", "Part Code", "Description", "PartType", 
				  "Active", "Discountable", "Blade", "Ask$"
				  };
	this.flags = new int[columnNames.length];
	setValues(lst);
	}

	public void setValues(Vector lst){
	if(lst == null || lst.size() == 0){
		data = null;
		return;
	}
	data = new Object[lst.size()][columnNames.length];

	for(int i=0; i< lst.size(); i++){
		for(int j=0; j< columnNames.length; j++)
		data[i][j] = "";

		int colIndex=0;
		Parts item = (Parts)lst.get(i);
		data[i][colIndex++] = item;
		data[i][colIndex++] = new Long(item.getPartID());
		data[i][colIndex++] = "  "+item.getPartCode();
		data[i][colIndex++] = "  "+item.getDescription();
		data[i][colIndex++] = "  ("+ item.getPartType() + ") " 
		+ sys.partInfo.getPartTypeDesc(item.getPartType());

		data[i][colIndex++] = new Boolean(item.isActive());
		data[i][colIndex++] = new Boolean(item.isDiscountable());
		data[i][colIndex++] = new Boolean(item.isBladeItem());
		if(item.getPartCode().equalsIgnoreCase("#1"))
			ErrorLogger.getInstance().logMessage("Debug");
		data[i][colIndex++] = new Boolean(item.askPrice());
	}
	}
}
