package rmk.reports;

import java.awt.print.*;
import java.awt.*;
import java.awt.geom.*;

import rmk.ErrorLogger;
import rmk.database.dbobjects.Customer;
import rmk.database.dbobjects.Invoice;
import Configuration.Config;

public class InvoiceReport extends BaseReport implements ReportInterface {
    boolean useGreyBars=true;
    boolean grey=false;   
    int rowsRendered = 0;    
    int format = 0;    
    static final int maxCommentLength=50;
    ReportData data;
    
    public InvoiceReport() {
        super();
        data = new ReportData();
    }
    
    public InvoiceReport(int invoice) throws Exception {
        super();
        data = new ReportData();
        data.setInvoiceNumber(invoice, maxCommentLength);
    }
//  ------------------------------------------------------------------
    public void setCustomer(Customer cust) {
        data.setCustomer(cust);
    }
//------------------------------------------------------------------
    public Invoice getInvoice() {
        return data.getInvoice();
    }
//  ------------------------------------------------------------------
    public void setInvoice(Invoice inv) {
        data.setInvoice(inv);
        data.setFormat(format, maxCommentLength);
    }
//  ------------------------------------------------------------------
    public void setFormat(int format) {
        this.format = format;
        data.setFormat(format, maxCommentLength);
        currPage = 0;
    }
//  ------------------------------------------------------------------
    public void setInvoiceNumber(int id) throws Exception {
        data.setInvoiceNumber(id, maxCommentLength);
        data.setFormat(format, maxCommentLength);
    }
//  ------------------------------------------------------------------
    public int getTotalNumberOfPages() {
        return getNumberOfPages();
    }    
//  ------------------------------------------------------------------
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
    throws PrinterException {
        Graphics2D g2 = (Graphics2D) graphics;
        double startX=pageFormat.getImageableX();
        double startY=pageFormat.getImageableY();
        if(getDestination() == PRINT_TO_PRINTER){
            startX += 20;
        }
        g2.translate(startX, startY);
        double endX = pageFormat.getImageableWidth();
        double endY = pageFormat.getImageableHeight();

        double width = endX - startX - 2;
        double pageHeight = endY - startY;
        int column = 0;
        
        //    	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ width + ":"+
        // height);
        
        g2.setFont(infoFont);
        FontMetrics metrics = g2.getFontMetrics();
        int incr = (int) metrics.getHeight();
        
        currPage = pageIndex;
        
        if (pageIndex > getNumberOfPages()) { return (NO_SUCH_PAGE); }
        
        g2.setFont(infoFont);
        pageHeader(g2, new Rectangle2D.Double(1, 20, width, 10+PAGE_HEADER_HEIGHT), pageIndex + 1);
        boolean lastPage = false;
        if (pageIndex == getNumberOfPages()) 
            lastPage = true;
        
        if (data.getInvoice() != null) {
            int totalPages = getNumberOfPages();
            if (pageIndex == 0) {
                grey = false;
                reportHeader(g2, new Rectangle2D.Double(1, 80, width, REPORT_HEADER_HEIGHT));
            }
            if (totalPages > 0) {
                if (pageIndex == 0) { // first of several pages
                    itemList(g2, new Rectangle2D.Double(1, LIST_START_LOCATION, 
                            width, pageHeight - PAGE_FOOTER_HEIGHT), 
                            0);
                } else {
                    int endRow = 0;
                    if (lastPage) { // last page
                        endRow = (int) pageHeight - listFontHeight - REPORT_FOOTER_HEIGHT;
                    } else { // middle pages
                        endRow = (int) pageHeight - PAGE_FOOTER_HEIGHT;
                    }
                    itemList(g2, new Rectangle2D.Double(1, 90, width, endRow),
                            rowsRendered(pageIndex));
                }
            } else { // only one page
                int endRow = (int) pageHeight  - listFontHeight - REPORT_FOOTER_HEIGHT;
                itemList(g2, new Rectangle2D.Double(1, LIST_START_LOCATION, width, endRow),
                        rowsRendered(pageIndex));
            }
            // FOLD lines
            g2.draw(new Line2D.Double(1, FOLD_LINE_ROW, 3, FOLD_LINE_ROW));
            g2.draw(new Line2D.Double(width - 2, FOLD_LINE_ROW, width, FOLD_LINE_ROW));
            
            if (lastPage) { // last page
                g2.setFont(listFont);
                reportFooter(g2, width, new Point(1, (int) pageHeight - REPORT_FOOTER_HEIGHT),
                        pageIndex + 1);
            }
        }
        //  	g2.setFont(new Font ("serif", Font.BOLD, 10));
        //  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ width);
        
        if (lastPage) g2.translate(120, 10); // translate to start
        //  	width += 100;
        //  	drawFormatted(g2,"****TEST****", 0, 0);
        
        pageFooter(g2, width, new Point(0, (int) pageHeight - 10), pageIndex + 1);
        return (PAGE_EXISTS);
    }
//  ------------------------------------------------------------------
    int rowsRendered(int currentPage) {
        int results = 0;
        
        if (currentPage-- >= 1) results += PAGE_ONE_ROWS;
        while (currentPage-- > 0)
            results += OTHER_PAGE_ROWS;
        
        //    	ErrorLogger.getInstance().logMessage("rowsRendered:"+ results + ": P"+currentPage);
        return results;
    }
    //-------------------------------------------------------------------
    public int getNumberOfPages() {
        int pages = 0;
        int listRows = 0;
        int lstHt = 0;
        if (data.getInvoice() != null) {
            listRows = data.getTotalListRows();
            //  	    ErrorLogger.getInstance().logMessage("getNumberOfPages:listRows:" + listRows);
            if (listRows <= ONE_PAGE_ROWS) { // only 1 page?
                return 0; }
            
            pages++;
            listRows -= PAGE_ONE_ROWS;
            
            while (listRows > LAST_PAGE_ROWS) { // middle pages
                pages++;
                listRows -= OTHER_PAGE_ROWS;
            }
        } else {
            ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
                    + "No Invoice???:");
        }
        return pages;
    } 
