/*
 * Created: Jul 25, 2004
 * By: David Carpus
 * 
 * Last Modified:
 * Last Modified by:
 * 
 */
package rmk.database;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Vector;

import carpus.database.DBInterface;

import Configuration.Config;

import rmk.ErrorLogger;
import rmk.database.Workers.InvoiceWorker;
import rmk.database.Workers.PaymentsWorker;
import rmk.database.dbobjects.Invoice;
import rmk.database.dbobjects.Payments;


/**
 * @author dcarpus
 *
 * 
 */
public class Cleanup {

    public void paymentCreditCardNumbers(Connection cx) throws Exception{
        InvoiceWorker invDB = new InvoiceWorker();
        Vector badNums = new Vector();
        for(int i=1; i< 1000; i++){
            Invoice inv = invDB.fetch(cx, i);
            if(inv != null){
                Vector bad = fixInvoiceCCPayments(cx, inv);
                if(bad != null && bad.size() > 0)
                    badNums.addAll(bad);
            }
			if (i % 100 == 0)
				System.out.print("\n" + i);

        }
        ErrorLogger.getInstance().logMessage(this.getClass().getName() + "\nBad CheckNumbers?:");
        for(Iterator iter = badNums.iterator(); iter.hasNext();){
            ErrorLogger.getInstance().logMessage(""+iter.next());
        }
    }
    public Vector fixInvoiceCCPayments(Connection cx, Invoice inv) throws Exception{
        Vector badCheckNumbers=new Vector();
        
        System.out.print(".");
        PaymentsWorker paymentsDB = new PaymentsWorker();
        Vector payments = paymentsDB.load(cx, "invoice = " + inv.getInvoice());
        for(Iterator iter = payments.iterator();iter.hasNext(); ){
            Payments payment = (Payments) iter.next();
            int paymentType=(int) payment.getPaymentType();
            if(paymentType <=0)
                paymentType = paymentTypeFromCheckNum(payment.getNumber());

            if(paymentType == 2 || paymentType == 3 || paymentType == 4 || paymentType == 8 ){
                payment.setPaymentType(paymentType);
                String number = inv.getCreditCardNumber();
                if(number != null && number.length() > 0){
                    number = FinancialInfo.removeCardNumberDashes(number);
                    payment.setNumber(number);
                    ErrorLogger.getInstance().logMessage(this.getClass().getName() + payment);
                }
                
            } else if(paymentType == 5){ // cash, ignore
                
            } else if(paymentType == 1){ // check, just set type
                payment.setPaymentType(paymentType);
                
            } else if(paymentType == 6){ // MoneyOrder, just set type
                payment.setPaymentType(paymentType);
                
            } else{
                badCheckNumbers.add(payment.getNumber());
//                System.out.print("\nUnknown CheckNumber: " + payment.getCheckNumber());
            }
        }
        return badCheckNumbers;
    }
    
    int paymentTypeFromCheckNum(String num){
        num = num.toUpperCase();
        if(num == null) return 0;
        
        num = num.trim();

    	if(num.equals("CK") || num.equals("PRSL CK") ){
    	    return 1;
        
    	} else if(num.equals("VI") || num.equals("VISA")){
            return 2;
            
        } else if(num.equals("MC") || num.equals("MASTERCARD")){
            return 3;
            
        } else if(num.equals("DI")){
            return 4;
            
        } else if(num.equals("CASH") || num.equals("CA")){
            return 5;
            
        } else if(num.equals("MO")){
            return 6;
            
        } else if(num.equals("REFUND")){
            return 7;
            
        } else if(num.equals("CC")){
            return 8;
            
        }
        
        try {
            long cnvLng = Long.parseLong(num);
            if(num.length() <= 10)
                return 1; // Check number
            
        } catch (Exception e) {
            return 0;
        }
        return 0; // UNKNOWN
    }
    
    public static void main(String[] args) throws Exception {
        DBInterface db = Config.getDB();
        db.connect();
        Cleanup cleanUp = new Cleanup();
        cleanUp.paymentCreditCardNumbers(db.getConn());
    }
}
