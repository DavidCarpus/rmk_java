/*
 * Created on Feb 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package rmk.gui;

/**
 * @author carpus
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import rmk.ErrorLogger;

import java.beans.*; //property change stuff
import java.awt.*;
import java.awt.event.*;

public class ToDoDisplay extends JDialog
	implements PropertyChangeListener 
{
	private String typedText = null;
//		private JTextArea textPane;
	JEditorPane textPane;
	private String defaultText;
//		private String magicWord="Blah";
	private JOptionPane optionPane;
	private String btnString1 = "Print";
	private String btnString2 = "Cancel";
	/**
	 * Returns null if the typed string was invalid;
	 * otherwise, returns the string as the user entered it.
	 */
	//==================================================================
	public String getText() {
		return typedText;
	}
	//==================================================================
	/** Creates the reusable dialog. */
	public ToDoDisplay(Frame aFrame, String defaultText, String title, String message)  throws Exception{
	super(aFrame, true);
	create( defaultText,  title,  message);
	}
	public ToDoDisplay(String defaultText, String title, String message)  throws Exception{
	create( defaultText,  title,  message);
	}
	void create(String defaultText, String title, String message)
		throws Exception
	{
		this.defaultText = defaultText;
		setTitle(title);
		textPane = new JEditorPane();
		textPane.setContentType("text/html");
		//		textPane.setEditorKitForContentType("text/html");

		//		String location = "/home/carpus/public_html/funny/Cannot find Weapons of Mass Destruction.html";
		//		java.net.URL helpURL = new java.net.URL("file", null, -1,location);
		//		java.net.URL helpURL = new java.net.URL("http", "www.randallknives.com", -1,"/");
		String location = "";
		boolean gotIt = false;
		String locations[] = {
				"./rmk/docs/"
//				,"c:/Program Files/eclipse/workspace/rmk/rmk/docs/"
				,"s:/NewRMK/rmk/docs/"
				, "/home/dcarpus/rmk/"
				, "/home/carpus/rmk/" 
				,"/home/dcarpus/workspace/rmk/rmk/docs/" 
				, "/home/carpus/workspace/rmk/rmk/docs/"  
				};

		java.net.URL helpURL = null;
		for(int i=0; i< locations.length && ! gotIt; i++){
			location = locations[i] + "RMKToDo.htm";
			try
			{
				helpURL = new java.net.URL("file", null, -1, location);
				textPane.setPage(helpURL);
				gotIt = true;				
			} catch (Exception e){		}
		}
		if (!gotIt)
			return;
		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + helpURL);

		//		BufferedReader in = new BufferedReader(
		//							   new StringReader(
		//									"<HTML><TITLE>The Title</TITLE>" + 
		//									"<BODY><I>italics</I></BODY></HTML>"));
		//		htmlKit.read(in, textPane.getDocument(),0);

		textPane.setPreferredSize(new Dimension(800, 600));
		JScrollPane scrollPane = new JScrollPane(textPane);
		//Create an array of the text and components to be displayed.
		Object[] array = { message, scrollPane };
		//Create an array specifying the number of dialog buttons
		//and their text.
		Object[] options = { btnString1,btnString2  };
		//Create the JOptionPane.
		optionPane =
			new JOptionPane(
				array,
				JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION,
				null,
				options,
				options[0]);
		//Make this dialog display it.
		setContentPane(optionPane);

		//Handle window closing correctly.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				/* Instead of directly closing the window,
				 * we're going to change the JOptionPane's value property.
				*/
				optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
			}
		});
		//Ensure the text field always gets the first focus.
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent ce)
			{
				textPane.requestFocusInWindow();
			}
		});
		//Register an event handler that reacts to option pane state changes.
		optionPane.addPropertyChangeListener(this);
		setModal(true);
		pack();
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
			if(value.equals(btnString1)){
				try {
                    rmk.reports.Printing.printToDO(textPane);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, e1);
                }
			} else{
					optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
					clearAndHide();
				}
			 }
		}
	//==================================================================
	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		textPane.setText(null);
		setVisible(false);
	}
	//--------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------
	public static void main(String args[]) throws Exception{
	String txt = 	"<I>italics</I>";
//	"Message";
	ToDoDisplay dial = new ToDoDisplay(null, txt, "To Do", "");
		dial.setVisible(true);
	ErrorLogger.getInstance().logMessage(dial.getText());
	
	System.exit(0);
	}

}

