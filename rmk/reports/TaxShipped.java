/*
 * Created: May 9, 2004
 * By: David Carpus
 * 
 * Last Modified:
 * Last Modified by:
 * 
 */
package rmk.reports;

import java.awt.*;
import java.awt.geom.*;
import java.util.GregorianCalendar;


/**
 * @author dcarpus
 *
 * 
 */
public class TaxShipped extends TaxOrdered implements ReportInterface {

    public TaxShipped(GregorianCalendar orderedDate) throws Exception{
        super();
        data = new ReportDataInvoicesList();
        data.setFormat(ReportDataInvoicesList.FORMAT_TAX_SHIPPED);
        data.setShippedDate(orderedDate);
    }
    //===================================================================
    public void pageHeader(Graphics2D g2, Rectangle2D.Double region, int pageIndex){
  	double incr=g2.getFontMetrics().getHeight();
	
	g2.translate (region.x, region.y ); // translate to pt

	String text[] = {"Printed:" , "Page:", "Randall Made Knives", "Sales Tax Shipped Report", "Shipped:"};
	int i=0;
	printLeft(g2, text[i++] + " " + data.getShipDate(), region.width, 0);

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
        String labels[] = data.getColumnLabels();
//        String labels[] = { "Invoice", "Ordered", "Shipped", "nonTax", "Taxable",
//                "Tax %", "Taxed", "Paid", "Due"};
        int colLocations[] = { 8, 50, 110, 170, 220, 280, 320, 370, 430, 600};
        for (int col = 0; col < labels.length; col++) {
            String txt = labels[col];
            int x = colLocations[col] + 5;
//            if (col == 4) x -= 15;
            g2.drawString(txt, x, rowLocation);
        }

        rowLocation += fontHeight;
        colLocations = new int[]{ 8, 50, 110, 155, 220, 260, 310, 355, 410, 600};
        for (int i = startIndex; i < totalDataRows && total < height; i++) {
            total += getRowHeight(fontHeight, i, null);
            String dataRow[] = data.getListRow(i);

            if (dataRow != null) {
                addListRowToDisplay(g2, dataRow, rowLocation, colLocations);
                rowLocation += fontHeight;
            }
            rowsRendered++;
        }
        if((startIndex + rowsRendered) == totalDataRows){
            String dataRow[] = new String[labels.length];
            for(int i=0; i< dataRow.length;i++) dataRow[i]="";
            dataRow[3] = "<U><B>" + ReportDataInvoicesList.currencyFormat.format(data.getColumnTotal(3)) + "</B></U>";
            dataRow[4] = "<U><B>" + ReportDataInvoicesList.currencyFormat.format(data.getColumnTotal(4)) + "</B></U>";
            dataRow[6] = ReportDataInvoicesList.currencyFormat.format(data.getColumnTotal(6));
            dataRow[7] = ReportDataInvoicesList.currencyFormat.format(data.getColumnTotal(7));
            dataRow[8] = ReportDataInvoicesList.currencyFormat.format(data.getColumnTotal(8));
            addListRowToDisplay(g2, dataRow, rowLocation, colLocations);
        }
        
        colLocations = new int[]{ 8, 50, 110, 170, 220, 280, 320, 370, 430, 600};
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

                double x = 50 - txtLen;
                if(col == 0) x -= 15;
                if(col == 4) x += 5;
                if(col == 7) x += 15;
                if(col == 8) x += 15;
				drawFormatted(g2, txt, (int) x, rowLocation);
				
//                if (col == 7|| col == 6) {
//                    g2.drawString(txt, 0, rowLocation);
//                } else if (col > 1) {
//                    double x = 40 - txtLen;
//                    drawFormatted(g2, txt, (int) x, rowLocation);
//                } else {
//                    g2.drawString(txt, 0, rowLocation);
//                }
                g2.translate(-colLocations[col], 0); // translate back
            } catch (Exception e) {
                rmk.ErrorLogger.getInstance().logError(" col:" + col, e);
            } // end of try-catch
        }
    }
    public static void main(String args[]) throws Exception{
//    	rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog(null, rmk.gui.HtmlReportDialog.INVOICE_REPORT);
  	rmk.gui.HtmlReportDialog rpt = new rmk.gui.HtmlReportDialog();
  	rpt.exitOnCancel=true;
  	
	java.util.GregorianCalendar date = new java.util.GregorianCalendar(2003, 11, 1);
  	rmk.reports.TaxShipped tst = new rmk.reports.TaxShipped(date);
	tst.setFormat(ReportDataInvoicesList.FORMAT_TAX_SHIPPED); // FORMAT_TAX_SHIPPED FORMAT_MINIS

  	rpt.setReport(tst);
//    	ErrorLogger.getInstance().logMessage(tst.getInvoice());
//    	rpt.setInvoice(60001); // 42496, 42683, 50000, 42684
  	rpt.setVisible(true);
    }
}
