package com.moneydance.modules.features.dataextractor;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.moneydance.awt.GridC;

public class CalculateFieldPane extends JPanel {

	public CalculateFieldPane(){
		GridBagLayout layout = new GridBagLayout();
		setLayout (layout);
		JLabel typeLbl = new JLabel(Main.locale.getString(Constants.CFP_TYPE,"Field Type:"));
		JComboBox<String> typesCB = new JComboBox<>(CalculateField.getTypeArray());
		typesCB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> type = (JComboBox<String>)e.getSource();
				typeSelected((String)type.getSelectedItem());
				
			}
			
		});
		int ix=0;
		int iy=0;
		add(typeLbl, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		add(typesCB, GridC.getc(ix,iy++).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		ix=0;
		JPanel fieldPane = new JPanel();
		add(fieldPane, GridC.getc(ix,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
	}
	
	private void typeSelected(String type){
		if (type.equals(CalculateField.getDesc(Constants.CalculateFieldType.FORMULA))) {
			//TODO formula pane
		} else
		if (type.equals(CalculateField.getDesc(Constants.CalculateFieldType.STATUSLOOKUP))) {
			//TODO status pane
		} else
		if (type.equals(CalculateField.getDesc(Constants.CalculateFieldType.TYPELOOKUP))) {
			//TODO type look up pane
		} else
		if (type.equals(CalculateField.getDesc(Constants.CalculateFieldType.DATERANGE))) {
			//TODO date range pane
		} else
		if (type.equals(CalculateField.getDesc(Constants.CalculateFieldType.CHEQUERANGE))) {
			//TODO cheque range pane
		} else
		if (type.equals(CalculateField.getDesc(Constants.CalculateFieldType.TAGLOOKUP))) {
			//TODO tag lookup pane
		} else
		if (type.equals(CalculateField.getDesc(Constants.CalculateFieldType.DESCRIPTION))) {
			//TODO description pane
		}	
	}
	public void build() {
		
	}

}
