package com.moneydance.modules.features.reportwriter2.edit;

import java.util.SortedMap;
import java.util.TreeMap;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.edit.UndoDetail.DetailType;



public class UndoRecord {
	private Long idNumber = 0L;
	private int nextDetail;
	private SortedMap<Integer,UndoDetail> details;
	public UndoRecord() {
		details=new TreeMap<Integer, UndoDetail>();
		nextDetail = 0;
	}
	public void addDetail(UndoDetail record) {
		details.put(nextDetail++,record);
	}
	public UndoDetail newDetail(UndoAction object, DetailType type) {
		UndoDetail newDetail = new UndoDetail();
		newDetail.setIdNumber(idNumber);
		newDetail.setDetailObj(object);
		newDetail.setObjectName(type);
		return newDetail;
	}
	public Long getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(Long idNumber) {
		this.idNumber = idNumber;
	}
	public void undo() {
		for (Integer i = nextDetail-1;i>=0;i--) {
			UndoDetail detailRecord = details.get(i);
			if (detailRecord != null)
				detailRecord.undo();
			
		}
	}
	public void redo() {
		for (Integer i = nextDetail-1;i>=0;i--) {
			UndoDetail detailRecord = details.get(i);
			if (detailRecord != null)
				detailRecord.redo();
			
		}

	}
	public void newAction(UndoAction object, DetailType type, String before, String after, String fieldName) {
		UndoDetail undoDetail = newDetail(object,type);
		UndoFieldDetail fieldDetail = undoDetail.newFieldDetail();
		fieldDetail.setAction(type.getTypeName());
		fieldDetail.setFieldName(fieldName);
		fieldDetail.setBeforeValue(before);
		fieldDetail.setAfterValue(after);
		undoDetail.addFieldDetail(fieldDetail);
		addDetail(undoDetail);
	}
}
