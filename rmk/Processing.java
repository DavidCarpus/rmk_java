package rmk;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.Customer;

public class Processing {

    private static Processing instance = new Processing();

    GregorianCalendar shipDate = new GregorianCalendar();

    rmk.DataModel sys = rmk.DataModel.getInstance();

    private Processing() {
    }

    public static Processing getInstance() {
        return instance;
    }

    //============================================================================
    public void shipInvoices() {
        boolean validEntry = false;
        String results = "";
        String reply = "";
        Vector invoicesProcessed = new Vector();
        rmk.DataModel sys = rmk.DataModel.getInstance();
        ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + "shipInvoice");

        validEntry = false;
        shipDate = rmk.gui.Dialogs.getDate("Shipping Date",
                new GregorianCalendar());
        if (shipDate == null) return;

        long invoiceNumber = 1;
        while (invoiceNumber > 0) { // as long as invoice number's are entered
            invoiceNumber = getShippingInvoiceNumber();
            if (invoiceNumber != 0) { // an invoice was entered
                boolean remove = false;
                if (invoiceNumber < 0) {
                    remove = true;
                    invoiceNumber = -invoiceNumber;
                }
                
                Invoice invoice = DataModel.getInstance().invoiceInfo.getInvoice(invoiceNumber);

                if (remove){
                    unShipInvoice(invoice);
                    removeInvoiceFromList(invoicesProcessed, invoice);
                    if (!invoiceInList(invoicesProcessed, invoice))
                        invoicesProcessed.add(invoice);
                
                } else {
                    if (shipInvoice(invoice, shipDate)) {
                        removeInvoiceFromList(invoicesProcessed, invoice);
                        if (!invoiceInList(invoicesProcessed, invoice))
                                invoicesProcessed.add(invoice);
                    } else {
                    	ErrorLogger.getInstance().logMessage(this.getClass().getName() + "Dupe shipping:" + invoice);
                    }
                }
            }
        }

