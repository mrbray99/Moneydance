package com.moneydance.modules.features.forecaster;


public class IncludedReminder implements java.io.Serializable {
	private static final long serialVersionUID = 1l;
	private boolean bSelected;
	public IncludedReminder(boolean bSelectedp) {
		bSelected = bSelectedp;
	}
	/*
	 * Selected
	 */
	public boolean getSelected() {
		return bSelected;
	}
	public void setSelected(boolean bSelectedp) {
		bSelected = bSelectedp;
	}
}

