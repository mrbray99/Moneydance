package com.moneydance.modules.features.reportwriter;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.Platform;
import com.moneydance.modules.features.reportwriter.samples.HttpDownloadUtility;
import com.moneydance.modules.features.reportwriter.view.MyReport;
import com.moneydance.modules.features.reportwriter.samples.DownloadException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FirstRun {
    private final Parameters params;
    private MyReport myReport;
    private DirectoryChooser directoryChooser;
    private int ix=0;
    private int iy=0;
    private Stage stage;
    private Scene scene;
    private GridPane pane;
    private int maxWidth = 40;
    private TextField dataDirName;
    private TextField outputDirName;
    private TextField reportDirName;
	private ZipFile zipFile;
	private String fileName;
	private String samplesFile;
	private String tempDirName;
	private Label progressLbl;
	private HBox samplesBox;
   public FirstRun( MyReport myReport, Parameters params)
    {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new GridPane();
		scene = new Scene(pane);
		Main.accels.setSceneSave(scene, new Runnable () {
			@Override
			public void run() {
				saveData();
			}
		});
		
		Main.accels.setSceneClose(scene, new Runnable () {
			@Override
			public void run() {
				stage.close();
			}
		});
		stage.setScene(scene);
		stage.setTitle("Parameter Settings");
		this.params = params;
		this.myReport = myReport;
    	Main.rwDebugInst.debug("FirstRun", "FirstRun", MRBDebug.SUMMARY, "started ");
		Label dataDirLbl = new Label("Parameters Folder : ");
		pane.add(dataDirLbl,ix++,iy);
		dataDirLbl.setPadding(new Insets(10, 10, 10, 10));
		dataDirName = new TextField();
		dataDirName.setText(params.getDataDirectory());
		dataDirName.setTooltip(new Tooltip("Enter the folder name, or click on button to the right to find the folder"));
		dataDirName.setMinWidth(maxWidth*7);
		dataDirName.setPrefWidth(maxWidth*7);
		dataDirName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int width = dataDirName.getText()==null?0:dataDirName.getText().length();
				if (width > maxWidth) {
					maxWidth = width;
					dataDirName.setPrefWidth(maxWidth*7);
					outputDirName.setPrefWidth(maxWidth*7);
					reportDirName.setPrefWidth(maxWidth*7);
				}
			}
		});
		GridPane.setHgrow(dataDirName, Priority.ALWAYS);
		pane.add(dataDirName,ix,iy);
		GridPane.setMargin(dataDirName,new Insets(10, 10, 10, 10));

		Button dataChoose = new Button();
		if (Main.loadedIcons.searchImg == null)
			dataChoose.setText("Search Folder");
		else
			dataChoose.setGraphic(new ImageView(Main.loadedIcons.searchImg));
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
		Label outputDirLbl = new Label("Data Output Folder : ");
		pane.add(outputDirLbl,ix++,iy);
		outputDirLbl.setPadding(new Insets(10, 10, 10, 10));
		outputDirName = new TextField();
		outputDirName.setText(params.getOutputDirectory());
		outputDirName.setTooltip(new Tooltip("Enter the folder name, or click on button to the right to find the folder"));
		outputDirName.setMinWidth(maxWidth*7);
		outputDirName.setPrefWidth(maxWidth*7);
		outputDirName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int width =outputDirName.getText()==null?0:outputDirName.getText().length();
				if (width > maxWidth) {
					maxWidth = width;
					dataDirName.setPrefWidth(maxWidth*7);
					outputDirName.setPrefWidth(maxWidth*7);
					reportDirName.setPrefWidth(maxWidth*7);
				}
				
			}
		});
		GridPane.setHgrow(outputDirName, Priority.ALWAYS);
		
		pane.add(outputDirName,ix,iy);
		GridPane.setMargin(outputDirName,new Insets(10, 10, 10, 10));

		Button outputChoose = new Button();
		if (Main.loadedIcons.searchImg == null)
			outputChoose.setText("Search Folder");
		else
			outputChoose.setGraphic(new ImageView(Main.loadedIcons.searchImg));
		ix+=3;
		pane.add(outputChoose,ix, iy++);
		GridPane.setMargin(outputChoose,new Insets(10, 10, 10, 10));
		outputChoose.setTooltip(new Tooltip("Click to open file dialog to find required folder"));
		outputChoose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String directory = chooseFile();
				outputDirName.setText(directory);
			}
		});  
		ix=0;
		Label reportDirLbl = new Label("Report Template Folder : ");
		pane.add(reportDirLbl,ix++,iy);
		GridPane.setMargin(reportDirLbl,new Insets(10, 10, 10, 10));
		reportDirName = new TextField();
		reportDirName.setText(params.getReportDirectory());
		reportDirName.setTooltip(new Tooltip("Enter the folder name, or click on button to the right to find the folder"));
		reportDirName.setMinWidth(maxWidth*7);
		reportDirName.setPrefWidth(maxWidth*7);
		reportDirName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int width = reportDirName.getText()==null?0:reportDirName.getText().length();
				if (width > maxWidth) {
					maxWidth = width;
					dataDirName.setPrefWidth(maxWidth*7);
					outputDirName.setPrefWidth(maxWidth*7);
					reportDirName.setPrefWidth(maxWidth*7);
				}
				
			}
		});
		GridPane.setHgrow(reportDirName, Priority.ALWAYS);
		pane.add(reportDirName,ix,iy);
		GridPane.setMargin(reportDirName,new Insets(10, 10, 10, 10));
		Button reportChoose = new Button();
		if (Main.loadedIcons.searchImg == null)
			reportChoose.setText("Search Folder");
		else
			reportChoose.setGraphic(new ImageView(Main.loadedIcons.searchImg));
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
		samplesBox = new HBox();
		Label sampleLbl = new Label("Download Samples");
		GridPane.setMargin(sampleLbl,new Insets(10, 10, 10, 10));
		Button sampleBtn = new Button();
		if (Main.loadedIcons.downloadImg == null)
			sampleBtn.setText("Download Samples");
		else
			sampleBtn.setGraphic(new ImageView(Main.loadedIcons.downloadImg));
		sampleBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (dataDirName.getText()==null ||
						reportDirName.getText()==null ||
						dataDirName.getText().isEmpty() ||
						reportDirName.getText().isEmpty()) {
					Alert alert = new Alert(AlertType.ERROR,"Please set the directories");
					alert.showAndWait();
					return;
				}
				try {
					downloadSamples();
				}
				catch (DownloadException e2) {
					
				}
			}
		});	
		progressLbl = new Label();
		samplesBox.getChildren().addAll(sampleBtn,progressLbl);
		samplesBox.setSpacing(10);
		ix=0;
		pane.add(sampleLbl, ix++, iy);
		pane.add(samplesBox, ix, iy++);
        Button okBtn = new Button();
		if (Main.loadedIcons.okImg == null)
			okBtn.setText("Ok");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.okImg));
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				saveData();
			}
		});	
        Button cancelBtn = new Button();
		if (Main.loadedIcons.cancelImg == null)
			cancelBtn.setText("Cancel");
		else
			cancelBtn.setGraphic(new ImageView(Main.loadedIcons.cancelImg));
		cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				stage.close();
			}
		});	
		ix = 0;
        pane.add(okBtn,ix++,iy);
        GridPane.setMargin(okBtn,new Insets(10,10,10,10));
        pane.add(cancelBtn,ix,iy);
        GridPane.setMargin(cancelBtn,new Insets(10,10,10,10));
		stage.showAndWait();
    }
   private void saveData() {
		if (dataDirName.getText() == null || 
				dataDirName.getText().isEmpty() ||
				reportDirName.getText() == null ||
				reportDirName.getText().isEmpty() ||
				outputDirName.getText() == null ||
				outputDirName.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR,"Please set the directories");
			alert.showAndWait();
			return;
		}
		params.setReportDirectory(reportDirName.getText());
		params.setOutputDirectory(outputDirName.getText());
		params.setDataDirectory(dataDirName.getText());
		params.setDataTemplates();
		params.setReportTemplates();
		try {
			myReport.createAdapter(params.getReportDirectory());
			myReport.copyDatabaseFiles(params.getReportDirectory());
		}
		catch (DownloadException | RWException e2) {
			Main.rwDebugInst.debug("FirstRun", "FirstRun", MRBDebug.INFO, "IO Error downloading files");
			Alert alert = new Alert(AlertType.ERROR,"Error downloading files "+e2.getLocalizedMessage());
			alert.showAndWait();
		}
		stage.close();
   }
   private void downloadSamples() throws DownloadException{
	   String zipFileName;
  		tempDirName = System.getProperty("java.io.tmpdir");
  		samplesFile = Constants.REPOSITORY+Constants.SAMPLESFILE;
		if (Platform.isFreeBSD()|| Platform.isUnix())
			tempDirName +="/";
		Main.rwDebugInst.debug("FirstRun", "downloadSamples", MRBDebug.SUMMARY, "Downloading "+fileName);
	   try {
   			zipFileName = HttpDownloadUtility.downloadFile(samplesFile, tempDirName);
   			Main.rwDebugInst.debug("FirstRun", "downloadSamples", MRBDebug.SUMMARY, "File "+fileName+" downloaded");
   			progressLbl.setText("Samples file downloaded");
  			} 
	   catch (DownloadException e) {
			Main.rwDebugInst.debug("FirstRun", "downloadSamples", MRBDebug.INFO, "Error downloading "+fileName);
			throw e;
		} 
	    catch (IOException e) {
			Main.rwDebugInst.debug("FirstRun", "downloadSamples", MRBDebug.INFO, "IO Error downloading "+fileName);
			throw new DownloadException(e.getLocalizedMessage());
 		}
	   try {
		   loadZipFile(zipFileName);
 			progressLbl.setText("Sample files extracted");
	   }
	   catch (DownloadException e2) {
		   throw new DownloadException (e2.getLocalizedMessage());
	   }
   }	
	private void loadZipFile(String zipFileName) throws DownloadException {
		File foundFile = new File(zipFileName);
		if (!foundFile.exists()) {
			Main.rwDebugInst.debug("FirstRun", "loadZipFile", MRBDebug.INFO, "Zip file does not exist "+fileName);
			throw new DownloadException("Can not open zip file "+zipFileName);
		}
		try {
			Main.rwDebugInst.debug("FirstRun", "loadZipFile", MRBDebug.SUMMARY, "Opening zip file "+fileName);
			zipFile = new ZipFile(foundFile);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			List<? extends ZipEntry> listIt = Collections.list(entries);
			boolean found=false; 
			for (ZipEntry entry :listIt) {
				String fileName = entry.getName();
				String extension = "."+FilenameUtils.getExtension(fileName);
				String directory="";
				if (extension.contentEquals(Constants.DATAEXTENSION))
					directory = dataDirName.getText();
				else if (extension.contentEquals(Constants.SELEXTENSION))
						directory = dataDirName.getText();
				else if (extension.contentEquals(Constants.REPORTEXTENSION))
						directory = dataDirName.getText();
				else if (extension.contentEquals(Constants.TEMPLATEEXTENSION))
					directory = reportDirName.getText();
				if (!directory.isEmpty()) {
					File testFile = new File(directory + "/"+fileName);
					if (testFile.exists())
						found=true;
				}
			}
			if (found) {
				Alert askOverwrite = new Alert(AlertType.CONFIRMATION,"Sample files already exist.  Do you wish to Overwrite?",ButtonType.YES,ButtonType.NO);
				askOverwrite.showAndWait();
				if (askOverwrite.getResult() == ButtonType.NO)
					return;

			}
			for (ZipEntry entry :listIt) {
				copyZipFileEntry(entry);
			}
		}
		catch (IOException e) {
			Main.rwDebugInst.debug("FirstRun", "loadZipFile", MRBDebug.INFO, "Error opening "+fileName+" "+e.getMessage());
			throw new DownloadException(e.getMessage());
		}
		try {
			zipFile.close();
		}
		catch (IOException e) {
			Main.rwDebugInst.debug("FirstRun", "loadZipFile", MRBDebug.INFO, "Error closing "+fileName+" "+e.getMessage());
			return;
		}

	}


	private void copyZipFileEntry(ZipEntry zipEntry) throws DownloadException{
		Main.rwDebugInst.debug("FirstRun", "copyZipFileEntry", MRBDebug.DETAILED, "Copying file "+zipEntry.getName());
		String extName = new File(zipEntry.getName()).getName();
		String extension = "."+FilenameUtils.getExtension(extName);
		String directory="";
		if (extension.contentEquals(Constants.DATAEXTENSION))
			directory = dataDirName.getText();
		else if (extension.contentEquals(Constants.SELEXTENSION))
				directory = dataDirName.getText();
		else if (extension.contentEquals(Constants.REPORTEXTENSION))
			directory = dataDirName.getText();
		else if (extension.contentEquals(Constants.TEMPLATEEXTENSION))
			directory = reportDirName.getText();
		if (directory.isEmpty())
			return;
		String outFile = directory+"/"+extName;
		FileOutputStream outStream;
		InputStream inStream;
		byte[] buffer = new byte[1024];
		int noOfBytes=0;
		try {
			outStream = new FileOutputStream(outFile);
			try {
				inStream = zipFile.getInputStream(zipEntry);
				while ((noOfBytes = inStream.read(buffer)) !=-1) {
					outStream.write(buffer, 0, noOfBytes);
				}
				outStream.close();
				inStream.close();
				Main.rwDebugInst.debug("CopyFile", "copyZipFileEntry", MRBDebug.SUMMARY, "File "+extName+" extracted");
				progressLbl.setText("File "+extName+" extracted");
			}
			catch (IOException e) {
				Main.rwDebugInst.debug("CopyFile", "copyZipFileEntry", MRBDebug.INFO, "Error extracting "+extName+" "+e.getMessage());
				progressLbl.setText("Error extracting "+extName);
				throw new DownloadException(e.getMessage());
			}
		}
		catch (FileNotFoundException e) {
			Main.rwDebugInst.debug("CopyFile", "copyZipFileEntry", MRBDebug.INFO, "Zip entry not found "+extName+" "+e.getMessage());
			progressLbl.setText("Zip entry not found "+extName);
			throw new DownloadException(e.getMessage());
		}

	}
	private void copyDatabaseFiles() {
		Main.rwDebugInst.debug("FirstRun", "copyDatabaseFiles", MRBDebug.DETAILED, "Copying empty database");
		String outFile = reportDirName.getText()+"/Moneydance.mv.db";

		try {
			java.io.InputStream in = 
					getClass().getResourceAsStream(Constants.RESOURCES+Constants.JASPERDATABASE);
			byte[] buffer= new byte[in.available()];
			in.read(buffer);
			File outputFile = new File(outFile);
			OutputStream outStream = new FileOutputStream(outputFile);
			outStream.write(buffer);
			outStream.close();
		} catch (Throwable e) { 
			e.printStackTrace();
			throw new DownloadException("Error creating Database Adapter");
		}
		
	}


	/*
	 * Select a file
	 */
	private String chooseFile() {
		String strDirectory = Main.preferences.getString(Constants.FIRSTRUNDIR,System.getProperty("user.home"));
		directoryChooser = new DirectoryChooser();
		if (!(strDirectory == null || strDirectory.isEmpty() )) {
			try {
				directoryChooser.setInitialDirectory(new File(strDirectory));
			}
			catch (Exception e) {}
		}
		File directory = directoryChooser.showDialog(stage);
		if (directory == null)
			strDirectory = null;
		else
			strDirectory = directory.getAbsolutePath();
		Main.preferences.put(Constants.FIRSTRUNDIR, strDirectory);
		return strDirectory;
	}

}
