package carpus.gui;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.beans.*; //property change stuff
import java.awt.*;
import java.awt.event.*;
public class NoteDialog extends JDialog implements
 //  				      ActionListener,
		PropertyChangeListener {
	
	public static final int MAX_LEN_NOTES_NONE=-1; // NONE

	private String typedText = null;
	private JTextArea textPane;
	private String defaultText;
	//      private String magicWord="Blah";
	private JOptionPane optionPane;
	private String btnString1 = "Enter";
	private String btnString2 = "Cancel";
	int maxCharacters = 0;
	boolean onlyWarnOnMax=false;
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
	public NoteDialog(Frame aFrame, String defaultText, String title,
			String message, int maxCharacters, boolean onlyWarnOnMax) {
		super(aFrame, true);
		this.maxCharacters = maxCharacters;
		this.defaultText = defaultText;
		this.onlyWarnOnMax = onlyWarnOnMax;
		create(defaultText, title, message, maxCharacters);
	}
	void create(String defaultText, String title, String message, int maxLen) {
		this.defaultText = defaultText;
		
		setTitle(title);
		if (defaultText == null)
			defaultText = "";
		defaultText = defaultText.replace('|', '\n');
		defaultText = defaultText.replace((char)141, '\n');
		textPane = new JTextArea(defaultText);
//        textPane.setText(defaultText);
		if(maxLen != MAX_LEN_NOTES_NONE){
		    Document doc;
		    if(onlyWarnOnMax ){
		        doc= new WarnLengthDocument(maxLen);
		    } else{
		        doc = new MaxLengthDocument(maxLen);
		    }
	        textPane.setDocument(doc);
	        textPane.setText(defaultText);
		}
		textPane.setPreferredSize(new Dimension(900, 900));
		//  	JPanel pnl = new JPanel();
		//  	pnl.setPreferredSize(new Dimension(300,200));
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setPreferredSize(new Dimension(400, 200));
		//Create an array of the text and components to be displayed.
		Object[] array = {message, scrollPane};
		//Create an array specifying the number of dialog buttons and their text.
		Object[] options = {btnString1, btnString2};
		//Create the JOptionPane.
		optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION, null, options, options[0]);
		//Make this dialog display it.
		setContentPane(optionPane);
		//Handle window closing correctly.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				/* Instead of directly closing the window,
				 * we're going to change the JOptionPane's value property.
				 */
				optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
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
				&& (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
						.equals(prop))) {
			Object value = optionPane.getValue();
			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				//ignore reset
				return;
			}
			//Reset the JOptionPane's value. If you don't do this, then if the user
			//presses the same button next time, no property change event will be fired.
			optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
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
	public static void main(String args[]) throws Exception {
		String message = "TestMessage|MultipleLine";
		NoteDialog dial = new NoteDialog(null, message, "Test Title",
				"Enter some text\nTest.", 50, true);
		dial.setVisible(true);
		System.out.println(dial.getText());
		System.exit(0);
	}
}
class MaxLengthDocument extends PlainDocument {
	private int max;
	// create a Document with a specified max length
	public MaxLengthDocument(int maxLength) {		max = maxLength;	}
	
	// don't allow an insertion to exceed the max length
	public void insertString(int offset, String str, AttributeSet a)
			throws BadLocationException {
		if (getLength() + str.length() > max)
			java.awt.Toolkit.getDefaultToolkit().beep();
		else
			super.insertString(offset, str, a);
	}
}
class WarnLengthDocument extends PlainDocument {
	private int max;
	// create a Document with a specified max length
	public WarnLengthDocument(int maxLength) {		max = maxLength;	}
	
	// don't allow an insertion to exceed the max length
	public void insertString(int offset, String str, AttributeSet a)
			throws BadLocationException {
		if (str != null && getLength() + str.length() > max){
			java.awt.Toolkit.getDefaultToolkit().beep();
		}
		// ALWAYS insert
		super.insertString(offset, str, a);
	}
}