package com.moneydance.modules.features.reportwriter2.view.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

public class ReportLayoutLabel extends Label {
	private BooleanProperty selected = new SimpleBooleanProperty(false);
	private BooleanProperty inError = new SimpleBooleanProperty(false);
	private DoubleProperty bottomY = new SimpleDoubleProperty(0.0);
	private DoubleProperty rightX = new SimpleDoubleProperty(0.0);
	private DoubleProperty topY = new SimpleDoubleProperty(0.0);
	private DoubleProperty leftX = new SimpleDoubleProperty(0.0);
	private ReportLayout layout;
	public ReportLayoutLabel(ReportLayout layout) {
		this.layout=layout;
		leftX.bind(this.layout.layoutXProperty());
		topY.bind(this.layout.layoutYProperty());
		rightX.bind(this.layout.layoutXProperty().add(layout.widthProperty()));
		bottomY.bind(this.layout.layoutYProperty().add(layout.heightProperty()));
	}
	public Boolean isSelected() {
		return selected.get();
	}
	public BooleanProperty selectedProperty() {
		return selected;
	}
	
	public Boolean getSelected() {
		return selected.get();
	}

	public void setSelected(Boolean selected) {
		this.selected.set(selected);
	}
		
	public Boolean getInError() {
		return inError.get();
	}
	public BooleanProperty inErrorProperty() {
		return inError;
	}

	public void setInError(Boolean error) {
		this.inError.set(error);	
	}
	public Double getBottomY() {
		return bottomY.get();
	}
	public DoubleProperty bottomYProperty() {
		return bottomY;
	}
	public void setBottomY(Double bottomY) {
		this.bottomY.set(bottomY);
	}
	public Double getRightX() {
		return rightX.get();
	}
	public DoubleProperty rightXProperty() {
		return rightX;
	}
	public void setRightX(Double rightX) {
		this.rightX.set(rightX);
	}
	public Double getTopY() {
		return topY.get();
	}
	public DoubleProperty topYProperty() {
		return topY;
	}
	public void setTopY(Double topY) {
		 this.topY.set(topY);
	}
	public Double getLeftX() {
		return leftX.get();
	}
	public DoubleProperty leftXProperty() {
		return leftX;
	}
	public void setLeftX(Double leftX) {
		this.leftX.set(leftX);
	}
	
}
