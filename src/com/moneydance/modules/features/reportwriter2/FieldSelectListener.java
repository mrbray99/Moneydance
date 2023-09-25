package com.moneydance.modules.features.reportwriter2;

import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;

public interface FieldSelectListener {
	public void fieldSelectionAdded(ReportField field);
	public void fieldSelectionRemoved(ReportField field);
	public void fieldSelectionUpdated(ReportField field,boolean selected);
	public boolean checkFieldDelete(ReportField field);
}
