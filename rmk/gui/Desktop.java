package rmk.gui;

import javax.swing.*;

public class Desktop 
    extends JDesktopPane
{
    private static Desktop instance  = new Desktop(); 
    private static JFrame jframe=null;
    private java.awt.Image image=null;

    private Desktop(){ 
      super();
      if(!Configuration.Config.IDE){
	  image = java.awt.Toolkit.getDefaultToolkit().getImage(Configuration.Config.getBackgroundImageLocation());
      }
    }
    public static Desktop getInstance(){ return instance;}

    public void paintComponent(java.awt.Graphics g) {
	super.paintComponent(g);
	int width = getWidth();
	int height = getHeight();
	if(image != null)
	    g.drawImage( image, 0, 0, width, height, null ); 
    }   

    public void setFrame(JFrame frame){
		jframe = frame;
    }

    public java.awt.Frame getFrame(){
	return jframe;
    }

    public JComponent add(JComponent comp){
	super.add(comp);
	Screen screen = (Screen)comp;
	ApplicationMenu.getInstance().addScreenToWindowMenu(screen);
	return comp;
    }

	public void remove(JComponent comp) {
		super.remove(comp);
		ApplicationMenu.getInstance().removeScreenFromWindowMenu((Screen) comp);
		ApplicationMenu.getInstance().goToLastScreen();
	}

    public static void main(String args[]) throws Exception{
	Application.main(args);
    }

}

