package rmk.database;

import rmk.DataModel;
import rmk.ErrorLogger;
import rmk.database.dbobjects.*;

import java.util.Calendar;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Arrays;

import carpus.util.Formatting;

public class InvoiceInfo {
	static final boolean logHistory = true;

	Hashtable invoicesByID = new Hashtable();

	static carpus.database.DBInterface db;

	//    Vector customerInvoices;
	Customer lastCustomer = null;

	long lastInvoice = 0;

	//==========================================================
	public InvoiceInfo(carpus.database.DBInterface db) {
		if (InvoiceInfo.db != null)
			return;
		InvoiceInfo.db = db;
		//		java.util.GregorianCalendar start = new GregorianCalendar();
		//		start.set(Calendar.HOUR_OF_DAY, 1);
		//		Vector hist = db.getItems("HistoryItems", "DateStamp >= " +
		// db.dateStr(start) + " order by DateStamp " );
		//		if(hist != null && hist.size() > 0){
		//			hist.trimToSize();
		//			HistoryItems lastHist = (HistoryItems) hist.lastElement();
		//			lastInvoice = lastHist.getInvoice();
		//		}
	}

	/**
	 * @param int -
	 *            Invoice number
	 * @return Vector - Invoice Entries
	 */
	public Vector getInvoiceItem(int id) {
		InvoiceEntries entry;
		entry = (InvoiceEntries) db.getItems("InvoiceEntries",
				InvoiceEntries.fields[0] + " =" + id).get(0);

		entry.setFeatures(getInvoiceEntryAdditions(entry.getInvoiceEntryID()));
		Vector lst = new Vector();
		lst.add(entry);
		return lst;
	}

	public void logInvoiceAccess(Invoice inv) {
		long currInv = inv.getInvoice();
		if (currInv == lastInvoice)
			return; // don't log repeats

		if (logHistory && inv.getInvoice() > 0) {
			HistoryItems hist = new HistoryItems(0);
			hist.setInvoice(inv.getInvoice());
			//  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ hist);
			Vector lst = new Vector();
			lst.add(hist);
			db.saveItems("HistoryItems", lst);
			lastInvoice = currInv;
		}
	}

	public Vector getInvoiceEntries(long invNum) {
		Vector items = db.getItems("InvoiceEntries", "Invoice=" + invNum);
		if (items == null)
			return items;
		PartInfo partInfo = new PartInfo(db);

		for (java.util.Enumeration enum = items.elements(); enum
				.hasMoreElements();) {
			InvoiceEntries item = (InvoiceEntries) enum.nextElement();
			String desc = partInfo.getPartCodeFromID(item.getPartID());
			item.setPartType((int) partInfo.getPartTypeFromID((int) item
					.getPartID()));
			item.setPartDescription(desc);
			item.markSaved();
		}

		return items;
	}

	public long getInvoiceNumberFromEntryID(int id) {
		return Long.parseLong(db.lookup("InvoiceEntries", "Invoice", "" + id));
	}

	public Vector savePayments(Vector invPayments){
		db.saveItems("Payments", invPayments);
		return invPayments;
	}
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public Vector getMiniInvoices() {
		String criteria = "";
		criteria += "PONumber = '@'";
		criteria += "or PONumber = '#'";
		criteria += "or PONumber = '%'";

		Vector items = db.getItems("Invoices", criteria);
		return items;
	}

	//------------------------------------------------------------------------
	public Vector getDealerSpecInvoices(java.util.GregorianCalendar date) {
		java.util.GregorianCalendar start = (GregorianCalendar) date.clone();
		java.util.GregorianCalendar end = (GregorianCalendar) date.clone();
		start.set(Calendar.DAY_OF_MONTH, 1);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);

