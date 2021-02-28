package com.moneydance.modules.features.reportwriter.view;

import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;

import javafx.beans.value.ObservableValue;
import javafx.scene.layout.GridPane;

public class MyGridPane extends GridPane  {
	private String windowName;
	private Double winWidth;
	private Double winHeight;
	public MyGridPane(String windowName) {
		super();
		this.windowName = windowName;
		this.heightProperty().addListener((ObservableValue<? extends Number> ov, Number oldV, Number newV)-> {
			winHeight = (Double)newV;
			Main.preferences.put(Constants.PROGRAMNAME+".WS"+windowName+"HEIGHT", winHeight);
			Main.preferences.isDirty();
			this.setPrefHeight(winHeight);
			this.setMinHeight(winHeight);			
		});
		this.widthProperty().addListener((ObservableValue<? extends Number> ov, Number oldV, Number newV)-> {
			winWidth = (Double)newV;
			Main.preferences.put(Constants.PROGRAMNAME+".WS"+windowName+"WIDTH", winWidth);
			Main.preferences.isDirty();
			this.setPrefWidth(winWidth);
			this.setMinWidth(winWidth);			
		});
		winHeight = Main.preferences.getDouble(Constants.PROGRAMNAME+".WS"+windowName+"HEIGHT",Constants.DATASCREENHEIGHTMIN);
		winWidth = Main.preferences.getDouble(Constants.PROGRAMNAME+".WS"+windowName+"WIDTH",Constants.DATASCREENWIDTHMIN);
		this.setPrefHeight(winHeight);
		this.setMinHeight(winHeight);
		this.setPrefWidth(winWidth);
		this.setMinWidth(winWidth);

	}
	public Double getWinWidth() {
		return winWidth;
	}
	public Double getWinHeight() {
		return winHeight;
	}
	
}
