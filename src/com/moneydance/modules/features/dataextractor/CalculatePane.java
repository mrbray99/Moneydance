package com.moneydance.modules.features.dataextractor;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.moneydance.awt.GridC;
import com.moneydance.modules.features.mrbutil.MRBLocale;

public class CalculatePane extends JPanel {
	private MRBLocale locale = Main.locale;
	private List<CalculateField> calcFields;
	private Parameters params;

	
	public CalculatePane(Parameters paramsp){
		super();
		params = paramsp;
		calcFields = params.getCalcFields();
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		TitledBorder title;
		title = BorderFactory.createTitledBorder(locale.getString(Constants.CP_TITLE,"Calculated Fields"));
		setBorder(title);
		JLabel fldName = new JLabel(locale.getString(Constants.CP_FLDNAME,"Field Name"));
		JLabel fldType = new JLabel(locale.getString(Constants.CP_FLDTYPE,"Type"));
		JButton addBtn = new JButton("+");
		int ix=0;
		int iy=0;
		add(fldName, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		add(fldType, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		add(addBtn, GridC.getc(ix,iy++).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		addBtn.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				CalculateField newField = new CalculateField();
				CalculateFieldPane newFieldPane = new CalculateFieldPane();
				newFieldPane.build();
				JOptionPane.showMessageDialog(null, newFieldPane,"Information",JOptionPane.INFORMATION_MESSAGE);
				
			}
			
		});
		ix=0;
		for (CalculateField field : calcFields){
			JLabel name = new JLabel(field.getName());
			add(name, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));
			JLabel type = new JLabel(field.getTypeDesc());
			add(type, GridC.getc(ix,iy++).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
					Constants.BOTTOMINSET, Constants.RIGHTINSET));

		}
	}
	public void fieldAdded(CalculateFieldPane pane) {
		
	}

}
