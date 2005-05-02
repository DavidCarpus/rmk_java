package rmk.gui;
import rmk.database.dbobjects.DBObject;


public interface IScreen {
    boolean isEdited();
//      Vector getData();
//    public void addActionListener(ActionListener listener);    
	public void processCommand(String command, Object from);

    public boolean bringToFront();
    public void setData(DBGuiModel model);
	public void setVisible(boolean val);
	public void toFront();
	public void grabFocus();
	public void updateOccured(DBObject itemChanged, int changeType, DBObject parentItem);
	public void buttonPress(int button, int id);
}
