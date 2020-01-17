package com.moneydance.modules.features.jasperreports;


import java.io.File;
import java.io.InputStream;

import com.moneydance.modules.features.mrbutil.MRBDebug;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FirstRun {
    private final Parameters params;
    private DirectoryChooser directoryChooser;
    private int ix=0;
    private int iy=0;
    private Stage stage;
    private Scene scene;
    private GridPane pane;
    private int maxWidth = 40;
    private TextField dataDirName;
    private TextField reportDirName;

    public FirstRun(JFXPanel frame, Parameters paramsp)
    {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new GridPane();
		scene = new Scene(pane,maxWidth*14,150);
		stage.setScene(scene);
		params = paramsp;
    	Main.debugInst.debug("FirstRun", "FirstRun", MRBDebug.SUMMARY, "started ");
		Label dataDirLbl = new Label("Data Directory : ");
		pane.add(dataDirLbl,ix++,iy);
		dataDirLbl.setPadding(new Insets(10, 10, 10, 10));
		dataDirName = new TextField();
		dataDirName.setText(params.getDataDirectory());
		dataDirName.setTooltip(new Tooltip("Enter the folder name, or click on button to the right to find the folder"));
		dataDirName.setMinWidth(maxWidth*7);
		dataDirName.setPrefWidth(maxWidth*7);
		dataDirName.setMaxWidth(400);
		dataDirName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int width = dataDirName.getText().length();
				if (width > maxWidth) {
					maxWidth = width;
					dataDirName.setPrefWidth(maxWidth*7);
					reportDirName.setPrefWidth(maxWidth*7);
				}
				
			}
		});
		
		pane.add(dataDirName,ix,iy);
		GridPane.setMargin(dataDirName,new Insets(10, 10, 10, 10));

		Button dataChoose = new Button();
		InputStream stream = getClass().getResourceAsStream(Constants.RESOURCES+"Search-Folder-icon.jpg");
		Image img=null;
		if (stream == null)
			dataChoose.setText("Find");
		else {
			img = new Image(stream);
			dataChoose.setGraphic(new ImageView(img));
		}
		ix+=3;
		pane.add(dataChoose,ix, iy++);
		GridPane.setMargin(dataChoose,new Insets(10, 10, 10, 10));
		dataChoose.setTooltip(new Tooltip("Click to open file dialog to find required folder"));
		dataChoose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String directory = chooseFile();
				dataDirName.setText(directory);
			}
		});  
		ix=0;
		Label reportDirLbl = new Label("Report Directory : ");
		pane.add(reportDirLbl,ix++,iy);
		GridPane.setMargin(reportDirLbl,new Insets(10, 10, 10, 10));
		reportDirName = new TextField();
		reportDirName.setText(params.getReportDirectory());
		reportDirName.setTooltip(new Tooltip("Enter the folder name, or click on button to the right to find the folder"));
		reportDirName.setMinWidth(maxWidth*7);
		reportDirName.setPrefWidth(maxWidth*7);
		reportDirName.setMaxWidth(400);
		reportDirName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int width = reportDirName.getText().length();
				if (width > maxWidth) {
					maxWidth = width;
					dataDirName.setPrefWidth(maxWidth*7);
					reportDirName.setPrefWidth(maxWidth*7);
				}
				
			}
		});
		pane.add(reportDirName,ix,iy);
		GridPane.setMargin(reportDirName,new Insets(10, 10, 10, 10));
		Button reportChoose = new Button();
		if (img == null)
			reportChoose.setText("Find");
		else
			reportChoose.setGraphic(new ImageView(img));
		ix+=3;
		pane.add(reportChoose, ix, iy++);
		GridPane.setMargin(reportChoose,new Insets(10, 10, 10, 10));
		reportChoose.setTooltip(new Tooltip("Click to open file dialog to find required folder"));
		reportChoose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String directory = chooseFile();
				reportDirName.setText(directory);
			}
		}); 
        Button okButton = new Button("Ok");
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (dataDirName.getText().isEmpty() ||
						reportDirName.getText().isEmpty()) {
					Alert alert = new Alert(AlertType.ERROR,"Please set the directories");
					alert.showAndWait();
					return;
				}
				params.setReportDirectory(reportDirName.getText());
				params.setDataDirectory(dataDirName.getText());
				params.setDataTemplates();
				params.setReportTemplates();
				stage.close();
			}
		});	
        Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				stage.close();
			}
		});	
		ix = 0;
        pane.add(okButton,ix++,iy);
        GridPane.setMargin(okButton,new Insets(10,10,10,10));
        pane.add(cancelButton,ix,iy);
        GridPane.setMargin(cancelButton,new Insets(10,10,10,10));
		stage.showAndWait();
    }
     
	/*
	 * Select a file
	 */
	private String chooseFile() {
		String strDirectory = System.clearProperty("user.home");
		directoryChooser = new DirectoryChooser();
		if (!(strDirectory == "" || strDirectory == null)) {
			directoryChooser.setInitialDirectory(new File(strDirectory));
		}
		File directory = directoryChooser.showDialog(stage);
		if (directory == null)
			strDirectory = null;
		else
			strDirectory = directory.getAbsolutePath();
		return strDirectory;
	}

}
