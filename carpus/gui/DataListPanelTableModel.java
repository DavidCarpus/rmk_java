package carpus.gui;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

//==========================================================
//==========================================================
public abstract class DataListPanelTableModel extends AbstractTableModel {
    public final static int FLAGS_NONE=0;
    public final static int FLAGS_DATE=1;
    public final static int FLAGS_CURRENCY=2;

    protected String[] columnNames;
    protected int[] flags;

    protected Object[][] data;
    protected long minID=0;
    protected long maxID=0;

    public double getColTotal(int col){
	double total=0;
	for(int i=0; i< data.length; i++){
	    total += Double.parseDouble(""+data[i][col]);
	}
	return total;

    }
    public abstract void setValues(Vector lst);

    public boolean isDateColumn(int i){	return  (flags[i] & FLAGS_DATE)>0;}
    public boolean isCurrencyColumn(int i){	return (flags[i] & FLAGS_CURRENCY)>0;}

    public int getColumnCount() { return columnNames.length; }        
    public int getRowCount() {return data!= null?data.length:0;}    
    public String getColumnName(int col) {return columnNames[col];}
    public Object getValueAt(int row, int col) {
    	return data[row][col];
    }

    //----------------------------------------------------------
    public Class getColumnClass(int c) {
	if(getValueAt(0, c) != null)
	    return getValueAt(0, c).getClass();
	else
	    return null;
    }
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return false;
    }
}
//==========================================================
//==========================================================

