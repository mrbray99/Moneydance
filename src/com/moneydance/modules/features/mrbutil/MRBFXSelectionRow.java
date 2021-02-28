package com.moneydance.modules.features.mrbutil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class MRBFXSelectionRow {
	private String text;
	private String type;
	private BooleanProperty selected = new SimpleBooleanProperty(false);
	private String rowId;
	private String sortText;
	private Integer depth;
	private MRBFXSelectionRow thisRow;
	private MRBFXSelectionPanel panel;
	private BooleanProperty inActive= new SimpleBooleanProperty(false);
	public MRBFXSelectionRow(String rowIdp,String textp, String typep, Boolean selectedp) {
		rowId=rowIdp;
		text = textp;
		type=typep;
		selected.set(selectedp);
		thisRow = this;
	}
	public void setPanel(MRBFXSelectionPanel panelp) {
		panel= panelp;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isSelected() {
		return selected.get();
	}
	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}
	public String getRowId() {
		return rowId;
	}
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	
	public String getSortText() {
		return sortText;
	}
	public void setSortText(String sortText) {
		this.sortText = sortText;
	}
	
	public Integer getDepth() {
		return depth;
	}
	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	
	public boolean isInActive() {
		return inActive.get();
	}
	public void setInActive(boolean inActive) {
		this.inActive.set(inActive);
	}
	public CheckBox getCol1() {
		CheckBox check = new CheckBox();
		check.setSelected(selected.get());
		check.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ob, Boolean ov, Boolean nv) {
				setSelected(nv);
				if (panel != null)
					panel.setChildren(thisRow,nv);
			}
		});
		return check;
	}
	public HBox getCol2() {
		HBox listRow = new HBox();
		Region region1 = new Region();
		HBox.setHgrow(region1,  Priority.ALWAYS);
		Label textLbl = new Label(getText());
		Label typeLbl = new Label(getType());
		listRow.getChildren().addAll(textLbl,region1,typeLbl);
		return listRow;
	}
	
}
