package com.moneydance.modules.features.reportwriter.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;

import com.infinitekind.util.DateUtil;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.Platform;
import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;
import com.moneydance.modules.features.reportwriter.OptionMessage;
import com.moneydance.modules.features.reportwriter.Parameters;
import com.moneydance.modules.features.reportwriter.Utilities;
import com.moneydance.modules.features.reportwriter.samples.DownloadException;
import com.moneydance.modules.features.reportwriter.samples.HttpDownloadUtility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TemplateDataPane extends ScreenDataPane {
	/*
	 * Download Template window
	 */
	private List<TemplateListRow> templateList;
	private ObservableList<TemplateListRow> templates;
	private TableView<TemplateListRow> templateTable;
	private String templateListFile;
	private Scene scene;
	private GridPane pane;
	private ZipFile zipFile;
	private String fileName;
	private String samplesFile;
	private String tempDirName;
	private SortedMap<String,Integer>selectedTemplates;
	private Parameters params;
	public TemplateDataPane(Parameters params) {
		super();
		screenName = "TemplateDataPane";
		screenTitle = "Template Download Screen";
		this.params = params;
	}
	public void displayPanel() {
		setStage(new Stage());
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new MyGridPane(Constants.WINTEMPLATEDATA);
		scene = new Scene(pane);
		stage.setScene(scene);
		stage.setOnCloseRequest(ev->{
			stage.close();
		});
		Main.accels.setSceneSave(scene, new Runnable () {
			@Override
			public void run() {
				selectTemplates();
				stage.close();
				}
		});
		Main.accels.setSceneClose(scene, new Runnable () {
			@Override
			public void run() {
				stage.close();
			}
		});
		resize();
		String listFileName;
		tempDirName = System.getProperty("java.io.tmpdir");		
		templateListFile = Constants.REPOSITORY + Constants.TEMPLATELISTFILE;
		if (Platform.isFreeBSD() || Platform.isUnix())
			tempDirName += "/";
		Main.rwDebugInst.debug("TemplatePane", "downloadTemplates", MRBDebug.SUMMARY, "Downloading " + fileName);
		try {
			listFileName = HttpDownloadUtility.downloadFile(templateListFile, tempDirName);
			Main.rwDebugInst.debug("TemplatePane", "downloadTemplates", MRBDebug.SUMMARY, "File " + fileName + " downloaded");
		} catch (DownloadException e) {
			Main.rwDebugInst.debug("TemplatePane", "downloadTemplates", MRBDebug.INFO, "Error downloading " + fileName);
			throw e;
		} catch (IOException e) {
			Main.rwDebugInst.debug("TemplatePane", "downloadTemplates", MRBDebug.INFO, "IO Error downloading " + fileName);
			throw new DownloadException(e.getLocalizedMessage());
		}
		File listFile = new File(listFileName);
		if (!listFile.exists()) {
			throw new DownloadException("Template List not found");
		}
		BufferedReader br=null;
		String everything=null;
		try {
			br = new BufferedReader(new FileReader(listFile));
		} catch (FileNotFoundException e1) {
			throw new DownloadException("Error reading Template List");
		}
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
		}
		catch (Exception e) {
			throw new DownloadException("Error reading Template List");
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
				throw new DownloadException("Error reading Template List");
			}
		}
		String [] allLines = everything.split(System.lineSeparator());
		templateList= new ArrayList<TemplateListRow>();
		for (int i=0;i<allLines.length;i++) {
			TemplateListRow newRow = new TemplateListRow(allLines[i]);
			String oldFile = checkFilePresent(newRow.getName());
			if (oldFile.isEmpty()) {
				newRow.setPresent(false);
				newRow.setLastDate("");
			}
			else {
				newRow.setPresent(true);
				newRow.setLastDate(oldFile);
			}
			templateList.add(newRow);
		}
		templates = FXCollections.observableArrayList(templateList);
		setUpTemplateTable();
		pane.add(templateTable, 0, 0);
		GridPane.setColumnSpan(templateTable, 3);
		Button downloadBtn = new Button();
		if (Main.loadedIcons.downloadImg == null)
			downloadBtn.setText("Download");
		else
			downloadBtn.setGraphic(new ImageView(Main.loadedIcons.downloadImg));
		downloadBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				selectTemplates();
			}
		});
		pane.add(downloadBtn,0,1);
		Button okBtn = new Button();
		if (Main.loadedIcons.okImg == null)
			okBtn.setText("OK");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.okImg));
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				stage.close();
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
		pane.add(okBtn, 0, 2);	
		pane.add(cancelBtn, 1, 2);
		GridPane.setMargin(okBtn,new Insets(10,10,10,10));
		GridPane.setMargin(downloadBtn,new Insets(10,10,10,10));
		GridPane.setMargin(cancelBtn,new Insets(10,10,10,10));
		GridPane.setHgrow(templateTable, Priority.ALWAYS);
		widthChanged();
		stage.showAndWait();
	}
	public void widthChanged() {
		if ( templateTable != null)
			templateTable.setPrefWidth(SCREENWIDTH);
	}
	private String checkFilePresent(String name) {
		String oldDate = "";
		File oldFile = new File(params.getReportDirectory()+"/"+name+Constants.TEMPLATEEXTENSION);
		if (oldFile.exists()) {
			BasicFileAttributes atts;
			try {
				atts = Files.readAttributes(oldFile.toPath(), BasicFileAttributes.class);
				oldDate = Main.cdate.format(new Date(atts.creationTime().to(TimeUnit.MILLISECONDS)));
			}
			catch (IOException e) {
			}
		}
		return oldDate;
	}
	private void setUpTemplateTable() {
		templateTable = new TableView<>();
		templateTable.setEditable(true);
		templateTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		templateTable.setMaxWidth(Double.MAX_VALUE);
		templateTable.setMaxHeight(Double.MAX_VALUE);
		/*
		 * Columns
		 */
		TableColumn<TemplateListRow, CheckBox> select = new TableColumn<>("Select");
		TableColumn<TemplateListRow, String> name = new TableColumn<>("Name");
		TableColumn<TemplateListRow, String> present = new TableColumn<>("Exists");
		TableColumn<TemplateListRow, String> newDate = new TableColumn<>("New Date");
		TableColumn<TemplateListRow, String> oldDate = new TableColumn<>("Last Download Date");
		TableColumn<TemplateListRow, String> description = new TableColumn<>("Description");
		TableColumn<TemplateListRow, VBox> records = new TableColumn<>("Required Records");
		TableColumn<TemplateListRow, String> who = new TableColumn<>("Author");
		TableColumn<TemplateListRow, VBox> target = new TableColumn<>("Target Platforms");
		templateTable.getColumns().addAll(select,name, present,newDate,oldDate,description,records,who,target);
		templateTable.setItems(templates);
		select.setCellValueFactory(new PropertyValueFactory<>("select"));
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		present.setCellValueFactory(new PropertyValueFactory<>("present"));
		newDate.setCellValueFactory(new PropertyValueFactory<>("dateString"));
		oldDate.setCellValueFactory(new PropertyValueFactory<>("lastDate"));
		description.setCellValueFactory(new PropertyValueFactory<>("description"));
		records.setCellValueFactory(new PropertyValueFactory<>("recordsColumn"));
		who.setCellValueFactory(new PropertyValueFactory<>("who"));
		target.setCellValueFactory(new PropertyValueFactory<>("targetColumn"));
	}
	private void selectTemplates() {
		int rowCount = 0;
		selectedTemplates = new TreeMap<String,Integer>();
		for (int i=0;i<templates.size();i++) {
			TemplateListRow row = templates.get(i);
			if (row.isSelect()) {
				rowCount++;
				selectedTemplates.put(row.getName(),i);
				for (String graphic:row.getGraphicList()) {
					selectedTemplates.put(graphic, -1);
				}
			}
		}
		String zipFileName;
		if (rowCount < 1) return;
		try {
			zipFileName = downloadTemplates ();
		}
		catch (DownloadException e) {
			OptionMessage.displayMessage("Error downloading templates - "+e.getLocalizedMessage());  
			return;
		}
	}

	private String downloadTemplates() throws DownloadException {
		String zipFileName;
		tempDirName = System.getProperty("java.io.tmpdir");
		samplesFile = Constants.REPOSITORY + Constants.TEMPLATESFILE;
		if (Platform.isFreeBSD() || Platform.isUnix())
			tempDirName += "/";
		Main.rwDebugInst.debug("TemplateDataPane", "downloadSamples", MRBDebug.SUMMARY, "Downloading " + fileName);
		try {
			zipFileName = HttpDownloadUtility.downloadFile(samplesFile, tempDirName);
			Main.rwDebugInst.debug("TemplateDataPane", "downloadSamples", MRBDebug.SUMMARY, "File " + fileName + " downloaded");
		} catch (DownloadException e) {
			Main.rwDebugInst.debug("TemplateDataPane", "downloadSamples", MRBDebug.INFO, "Error downloading " + fileName);
			throw e;
		} catch (IOException e) {
			Main.rwDebugInst.debug("TemplateDataPane", "downloadSamples", MRBDebug.INFO, "IO Error downloading " + fileName);
			throw new DownloadException(e.getLocalizedMessage());
		}
		try {
			loadZipFile(zipFileName);
			templateTable.setItems(templates);
			templateTable.refresh();
		} catch (DownloadException e2) {
			throw new DownloadException(e2.getLocalizedMessage());
		}
		return zipFileName;
	}
	private void loadZipFile(String zipFileName) throws DownloadException {
		Boolean filesFound = false;
		File foundFile = new File(zipFileName);
		if (!foundFile.exists()) {
			Main.rwDebugInst.debug("TemplateDataPane", "loadZipFile", MRBDebug.INFO, "Zip file does not exist " + fileName);
			throw new DownloadException("Can not open zip file " + zipFileName);
		}
		try {
			Main.rwDebugInst.debug("TemplateDataPane", "loadZipFile", MRBDebug.SUMMARY, "Opening zip file " + fileName);
			zipFile = new ZipFile(foundFile);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			List<? extends ZipEntry> listIt = Collections.list(entries);
			for (ZipEntry entry : listIt) {
				String fileName = entry.getName();
				String name = FilenameUtils.getBaseName(fileName);
				String extension = "."+FilenameUtils.getExtension(fileName);
				if (extension.contentEquals(Constants.TEMPLATEEXTENSION)) {
					if (selectedTemplates.containsKey(name)) {
						copyZipFileEntry(entry);
						filesFound = true;
					}
				}
				else {
					if (selectedTemplates.containsKey(fileName)) {
						copyZipFileEntry(entry);
						filesFound = true;
					}
				}
			}
		} catch (IOException e) {
			Main.rwDebugInst.debug("TemplateDataPane", "loadZipFile", MRBDebug.INFO,
					"Error opening " + fileName + " " + e.getMessage());
			throw new DownloadException(e.getMessage());
		}
		try {
			zipFile.close();
		} catch (IOException e) {
			Main.rwDebugInst.debug("TemplateDataPane", "loadZipFile", MRBDebug.INFO,
					"Error closing " + fileName + " " + e.getMessage());
			return;
		}
		if (filesFound)
			OptionMessage.displayMessage("Templates and associated files downloaded");
	}

	private void copyZipFileEntry(ZipEntry zipEntry) throws DownloadException {
		Main.rwDebugInst.debug("TemplateDataPane", "copyZipFileEntry", MRBDebug.DETAILED, "Copying file " + zipEntry.getName());
		Utilities.notifyUser("Downloading "+zipEntry.getName());
		String extName = new File(zipEntry.getName()).getName();
		String directory = params.getReportDirectory();
		String outFile = directory + "/" + extName;
		FileOutputStream outStream;
		InputStream inStream;
		byte[] buffer = new byte[1024];
		int noOfBytes = 0;
		try {
			File testFile = new File(outFile);
			if (testFile.exists())
				testFile.delete();
			outStream = new FileOutputStream(outFile);
			try {
				inStream = zipFile.getInputStream(zipEntry);
				while ((noOfBytes = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, noOfBytes);
				}
				outStream.close();
				inStream.close();
				Main.rwDebugInst.debug("CopyFile", "copyZipFileEntry", MRBDebug.SUMMARY,
						"File " + extName + " extracted");
			} catch (IOException e) {
				Main.rwDebugInst.debug("CopyFile", "copyZipFileEntry", MRBDebug.INFO,
						"Error extracting " + extName + " " + e.getMessage());
				throw new DownloadException(e.getMessage());
			}
		} catch (FileNotFoundException e) {
			Main.rwDebugInst.debug("CopyFile", "copyZipFileEntry", MRBDebug.INFO,
					"Zip entry not found " + extName + " " + e.getMessage());
			throw new DownloadException(e.getMessage());
		}

	}
}
