package com.moneydance.modules.features.reportwriter2.view;

import java.io.IOException;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.PopUpController;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PopUpScreen extends Stage{
	private Scene scene;
	private GridPane pane = new GridPane();
	private VBox scenePane;
	private HBox buttons = new HBox();
	private Button okBtn;
	private Button cancelBtn;
	private PopUpScreen thisObj;
	private Callback<Integer,String> caller;
	private String result="";
	private FXMLLoader loader;
	public static final  Integer OKPRESSED = 0; 
	public static final Integer CANCELPRESSED = -1; 
	public static final String CLOSESCREEN = "close";
	public static final String LEAVEOPEN = "leave";
	public static final String CANCELLED = "cancelled";
	public PopUpScreen (Callback<Integer,String>caller ) {
		this.caller=caller;
		thisObj=this;
		setUpScreen();
		scenePane = new VBox();
		buttons= new HBox();
		setUpScreen();
		scenePane.getChildren().addAll(pane,buttons);
		scene = new Scene(scenePane);
		setScene(scene);
	}
	public PopUpScreen(String fxmlFileName,PopUpController controller,Callback<Integer,String>caller) {
		Main.rwDebugInst.debugThread("PopUpController","const", MRBDebug.DETAILED, "New popup screen from fxml "+fxmlFileName);
		this.caller = caller;
		thisObj= this;
		try {
			loader = new FXMLLoader(getClass().getResource(Constants.RESOURCES+fxmlFileName));
			loader.setController(controller);
			scenePane = loader.load();
			Main.rwDebugInst.debugThread("PopUpController","const", MRBDebug.DETAILED, "New popup screen loaded ");
		}
		catch (IOException e) {
			Main.rwDebugInst.debugThread("PopUpController","const", MRBDebug.DETAILED, "New popup screen failed "+fxmlFileName);
			e.printStackTrace();
			return;
		}
		buttons= controller.getButtons();
		setUpScreen();
		scene = new Scene(scenePane);
		setScene(scene);
	}
	private void setUpScreen() {
		okBtn = new Button();
		if (Main.loadedIcons.closeImg == null)
			okBtn.setText("Close");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.closeImg));
		okBtn.setOnAction((e)->{
			result=caller.call(OKPRESSED);
			if ( result.equals(CLOSESCREEN))
				thisObj.close();
		});
		Tooltip saveTT = new Tooltip("OK");
		okBtn.setTooltip(saveTT);
		cancelBtn = new Button();
		if (Main.loadedIcons.cancelImg == null)
			cancelBtn.setText("Cancel");
		else
			cancelBtn.setGraphic(new ImageView(Main.loadedIcons.cancelImg));
		Tooltip cancelTT = new Tooltip("Cancel");
		cancelBtn.setTooltip(cancelTT);
		cancelBtn.setOnAction((e)->{
			result=caller.call(CANCELPRESSED);
			if (result.equals(CLOSESCREEN)|| result.equals(CANCELLED))
				thisObj.close();
		});
		buttons.getChildren().addAll(okBtn, cancelBtn);
		buttons.setSpacing(5.0);
		HBox.setMargin(buttons, new Insets(5.0,5.0,5.0,5.0));
	}
	public void display() {
		showAndWait();
	}
	public GridPane getPane() {
		return pane;
	}
	public String getResult() {
		return result;
	}
}
