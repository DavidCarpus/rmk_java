package carpus.gui;

import java.awt.*;
import javax.swing.*;
import java.text.*;
import javax.swing.text.*;

public class FormattedTextFields{
    public static JTextField getNameField(String text, int size){
	FirstCharUpperCaseField results  = new FirstCharUpperCaseField(size, text);
	results.addFocusListener(new SelectAllText(results));

	if(text != null && text.length() > 0){
	    results.setText(text);
	}	
	return results;
    }

    public static JFormattedTextField getPercentageField(double amt){
	JFormattedTextField results = new JFormattedTextField(NumberFormat.getPercentInstance());
	results.addFocusListener(new SelectAllText(results));
  	results.setValue(new Double(0));
	results.setColumns(5);
	return results;
    }

    public static JFormattedTextField getCurrencyField(double amt){
//   throws Exception{
	NumberFormat formatter    = NumberFormat.getCurrencyInstance();
	JFormattedTextField results = new JFormattedTextField(formatter);
	results.addFocusListener(new SelectAllText(results));
  	results.setValue(new Double(0));
	results.setColumns(7);
	return results;
    }

    public static JFormattedTextField getDateField() {
    	DateFormat format = new SimpleDateFormat("MM/dd/yy");
	format.setLenient(true);
  	DateFormatter formatter = new DateFormatter(format);
	JFormattedTextField results = new JFormattedTextField(formatter);
	results.setMinimumSize(new Dimension(80,20));
	results.setPreferredSize(new Dimension(80,20));
	results.addFocusListener(new SelectAllText(results));
//  	results.setValue(new java.util.Date());
	return results;
    }

//      public static JFormattedTextField getSSN() {// US Social Security number
//  	try{
//  	    MaskFormatter formatter = new MaskFormatter("###-##-####");
//  	    formatter.setPlaceholderCharacter('_');
//  	    return new JFormattedTextField(formatter);
//  	} catch (Exception e){
//  	    carpus.util.Logger.getInstance().logError("Creating SSN Field", e);
//  	    return new JFormattedTextField();
//  	}
//      }

}
