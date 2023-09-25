package com.moneydance.modules.features.reportwriter2.view;

import java.io.IOException;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportBanner;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportLayout;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.LayoutPaneController;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.TemplateDataPaneController;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class LayoutPane extends ScreenPanel implements Callback<String,Integer>{
	public static ReportLayout fieldBeingMoved;
	public static ReportField itemBeingDragged;
	public static ReportBanner bannerBeingResized;
	public static ReportBanner nextBannerBeingResized;
	private FXMLLoader loader;
	private LayoutPaneController controller;
	private SplitPane layoutBox;
	private Scene scene;
	private Stage stage;
	private ReportTemplate template;
	private TemplateDataPaneController callingPane;
	public LayoutPane(ReportTemplate template,TemplateDataPaneController callingPane) {
		super();
		this.template = template;
		this.callingPane = callingPane;
		try {
			loader = new FXMLLoader(getClass().getResource(Constants.RESOURCES+Constants.LAYOUTPANE));
			controller = new LayoutPaneController();
			loader.setController(controller);
			layoutBox = loader.load();
			layoutBox.requestFocus();
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		controller.setFields(this.template,this);
		callingPane.addFieldListener(controller);
		scene = new Scene(layoutBox);
		scene.getStylesheets().add("file:///"+Main.extensionDir.replace("\\","/") +"/"+ Constants.STYLESHEET);
		layoutBox.prefWidthProperty().bind(scene.widthProperty());
		layoutBox.prefHeightProperty().bind(scene.heightProperty());
		stage = new Stage();
		stage.setScene(scene);
		boolean max = Main.preferences.getBoolean(Constants.PROGRAMNAME+"."+Constants.LAYOUTMAXIMISE,true);
		if (max) 
			stage.setMaximized(true);
		else {
			stage.setWidth( Main.preferences.getDouble(Constants.PROGRAMNAME+"."+Constants.LAYOUTWIDTH,Constants.MINLAYOUTWIDTH));
			stage.setHeight( Main.preferences.getDouble(Constants.PROGRAMNAME+"."+Constants.LAYOUTHEIGHT,Constants.MINLAYOUTHEIGHT));
		}
		stage.maximizedProperty().addListener((ov, oldv, newv)->{
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.LAYOUTMAXIMISE,(boolean)newv);
			Main.rwDebugInst.debugThread("LayoutPane", "maximisedProperty", MRBDebug.DETAILED, "Layout maximised "+newv);
			Main.preferences.isDirty();
			if (!newv) {
				stage.setWidth( Main.preferences.getDouble(Constants.PROGRAMNAME+"."+Constants.LAYOUTWIDTH,Constants.MINLAYOUTWIDTH));
				stage.setHeight( Main.preferences.getDouble(Constants.PROGRAMNAME+"."+Constants.LAYOUTHEIGHT,Constants.MINLAYOUTHEIGHT));
			}
		});
		stage.widthProperty().addListener((ov, oldv, newv)->{
			if (!stage.isMaximized()) {
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.LAYOUTWIDTH,(double)newv);			
				Main.rwDebugInst.debugThread("LayoutPane", "widthProperty", MRBDebug.DETAILED, "Layout width "+newv);
				Main.preferences.isDirty();
			}
		});
		stage.heightProperty().addListener((ov, oldv, newv)->{
			if (!stage.isMaximized()) {
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.LAYOUTHEIGHT,(double)newv);			
				Main.rwDebugInst.debugThread("LayoutPane", "heightProperty", MRBDebug.DETAILED, "Layout height"+newv);
				Main.preferences.isDirty();
			}
		});
		/**
		 * used to control multiple field selection and moving pane divider
		 */
		 scene.addEventFilter(KeyEvent.KEY_PRESSED,event->{
				Main.rwDebugInst.debugThread("LayoutPane", "setOnKeyPressed", MRBDebug.DETAILED, "key press detected "+event.getCode().getName());
			   switch (event.getCode()) {
			   		case UP:
			   			moveSelected(event,0,-Constants.LAYOUTDIVIDER);
			   			break;
			   		case DOWN:
			   			moveSelected(event,0,+Constants.LAYOUTDIVIDER);
			   			break;
			   		case RIGHT:
			   			moveSelected(event,+Constants.LAYOUTDIVIDER,0);
			   			break;
			   		case LEFT:
			   			moveSelected(event,-Constants.LAYOUTDIVIDER,0);
			   			break;   		
			   		case CONTROL:
			   			controller.setSelectingMany(true); // rule 1, action A1
			   		default:
			   			break;
			   }
		   });
		 scene.addEventFilter(KeyEvent.KEY_RELEASED,event->{
				Main.rwDebugInst.debugThread("LayoutPane", "setOnKeyReleased", MRBDebug.DETAILED, "key release detected "+event.getCode().getName());
			   switch (event.getCode()) {
			   		case CONTROL:
			   			controller.setSelectingMany(false); // rule 2, action A2
			   		default:
			   			break;
			   }
		   });
		 KeyCombination kc = new KeyCodeCombination(KeyCode.Z,KeyCombination.SHORTCUT_DOWN);
		 scene.addEventFilter(KeyEvent.KEY_PRESSED,event ->{
			 if (kc.match(event)) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							Main.rwDebugInst.debugThread("LayoutPane", "LayoutPane", MRBDebug.DETAILED, "ctrl-z detected ");
							controller.undo();
						}
					});
					event.consume();
			 }
		 });
		 KeyCombination kc2 = new KeyCodeCombination(KeyCode.Y,KeyCombination.SHORTCUT_DOWN);
		 scene.addEventFilter(KeyEvent.KEY_PRESSED,event ->{
			 if (kc2.match(event)) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							Main.rwDebugInst.debugThread("LayoutPane", "LayoutPane", MRBDebug.DETAILED, "ctrl-y detected ");
							controller.redo();
						}
					});
				event.consume();
			 }
		 });

		 stage.show();		
		 controller.resizeBanners();
	}
	private void moveSelected(KeyEvent event,int xmove, int ymove) {
		Main.rwDebugInst.debugThread("LayoutPane", "moveSelected", MRBDebug.DETAILED, "moving field ");
		controller.moveSelected(event, xmove,  ymove);
	}
	public void notifyFieldPane() {
		if (controller != null)
			callingPane.addFieldListener(controller);
	}
	@Override
	public Integer call(String arg0) {
		callingPane.removeFieldListener(controller);
		stage.close();
		callingPane.closeLayoutScreen();
		return null;
	}
	
}
