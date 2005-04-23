package rmk.gui;
import java.util.Vector;


public class DBGuiModel extends rmk.DBModel{

    public DBGuiModel(int dataType, Vector lst){
	setData(dataType, lst);
    }

    public DBGuiModel(){
	super();
    }
    public String toString(){
	String results="";
	for(int i=0; i< rmk.DBModel.CNT; i++){
	    if(getData(i) != null)
		results += getData(i) + "\n";
	}
	return results;
    }

//      public static void main(String args[]) throws Exception{
//  	ErrorLogger.getInstance().logMessage(new DBGuiModel());
//      }

}