//  ------------------------------------------------------------------
    public void pageHeader(Graphics2D g2, Rectangle2D.Double region,
            int pageIndex) {
        
        g2.setFont(listFont);
        double incr = g2.getFontMetrics().getHeight();
        int height = (int) (region.height - region.y);
        g2.translate(region.x, region.y); // translate to pt
        
        String text[] = { "Printed:", "Page:", "RANDALL MADE KNIVES", "Phone:",
        "Fax:"};
        int i = 0;
        printLeft(g2, text[i++] + " " + data.getCurrentDate(), region.width, 0);
        
        // ------------ Page x of y
        String pageStr = text[i++] + pageIndex;
        if (getNumberOfPages() > 0) {
            pageStr += " of " + (getNumberOfPages() + 1);
        }
        printRight(g2, pageStr, region.width, 0);
        
        // ------------ Center Header
        g2.setFont(new Font("serif", Font.BOLD, listFontHeight + 2));
        printCentered(g2, text[i++], region.width, 0);
        g2.setFont(listFont);
        printCentered(g2, text[i++] + Config.getBusinessNumber(), region.width,
                incr);
        printCentered(g2, text[i++] + Config.getFaxNumber(), region.width,
                2 * incr);
        g2.translate(-region.x, -region.y); // translate back
        
        g2.setFont(new Font("serif", Font.BOLD, 14));
        String invoiceStr = "Invoice";
        Invoice invoice = data.getInvoice();
        if (invoice != null) {
            invoiceStr += ":  " + invoice.getInvoice();
        }
        printCentered(g2, invoiceStr, region.width, height - 8);
        g2.setFont(infoFont);
        //  	g2.setFont(new Font ("serif", Font.PLAIN, 10));
    }
//  ------------------------------------------------------------------
    void reportHeader(Graphics2D g2, Rectangle2D.Double region) {
        //  	invoiceInfo(g2, region.width, new Point(1,80));
        g2.setFont(infoFont);
        invoiceInfo(g2, 
                new Rectangle(1, 80, (int) region.width, (int) region.height),
                data.invoiceInfo(),
                data.getCustomer(), 
                data.invoiceShipDates()
                );
        
        addressInfo(g2, region.width, listFontHeight*7, new Point(1, 125), 
                data.getCustomerAddress(), data.invoiceShippingInstructions());
        
        g2.draw(new Line2D.Double(0, 110, region.width, 110));
        g2.draw(new Line2D.Double(0, 65, region.width, 65));
    }    
