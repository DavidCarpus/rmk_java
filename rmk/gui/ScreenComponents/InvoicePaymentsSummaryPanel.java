package rmk.gui.ScreenComponents;

import javax.swing.*;

import carpus.util.Formatting;

import java.awt.*;

import rmk.database.FinancialInfo;
import rmk.database.dbobjects.Invoice;
import java.text.NumberFormat;

public class InvoicePaymentsSummaryPanel 
    extends JPanel
//      implements Printable, Pageable
{
    String labelsText[] = {"Total", "-Discount", "SubTotal", "+Shipping", "+Tax", "-Payments", "Due:"};
    JLabel labels[] = new JLabel[labelsText.length];
    JLabel fields[] = new JLabel[labels.length];

    static final int FIELD_RETAIL = 0;
    static final int FIELD_DISCOUNT = 1;
    static final int FIELD_SUBTOTAL = 2;
    static final int FIELD_SHIPPING = 3;
    static final int FIELD_TAX = 4;
    static final int FIELD_PAYMENTS = 5;
    static final int FIELD_DUE = 6;
    

    InvoicePaymentsSummaryPanel(){
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	setLayout(gridbag);

  	c.fill = GridBagConstraints.NONE;
//  	JLabel label;

	for(int labelIndex=0; labelIndex < labels.length; labelIndex++){
	    c.gridx = 0;
	    c.gridy = labelIndex;
	    c.anchor = GridBagConstraints.WEST;
	    labels[labelIndex] = new JLabel(labelsText[labelIndex]);
	    gridbag.setConstraints(labels[labelIndex], c);
	    add(labels[labelIndex]);
	    labels[labelIndex].setBackground(Color.BLUE);

	    
	    c.gridx = 1;
	    c.ipadx = 20;
	    c.anchor = GridBagConstraints.EAST;
	    fields[labelIndex] = new JLabel("$0.00");
//  	    label.setText("Value:" + labelIndex);
	    gridbag.setConstraints(fields[labelIndex], c);

	    add(fields[labelIndex]);
	}
	
    }
	void setInvoice(Invoice invoice) {
		carpus.util.Formatting formatter = new carpus.util.Formatting();
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMinimumFractionDigits(2);

		rmk.DataModel sys = rmk.DataModel.getInstance();
		double totalPayments =
			sys.financialInfo.getTotalInvoicePayments(invoice.getInvoice());
		double discount = sys.financialInfo.getTotalInvoiceDiscount(invoice);
		double retail = sys.financialInfo.getTotalRetail(invoice);
		double shipping = invoice.getShippingAmount();
		double taxes = invoice.getTaxPercentage();
		if (taxes > 1)
			taxes /= 100.0;

		fields[FIELD_RETAIL].setText(Formatting.financial(retail));
		if (FinancialInfo.roundDollarAmt(retail) < 0)
			fields[FIELD_RETAIL].setForeground(Color.RED);
		else
			fields[FIELD_RETAIL].setForeground(Color.BLACK);

		fields[FIELD_DISCOUNT].setText(Formatting.financial(discount));
		if (FinancialInfo.roundDollarAmt(discount) < 0)
			fields[FIELD_DISCOUNT].setForeground(Color.RED);
		else
			fields[FIELD_DISCOUNT].setForeground(Color.BLACK);
		double discPercent = invoice.getDiscountPercentage();
		
		if (discPercent > 0) {
			labels[FIELD_DISCOUNT].setText(
				labelsText[FIELD_DISCOUNT]
					+ " "
					+ percentFormat.format(discPercent));
		} else{ // don't display a percentage if it's zero
			labels[FIELD_DISCOUNT].setText(
					labelsText[FIELD_DISCOUNT]);
		}

		fields[FIELD_SUBTOTAL].setText(Formatting.financial(retail - discount));
		if (FinancialInfo.roundDollarAmt(retail - discount) < 0)
			fields[FIELD_SUBTOTAL].setForeground(Color.RED);
		else
			fields[FIELD_SUBTOTAL].setForeground(Color.BLACK);

		fields[FIELD_SHIPPING].setText(Formatting.financial(shipping));
		if (FinancialInfo.roundDollarAmt(shipping) < 0)
			fields[FIELD_SHIPPING].setForeground(Color.RED);
		else
			fields[FIELD_SHIPPING].setForeground(Color.BLACK);

		double taxesDue = sys.financialInfo.getInvoiceTaxes(invoice);
		fields[FIELD_TAX].setText(Formatting.financial(taxesDue));
		if (taxesDue < 0)
			fields[FIELD_TAX].setForeground(Color.RED);
		else
			fields[FIELD_TAX].setForeground(Color.BLACK);

		//    	System.out.println(this.getClass().getName() + ":Taxes:"+ taxes);
		//  	if(taxes > 0){
		labels[FIELD_TAX].setText(
			labelsText[FIELD_TAX] + " " + percentFormat.format(taxes));
		//  	}
		if (taxesDue > 0)
			labels[FIELD_TAX].setForeground(
				rmk.gui.InvoiceDetailsScreen.DK_GREEN);
		else
			labels[FIELD_TAX].setForeground(Color.BLACK);

		fields[FIELD_PAYMENTS].setText(Formatting.financial(totalPayments));
		if (totalPayments < 0)
			fields[FIELD_PAYMENTS].setForeground(Color.RED);
		else
			fields[FIELD_PAYMENTS].setForeground(Color.BLACK);

		double due = sys.financialInfo.getInvoiceDue(invoice);
		fields[FIELD_DUE].setText(
			Formatting.financial(FinancialInfo.roundDollarAmt(due)));
		if (FinancialInfo.roundDollarAmt(due) < 0)
			fields[FIELD_DUE].setForeground(Color.RED);
		else
			fields[FIELD_DUE].setForeground(Color.BLACK);
	}

    //========================================================
    public static void main(String args[])throws Exception{
	rmk.gui.Application.main(args);
    }


    //   42683
}
