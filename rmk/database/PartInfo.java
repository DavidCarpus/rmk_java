package rmk.database;

import rmk.database.dbobjects.*;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;

public class PartInfo {
	static Hashtable parts = null;
	//      static double currPartPrices[];
	static public Hashtable partTypes = null;
	static final int MODEL_PART_TYPE = 10;
	static public carpus.database.DBInterface db;
//	static public final int currentYear = 0;
//		(new java.util.GregorianCalendar()).get(
//			java.util.GregorianCalendar.YEAR);
	static rmk.DataModel sys = rmk.DataModel.getInstance();
	//==========================================================
	//==========================================================
	public PartInfo(carpus.database.DBInterface dbase) {
		if(db != null) return;
		db = dbase;
	}
	//==========================================================
	//==========================================================
	public boolean validPartType(int knife, int part) {
		return true;
		//    	return ((knife/100) == (part/100));
	}
	//==========================================================
	public long largestPartID() {
		long results = 0;
		getParts();
		for (java.util.Enumeration<Parts> iter = parts.elements(); iter.hasMoreElements();) {
			Parts part = (Parts) iter.nextElement();
			results = Math.max(part.getPartID(), results);
		}
		return results;
	}
	//==========================================================
	public int getPartTypeFromID(int partID) {
		getParts();
		for (java.util.Enumeration<Parts> iter = parts.elements(); iter.hasMoreElements();) {
			Parts part = (Parts) iter.nextElement();
			if (part.getPartID() == partID)
				return part.getPartType();
		}
		return 0;
	}

	//=============================================PartTable =============
	public Parts getPart(long id) {
		getParts();
		return (Parts) parts.get(new Long(id));
	}
	public Parts getPartFromCode(String code) {
	    if(code == null) code="";
		long id = getPartIDFromCode(code.toUpperCase());
		return (Parts) parts.get(new Long(id));
	}
	public Parts getPartFromPartialCode(String code) {
		long id = getPartIDFromPartialCode(code.toUpperCase());
		return (Parts) parts.get(new Long(id));
	}
	//==========================================================
	public boolean partIsDiscountable(long id) {
		Parts part = getPart(id);
		if (part != null)
			return part.isDiscountable();
		else
			return false;
	}
	//==========================================================
	public boolean partIsTaxable(long id) {
		Parts part = getPart(id);
		if (part != null)
			return part.isTaxable();
		else
			return false;
	}
	//==========================================================
	public boolean partIsBladeItem(long id) {
		Parts part = getPart(id);
		if (part != null)
			return part.isBladeItem();
		else
			return false;
	}
	public boolean partIsSheath(long id) {
		Parts part = getPart(id);
		if (part != null)
			return part.isSheath() || part.getPartCode().equalsIgnoreCase("FCH");
		else
			return false;
	}
	public boolean partIsNamePlate(long id) {
		Parts part = getPart(id);
		if (part == null)
			return false;
		if (part.getPartCode().equals("NP"))
			return true;
		if (part.getPartCode().equals("NPB"))
			return true;
		if (part.getPartCode().equals("NPN"))
			return true;
		return false;
	}

	public boolean partIsEtching(long id) {
		Parts part = getPart(id);
		if (part == null)
			return false;
		if (part.getPartCode().equals("ET1"))
			return true;
		if (part.getPartCode().equals("ET2"))
			return true;
		return false;
	}

	//==========================================================
	public String getPartDescFromID(long id) {
		Parts part = getPart(id);
		if (part != null)
			return part.getDescription();
		else
			return id + ":";
	}
	//==========================================================
	public int getPartIDFromCode(String partCode) {
		getParts();
		for (java.util.Enumeration<Parts> iter = parts.elements(); iter.hasMoreElements();) {
			Parts part = (Parts) iter.nextElement();
			if (partCode.equals(part.getPartCode()))
				return (int) part.getPartID();
		}
		return 0;
	}
	public int getPartIDFromPartialCode(String partCode) {
		getParts();
		partCode = partCode.trim();
		String match="";
		int matchID=0;
		for (java.util.Enumeration<Parts> iter = parts.elements(); iter.hasMoreElements();) {
			Parts part = (Parts) iter.nextElement();
			String realPartCode = part.getPartCode();
			if (realPartCode.startsWith(partCode) && part.isActive()){
				if(match.length() > 0) 
					return 0; // if multiple matches, fail
				match = realPartCode;
				matchID=(int) part.getPartID();
			}
		}
		return matchID;
	}
	
