/*
 * Created on Apr 29, 2005
 */
package rmk;

import java.util.Enumeration;
import java.util.Vector;

import rmk.database.dbobjects.DBObject;
import rmk.gui.IScreen;


/**
 * @author David
 *
 */
public class SignalProcessor {
	private static SignalProcessor instance=new SignalProcessor();	
	private Vector screens = null;
	
	private SignalProcessor(){
		screens = new Vector();
	}
	
	public static SignalProcessor getInstance(){
		return instance;	
	}
	
	public void addScreen(IScreen screen){
		IScreen check;
//		for(Enumeration list = screens.elements();list.hasMoreElements();){
//			check = (IScreen) list.nextElement();
//			if(check != null){
//				return;
//			} else{
//				
//			}
//		}
		screens.add(screen);
	}
	public void removeScreen(IScreen screen){
		screens.remove(screen);
	}
	
	public void notifyUpdate(IScreen fromScreen, DBObject itemChanged, int changeType, DBObject parentItem){
		IScreen screen;
		for(Enumeration list = screens.elements();list.hasMoreElements();){
			screen = (IScreen) list.nextElement();
			if(screen != null && screen != fromScreen){
				screen.updateOccured(itemChanged, changeType, parentItem);
			}
		}	
	}
}
