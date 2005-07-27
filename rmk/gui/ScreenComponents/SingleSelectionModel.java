package rmk.gui.ScreenComponents;

import javax.swing.*;


//===============================================================
//===============================================================
class SingleSelectionModel extends DefaultListSelectionModel {
//===============================================================
    int id;
//-----------------------------------------------------------------
    public SingleSelectionModel() {
    }
    
    public SingleSelectionModel(int id) {
	this.id = id;
	setSelectionMode(SINGLE_SELECTION);
    }
	public void setSelectionInterval(int index0, int index1) {
		int oldIndex = getMinSelectionIndex();
		super.setSelectionInterval(index0, index1);
		int newIndex = getMinSelectionIndex();
		if (oldIndex != newIndex) {
			updateSingleSelection(oldIndex, newIndex);
		}
	}

    public void updateSingleSelection(int oldIndex, int newIndex) {
    }
} 
//===============================================================
