package rmk.gui;

import javax.swing.*;

//import com.adobe.acrobat.Viewer;

import carpus.gui.NoteDialog;

import java.awt.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;
import java.util.GregorianCalendar;

import rmk.DataModel;
import rmk.ErrorLogger;
import rmk.database.FinancialInfo;
import rmk.database.PartPriceTable;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.InvoiceEntryAdditions;
import rmk.database.dbobjects.PartPrices;
import rmk.database.dbobjects.Parts;
import rmk.database.dbobjects.Payments;
import rmk.database.dbobjects.InvoiceEntries;
import rmk.database.dbobjects.Customer;
import rmk.reports.AcknowledgeReport;
import rmk.reports.PartListReport;
import rmk.reports.TaxShipped;

import java.text.NumberFormat;

public class Dialogs {

    public static final int MAX_LEN_USER_NOTES = NoteDialog.MAX_LEN_NOTES_NONE;

    public static final int MAX_LEN_BLADELIST_NOTES = 30;

    public static final int MAX_LEN_INVOICE_NOTES = 90;

    static Dialogs instance = new Dialogs();

    static rmk.DataModel sys = rmk.DataModel.getInstance();

    public static Dialogs getInstance() {
        return instance;
    }

    private Dialogs() {
    }

    //--------------------------------------------------------------------------------
    public static void displayToDo() {
        try {
            ToDoDisplay dial = new ToDoDisplay(null, "", "To Do List", "");
            dial.setVisible(true);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    //--------------------------------------------------------------------------------
    public static Parts getNewPart() {
        Parts newPart = new Parts(0);
        newPart.setPartCode(JOptionPane.showInputDialog("Part Code"));
        if (newPart.getPartCode() == null
                || newPart.getPartCode().length() == 0) return null;
        newPart.setDescription(JOptionPane.showInputDialog("Description"));
        if (newPart.getDescription() == null
                || newPart.getDescription().length() == 0) return null;

        return newPart;
    }
    //--------------------------------------------------------------------------------
    public static boolean yesConfirm(String msg) {
        return (0 == JOptionPane.showConfirmDialog(null, msg, "Confirm",
                JOptionPane.YES_NO_OPTION));
    }
    //--------------------------------------------------------------------------------
    public static Vector getSearchResults() {
        rmk.gui.search.ReportSearchScreen dial = new rmk.gui.search.ReportSearchScreen();
        dial.setVisible(true);
        Vector criteria = dial.getCriteria();
        if (criteria == null) return null;
        String type = dial.getType();

        try {
            Vector data = rmk.gui.search.Processing.getSearchResults(type,
                    criteria);
            String msg = "";
            int cnt = 0;
            if (data != null) {
                for (java.util.Enumeration enum = data.elements(); enum
                        .hasMoreElements();) {
                    if (cnt++ < 30) {
                        msg += enum.nextElement() + "\n";
                    } else if (cnt > 30) {
                        msg += ".";
                    }
                }
            }
            msg = "*****Not fully implimeted!!!**** \n" + msg;

            System.out.println(msg);
            JOptionPane.showMessageDialog(null, msg, "UnImplemented:",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "" + e, "UnImplemented:",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        } // end of try-catch
        //  	return data;
    }
    //--------------------------------------------------------------------------------
    public static boolean taxRateChange(double oldRate, double newRate,
            int invNumber) {
        if (oldRate > 1) oldRate /= 100;
        if (newRate > 1) newRate /= 100;

        NumberFormat formatter = NumberFormat.getPercentInstance();
        formatter.setMaximumFractionDigits(1);

        String question = "Change tax rate from " + formatter.format(oldRate)
                + " to " + formatter.format(newRate) + " ?";

        double diff = oldRate - newRate;
        diff = Math.floor(diff * 100 + 0.5) / 100;
        if (diff != 0) System.out.println("Dialogs:taxRateChange:" + question);

        if (diff == 0) return false; // obviously no change

        if (invNumber == 0) return true;

        return (0 == JOptionPane.showOptionDialog(Desktop.getInstance()
                .getFrame(), question, "Tax Rate Change",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                null, null) //defaults for the rest
        );
    }
    //--------------------------------------------------------------------------------
    public static void report(int type, int format, int keyValue) {
        try {
            HtmlReportDialog rpt = new HtmlReportDialog(Desktop.getInstance()
                    .getFrame(), type);
            rpt.setInvoice(keyValue);
            rpt.setFormat(format);
            rpt.setVisible(true);
        } catch (Exception ex) {
            System.out.println(rmk.ErrorLogger.getInstance().stkTrace(ex));
        }
    }
    //--------------------------------------------------------------------------------
    public static void multiInvoice() {

        // get date
        java.util.GregorianCalendar date = new java.util.GregorianCalendar();
        date.add(Calendar.DATE, 8 * 7); // default to 8 weeks out
        while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
            date.add(Calendar.DATE, 1);
        date = getDate("Estimated Date", date);
        if (date == null) return; // canceled

        // get Terms
        String termsStr = JOptionPane.showInputDialog("Terms?");
        if (termsStr == null) return; // canceled
        int terms = 1;
        try {
            terms = Integer.parseInt(termsStr);
            if (terms > 2) return; // invalid terms
        } catch (Exception e) {
            return;
        } // end of try-catch

        // get invoices
        Vector data = sys.invoiceInfo.getInvoicesByEstimatedAndTerms(date,
                terms);
        if (data == null || data.size() < 1) return;

        rmk.reports.InvoiceReport rpt;
        try {
            rpt = new rmk.reports.InvoiceReport(2);
        } catch (Exception e) {
            return;
        } // end of try-catch

        int cnt = 0;
        for (java.util.Enumeration enum = data.elements(); enum
                .hasMoreElements();) {
            Invoice invoice = (Invoice) enum.nextElement();
            rpt.setInvoice(invoice);
            try {
                Customer cust = sys.customerInfo.getCustomerByID(invoice
                        .getCustomerID());
                rpt.setCustomer(cust);
            } catch (Exception e) {
            } // end of try-catch

            if (terms == 1)
                rpt.setFormat(1); // customer
            else
                rpt.setFormat(0); // dealer

            //  	    System.out.println(invoice);
            //      	    if(cnt < 2){
            try {
                rmk.reports.Printing.printReport(rpt);
                //      	    }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
            cnt++;
        }
    }
    //--------------------------------------------------------------------------------
    public static long generateBlankDealerInvoice(long dealerCustID)
            throws Exception {
        Customer cust = sys.customerInfo.getCustomerByID(dealerCustID);
        GregorianCalendar shipDate = getDate("Estimated Ship Date", null);
        if (shipDate == null) return 0;

        String knifeStr = "50";
        knifeStr = JOptionPane.showInputDialog("Knives", knifeStr);
        if (knifeStr == null) return 0; //canceled
        int knives = Integer.parseInt(knifeStr);

        Vector saveVector = new Vector();
        Invoice invoice = new Invoice(0);
        saveVector.add(invoice);

        invoice.setCustomerID(cust.getCustomerID());
        invoice.setDateEstimated(shipDate);
        invoice.setDateOrdered(new GregorianCalendar());
        invoice.setTotalRetail(0);
        invoice.setDiscountPercentage(cust.getDiscount());
        invoice.setTaxPercentage(0);
        Configuration.Config.getDB().saveItems("Invoices", saveVector);

        saveVector = new Vector();
        InvoiceEntries item = new InvoiceEntries(0);
        saveVector.add(item);

        Parts knvPart = sys.partInfo.getPartFromCode("KNV");

        item.setPartID(knvPart.getPartID());
        item.setPartDescription(knvPart.getPartCode());
        item.setInvoice(invoice.getInvoice());
        item.setQuantity(knives);
        Configuration.Config.getDB().saveItems("InvoiceEntries", saveVector);

        AcknowledgeReport rpt = new AcknowledgeReport();
        rpt.setInvoice(invoice);
        rpt.setCustomer(cust);
        rmk.reports.Printing.printReport(rpt);

        String msg = "Invoice:" + invoice.getInvoice() + " created for "
                + cust.getLastName();
        JOptionPane.showMessageDialog(null, msg, "Invoice Created",
                JOptionPane.INFORMATION_MESSAGE);

        return invoice.getInvoice();
    }
    //--------------------------------------------------------------------------------
    public static void bladeList(boolean exitOnCancel, GregorianCalendar defaultDate) {
        java.util.GregorianCalendar date = new java.util.GregorianCalendar();
        while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
            date.add(Calendar.DATE, 1);
        //	date.add(Calendar.WEEK_OF_YEAR,5); // usually ask for 5 weeks from
        // today
        date.add(Calendar.WEEK_OF_YEAR, 5); // TODO: Debug
        if(defaultDate != null)
        	date = defaultDate;
        date = getDate("Week of:", date);
        if (date == null) return;

        //-----------------------------------
        // taken out as per valerie, something funky with forcing a large
        // dealer invoice
        // to FRI and generating a special blade list
        //-----------------------------------
        //  	int dayAdd = 1;
        //  	if(date.DAY_OF_WEEK > date.THURSDAY)
        //  	    dayAdd = -1;
        //  	while(date.get(date.DAY_OF_WEEK) != date.THURSDAY)
        //  	    date.add(date.DATE,dayAdd);

        try {
            HtmlReportDialog rpt = new HtmlReportDialog();
            rpt.exitOnCancel = exitOnCancel;
            rmk.reports.BladeList tst = new rmk.reports.BladeList(date);
            rpt.setReport(tst);
            rpt.setVisible(true);
        } catch (Exception ex) {
            System.out.println(rmk.ErrorLogger.getInstance().stkTrace(ex));
        }
    }
    //--------------------------------------------------------------------------------
    public static void taxOrderedReport() {
        java.util.GregorianCalendar date = new java.util.GregorianCalendar();
        date.add(Calendar.MONTH, -1);

        date = getTaxReportDate("Month/Year:", date);
        if (date == null) return;

        try {
            HtmlReportDialog rpt = new HtmlReportDialog();
            rmk.reports.TaxOrdered tst = new rmk.reports.TaxOrdered(date);
            rpt.setReport(tst);
            rpt.setVisible(true);
        } catch (Exception ex) {
            System.out.println(rmk.ErrorLogger.getInstance().stkTrace(ex));
        }
    }
    //--------------------------------------------------------------------------------
    public static void taxShippedReport() {
        java.util.GregorianCalendar date = new java.util.GregorianCalendar();
        date.add(Calendar.MONTH, -1);

        date = getTaxReportDate("Month/Year:", date);
        if (date == null) return;

        try {
            HtmlReportDialog rpt = new HtmlReportDialog();
            TaxShipped tst = new TaxShipped(date);
            rpt.setReport(tst);
            rpt.setVisible(true);
        } catch (Exception ex) {
            System.out.println(rmk.ErrorLogger.getInstance().stkTrace(ex));
        }
    }
    //--------------------------------------------------------------------------------
    public static void partListReport() {
        try {
            HtmlReportDialog rptDialog = new HtmlReportDialog();
            PartListReport rpt = new PartListReport();
            rptDialog.setReport(rpt);
            rptDialog.setVisible(true);
        } catch (Exception ex) {
            System.out.println(rmk.ErrorLogger.getInstance().stkTrace(ex));
        }
    }
    //--------------------------------------------------------------------------------
    public boolean dataErrors(Vector errors) { // returns true if errors
        if (errors != null) {
            String errMsg = "";
            for (java.util.Enumeration enum = errors.elements(); enum
                    .hasMoreElements();) {
                errMsg += enum.nextElement() + "\n";
            }
            JOptionPane.showMessageDialog(null, errMsg, "Data Entry Errors:",
                    JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }
    //--------------------------------------------------------------------------------
    public static boolean shippedItemEditConfirm(String action) {
        String question = "Invoice Alredy shipped!!!\nConfirm " + action + ".";
        return (0 == JOptionPane.showOptionDialog(Desktop.getInstance()
                .getFrame(), question, "Options", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null) //defaults for
                                                                // the rest
        );
    }
    //--------------------------------------------------------------------------------
    public static Vector initialNewInvoiceEntry(Screen parent, Invoice inv,
            Customer cust, String message) {
        // get initial model entry, mabye...
        PartPriceTable pricetable = sys.pricetable;
        String initialModel = JOptionPane.showInputDialog(message);

        if (initialModel == null) { // cancelled
        	return cancelledInvoiceEntry();
        } else if (initialModel.length() <= 0) { // OK'd with blank
        return null; }

        String modelCode = initialModel.trim();
        if (modelCode.endsWith(",")) // trim off extraneous ,
                modelCode = modelCode.substring(0, modelCode.length() - 1);

        boolean enteredFeatures = false;
        int seperatorIndex = modelCode.indexOf(",");
        if (seperatorIndex < 0) seperatorIndex = modelCode.indexOf(".");

        if (seperatorIndex > 0 && seperatorIndex != modelCode.length())
                enteredFeatures = true;
        if (enteredFeatures) {
            if (modelCode.equals(modelCode.toLowerCase())
                    && yesConfirm("Convert to charged features?"))
                    modelCode = modelCode.toUpperCase();
        }
        int year = rmk.database.InvoiceInfo.getPricingYear(inv, cust.isDealer());

        Vector partCodes = sys.partInfo.getPartCodesFromString(modelCode);
//        System.out.println(partCodes);
        Vector parts = sys.partInfo.getPartsFromPartCodeVector(partCodes);

        //-- model
        Parts part = (Parts) parts.get(0);

        while (part == null) { // fix main model code
            String code = ""+partCodes.get(0);
            Parts correctedPart = getCorrectedModel(parent,code);
            part = correctedPart;
            if (part == null) {
                return null;
            }
        }
        
        int qty = getNumericValue(initialModel + "\nQuantity?", 1);
        if(qty <= 0){
        	return cancelledInvoiceEntry();
        }


        InvoiceEntries item = new InvoiceEntries(0);
        item.setPartID(part.getPartID());
        item.setPartDescription(part.getPartCode());
        item.setInvoice(inv.getInvoice());
        item.setQuantity(qty);
        
        String note = JOptionPane.showInputDialog(initialModel + "\nKnife Comment?");
        if (note == null) {
        	return cancelledInvoiceEntry();
        }
        item.setComment(note);

        int partID = (int) part.getPartID();
        double price = getFeaturePrice(year, part, pricetable, initialModel);
        item.setPrice(price * qty);

        if (parts.size() > 1) { // features were on list
//            Vector featureList = new Vector();
            for (int featureIndex = 1; featureIndex < parts.size(); featureIndex++) {
                part = (Parts) parts.get(featureIndex);
                String code = (String) partCodes.get(featureIndex);
                if (part == null) { // invalid code, allow correction
                    Parts correctedPart = getCorrectedPart("Invalid CODE\n", parent,code);
                    part = correctedPart;
                }
                if(part != null){
                    price = getPartPrice(part, code, inv.isShopSale(), year, initialModel);
                    InvoiceEntryAdditions feature = new InvoiceEntryAdditions(0);
                    feature.setPartID(part.getPartID());
                    feature.setPrice(price);
                    price *= qty;
                    
                    item.setPrice(item.getPrice() + price); // add price to item
                    item.addFeature(feature); // add to item
//                    featureList.add(feature); // add to list
                }
            }
        }
        Vector data = new Vector();
        data.add(item);
        return data;
    }
    //--------------------------------------------------------------------------------
    static Vector cancelledInvoiceEntry(){
        Vector results = new Vector();
        results.add(new InvoiceEntries(0));
        return results;
    }
    //--------------------------------------------------------------------------------
    static double getPartPrice(Parts part, String enteredCode, boolean shopSale, int year, String fullLineItem){
        PartPriceTable pricetable = sys.pricetable;

        if (part != null) {
            int partID = (int) part.getPartID();
            if (enteredCode != enteredCode.toUpperCase()) {
                return 0;
            } else {
                if (shopSale) // don't ask about price
                    return pricetable.getPartPrice(year, (int) part
                            .getPartID());
                else
                    return getFeaturePrice(year, part, pricetable, fullLineItem);
            }
        }
        return 0;
    }
    //--------------------------------------------------------------------------------
    static Parts getCorrectedModel(Screen parent, String code){
        boolean correctCode=false;
        Parts newPart =null;
        while(! correctCode){
        	// first try a partial match
            newPart = sys.partInfo.getPartFromPartialCode(code);
            if(newPart != null)
            	return newPart;
            // then ask again
            JOptionPane.showMessageDialog(parent, "Invalid model: "
                    + code);
            code = JOptionPane.showInputDialog("Correct model: " + code);
            if(code == null || code.length()==0) 
                return null;
            newPart = sys.partInfo.getPartFromPartialCode(code);
            if(newPart != null)
                correctCode = true;
        }
        return newPart;
    }
    //--------------------------------------------------------------------------------
    static Parts getCorrectedPart(String msg,Screen parent, String code){
        boolean correctCode=false;
        Parts newPart =null;
        while(! correctCode){
            JOptionPane.showMessageDialog(parent, msg
                    + code);
            code = JOptionPane.showInputDialog("Correct code: " + code);
            if(code == null || code.length()==0) 
                return null;
            newPart = sys.partInfo.getPartFromCode(code);
            if(newPart != null)
                correctCode = true;
        }
        return newPart;
    }
    //--------------------------------------------------------------------------------
    static double getFeaturePrice(int year, Parts part,
            PartPriceTable pricetable, String fullLineItem) {
        double price = pricetable.getPartPrice(year, (int) part.getPartID());
        String code = "" + part.getPartCode();
        if(part.askPrice())
        	price = askPriceOfPart(price, code, fullLineItem);
//        if (code.startsWith("CW") ||  code.startsWith("22-4") 
//                || code.equalsIgnoreCase("WT")
//                || code.equalsIgnoreCase("FG") || code.equalsIgnoreCase("BRP")
//                || code.equalsIgnoreCase("NSP") || code.equalsIgnoreCase("CI")
//                || code.equalsIgnoreCase("LFG") || code.equalsIgnoreCase("G10")
//                || code.equalsIgnoreCase("MISC") || code.equalsIgnoreCase("MISCNT")
//                || code.equalsIgnoreCase("SHOPSALE-T") || code.equalsIgnoreCase("SHOPSALE-N")
//				) {
//            price = getPrice(price, code);
//        }
        return price;
    }
    //--------------------------------------------------------------------------------
    public static double askPriceOfPart(double startPrice, String partCode, String fullLineItem) {
        double results = -1;
        while (results < 0) {
            try {
                results = startPrice;
                String question = "";
                if(fullLineItem != null)
                	question = fullLineItem + "\n"; 
                
                question += "Amount for " + partCode + "?";
                
                results = Double.parseDouble(JOptionPane.showInputDialog(
                        question, "" + results));
                return results;
            } catch (Exception e) {
            }
        }
        return results;
    }
    //--------------------------------------------------------------------------------
    public static String getEditNote(String initialValue, String title,
            int maxLen, boolean warnOnly) {
        //  	String text = initialValue;
        Frame parentFrame = Desktop.getInstance().getFrame();
        carpus.gui.NoteDialog dial = new carpus.gui.NoteDialog(
                (Frame) parentFrame, initialValue, title, "Notes/Comments",
                maxLen, warnOnly);
        if (parentFrame != null) {
            Point p = parentFrame.getLocation();
            dial
                    .setLocation(p.x
                            + (parentFrame.getWidth() - dial.getWidth()) / 2,
                            p.y + (parentFrame.getHeight() - dial.getHeight())
                                    / 2);
        }

        dial.setVisible(true);
        String newText = dial.getText();
        //  	if(newText != null) return newText;
        return newText;
        //  	if(text == null || text.equals(initialValue)) return null; // NO
        // change
        //  	return text;
    }
    //--------------------------------------------------------------------------------
    public static Payments getPayment(long invoice, long customerID) {
        String reply = "";
        boolean validEntry = true;
        boolean creditCard = false;
        String vcode = null;
        double amt = 0;
        GregorianCalendar paymentDate = DataModel.currentDate;
        //  	GregorianCalendar expirationDate = sys.currentDate;
        GregorianCalendar expirationDate = null;
        ;

        validEntry = false;
        while (!validEntry) {
            reply = JOptionPane.showInputDialog("Amount?");
            if (reply == null || reply.equals("")) return null;
            try {
                amt = Double.parseDouble(reply);
            } catch (Exception e) {
            }
            if (amt != 0) validEntry = true;
        }

        String cardNumber = sys.financialInfo.getLastCreditCard(customerID);
        //  	cardNumber = sys.financialInfo.addCardNumberDashes(cardNumber);
        cardNumber = rmk.database.FinancialInfo
                .removeCardNumberDashes(cardNumber);
        cardNumber = rmk.database.FinancialInfo.addCardNumberDashes(cardNumber);

        //  	System.out.println(this.getClass().getName() + ":" + "cardNumber:"+ cardNumber);

        boolean sameCard = false;
        validEntry = false;
        while (!validEntry) {
            reply = JOptionPane.showInputDialog("Check/Card Number?",
                    cardNumber);
            //  	    System.out.println(this.getClass().getName() + ":" +  ":"+ reply);
            if (reply == null) return null; //canceled
            if (reply.length() >= 1) {
                if (FinancialInfo.isValidCCNumber(reply)) {
                    creditCard = true;
                } else { // NOT a card #
                }
                if (cardNumber.equals(reply)) sameCard = true;
                cardNumber = reply;
                validEntry = true;
            }
        }
        //  	JOptionPane.showMessageDialog(null, cardNumber + "\n" + vcode);

        if (creditCard) {
            if (reply.indexOf("*") <= 0) { // vcode was not ALSO entered
                reply = "";
                validEntry = false;
                while (!validEntry) {
                    reply = JOptionPane.showInputDialog("V-Code?", reply);
                    if (reply == null) return null; //canceled
                    if (reply.length() >= 1) {
                        vcode = reply;
                        validEntry = true;
                    }
                }
            } else {// vcode was entered - split CC#/Vcode
                cardNumber = reply.substring(0, reply.indexOf("*"));
                vcode = reply.substring(reply.indexOf("*") + 1);
                validEntry = true;
            }
            cardNumber = FinancialInfo.removeCardNumberDashes(cardNumber);
            //  	    if(sameCard){
            System.out.println("Dialogs:getPayments:" + "lookup" + customerID + "," + cardNumber);

            expirationDate = sys.financialInfo.getCardExpiration(customerID,
                    cardNumber + "*" + vcode);
            //  	    }
            expirationDate = getExpirationDate("Expiration Date",
                    expirationDate);
            if (expirationDate == null) return null;
        }

        paymentDate = getDate("Payment Date", paymentDate);
        if (paymentDate == null) return null;

        Payments results = new Payments(0);
        results.setPayment(amt);
        results.setInvoice(invoice);
        results.setCustomerID(customerID);
        results.setCheckNumber(cardNumber);
        results.setPaymentDate(paymentDate);
        results.setExpirationDate(expirationDate);
        results.setVCODE(vcode);
        return results;
    }
    //--------------------------------------------------------------------------------
    public static GregorianCalendar getDate(String msg, GregorianCalendar defaultDate) {
        String reply = "";
        int currYear = (new GregorianCalendar()).get(GregorianCalendar.YEAR);
        while (true) {
            String date = carpus.util.DateFunctions.getSQLDateStr(defaultDate);
            reply = JOptionPane.showInputDialog(msg, date);
            if (reply == null) return null; //canceled
            int len=reply.length();
            if(len > 2 && len < 5 && reply.indexOf('/')>0)
            	reply += "/" + currYear;
            if(len > 2 && len < 5 && reply.indexOf('-')>0)
            	reply = currYear + "-" + reply;
            if (reply.length() >= 5) {
                if (carpus.util.DateFunctions.gregorianFromString(reply) != null)
                        return carpus.util.DateFunctions
                                .gregorianFromString(reply);
            }
        }
    }
    //--------------------------------------------------------------------------------
    public static int getNumericValue(String baseMsg, int defaultValue){
    	int qty = defaultValue;
    	String errMsg="";
    	boolean invalidEntry=true;
    	while (invalidEntry) {
    		String entry = JOptionPane.showInputDialog(errMsg + baseMsg, "" + qty);
    		if (entry == null || entry.length() <= 0) { // cancel
    			return -1;
    		} else {
    			try {
    				qty = Integer.parseInt(entry);
    				return qty;
    			} catch (Exception e) {
    				errMsg = "Invalid value... \n";
    			}
    		}
    	}
    	return -1; // should never get here
    }
    //--------------------------------------------------------------------------------
    public static GregorianCalendar getExpirationDate(String msg,
            GregorianCalendar defaultDate) {
        String reply = "";
        GregorianCalendar results = null;
        while (true) {
            String date = "";
            if (defaultDate != null)
                    date = (defaultDate.get(Calendar.MONTH) + 1) + "/"
                            + defaultDate.get(Calendar.YEAR);
            reply = JOptionPane.showInputDialog(msg, date);
            if (reply == null) return null; //canceled
            if (reply.length() >= 4) {
                try {
                    int month = Integer.parseInt(reply.substring(0, reply
                            .indexOf("/")));
                    int year = Integer.parseInt(reply.substring(reply
                            .indexOf("/") + 1));
                    GregorianCalendar enteredDate = new java.util.GregorianCalendar();
                    if (month > 12)
                            throw new Exception("Invalid Month:" + (month - 1));
                    enteredDate.set(Calendar.MONTH, month - 1);
                    if (year < 1900) year += 2000;
                    enteredDate.set(Calendar.YEAR, year);

                    enteredDate.add(Calendar.MONTH, 1);
                    enteredDate.set(Calendar.DAY_OF_MONTH, 1);
                    enteredDate.add(Calendar.DAY_OF_MONTH, -1);

                    GregorianCalendar endOfMonth = new GregorianCalendar();
                    endOfMonth.add(Calendar.MONTH, 1);
                    endOfMonth.set(Calendar.DAY_OF_MONTH, 1);

                    if (enteredDate.before(endOfMonth))
                            throw new Exception("Expired!!!");
                    // end of month
                    return enteredDate;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Invalid Date:  "
                            + e.getMessage());
                }
            }
        }

    } //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    public static GregorianCalendar getTaxReportDate(String msg,
            GregorianCalendar defaultDate) {
        String reply = "";
        GregorianCalendar results = null;
        while (true) {
            String date = "";
            if (defaultDate != null)
                    date = (defaultDate.get(Calendar.MONTH) + 1) + "/"
                            + defaultDate.get(Calendar.YEAR);
            reply = JOptionPane.showInputDialog(msg, date);
            if (reply == null) return null; //canceled
            if (reply.length() >= 4) {
                try {
                    int month = Integer.parseInt(reply.substring(0, reply
                            .indexOf("/")));
                    int year = Integer.parseInt(reply.substring(reply
                            .indexOf("/") + 1));
                    GregorianCalendar enteredDate = new java.util.GregorianCalendar();
                    if (month > 12)
                            throw new Exception("Invalid Month:" + (month - 1));
                    enteredDate.set(Calendar.MONTH, month - 1);
                    if (year < 1900) year += 2000;
                    enteredDate.set(Calendar.YEAR, year);

                    enteredDate.add(Calendar.MONTH, 1);
                    enteredDate.set(Calendar.DAY_OF_MONTH, 1);
                    enteredDate.add(Calendar.DAY_OF_MONTH, -1);
                    return enteredDate;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Invalid Date:  "
                            + e.getMessage());
                }
            }
        }

    }
    //--------------------------------------------------------------------------------
    public static void showKnifeCounts(java.util.GregorianCalendar date) {
        String message = "";

        Vector result = sys.invoiceInfo.getKnifeCounts(date);
        for (int i = 0; i < result.size(); i++) {
            message += result.get(i) + "\n";
        }

        JOptionPane.showMessageDialog(null, message);
    }
    //--------------------------------------------------------------------------------
    public static void mergeIntoCustomer(long incorrectCustomerID){
        int id = 0;
        Customer incorrectCustomer=null;
        try{
        	incorrectCustomer = sys.customerInfo.getCustomerByID(incorrectCustomerID);
        }catch (Exception e) {
            ErrorLogger.getInstance().logError("Retrieving customer:" + id, e);
            return;
        }
        try {
            Customer correctCustomer = getMergeCustomer();
            if(correctCustomer != null){
            	// 	merge
            	sys.invoiceInfo.mergeCustomers(correctCustomer, incorrectCustomer);
            }
        } catch (Exception e) {
            ErrorLogger.getInstance().logError("Retrieving customer:" + id, e);
            return;
        }
    }
    //--------------------------------------------------------------------------------
    private static Customer getMergeCustomer(){
        Customer mergeCustomer =null;
        long id=0;
        // get old customer ID/cust
        String msg = "Old Customer ID to merge into selected customer:";
        String reply = JOptionPane.showInputDialog(msg, "");
        if(reply == null || reply.length() == 0)
            return null;
        try {
            id = Long.parseLong(reply);
            mergeCustomer = sys.customerInfo.getCustomerByID(id);
            msg = "Merge Customer:" + mergeCustomer.getFirstName() + " " + mergeCustomer.getLastName() + "?";
            // confirm
            if(yesConfirm(msg))
                return mergeCustomer;
            else
                return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid id", "Invalid ID:",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
    //--------------------------------------------------------------------------------    
    static Vector generalSearch() {
        Vector results = new Vector();
        String msg = "Customer's Name? (last,first) | Phone # | Invoice | *Customer #";
        String qry = JOptionPane.showInputDialog(msg);

        boolean found = false;
        while (qry != null && qry.length() != 0) {
            //  	    System.out.println("generalSearch:" + qry);
            if(qry.startsWith("*")){
                results = sys.customerInfo.getCustomersByID(qry);
            } else if (qry.indexOf(",") > 0) { // Name??
                results = sys.customerInfo.getCustomersByName(qry);
            } else if (qry.indexOf("-") > 0 || qry.indexOf("(") > 0) { // phone
                                                                       // #
                results = sys.customerInfo.getCustomersFromPhone(qry);
            } else { // Invoice or customer last name
                long id = 0;
                if (qry.length() < 7) {
                    try {
                        id = Long.parseLong(qry);
                    } catch (Exception e) {
                    } // end of try-catch
                    if (id > 0) {
                        results.add(sys.invoiceInfo.getInvoice(id));
                    }
                    if (results.size() == 0) { // short name?
                    //  			System.out.println("By short Name?");
                        results = sys.customerInfo.getCustomersByName(qry);
                    }
                } else { // qry length >= 7
                    boolean name = true;
                    for (int i = 0; i < qry.length(); i++) {
                        if (Character.isDigit(qry.charAt(i))) name = false;
                    }
                    if (name)
                        results = sys.customerInfo.getCustomersByName(qry);
                    else {
                        qry = rmk.DataModel.getFixedPhoneNumber(qry);
                        results = sys.customerInfo.getCustomersFromPhone(qry);
                    }
                }

                if (results.size() == 0) {
                }
            }
            if (results != null && results.size() >= 1) return results;
            qry = JOptionPane.showInputDialog(msg);
        }
        if (qry != null) {
            boolean addNew = yesConfirm("Add new Customer");
            System.out.println("Dialogs:addNew:" + addNew);
            if (addNew) rmk.ScreenController.getInstance().newCustomer();
        }
        return results;
    }
    
    static void updatePricing(){
    	PartPriceTable pricetable = sys.pricetable;
    	// get year
    	GregorianCalendar now = new GregorianCalendar();
        String yearStr = JOptionPane.showInputDialog("Year?", "" + (now.get(GregorianCalendar.YEAR)+1));
        if(yearStr == null || yearStr.length()==0) return;
        int year = Integer.parseInt(yearStr);
    	// get All active parts
		Vector newPrices = new Vector();

		int cnt=0;
		Vector partsVector = new Vector();
        for(Enumeration parts = sys.partInfo.getParts();parts.hasMoreElements();){
        	Parts part = (Parts) parts.nextElement();
        	if(part.isActive())
        		partsVector.add(part);
        }
        Parts[] partsArray = new Parts[partsVector.size()];
        partsVector.toArray(partsArray);
        Arrays.sort(partsArray, new rmk.comparators.PartListRpt());
		
        for(int i=0; i< partsArray.length; i++){
        	// for each part
        	Parts part = partsArray[i];
        	if(part.isActive()){
        		
        		//	get partprice for year
        		boolean update = true;
        		int lookupYear = year;
        		double partPrice = pricetable.getPartPrice(lookupYear, (int) part.getPartID());
        		if(partPrice <=0){ // try previous year
        			lookupYear--;
        			partPrice = pricetable.getPartPrice(lookupYear, (int) part.getPartID());
        			update = false;
        		}
        		
        		String newPrice = JOptionPane.showInputDialog("Price: " + part.getPartCode() + " ?", "" + partPrice );
        		if(newPrice == null || newPrice.length()==0)
        			break;
        		double newPartPrice = Double.parseDouble(newPrice);
        		
        		// get partPriceID
        		int partPriceID = 0;
        		PartPrices partPriceObject = pricetable.getPartPriceObject(year, (int) part.getPartID());
        		if(partPriceObject != null)
        			partPriceID = (int) partPriceObject.getPartPriceID();
        		PartPrices newPartPriceItem = new PartPrices(partPriceID);
        		newPartPriceItem.setPartID(part.getPartID());
        		newPartPriceItem.setPrice(newPartPrice);
        		newPartPriceItem.setYear(year);
        		newPartPriceItem.setDiscountable(part.isDiscountable());
        		newPrices.add(newPartPriceItem);
        	}
        }
        if(newPrices.size() > 0){
        	Configuration.Config.getDB().saveItems("PartPrices", newPrices);
        }
    	
    }
    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    public static void main(String args[]) throws Exception {
//        Invoice inv = rmk.DataModel.getInstance().invoiceInfo.getInvoice(44469);
//        Customer cust = rmk.DataModel.getInstance().customerInfo
//                .getCustomerByID(inv.getCustomerID());
//        Vector entry = initialNewInvoiceEntry(null, inv, cust,
//                "Model,feature,feature,...?");
//        if (entry != null) System.out.println(entry.get(0));
//    	
        System.exit(0);
    }

}
