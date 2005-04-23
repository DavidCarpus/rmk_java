package rmk.gui.search;

import javax.swing.*;

import rmk.ErrorLogger;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.beans.*; //property change stuff

public class ReportSearchScreen 
    extends JDialog
    implements 
//  ActionListener
// , ItemListener
	PropertyChangeListener
{
    private Vector searchCriteria;
    private String type="";
//      JPanel searchPanel = new JPanel();
    ReportSearchPanel searchPanel = new ReportSearchPanel();
    private String btnString1 = "Search";
    private String btnString2 = "Cancel";
    private JOptionPane optionPane;

    public ReportSearchScreen(Frame aFrame) {
	super(aFrame, true);
  	create();
    }
    public ReportSearchScreen(){
  	create();
    }
    void create(){
	setTitle("Report Search");

        //Create an array of the text and components to be displayed.
        Object[] array = {searchPanel};
        //Create an array specifying the number of dialog buttons and their text.
	Object[] options = {btnString1, btnString2};
        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
				     JOptionPane.QUESTION_MESSAGE,
				     JOptionPane.YES_NO_OPTION,
				     null,
				     options,
				     options[0]);
        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                /* Instead of directly closing the window,
                 * we're going to change the JOptionPane's value property.
		 */
                    optionPane.setValue(new Integer(
                                        JOptionPane.CLOSED_OPTION));
            }
        });
        //Ensure the search panel always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                searchPanel.requestFocusInWindow();
            }
        });
        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
	setModal(true);
  	pack();
    }


    public boolean isEdited(){return false;}



    public void internalFrameActivated(javax.swing.event.InternalFrameEvent e) {
//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "Window Activated.");
//  	("Internal frame activated", e);
    }


    //==================================================================
    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (isVisible()
         && (e.getSource() == optionPane)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();
            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }
            //Reset the JOptionPane's value. If you don't do this, then if the user
            //presses the same button next time, no property change event will be fired.
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
//  		searchCriteria = textPane.getText();
		searchCriteria = searchPanel.getCriteria();
		type = searchPanel.getType();
		//we're done; clear and dismiss the dialog
		clearAndHide();
            } else { //user closed dialog or clicked cancel
//  		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "It's OK. We won't force you to type "
//                             + magicWord + ".");
                searchCriteria = null;
                clearAndHide();
            }
        }
    }
    //==================================================================
    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
//          textPane.setText(null);
        setVisible(false);
    }

    public Vector getCriteria(){
	return searchCriteria;
    }
    public String getType(){
	return type;
    }
    //==========================================================
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase().trim();

//          if (command.equals("CANCEL")) { //cancel
//  	    defaultCancelAction();
//  	    return;

//  	}else{
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "|");
//          }
    }
}
