package carpus.gui;

import javax.swing.*;
import java.awt.*;
import java.text.*;
import javax.swing.table.DefaultTableCellRenderer;

public class TableCurrencyCellRenderer extends DefaultTableCellRenderer {

    private JLabel cell = new JLabel();

    Color highlightColor;

    private JPanel pnl = new JPanel();

    final NumberFormat formatter;

    // Constructors
    public TableCurrencyCellRenderer(java.awt.Color selectionBackground) {
        formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        cell.setHorizontalAlignment(JLabel.CENTER);
        cell.setVerticalAlignment(JLabel.TOP);
        highlightColor = selectionBackground;
        pnl.setLayout(new BorderLayout());
        pnl.add(cell, BorderLayout.EAST);
    }

    // TableCellRenderer interface implementation
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        
        pnl.setBackground(isSelected ? highlightColor : Color.white);
        
        Font newFont = cell.getFont();
//        newFont = newFont.deriveFont(newFont.getStyle(), 12);
        newFont = newFont.deriveFont(Font.BOLD, 12);
        cell.setFont(newFont);

        if (value == null) {
            cell.setText("");
            return pnl;
        }

        Class type = value.getClass();
        if (type == Double.class || type.getSuperclass() == Double.class) {
            Double val = (Double) value;
            if(val.doubleValue()<0){
            	cell.setForeground(Color.RED);
            }else{
            	if(carpus.util.SystemPrefrences.runningOnWindows()){
            	    cell.setForeground(isSelected?Color.WHITE:Color.BLACK);
            	} else{
            	    cell.setForeground(isSelected?Color.BLACK:Color.BLACK);
            	}
            }

            String txt = "$" + formatter.format(val) + "   ";
            //  	    System.out.println(this.getClass().getName() + ":"+ txt);
            cell.setText(txt);
        }
        return pnl;
    }
}