		end.set(Calendar.DAY_OF_MONTH, 1);
		end.add(Calendar.MONTH, 1);
		end.add(Calendar.DAY_OF_MONTH, -1);
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);

		String dateField = "dateestimated";
		String criteria = dateField
				+ " >= "
				+ db.dateStr(start)
				+ " and "
				+ dateField
				+ " <= "
				+ db.dateStr(end)
				+ " and customerID in (Select customerid from Customers where dealer <> 0)"
				+ " and totalRetail = 0 order by " + dateField + ", Invoice";
		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + criteria);

		Vector results = new Vector();
		results = db.getItems("Invoice", criteria);

		return results;
	}

	//------------------------------------------------------------------------
	public Vector getBalanceDueInvoices(java.util.GregorianCalendar date) {
		Vector results = new Vector();
		Vector invoices = getInvoicesByEstimatedAndTerms(date, 1);

		FinancialInfo finance = new FinancialInfo(db);
		for (java.util.Enumeration enum = invoices.elements(); enum
				.hasMoreElements();) {
			Invoice inv = (Invoice) enum.nextElement();
			double due = finance.getInvoiceDue(inv);
			if (due > 0) {
				results.add(inv);
			}
		}
		return results;
	}

	//------------------------------------------------------------------------
	public Vector getInvoicesByEstimatedAndTerms(
			java.util.GregorianCalendar date, int terms) {
		String dateField = "DateEstimated";
		String criteria = dateField
				+ " = "
				+ db.dateStr(date)
				+ " and CustomerID in (Select CustomerID from Customers where Terms = '"
				+ terms + "')";
		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + criteria);

		Vector results = db.getItems("Invoice", criteria);
		return results;
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	public Invoice getInvoiceFromEntryID(int id) {
		long invoiceNumber = getInvoiceNumberFromEntryID(id);
		Vector items = db.getItems("Invoices", "Invoice=" + invoiceNumber);
		if (items.size() > 0)
			return (Invoice) items.get(0);
		return null;
	}

	//==========================================================
	public int getKnifeCount(long invoice) {
		int results = 0;
		Vector entries = getInvoiceEntries(invoice);
		for (java.util.Enumeration enum = entries.elements(); enum
				.hasMoreElements();) {
			InvoiceEntries entry = (InvoiceEntries) enum.nextElement();
			results += entry.getQuantity();
		}
		return results;
	}

	public Vector getKnifeCounts(java.util.GregorianCalendar date) {
		java.util.Hashtable resultsHash = new java.util.Hashtable();
		Vector results = new Vector();

		rmk.DataModel sys = rmk.DataModel.getInstance();
		Vector invoices = DataModel.db.getItems("Invoice", "DateEstimated = "
				+ db.dateStr(date));

		if (invoices == null) {
			carpus.database.Logger dbLogger = carpus.database.Logger
					.getInstance();
			dbLogger.logMessage("No Invoices for EstimatedDate "
					+ db.dateStr(date));

			return null;
		}

		for (java.util.Enumeration enum = invoices.elements(); enum
				.hasMoreElements();) {
			Invoice inv = (Invoice) enum.nextElement();
			Vector items = getInvoiceEntries(inv.getInvoice());
			for (java.util.Enumeration enum2 = items.elements(); enum2
					.hasMoreElements();) {
				InvoiceEntries item = (InvoiceEntries) enum2.nextElement();
				// key Description
				if (sys.partInfo.partIsBladeItem(item.getPartID())) {
					String key = ""
							+ sys.partInfo.getPartCodeFromID(item.getPartID());
					int qty = item.getQuantity();

					Integer n = (Integer) resultsHash.get(key);
					if (n != null) {
						qty += n.intValue();
					}
					n = new Integer(qty);
					resultsHash.put(key, n);
				}
			}
		}
		int total = 0;
		//	carpus.util.Formatting formatter = new carpus.util.Formatting();
		results.add(Formatting.textSizer("For Week", 25) + "  "
				+ db.dateStr(date));
		for (java.util.Enumeration enum = resultsHash.keys(); enum
				.hasMoreElements();) {
			String key = (String) enum.nextElement();
			Integer cnt = (Integer) resultsHash.get(key);
			//  	    results.add( formatter.textSizer(""+cnt, 5) + "- " +
			// formatter.textSizer(key, 25));
			total += cnt.intValue();
		}
		//  	results.add("---------------");
		results.add(Formatting.textSizer("Total Knives", 25) + "  " + total);

		return results;
	}

	//==========================================================
	public Vector getInvoiceEntryAdditions(long entryID) {
		Vector items = db.getItems("InvoiceEntryAdditions", "EntryID ="
				+ entryID);
		PartInfo partInfo = new PartInfo(db);

		for (java.util.Enumeration enum = items.elements(); enum
				.hasMoreElements();) {
			InvoiceEntryAdditions item = (InvoiceEntryAdditions) enum
					.nextElement();
			item.setPartType((int) partInfo.getPartTypeFromID((int) item
					.getPartID()));
		}
		Object[] entries = items.toArray();
		Arrays.sort(entries, new rmk.comparators.InvoiceEntryAdditions());

		//  	return items;

		Vector results = new Vector();
		//  	for(java.util.Enumeration enum=items.elements();
		// enum.hasMoreElements();){
		//  	    InvoiceEntryAdditions item =
		// (InvoiceEntryAdditions)enum.nextElement();
		for (int i = 0; i < entries.length; i++) {
			InvoiceEntryAdditions item = (InvoiceEntryAdditions) entries[i];
			results.add(item);
		}
		return results;
	}

	public boolean removeInvoiceEntryAndAdditions(long entryID) {
		try {
			db.execute("Delete from InvoiceEntryAdditions where EntryID  = "
					+ entryID);
			db.execute("Delete from InvoiceEntries where invoiceEntryID = "
					+ entryID);
			return true;
		} catch (java.sql.SQLException e) {
			carpus.database.Logger dbLogger = carpus.database.Logger
					.getInstance();
			dbLogger.logError("" + e.getMessage()
					+ " while  removeInvoiceEntryAndAdditions" + entryID, e);

		}
		//  	return db.getItems("InvoiceEntryAdditions", "EntryID =" + entryID);
		return false;
	}

	public boolean removeAdditionID(long additionID){
		try {
			db.execute("Delete from InvoiceEntryAdditions where AdditionID  = "
					+ additionID);
			return true;
		} catch (java.sql.SQLException e) {
			carpus.database.Logger
			.getInstance().logError("" + e.getMessage()
					+ " while  removeInvoiceEntryAndAdditions" + additionID, e);

		}
		//  	return db.getItems("InvoiceEntryAdditions", "EntryID =" + entryID);
		return false;
	}
	
	
	//==========================================================
	public boolean isDealerInvoice(Invoice invoice) {
		boolean dealer = false;

		Boolean boolDeal = invoice.isDealer();
		if (boolDeal != null){
			dealer = boolDeal.booleanValue();
		} else{ 
			CustomerInfo customerInfo = new CustomerInfo(db);
			dealer = customerInfo.isDealer(invoice.getCustomerID());
		}
		return dealer;
	}

	//==========================================================
	public int getPricingYear(Invoice invoice) {
		return getPricingYear(invoice, isDealerInvoice(invoice));
	}
	public int getPricingYear(long invoiceNumber) {
		Invoice invoice = getInvoice(invoiceNumber);
		return getPricingYear(invoice, isDealerInvoice(invoice));
	}

	public static int getPricingYear(Invoice invoice, boolean dealer) {
		String dateUsed="Ordered";
		java.util.GregorianCalendar date = invoice.getDateOrdered();
		if (dealer){
			date = invoice.getDateEstimated();
			dateUsed="Estimated";
		}if (date == null){
			date = invoice.getDateOrdered(); // fall back to estimatedDate
			dateUsed="Ordered";
		}

		int year = date.get(Calendar.YEAR);
//		ErrorLogger.getInstance().logMessage("InvoiceInfo" + "Inv (" + invoice.getInvoice() + ") year:" + year + " Dealer?:" + dealer + " date used=" + dateUsed);
		return year;
	}

	public int getInvoiceYearOrdered(int invoice) {
		String lookup = db.lookup("Invoices", "DateOrdered", "" + invoice);
		if (lookup != null) {
			lookup = lookup.substring(0, 4);
			//  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() +
			// ":getInvoiceYearOrdered()"+ lookup);
			return Integer.parseInt(lookup);
		}
		return 9999;
	}

	//==========================================================
	//==========================================================
	Vector getCustomerInvoices(Customer customer) {
		if (lastCustomer != null
				&& customer.getCustomerID() == lastCustomer.getCustomerID())
			return lastCustomer.getInvoices();

		Vector customerInvoices = db.getItems("Invoice", "customerid = "
				+ customer.getCustomerID());
		lastCustomer = customer;
		customer.setInvoices(customerInvoices);

		return customerInvoices;
	}

	//==========================================================
	public Vector getPendingInvoices(Customer customer) {
		Vector results = new Vector();
		Vector invoices = getCustomerInvoices(customer);

		for (int i = 0; i < invoices.size(); i++) {
			Invoice item = (Invoice) invoices.get(i);
			if (item.getDateShipped() == null)
				results.add(item);
		}
		return results;
	}

	public Vector getInvoicesByDate(String dateField, GregorianCalendar start,
			GregorianCalendar end) {
		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + dateField + ":"
				+ db.dateStr(start));
		String whereClause = dateField + " >= " + db.dateStr(start) + " and "
				+ dateField + " <= " + db.dateStr(end);
		Vector results = db.getItems("Invoice", whereClause);

		if(results == null) return null;
		for (Enumeration enum = results.elements(); enum.hasMoreElements();) {
			Invoice inv = (Invoice) enum.nextElement();
			Vector entries = getInvoiceEntries(inv.getInvoice());
			for (Enumeration enum2 = entries.elements(); enum2
					.hasMoreElements();) {
				InvoiceEntries entry = (InvoiceEntries) enum2.nextElement();
				entry.setFeatures(getInvoiceEntryAdditions(entry
						.getInvoiceEntryID()));
			}
			inv.setItems(entries);
		}
		//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ results.size());

		return results;
	}

	//==========================================================
	public Vector getShippedInvoices(Customer customer) {
		Vector results = new Vector();
		Vector invoices = getCustomerInvoices(customer);

		for (int i = 0; i < invoices.size(); i++) {
			Invoice item = (Invoice) invoices.get(i);
			if (item.getDateShipped() != null)
				results.add(item);
		}
		return results;
	}

	//==========================================================
	public Invoice getInvoice(long id) {
		Vector invoiceVect = db.getItems("Invoice", "Invoice =" + id);
		if (invoiceVect.size() <= 0)
			return null;
		return (Invoice) invoiceVect.get(0);
	}

	//==========================================================
	public Vector validate(Invoice inv) { // returns errors
		Vector errors = new Vector();
		GregorianCalendar chkDate = inv.getDateShipped();
		if (chkDate != null) // Dont do validation if already shipped as per
							 // Shop Meet Wed, 14 Jul 2004
			return null;

		//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ inv.lst());
		//  	if(inv.getDateEstimated() == null) errors.add("Missing
		// DateEstimated.");
		GregorianCalendar today = new GregorianCalendar();
		today.add(Calendar.WEEK_OF_YEAR, -1);
		chkDate = inv.getDateOrdered();
		if (chkDate == null)
			errors.add("Missing Ordered Date.");
		else {
			int dow = chkDate.get(Calendar.DAY_OF_WEEK);
			if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY)
				errors.add("Ordered Date is weekend.");
		}

		// estimated date is NOT required
		chkDate = inv.getDateEstimated();
		if (chkDate != null && chkDate.before(today))
			errors.add("Invalid Estimated Date, Before today (within 1 week)");

		if (chkDate != null) {
			int dow = chkDate.get(Calendar.DAY_OF_WEEK);
			if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY)
				errors.add("Estimated Date is weekend.");
		}

		if (errors.size() == 0)
			return null;
		return errors;
	}

	//      public Vector getShippedInvoices(int customerID, int yearsBack){

	//      }
	//==========================================================
	public Vector getLastShippedInvoices(Customer customer, int cnt) {
		Vector results = new Vector();
		Vector invoices = getCustomerInvoices(customer);

		if (invoices != null) {
			Object[] lst = getShippedInvoices(customer).toArray();
			java.util.Arrays.sort(lst, new rmk.comparators.InvoiceComparator());
			if (cnt > lst.length)
				cnt = lst.length;

			for (int i = 0; i < cnt; i++) {
				results.add((Invoice) lst[i]);
			}
		}
		return results;
	}

	//==========================================================
	public Vector getInitialInvoices(Customer customer) {
		Vector results = null;
		//  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+
		// "getInitialInvoices");

		//  	if(customerInvoices == null ||
		// ((Invoice)customerInvoices.get(0)).getCustomerID() != customerID){
		results = getLastShippedInvoices(customer, 2);
		results.addAll(getPendingInvoices(customer));
		//  	}
		return results;
	}

	public void mergeCustomers(Customer correctCustomer,
			Customer incorrectCustomer) {
		Vector invoices = getCustomerInvoices(incorrectCustomer);
		for (Enumeration invList = invoices.elements(); invList
				.hasMoreElements();) {
			Invoice inv = (Invoice) invList.nextElement();
			inv.setCustomerID(correctCustomer.getCustomerID());
		}
		db.saveItems("Invoice", invoices);
	}

	//==========================================================
	//==========================================================
	//==========================================================
	public static void main(String args[]) throws Exception {
		rmk.DataModel sys = rmk.DataModel.getInstance();
		//  	ErrorLogger.getInstance().logMessage(sys.invoiceInfo.getLastShippedInvoices(7,2));
		//  	ErrorLogger.getInstance().logMessage(sys.invoiceInfo.getShippedInvoices(7));
		//  	ErrorLogger.getInstance().logMessage(sys.invoiceInfo.getInvoiceEntryAdditions(112952));
		//  	ErrorLogger.getInstance().logMessage(sys.invoiceInfo.getKnifeCount(50004));
		Invoice inv = sys.invoiceInfo.getInvoice(41859);
		// 41859, 42496, 50101, 50111 42514, 42511, 60001
		//  	ErrorLogger.getInstance().logMessage(sys.invoiceInfo.isDealerInvoice(inv));
		//  	ErrorLogger.getInstance().logMessage(sys.invoiceInfo.getPricingYear(inv,true));

		//  	ErrorLogger.getInstance().logMessage(sys.invoiceInfo.getPricingYear(inv));

		java.util.GregorianCalendar date = new java.util.GregorianCalendar();
		while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
			date.add(Calendar.DATE, 1);
		//  	ErrorLogger.getInstance().logMessage(date);
		//  	date.add(date.DATE,14);
		date.add(Calendar.MONTH, 4);
		//  	ErrorLogger.getInstance().logMessage(sys.invoiceInfo.getInvoicesByDate("dateestimated",
		// date, date));
		//  	Vector data = sys.invoiceInfo.getDealerSpecInvoices(date);

		Vector data = sys.invoiceInfo.getInvoiceEntryAdditions(132172);

		//  	Vector data = sys.invoiceInfo.getBalanceDueInvoices(date);
		//  	sys.invoiceInfo.logInvoiceAccess(inv);
		//  	sys.invoiceInfo.logInvoiceAccess(inv);

		//  	Vector data = sys.invoiceInfo.getMiniInvoices();

		for (Enumeration enum = data.elements(); enum.hasMoreElements();) {
			//  	    Invoice invoice = (Invoice )enum.nextElement();
			//  	    ErrorLogger.getInstance().logMessage(invoice + ":" + invoice.getCustomerID());
			InvoiceEntryAdditions item = (InvoiceEntryAdditions) enum
					.nextElement();
			ErrorLogger.getInstance().logMessage(item + ":" + item.getPartType() + " : "
					+ sys.partInfo.getPartDescFromID(item.getPartID()));
		}

	}

	/**
	 * @param int
	 *            days
	 * @return Vector - HistoryItems
	 */
	public Vector getHistory(int days) {
		CustomerInfo custInfo = new CustomerInfo(db);
		InvoiceInfo invInfo = new InvoiceInfo(db);

		java.util.GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.DATE, -days);
		String criteria = "DateStamp >= " + db.dateStr(start)
				+ " order by DateStamp ";
		Vector historyItems = db.getItems("HistoryItems", criteria);
		if (historyItems == null)
			return new Vector();
		Enumeration enum = historyItems.elements();
		while (enum.hasMoreElements()) {
			HistoryItems item = (HistoryItems) enum.nextElement();
			try {
				Invoice inv = invInfo.getInvoice(item.getInvoice());
				Customer cust = custInfo.getCustomerByID(inv.getCustomerID());
				String name = cust.getLastName();
				if (name == null)
					name = "";
				if (cust.getFirstName() != null)
					name += "," + cust.getFirstName();
				item.setCustomerName(name);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return historyItems;
	}

}