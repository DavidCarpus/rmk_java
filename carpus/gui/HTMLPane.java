package carpus.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.geom.*;

public class HTMLPane extends JEditorPane implements Printable{
    JEditorPane rpt = new JEditorPane("text/html", "");

    public static String text = "<html><head></head><body><table>" + 
	"<TR><TD>Test</TD><TD>One</TD></TR>" + 
	"<TR><TD></TD><TD>Two</TD></TR>" +
	"<TR><TD>More</TD><TD></TD></TR>" +
	"</TABLE>" + 
	"</body></html>";

    public HTMLPane(){
	super();
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex){
	Graphics2D graphics2D = (Graphics2D) graphics;

	Rectangle2D.Double rectangle = new Rectangle2D.Double ();
	
	rectangle.setRect (pageFormat.getImageableX (),
			   pageFormat.getImageableY (),
			   72, 72);

	graphics2D.draw (rectangle);

//     return (PAGE_EXISTS);
	return NO_SUCH_PAGE;
    }

//    public static void main(String args[]) throws Exception{
////  	Application.main(args);
//
//
//
//	HtmlReportDialog rpt = new HtmlReportDialog(null);
//	rpt.exitOnCancel=true;
//	rpt.setText(text);
//	rpt.setVisible(true);
//
//    }

}
