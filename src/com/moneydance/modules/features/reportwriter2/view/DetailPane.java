package com.moneydance.modules.features.reportwriter2.view;

import java.io.IOException;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
 
public class DetailPane extends AnchorPane {
	private DetailPane controller;
	private FXMLLoader loader;
	private GridPane pane;
	protected ReportTemplate template;
	public DetailPane(String fxmlFile, ReportTemplate template){
		this.controller = this;
		this.template = template;	
		try {
			loader = new FXMLLoader(getClass().getResource(Constants.RESOURCES+fxmlFile));
			loader.setController(controller);
			pane = loader.load();
			this.getChildren().add(pane);
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
	}
	public void setLine() {
		setStyle("-fx-border-color:black;-fx-border-width:2px 0px 0px 0px;");
	}

}
