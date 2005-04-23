package rmk.gui.search;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class InvoicesSearchPanel 
    extends CriteriaPanel
{
    static final int FIELD_INVOICE_START   =0;
    static final int FIELD_INVOICE_END     =1;
    static final int FIELD_ESTIMATED_START =2;
    static final int FIELD_ESTIMATED_END   =3;
    static final int FIELD_SHIPPED_START   =4;
    static final int FIELD_SHIPPED_END     =5;
    static final int FIELD_MODEL           =6;
    static final int FIELD_FEATURES        =7;
    static final String fieldNames[] = {"InvoiceStart", "InvoiceEnd", 
					"InvoiceEstimatedStart", "InvoiceEstimatedEnd",
					"InvoiceShippedStart", "InvoiceShippedEnd",
					"InvoiceItemModel", "InvoiceFeatures"};
					
    JComponent[] txtFields = new JComponent[8];

    public InvoicesSearchPanel(){
	BoxLayout layout = new BoxLayout(this,BoxLayout.Y_AXIS);
	setLayout(layout);
	add(invInfo());
	add(modInfo());
	setPreferredSize(new Dimension(300,100));
    }

    //========================================================
    public Vector getCriteria(){
	Vector results = new Vector();
	String value="";
	for(int i=0; i < txtFields.length; i++){
	    value = ((JTextField)txtFields[i]).getText().trim();
	    if(value.length() > 0) results.add(fieldNames[i] + "-" + value);
	}
	return results;
    }
    JPanel modInfo(){
	JPanel results = new JPanel();
	GridLayout layout = new GridLayout(2,2);
	results.setLayout(layout);

	txtFields[FIELD_MODEL] = new JTextField("",8);
	results.add(new JLabel("Model"));
	results.add((JTextField)txtFields[FIELD_MODEL]);

	txtFields[FIELD_FEATURES] = new JTextField("",10);
	results.add(new JLabel("Features"));
	results.add((JTextField)txtFields[FIELD_FEATURES]);
	return results;
    }

    JPanel invInfo(){
	JPanel results = new JPanel();
	GridLayout layout = new GridLayout(3,3);
	results.setLayout(layout);

	txtFields[FIELD_INVOICE_START] = new JTextField("", 6);
	txtFields[FIELD_INVOICE_END] = new JTextField("", 6);
	results.add(new JLabel("Invoice"));
	results.add(txtFields[FIELD_INVOICE_START]);
	results.add(txtFields[FIELD_INVOICE_END]);

	txtFields[FIELD_ESTIMATED_START] = new JTextField("", 6);
	txtFields[FIELD_ESTIMATED_END] = new JTextField("", 6);
	results.add(new JLabel("Estimated"));
	results.add(txtFields[FIELD_ESTIMATED_START]);
	results.add(txtFields[FIELD_ESTIMATED_END]);

	txtFields[FIELD_SHIPPED_START] = new JTextField("", 6);
	txtFields[FIELD_SHIPPED_END] = new JTextField("", 6);
	results.add(new JLabel("Shipped"));
	results.add(txtFields[FIELD_SHIPPED_START]);
	results.add(txtFields[FIELD_SHIPPED_END]);


	return results;
    }
}
