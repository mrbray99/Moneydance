package com.moneydance.modules.features.dataextractor;

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.moneydance.awt.GridC;
import com.moneydance.modules.features.mrbutil.MRBLocale;

public class ColumnPane extends JPanel {
	private MRBLocale locale = Main.locale;

	
	public ColumnPane(){
		super();
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		TitledBorder title;
		title = BorderFactory.createTitledBorder(locale.getString(Constants.COP_TITLE,"Columns"));
		setBorder(title);
		JLabel columnNo = new JLabel(locale.getString(Constants.COP_COLUMN,"Column No."));
		JLabel fldName = new JLabel(locale.getString(Constants.COP_FLDNAME,"Field"));
		JLabel fldHead = new JLabel(locale.getString(Constants.COP_HEADING,"Heading"));
		int ix=0;
		int iy=0;
		add(columnNo, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		add(fldName, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		add(fldHead, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
	}
}
