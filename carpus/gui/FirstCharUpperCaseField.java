package carpus.gui;

import javax.swing.*;
import javax.swing.text.*;

public class FirstCharUpperCaseField extends JTextField {
 
    public FirstCharUpperCaseField(int cols, String txt) {
	super(cols);
	setText(txt);
    }
    public FirstCharUpperCaseField(int cols) {
	super(cols);
    }
 
    protected Document createDefaultModel() {
	return new FirstCharUpperCaseDocument();
    }
 
    static class FirstCharUpperCaseDocument extends PlainDocument {
 
	public void insertString(int offs, String str, AttributeSet a) 
	    throws BadLocationException {
 
	    if (str == null || str.length() == 0) {
		return;
	    }
	    
	    char[] upper = str.toCharArray();
	    String original=getText(0,offs);
//  	    System.out.println(this.getClass().getName() + ":" + offs + ":" + original + ":" + str);
	    if(offs == 0)
		upper[0] = Character.toUpperCase(upper[0]);
	    super.insertString(offs, new String(upper), a);

	}
    }
}

