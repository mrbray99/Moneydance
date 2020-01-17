package com.moneydance.modules.features.forecaster;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

public class MyPercEditor extends DefaultCellEditor { 
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings({"serial" })
	public MyPercEditor() {
		super(new JFormattedTextField());	 
	  }
	
	  //Override to invoke setValue on the formatted text field.
	  @Override
	public Component getTableCellEditorComponent(JTable table,
	          Object value, boolean isSelected,
	          int row, int column) {
		 JFormattedTextField ftf = (JFormattedTextField)super.getTableCellEditorComponent(
  	                table, value, isSelected, row, column);
		 ftf.setText((String)value);		 
		 return ftf;
	  }
	

	  @Override
	public Object getCellEditorValue() {
	        JFormattedTextField ftf = (JFormattedTextField)getComponent();
	      return ftf.getText();
	  }
	
	  //Override to check whether the edit is valid,
	  //setting the value if it is and complaining if
	  //it isn't.  If it's OK for the editor to go
	  //away, we need to invoke the superclass's version 
	  //of this method so that everything gets cleaned up.
	@Override
	public boolean stopCellEditing() {
	      JFormattedTextField ftf = (JFormattedTextField)getComponent();
	      if (!validateRPI(ftf.getText()))
	    	  return false;
        return super.stopCellEditing();
	}
	/*
	 * Validate string as RPI
	 */
	private boolean validateRPI(String strRPI) {
        double dTemp;
		try {
			dTemp = Double.parseDouble(strRPI);
		} catch (NumberFormatException pe) {
			if (strRPI.endsWith("%"))
				strRPI = strRPI.substring(0, strRPI.length() - 1);
			try {
				dTemp = Double.parseDouble(strRPI);
			} catch (NumberFormatException pe2) {
				JFrame fTemp = new JFrame();
				JOptionPane.showMessageDialog(fTemp,
						"Invalid RPI amount");
				return false;
			}
		}
		if (dTemp < -100.00 || dTemp > 100.00) {
			JFrame fTemp = new JFrame();
			JOptionPane.showMessageDialog(fTemp,
					"RPI amount must be within -100 to +100");
			return false;
		}
      return true;
		
	}
}
