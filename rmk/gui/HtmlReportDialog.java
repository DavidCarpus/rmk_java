package rmk.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import carpus.gui.BasicToolBar;

import rmk.ErrorLogger;
import rmk.reports.BaseReport;

public class HtmlReportDialog extends JDialog implements ActionListener{
//    DocFlavor myFlavor;
    public static final int SHORT_FORMAT = 0;
    public static final int LONG_FORMAT = 1;
    public static final int INVOICE_REPORT = 0;
    public static final int ACKNOWLEDGE_REPORT = 1;
    public static final int BLADELIST = 3;
    public static final int TAX_REPORT_ORDERED = 4;
    public static final int invoices[] = {42496, 60001, 42683, 50000, 42684};
    int currInv = 0;
    
    int currPage = 0;
    int rptID=0;
    int rptFormat= SHORT_FORMAT;
//  int rptFormat= LONG_FORMAT;
    BasicToolBar buttonBar;
    rmk.reports.ReportInterface rpt;
    
    JPanel rptPnl = new JPanel();
    JScrollPane rptPane = new JScrollPane(rptPnl);
    
    public boolean exitOnCancel=false;    
    //-----------------------------------------------------------------------
    public HtmlReportDialog(Frame parent, int rpt) throws Exception{
        super(parent);
        setReport(rpt);
        create();
    }
    //-----------------------------------------------------------------------
    public HtmlReportDialog() throws Exception{
        super();
        create();
    }
    //-----------------------------------------------------------------------
    public void setReport(int rptID){
        this.rptID = rptID;
        if(rptID == INVOICE_REPORT)
            rpt = (rmk.reports.ReportInterface)new rmk.reports.InvoiceReport();
        if(rptID == ACKNOWLEDGE_REPORT)
            rpt = (rmk.reports.ReportInterface)new rmk.reports.AcknowledgeReport();
        Rectangle area = BaseReport.getDisplayDim();
        rpt.setPreferredSize(new Dimension(area.width,area.height));
//        rpt.setPreferredSize(new Dimension(550,780));
    }
    public void setReport(rmk.reports.ReportInterface rpt){
        this.rpt = rpt;
//      MediaPrintableArea area = Printing.getPrintArea();
//      int dim1=(int) area.getWidth(MediaPrintableArea.MM);
//      int dim2=(int) area.getHeight(MediaPrintableArea.MM);
//      rpt.setPreferredSize(new Dimension(3*dim1,3*dim2));
        
        Rectangle area = BaseReport.getDisplayDim();
        rpt.setPreferredSize(new Dimension(area.width,area.height));
//        rpt.setPreferredSize(new Dimension(550,780));
        
        //  	((JPanel)rpt).setBackground(Color.RED);
//      rptPnl.setBackground(Color.BLUE);
        rptPnl.removeAll();
        rptPnl.add((JPanel)rpt);
        rptPnl.validate();
//      rptPane = new JScrollPane((JPanel)rpt);
//      pack();
    }
    
    //-----------------------------------------------------------------------
    private void create(){
        rptPnl.removeAll();
        if(rpt != null)
            rptPnl.add((JPanel)rpt);
//      rptPane = new JScrollPane((JPanel)rpt);
        
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        getContentPane().setLayout(gridBag);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        gridBag.setConstraints(rptPane, c);
        
        getContentPane().add(rptPane);
        
//      buttonBar = new BasicToolBar(null, new String[] {"Customer", "Dealer", "Print", "Cancel", "NextInv"}, 
//      new String[] {"Customer", "Dealer", "Print", "Cancel", "NextInv"},
//      new String[] {"Customer", "Dealer", "Print", "Cancel", "NextInv"});
        buttonBar = new BasicToolBar(null, new String[] {"Customer", "Dealer", "Print", "Cancel"}, 
                new String[] {"Customer", "Dealer", "Print", "Cancel"},
                new String[] {"Customer", "Dealer", "Print", "Cancel"});
        buttonBar.getButton(0).setMnemonic(KeyEvent.VK_C); // Customer Button
        buttonBar.getButton(1).setMnemonic(KeyEvent.VK_D); // Dealer Button
        buttonBar.getButton(2).setMnemonic(KeyEvent.VK_P); // Print Button
        
        buttonBar.setFloatable(false);
//      buttonBar.enableButton(0,false);
        
//        ErrorLogger.getInstance().TODO();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
        buttonBar.registerKeyboardAction(this, "Cancel", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke stroke2 = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0, true);
        buttonBar.registerKeyboardAction(buttonBar, "Next", stroke2, JComponent.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke stroke3 = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0, true);
        buttonBar.registerKeyboardAction(buttonBar, "Prev", stroke3, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        buttonBar.setLayout( new FlowLayout(FlowLayout.CENTER));
        buttonBar.addActionListener(this);
//		ButtonBarTranslator translator = new ButtonBarTranslator(this, buttonBar);

        getContentPane().add(buttonBar);
        
//      rpt.setPreferredSize(new Dimension(450,650));
//      if(rpt != null)
//      rpt.setPreferredSize(new Dimension(550,710));
        
//      rptPnl.setPreferredSize(new Dimension(555,620));
        
        if(Configuration.Config.IDE)
            rptPane.setPreferredSize(new Dimension(555,740));
        else
//          rptPane.setPreferredSize(new Dimension(655,720));
            rptPane.setPreferredSize(new Dimension(555,620));
        
        setTitle("Report Preview");
        setModal(true);
        
        pack();
    }
    //-----------------------------------------------------------------------
    public void setInvoice(int invoiceNum){
        try{
            rpt.setInvoiceNumber(invoiceNum);
            setFormat(rptFormat);
            pack();
        }catch (Exception e){
//          ErrorLogger.getInstance().logMessage(e);	    
        }
    }
    //-----------------------------------------------------------------------
    public void setFormat(int format){
        rptFormat = format;
        if(rptFormat == SHORT_FORMAT ){
            buttonBar.enableButton(1,false);
            buttonBar.enableButton(0,true);
        }
        if(rptFormat == LONG_FORMAT){
            buttonBar.enableButton(0,false);
            buttonBar.enableButton(1,true);
        }
        
        rpt.setFormat(rptFormat);
        pack();
        rpt.repaint();
    }
    //-----------------------------------------------------------------------
//  public void setText(String text){
//  pack();
//  }
    //-----------------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand().toUpperCase().trim();
        ErrorLogger.getInstance().logDebugCommand(command);

        if (command.equals("CANCEL")) { //cancel
            this.setVisible(false);
            if (exitOnCancel)  
            	System.exit(0);
        } else if (command.equals("NEXT")) { //switch pages
            rpt.actionPerformed(e);
        } else if (command.equals("PREV")) { //switch pages
            rpt.actionPerformed(e);
        } else if (command.equals("CUSTOMER")) { //Long format
            setFormat(1);
        } else if (command.equals("DEALER")) { //Short Format
            setFormat(0);
        } else if (command.equals("NEXTINV")) { //Short Format
            if(currInv <= invoices.length){
                setInvoice(invoices[++currInv]);
                pack();
            }
        }else if (command.equals("PRINT")) { //print
            try {
                rmk.reports.Printing.printReport(rpt);
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, e1);
            }
        } else {  // Undefined
            ErrorLogger.getInstance().logMessage(this.getClass().getName() + ":" + command + "|");
        }
    }
    
    //=======================================================================
}
