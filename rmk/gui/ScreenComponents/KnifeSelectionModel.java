package rmk.gui.ScreenComponents;

//===============================================================
//===============================================================
class KnifeSelectionModel extends SingleSelectionModel {
//===============================================================
//-----------------------------------------------------------------
    public KnifeSelectionModel(int id){
	super(id);
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
} 
//===============================================================
