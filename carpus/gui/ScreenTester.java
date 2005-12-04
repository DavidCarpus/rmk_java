package carpus.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ScreenTester extends JDialog implements ActionListener{
    public boolean exitOnCancel = false;

    public ScreenTester(JPanel panel){
	JScrollPane scrollPane = new JScrollPane(panel);

	GridBagLayout gridBag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	getContentPane().setLayout(gridBag);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        gridBag.setConstraints(scrollPane, c);

	getContentPane().add(scrollPane);

	BasicToolBar buttonBar = new BasicToolBar(null, new String[] {"Print", "Cancel"}, 
				     new String[] {"Print", "Cancel"},
				     new String[] {"Print", "Cancel"});

	buttonBar.setFloatable(false);
//  	buttonBar.enableButton(0,false);
//	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
//      	buttonBar.registerKeyboardAction(this, "Cancel", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

	buttonBar.addActionListener(this);
	buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));
	getContentPane().add(buttonBar);

//     	rpt.setPreferredSize(new Dimension(450,650));
  	panel.setPreferredSize(new Dimension(550,710));

	setTitle("Screen Tester:" + panel.getClass().getName());
	setModal(true);
  	scrollPane.setPreferredSize(new Dimension(555,715));
	pack();
    }

    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand().toUpperCase().trim();

        if (command.equals("CANCEL")) { //cancel
            this.setVisible(false);
	    if (exitOnCancel)  System.exit(0);

	}
    }

//    public static void main(String args[]) throws Exception{
//	JPanel pnl = new rmk.gui.InvoiceItem();
//	ScreenTester tstr = new ScreenTester(pnl);
//	tstr.exitOnCancel=true;
//	tstr.setVisible(true);
//    }

}

