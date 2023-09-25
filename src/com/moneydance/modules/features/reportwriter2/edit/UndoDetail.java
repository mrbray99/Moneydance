package com.moneydance.modules.features.reportwriter2.edit;

import java.util.ArrayList;
import java.util.List;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.Constants.AnalyseType;

public class UndoDetail {
	private Long idNumber;
	private UndoAction detailObj;
	private DetailType objectName;
	private List<UndoFieldDetail> detail;
	public enum DetailType {
		UNKNOWN("unknown"),
		LAYOUT("layout"),
		MOVELAYOUT("movelayout"),
		FIELD("field"),
		MARGIN("margins"),
		PAPER("paper"),
		STYLE("style"),
		BANNER("banner"),
		ADDBANNER("addbanner"),
		ADDVARIABLE("addvariable"),
		ADDLAYOUT("addlayout"),
		UNSETBANNERFOOTER("unsetbannerfooter"),
		DELETEBANNER("deletebanner");
		private String typeName;
		DetailType(String typeName){
			this.typeName=typeName;
		}
		public String getTypeName() {
			return typeName;
		}
		public static DetailType findType(String typeName) {
				for (DetailType type:values()) {
					if(type.getTypeName().equalsIgnoreCase(typeName)) 	return type;
				}
				throw new IllegalArgumentException();				
		}
	}
	public UndoDetail() {
		detail = new ArrayList<UndoFieldDetail>();
	}
	public Long getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(Long idNumber) {
		this.idNumber = idNumber;
	}
	public Object getDetailObj() {
		return detailObj;
	}
	public void setDetailObj(UndoAction detailObj) {
		this.detailObj = detailObj;
	}
	public String getObjectName() {
		return objectName.getTypeName();
	}
	public void setObjectName(DetailType objectName) {
		this.objectName = objectName;
	}
	public List<UndoFieldDetail> getDetail() {
		return detail;
	}
	public void setDetail(List<UndoFieldDetail> detail) {
		this.detail = detail;
	}
	public UndoFieldDetail newFieldDetail() {
		return new UndoFieldDetail();
	}
	public void addFieldDetail(UndoFieldDetail fieldDetal) {
		detail.add(fieldDetal);
	}
	public void undo() {
		detailObj.undo(detail);
	}
	public void redo() {
		detailObj.redo(detail);
		
	}
	
}
