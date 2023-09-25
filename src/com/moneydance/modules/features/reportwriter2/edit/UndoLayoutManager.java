package com.moneydance.modules.features.reportwriter2.edit;

import java.util.SortedMap;
import java.util.TreeMap;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Main;

public class UndoLayoutManager {
	private SortedMap<Long, UndoRecord> stack;
	private Long currentID;
	private Long nextID;
	private Boolean undoUnderway=false;
	private static UndoLayoutManager instance = null;
	public UndoLayoutManager() {
		stack = new TreeMap<Long,UndoRecord>();
		currentID=0L;
		nextID=1L;
	}
	public static UndoLayoutManager getInstance() {
		if (instance == null)
			instance = new UndoLayoutManager();
		return instance;
	}
	public UndoRecord newRecord() {
		UndoRecord newRec = new UndoRecord();
		currentID=nextID;
		newRec.setIdNumber(currentID);
		nextID =currentID+1L;
		if (!stack.isEmpty()) {
			for(Long  i = currentID+1;i<= stack.lastKey(); i++) {
				if (stack.get(i) != null)
					stack.remove(i);
			}
		}
		return newRec;
	}
	public void addRecord(UndoRecord record) {
		stack.put(record.getIdNumber(), record);
	}
	public Boolean undo() {
		UndoRecord record = stack.get(currentID);
		if ( record!=null) {
			record.undo();
			nextID = currentID;
			currentID--;
		}
		return false;
	}
	public Boolean redo() {
		UndoRecord record = stack.get(currentID+1);
		if (record!=null) {
			record.redo();
			currentID++;
			nextID=currentID+1;;
		}
		return false;
	}
	public Boolean getUndoUnderway() {
		return undoUnderway;
	}
	public void setUndoUnderway(Boolean undoUnderway) {
		this.undoUnderway = undoUnderway;
	}
	

}
