package rmk.reports;

import javax.print.attribute.standard.MediaPrintableArea;
import javax.swing.*;

import rmk.ErrorLogger;
import rmk.database.dbobjects.Customer;

import java.awt.*;

import java.awt.event.*;
import java.awt.print.*;
import java.awt.geom.*;
import java.util.ArrayList;

public abstract class BaseReport extends JPanel implements Printable, Pageable, ReportInterface,
        ActionListener {
	// bitmap
	public static final int PLAIN=0;
	public static final int BOLD=1;
	public static final int ITALIC=2;
	public static final int UNDERLINE=4;
	
    public int printDestination=ReportInterface.PRINT_TO_SCREEN;
    
    int listFontHeight = 10;

    int infoFontHeight = listFontHeight;

    int footerFontHeight = listFontHeight;

    Font listFont = new Font("serif", Font.PLAIN, listFontHeight);

    Font infoFont = new Font("serif", Font.PLAIN, infoFontHeight);

    Font footerFont = new Font("serif", Font.BOLD, footerFontHeight);

    int TOTAL_ROWS = 49;

    int PAGE_HEADER_ROWS = 4;

    int REPORT_HEADER_ROWS = 7;

    int REPORT_FOOTER_ROWS = 7;

    int PAGE_FOOTER_ROWS = 2;

    int BASE_LIST_ROWS = TOTAL_ROWS - PAGE_HEADER_ROWS - PAGE_FOOTER_ROWS;

    int ONE_PAGE_ROWS = BASE_LIST_ROWS - REPORT_HEADER_ROWS - REPORT_FOOTER_ROWS;

    int PAGE_ONE_ROWS = BASE_LIST_ROWS - REPORT_HEADER_ROWS;

    int OTHER_PAGE_ROWS = BASE_LIST_ROWS+3;

    int LAST_PAGE_ROWS = BASE_LIST_ROWS - REPORT_FOOTER_ROWS;

    int PAGE_HEADER_HEIGHT = PAGE_HEADER_ROWS * (listFontHeight*2);

    int REPORT_HEADER_HEIGHT = REPORT_HEADER_ROWS * (listFontHeight*2);

    int PAGE_FOOTER_HEIGHT = PAGE_FOOTER_ROWS * (listFontHeight*2);

    int REPORT_FOOTER_HEIGHT = REPORT_FOOTER_ROWS
            * (listFontHeight*2);

    int FOLD_LINE_ROW = 220;

    int LIST_START_LOCATION = PAGE_HEADER_HEIGHT
            + REPORT_HEADER_HEIGHT;

    public rmk.DataModel sys = rmk.DataModel.getInstance();

    int currPage = 0;
    int currentYear=0;

    abstract int getTotalNumberOfPages();

    static final float[] dashLinePattern = { 5, 2, 5, 2};

    static Stroke dashedLine = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10, dashLinePattern, 0);

    int greyLevel = 255; // 0->255 b->w

//    Color greyColor = new Color(greyLevel,greyLevel,greyLevel);
//    Color greyColor = Color.LIGHT_GRAY;
    static final Color HIGHLIGHT_COLOR =  new Color(204,255,204); // a light yellow
