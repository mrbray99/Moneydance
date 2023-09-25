package com.moneydance.modules.features.reportwriter2.edit;

import java.util.List;

public interface UndoAction {
	public void undo(List<UndoFieldDetail> changes);
	public void redo(List<UndoFieldDetail> changes);
}
