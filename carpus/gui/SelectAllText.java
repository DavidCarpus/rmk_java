package carpus.gui;

import java.awt.event.*;
import javax.swing.text.*;

class SelectAllText implements FocusListener{
    JTextComponent field;
    SelectAllText(JTextComponent field){this.field = field;  }    
    public void focusGained(FocusEvent e) {this.field.selectAll(); }
    public void focusLost(FocusEvent e) {this.field.select(0,0); };
}