        if (invoicesProcessed.size() > 0) { // list invoices shipped
            String list = "";
            String invoices = "";
            int cnt = 0;

            for (Enumeration enum = invoicesProcessed.elements(); enum
                    .hasMoreElements();) {
                Invoice invoice = (Invoice) enum.nextElement();
                if (invoice.getDateShipped() != null) {
                    invoices += invoice.getInvoice() + "\n";
                    cnt++;
                }
            }
            list += "Shipped: " + cnt + " Invoices\n";
            list += invoices;

            cnt = 0;
            invoices="";
            for (Enumeration enum = invoicesProcessed.elements(); enum
                    .hasMoreElements();) {
                Invoice invoice = (Invoice) enum.nextElement();
                if (invoice.getDateShipped() == null) {
                    invoices += invoice.getInvoice() + "\n";
                    cnt++;
                }
            }
            list += "\nUn-Shipped: " + cnt + " Invoices\n";
            list += invoices;

            JOptionPane.showMessageDialog(null, list, "Shipped Invoces",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    void removeInvoiceFromList(Vector lst, Invoice invoice) {
        if (lst.contains(invoice)){ 
            lst.remove(invoice);
        } else{
            for (Enumeration enum = lst.elements(); enum.hasMoreElements();) {
                Invoice inv = (Invoice) enum.nextElement();
                if (inv.getInvoice() == invoice.getInvoice()) {
                    lst.remove(inv);
                    return;
                }
            }
        }
    }
        
    boolean invoiceInList(Vector lst, Invoice invoice) {
        if (lst.contains(invoice)) return true;
        for (Enumeration enum = lst.elements(); enum.hasMoreElements();) {
            Invoice inv = (Invoice) enum.nextElement();
            if (inv.getInvoice() == invoice.getInvoice()) return true;
        }
        return false;
    }

    public boolean unShipInvoice(Invoice invoice) {
        if (unsavedScreens(invoice)) { // if screens need to be saved first
            return false;
        }

        invoice.setDateShipped(null);
        Vector invoiceVect = new Vector();
        invoiceVect.add(invoice);
        DataModel.db.saveItems("Invoice", invoiceVect);
        updateScreens_Shipping(invoice);
        return false;

    }

    public boolean shipInvoice(Invoice invoice, GregorianCalendar shipDate) {
        if (unsavedScreens(invoice)) { // if screens need to be saved first
            return false;
        }

        if (invoice.getDateEstimated() == null)
                invoice.setDateEstimated(shipDate);
        invoice.setDateShipped(shipDate);

        Vector invoiceVect = new Vector();
        invoiceVect.add(invoice);
        DataModel.db.saveItems("Invoice", invoiceVect);
        updateScreens_Shipping(invoice);
        return true;
    }

    public boolean unsavedScreens(Invoice invoice) {
        rmk.gui.IScreen screen = rmk.ScreenController.getInstance()
                .getInvoiceScreen(invoice);
        if (screen == null) return false;
        if (screen.isEdited()) {
            screen.bringToFront();
            JOptionPane.showMessageDialog(null, "Must save Invoice first",
                    "Shipping Invoices", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    public static void updateScreens_Shipping(Invoice invoice) {
        rmk.gui.IScreen screen = rmk.ScreenController.getInstance()
                .getInvoiceScreen(invoice);

        String title;
        rmk.gui.DBGuiModel model;
        // Invoice Details Screen?
        screen = rmk.ScreenController.getInstance().getInvoiceScreen(invoice);
        if (screen != null) {
            model = ((rmk.gui.InvoiceDetailsScreen) screen).getModel();
            model = updateModelsInvoice(model, invoice);
//            ((rmk.gui.InvoiceDetailsScreen) screen).setData(model);
            ((rmk.gui.InvoiceDetailsScreen) screen).setInvoice(invoice);
        }
        //---------------------------------------
        // Customer Details Screen?
        screen = rmk.ScreenController.getInstance().getCustomerScreen(invoice);
        if (screen != null) {
            //  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + "updateScreens_Shipping:"+ "Updating:" +
            // title);
            model = ((rmk.gui.CustomerScreen) screen).getModel();
            model = updateModelsInvoice(model, invoice);
            ((rmk.gui.CustomerScreen) screen).setData(model);
        }
        // Payments Screen?
        screen = rmk.ScreenController.getInstance().getPaymentsScreen(invoice);
        if (screen != null) {
            //  	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + "updateScreens_Shipping:"+ "Updating:" +
            // title);
            model = ((rmk.gui.InvoicePaymentsScreen) screen).getModel();
            model = updateModelsInvoice(model, invoice);
            ((rmk.gui.InvoicePaymentsScreen) screen).setData(model);
        }
    }

    //============================================================================
    public static rmk.gui.DBGuiModel updateModelsInvoice(
            rmk.gui.DBGuiModel model, Invoice invoice) {
        Vector invoiceVect = model.getInvoiceData();
        boolean found = false;
        for (Enumeration enum = invoiceVect.elements(); enum.hasMoreElements();) {
            Invoice inv = (Invoice) enum.nextElement();
            if (inv.getInvoice() == invoice.getInvoice()) {
                inv.setDateShipped(invoice.getDateShipped());
                inv.setDateEstimated(invoice.getDateEstimated());
                inv.setDateOrdered(invoice.getDateOrdered());
                found = true;
            }
        }
        if (!found) {
            invoiceVect.add(invoice);
            model.setInvoiceData(invoiceVect);
        }
        return model;
    }

    //============================================================================
    private long getShippingInvoiceNumber() {
        String reply = "";
        long invoiceNum = 0;
        while (true) {
            reply = JOptionPane.showInputDialog("Invoice Number?", "");
            if (reply == null || reply.equals("")) return 0; // canceled
            try {
                invoiceNum = Long.parseLong(reply);
            } catch (Exception e) {
            }
            if (invoiceNum <= 0) {
                JOptionPane.showMessageDialog(null, "Invalid Invoice#:\n"
                        + reply, "Invalid Entry", JOptionPane.WARNING_MESSAGE);
            } else {
                long validation = validateInvoice(invoiceNum);
                if (validation != 0) return validation;
                //  		else
                //  		    JOptionPane.showMessageDialog(null, "NOT shippint
                // Invoice#:\n" + reply , "Invalid Entry"
                //  						  , JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    //============================================================================
    public long validateInvoice(long invoiceNumber) {
        Vector list = DataModel.db.getItems("Invoice", "Invoice ="
                + invoiceNumber);
        if (list == null || list.size() <= 0) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Invalid Invoice#:\n"
                    + invoiceNumber, "Invalid Entry",
                    JOptionPane.WARNING_MESSAGE);
            return 0;
        }
        Invoice invoice = (Invoice) list.get(0);
        GregorianCalendar originalDate = invoice.getDateShipped();
        if (originalDate != null) {
            Toolkit.getDefaultToolkit().beep();
            if (0 == JOptionPane.showConfirmDialog(null,
                    "Clear shipping Info?", "Confirm",
                    JOptionPane.YES_NO_OPTION)) return -invoiceNumber;

            if (!rmk.gui.Dialogs.yesConfirm("Confirm resetting ship date\n"
                    + "From: "
                    + carpus.util.DateFunctions.getSQLDateStr(originalDate)
                    + "\n" + "To    : "
                    + carpus.util.DateFunctions.getSQLDateStr(shipDate) + "\n"))
                    return 0;
        }
        list = DataModel.db.getItems("Customers", "Customerid ="
                + invoice.getCustomerID());
        Customer customer = (Customer) list.get(0);
        if (!rmk.gui.Dialogs.yesConfirm("Confirm\n" + "Invoice: "
                + invoiceNumber + "\n" + "Customer: " + customer.getLastName()
                + ", " + customer.getFirstName())) return 0;

        if (customer.getTerms().equals("" + customer.TERMS_IN_ADVANCED)) { // confirm payments have been made
            double due = sys.financialInfo.getInvoiceDue(invoice);

            if (due >= .004) {
                Toolkit.getDefaultToolkit().beep();
                if (!rmk.gui.Dialogs.yesConfirm("Confirm Shipping for\n"
                        + "Invoice: " + invoiceNumber + "\n" + "Customer: "
                        + customer.getLastName() + "\n " + due + "\n" + "DUE "))

                return 0; // DON'T ship payments with no invoices
            }
        }

        return invoiceNumber;
    }

    //============================================================================
    //============================================================================
    //============================================================================
    public static void main(String args[]) throws Exception {
        rmk.gui.Application.main(args);
    }
}

