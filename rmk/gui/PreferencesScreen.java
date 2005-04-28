/*
 * Created on: Oct 24, 2004
 * By: David Carpus
 */
package rmk.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.TableColumn;

import rmk.ErrorLogger;

import Configuration.Config;

import carpus.gui.DataEntryPanel;
import carpus.gui.LabeledTextField;

/**
 * @author David Carpus
 */
public class PreferencesScreen extends Screen {
    static final int FIELD_BUSINESS_NUMBER=0;
    static final int FIELD_FAX_NUMBER=1;
    static final int FIELD_BACKLOG=2;

    LabeledTextField[] txtFields = new LabeledTextField[3];
    ShippingListCodePanel codePanel = new ShippingListCodePanel();
    TextEntryPanel textPanel = new TextEntryPanel();
    boolean edited = false;
    
	public PreferencesScreen(){		
		super("Preferences");
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));

		getContentPane().add(textPanel);
		getContentPane().add(codePanel);

		textPanel.setFieldEditCheck(txtFields, "PrefChange", textPanel);
		textPanel.addActionListener(this);
		codePanel.addActionListener(this);
		// load properties file
		//set field values
		setFields(Config.p);

		getContentPane().add(buttonBar);
	  	setPreferredSize(new Dimension(400,320));
    	pack();

	}
	
	void setFields(Properties props){
	    txtFields[FIELD_BUSINESS_NUMBER].setValue(Config.getBusinessNumber());
	    txtFields[FIELD_FAX_NUMBER].setValue(Config.getFaxNumber());
	    txtFields[FIELD_BACKLOG].setValue(""+Config.getMonthsBacklogged());
    	Vector translations = new Vector();
	    for(int codeID=1; codeID<20; codeID++){
	    	String translation = Config.getShippingCodeTranslation(""+codeID);
	    	if(translation != null){
	    		String[] pair = {""+codeID, translation};
	    		translations.add(pair);
	    	}
	    }
	    codePanel.setStringValues(translations);
	}
	
	void saveConfiguration() throws Exception{
		if(Config.getPropFileName() != null){
			Properties prop = Config.p;

			prop.setProperty("businessNumber", txtFields[FIELD_BUSINESS_NUMBER].getValue());
			prop.setProperty("faxNumber", txtFields[FIELD_FAX_NUMBER].getValue());
			prop.setProperty("monthsBacklogged", txtFields[FIELD_BACKLOG].getValue());
			String fileName = Config.getPropFileName();
			ErrorLogger.getInstance().logMessage("Saving Preferences to:" + fileName);
			FileOutputStream propFile = new FileOutputStream(fileName);
			prop.store(propFile, "RMK Settings");
			propFile.close();
			ErrorLogger.getInstance().logMessage(""+prop);
			ErrorLogger.getInstance().logMessage("Saved Preferences to:" + fileName);
		}

	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand().toUpperCase().trim();
        ErrorLogger.getInstance().logDebugCommand(command);

		//-----------------------------
		if (command.equals("CANCEL")) { //cancel
		    defaultCancelAction();
		    //-----------------------------
		} else if (command.equals("PREFCHANGE")) { //changed text value
			edited=true;
			buttonBar.enableButton(0, edited);
			//-----------------------------
		} else if (command.equals("EDITTRANSLATION")) { 
		    int id= e.getID()+1;
			Properties prop = Config.p;
			String key = "ShippingCode" + id;
			String startValue=prop.getProperty(key);
            String newValue = JOptionPane.showInputDialog("Translation Value:", startValue);
            if(newValue == null) return;
            if(newValue.equalsIgnoreCase(startValue)) return; // no change
            prop.setProperty(key, newValue);
            setFields(prop);
			edited=true;
			buttonBar.enableButton(0, edited);
			//-----------------------------
		} else if (command.equals("SAVE")) { //save
			try {
				saveConfiguration();
				edited=false;
				buttonBar.enableButton(0, edited);
	            pack();

			} catch (Exception err) {
				ErrorLogger.getInstance().logError("Saving Configuration", err);
			}
			//-----------------------------
		} else {  // Undefined
		    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
		}

	}

	public void internalFrameActivated(InternalFrameEvent e) {
		txtFields[FIELD_BUSINESS_NUMBER].requestFocus();
	}

	public boolean isEdited() {
		return edited;
	}

	public void setData(DBGuiModel model) {} // not used for this screen
	
	class TextEntryPanel extends DataEntryPanel{
		public TextEntryPanel(){
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			
			LabeledTextField field=null;
			field = new LabeledTextField("Business Number    ",new JTextField("", 15));
			txtFields[FIELD_BUSINESS_NUMBER] = field;
			add(field);
			
			field = new LabeledTextField("Fax Number    ",new JTextField("", 15));
			txtFields[FIELD_FAX_NUMBER] = field;
			add(field);
			
			field = new LabeledTextField("Back Log (Months)   ",new JTextField("", 5));
			txtFields[FIELD_BACKLOG] = field;
			add(field);
			
			setPreferredSize(new Dimension(400,80));
		}
	    public void actionPerformed(ActionEvent e) {
	    	String command = e.getActionCommand().toUpperCase().trim();
	    	if(command.equals("COMBOBOXCHANGED"))
	    	    notifyListeners(new ActionEvent(this,0,"PrefChange"));
	    	else
//	    	    notifyListeners(e);
	      	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":Undefined:" + command + "|");
//	      	ErrorLogger.getInstance().logMessage(e);   
	        }
	    void setValues(String[] values){
			txtFields[FIELD_BUSINESS_NUMBER].setValue(values[0]);
			txtFields[FIELD_FAX_NUMBER].setValue(values[1]);
			txtFields[FIELD_BACKLOG].setValue(values[2]);
	    }
	}
	
	class ShippingCodesList  extends carpus.gui.DataListPanelTableModel {
		public ShippingCodesList(Vector lst){
			columnNames= new String[]{"Code", "Translation"};
			this.flags = new int[columnNames.length];
			setValues(lst);
		}
		
		public void setValues(Vector lst) {
			if(lst == null || lst.size() == 0){
			    data = new Object[0][columnNames.length];
			} else{
				data = new Object[lst.size()][columnNames.length];
				for(int i=0; i< lst.size(); i++){
					String[] item = (String[])lst.get(i);
					data[i][0] = item[0];
					data[i][1] = item[1]; 
				}
			}
		}
	}
	
	class ShippingListCodePanel extends carpus.gui.DataListPanel  implements ActionListener{	
		Vector shippingCodes;
		long currItem = 0;
		
		public ShippingListCodePanel(){
			dataModel = new ShippingCodesList(shippingCodes);
		    addTable(dataModel);

		    TableColumn column = table.getColumnModel().getColumn(0);
		    int col0Width = 10;
		    column.setMinWidth(col0Width);
		    column.setWidth(col0Width); 
		    column.setPreferredWidth(col0Width);

			setTableSelectionListeners();
		}
		public void setStringValues(Vector data) {
			dataModel.setValues(data);
			sorter.tableChanged(new javax.swing.event.TableModelEvent(sorter));
		}

		protected void doubleClick() {
			notifyListeners(new ActionEvent(this,(int)currItem,"EditTranslation"));
		}

		protected long selectedItem(int row) {
			currItem = row;
			return selectedItem;
		}

		public void setData(DBGuiModel model) {
			// TODO Auto-generated method stub
			
		}
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}

	}
}
