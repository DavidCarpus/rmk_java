/*
 * Created: Jul 3, 2004
 * By: David Carpus
 * 
 * Last Modified:
 * Last Modified by:
 * 
 */
package rmk.reports;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

/**
 * @author dcarpus
 *
 * 
 */
public class PartListReport extends BaseReport implements ReportInterface {
    boolean useGreyBars=true;
    boolean grey=false;

    int format = 0;
    int TOTAL_ROWS = 53;
    int PAGE_HEADER_ROWS = 3;
    int REPORT_HEADER_ROWS = 0;
    int REPORT_FOOTER_ROWS = 0;
    int PAGE_FOOTER_ROWS = 0;
    int BASE_LIST_ROWS = TOTAL_ROWS - PAGE_HEADER_ROWS - PAGE_FOOTER_ROWS;
    int ONE_PAGE_ROWS = BASE_LIST_ROWS - REPORT_HEADER_ROWS - REPORT_FOOTER_ROWS;
    int PAGE_ONE_ROWS = BASE_LIST_ROWS - REPORT_HEADER_ROWS;
    int OTHER_PAGE_ROWS = BASE_LIST_ROWS;
    int LAST_PAGE_ROWS = BASE_LIST_ROWS - REPORT_FOOTER_ROWS;
    int PAGE_HEADER_HEIGHT = PAGE_HEADER_ROWS * (listFontHeight*2);
    int REPORT_HEADER_HEIGHT = REPORT_HEADER_ROWS * (listFontHeight*2);
    int PAGE_FOOTER_HEIGHT = PAGE_FOOTER_ROWS * (listFontHeight*2);
    int REPORT_FOOTER_HEIGHT = REPORT_FOOTER_ROWS
            * (listFontHeight*2);

    int FOLD_LINE_ROW = 220;

    int LIST_START_LOCATION = PAGE_HEADER_HEIGHT
            + REPORT_HEADER_HEIGHT;
    
    
    ReportData data;

    
    public PartListReport() {
        super();
        currentYear = (new java.util.GregorianCalendar()).get(java.util.GregorianCalendar.YEAR);
        data = new ReportData();
    }

