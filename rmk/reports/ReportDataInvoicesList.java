package rmk.reports;
import java.util.*;

import rmk.DataModel;
import rmk.ErrorLogger;
import rmk.database.dbobjects.*;

import java.text.SimpleDateFormat;
import java.text.NumberFormat;

public class ReportDataInvoicesList {
	static rmk.DataModel sys = rmk.DataModel.getInstance();
	int cols = 4;
	static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"MM/dd/yyyy");
	static final NumberFormat currencyFormat = NumberFormat
			.getCurrencyInstance();
	static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
	static final int FORMAT_BLADE_LIST = 1;
	static final int FORMAT_TAX_ORDERED = 2;
	static final int FORMAT_BALANCE_DUE = 3;
	static final int FORMAT_MINIS = 4;
	static final int FORMAT_TAX_SHIPPED = 5;
	Vector invoices = null;
	Vector listData = null;
	GregorianCalendar startDate = null;
	GregorianCalendar endDate = null;
	int format = 0;
	public Vector getInvoices() {
		return invoices;
	}
	public int getTotalListRows() throws Exception {
		if (format == FORMAT_BLADE_LIST)
			return getBladeList().size();
		if (format == FORMAT_TAX_ORDERED)
			return getTaxOrdered().size();
		if (format == FORMAT_BALANCE_DUE)
			return getBalanceDue().size();
		if (format == FORMAT_TAX_SHIPPED)
		    return getTaxShipped().size();
		//  	if(format == FORMAT_MINIS)
		//  	    return getMinis().size();
		return 0;
	}
	String getCurrentDate() {
		return dateFormatter.format(new Date());
	}
	public String[] getListRow(int row) {
		return (String[]) listData.get(row);
	}
	//------------------------------------------------------------------------
	public Vector getBladeList() throws Exception {
		cols = 5;
		if (listData != null)
			return listData;
		else
			listData = new Vector();
		Object inv[] = invoices.toArray();
		Arrays.sort(inv, new rmk.comparators.BladeList());
		int lastCustID = 0;
		int cnt = 0;
		boolean startedDealers=false;
		
		try {
			for (int i = 0; i < inv.length; i++) {
				Invoice invoice = (Invoice) inv[i];
				Customer cust = sys.customerInfo.getCustomerByID(invoice
						.getCustomerID());
				if (cust.isDealer() && !startedDealers) { // add a blank row before dealers
					String info[] = blankInfo(cols);
					for (int spacerIndex = 0; spacerIndex < cols; spacerIndex++)
						info[spacerIndex] = "======";
					listData.add(info);
					startedDealers = true;
				}
				addCustInfo(listData, (int) invoice.getCustomerID(), lastCustID);

				// VR wanted customer info ALWAYS
				//			if (invoice.getCustomerID() != lastCustID) {
				//				addCustInfo(listData, (int) invoice.getCustomerID());
				//				lastCustID = (int) invoice.getCustomerID();
				//			}
				lastCustID = (int) invoice.getCustomerID();
				cnt += addInvoiceInfo(listData, invoice);
				int custQty = addInvoiceItems(listData, invoice);
				//    	    System.out.print(sys.invoiceInfo.isDealerInvoice(invoice) + " -
				// ");

				if (cust.isDealer()) {
					String custName = "";
					if (cust.getLastName() != null)
						custName += cust.getLastName();
					if (cust.getFirstName() != null)
						custName += cust.getFirstName();
					addDealerEnd(listData, custName, custQty);
					addBlank(listData);
				}
				cnt += custQty;
			}
		} catch (Exception e) {
			ErrorLogger.getInstance().logError("loading bladelist data", e);
			throw e;
			// TODO: handle exception
		}
		
		String info[] = blankInfo(cols);
		info[0] = "Total:";
		info[1] = "" + cnt;
		listData.add(info);
		return listData;
	}
	/**
	 * @param custQty
	 */
	private void addDealerEnd(Vector results, String cust, int custQty) {
		String info[] = blankInfo(cols);
		info[0] = "<B>Total</B>";
		info[2] = "" + custQty;
		info[3] = "**" + cust;
		results.add(info);
		
	}
	public Vector getTaxOrdered() {
		cols = 9;
		if (listData != null)
			return listData;
		else
			listData = new Vector();
		Object inv[] = invoices.toArray();
		Arrays.sort(inv, new rmk.comparators.RptTaxOrdered());
		int lastCustID = 0;
		int cnt = 0;
		for (int i = 0; i < inv.length; i++) {
			Invoice invoice = (Invoice) inv[i];
			addInvoiceTaxInfo(listData, invoice);
		}
		addInvoiceTaxOrderedSummary(listData, invoices);
		return listData;
	}
	public Vector getTaxShipped() {
		cols = 9;
		percentFormat.setMinimumFractionDigits(1);
		if (listData != null && listData.size() > 0)
			return listData;
		else
			listData = new Vector();
		Object inv[] = invoices.toArray();
		Arrays.sort(inv, new rmk.comparators.RptTaxShipped());
		int lastCustID = 0;
		int cnt = 0;
		for (int i = 0; i < inv.length; i++) {
			Invoice invoice = (Invoice) inv[i];
			addInvoiceTaxShippedInfo(listData, invoice);
		}
		addInvoiceTaxShippedSummary(listData, invoices);
		return listData;
	}
	public Vector getBalanceDue() throws Exception {
		cols = 9;
		if (listData != null)
			return listData;
		else
			listData = new Vector();
		
		Vector due = getCustomerInvoicesDue(invoices);
		Object inv[] = due.toArray();
		Arrays.sort(inv, new rmk.comparators.RptTaxOrdered());
		
		int lastCustID = 0;
		int cnt = 0;
		// Headings
		String info[] = blankInfo(cols);
		info[0] = "Invoice";
		info[1] = "Due";
		info[2] = "Prefix";
		info[3] = "First";
		info[4] = "Last";
		info[5] = "Address0";
		info[6] = "Address1";
		info[7] = "Address2";
		info[8] = "CSZ";		
		listData.add(info);
		
		for (int i = 0; i < inv.length; i++) {
			Invoice invoice = (Invoice) inv[i];
			addInvoiceBalanceDueInfo(listData, invoice);
		}
		return listData;
	}
	public Vector getMinis() throws Exception {
		cols = 11;
		if (listData != null)
			return listData;
		else
			listData = new Vector();
		//  	Vector minis = sys.invoiceInfo.getMiniInvoices();
		//  	Vector miniCust = getMiniCustomers(minis);
		ErrorLogger.getInstance().logMessage(this.getClass().getName() + "Get mini Customers.");
		Vector miniCust = sys.customerInfo.getMiniCustomers();
		ErrorLogger.getInstance().logMessage(this.getClass().getName() + "Got mini Customers.");
		//  	Object inv[] = minis.toArray();
		//  	Arrays.sort(inv, new rmk.comparators.RptTaxOrdered());
		//  	int lastCustID=0;
		//  	int cnt=0;
		String info[] = blankInfo(cols);
		info[0] = "Prefix";
		info[1] = "FName";
		info[2] = "LName";
		info[3] = "Address0";
		info[4] = "Address1";
		info[5] = "Address2";
		info[6] = "City";
		info[7] = "State";
		info[8] = "Zip";
		info[9] = "Country";
		info[10] = "EMail";
		listData.add(info);
		for (int i = 0; i < miniCust.size(); i++) {
			Customer cust = (Customer) miniCust.get(i);
			addMiniCustomerInfo(listData, cust);
		}
		return listData;
	}
	//------------------------------------------------------------------------
	void addMiniCustomerInfo(Vector results, Customer cust) throws Exception {
		String info[] = blankInfo(cols);
		if (cust.getPrefix() != null && !cust.getPrefix().equals("null"))
			info[0] = cust.getPrefix();
		if (cust.getFirstName() != null && !cust.getFirstName().equals("null"))
			info[1] = cust.getFirstName();
		if (cust.getLastName() != null && !cust.getLastName().equals("null"))
			info[2] = cust.getLastName();
		Address addr = sys.customerInfo.getCurrentAddress(cust.getCustomerID());
		if (addr != null) {
			if (addr.getAddress(0) != null)
				info[3] += addr.getAddress(0);
			if (addr.getAddress(1) != null)
				info[4] += addr.getAddress(1);
			if (addr.getAddress(2) != null)
				info[5] += addr.getAddress(2);
			if (addr.getCITY() != null)
				info[6] += addr.getCITY();
			if (addr.getSTATE() != null)
				info[7] += addr.getSTATE();
			if (addr.getZIP() != null)
				info[8] += addr.getZIP();
			if (addr.getCOUNTRY() != null)
				info[9] += addr.getCOUNTRY();
			//  	    info[6] += sys.customerInfo.csz(addr);
		}
		if (cust.getEMailAddress() != null)
			info[10] += cust.getEMailAddress();
		results.add(info);
		//  	ErrorLogger.getInstance().logMessage("Added mini Customer " + cust);
	}
	//------------------------------------------------------------------------
	double getColumnTotal(int col){
	    double results=0;
	    for(int row=0; row < listData.size(); row++){
	        String[] rowData = getListRow(row);
	        String colData = BaseReport.unformatted(rowData[col]);
	        try {
		        results += (currencyFormat.parse(colData).doubleValue());
			} catch (Exception e) {
			    carpus.util.Logger.getInstance().logError("Getting column Total", e);
			}
	    }
	    return results;
	}
	
	//------------------------------------------------------------------------
	Vector getCustomerInvoicesDue(Vector invoices) throws Exception {
		Vector results = new Vector();
		for (int i = 0; i < invoices.size(); i++) {
			Invoice invoice = (Invoice) invoices.get(i);
			Customer cust = sys.customerInfo.getCustomerByID(invoice
					.getCustomerID());
			if (cust.getTerms().equals("1")) {
				double due = sys.financialInfo.getInvoiceDue(invoice);
				if (due > 0)
					results.add(invoice);
			}
		}
		return results;
	}
	void addInvoiceBalanceDueInfo(Vector results, Invoice invoice)
			throws Exception {
		String info[] = blankInfo(cols);
		int col = 0;
		Customer cust = sys.customerInfo.getCustomerByID(invoice
				.getCustomerID());
		Address addr = sys.customerInfo.getCurrentAddress(cust.getCustomerID());
		info[0] = "" + invoice.getInvoice();
		double due = sys.financialInfo.getInvoiceDue(invoice);
		info[1] = currencyFormat.format(due);
		// customer name
		if (cust.getPrefix() != null)
			info[2] += cust.getPrefix();
		if (cust.getFirstName() != null)
			info[3] += cust.getFirstName();
		if (cust.getLastName() != null)
			info[4] += cust.getLastName();
		// customer address
		if (addr != null) {
			info[5] += addr.getAddress(0);
			info[6] += addr.getAddress(1);
			info[7] += addr.getAddress(2);
			info[8] += sys.customerInfo.csz(addr);
		}
		results.add(info);
	}
	//------------------------------------------------------------------------
	void addInvoiceTaxOrderedSummary(Vector results, Vector invoices) {
		String info[] = blankInfo(cols);
		double totals[] = new double[cols];
		int totalTaxableInvoices = 0;
		int totalNonTaxableInvoices = 0;
		double totalRetail=0;
		double totalShipping=0;
		double totalDiscount=0;
		
		for (int i = 0; i < invoices.size(); i++) {
			Invoice inv = (Invoice) invoices.get(i);

			double taxAmt = sys.financialInfo.getInvoiceTaxes(inv);
			//  	if(taxAmt == 0) return;
			double discount = sys.financialInfo.getTotalInvoiceDiscount(inv);
			double nonTaxed = sys.financialInfo.getInvoiceNonTaxable(inv);			
			double retail = inv.getTotalRetail();
			double shipping = inv.getShippingAmount();
			double taxable = retail - nonTaxed;
			double nonTaxable = sys.financialInfo.getInvoiceNonTaxable(inv);
			double taxes = sys.financialInfo.getInvoiceTaxes(inv);
						
			totalRetail += retail;
			totalShipping += shipping;
			totalDiscount += discount;
			
			totals[2] += nonTaxable;
			totals[3] += taxable - discount + shipping;
			totals[5] += taxes;

			if (taxes > 0)
				totalTaxableInvoices++;
			else
				totalNonTaxableInvoices++;
		}
		for (int i = 2; i <= 5; i++)
			// totals
			info[i] = "<B>" + currencyFormat.format(totals[i]) + "</B>";
		info[cols - 1] += totalTaxableInvoices + " Taxable Invoices";
		results.add(info);
		info = blankInfo(cols);
		info[cols - 1] += totalNonTaxableInvoices + " NON Taxable Invoices";
		results.add(info);

		info = blankInfo(cols);
		info[cols - 1] += currencyFormat.format(totalRetail) + " Total Retail";
		results.add(info);
		info = blankInfo(cols);
		info[cols - 1] += currencyFormat.format(totalShipping) + " Total Shipping";
		results.add(info);
		info = blankInfo(cols);
		info[cols - 1] += currencyFormat.format(totalDiscount) + " Total Discount";
		results.add(info);
	}
	
	void addInvoiceTaxShippedSummary(Vector results, Vector invoices) {
		String info[] = blankInfo(cols);

		info[0] = ""+invoices.size() + " Iinvoices.";
		results.add(info);
	}
	
	
	String[] blankInfo(int cols) {
		String info[] = new String[cols];
		for (int i = 0; i < cols; i++)
			info[i] = "";
		return info;
	}
	//------------------------------------------------------------------------
	void addInvoiceTaxInfo(Vector results, Invoice invoice) {
		double totalPayments = sys.financialInfo.getTotalInvoicePayments(invoice.getInvoice());
		double discount = sys.financialInfo.getTotalInvoiceDiscount(invoice);
		double retail = sys.financialInfo.getTotalRetail(invoice);
		double shipping = invoice.getShippingAmount();
		double taxRate = invoice.getTaxPercentage();
		if (taxRate > 1)
			taxRate /= 100.0;
		double due = retail;
		double discPercent = invoice.getDiscountPercentage();
		double taxesDue = sys.financialInfo.getInvoiceTaxes(invoice);
		
		
		String info[] = blankInfo(cols);
		double nonTaxable = sys.financialInfo.getInvoiceNonTaxable(invoice);

		info[0] = "" + invoice.getInvoice(); // invoice
		info[1] = "" + dateFormatter.format(invoice.getDateOrdered().getTime()); // ordered date
		info[2] = currencyFormat.format(nonTaxable); // non Taxable
		if (nonTaxable > 0)			info[2] = "<B>" + info[2] + "</B>";
		info[3] = "" + currencyFormat.format(
				retail - discount + shipping); // amount due

		// taxrate
		percentFormat.setMinimumFractionDigits(1);
		info[4] = "" + percentFormat.format(taxRate);

		// tax amount
		if (taxesDue > 0) {
			info[5] = "<B>" + currencyFormat.format(taxesDue) + "</B>";
		} else {
			info[5] = "" + currencyFormat.format(taxesDue);
		}

		// shipping state
		info[6] = "" + sys.financialInfo.getShippingState(invoice);
		
		// date shipped
		if (invoice.getDateShipped() != null)
			info[7] = "" + dateFormatter.format(invoice.getDateShipped().getTime());
		
		info[8] = currencyFormat.format(totalPayments);
		
		results.add(info);
	}
	
	
	public String [] getColumnLabels(){
	    String labels[]=null;
//		if (format == FORMAT_BLADE_LIST)
//			return getBladeList().size();
		if (format == FORMAT_TAX_ORDERED)
	        labels = new String[] { "Invoice", "Ordered", "Retail", "Ret-Disc+Ship",
                "NonTaxable", "Taxes", "State", "Shipped"};
//		if (format == FORMAT_BALANCE_DUE)
//			return getBalanceDue().size();
		if (format == FORMAT_TAX_SHIPPED)
	        labels = new String[] { "Invoice", "Ordered", "Shipped", "nonTax", "Taxable",
                "Tax %", "Taxed", "Paid", "Due"};
		
		return labels;

	}
	//------------------------------------------------------------------------
	void addInvoiceTaxShippedInfo(Vector results, Invoice inv) {
		String info[] = blankInfo(cols);
		double taxAmt = sys.financialInfo.getInvoiceTaxes(inv);
		//  	if(taxAmt == 0) return;
		double discountTotal = sys.financialInfo.getTotalInvoiceDiscount(inv);
		double retail = inv.getTotalRetail();
		double shipping = inv.getShippingAmount();
		double due = sys.financialInfo.getInvoiceDue(inv);
		double nonTaxable = sys.financialInfo.getInvoiceNonTaxable(inv);
		double nonTaxableDiscounted = sys.financialInfo.getInvoiceNonTaxableAfterDiscount(inv);

		info[0] = "" + inv.getInvoice();
		if (inv.getDateOrdered() != null)
		    info[1] = "" + dateFormatter.format(inv.getDateOrdered().getTime());
		if (inv.getDateShipped() != null)
			info[2] = "" + dateFormatter.format(inv.getDateShipped().getTime());

		info[3] = "" + currencyFormat.format(nonTaxableDiscounted);

		//"total invoice???
		double taxable = (retail - discountTotal) - nonTaxable + shipping;
		info[4] = "" + currencyFormat.format(taxable);

		double taxPercentage = inv.getTaxPercentage();
		if(taxPercentage>1) taxPercentage /= 100.0;
		info[5] = ""+percentFormat.format(taxPercentage);

		double taxes = sys.financialInfo.getInvoiceTaxes(inv);
		if (taxes > 0)
			info[6] = "<B>" + currencyFormat.format(taxes) + "</B>";
		else
			info[6] = "" + currencyFormat.format(taxes);
			
		double paid = sys.financialInfo.getTotalInvoicePayments(inv.getInvoice());
		info[7] = "" + currencyFormat.format(paid);
		
//		info[8] = "" + currencyFormat.format(due +taxes - paid);
		info[8] = "" + currencyFormat.format(sys.financialInfo.getInvoiceDue(inv));

		//  	if(nonTaxable > 0)
		results.add(info);
	}
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public String getShipDate() {
		if (startDate != null)
			return dateFormatter.format(startDate.getTime());
		else
			return "";
	}
	//------------------------------------------------------------------------
	public String getShipDateShort() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMMM, yyyy");
		if (startDate != null)
			return dateFormatter.format(startDate.getTime());
		else
			return "";
	}
	//------------------------------------------------------------------------
	public void setFormat(int format) {
		this.format = format;
	}
	//------------------------------------------------------------------------
	public int getFormat() {
		return format;
	}
	//------------------------------------------------------------------------
	public void setEstimatedShipDatesRange(GregorianCalendar shipDateS,
			GregorianCalendar shipDateE) {
		if (format == 0)
			setFormat(FORMAT_BLADE_LIST);
		this.startDate = shipDateS;
		this.endDate = shipDateE;
		if (shipDateE == null) {
			int year = shipDateS.get(Calendar.YEAR);
			int month = shipDateS.get(Calendar.MONTH);
			this.endDate = new GregorianCalendar(year, month, 1);
			this.endDate.add(Calendar.MONTH, 1);
		}
		//    	ErrorLogger.getInstance().logMessage("SetShipDateS:" + sys.db.dateStr(startDate));
		//    	ErrorLogger.getInstance().logMessage("SetShipDateE:" + sys.db.dateStr(endDate));
		invoices = sys.invoiceInfo.getInvoicesByDate("dateestimated",
				startDate, endDate);
		//  	ErrorLogger.getInstance().logMessage(invoices.size() + " invoices.");
	}
	//------------------------------------------------------------------------
	public void setOrderedDate(GregorianCalendar ordered) {
		if (format == 0)
			setFormat(FORMAT_TAX_ORDERED);
		int year = ordered.get(Calendar.YEAR);
		int month = ordered.get(Calendar.MONTH);
		this.startDate = new java.util.GregorianCalendar(year, month, 1);
		this.endDate = new java.util.GregorianCalendar(year, month, 1);
		endDate.add(Calendar.MONTH, 1);
		endDate.add(Calendar.DAY_OF_MONTH, -1);
		ErrorLogger.getInstance().logMessage("Ordered:" + DataModel.db.dateStr(startDate) + ":"
				+ DataModel.db.dateStr(endDate));
		invoices = sys.invoiceInfo.getInvoicesByDate("dateOrdered", startDate,
				endDate);
		ErrorLogger.getInstance().logMessage(":invoices.size:"
				+ invoices.size());
	}
	//------------------------------------------------------------------------
	public void setShippedDate(GregorianCalendar shipped) {
		if (format == 0)
			setFormat(FORMAT_TAX_ORDERED);
		int year = shipped.get(Calendar.YEAR);
		int month = shipped.get(Calendar.MONTH);
		this.startDate = new java.util.GregorianCalendar(year, month, 1);
		this.endDate = new java.util.GregorianCalendar(year, month, 1);
		endDate.add(Calendar.MONTH, 1);
		endDate.add(Calendar.DAY_OF_MONTH, -1);
		ErrorLogger.getInstance().logMessage(this.getClass().getName() + "Shipped:" + DataModel.db.dateStr(startDate) + ":"
				+ DataModel.db.dateStr(endDate));
		invoices = sys.invoiceInfo.getInvoicesByDate("DateShipped", startDate,
				endDate);
		ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":invoices.size:"
				+ invoices.size());
	}
	
	
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	int addInvoiceInfo(Vector results, Invoice inv) {
//		String info[] = blankInfo(cols);
//		info[0] += "<I><B>" + inv.getInvoice() + "</B></I>";
//		Object items[] = inv.getItems().toArray();
		int qty = 0;
//		int cnt = 0;
//		for (int i = 0; i < items.length; i++) {
//			InvoiceEntries entry = (InvoiceEntries) items[i];
//			if (sys.partInfo.partIsBladeItem(entry.getPartID())) {
//				qty += entry.getQuantity();
//				cnt++;
//			}
//		}
//		//  	if(qty > 1) info[3] += "<I>"+qty + " knives" + "</I>";
//		//  	else info[2] += "<I>1 knife" + "</I>";
//		if (cnt >= 2)
//			results.add(info);
		return qty;
	}
	//------------------------------------------------------------------------
	int invoiceItemCount(Invoice inv) {
		Object items[] = inv.getItems().toArray();
		int cnt = 0;
		for (int i = 0; i < items.length; i++) {
			InvoiceEntries entry = (InvoiceEntries) items[i];
			if (sys.partInfo.partIsBladeItem(entry.getPartID()))
				cnt += entry.getQuantity();
		}
		return cnt;
	}
	//------------------------------------------------------------------------
	int addInvoiceItems(Vector results, Invoice inv) {
		Object items[] = inv.getItems().toArray();
		int totalQty=0;
		int COMMENT_CHARS = 30;
		Arrays.sort(items, new rmk.comparators.BladeListItems());
		for (int i = 0; i < items.length; i++) {
			String info[] = blankInfo(cols);
			InvoiceEntries entry = (InvoiceEntries) items[i];
			if (sys.partInfo.partIsBladeItem(entry.getPartID())) {
				boolean longComment = false;
//				if (items.length < 2) {
//					info[0] += "<I><B>" + inv.getInvoice() + "</B></I>";
//				} else {
//					info[0] += "" + inv.getInvoice();
//				}
				if(i==0)
					info[0] += "" + inv.getInvoice();
				else
					info[0] += "" ;
				
				info[1] = "" + entry.getQuantity();
				totalQty += entry.getQuantity();
				info[2] = sys.partInfo.getPartCodeFromID(entry.getPartID());
				
				String featureList = getInvoiceItemFeatureList(entry);

				String comment = "";
				String originalComment = entry.getComment();
				if(originalComment == null) originalComment="";
				
				// XFER certain features to comments section 
//				if(featureList != null && featureList.trim().length() > 0){
//					// if bold item, transfer to comments also
//					ArrayList parsedText = BaseReport.parseFormattedText(featureList);
//					for(int segmentIndex=0; segmentIndex< parsedText.size(); segmentIndex++){
//						FormattedText segment = (FormattedText) parsedText.get(segmentIndex);
//						boolean etchSpec = segment.text.trim().startsWith("ET");
//						if (!etchSpec && (segment.format & BaseReport.BOLD) > 0){
//							comment += "<B>" + segment.text + "</B> ";
//							if(originalComment.indexOf(segment.text.trim()) >= 0)
//								if(!etchSpec)
//									originalComment = originalComment.replaceAll(segment.text.trim(), "");
//						}
//						// if Underline item, transfer to comments also
//						// remove underline though
//						//		            !segment.text.trim().startsWith("ET") &&  
//						if ((segment.format & BaseReport.UNDERLINE) > 0) // etched Item
//							comment += segment.text;
//					}
//				}
				
				info[3] = featureList;

				if(originalComment != null && originalComment.indexOf("[") >= 0){
					originalComment = originalComment.replaceAll("\\[", "<I>");
					originalComment = originalComment.replaceAll("\\]", "</I>");
				}
				
		        comment += originalComment;
				comment = comment.replaceAll("null", "");
				comment = comment.trim();
				
//				if(comment == null) 	comment="";
				
				if (comment.trim().length() > COMMENT_CHARS) 		
					longComment = true;

				if(longComment){
				    for(Enumeration enum = ReportData.commentRows(comment,COMMENT_CHARS).elements(); enum.hasMoreElements();){
				        String commentPart = (String)enum.nextElement();
					    info[4] = ""+commentPart;
					    if(results.indexOf(info) <= 0)
					    	results.add(info);
				    	info = blankInfo(cols);
				    }
				} else{
					info[4] = comment;
					results.add(info);
				}
			}
			if(info[4].indexOf("<I>") > 0)
				ErrorLogger.getInstance().logMessage("Debug");
		}
		return totalQty;
	}
	//------------------------------------------------------------------------
	String getInvoiceItemFeatureList(InvoiceEntries entry) {
		String results = "";
		Object items[] = entry.getFeatures().toArray();
		Arrays.sort(items, new rmk.comparators.Features());
		for (int i = 0; i < items.length; i++) {
			String info[] = blankInfo(cols);
			InvoiceEntryAdditions feature = (InvoiceEntryAdditions) items[i];
			String partCode = sys.partInfo.getPartCodeFromID(feature
					.getPartID());
			if(feature.getPrice() == 0){
			    partCode = partCode.toLowerCase();
			}

			if (sys.partInfo.partIsSheath(feature.getPartID()))
				partCode = bold(partCode);
			if (partCode.equals("RDH") 
					|| partCode.equals("LS1") || partCode.equals("LS2") || partCode.equals("LS3")
					|| partCode.equals("NP") || partCode.equals("NPB") || partCode.equals("NPN") )
				partCode = bold(partCode);
			if (sys.partInfo.partIsNamePlate(feature.getPartID()))
				partCode = bold(partCode);
			if (sys.partInfo.partIsEtching(feature.getPartID()))
				partCode = bold(partCode);
			results += partCode + " ";
		}
		results = results.replaceAll("  ", " ");
		return results.trim();
	}
	String bold(String str){
		return "<B>" + str + "</B>";
	}
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	void addBlank(Vector results) {
		String info[] = blankInfo(cols);
		results.add(info);
	}
	//------------------------------------------------------------------------
	void addCustInfo(Vector results, int custID, int lastCustID) throws Exception {
		String info[] = blankInfo(cols);
		Customer cust = sys.customerInfo.getCustomerByID(custID);
		if (cust.isDealer()) {
			info[0] = "Dealer";
			String name = "";
			if (cust.getLastName() != null)
				name += cust.getLastName().trim();
			if (name.trim().length() > 0)
				name += ",";
			if (cust.getFirstName() != null)
				name += cust.getFirstName().trim();
			if (name.endsWith(","))
				name = name.substring(0, name.length() - 1);
			if(custID == lastCustID)
			    name += " (cont.)";
			info[3] = "<B>" + name + "</B>";
			
			if (cust.getBladeList() != null)
				info[4] = "<B><I>" + cust.getBladeList() + "</I></B>";
			results.add(info);
		}
	}
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public static void main(String args[]) throws Exception {
		//  	int fontHeight = 10;
		rmk.DataModel sys = rmk.DataModel.getInstance();
		ReportDataInvoicesList blData = new ReportDataInvoicesList();
		GregorianCalendar date = new java.util.GregorianCalendar(
				2004, 5, 3);
		//    	java.util.GregorianCalendar date = new
		// java.util.GregorianCalendar(2004, 0, 8);
		blData.setFormat(FORMAT_TAX_ORDERED); // FORMAT_TAX_SHIPPED FORMAT_MINIS
											  // FORMAT_TAX_ORDERED
		blData.setEstimatedShipDatesRange(date, null);
//		blData.setOrderedDate(date);
		ErrorLogger.getInstance().logMessage("Rows:" + blData.getTotalListRows());
		// getBladeList getBalanceDue getTaxShipped, getTaxOrdered getMinis
		Vector data = blData.getTaxOrdered();
		//  	ErrorLogger.getInstance().logMessage(invoices.size());
		//  	ErrorLogger.getInstance().logMessage(data.size());
		for (Enumeration enum = data.elements(); enum.hasMoreElements();) {
			String info[] = (String[]) enum.nextElement();
			for (int i = 0; i < info.length; i++) {
				System.out.print(info[i] + "|");
				//  		System.out.print(info[i] + ".. ");
			}
			System.out.print("\n");
		}
	}
}