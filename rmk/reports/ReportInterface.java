package rmk.reports;

import java.awt.*;
import java.awt.event.*;

public interface ReportInterface{
    public static int PRINT_TO_SCREEN=0;
    public static int PRINT_TO_PRINTER=1;

    public void setInvoiceNumber(int number)  throws Exception;
    public void setPreferredSize(Dimension dim);
    public void setFormat(int format);
    public void repaint();
    public void actionPerformed(ActionEvent e);
    public void setDestination(int dest);


}
