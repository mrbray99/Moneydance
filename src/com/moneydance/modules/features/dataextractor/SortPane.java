package com.moneydance.modules.features.dataextractor;

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.moneydance.awt.GridC;
import com.moneydance.modules.features.mrbutil.MRBLocale;

public class SortPane extends JPanel {
	private MRBLocale locale = Main.locale;
	
	public SortPane(){
		super();
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		TitledBorder title;
		title = BorderFactory.createTitledBorder(locale.getString(Constants.SP_TITLE,"Sort Sequence"));
		setBorder(title);
		JLabel columnNo = new JLabel(locale.getString(Constants.COP_COLUMN,"Column No."));
		JLabel fldName = new JLabel(locale.getString(Constants.COP_FLDNAME,"Field"));
		JLabel sequence = new JLabel(locale.getString(Constants.SP_SEQUENCE,"Asc/Desc"));
		int ix=0;
		int iy=0;
		add(columnNo, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		add(fldName, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
		add(sequence, GridC.getc(ix++,iy).fillx().west().insets(Constants.TOPINSET, Constants.LEFTINSET,
				Constants.BOTTOMINSET, Constants.RIGHTINSET));
	}
}
