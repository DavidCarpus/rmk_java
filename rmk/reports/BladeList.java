package rmk.reports;
import java.awt.print.*;
import java.awt.*;
import java.awt.geom.*;

import rmk.ErrorLogger;
import rmk.database.dbobjects.Invoice;
import java.util.*;
public class BladeList extends BaseReport implements ReportInterface {
    boolean useGreyBars=true;
    boolean grey=false;
    
    static final int OTHER_PAGE_ROWS = 50;
    static final String COMMENT_CONT_INDICATOR = "**VvV**";
	int listFontHeight = 12;
	Font listFont = new Font("serif", Font.PLAIN, listFontHeight - 2);
	//      int totalPages;
	int rowsRendered = 0;
	int format = 0;
	ReportDataInvoicesList data;
	static final int PRINTABLE_PAGE_WIDTH = 500;
	static final int LAST_COLUMN_START = 345;
	
	public BladeList() {
		super();
		data = new ReportDataInvoicesList(PRINTABLE_PAGE_WIDTH-LAST_COLUMN_START,listFont);
	}
	public BladeList(GregorianCalendar shipDate) throws Exception {
		super();
		data = new ReportDataInvoicesList(PRINTABLE_PAGE_WIDTH-LAST_COLUMN_START,listFont);
		data.setEstimatedShipDatesRange(shipDate, shipDate);
	}
	public Invoice getInvoice() {
		return null;
	}
	public void setInvoice(Invoice inv) {
	}
	public void setFormat(int format) {
		this.format = format;
		//  	data.setFormat(format);
		currPage = 0;
	}
	public void setInvoiceNumber(int id) throws Exception {
		//  	data.setInvoiceNumber(id);
		//  	data.setFormat(format);
	}
	public int getTotalNumberOfPages() {
		return getNumberOfPages();
	}
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		Graphics2D g2 = (Graphics2D) graphics;
		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		double width = pageFormat.getImageableWidth()
				- pageFormat.getImageableX() - 2;
		double height = pageFormat.getImageableHeight()
				- pageFormat.getImageableY();
		int column = 0;
		g2.setFont(listFont);
		FontMetrics metrics = g2.getFontMetrics();
		int incr = (int) metrics.getHeight();
		currPage = pageIndex;
		if (pageIndex > getNumberOfPages()) {
			return (NO_SUCH_PAGE);
		}
		g2.setFont(new Font("serif", Font.PLAIN, 10));
		pageHeader(g2, new Rectangle2D.Double(1, 20, width, 80), pageIndex + 1);
		if (data.getInvoices() != null) {
			int totalPages = getNumberOfPages();
			if (pageIndex == 0) {
				reportHeader(g2, new Rectangle2D.Double(1, 80, width, 40));
			}
			g2.setFont(listFont);
			if (totalPages > 0) {
				if (pageIndex == 0) {// first of several pages
					itemList(g2, new Rectangle2D.Double(1, 80, width, height),
							0);
				} else {
					int endRow = 0;
					if (pageIndex == totalPages) {// last page
						endRow = (int) height - listFontHeight;
					} else {// middle pages
						endRow = (int) height;
					}
					itemList(g2, new Rectangle2D.Double(1, 80, width, endRow),
							rowsRendered(pageIndex));
				}
			} else { // only one page	
				int endRow = (int) height - 100 - listFontHeight;
				itemList(g2, new Rectangle2D.Double(1, 110, width, endRow),
						rowsRendered(pageIndex));
			}
			if (pageIndex == getNumberOfPages()) { // last page
				g2.setFont(listFont);
				reportFooter(g2, width, new Point(1, (int) height - 100),
						pageIndex + 1);
			}
		}
		g2.setFont(new Font("serif", Font.BOLD, 10));
		pageFooter(g2, width, new Point(1, (int) height - 10), pageIndex + 1);
		return (PAGE_EXISTS);
	}
	//===================================================================
	int rowsRendered(int currentPage) {
		int results = 0;
		while (currentPage-- > 0)
			results += OTHER_PAGE_ROWS;
		return results;
	}
	//-------------------------------------------------------------------
	public int getNumberOfPages() {
		int pages = 0;
		int listRows = 0;
		int lstHt = 0;
		if (data.getInvoices() != null) {
			try {
				listRows = data.getTotalListRows();
			} catch (Exception e) {
			} // end of try-catch
			while (listRows > OTHER_PAGE_ROWS) { // all pages
				pages++;
				listRows -= OTHER_PAGE_ROWS;
			}
		} else {
			ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"
					+ "No Invoice???:");
		}
		return pages;
	}
	//-------------------------------------------------------------------
	public PageFormat getPageFormat(int pageIndex) {
		return new PageFormat();
	}
	//-------------------------------------------------------------------
	public Printable getPrintable(int pageIndex) {
		return this;
	}
	//===================================================================
	public void pageHeader(Graphics2D g2, Rectangle2D.Double region,
			int pageIndex) {
		double incr = g2.getFontMetrics().getHeight();
		g2.translate(region.x, region.y); // translate to pt
		String text[] = {"Printed:", "Page:", "Randall Made Knives",
				"Blade List", "Scheduled Ship:  "};
		int i = 0;
		printLeft(g2, text[i++] + " " + data.getCurrentDate(), region.width, 0);
		// ------------  Page x of y
		String pageStr = text[i++] + pageIndex;
		if (getNumberOfPages() > 0) {
			pageStr += " of " + (getNumberOfPages() + 1);
		}
		printRight(g2, pageStr, region.width, 0);
		// ------------  Center Header
		printCentered(g2, text[i++], region.width, 0);
		printCentered(g2, text[i++], region.width, incr);
		
		Font originalFont = g2.getFont();
		Font newFont = originalFont.deriveFont(Font.BOLD);
		g2.setFont(newFont);
		printCentered(g2, text[i++] + data.getShipDate(), region.width,
				2 * incr);
		g2.setFont(originalFont);
		g2.translate(-region.x, -region.y); // translate back
	}
	//===================================================================
	void reportHeader(Graphics2D g2, Rectangle2D.Double region) {
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
		String labels[] = {"Invoice", "Qty", "Model", "Features", "Comments"};
//		int colLocations[] = {6, 40, 60, 130, 325};
		int colLocations[] = {6, 40, 60, 130, LAST_COLUMN_START};
		g2.setColor(HIGHLIGHT_COLOR);
		g2.fill3DRect(1, rowLocation-fontHeight+4, (int)region.width-1, fontHeight+1, false) ;
		g2.setColor(Color.BLACK);
		for (int col = 0; col < colLocations.length; col++) {
			g2.drawString(labels[col], colLocations[col], rowLocation);
		}
		rowLocation += fontHeight;
		for (int i = startIndex; i < totalDataRows && total < height; i++) {
			//  	    rowLocation = (i-startIndex)*fontHeight;
			//  	    rowLocation += fontHeight;
			total += getRowHeight(fontHeight, i, null);
			String dataRow[] = data.getListRow(i);
//            if(useGreyBars && grey){
//                g2.setColor(greyColor);
//                g2.fill3DRect(1, rowLocation-fontHeight+4, (int)region.width-1, fontHeight+1, false) ;
//                g2.setColor(Color.BLACK);
//            }

			if (dataRow != null) {
			    g2.translate(0, rowLocation);
			    
			    boolean commentsContinued = false;
		    	// last line of page but more pages
			    if(i > 1 && (i + 1 - startIndex == OTHER_PAGE_ROWS) && i+1 < totalDataRows ){ 
			    	String nextRow[] = data.getListRow(i+1);
//			    	commentsContinued=true;
			    	commentsContinued = nextRow[2].length() == 0;
			    	if(commentsContinued && !dataRow[dataRow.length-1].endsWith(COMMENT_CONT_INDICATOR))
			    		dataRow[dataRow.length-1] += COMMENT_CONT_INDICATOR;
			    }
			    
				for (int col = 0; col < dataRow.length; col++) {
					try {
						String txt = dataRow[col];
                        String baseTxt = unformatted(txt);
                        double txtLen = metrics.charsWidth(baseTxt
                                .toCharArray(), 0, baseTxt.length());
                        double x = colLocations[col];
                        drawFormatted(g2, txt, (int) x, 0);
//                            g2.drawString (txt, (int) x, 0);
//						drawFormatted(g2, txt, colLocations[col], rowLocation);
						//  			g2.drawString (txt, colLocations[col], rowLocation);
					} catch (Exception e) {
						rmk.ErrorLogger.getInstance()
								.logError(" col:" + col, e);
					} // end of try-catch
				}
			    g2.translate(0, -rowLocation);
				rowLocation += fontHeight;
			}
			grey = !grey;
			rowsRendered++;
			//  	    rowLocation += fontHeight;
		}
		for (int col = 1; col < colLocations.length; col++) {
			g2.draw(new Line2D.Double(colLocations[col] - 5, -fontHeight,
					colLocations[col] - 5, height));
		}
		g2.translate(-region.x, -region.y); // translate back
		return rowsRendered;
	}
	//===================================================================
	void reportFooter(Graphics2D g2, double width, Point pt, int pageIndex) {
		g2.translate(pt.x, pt.y); // translate to pt
		g2.translate(-pt.x, -pt.y); // translate back
	}
	//===================================================================
	public void pageFooter(Graphics2D g2, double width, Point pt, int pageIndex) {
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
	void hangingIndent(Graphics2D g2, String label, String txt[]) {
		FontMetrics metrics = g2.getFontMetrics();
		int incr = (int) metrics.getHeight();
		int space = metrics.charsWidth(label.toCharArray(), 0, label.length());
		printLeft(g2, label, 0, 0);
		g2.translate(space, 0); // translate
		printLeft(g2, txt);
		g2.translate(-space, 0); // translate back
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
	public static void main(String args[]) throws Exception {
        java.util.GregorianCalendar date = new java.util.GregorianCalendar(2004, 10, 11);
        while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
            date.add(Calendar.DATE, 1);
        
		rmk.gui.Dialogs.bladeList(true, date);
		//    	rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog();
		//    	rpt.exitOnCancel=true;
		//    	java.util.GregorianCalendar date = new java.util.GregorianCalendar(2004, 0, 8);
		//  //  	java.util.GregorianCalendar date = new java.util.GregorianCalendar(2007, 4, 31);
		//  //  	java.util.GregorianCalendar date = new java.util.GregorianCalendar(2003, 5, 5);
		//    	rmk.reports.BladeList tst = new rmk.reports.BladeList(date);
		//    	rpt.setReport(tst);
		//  //    	ErrorLogger.getInstance().logMessage(tst.getInvoice());
		//  //    	rpt.setInvoice(60001); // 42496, 42683, 50000, 42684
		//    	rpt.setVisible(true);
	}
}