    /* (non-Javadoc)
     * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        Graphics2D g2 = (Graphics2D) graphics;
        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        double width = pageFormat.getImageableWidth()
        - pageFormat.getImageableX() - 2;
        double pageHeight = pageFormat.getImageableHeight()
        - pageFormat.getImageableY();
        int column = 0;
        
        //    	System.out.println(this.getClass().getName() + ":"+ width + ":"+
        // height);
        
        g2.setFont(infoFont);
        FontMetrics metrics = g2.getFontMetrics();
        int incr = (int) metrics.getHeight();
        
        currPage = pageIndex;
        
        if (pageIndex > getNumberOfPages()) { return (NO_SUCH_PAGE); }
        
        g2.setFont(infoFont);
        pageHeader(g2, new Rectangle2D.Double(1, 20, width, 10+PAGE_HEADER_HEIGHT), pageIndex + 1);
        boolean lastPage = false;
        if (pageIndex == getNumberOfPages()) lastPage = true;
        
        if (data.getPartsList() != null) {
            int totalPages = getNumberOfPages();
            if (pageIndex == 0) {
                grey = false;
//                reportHeader(g2, new Rectangle2D.Double(1, 80, width, REPORT_HEADER_HEIGHT));
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
                    itemList(g2, new Rectangle2D.Double(1, LIST_START_LOCATION, width, endRow),
                            rowsRendered(pageIndex));
                }
            } else { // only one page
                int endRow = (int) pageHeight  - listFontHeight - REPORT_FOOTER_HEIGHT;
                itemList(g2, new Rectangle2D.Double(1, LIST_START_LOCATION, width, endRow),
                        rowsRendered(pageIndex));
            }
            // FOLD lines
//            g2.draw(new Line2D.Double(1, FOLD_LINE_ROW, 3, FOLD_LINE_ROW));
//            g2.draw(new Line2D.Double(width - 2, FOLD_LINE_ROW, width, FOLD_LINE_ROW));
            
            if (lastPage) { // last page
                g2.setFont(listFont);
                reportFooter(g2, width, new Point(1, (int) pageHeight - REPORT_FOOTER_HEIGHT),
                        pageIndex + 1);
            }
        }
        //  	g2.setFont(new Font ("serif", Font.BOLD, 10));
        //  	System.out.println(this.getClass().getName() + ":"+ width);
        
        if (lastPage) g2.translate(120, 10); // translate to start
        //  	width += 100;
        //  	drawFormatted(g2,"****TEST****", 0, 0);
        
        pageFooter(g2, width, new Point(0, (int) pageHeight - 10), pageIndex + 1);
        return (PAGE_EXISTS);
    }
    //===================================================================
    public void pageHeader(Graphics2D g2, Rectangle2D.Double region,
            int pageIndex) {
        
        g2.setFont(listFont);
        double incr = g2.getFontMetrics().getHeight();
        int height = (int) (region.height - region.y);
        g2.translate(region.x, region.y); // translate to pt
        
        String text[] = { "Printed:", "Page:", "Parts List"};
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
        g2.translate(-region.x, -region.y); // translate back
    }
    //===================================================================
    void reportHeader(Graphics2D g2, Rectangle2D.Double region) {
//        g2.draw(new Line2D.Double(0, 110, region.width, 110));
//        g2.draw(new Line2D.Double(0, 65, region.width, 65));
    }
    //===================================================================
    void reportFooter(Graphics2D g2, double width, Point pt, int pageIndex) {
    }
    //===================================================================
    public void pageFooter(Graphics2D g2, double width, Point pt, int pageIndex) {
    }
    
    //===================================================================
    int itemList(Graphics2D g2, Rectangle2D.Double region, int startIndex) {
        g2.setFont(listFont);
        g2.translate(region.x, region.y); // translate to pt
        
        FontMetrics metrics = g2.getFontMetrics();
        int fontHeight = (int) metrics.getHeight();
        int height = (int) (region.height - region.y);
        
        int totalDataRows = data.getTotalListRows();
        
        int lstHt = getListHeight(listFontHeight, 0, totalDataRows, data);
        
        Rectangle2D.Double rectangle = new Rectangle2D.Double();
        rectangle.setRect(0, -fontHeight, region.width, height + fontHeight);
        g2.draw(rectangle);
        rectangle.setRect(0, -2 * fontHeight, region.width, fontHeight);
        g2.draw(rectangle);
        int total = 0;
        int rowsRendered = 0;
        
        int rowLocation = -(fontHeight + 3);

        String labels[] = {"Model", "Desc", "Codes", "Price1", "Price2", "Price3", "Price4"};
        int startYear = currentYear;
        for(int col=3; col<=6; col++){
            labels[col] = ""+startYear--;
        }
        
        
        int colLocations[] = { 10, 80, 320, 360, 400, (int) region.width - 80,
                (int) region.width - 35};
        for (int col = 0; col < colLocations.length; col++) {
            g2.drawString(labels[col], colLocations[col], rowLocation);
        }
        
        rowLocation += fontHeight;
        for (int i = startIndex; i < totalDataRows && total < height; i++) {
            total += getRowHeight(fontHeight, i, null);
            String nextRow[] = {"",""};
            if(i < totalDataRows)
                nextRow = data.getListRow(i+1);
            String dataRow[] = data.getListRow(i);

            g2.setColor(Color.BLACK);
            if(useGreyBars && grey){
                g2.setColor(HIGHLIGHT_COLOR);
                g2.fill3DRect(1, rowLocation-fontHeight+4, (int)region.width, fontHeight+1, false) ;
                g2.setColor(Color.BLACK);
            }
            
            if (dataRow != null) {
                for (int col = 0; col < dataRow.length-1; col++) {
                    try {
                        String txt = dataRow[col];
                        if (txt == null) txt = "";
                        g2.translate(colLocations[col], 0);
                        // translate to start of col
//                        boolean priceCol = (col > 0);
                        boolean priceCol = col>1;
                        if (!priceCol) {
                            String baseTxt = unformatted(txt);
                            double txtLen = metrics.charsWidth(baseTxt
                                    .toCharArray(), 0, baseTxt.length());
                            double x = 0;
                            drawFormatted(g2, txt, (int) x, rowLocation);
                        } else {                            
                            printRight(g2, txt, 35, rowLocation);
                        }
                        g2.translate(-colLocations[col], 0); // translate back
                    } catch (Exception e) {
                        rmk.ErrorLogger.getInstance().logError(
                                " col:" + col + " row:" + i, e);
                    } // end of try-catch
                }

                if(!newPartType(dataRow, nextRow, i-startIndex) && i > 0){
                    grey = !grey;
                }
                
                rowLocation += fontHeight;
            }
            rowsRendered++;
            //  	    rowLocation += fontHeight;
        }
        g2.setColor(Color.BLACK);

        drawListColumSeperators(g2, colLocations, -fontHeight, height);
        
        g2.translate(-region.x, -region.y); // translate back
        g2.setFont(infoFont);
        
        return rowsRendered;
    }

    /**
     * @param dataRow
     * @param nextRow
     * @param i
     * @return
     */
    private boolean newPartType(String[] dataRow, String[] nextRow, int i) {
        if(dataRow == null || nextRow == null) 
            return true;
        String type1=dataRow[dataRow.length-1];
        String type2=nextRow[nextRow.length-1];
        
        if(type1 == null) 
            return true;

        return (type1.equals(type2));
    }

    //===================================================================
    int rowsRendered(int currentPage) {
        int results = 0;
        
        if (currentPage-- >= 1) results += PAGE_ONE_ROWS;
        while (currentPage-- > 0)
            results += OTHER_PAGE_ROWS;
        
        //    	System.out.println("rowsRendered:"+ results + ": P"+currentPage);
        return results;
    }

    /* (non-Javadoc)
     * @see rmk.reports.BaseReport#getTotalNumberOfPages()
     */
    int getTotalNumberOfPages() {
        int pages = 0;
        int listRows = 0;
        int lstHt = 0;
        if (data.getPartsList() != null) {
            listRows = data.getTotalListRows();
            //  	    System.out.println("getNumberOfPages:listRows:" + listRows);
            if (listRows <= ONE_PAGE_ROWS) { // only 1 page?
                return 0; }
            
            pages++;
            listRows -= PAGE_ONE_ROWS;
            
            while (listRows > LAST_PAGE_ROWS) { // middle pages
                pages++;
                listRows -= OTHER_PAGE_ROWS;
            }
        } else {
            System.out.println(this.getClass().getName() + ":"
                    + "No Parts???:");
        }
        return pages;
    }

    /* (non-Javadoc)
     * @see rmk.reports.ReportInterface#setInvoiceNumber(int)
     */
    public void setInvoiceNumber(int number) throws Exception {
        throw new Exception("PartListReport:setInvoiceNumber should not be used");
    }

    public void setFormat(int format) { }   // Not used


    /* (non-Javadoc)
     * @see java.awt.print.Pageable#getNumberOfPages()
     */
    public int getNumberOfPages() {
        return getTotalNumberOfPages();
    }

    public static void main(String[] args) {
    }
}
