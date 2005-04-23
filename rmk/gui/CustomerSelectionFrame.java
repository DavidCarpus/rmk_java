package rmk.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import rmk.ErrorLogger;

public class CustomerSelectionFrame extends JInternalFrame implements ActionListener {
    Vector listeners=null;
    ArrayList customerList;
    carpus.gui.BasicToolBar buttonBar;
    long selectedCustomer;
    CustomerSelectionTableModel customerData;
    carpus.util.TableSorter sorter;

    public CustomerSelectionFrame() throws Exception{
        super("Customers", 
              true, //resizable
              true, //closable
              false, //maximizable
              false);//iconifiable

	buttonBar = new carpus.gui.BasicToolBar(null, 
						new String[] {"Select", "Cancel"}, 
						new String[] {"Select", "Cancel"},
						new String[] {"Select", "Cancel"});
	buttonBar.addActionListener(this);
	buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));
        
    	getContentPane().setLayout(new BorderLayout());
	JPanel lst=getSelectionList();
	getContentPane().add(lst , BorderLayout.CENTER);

	getContentPane().add( buttonBar, BorderLayout.SOUTH);
	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	buttonBar.registerKeyboardAction(this, "Cancel", stroke, 
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	
	setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//          setSize(200,200); //...Then set the window size or call pack...
  	pack();
// 	this.addWindowListener(new WindowAdapter() {
// 		public void windowClosed(WindowEvent e) {
//   		    this.setVisible(false);
// 		}
// 	    });
    }

    JPanel getSelectionList(){
	JPanel results=new JPanel();
    	results.setLayout(new BorderLayout());

        customerData = new CustomerSelectionTableModel(customerList);
        sorter = new carpus.util.TableSorter(customerData); 
        JTable table = new JTable(sorter);
	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	table.registerKeyboardAction(this, "Cancel", stroke, 
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//  	results.registerKeyboardAction(this, "Cancel", stroke, WHEN_IN_FOCUSED_WINDOW);
//  	table.registerKeyboardAction(this, "Cancel", stroke, WHEN_IN_FOCUSED_WINDOW);

	TableColumn column = null;
	for (int i = 0; i < 3; i++) {
	    column = table.getColumnModel().getColumn(i);
	    if (i == 0) { // ID column
//  		column.setPreferredWidth(5);
		column.setMaxWidth(0);
		column.setMinWidth(0);
		column.setWidth(0); 
	    }else {
		column.setPreferredWidth(100);
	    }
	}

	sorter.addMouseListenerToHeaderInTable(table);

	//following code specifies that only one row at a time can be selected
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	ListSelectionModel rowSM = table.getSelectionModel();
	rowSM.addListSelectionListener(new ListSelectionListener(){
		public void valueChanged(ListSelectionEvent e) {
		    if (e.getValueIsAdjusting()) return; //Ignore extra messages.
		    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		    if (!lsm.isSelectionEmpty()) {
			int row=lsm.getMinSelectionIndex();
			selectedCustomer = ((Long)sorter.getValueAt(row,0)).longValue();
		    }
		}
	    });
	table.addMouseListener(new MouseAdapter(){
		public void mouseClicked(MouseEvent e){
		    if (e.getClickCount() == 2){
			actionPerformed(new ActionEvent(this,1,"SELECT"));
		    }
		}
	    } );


      	JScrollPane scrollPane = new JScrollPane(table);

	results.add(scrollPane, BorderLayout.CENTER);
	return results;
    }
    public void actionPerformed(ActionEvent e) {
//  	ErrorLogger.getInstance().logMessage(e);
	String command = e.getActionCommand().toUpperCase();
	
        if (command.equals("CANCEL")) { //cancel
            this.setVisible(false);
        } else if (command.equals("SELECT")) { //select
	    if(listeners != null){
		ActionEvent event = 
		    new ActionEvent(this,1,"Customer Select " + selectedCustomer);
		for(Enumeration enum=listeners.elements(); enum.hasMoreElements();){
		    ((ActionListener)enum.nextElement()).actionPerformed(event);
		}
	    }
        } else {
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command);
	}
	
    }

    public void setCustomers(ArrayList lst){
//  	ErrorLogger.getInstance().logMessage("CustomerSelectionFrame:setCustomers()" + lst.size());
	this.toFront();
	
	customerList = lst;
	customerData.setValues(lst);
	sorter.tableChanged(new javax.swing.event.TableModelEvent(sorter));
	sorter.sortByColumn(1, true);
	setVisible(true);
	pack();
    }
    public void addActionListener(ActionListener listener){
	if(listeners == null) listeners = new Vector();
	if(!listeners.contains(listener)) listeners.addElement(listener);
    }
    public static void main(String args[])
	throws Exception
    {
	carpus.gui.GJApp.launch(new Application(), "RMK System",50,50,550,600); 
    }
}
class CustomerSelectionTableModel extends AbstractTableModel {
    String[] columnNames;
    Object[][] data= new Object[0][3];

    CustomerSelectionTableModel(ArrayList lst){
	columnNames= new String[]{"ID", "Customer", "Phone", "Dealer"};

	if(lst != null){
	    setValues(lst);
	}
    }

    public void setValues(ArrayList lst){
	if(lst == null){
	    data = new Object[columnNames.length][1];
	    return;
	}
	data = new Object[lst.size()][columnNames.length];
	for(int i=0; i< lst.size(); i++){
	    rmk.database.dbobjects.Customer item = 
		(rmk.database.dbobjects.Customer)lst.get(i);
	    data[i][0] = new Long(item.getCustomerID());
	    data[i][1] = "" + item.getLastName() + 
		(item.getFirstName() != null? "," + item.getFirstName(): "");
	    data[i][2] = "" + item.getPhoneNumber();
	    data[i][3] = new Boolean(item.getDealer() != 0);
	}
    }

    public int getColumnCount() { return columnNames.length; }        
    public int getRowCount() {return data.length;}    
    public String getColumnName(int col) {return columnNames[col];}
    public Object getValueAt(int row, int col) {return data[row][col];}

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
	return getValueAt(0, c).getClass();
    }

}