	//==========================================================
	public String getPartCodeFromID(long id) {
		Parts part = getPart(id);
		if (part != null)
			return part.getPartCode();
		else
			return id + ":";
	}
	//==========================================================
	public Vector getParts(int partType) {
		getParts();
		Vector lst = new Vector();
		for (java.util.Enumeration<Parts> iter = parts.elements(); iter.hasMoreElements();) {
			Parts part = (Parts) iter.nextElement();
			if (part.getPartType() == partType)
				lst.add(part);
		}
		return lst;
	}
	//==========================================================
	//==========================================================
	public Vector getPartTypes() {
		Vector lst = new Vector();
		getPartTypesEnum();
		for (int i = 0; i < 9999; i++) {
			PartTypes type = getPartType(i);
			if (type != null)
				lst.add(type);
		}
		return lst;
	}
	public java.util.Enumeration<PartTypes> getPartTypesEnum() {
		if (partTypes == null) {
			partTypes = new Hashtable();
			Vector lst =
				db.getItems("PartTypes", "PartTypeID >0 order by PartTypeID");
			if (lst != null) {
				for (Iterator iter = lst.iterator();
					iter.hasNext();
					) {
					PartTypes type = (PartTypes) iter.next();
					//  		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ type);

					partTypes.put(type.getID(), type);
				}
			}
		}
		return partTypes.elements();
	}
	public PartTypes getPartType(int id) {
		getPartTypesEnum();
		return (PartTypes) partTypes.get(new Long(id));
	}
	public int partTypeCnt() {
		getPartTypesEnum();
		return partTypes.size();
	}

	public int mainPartTypeCnt() {
		return 5;
	}
	//==========================================================
	public String getPartTypeDesc(int id) {
		PartTypes type = getPartType(id);
		if (type == null)
			return "Unknown";
		return type.getDescription();
		//  	if(id >=1 && id <= 5) return Parts.partTypes[id-1];
		//  	if(id == 99) return "Misc";
		//  	return "Unknown";
	}
	public int getPartTypeID(String desc) {
		for (Enumeration<PartTypes> partTypes = getPartTypesEnum(); partTypes.hasMoreElements();) {
			PartTypes type = (PartTypes) partTypes.nextElement();
			String typeDesc = type.getDescription();
			if(typeDesc == null)
				typeDesc = "";	
			if (desc.toUpperCase().equals(typeDesc.toUpperCase()))
				return (int) type.getPartTypeID();
		}
		//  	if(desc.equals("Knives") ) return 1;
		//  	if(desc.equals("Blades") ) return 2;
		//  	if(desc.equals("Handles") ) return 3;
		//  	if(desc.equals("Hilt") ) return 4;
		//  	if(desc.equals("Butt") ) return 5;
		//  	if(desc.equals("Misc") ) return 99;
		return 0;
	}
	//==========================================================
	public java.util.Enumeration<Parts> getParts() {
		if (parts == null) {
			parts = new Hashtable();
			Vector lst = db.getItems("Parts", "partid >0 order by partid");
			for (Iterator iter = lst.iterator(); iter.hasNext();) {
				Parts part = (Parts) iter.next();
				parts.put(part.getID(), part);
			}
		}
		return parts.elements();
	}
	
	public void newPart(Parts part){
	   if( parts != null && part.getPartID() > 0){
		   parts.put(part.getID(), part);
	   }
	}

	//==========================================================
	public Vector getKnifeOptions() {
		
		Vector lst = new Vector();
		for (Enumeration<Parts> parts = getParts(); parts.hasMoreElements();) {
			Parts part = (Parts) parts.nextElement();
			if (part.getPartType() != MODEL_PART_TYPE
				&& part.getPartType() != 99) // Model,Misc
				lst.add(part);
		}
		return lst;
	}
	//==========================================================
	public Vector getKnives() {
		Vector lst = new Vector();
		for (Enumeration<Parts> parts = getParts(); parts.hasMoreElements();) {
			Parts part = (Parts) parts.nextElement();
			if (part.getPartType() == MODEL_PART_TYPE)
				lst.add(part);
		}
		return lst;
	}
	//==========================================================
	public static Vector getPartsOfType(int type, Vector data) {
		Vector results = new Vector();
		for (int partIndex = 0; partIndex < data.size(); partIndex++) {
			Parts part = (Parts) data.get(partIndex);
			if (part.getPartType() == type) {
				results.add(part);
			}
		}
		return results;
	}
	//=================================================================================
	//=================================================================================
	public Vector getPartCodesFromString(String enteredString) {
		String seperator = ".";
		Vector partCodes = new Vector();
		String partCode = "";
		//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + "enteredString:" + enteredString);
		if (enteredString.indexOf(",") > 0)
			seperator = ",";

		while (enteredString != null && enteredString.indexOf(seperator) > 0) {
			//  	    ErrorLogger.getInstance().logMessage("enteredString:" + enteredString);
			partCode =
				enteredString.substring(0, enteredString.indexOf(seperator));
			enteredString =
				enteredString.substring(enteredString.indexOf(seperator) + 1);
			partCodes.add(partCode);
		}
		partCode = enteredString;
		partCodes.add(partCode);

		return partCodes;
	}
	//--------------------------------------------------------------------------------
	public Vector getPartsFromPartCodeVector(Vector partCodes) {
		Vector parts = new Vector();
		for (java.util.Iterator iter = partCodes.iterator(); iter.hasNext();
			) {
//			Parts part = getPartFromPartialCode((String) iter.next());
			Parts part = getPartFromCode((String) iter.next());
			parts.add(part);
		}
		return parts;
	}
	//==========================================================
	//==========================================================
}
