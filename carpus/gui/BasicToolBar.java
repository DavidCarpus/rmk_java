package carpus.gui;

import java.awt.*;
import javax.swing.*;

import java.util.Vector;
//import java.util.Enumeration;
import java.util.Iterator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BasicToolBar extends JToolBar implements ActionListener 
{

    String[] imageFileNames;

    String[] labels;

    Vector listeners;

    static final Insets margins = new Insets(0, 0, 0, 0);

    Vector buttons;

    public BasicToolBar(String[] fileNames, String[] labels, String[] cmdStr,
            String[] toolTips) {
        setFloatable(false);
        imageFileNames = fileNames;
        this.labels = labels;
        buttons = new Vector();
        setLayout(new FlowLayout(FlowLayout.LEFT));
        for (int i = 0; i < labels.length; i++) {
            if (fileNames == null)
                addButton("", labels[i], cmdStr[i], toolTips[i]);
            else
                addButton(fileNames[i], labels[i], cmdStr[i], toolTips[i]);
        }
        setMaximumSize(new Dimension(925, 50));
    }

    public JButton getButton(int index) {
        return (JButton) getComponentAtIndex(index);
    }

    public void addButton(String fileName, String label, String cmdStr,
            String toolTip) {
        JButton button;
        if (fileName == null || fileName.equals(""))
            button = new JButton(label);
        else
            button = new ToolBarButton("images/" + imageFileNames);
        buttons.add(button);

        button.setToolTipText("" + toolTip);
        button.setMargin(margins);
        add(button);
        String[] newLabels = new String[labels.length +1];
        for(int i=0; i< labels.length; i++)
        	newLabels[i] = labels[i];
        newLabels[labels.length] = label;
        labels = newLabels;
        
        button.addActionListener(this);
        button.setActionCommand(cmdStr);
    }

    public String getButtonLabel(int buttonIndex) {
        return labels[buttonIndex];
    }

    public String setButtonLabel(int buttonIndex, String newLabel) { // returns
                                                                     // oldLabel
        String old = getButtonLabel(buttonIndex);
        labels[buttonIndex] = newLabel;
        ((JButton) buttons.get(buttonIndex)).setText(newLabel);
        return old;
    }

    public void enableButton(int buttonIndex, boolean value) {
        Component c;
        if ((c = getComponentAtIndex(buttonIndex)) != null) {
            JButton button = (JButton) c;
            button.setEnabled(value);
        }
    }

    public boolean enabled(int buttonIndex) {
        Component c;
        if ((c = getComponentAtIndex(buttonIndex)) != null) {
            JButton button = (JButton) c;
            return button.isEnabled();
        }
        return false;
    }

    public void setTextLabels(boolean labelsAreEnabled) {
        Component c;
        int i = 0;
        while ((c = getComponentAtIndex(i++)) != null) {
            ToolBarButton button = (ToolBarButton) c;
            if (labelsAreEnabled)
                button.setText(button.getToolTipText());
            else
                button.setText(null);
        }
    }

    public void addActionListener(ActionListener listener) {
        if (listeners == null) listeners = new Vector();
        if (!listeners.contains(listener)) listeners.addElement(listener);
    }

    public void actionPerformed(ActionEvent e) {
        if (listeners != null) {
            for (Iterator iter = listeners.iterator(); iter.hasNext();) {
                ((ActionListener) iter.next()).actionPerformed(e);
            }
        }
    }
}

