package com.moneydance.modules.features.reportwriter2.view.tables;

import com.infinitekind.util.DateUtil;
import com.moneydance.modules.features.reportwriter2.Main;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TemplateListRow {
	private boolean select;
	private String name;
	private String description;
	private String records;
	private String who;
	private String target="";
	private String graphics="";
	private int dateInt;
	private boolean present;
	private String lastDate;
	public TemplateListRow (String line) {
		String [] fields = line.split("\\|");
		name = fields[0];
		description = fields[1];
		records=fields[2];
		who=fields[3];
		if (fields.length>4)
			target=fields[4];
		if (fields.length>5)
			graphics=fields[5];
		if (fields.length>6)
			dateInt=Integer.valueOf(fields[6]);
	}
	public boolean isSelect() {
		return select;
	}
	public CheckBox getSelect() {
		CheckBox box = new CheckBox();
		box.setSelected(isSelect());
		box.selectedProperty().addListener((ov,oldv,newv)->{
			setSelect(newv);
		});
		return box;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRecords() {
		return records;
	}
	public void setRecords(String records) {
		this.records = records;
	}
	public String getWho() {
		return who;
	}
	public void setWho(String who) {
		this.who = who;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getGraphics() {
		return graphics;
	}
	public void setGraphics(String graphics) {
		this.graphics = graphics;
	}
	public String [] getRecordsList() {
		return records.split(";");
	}
	public String [] getTargetList() {
		return target.split(";");
	}
	public String [] getGraphicList() {
		return graphics.split(";");
	}
	
	public int getDateInt() {
		return dateInt;
	}
	public void setDateInt(int dateInt) {
		this.dateInt = dateInt;
	}
	
	public String getPresent() {
		if (present)
			return "Y";
		return "N";
	}
	public void setPresent(boolean present) {
		this.present = present;
	}
	public String getLastDate() {
		return lastDate;
	}
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
	public VBox getRecordsColumn() {
		VBox list = new VBox();
		String [] recordsList = getRecordsList();
		for (int i=0;i<recordsList.length;i++) {
			Label recordLbl = new Label(recordsList[i]);
			list.getChildren().add(recordLbl);
		}
		list.setSpacing(5.0);
		return list;
	}
	public VBox getTargetColumn() {
		VBox list = new VBox();
		String [] targetList = getTargetList();
		for (int i=0;i<targetList.length;i++) {
			Label recordLbl = new Label(targetList[i]);
			list.getChildren().add(recordLbl);
		}
		list.setSpacing(5.0);
		return list;
	}
	public String getDateString() {
		String newDate = Main.cdate.format(DateUtil.convertIntDateToLong(dateInt));
		return newDate;
	}


}
