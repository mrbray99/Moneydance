package com.moneydance.modules.features.reportwriter2;




import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.Parameters;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HelpScreen {
    private Stage stage;
    private Scene scene;
    private GridPane pane;
    private Button introScreen;
	private ToggleGroup group;
 	private RadioButton debugOff;
	private RadioButton debugInfo;
	private RadioButton debugSummary;
	private RadioButton debugDetailed;
	private Parameters thisParams;

    public HelpScreen(Parameters params) {
    	thisParams= params;
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new GridPane();
		scene = new Scene(pane);
		Main.accels.setSceneClose(scene, new Runnable () {
			@Override
			public void run() {
				stage.close();
			}
		});
		stage.setScene(scene);
		stage.setTitle("Help");
		Hyperlink link = new Hyperlink();
		link.setText("Show Help");
		link.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Main.context.showURL("moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.SHOWHELP);
					}
				});
			}
		});
		GridPane.setMargin(link,new Insets(10,10,10,10));
		pane.add(link,0,0);
		introScreen = new Button("Display Intro Screen");
		introScreen.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				thisParams.setIntroScreen(true);
				params.save();
				Alert alert = new Alert(AlertType.INFORMATION, "The intro screen will be shown the next time you start the extension");
				alert.showAndWait();
			}
		});		pane.add(introScreen, 0, 1);
		GridPane.setMargin(introScreen,new Insets(10,10,10,10));
		debugOff = new RadioButton ("Debug Off");
		debugInfo = new RadioButton ("Debug Info Messages Only");
		debugSummary = new RadioButton ("Debug Summary Message");
		debugDetailed = new RadioButton ("All Debug Messages");
		GridPane.setMargin(debugOff,new Insets(10,10,10,10));
		GridPane.setMargin(debugInfo,new Insets(10,10,10,10));
		GridPane.setMargin(debugSummary,new Insets(10,10,10,10));
		GridPane.setMargin(debugDetailed,new Insets(10,10,10,10));
		group = new ToggleGroup();
		switch (Main.rwDebugInst.getDebugLevel()) {
		case MRBDebug.OFF:
			debugOff.setSelected(true);
			break;
		case MRBDebug.INFO:
			debugInfo.setSelected(true);
			break;
		case MRBDebug.SUMMARY:
			debugSummary.setSelected(true);
			break;
		default :
			debugDetailed.setSelected(true);
		}
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
				RadioButton rb = (RadioButton) group.getSelectedToggle();
				if (rb == debugOff) { 
					Main.rwDebugInst.debug("HelpScreen","HelpScreen",MRBDebug.INFO,"Debug turned Off");
					Main.rwDebugInst.setDebugLevel(MRBDebug.OFF);;
				}
				else {
					if (rb == debugInfo) {
						Main.rwDebugInst.setDebugLevel(MRBDebug.INFO);
						Main.rwDebugInst.debug("HelpScreen","HelpScreen",MRBDebug.INFO,"Debug turned To Info");
					}
					else if (rb==debugSummary) {
						Main.rwDebugInst.setDebugLevel(MRBDebug.SUMMARY);
						Main.rwDebugInst.debug("HelpScreen","HelpScreen",MRBDebug.INFO,"Debug turned To Summary");
					}
					else { 
						Main.rwDebugInst.setDebugLevel(MRBDebug.DETAILED);
						Main.rwDebugInst.debug("HelpScreen","HelpScreen",MRBDebug.INFO,"Debug turned To Detailed");
					}
				}
				Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, Main.rwDebugInst.getDebugLevel());
				Main.preferences.isDirty();
			}
		});
		debugOff.setToggleGroup(group);
		debugInfo.setToggleGroup(group);
		debugSummary.setToggleGroup(group);
		debugDetailed.setToggleGroup(group);
		pane.add(debugOff, 0, 2);
		pane.add(debugInfo, 0, 3);
		pane.add(debugSummary, 0, 4);
		pane.add(debugDetailed, 0, 5);
		String about = Constants.ABOUT1+Constants.ABOUT2+Constants.ABOUT3+Main.buildNo+"."+Main.minorBuildNo+"\n\n"+Constants.ABOUT4+Constants.ABOUT5;
		Label aboutLbl = new Label(about);
		pane.add(aboutLbl,1,0);
		GridPane.setRowSpan(aboutLbl, 5);
		GridPane.setMargin(aboutLbl,new Insets(20,20,20,20));
 		stage.showAndWait();
    }

}
