package com.moneydance.modules.features.reportwriter2.view;

import java.io.IOException;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.FieldSelectListener;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportLayout;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.FieldPaneController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FieldPane extends ScreenPanel implements Callback<String,Integer>{
	public static ReportLayout fieldBeingMoved;
	public static ReportField itemBeingDragged;
	public static ReportLayout selectedField;
	private FXMLLoader loader;
	private FieldPaneController controller;
	private GridPane fieldBox;
	private Scene scene;
	private Stage stage;
	private ReportTemplate template;
	public FieldPane(ReportTemplate template) {
		super();
		this.template = template;
		try {
			loader = new FXMLLoader(getClass().getResource(Constants.RESOURCES+Constants.FIELDPANE));
			controller = new FieldPaneController(this);
			loader.setController(controller);
			fieldBox = loader.load();
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		controller.setFields(this.template);
		scene = new Scene(fieldBox);
		stage = new Stage();
		stage.setScene(scene);
		boolean max = Main.preferences.getBoolean(Constants.PROGRAMNAME+"."+Constants.FIELDMAXIMISE,true);
		if (max) 
			stage.setMaximized(true);
		else {
			stage.setWidth( Main.preferences.getDouble(Constants.PROGRAMNAME+"."+Constants.FIELDWIDTH,Constants.MINFIELDWIDTH));
			stage.setHeight( Main.preferences.getDouble(Constants.PROGRAMNAME+"."+Constants.FIELDHEIGHT,Constants.MINFIELDHEIGHT));
		}
		stage.maximizedProperty().addListener((ov, oldv, newv)->{
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.FIELDMAXIMISE,(boolean)newv);
			Main.rwDebugInst.debugThread("FieldPane", "maximisedProperty", MRBDebug.DETAILED, "Field maximised "+newv);
			Main.preferences.isDirty();
			if (!newv) {
				stage.setWidth( Main.preferences.getDouble(Constants.PROGRAMNAME+"."+Constants.FIELDWIDTH,Constants.MINFIELDWIDTH));
				stage.setHeight( Main.preferences.getDouble(Constants.PROGRAMNAME+"."+Constants.FIELDHEIGHT,Constants.MINFIELDHEIGHT));
			}
		});
		stage.widthProperty().addListener((ov, oldv, newv)->{
			if (!stage.isMaximized()) {
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.FIELDWIDTH,(double)newv);			
				Main.rwDebugInst.debugThread("FieldPane", "widthProperty", MRBDebug.DETAILED, "Field width "+newv);
				Main.preferences.isDirty();
			}
		});
		stage.heightProperty().addListener((ov, oldv, newv)->{
			if (!stage.isMaximized()) {
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.FIELDHEIGHT,(double)newv);			
				Main.rwDebugInst.debugThread("FieldPane", "heightProperty", MRBDebug.DETAILED, "Field height"+newv);
				Main.preferences.isDirty();
			}
		});
	}
	public void showFields() {
		stage.showAndWait();		
	}
	public Integer call(String command) {
		switch (command) {
		case Constants.CLOSEFIELDLIST:
			stage.close();
			return 0;
		}
		return 0;
	}
	public void addFieldListener(FieldSelectListener listener) {
		controller.addFieldListener(listener);
	}
	public void removeFieldListener(FieldSelectListener listener) {
		controller.removeFieldListener(listener);
	} 
}
