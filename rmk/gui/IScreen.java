package rmk.gui;
import java.awt.event.*;


public interface IScreen {
    boolean isEdited();
//      Vector getData();
    public void addActionListener(ActionListener listener);    
    public boolean bringToFront();
    public void setData(DBGuiModel model);
	public void setVisible(boolean val);
	public void toFront();
	public void grabFocus();	
}
