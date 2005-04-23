package rmk.reports;

import java.awt.print.PageFormat;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.JEditorPane;

import rmk.ErrorLogger;

public class Printing {
    public static boolean printerIsSelectable=false;

    //--------------------------------------------------------------------------------
    public static void printReport(rmk.reports.ReportInterface rpt) throws Exception {
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

        DocAttributeSet das = new HashDocAttributeSet();
        das.add(getPrintArea());

        PrintRequestAttributeSet pras = getPRAS();
 
        PrintService service = 
            getPrinterService(pras, flavor);
                
        pras.add(getPrintArea());
        
//        pageFormat = printerJob.pageDialog(pageFormat);
        
        DocPrintJob pj;
        pj = service.createPrintJob();
        PageFormat pf = new PageFormat();
        //  	    Attribute attribs[] = pras.getAttributes().toArray();

        rpt.setDestination(ReportInterface.PRINT_TO_PRINTER);
        Doc doc = new SimpleDoc(rpt, flavor, das); 
        print(doc, pj, pras);
        rpt.setDestination(ReportInterface.PRINT_TO_SCREEN);
    }

    static PrintService getPrinterService(PrintRequestAttributeSet pras, DocFlavor flavor) throws Exception{
        PrintService service = null;
        if(!printerIsSelectable){
            service = PrintServiceLookup.lookupDefaultPrintService();
        } else{
            
            if(carpus.util.SystemPrefrences.runningOnWindows()){
                PrintService[] pservices =
                    PrintServiceLookup.lookupPrintServices(flavor, null);
                service = ServiceUI.printDialog(null, 200, 200, pservices,
                        null,flavor, pras);
//                if (pservices.length >= 2) {
//                    service = pservices[1];
//                } else{
//                    service = pservices[0];
//                }
            }
        }
        
//        Object lst[] = service.getAttributes().toArray();
//        for(int i=0; i< lst.length; i++)
//            ErrorLogger.getInstance().logMessage(
//                    ((javax.print.attribute.Attribute)lst[i]).getName());
//        
//        ErrorLogger.getInstance().logMessage(service.getSupportedAttributeValues(MediaPrintableArea.class,
//                null, pras));
//        
//        service=null;
//        if (service == null) { // No Printers installed?
//            throw new Exception("no printer(s)?");
//        }
        
        return service;
    }
    static PrintRequestAttributeSet getPRAS(){
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        pras.add(MediaSizeName.NA_LETTER);

        
        //		MediaPrintableArea pa = new MediaPrintableArea(13,13,200,260,
        // MediaPrintableArea.MM);
        pras.add(getPrintArea());
        return pras;
    }
    public static MediaPrintableArea getPrintArea(){
        MediaPrintableArea pa = new MediaPrintableArea(0.5f, 0.5f,
                8.5f, 11.0f, MediaPrintableArea.INCH);
        return pa;
    }
    
    public static void printToDO(JEditorPane data) throws Exception {
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

        DocAttributeSet das = new HashDocAttributeSet();
        PrintRequestAttributeSet pras = getPRAS();
        
        PrintService service = 
            PrintServiceLookup.lookupDefaultPrintService();

        DocPrintJob pj;
        pj = service.createPrintJob();

        Doc doc = new SimpleDoc(data, flavor, das);
        print(doc, pj, pras);
    }

    static void print(Doc doc, DocPrintJob pj, PrintRequestAttributeSet pras){
        try {
            pj.print(doc, pras);
        } catch (PrintException printError) {
        	ErrorLogger.getInstance().logError("PrintException when printing:",printError);
        } catch (Exception ex) {
        	ErrorLogger.getInstance().logError("Exception when printing:",ex);
        }
    }
    
    public static void main(String args[]) throws Exception {
        //    	rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog(null,
        // rmk.gui.HtmlReportDialog.INVOICE_REPORT);
        rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog();
        rpt.exitOnCancel = true;
        rmk.reports.InvoiceReport tst = new rmk.reports.InvoiceReport(53163);
        ErrorLogger.getInstance().logMessage(""+tst.getInvoice());
        rpt.setReport(tst);
        //    	rpt.setInvoice(60001); // 42496, 42683, 50000, 42684, 44732
        rpt.setVisible(true);
    }

}
