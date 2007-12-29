package rmk.gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;

public class ApplicationMenu 
extends JMenuBar
implements ActionListener
{
    private Vector listeners = new Vector();
    private static ApplicationMenu instance = new ApplicationMenu();
    private JMenu windowMenu;
    private Vector screens = new Vector();
    //===================================================
    public void addScreenToWindowMenu(JComponent screen){
    	screens.add(screen);
    	addWindowMenuItem(((Screen)screen).getTitle());
    }
    //===================================================
	public void removeScreenFromWindowMenu(Screen screen) {
		// remove from screens vector
		Screen screenToRemove=null;
		for (Iterator<Screen> iter = screens.iterator(); iter.hasNext();) {
			Screen currScreen = (Screen) iter.next();
			if (screen == currScreen) {
				screenToRemove = currScreen;
			}
		}
		if(screenToRemove != null)	screens.removeElement(screenToRemove);
	// remove from menu
		removeWindowMenuItem(screen.getTitle());
		removeWindowMenuItem(screen.getTitle());
		
	// remove menu if empty
	if(screens.size() == 0)  windowMenu.setVisible(false);
    }
	
	void removeWindowMenuItem(String title){
		for(int itemIndex = windowMenu.getItemCount()-1; itemIndex>=0; itemIndex--){
			JMenuItem menuItem = windowMenu.getItem(itemIndex);
			String text = menuItem.getText();
			if(text.equalsIgnoreCase(title)){
				windowMenu.remove(menuItem);
				break;
			}
		}		
	}
	void addWindowMenuItem(String title){
		JMenuItem menuItem = new JMenuItem(title);
		menuItem.setActionCommand("Window-" + title);
		menuItem.addActionListener(this);
		windowMenu.add(menuItem);
		windowMenu.setVisible(true);
	}

    //===================================================
    //===================================================
    private ApplicationMenu(){
        JMenu menu1 = new JMenu("Screen");
        menu1.setMnemonic(KeyEvent.VK_D);
        add(menu1);

        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new");
        menuItem.addActionListener(this);
        menu1.add(menuItem);

        menuItem = new JMenuItem("Ship Invoices");
        menuItem.setMnemonic(KeyEvent.VK_F12);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F12, 0));
//          menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                  KeyEvent.VK_F12, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("ship");
        menuItem.addActionListener(this);
        menu1.add(menuItem);

        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(this);
        menu1.add(menuItem);

        menuItem = new JMenuItem("Query Invoices");
        menuItem.setMnemonic(KeyEvent.VK_R);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_V, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("INVOICE_SEARCH");
        menuItem.addActionListener(this);
        menu1.add(menuItem); 
        
        menuItem = new JMenuItem("Preferences");
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("Preferences");
        menuItem.addActionListener(this);
        menu1.add(menuItem); 
        
	// -------------------------------
        JMenu menu3 = new JMenu("Reports");
        menu3.setMnemonic(KeyEvent.VK_R);
        add(menu3);

        menuItem = new JMenuItem("Blade List");
        menuItem.setMnemonic(KeyEvent.VK_1);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("bladelist");
        menuItem.addActionListener(this);
        menu3.add(menuItem);

		menuItem = new JMenuItem("Sales Tax Ordered Report");
		menuItem.setMnemonic(KeyEvent.VK_2);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_2, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("taxordered");
		menuItem.addActionListener(this);
		menu3.add(menuItem);
        
		menuItem = new JMenuItem("Sales Tax Shipped Report");
		menuItem.setMnemonic(KeyEvent.VK_3);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_3, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("taxshipped");
		menuItem.addActionListener(this);
		menu3.add(menuItem);
        
		menuItem = new JMenuItem("Part List Report");
		menuItem.setMnemonic(KeyEvent.VK_4);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_4, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("partlistreport");
		menuItem.addActionListener(this);
		menu3.add(menuItem);
        
		

        menuItem = new JMenuItem("Dealer Spec Request Data");
        menuItem.setActionCommand("SPEC_REQUEST");
        menuItem.addActionListener(this);
        menu3.add(menuItem);

        menuItem = new JMenuItem("Balance Due Data");
        menuItem.setActionCommand("BALANCE_DUE");
        menuItem.addActionListener(this);
        menu3.add(menuItem);

        menuItem = new JMenuItem("Invoices by scheduled shipdate");
        menuItem.setActionCommand("MULTI_INVOICE");
        menuItem.addActionListener(this);
        menu3.add(menuItem);

        menuItem = new JMenuItem("Wierd Invoices with due $");
        menuItem.setActionCommand("WIERD_INVOICES");
        menuItem.addActionListener(this);
        menu3.add(menuItem);

        

	// -------------------------------
	windowMenu=new JMenu("Window");
        windowMenu.setMnemonic(KeyEvent.VK_W);
	windowMenu.setVisible(false); // initially invisible
        add(windowMenu);
        
        String name = carpus.util.SystemPrefrences.getName();
        if(name == null) name = "";
        name = name.toLowerCase();
        if (name.equals("ordrc") || name.endsWith("carpus") ) {
            JMenuItem testMenu = new JMenuItem("Test");
            testMenu.setActionCommand("TEST");
            testMenu.addActionListener(this);
            add(testMenu);
        }
