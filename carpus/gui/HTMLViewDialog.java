package carpus.gui;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import java.beans.*; //property change stuff
import java.awt.*;
import java.awt.event.*;

public class HTMLViewDialog extends JDialog
                   implements 
//  				      ActionListener,
			      PropertyChangeListener 
{
    private String typedText = null;
//      private JTextArea textPane;
    JEditorPane textPane;
    private String defaultText;
//      private String magicWord="Blah";
    private JOptionPane optionPane;
    private String btnString1 = "Enter";
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
    public HTMLViewDialog(Frame aFrame, String defaultText, String title, String message)  throws Exception{
	super(aFrame, true);
  	create( defaultText,  title,  message);
    }
    public HTMLViewDialog(String defaultText, String title, String message)  throws Exception{
  	create( defaultText,  title,  message);
    }
    void create(String defaultText, String title, String message) throws Exception{
        this.defaultText = defaultText;
	setTitle(title);
        textPane = new JEditorPane();
	textPane.setContentType("text/html");
//  	textPane.setEditorKitForContentType("text/html");

//    	String location = "/home/carpus/public_html/funny/Cannot find Weapons of Mass Destruction.html";
  	String location = "s:/NewRMK/docs/RMKToDo.htm";
//  	java.net.URL helpURL = new java.net.URL("file", null, -1,location);

//  	java.net.URL helpURL = new java.net.URL("http", "www.randallknives.com", -1,"/");
  	java.net.URL helpURL = new java.net.URL("file", null, -1,location);

//  	HTMLEditorKit htmlKit = new HTMLEditorKit();
//  	textPane.setEditorKit(htmlKit);
//  	textPane.setText(defaultText);
	System.out.println(this.getClass().getName() + ":"+ helpURL);
	textPane.setPage(helpURL);	

//  	BufferedReader in = new BufferedReader(
//  					       new StringReader(
//  								"<HTML><TITLE>The Title</TITLE>" + 
//  								"<BODY><I>italics</I></BODY></HTML>"));
//  	htmlKit.read(in, textPane.getDocument(),0);



	textPane.setPreferredSize(new Dimension(800,600));
	JScrollPane scrollPane = new JScrollPane(textPane); 
        //Create an array of the text and components to be displayed.
        Object[] array = {message, scrollPane};
        //Create an array specifying the number of dialog buttons
        //and their text.
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
        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
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
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);
            if (btnString1.equals(value)) {
		typedText = textPane.getText();
		//we're done; clear and dismiss the dialog
		clearAndHide();
            } else { //user closed dialog or clicked cancel
//  		System.out.println(this.getClass().getName() + ":"+ "It's OK. We won't force you to type "
//                             + magicWord + ".");
                typedText = null;
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
//  "Message";
	HTMLViewDialog dial = new HTMLViewDialog(null, txt, "Test Title", "Enter some text\nTest.");
        dial.setVisible(true);
	System.out.println(dial.getText());
	
  	System.exit(0);
    }
}