//  ------------------------------------------------------------------
    boolean itemList(Graphics2D g2, Rectangle2D.Double region, int startIndex) {
        g2.setFont(listFont);
        g2.translate(region.x, region.y); // translate to pt

        FontMetrics metrics = g2.getFontMetrics();
        int fontHeight = (int) metrics.getHeight();
        int height = (int) (region.height - region.y);
        
        int totalDataRows = data.getTotalListRows();
        
        int lstHt = getListHeight(listFontHeight, 0, totalDataRows, data);
        
        int total = 0;
        int rowsRendered = 0;
        
        int rowLocation = -(fontHeight + 3);
        String labels[] = { "Qty", "Model", "Description", "Price", "Extended"};
        int colLocations[] = { 10, 40, 100, (int) region.width - 105,
                (int) region.width - 50};

//        g2.setColor(Color.LIGHT_GRAY);
//        g2.fill3DRect(0, rowLocation-fontHeight, (int)region.width,  fontHeight+2, false) ;
//        g2.setColor(Color.BLACK);

        for (int col = 0; col < colLocations.length; col++) {
            g2.drawString(labels[col], colLocations[col], rowLocation);
        }

        rowLocation += fontHeight;
        
        String dataRow[] = data.getListRow(startIndex);
        Color txtColor = Color.BLACK;
        for (int i = startIndex; i < totalDataRows && total < height; i++) {
            total += getRowHeight(fontHeight, i, null);
            String nextRow[] = {"",""};
            if(i < totalDataRows)
                nextRow = data.getListRow(i+1);
            dataRow = data.getListRow(i);
            g2.setColor(Color.BLACK);
            if(useGreyBars && grey){
                g2.setColor(HIGHLIGHT_COLOR);
                g2.fill3DRect(1, rowLocation-fontHeight+4, (int)region.width, fontHeight+1, false) ;
                g2.setColor(Color.BLACK);
            }

            if (dataRow != null) {
                for (int col = 0; col < dataRow.length; col++) {
                    try {
                        String txt = dataRow[col];
                        if (txt == null) txt = "";
                        g2.translate(colLocations[col], 0);
                        // translate to start of col
                        boolean priceCol = (col > colLocations.length - 3);
                        if (!priceCol) {
                            String baseTxt = unformatted(txt);
                            double txtLen = metrics.charsWidth(baseTxt
                                    .toCharArray(), 0, baseTxt.length());
                            double x = 0;
                            drawFormatted(g2, txt, (int) x, rowLocation);
                        } else {                            
                            if(txt != null && txt.trim().length() > 0)
                                g2.drawString("$", 0, rowLocation);
                            if(col == colLocations.length-1){
                                if(txt.indexOf("<U>") >= 0)
                                    printRight(g2, txt, 45, rowLocation);
                                else
                                    printRight(g2, txt, 50, rowLocation);
                                
                            } else{
                                printRight(g2, txt, 47, rowLocation);
//                            g2.drawString(txt, 0, rowLocation);
                            }
                        }
                        g2.translate(-colLocations[col], 0); // translate back
                    } catch (Exception e) {
                        rmk.ErrorLogger.getInstance().logError(
                                " col:" + col + " row:" + i, e);
                    } // end of try-catch
                }
//                boolean addDashedLine = ((dataRow[2] != null 
//                        && dataRow[2].indexOf("SubTotal") >= 0));
                
//                try{
//                if(format == HtmlReportDialog.LONG_FORMAT && i < totalDataRows)
//                    if(nextRow != null && nextRow[0] != null && nextRow[0].length() > 0) // next row col 0
//                        addDashedLine = true;
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//                if (addDashedLine) {
//                    Rectangle2D.Double coordinates = new Rectangle2D.Double();
//                    coordinates.setRect(0, rowLocation + 2, region.width,
//                            rowLocation + 2);
//                    drawDashedLine(g2, coordinates);
//                }
                if(!sameKnife(dataRow, nextRow, i-startIndex) ){
                    grey = !grey;
                }

                rowLocation += fontHeight;

            }
            rowsRendered++;
            //  	    rowLocation += fontHeight;
        }
        g2.setColor(Color.BLACK);

        drawListColumSeperators(g2, colLocations, -fontHeight, height);

        Rectangle2D.Double rectangle = new Rectangle2D.Double();
        rectangle.setRect(0, -fontHeight, region.width, height + fontHeight);
        g2.draw(rectangle);
        rectangle.setRect(0, -2 * fontHeight, region.width, fontHeight);
        g2.draw(rectangle);
        
        g2.translate(-region.x, -region.y); // translate back
        g2.setFont(infoFont);

        
        return grey;
    }
    //===================================================================
    void reportFooter(Graphics2D g2, double width, Point pt, int pageIndex) {
        g2.setFont(infoFont);
        g2.translate(pt.x, pt.y); // translate to pt
        
        //------------------------------------------------------
        int box1Width=200;
        int box1Height=infoFontHeight * 4+1;
        drawRectangle(g2, infoFontHeight, box1Width, box1Height);
        g2.translate(2, 0); // translate
        hangingIndent(g2, "Terms ", data.getTerms());
        g2.translate(-2, 0); // translate

        int taxIDLocation=infoFontHeight * 3;
        g2.translate(0, taxIDLocation); // translate
        printLeft(g2, data.getTaxID(), 0, 0);
        g2.translate(0, -taxIDLocation); // translate        
        //------------------------------------------------------
        int box2Width=170;
        int box2Height=infoFontHeight * 3 + 12;
        g2.translate(0, box1Height+2); // translate
        drawRectangle(g2, infoFontHeight, box2Width, box2Height);
        
        g2.translate(2, 1); // translate
        hangingIndent(g2, "Pay To ", data.remittanceAddress());
        g2.translate(-2, -1); // translate
        g2.translate(0, -(box1Height+2)); // translate
        //------------------------------------------------------
        int commentStart = box1Height + box2Height+10;
        g2.translate(0, commentStart); // translate
        printLeft(g2, data.invoiceComment());
        g2.translate(0, -commentStart); // translate back
        
        //  	g2.drawString (data.invoiceComment(), 0, 0);
        
//        String cc = data.customerCCNum();
//        if (cc != null && cc.length() > 1) {
//            g2.setFont(listFont);
//            g2.translate(0, 90); // shift
//            printLeft(g2, "CC#:" + cc, 0, 0);
//            g2.translate(0, -90); // shift back
//            g2.setFont(infoFont);
//        }
        
        String accountingLabels[] = { "Total", "- Discount", "SubTotal",
                "+Shipping", "+Tax", "-Payments", "LastPayment", "Balance"};
        
        g2.translate((int) width - 250, 0); // translate
        accounting(g2, accountingLabels, data.lastInvoicePayment(), 
                data.invoiceTotals(), data.invoicePercentages());
        g2.translate(-((int) width - 250), 0); // translate back

        g2.translate(-60, 0); // shift back        
        g2.translate(-60, 0); // shift back        
        
        g2.translate(-pt.x, -pt.y); // translate back
    }
//----------------------------------------------------------------
    public void printSplit(Graphics2D g2, String txt[][], double width, Point pt) {
        FontMetrics metrics = g2.getFontMetrics();
        double incr = metrics.getHeight();
        
        g2.translate(pt.x, pt.y); // translate to pt
        for (int i = 0; i < txt.length; i++) {
            printLeft(g2, txt[i][0], width, i * incr);
            printRight(g2, txt[i][1], width, i * incr);
        }
        g2.translate(-pt.x, -pt.y); // translate back
    }
//  ----------------------------------------------------------------
    public static void main(String args[]) throws Exception {
        //    	rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog(null,
        // rmk.gui.HtmlReportDialog.INVOICE_REPORT);
        rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog();
        rpt.exitOnCancel = true;
        rmk.reports.InvoiceReport tst = new rmk.reports.InvoiceReport(42699);
        ErrorLogger.getInstance().logMessage(""+tst.getInvoice());
        rpt.setReport(tst);
        //    	rpt.setInvoice(60001); // 42496, 44424, 42683, 50000, 42684, 44732, 53163,
        // 53384, 44800, 53483
        rpt.setVisible(true);
    }
}