//        testMenu.setMnemonic(KeyEvent.VK_W);
    }
    //===================================================
    public void actionPerformed(ActionEvent e) {
    	String command = e.getActionCommand().toUpperCase().trim();
    	if(command.startsWith("WINDOW-")){
    		String title = command.substring("WINDOW-".length());
    		Screen screen = findScreen(title);
    		if(screen != null){
    			try {
    				screen.grabFocus();
    				screen.toFront();
    				screen.setSelected(true);
    				screen.setIcon(false);
    			} catch (Exception except){} // end of try-catch
    		} else{ // screen should not be on list, remove it
    			removeWindowMenuItem(title);
    		}
    	} else{
    		//  	    notifyListeners(e);
    		if(listeners == null) return;
    		for(Iterator<ActionListener> iter=listeners.iterator(); iter.hasNext();){
    			((ActionListener)iter.next()).actionPerformed(e);
    		}
    	}
    }
   //===================================================
	public Screen findScreen(String title) {
		for (Iterator<Screen> iter = screens.iterator(); iter.hasNext();) {
			Screen screen = (Screen) iter.next();
			if (screen.getTitle().toUpperCase().startsWith(title.toUpperCase())) {
				return screen;
			}
		}
		return null;
	}
   //===================================================
	public void addActionListener(ActionListener listener) {
		if (listeners == null)
			listeners = new Vector();
		if (!listeners.contains(listener))
			listeners.addElement(listener);
	}
    //===================================================
	public static ApplicationMenu getInstance() {
		return instance;
	}
	public int goToLastScreen() {
		int lastScreen = screens.size() - 1;
		if (!windowMenu.isVisible())
			return lastScreen;

		Screen screen = (Screen) screens.get(lastScreen);
		screen.toFront();
		screen.grabFocus();
		try {
			screen.setSelected(true);
			screen.setIcon(false);
		} catch (Exception except) {
		} // end of try-catch
		
		return lastScreen;
    }
	public void notifyLastScreen(int buttonPress, int id){
		int lastScreen = screens.size() - 1;
		if (!windowMenu.isVisible())
			return;

		Screen screen = (Screen) screens.get(lastScreen);
		screen.buttonPress(buttonPress, id);
	}
	
    public void pushScreenToTopOfStack(Screen screen){
    	if(screens.size() <=0 ){
    		screens.add(screen);
    		return;
    	}
    	screens.remove(screen);
    	screens.add(screen);
    	
    	String title = ((Screen)screen).getTitle();
    	removeWindowMenuItem(title);
    	addWindowMenuItem(title);
    	
//    	ErrorLogger.getInstance().logMessage("Screen:" + screen.getTitle() + " is not in STACK.");
    }
    public void updateScreenTitle(String from, String newTitle){
		removeWindowMenuItem(from);
		addWindowMenuItem(newTitle);
    }
    
    public boolean unsavedScreens(){
		for (Iterator<Screen> iter = screens.iterator(); iter.hasNext();) {
			Screen currScreen = (Screen) iter.next();
			if (currScreen.isEdited()) {
				return true;
			}
		}
		return false;
    }

}
