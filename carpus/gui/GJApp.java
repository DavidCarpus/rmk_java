package carpus.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GJApp extends WindowAdapter {
    static private JPanel statusArea = new JPanel();
    static private JLabel status = new JLabel(" ");
    static private ResourceBundle resources;

    public static void launch(final JFrame f, String title,
			      final int x, final int y, 
			      final int w, int h) {
	launch(f,title,x,y,w,h,null);	
    }
    public static void launch(final JFrame f, String title,
			      final int x, final int y, 
			      final int w, int h,
			      String propertiesFilename) {
	f.setTitle(title);
	f.setBounds(x,y,w,h);
	f.setVisible(true);

	statusArea.setBorder(BorderFactory.createEtchedBorder());
	statusArea.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
	statusArea.add(status);
	status.setHorizontalAlignment(JLabel.LEFT);

	f.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE);

	if(propertiesFilename != null) {
	    resources = ResourceBundle.getBundle( propertiesFilename, Locale.getDefault());
	}

	f.addWindowListener(new WindowAdapter() {
		public void windowClosed(WindowEvent e) {
  		    System.exit(0);
		}
	    });
    }
    static public JPanel getStatusArea() {
	return statusArea;
    }
    static public void showStatus(String s) {
	status.setText(s);
    }
    static Object getResource(String key) {
	if(resources != null) {
	    return resources.getString(key);
	}
	return null;
    }
}
