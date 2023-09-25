package com.moneydance.modules.features.reportwriter2.view.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

public class FieldLabel extends Label {
	private StringProperty fontName = new SimpleStringProperty();
	public FieldLabel() {
		super();
		fontName.set("");
	}
	public FieldLabel (String text) {
		super(text);
		fontName.set("");
	}
	public void setFontName(String fontName) {
		this.fontName.set(fontName);
	}
	public String getFontName() {
		return fontName.get();
	}
}
