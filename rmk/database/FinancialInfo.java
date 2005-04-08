package rmk.database;

import javax.swing.*;

import rmk.ErrorLogger;
import rmk.database.dbobjects.*;

import java.util.Calendar;
import java.util.Vector;
import java.util.GregorianCalendar;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class FinancialInfo {
	static final int INV_ENTRY_ID_LAST_XFERED=140451;
	public static boolean INVOICE_DUE_CUTOFF=true;
	public static final int VCODE_LENGTH=3; 

    static carpus.database.DBInterface db = null;

    //==========================================================
    public FinancialInfo(carpus.database.DBInterface dbParam) {
        if (db != null) return;
        db = dbParam;
    }

    //==========================================================
    //==========================================================
    public Payments getPayment(long id) {
        Vector paymentVect = db.getItems("Payments", "Payment =" + id);
        return (Payments) paymentVect.get(0);
    }

    //------------------------------------------------------------------
    public Vector getInvoicePayments(long invoice) {
        return db.getItems("Payments", "Invoice = " + invoice);
    }

    public Vector getInvoiceEntries(Invoice invoice) {
        Vector entries = invoice.getItems();
        if (entries == null || entries.size() == 0) {
            InvoiceInfo invoiceInfo = new InvoiceInfo(db);
            entries = invoiceInfo.getInvoiceEntries(invoice.getInvoice());
            invoice.setItems(entries);
        }
        return entries;
    }

    //==========================================================
    //==========================================================
    public boolean recomputeInvoiceRetail(Invoice invoice) {
        boolean changed = false;
        CustomerInfo customerInfo = new CustomerInfo(db);
        int year = InvoiceInfo.getPricingYear(invoice, customerInfo
                .isDealer(invoice.getCustomerID()));
        int invPrice = 0;
        //  	System.out.println(this.getClass().getName() + ":year:"+ year);

        Vector entries = getInvoiceEntries(invoice);

        for (java.util.Enumeration enum = entries.elements(); enum
                .hasMoreElements();) {
            InvoiceEntries entry = (InvoiceEntries) enum.nextElement();
            if (recomputeInvoiceEntryRetail(entry, year, true)) changed = true;
        }
        return changed;
    }

    public void addFeaturesToEntryPrice(InvoiceEntries entry) throws Exception{
    	if(entry.getID().longValue() > 0) throw new Exception("Should not be called with non-new entry");
        Vector additions = entry.getFeatures();
        double price = entry.getPrice();
        for (java.util.Enumeration enum = additions.elements(); enum
                .hasMoreElements();) {
        	InvoiceEntryAdditions feature = (InvoiceEntryAdditions) enum.nextElement();
        	price += feature.getPrice();
        }
        entry.setPrice(price);
    }
    
    public boolean recomputeInvoiceEntryRetail(InvoiceEntries entry,
            int priceYear, boolean updateFeaturePrices) {
        boolean changed = false;

        double entryRetail = 0;
        double price = 0;
        PartPriceTable partPrices = rmk.DataModel.getInstance().pricetable;
        price =  partPrices.getPartPrice(priceYear, (int) entry.getPartID());

        entryRetail += price;
        System.out.println(this.getClass().getName() + "Base :" + entry + ": " + price);
        //    	System.out.println(this.getClass().getName() + ":"+ price);

        Vector additions = entry.getFeatures();
        if (additions == null) {
            InvoiceInfo invoiceInfo = new InvoiceInfo(db);
            additions = invoiceInfo.getInvoiceEntryAdditions(entry
                    .getInvoiceEntryID());
            entry.setFeatures(additions);
        }

        for (java.util.Enumeration enum = additions.elements(); enum
                .hasMoreElements();) {
            InvoiceEntryAdditions feature = (InvoiceEntryAdditions) enum
                    .nextElement();
            price = partPrices.getPartPrice(priceYear, (int) feature
                    .getPartID());
            if (updateFeaturePrices && price != feature.getPrice() && price > 0) {
                feature.setPrice(price);
                System.out.println(this.getClass().getName() + "Feature :" + feature + ": " + price);
            }
            entryRetail += feature.getPrice();
        }
        entryRetail *= entry.getQuantity();
        if (entryRetail != entry.getPrice()) {
            //  	    System.out.println(this.getClass().getName() + ":"+ entry + ":
            // N:" + entryRetail);
            entry.setPrice(entryRetail);
            changed = true;
        }

        return changed;
    }

    public static double roundDollarAmt(double val) {
        return Math.floor(val * 100 + 0.5) / 100;
    }

    //==========================================================
    //==========================================================
    public double getInvoiceTaxes(Invoice invoice) {
        double retail = getTotalRetail(invoice);
        double discount = getTotalInvoiceDiscount(invoice);
        double shipping = invoice.getShippingAmount();
        double taxes = invoice.getTaxPercentage();
        double nonTaxable = getInvoiceNonTaxable(invoice);

        if (taxes > 1) taxes /= 100.0;

        double taxesDue = (retail - nonTaxable - discount + shipping) * taxes;
        double results = Math.floor(taxesDue * 100 + 0.5) / 100.0;
        return results;
    }

    //------------------------------------------------------------------
    public double getInvoiceNonTaxable(Invoice invoice) {
        Vector entries = getInvoiceEntries(invoice);
        double total = 0;

        PartInfo partInfo = new PartInfo(db);
        for (java.util.Enumeration enum = entries.elements(); enum
                .hasMoreElements();) {
            InvoiceEntries entry = (InvoiceEntries) enum.nextElement();
            if (!partInfo.partIsTaxable(entry.getPartID()))
                    total += entry.getPrice();
        }
        return total;
    }
    //------------------------------------------------------------------
    public double getInvoiceNonTaxableAfterDiscount(Invoice invoice) {
        Vector entries = getInvoiceEntries(invoice);
        double total = 0;

        PartInfo partInfo = new PartInfo(db);
        for (java.util.Enumeration enum = entries.elements(); enum
                .hasMoreElements();) {
            InvoiceEntries entry = (InvoiceEntries) enum.nextElement();
            if (!partInfo.partIsTaxable(entry.getPartID())){
                double price= entry.getPrice();
                double discount = invoice.getDiscountPercentage();
                if(discount > 1) discount /= 100.0;
                discount = 1.0-discount;
                total += price * discount;
            }
        }
        return total;
    }

    //------------------------------------------------------------------
    public double getTotalInvoiceDiscount(Invoice invoice) {
        double discount = invoice.getDiscountPercentage();
        if (discount > 1) discount /= 100;

        double total = 0;

        Vector items = getInvoiceEntries(invoice);
        if (items == null) return 0;

        PartInfo partInfo = new PartInfo(db);

        for (Enumeration enum = items.elements(); enum.hasMoreElements();) {
            InvoiceEntries item = (InvoiceEntries) enum.nextElement();
            boolean discounted = false;
            

            if(INV_ENTRY_ID_LAST_XFERED > item.getInvoiceEntryID()){
            	//did we override it in old system?
            	discounted=item.isDiscounted();
            } else{
            	discounted= partInfo.partIsDiscountable(item.getPartID());
            }
        
            if (discounted) {
                total += item.getPrice() * discount;
            } else {
                total += 0;
            }
        }
        return total;
    }

    //------------------------------------------------------------------
    public double getInvoiceDue(int invoiceNumber) {
        InvoiceInfo invoiceInfo = new InvoiceInfo(db);
        return getInvoiceDue(invoiceInfo.getInvoice(invoiceNumber));
    }

    /**
     * Returns the total retail amount for given invoices Computes it from the
     * invoices items, updates DB if different than stored value in invoice
     * 
     * @param Invoice
     *            invoice
     * @return Double total Retail of invoice
     */
    public double getTotalRetail(Invoice invoice) {
        double total = 0;
        if (invoice == null) return 0;
        InvoiceInfo invoiceInfo = new InvoiceInfo(db);
        Vector items = invoice.getItems();
        if (items == null) {
            items = invoiceInfo.getInvoiceEntries(invoice.getInvoice());
            if (items == null) items = new Vector();
            invoice.setItems(items);
            //  	    System.out.println(this.getClass().getName() + ":"+ "got
            // items");
        }
        for (Enumeration enum = items.elements(); enum.hasMoreElements();) {
            InvoiceEntries item = (InvoiceEntries) enum.nextElement();
            total += item.getPrice();
        }
        double diff = invoice.getTotalRetail() - total;
        diff = Math.floor(diff * 100 + 0.5) / 100;
        if (diff != 0) { // round to 1 cent
            total = Math.floor(total * 100 + 0.5) / 100;
            //  	    System.out.println(this.getClass().getName() + ":" + "update db
            // - Invoice.totalretail $" + total );
            try {
                db.execute("update Invoices set TotalRetail = " + total
                        + " where Invoice = " + invoice.getInvoice());
                boolean edited = invoice.isEdited();
                invoice.setTotalRetail(total);
                if (!edited) invoice.markSaved();
            } catch (Exception e) {
                System.out.println(this.getClass().getName() + ":ERR:" + e);
            } // end of try-catch
        }
        //  	System.out.println(this.getClass().getName() + ":"+
        // invoice.getTotalRetail());

        return total;
    }

    /**
     * Returns the total due for given invoice Accounts for payments,
     * discounts, and taxes
     * 
     * @param Invoice
     *            invoice
     * @return Double totalDue
     */
    public double getInvoiceDue(Invoice invoice) {
    	// if shipped more than 2 years ago force it to $0
    	// as per gary 11-18-2004
    	if(INVOICE_DUE_CUTOFF){ // toggled above to allow for creating list
    		GregorianCalendar cutoff = new GregorianCalendar();
    		cutoff.add(GregorianCalendar.YEAR, -3);
    		if(invoice.getDateShipped() != null 
    				&& cutoff.after(invoice.getDateShipped())
    		)
    			return 0;
    	}
    			
		double totalPayments = getTotalInvoicePayments(invoice.getInvoice());
		double discount = getTotalInvoiceDiscount(invoice);
		double retail = getTotalRetail(invoice);
		double shipping = invoice.getShippingAmount();
		double due = retail;

		double discPercent = invoice.getDiscountPercentage();
		due -= discount;
		due += shipping;

		double taxesDue = getInvoiceTaxes(invoice);
		due += taxesDue;
		due -= totalPayments;
		return roundDollarAmt(due);
    }

    //------------------------------------------------------------------
    public void removePayment(long paymentID) {
        try {
            db.execute("Delete from Payments where PaymentID  = " + paymentID);
        } catch (Exception e) {
            rmk.ErrorLogger.getInstance().logError(
                    "Deleting payment(" + paymentID + ")", e);
        } // end of try-catch
    }

    //------------------------------------------------------------------
    public double getTotalInvoicePayments(long invoice) {
        double totalPayments = 0;
        Vector payments = getInvoicePayments(invoice);
        if (payments != null) {
            for (Enumeration enum = payments.elements(); enum.hasMoreElements();) {
                Payments payment = (Payments) enum.nextElement();
                totalPayments += payment.getPayment();
            }
        }
        return totalPayments;
    }

    //==========================================================
    //==========================================================
    public static boolean isTaxableState(String state) {
        return state.equals("FL");
    }

    //------------------------------------------------------------------
    public static double getTaxRateForState(String state) {
        if (!isTaxableState(state)) return 0;
        if (state.equals("FL")) {
            double rate = Configuration.Config.getFLTaxRate(); // 6.5% default
            if (rate > 1) rate /= 100.0;
            return rate;
        }
        JOptionPane.showMessageDialog(null, "Unknown tax rate:" + state);
        return 0;
    }

    //------------------------------------------------------------------
    public double getInvoiceTaxRate(Invoice invoice) {
        CustomerInfo cust = new CustomerInfo(db);
        if (cust.currentTaxIDonFile(invoice.getCustomerID())) { return 0; }
        if (invoice.isShopSale()) return getTaxRateForState("FL");

        double percentage = invoice.getTaxPercentage();
        //  	System.out.println(this.getClass().getName() + ":"+ percentage);
        if (percentage > 1) percentage /= 100; // make percentage == .xx
        if (percentage != 0) return percentage;

        String state = getShippingState(invoice);
        percentage = getTaxRateForState(state);
        if (percentage > 1) percentage /= 100; // make percentage == .xx
        return percentage;
    }

    //==========================================================
    //==========================================================
    public String getLastCreditCard(long invoiceNum, long customerID) {
        Vector dbPayments = null;
        Payments lastCCPayment=null;
        dbPayments = db.getItems("Payments", db
                .lenCriteria("checknumber")
                + ">=9"
                + " and  Invoice ="
                + invoiceNum
                + " order by PaymentDate desc");
        if(dbPayments != null && dbPayments.size() > 0){            
            return ((Payments) dbPayments.get(0)).getCheckNumber();
        }
        InvoiceInfo invDB = new InvoiceInfo(db);
        Invoice invoice = invDB.getInvoice(invoiceNum);
        if(invoice == null)
            return "";
        if(invoice.getCreditCardNumber() != null){
            String cc = invoice.getCreditCardNumber();
            return addCardNumberDashes(removeCardNumberDashes(cc));
        }
        lastCCPayment = getLastCCPayment(customerID);
        if(lastCCPayment == null) return null;
        return lastCCPayment.getCheckNumber();
    }
    
    public String getLastCreditCard(long customerID) {
        CustomerInfo custInfo = new CustomerInfo(db);
        try { // first try to get CC # from customer data
            Customer cust = custInfo.getCustomerByID(customerID);
            String number = cust.getCreditCardNumber();
            if (number == null) number = "";
            if (number.length() > 0) {
                number = removeCardNumberDashes(number);
                number = addCardNumberDashes(number);
            } else { // now try to get # from last CC payment
                Payments payment = getLastCCPayment(customerID);

                if (payment != null)
                        number = "" + payment.getCheckNumber() + "*"
                                + payment.getVCODE();
            }
            return number;

        } catch (Exception e) {
            rmk.ErrorLogger.getInstance().logError(
                    "getLastCreditCard(" + customerID + ")", e);
            return "";
        } // end of try-catch

    }
    private Customer getCust(long customerID) throws Exception{
        CustomerInfo custInfo = new CustomerInfo(db);
        return custInfo.getCustomerByID(customerID);
    }
    
    public String getCustCCNumber(long customerID) throws Exception{
        Customer cust = getCust(customerID);
        return removeCardNumberDashes(cust.getCreditCardNumber());
    }
    
    public GregorianCalendar getCardExpiration(long customerID, String card) {
        GregorianCalendar expDate = null;
        
        try { // first try to get CC # from customer data
            Customer cust = getCust(customerID);
            card = removeCardNumberDashes(card);
            if(card.endsWith("*000"))
                card = card.substring(0, card.length()-4);
            String custCard = removeCardNumberDashes(cust.getCreditCardNumber());
            if (custCard.startsWith(card)) 
                // needed to allow a CC# in customer record that does not have vcode 
                return cust.getCreditCardExpiration();
        } catch (Exception e) {
            rmk.ErrorLogger.getInstance().logError(
                    "getCardExpiration(" + customerID + "," + card + ")", e);
            return null;
        } // end of try-catch

        Vector dbPayments = db.getItems("Payments", "checknumber = '" + card
                + "'" + " order by PaymentDate desc");
        if (dbPayments != null && dbPayments.size() > 0) {
            Payments payment = (Payments) dbPayments.get(0);
            return payment.getExpirationDate();
        }
        
        Payments payment = getLastCCPayment(customerID);
        if(payment != null){
            
        }
        return null;
    }

    //==========================================================
    Payments getLastCCPayment(long customerID) {
        Vector dbPayments = db.getItems("Payments", db
                .lenCriteria("checknumber")
                + ">=9"
                + " and CustomerID ="
                + customerID
                + " order by PaymentDate desc");
        Payments payment = null;
        for (Enumeration payments = dbPayments.elements(); payments
                .hasMoreElements();) {
            GregorianCalendar cutOffDate = new GregorianCalendar();
            cutOffDate.add(Calendar.MONTH, -Configuration.Config
                    .getCreditCardSearchMonths());

            payment = (Payments) payments.nextElement();
            String number = "" + payment.getCheckNumber();
            GregorianCalendar paymentDate = payment.getPaymentDate();
            GregorianCalendar expirationDate = payment.getExpirationDate();

            boolean dateChecks = true;
            if (!paymentDate.after(cutOffDate)) dateChecks = false;
            if (expirationDate == null
                    || !expirationDate.after(new GregorianCalendar())) {
                dateChecks = false;
            }

            if (isValidCCNumber(number) && dateChecks) return payment;
        }
        return null;
    }

    //==========================================================
    public Payments getLastPayment(long invoice) {
        Vector dbPayments = db.getItems("Payments", " Invoice =" + invoice
                + " order by PaymentDate desc");
        if (dbPayments == null || dbPayments.size() == 0) return null;
        return (Payments) dbPayments.get(0);
    }

    //==========================================================
    public String getPaymentTypeCode(Payments payment) {
        if (payment == null) return "";

        String number = payment.getCheckNumber();
        if (number == null || number.trim().length() == 0) return "CA";
        number = number.toUpperCase();
        //  	System.out.println(this.getClass().getName() + ":"+ number);

        if (number.equals("VI") || number.equals("MC") || number.equals("DI"))
                return number; // cc code, not number
        if (isValidCCNumber(number)) {
            if (number.startsWith("4")) return "VI";
            if (number.startsWith("5")) return "MC";
            if (number.startsWith("6")) return "DI";
        }
        if (number.startsWith("CC")) return "CC";
        if (number.startsWith("MO")) return "MO";
        if (number.startsWith("CA")) return "CA";
        return "CK";
    }

    public static int getPaymentTypeID(Payments payment) {
        int results = 0;
    
        return results;
    }
    
    //------------------------------------------------------------------
    public static boolean isValidCCNumber(String number) {
        if (number == null) return false;

        number = removeCardNumberDashes(number);
        number = addCardNumberDashes(number);
        //  	System.out.println( "isValidCCNumber():number.length():"+
        // number.length());

        if (number.length() < 19) return false; // must be AT LEAST 19
                //  	if(number.indexOf("*") <= 19)
                //  	    return false; // * is start of vcode
        return true;
    }

    public static String getBaseCCNumber(String ccnum) {
        ccnum = ccnum.toUpperCase();

        int index = ccnum.indexOf("*");
        if (index <= 0) index = ccnum.indexOf("VCODE");
        if (index > 5)
            return ccnum.substring(0, index).trim();
        else
            return ccnum;
    }

    //------------------------------------------------------------------
    public static String removeCardNumberDashes(String original) {
        String results = "";
        if (original == null) return "";

        original = original.trim();
        while (original.length() > 0 && results.length() < 16
                && Character.isDigit(original.charAt(0))) {
            results += original.charAt(0);
            original = original.substring(1);
            while (original.length() > 0
                    && (Character.isWhitespace(original.charAt(0)) || original
                            .charAt(0) == '-'))
                original = original.substring(1);
            //  	    System.out.println(results + ":" + original);
        }
        while (original.length() > 0
                && // get VCode??
                (Character.isDigit(original.charAt(0)) || original.charAt(0) == '*')) {
            results += original.charAt(0);
            original = original.substring(1);
        }
        return results;
    }

    public static long getVCode(String ccnum) {
        if (ccnum == null) return 0;
        ccnum = ccnum.toUpperCase();
        try {
            int index = ccnum.indexOf("*") + 1;
            if (index <= 0) index = ccnum.indexOf("VCODE") + 5;
            if (index > 5)
                return Long.parseLong(ccnum.substring(index).trim());
            else
                return 0;
        } catch (Exception e) {
            return 0;
        } // end of try-catch
    }

    //------------------------------------------------------------------
    public static String addCardNumberDashes(String original) {
        String results = "";
        int dashCnt = 0;
        if (original == null) return results;
        while (original.length() > 4 && dashCnt < 3) {
            //  	    System.out.println(results + ":" + original);
            results += original.substring(0, 4);
            original = original.substring(4);
            results += "-";
            dashCnt++;
        }
        results += original;
        return results;
    }

    //==========================================================
    //==========================================================
    public Vector getPaymentSummary(long invoice) {
        Vector results = new Vector();

        Vector payments = getInvoicePayments(invoice);
        return results;
    }

    //==========================================================
    //==========================================================
    public String getShippingState(Invoice inv) {
        if (inv.isShopSale()) return "SHOP";
        if (inv.isPickUp()) return "PU";
        
        String shippingInfo = inv.getShippingInfo();

        if (shippingInfo == null || shippingInfo.length() == 0) {
            CustomerInfo cust = new CustomerInfo(db);
            try {
                String state = cust.getCustomerState(inv.getCustomerID());
                if(state == null) state = "";
                state = state.toUpperCase();
                if(state.length() == 0)
                    state = cust.getCustomerCountry(inv.getCustomerID());
                return state;
            } catch (Exception e) {
                rmk.ErrorLogger.getInstance().logError(
                        "Getting shipping state(" + inv.getCustomerID() + ")",
                        e);
                return "";
            } // end of try-catch
        }
        StringTokenizer tokens = new StringTokenizer(shippingInfo);

        StringTokenizer st = new StringTokenizer(shippingInfo,"\n\t |,");
        String curr = "";
        String prev = "";
        int zip;
        int tokenLen;
        String tmpStr;
        while (st.hasMoreTokens()) {
            curr = st.nextToken();
            tokenLen = curr.length();
            //  	    System.out.println("curr:" + curr);
            if (tokenLen == 5 || tokenLen == 9 || tokenLen == 10) { // possible zip code
                tmpStr = curr.substring(0, 5);
                zip = 0;
                try {
                    zip = Integer.parseInt(tmpStr);
                } catch (Exception e) {
                }
                if (zip > 0) {
                    if (prev.length() == 2) return prev.toUpperCase();
                    System.out.println("**zip:" + zip);
                    System.out.println("**prev:" + prev);
                }
            } else if(curr.equalsIgnoreCase("APO")){
                return curr;
            }
            prev = curr;
        }
        //  	System.out.println(this.getClass().getName() + ":"+ shippingInfo);
        return "UNKNOWN";
    }

    //------------------------------------------------------------------
    boolean isFloridaState(Invoice inv) {
        String state = getShippingState(inv);
        return state.equals("FL");
    }

    public String substituteInCCNum(Invoice inv){
        String comment = inv.getComment();
        if(comment != null && comment.indexOf('^') >= 0){
        	String lastCreditCard ="";
        	try { lastCreditCard = getCustCCNumber(inv.getCustomerID());
			} catch (Exception e1) {ErrorLogger.getInstance().logError("Retrieving Cust CC# CustID:" + inv.getCustomerID(), e1);
			}
			if(lastCreditCard == null || lastCreditCard.length()==0){
				lastCreditCard = getLastCreditCard(inv.getInvoice(), inv.getCustomerID());
			}
            if(lastCreditCard == null)
            	lastCreditCard = "";
            lastCreditCard = FinancialInfo.addCardNumberDashes(FinancialInfo.removeCardNumberDashes(lastCreditCard));

            GregorianCalendar cardExpiration = getCardExpiration(inv.getCustomerID(), lastCreditCard);
            if(cardExpiration != null){
            	String date = ""+ (cardExpiration.get(GregorianCalendar.MONTH)+1) + "/"
                + ("" + cardExpiration.get(GregorianCalendar.YEAR)).substring(2);
            	while(date.length()<5) date = "0" + date;
            	
                lastCreditCard += " - " + date;
            } else{
                if(inv != null && inv.getCreditCardExpiration() != null){
                    lastCreditCard += " - " + inv.getCreditCardExpiration();
                }
                
            }
            comment = comment.replaceAll("\\^", lastCreditCard);
        }
        return comment;
 
    }
    //==========================================================
    //==========================================================
    //==========================================================
    public static void main(String args[]) throws Exception {
        rmk.DataModel sys = rmk.DataModel.getInstance();
        FinancialInfo fi = sys.financialInfo;
        //    	System.out.println(fi.getLastCreditCard(23532)); // 78
        //      	Invoice inv = sys.invoiceInfo.getInvoice(51482);
        //    	System.out.println(fi.getInvoiceTaxes(inv));

        //        	String cardNum = "1111-2222-3333-4444 vcode 233";
        String cardNum = "1111-2222-3333-4444  * 233";
        //      	String cardNum = "1234 5678-9012 3456";
        //      	cardNum = removeCardNumberDashes(cardNum);
        //      	cardNum = addCardNumberDashes(cardNum);
        //    	System.out.println(cardNum);
        System.out.println(isValidCCNumber(cardNum));
        System.out.println(getVCode(cardNum));
        System.out.println(getBaseCCNumber(cardNum));

        //  	System.out.println(fi.getCardExpiration(80000, "1234 5678-9012
        // 3456*234"));
        //  	System.out.println(fi.getCardExpiration(80000, "1111 2222 3333
        // 4444*234"));

        Invoice inv = sys.invoiceInfo.getInvoice(53089);
        // 41859, 42496, 50101, 50111 42514, 42511, 44827, 44266, 44800
        //    	System.out.println(fi.getShippingState(inv));
        //    	System.out.println(fi.getInvoiceTaxRate(inv));
        //    	System.out.println(fi.getInvoiceDue(inv));
        //  	System.out.println(fi.recomputeInvoiceRetail(inv));

        //  	System.out.println("FL?:"+ fi.isFloridaState(inv));
        //  	System.out.println(fi.getInvoiceTaxRate(inv));
        //    	System.out.println(fi.getTotalInvoiceDiscount(inv));
        //    	System.out.println(fi.getLastPayment(44266));
        //    	System.out.println(fi.getPaymentTypeCode(fi.getLastPayment(47929)));
        // // 44266

        System.out.println("TR:" + fi.getTotalRetail(inv));

        System.exit(0);
    }

}
