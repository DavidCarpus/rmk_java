package carpus.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;

public class LabeledTextField extends JPanel {

    final JTextComponent field;

    JLabel label;

    public LabeledTextField(String label, JTextComponent field, String txt) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.label = new JLabel(label);
        this.field = field;
        this.field.setFont(new Font("Serif", Font.BOLD, 14));
        this.label.setFont(new Font("Serif", Font.BOLD, 14));
        this.field.addFocusListener(new SelectAllText(field));

        add(this.label);

        add(field);

    }

    public JTextComponent getField() {
        return field;
    }

    public void processFocusEvent(FocusEvent e) {
        field.requestFocus();
    }

    public void requestFocus() {
        field.requestFocus();
    }

    public void grabFocus() {
        field.grabFocus();
    }

    public LabeledTextField(String label, JTextField field) {
        this(label, field, "");
    }

    public void setValue(String value) {
        field.setText(value);
    }

    public void setValue(double value) {
        ((JFormattedTextField) field).setValue(new Double(value));
    }

    public String getValue() {
        return field.getText();
    }

    public void addKeyListener(KeyListener listener) {
        this.field.addKeyListener(listener);
    }

}

