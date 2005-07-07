package rmk.reports;

import java.awt.print.*;
import java.awt.*;
import java.awt.geom.*;

import rmk.ErrorLogger;
import rmk.database.dbobjects.Invoice;
import java.util.*;

public class TaxOrdered 
    extends BaseReport
    implements ReportInterface
{
    static final int PAGE_ONE_ROWS = 62;
    static final int OTHER_PAGE_ROWS = 62;

    int listFontHeight = 10;
    Font listFont = new Font ("serif", Font.PLAIN, listFontHeight-2);
//      int totalPages;
    int rowsRendered=0;
    int format=0;
    ReportDataInvoicesList data;
	static final int PRINTABLE_PAGE_WIDTH = 500;
	static final int LAST_COLUMN_START = 510;
    public TaxOrdered(){
	super();
	data = new ReportDataInvoicesList(PRINTABLE_PAGE_WIDTH-LAST_COLUMN_START,listFont);
    }

    public TaxOrdered(GregorianCalendar orderedDate) throws Exception{
	super();
	data = new ReportDataInvoicesList(PRINTABLE_PAGE_WIDTH-LAST_COLUMN_START,listFont);
	data.setOrderedDate(orderedDate);
    }

    public Invoice getInvoice(){return null;}

    public void setInvoice(Invoice inv){}
    public void setFormat(int format){
	this.format = format;
//  	data.setFormat(format);
	currPage=0;
    }
    public void setInvoiceNumber(int id) throws Exception{
//  	data.setInvoiceNumber(id);
//  	data.setFormat(format);
    }
    public int getTotalNumberOfPages(){
	return getNumberOfPages();
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException{
	Graphics2D g2 = (Graphics2D) graphics;
	g2.translate (pageFormat.getImageableX(), pageFormat.getImageableY() );
	double width = pageFormat.getImageableWidth()-pageFormat.getImageableX()-2;
	double height = pageFormat.getImageableHeight()-pageFormat.getImageableY();
	int column=0;


	g2.setFont(listFont);		
	FontMetrics metrics = g2.getFontMetrics();
	int incr=(int)metrics.getHeight();

	currPage = pageIndex;
	
	if(pageIndex > getNumberOfPages()){
	    return (NO_SUCH_PAGE);
	}

	g2.setFont(new Font ("serif", Font.PLAIN, 10));
	pageHeader(g2, new Rectangle2D.Double (1,20, width, 80), pageIndex+1);

	
	if(data.getInvoices() != null){
	    int totalPages = getNumberOfPages();
	    if(pageIndex == 0){
		reportHeader(g2, new Rectangle2D.Double (1,80, width, 40));
	    }
	    g2.setFont(listFont);
	    if(totalPages > 0){
		if(pageIndex == 0){// first of several pages
		    itemList(g2, new Rectangle2D.Double (1,80, width, height), 0);
		} else {
		    int endRow=0;
		    if(pageIndex == totalPages){// last page
			endRow = (int)height-listFontHeight;
		    } else {// middle pages
			endRow = (int)height;
		    }
		    itemList(g2, new Rectangle2D.Double (1,80, width, endRow), rowsRendered(pageIndex));
		}
	    } else { // only one page	
		int endRow = (int)height-100-listFontHeight;
		itemList(g2, new Rectangle2D.Double (1,110, width, endRow), rowsRendered(pageIndex));
	    }
	
	    if(pageIndex == getNumberOfPages()){ // last page
		g2.setFont(listFont);		
		reportFooter(g2, width, new Point(1,(int)height-100), pageIndex+1);
	    }
	}
	g2.setFont(new Font ("serif", Font.BOLD, 10));
	pageFooter(g2, width, new Point(1,(int)height-10), pageIndex+1);	    
	return (PAGE_EXISTS);
    }

    //===================================================================
    int rowsRendered(int currentPage){
	int results = 0;
	
	while(currentPage-- > 0)
	    results += PAGE_ONE_ROWS;

	return results;
    }
    //-------------------------------------------------------------------
    public int getNumberOfPages(){
	int pages=0;
	int listRows = 0;
	int lstHt = 0;
	if(data.getInvoices() != null){
	    try{
		listRows = data.getTotalListRows(); 
		while(listRows > PAGE_ONE_ROWS){ // all pages
		    pages++;
		    listRows -= PAGE_ONE_ROWS;
		}
	    } catch (Exception e){ }
	} else{
	    ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ "No Invoice???:");
	}
	return pages;
    }
    //-------------------------------------------------------------------
    public PageFormat getPageFormat(int pageIndex){
	return new PageFormat();
    }
    //-------------------------------------------------------------------
    public Printable getPrintable(int pageIndex){
	return this;
    }
    //===================================================================
    public void pageHeader(Graphics2D g2, Rectangle2D.Double region, int pageIndex){
  	double incr=g2.getFontMetrics().getHeight();
	
	g2.translate (region.x, region.y ); // translate to pt

	String text[] = {"Printed:" , "Page:", "Randall Made Knives", "Sales Tax Ordered Report", "Ordered:"};
	int i=0;
	printLeft(g2, text[i++] + " " + data.getCurrentDate(), region.width, 0);

	// ------------  Page x of y
	String pageStr = text[i++] + pageIndex;
	if(getNumberOfPages() > 0){
	    pageStr += " of " + (getNumberOfPages()+1);
	}
	printRight(g2, pageStr, region.width, 0);

	// ------------  Center Header
	printCentered(g2, text[i++], region.width, 0);
  	printCentered(g2, text[i++], region.width, incr);
	printCentered(g2, text[i++] + data.getShipDateShort(), region.width, 2*incr);
	g2.translate (-region.x, -region.y ); // translate back
    }

    //===================================================================
    void reportHeader(Graphics2D g2, Rectangle2D.Double region){
//  	invoiceInfo(g2, region.width, new Point(1,80));
//  	g2.draw(new Line2D.Double(0,110, region.width, 110));
//  	g2.draw(new Line2D.Double(0,65, region.width, 65));
    }
    //----------------------------------------------------------------
    int itemList(Graphics2D g2, Rectangle2D.Double region, int startIndex) {
        g2.translate(region.x, region.y); // translate to pt

        FontMetrics metrics = g2.getFontMetrics();
        int fontHeight = (int) metrics.getHeight();
        int height = (int) (region.height - region.y);

        int totalDataRows = 0;
        try {
            totalDataRows = data.getTotalListRows();
        } catch (Exception e) {
        } // end of try-catch

        int lstHt = getListHeight(listFontHeight, 0, totalDataRows, data);

        Rectangle2D.Double rectangle = new Rectangle2D.Double();
        rectangle.setRect(0, -fontHeight, region.width, height + fontHeight);
        g2.draw(rectangle);
        rectangle.setRect(0, -2 * fontHeight, region.width, fontHeight);
        g2.draw(rectangle);
        int total = 0;
        int rowsRendered = 0;

        int rowLocation = -(fontHeight + 3);
        String labels[] = { "Invoice", "Ordered",  "NonTaxable", "Taxed-Disc+Ship",
                 "Rate", "Taxes", "State", "Shipped", "Payments"};
        int colLocations[] = { 8, 50, 110, 170, 250, 295, 350, 410, 470, LAST_COLUMN_START};
        for (int col = 0; col < labels.length; col++) {
            String txt = labels[col];
            int x = colLocations[col] + 5;
            if (col == 3) x -= 5;
            g2.drawString(txt, x, rowLocation);
        }

        rowLocation += fontHeight;
        for (int i = startIndex; i < totalDataRows && total < height; i++) {
            total += getRowHeight(fontHeight, i, null);
            String dataRow[] = data.getListRow(i);

            if (dataRow != null) {
                addListRowToDisplay(g2, dataRow, rowLocation, colLocations);
                rowLocation += fontHeight;
            }
            rowsRendered++;
        }
        for (int col = 1; col < colLocations.length - 1; col++) { 
            // -1 is to stop last line
            g2.draw(new Line2D.Double(colLocations[col] - 5, -fontHeight,
                    colLocations[col] - 5, height));
        }

        g2.translate(-region.x, -region.y); // translate back

        return rowsRendered;
    }
    
    void addListRowToDisplay(Graphics2D g2, String dataRow[], int rowLocation, int colLocations[]){
    	FontMetrics metrics = g2.getFontMetrics();
        for (int col = 0; col < dataRow.length; col++) {
            try {
                String txt = dataRow[col];
                String baseTxt = unformatted(txt);
                double txtLen = metrics.charsWidth(baseTxt.toCharArray(), 0,
                        baseTxt.length());
                g2.translate(colLocations[col], 0); // translate to start of col
                if (col == 7|| col == 6) {
                    g2.drawString(txt, 0, rowLocation);
                } else if (col > 1) {
                    double x = 40 - txtLen;
                    drawFormatted(g2, txt, (int) x, rowLocation);
                } else {
                    g2.drawString(txt, 0, rowLocation);
                }
                g2.translate(-colLocations[col], 0); // translate back
            } catch (Exception e) {
                rmk.ErrorLogger.getInstance().logError(" col:" + col, e);
            } // end of try-catch
        }
    }

    //===================================================================
    void reportFooter(Graphics2D g2, double width, Point pt, int pageIndex){
	g2.translate (pt.x, pt.y ); // translate to pt


	g2.translate (-pt.x, -pt.y ); // translate back
    }
    //===================================================================
    public void pageFooter(Graphics2D g2, double width, Point pt, int pageIndex){
//    	String text[] = {"Thank you for your order.", "Deposits are not transferable or refundable"};
//  	g2.translate (pt.x, pt.y ); // translate to pt
//  	int i=0;
//  	printLeft(g2, text[i++], width, 0);
//  	printRight(g2, text[i++], width, 0);
//  	g2.translate (-pt.x, -pt.y ); // translate back
    }

    //===================================================================
    //===================================================================
    //===================================================================
    void hangingIndent(Graphics2D g2, String label, String txt[]){
	FontMetrics metrics = g2.getFontMetrics();
	int incr=(int)metrics.getHeight();
	int space = metrics.charsWidth(label.toCharArray(), 0, label.length());
	printLeft(g2, label, 0, 0);
	g2.translate(space, 0 ); // translate
	printLeft(g2, txt );
	g2.translate(-space, 0 ); // translate back
    }
    //----------------------------------------------------------------
    public void printSplit(Graphics2D g2, String txt[][] , double width, Point pt){
	FontMetrics metrics = g2.getFontMetrics();
	double incr=metrics.getHeight();

	g2.translate (pt.x, pt.y ); // translate to pt
	for(int i=0; i< txt.length; i++){
	    printLeft( g2, txt[i][0], width, i*incr);
	    printRight(g2, txt[i][1], width, i*incr);
	}
	g2.translate (-pt.x, -pt.y ); // translate back
    }


    public static void main(String args[]) throws Exception{
//    	rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog(null, rmk.gui.HtmlReportDialog.INVOICE_REPORT);
  	rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog();
  	rpt.exitOnCancel=true;
	java.util.GregorianCalendar date = new java.util.GregorianCalendar(2003, 11, 1);
  	rmk.reports.TaxOrdered tst = new rmk.reports.TaxOrdered(date);
	tst.setFormat(ReportDataInvoicesList.FORMAT_TAX_ORDERED); // FORMAT_TAX_SHIPPED FORMAT_MINIS

  	rpt.setReport(tst);
//    	ErrorLogger.getInstance().logMessage(tst.getInvoice());
//    	rpt.setInvoice(60001); // 42496, 42683, 50000, 42684
  	rpt.setVisible(true);
    }
}
