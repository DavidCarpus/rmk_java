package rmk.reports;

import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.Address;
import rmk.database.dbobjects.InvoiceEntries;
import java.text.SimpleDateFormat;
import java.util.*;


public interface I_ReportData{
    public static rmk.DataModel sys = rmk.DataModel.getInstance();
    static final SimpleDateFormat dateFormatter = new SimpleDateFormat ("MM/dd/yyyy");
//    static final NumberFormat priceFormatter;
    Invoice invoice=null;
    Customer customer=null;
    Vector listData=null;
    Vector invoices=null;

    public void setInvoices(Vector inv);

    public Vector getInvoices();
    public Vector getCustomers();

    public String[] getListRow(int row);
    public int getTotalListRows();

    public void setInvoiceNumber(int id) throws Exception;
    public void setFormat(int format);

    public Vector getInvoiceItems(Invoice invoice);
    public Vector getInvoiceItemsDealer(Invoice invoice);
    public String[][] getRow_LongForm(InvoiceEntries entry);
    public String[][] getRow_ShortForm(InvoiceEntries entry);
    public String[] getCustomerAddress(Customer customer, Address address);
    public String[] getCustomerAddress();
    public String[] invoiceShippingInstructions();
    public String [] invoiceInfo();
    public String [] acknowledgeInfo();
    public String [] invoiceShipDates();
    public String getCurrentDate();
    public String [] remittanceAddress();
    public String [] invoiceTotals();
    public String[] getTerms();
    public String invoiceComment();
    public String [] invoicePercentages();

}