//    static final Color HIGHLIGHT_COLOR = Color.LIGHT_GRAY;

    //===================================================================
    void verticalText(Graphics2D g2, String txt, Point pt) {
        FontMetrics metrics = g2.getFontMetrics();
        int incr = (int) metrics.getHeight();
        g2.translate(pt.x, pt.y); // translate to pt
        for (int row = 0; row < txt.length(); row++) {
            g2.drawString("" + txt.charAt(row), 0, row * incr);
        }
        g2.translate(-pt.x, -pt.y); // translate back
    }

    void drawRectangle(Graphics2D g2, int fontHeight, int width, int height) {
        Rectangle2D.Double rectangle = new Rectangle2D.Double();
        rectangle.setRect(0, -fontHeight, width, height + 2);
        g2.draw(rectangle);
    }

    //===================================================================
    void printLeft(Graphics2D g2, String str[]) {
        FontMetrics metrics = g2.getFontMetrics();
        double incr = metrics.getHeight();
        for (int i = 0; i < str.length; i++) {
            //  	    printLeft(g2,str[i], 0, i*incr);
            //      void drawFormatted(Graphics2D g2, String txt, int x, int y){
            drawFormatted(g2, str[i], 0, (int) (i * incr));
        }

    }

    //----------------------------------------------------------------
    void printRight(Graphics2D g2, int width, String str[]) {
        FontMetrics metrics = g2.getFontMetrics();
        double incr = metrics.getHeight();
        for (int i = 0; i < str.length; i++) {
            printRight(g2, str[i], width, i * incr);
        }
    }

    //===================================================================
    void printLeft(Graphics2D g2, String str) {
        g2.drawString(str, 0, 0);
    }

    //----------------------------------------------------------------
    void printLeft(Graphics2D g2, String str, double width, double y) {
        double x = 0;
        //  	g2.drawString (str, (int)x, (int)y);
        drawFormatted(g2, str, 0, (int) y);
    }

    //----------------------------------------------------------------
    void printRight(Graphics2D g2, String str, double width, double y) {
        FontMetrics metrics = g2.getFontMetrics();
        //  	double x=width - metrics.charsWidth(str.toCharArray(), 0,
        // str.length());
        //  	g2.drawString (str, (int)x, (int)y);
        String txt = unformatted(str);
        double x = width
                - metrics.charsWidth(txt.toCharArray(), 0, txt.length());
        drawFormatted(g2, str, (int) x, (int) y);
    }

    //----------------------------------------------------------------
    void printRightUnderlined(Graphics2D g2, String str, double width, double y) {
        FontMetrics metrics = g2.getFontMetrics();
        double txtLen = metrics.charsWidth(str.toCharArray(), 0, str.length());
        double x = width - txtLen;
        g2.drawString(str, (int) x, (int) y);
        g2.draw(new Line2D.Double(x, y, x + txtLen, y));

    }

    //----------------------------------------------------------------
    void printCentered(Graphics2D g2, String str, double width, double y) {
        FontMetrics metrics = g2.getFontMetrics();
        double x = width / 2
                - metrics.charsWidth(str.toCharArray(), 0, str.length()) / 2;
        g2.drawString(str, (int) x, (int) y);
    }

    //----------------------------------------------------------------

    //----------------------------------------------------------------
    //----------------------------------------------------------------
    //----------------------------------------------------------------
    void drawFormatted(Graphics2D g2, String txt, int x, int y) {
        ArrayList parsedText = parseFormattedText(txt);
        
        Font originalFont = g2.getFont();
        for(int i=0; i< parsedText.size(); i++){
        	FormattedText segment = (FormattedText) parsedText.get(i);
            int style = Font.PLAIN;
            boolean underline=false;
            if ((segment.format & BOLD) > 0) 
            	style = style | Font.BOLD;
            if ((segment.format & ITALIC) > 0) 
            	style = style | Font.ITALIC;
            underline = ((segment.format & UNDERLINE) > 0);
            Font newFont = originalFont.deriveFont(style);
//          x -= 3; // not sure why this is needed
            if (style != Font.PLAIN) 
                x += 3;
            g2.setFont(newFont);
            g2.drawString(segment.text, x, y);
            if ((segment.format & BOLD) > 0)  x += 2;
            FontMetrics metrics = g2.getFontMetrics();
            double txtLen = metrics.charsWidth(segment.text.toCharArray(), 0,
            		segment.text.length());

            if (underline) {
                g2.draw(new Line2D.Double(x, y + 1, x + txtLen, y + 1));
            }
            x += txtLen;
            g2.setFont(originalFont);
        }
        //  	g2.setFont(currFont);
        //  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+
        // originalFont.getStyle());

//        outTxt = txt;
//        while ((formatStart = formatStart(txt)) >= 0) {
//
//            if (formatStart != 0) {
//                outTxt = txt.substring(0, txt.indexOf("<"));
//                // write out txt so far
//                int style = Font.PLAIN;
//                //  		int style = currFont.getStyle();
//                if (bold > 0) style = style | Font.BOLD;
//                if (italic > 0) style = style | Font.ITALIC;
//
//                //    		Font newFont = new
//                // Font(originalFont.getFontName(),style,originalFont.getSize());
//                Font newFont = originalFont.deriveFont(style);
//
////                x -= 3; // not sure why this is needed
//                if (style != Font.PLAIN) 
//                    x += 3;
//                g2.setFont(newFont);
//                g2.drawString(outTxt, x, y);
//                if (bold > 0) x += 2;
//                FontMetrics metrics = g2.getFontMetrics();
//                double txtLen = metrics.charsWidth(outTxt.toCharArray(), 0,
//                        outTxt.length());
//
//                if (underline > 0) {
//                    g2.draw(new Line2D.Double(x, y + 1, x + txtLen, y + 1));
//                }
//                x += txtLen;
//
//                txt = txt.substring(txt.indexOf("<"));
//            }
//            if (txt.trim().length() == 0) return;
//            String style = txt.substring(0, txt.indexOf(">") + 1).toUpperCase();
//
//            if (style.equals("<I>")) {
//                italic++;
//            }
//            if (style.equals("</I>")) {
//                italic--;
//            }
//            if (style.equals("<B>")) {
//                bold++;
//            }
//            if (style.equals("</B>")) {
//                bold--;
//            }
//            if (style.equals("<U>")) {
//                underline++;
//            }
//            if (style.equals("</U>")) {
//                underline--;
//            }
//
//            txt = txt.substring(style.length());
//        }
//        g2.setFont(originalFont);
//        if (outTxt.equals(txt)) // NO Formatting, but still need to display
//                g2.drawString(outTxt, x, y);
    }

    public static String unformatted(String txt) {
        txt += " ";
        String outTxt = "";
        int formatStart = 0;

        outTxt = txt;
        while ((formatStart = formatStart(txt)) >= 0) {
            if (formatStart != 0) {
                outTxt = txt.substring(0, txt.indexOf("<"));
                // write out txt so far
                txt = txt.substring(txt.indexOf("<"));
            }
            if (txt.trim().length() == 0) return outTxt;
            String style = txt.substring(0, txt.indexOf(">") + 1).toUpperCase();
            txt = txt.substring(style.length());
        }
        return outTxt;
    }

    public static int formatStart(String txt) {
        int strt = 0;
        int first = txt.trim().length();
        if (first == 0) return -1;
        String codes[] = { "<I>", "</I>", "<B>", "</B>", "<U>", "</U>"};
        txt = txt.toUpperCase();

        for (int i = 0; i < codes.length; i++) {
            strt = txt.indexOf(codes[i]);
            if (strt >= 0 && strt < first) {
                first = strt;
            }
        }
        if (first == txt.trim().length()) return -1; // NO formatting
        return first;
    }

    //----------------------------------------------------------------
    //----------------------------------------------------------------
    //----------------------------------------------------------------
    public static int getRowHeight(int fontHeight, int row, String[][] data) {
        //  	String rowData[][] = data.getListRow(row);
        //  	return fontHeight * rowData.length;
        return fontHeight;
    }

    //----------------------------------------------------------------
    public static int getListHeight(int fontHeight, int startIndex,
            int endIndex, ReportData data) {
        int total = 0;

        for (int i = startIndex; i < endIndex; i++) {
            int col = 0;
            //  	    total += getRowHeight(fontHeight, i, data);
            total += getRowHeight(fontHeight, i, null);
        }
        //  	total += getRowHeight(fontHeight, 0, data);
        total += getRowHeight(fontHeight, 0, null);
        return total;
    }

    //----------------------------------------------------------------
    public static int getListHeight(int fontHeight, int startIndex,
            int endIndex, ReportDataInvoicesList data) {
        int total = 0;

        for (int i = startIndex; i < endIndex; i++) {
            int col = 0;
            //  	    total += getRowHeight(fontHeight, i, data);
            total += getRowHeight(fontHeight, i, null);
        }
        //  	total += getRowHeight(fontHeight, 0, data);
        total += getRowHeight(fontHeight, 0, null);
        return total;
    }

    //----------------------------------------------------------------
    //----------------------------------------------------------------
    //----------------------------------------------------------------
    public static Rectangle getDisplayDim(){
        return new Rectangle(15, 15, 530, 725);
    }
    public void paint(Graphics g) {
        //  	Rectangle dim = new Rectangle(5,5,608,791);
        
        try {
            //  	    g.clearRect(0, 0, 600, 750);
            PageFormat format = new PageFormat();
            Paper paper = new Paper();
            // Letter paper dimensions with "default==1'' " margins
            // [x=0.0,y=0.0,w=530,h=700]
            //  	    paper.setImageableArea(5,5,530, 700);
            // 1/72
//            paper.setImageableArea((int) dim.getX(), (int) dim.getY(),
//                    (int) dim.getWidth(), (int) dim.getHeight());
            if(printDestination == ReportInterface.PRINT_TO_SCREEN){
//                ErrorLogger.getInstance().logMessage(this.getClass().getName() + "Print To Screen...");
                Rectangle dim = getDisplayDim();
                g.clearRect((int) dim.getX(), (int) dim.getY(), (int) dim
                        .getWidth() + 100, (int) dim.getHeight() + 100);

                paper.setImageableArea((int) dim.getX(), (int) dim.getY(),
                        (int) dim.getWidth(), (int) dim.getHeight());
                
            } else if(printDestination == ReportInterface.PRINT_TO_PRINTER){
                ErrorLogger.getInstance().logMessage(this.getClass().getName() + "Print To Printer...");
                MediaPrintableArea area = Printing.getPrintArea();
                double convRate = 1.0/72.0;
                paper.setImageableArea(0.5*convRate, 0.5*convRate,
                        8.0*convRate, 11.0*convRate);
                
            }

            format.setPaper(paper);
            print(g, format, currPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //===================================================================
    //===================================================================
    //===================================================================
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand().toUpperCase().trim();
        ErrorLogger.getInstance().logDebugCommand(command);

        //  	ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":"+ command);

        if (command.equals("CANCEL")) { //cancel
            System.exit(0);
        }
        if (command.equals("NEXT")) { //switch pages
            if (currPage == getTotalNumberOfPages())
                currPage = 0;
            else
                currPage++;
            //  	    currPage = (currPage == 0? 1: 0);
            repaint();
        }
        if (command.equals("PREV")) { //switch pages
            if (currPage == 0)
                currPage = getTotalNumberOfPages();
            else
                currPage--;
            repaint();
        }
    }

    //===================================================================
    public void pageFooter(Graphics2D g2, double width, Point pt, int pageIndex) {
        g2.setFont(footerFont);
        String text[] = { "Thank you for your order.",
                "Deposits are not transferable or refundable"};
        g2.translate(pt.x, pt.y); // translate to pt
        int i = 0;
        printLeft(g2, text[i++], width, 0);
        printRight(g2, text[i++], width, 0);
        g2.translate(-pt.x, -pt.y); // translate back
    }

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

    protected void drawDashedLine(Graphics2D g2, Rectangle2D.Double coordinates) {
        Stroke originalStroke = g2.getStroke();
        Color originalColor = g2.getColor();
        g2.setStroke(dashedLine);
        g2.setColor(Color.gray);
        g2.draw(new Line2D.Double(coordinates.x, coordinates.y,
                coordinates.width, coordinates.height));
        g2.setStroke(originalStroke);
        g2.setColor(originalColor);

    }
    //----------------------------------------------------------------
    protected void invoiceInfo(Graphics2D g2, Rectangle region, String info[], 
            Customer cust, String dates[]) {
        g2.setFont(listFont);
        //  	g2.setFont(infoFont);
        g2.translate(region.x, region.y); // translate
        
        String invLabels[] = { "Phone:", "PO#", "Invoice:"};
        printLeft(g2, invLabels);
        g2.translate(60, 0); // translate
        printLeft(g2, info);
        g2.translate(100, 0); // translate
        printLeft(g2, "Customer #:", 40, 0);
        if (cust != null) {
            g2.translate(60, 0); // translate
            printLeft(g2, "" + cust.getCustomerID(), 40, 0);
            g2.translate(-60, 0); // translate back
        }
        g2.translate(-100, 0); // translate back
        g2.translate(-60, 0); // translate back
        
        String dateLabels[] = { "<B>Scheduled Ship<B>:", "Ordered", "Shipped"};
        g2.translate((int) region.width - 190, 0); // translate
        if (dates[2] == null || dates[2].length() == 0) dateLabels[2] = "";
        printLeft(g2, dateLabels);
        g2.translate(100, 0); // translate
        printLeft(g2, dates);
        g2.translate(-100, 0); // translate back
        g2.translate(-(int) region.width + 190, 0); // translate back
        
        g2.translate(-region.x, -region.y); // translate back
    }
    void addressInfo(Graphics2D g2, double width, double height, Point pt, String addressInfo[], String shippingInstructions[]) {
        g2.setFont(listFont);
        //  	g2.setFont(infoFont);
        FontMetrics metrics = g2.getFontMetrics();
        int incr = (int) metrics.getHeight();
        Rectangle billingRegion = new Rectangle(40, 0, 200, 40);
        //  	Rectangle shippingRegion = new Rectangle (260,0, 200, 40);
        Rectangle shippingRegion = new Rectangle(320, 0, 200, 40);
        int verticalLineOffset = 5;
        int verticalLineLength = (int)height-20;
        
        g2.translate(pt.x, pt.y); // translate to pt
        
        // ------------ billing
        verticalText(g2, "BILL", new Point(5, 5));
        g2.draw(new Line2D.Double(billingRegion.x - verticalLineOffset,
                -verticalLineOffset, billingRegion.x - verticalLineOffset,
                verticalLineLength));
        g2.translate(billingRegion.x, billingRegion.y);
        // translate to billingRegion
        printLeft(g2, addressInfo);
        g2.translate(-billingRegion.x, -billingRegion.y); // translate back
        // ------------ Shipping
        verticalText(g2, "SHIP", new Point(shippingRegion.x - 15, 5));
        g2.draw(new Line2D.Double(shippingRegion.x - verticalLineOffset,
                -verticalLineOffset, shippingRegion.x - verticalLineOffset,
                verticalLineLength));
        g2.translate(shippingRegion.x, shippingRegion.y);
        // translate to shippingRegion
        printLeft(g2, shippingInstructions);
        //  	printLeft(g2, data.invoiceShippingInstructions(),
        // shippingRegion.width, 0);
        g2.translate(-shippingRegion.x, -shippingRegion.y); // translate back
        
        g2.translate(-pt.x, -pt.y); // translate back
    }
    public PageFormat getPageFormat(int pageIndex) {
        return new PageFormat();
    }
    
    public Printable getPrintable(int pageIndex) {
        return this;
    }

    protected void drawListColumSeperators(Graphics2D g2, int colLocations[],
            int startRow, int height) {
        for (int col = 1; col < colLocations.length; col++) {
            g2.draw(new Line2D.Double(colLocations[col] - 5, startRow,
                    colLocations[col] - 5, height));
        }
    }

    protected void accounting(Graphics2D g2, String[] labels, String lastInvoicePayment,
            String[] totals, String[] percentages){
        g2.translate(-40, 0); // shift == yes ITS nEGATIVE
        drawFormatted(g2, lastInvoicePayment, 0, 0);
        g2.translate(40, 0); // shift back
        //  	printRight(g2, data.lastPayment(), 0, 0);
        
        g2.translate(0, 10); // translate
        //  	printRight(g2, (int)60, data.invoiceTotals());
//        String totals[] = data.invoiceTotals();
//        String percentages[] = data.invoicePercentages();
        
        FontMetrics metrics = g2.getFontMetrics();
        double incr = metrics.getHeight();
        double row = 0;
        for (int i = 0; i < totals.length; i++) {
            String val = totals[i];
            //    	    if(! val.equals("$0.00") && ! val.equals("") ){
            if (!val.equals("")) {
                String label = labels[i];
                String percent = percentages[i];
                String total = "$" + totals[i];
                if (label.equals("Balance")) {
                    label = "<B>" + label + "</B>";
                    total = "<B>" + total + "</B>";
                }
                printLeft(g2, label, 0, row);
                g2.translate(140, 0); // shift
                printRight(g2, percent, (int) 40, row);
                g2.translate(55, 0); // shift
                printRight(g2, total, (int) 55, row);
                g2.translate(-195, 0); // shift back
                row += incr;
            }
        }

    }
    boolean sameKnife(String currRow[], String nextRow[], int rowNum){
        boolean results = false;

        if(nextRow == null) return true;
        
        if(rowNum == 0){
            if(!currRow[0].equals("") && nextRow[0].equals("")) 
                results = true;
            else if(currRow[0].equals("") && nextRow[0].equals("") ) 
                results = true;
        } else{
            if(currRow[0].equals("") && nextRow[0].equals("") ) 
                results = true;
            else if(nextRow[0].equals("") ) 
                results = true;
        }
        
        return results;
    }

    public void setDestination(int dest){
        printDestination = dest;
    }
    public int getDestination(){
        return printDestination;
    }
    //      public void paint(Graphics g){
    //  	super.paint(g);
    //  	try{
    //  	    print(g, new PageFormat(), 0);
    //  	} catch (Exception e){
    //  	    e.printStackTrace();
    //  	}
    //      }
    
    static ArrayList parseFormattedText(String text){
    	ArrayList results = new ArrayList();
    	if(text.indexOf("<") < 0){
    		FormattedText txt = new FormattedText(text);
    		results.add(txt);
    		return results;
    	}
    	
    	String currText = text;
    	int currStyle = PLAIN;
    	String currSegment = getNextTextSegment(currText);
    	FormattedText formattedText = new FormattedText();
    	
    	while (currSegment.length() > 0){
    		if(currSegment.startsWith("<")){ // formatting
    			int styleMod=0;
				if(currSegment.indexOf("U")  > 0)
					styleMod = UNDERLINE;
				else if(currSegment.indexOf("B")  > 0)
					styleMod = BOLD;
				else if(currSegment.indexOf("I")  > 0)
					styleMod = ITALIC;
				else
					ErrorLogger.getInstance().logMessage("BaseReport:" + "Unknown format" + currSegment);
    			
    			if(currSegment.indexOf("/") > 0){ // end format
    				styleMod = -styleMod;
    			} else{ // startFormat
    			}
    			currStyle += styleMod;
    		} else{
    			formattedText.setText(currSegment);
    			formattedText.setFormat(currStyle);
    			results.add(new FormattedText(currSegment, currStyle));
    		}
//    			if block starts new format
//    		change format
//	    	if block ends format
//	    		add to results
//	    		remove style
    		currText = currText.substring(currSegment.length()); // trim block from front
    		currSegment = getNextTextSegment(currText);
    	}
    	return results;
    }
    static String getNextTextSegment(String txt){
    	if(txt.indexOf("<") > 0){
    		txt = txt.substring(0, txt.indexOf("<"));
    	} else if(txt.indexOf('<') == 0){
    		txt = txt.substring(0, txt.indexOf(">")+1);
    	} 
    	return txt;
    }
	
    public static void main(String args[]) throws Exception {
        String txt = "<B><U>test</U>item</B>message";
        BaseReport tst = new rmk.reports.BladeList();
        ErrorLogger.getInstance().logMessage(""+BaseReport.parseFormattedText(txt));
    }
}

class FormattedText{
	public String text;
	public int format=Font.PLAIN;    	
	public FormattedText(String text, int style){
		this.text = text;
		this.format = style;
	}
    	public FormattedText(String text){
		setText(text);
	}
	public FormattedText(){}
	
	public void setFormat(int format) {
		this.format = format;
	}
	public void setText(String text) {
		if(text == null)
			text="";
		this.text = text;
	}
	public String toString(){
		return format + ":" + text;
	}
}
