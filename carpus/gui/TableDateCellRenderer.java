package carpus.gui;

import javax.swing.*;
import java.awt.*;
import java.text.*;
import javax.swing.table.DefaultTableCellRenderer;


public class TableDateCellRenderer extends DefaultTableCellRenderer{
    private JLabel cell = new JLabel();
    Color highlightColor;
    private JPanel pnl = new JPanel();

    // Constructors 
    public TableDateCellRenderer (java.awt.Color selectionBackground) { 
	cell.setHorizontalAlignment(JLabel.CENTER); 
	cell.setVerticalAlignment(JLabel.TOP); 
	highlightColor = selectionBackground;
	pnl.setLayout(new BorderLayout());
	pnl.add(cell, BorderLayout.NORTH);
    } 

    // TableCellRenderer interface implementation 
    public Component getTableCellRendererComponent(JTable table, Object value, 
						   boolean isSelected, boolean hasFocus, 
						   int row, int column) {
        
	pnl.setBackground(isSelected?highlightColor:Color.WHITE);
    cell.setFont(cell.getFont().deriveFont(Font.BOLD));
	if(carpus.util.SystemPrefrences.runningOnWindows()){
	    cell.setForeground(isSelected?Color.WHITE:Color.BLACK);
	} else{
	    cell.setForeground(isSelected?Color.BLACK:Color.BLACK);
	}

	if(value == null){
	    cell.setText("");
	    return pnl;
	}
	
        Class type = value.getClass();	
        if (type == java.util.Date.class || type.getSuperclass() == java.util.Date.class ) {
	    SimpleDateFormat formatter = new SimpleDateFormat ("MM / dd / yyyy");	    
	    cell.setText(formatter.format((java.util.Date)value));
	}
	return pnl; 
    }
}
