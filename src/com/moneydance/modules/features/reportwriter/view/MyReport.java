/*
 * Copyright (c) 2020, Michael Bray.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - The name of the author may not used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.moneydance.modules.features.reportwriter.view;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBDirectoryUtils;
import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.FirstRun;
import com.moneydance.modules.features.reportwriter.Main;
import com.moneydance.modules.features.reportwriter.Parameters;
import com.moneydance.modules.features.reportwriter.RWException;
import com.moneydance.modules.features.reportwriter.samples.DownloadException;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class MyReport extends JFXPanel implements EventHandler<ActionEvent>{
	private Parameters params;
	/*
	 * Screen variables
	 */
	private Scene scene;
	private GridPane mainScreen;
	private TemplatePane templatePan;
	private SelectionPane selectionPan;
	private DataPane dataPan;
	private ReportPane reportPan;
	private MyReport thisObj;
	private Button closeBtn;
	private Button settingsBtn;
	private Button helpBtn;
	private HBox buttons;
	public int iFRAMEWIDTH;
	public int iFRAMEDEPTH;
	public int SCREENWIDTH = 0;
	public int SCREENHEIGHT = 0;

	public MyReport() {
		thisObj = this;
	}

	public Scene createScene() {
		mainScreen = new GridPane();
		scene = new Scene(mainScreen);
		Main.accels.setSceneClose(scene, new Runnable() {
			@Override
			public void run() {
				Main.rwDebugInst.debugThread("MyReport", "createScene", MRBDebug.DETAILED, "Accelerator close ");
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Main.extension.closeConsole();
					}
				});
			}
		});
		Main.accels.setSceneOpen(scene, new Runnable() {
			@Override
			public void run() {
				Main.rwDebugInst.debugThread("MyReport", "createScene", MRBDebug.DETAILED, "Accelerator open ");
				ScreenPanel crntPan = getFocus();
				if (crntPan != null)
					crntPan.openMsg();
			}
		});
		Main.accels.setSceneDelete(scene, new Runnable() {
			@Override
			public void run() {
				Main.rwDebugInst.debugThread("MyReport", "createScene", MRBDebug.DETAILED, "Accelerator delete ");
				ScreenPanel crntPan = getFocus();
				if (crntPan != null)
					crntPan.deleteMsg();
			}
		});
		Main.accels.setSceneNew(scene, new Runnable() {
			@Override
			public void run() {
				Main.rwDebugInst.debugThread("MyReport", "createScene", MRBDebug.DETAILED, "Accelerator new ");
				ScreenPanel crntPan = getFocus();
				if (crntPan != null)
					crntPan.newMsg();
			}
		});

		params = new Parameters();
		if (params.getDataDirectory() == null || params.getDataDirectory().equals(Constants.NODIRECTORY)
				|| params.getReportDirectory() == null || params.getReportDirectory().equals(Constants.NODIRECTORY)
				|| params.getOutputDirectory() == null || params.getOutputDirectory().equals(Constants.NODIRECTORY)) {
			new FirstRun(this, params);
			if (params.getDataDirectory().equals(Constants.NODIRECTORY)
					|| params.getReportDirectory().equals(Constants.NODIRECTORY)
					|| params.getOutputDirectory().equals(Constants.NODIRECTORY)) {
				Alert alert = new Alert(AlertType.ERROR, "Extension can not continue without setting the directories");
				alert.showAndWait();
			} else
				params.save();
			resetData();
		} else
			checkDefaultDb();
		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				Main.rwDebugInst.debugThread("MyReport", "createScene", MRBDebug.SUMMARY,
						"Component New size " + thisObj.getWidth() + "/" + thisObj.getHeight());
				Main.preferences.put(Constants.PROGRAMNAME + "." + Constants.CRNTFRAMEWIDTH, thisObj.getWidth());
				Main.preferences.put(Constants.PROGRAMNAME + "." + Constants.CRNTFRAMEHEIGHT, thisObj.getHeight());
				updatePreferences(thisObj.getWidth(), thisObj.getHeight());
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentShown(ComponentEvent e) {

			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		closeBtn = new Button();
		if (Main.loadedIcons.closeImg == null)
			closeBtn.setText("Exit");
		else
			closeBtn.setGraphic(new ImageView(Main.loadedIcons.closeImg));
		closeBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Main.extension.closeConsole();
					}
				});
			}
		});
		settingsBtn = new Button();
		if (Main.loadedIcons.settingsImg == null)
			settingsBtn.setText("Settings");
		else
			settingsBtn.setGraphic(new ImageView(Main.loadedIcons.settingsImg));
		settingsBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						new FirstRun(thisObj, params);
						if (params.getDataDirectory().equals(Constants.NODIRECTORY)) {
							Alert alert = new Alert(AlertType.ERROR,
									"Extension can not continue without setting the directories");
							alert.showAndWait();
						} else
							params.save();
						resetData();
					}
				});
			}
		});
		helpBtn = new Button();
		if (Main.loadedIcons.helpImg == null)
			helpBtn.setText("Help");
		else
			helpBtn.setGraphic(new ImageView(Main.loadedIcons.helpImg));
		helpBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						new HelpScreen();
					}
				});
			}
		});
		templatePan = new TemplatePane(params);
		dataPan = new DataPane(params);
		reportPan = new ReportPane(params);
		selectionPan = new SelectionPane(params);
		mainScreen.add(templatePan, 0, 0);
		GridPane.setMargin(templatePan, new Insets(10, 10, 10, 10));
		mainScreen.add(selectionPan, 1, 0);
		GridPane.setMargin(selectionPan, new Insets(10, 10, 10, 10));
		mainScreen.add(dataPan, 0, 1);
		GridPane.setMargin(dataPan, new Insets(10, 10, 10, 10));
		mainScreen.add(reportPan, 1, 1);
		GridPane.setMargin(reportPan, new Insets(10, 10, 10, 10));
		buttons = new HBox();
		buttons.getChildren().addAll(closeBtn, settingsBtn, helpBtn);
		buttons.setSpacing(10);
		mainScreen.add(buttons, 0, 2);
		GridPane.setMargin(buttons, new Insets(10, 10, 10, 10));
		String css = "";
		try {
			java.io.InputStream in = Main.loader.getResourceAsStream(Constants.RESOURCES + "datadatapane.css");
			if (in != null) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
				byte buf[] = new byte[256];
				int n = 0;
				while ((n = in.read(buf, 0, buf.length)) >= 0)
					bout.write(buf, 0, n);
				css = bout.toString();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			Main.rwDebugInst.debugThread("MyReport", "createScene", MRBDebug.SUMMARY,
					"css not found " + e.getLocalizedMessage());
			return scene;
		}
		Main.rwDebugInst.debugThread("MyReport", "createScene", MRBDebug.SUMMARY, "css " + css);
		mainScreen.setStyle(css);
		return scene;
	}

	/*
	 * check to see if default database has changed
	 */
	private void checkDefaultDb() {
		if (params.getReportDirectory().equals(Constants.NODIRECTORY))
			return;
		String defaultDBLoaded = Main.preferences.getString(Constants.PARMLASTDB, "00000000");
		if (defaultDBLoaded.compareTo(Main.databaseChanged) < 0) {
			try {
				createAdapter(params.getReportDirectory());
				copyDatabaseFiles(params.getReportDirectory());
			} catch (DownloadException | RWException e) {
				Alert alert = new Alert(AlertType.ERROR, "Issues setting up default database");
				alert.showAndWait();
			}
		}

	}

	public void copyDatabaseFiles(String directory) {
		Main.rwDebugInst.debug("Main", "checkDefaultDb", MRBDebug.DETAILED, "Copying empty database");
		String outFile = directory + "/Moneydance.mv.db";
		try {
			java.io.InputStream in = getClass().getResourceAsStream(Constants.RESOURCES + Constants.JASPERDATABASE);
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			File outputFile = new File(outFile);
			OutputStream outStream = new FileOutputStream(outputFile);
			outStream.write(buffer);
			outStream.close();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new DownloadException("Error creating Database Adapter");
		}
		Main.preferences.put(Constants.PARMLASTDB, Main.databaseChanged);
	}

	public void createAdapter(String directory) throws RWException {
		Main.rwDebugInst.debug("MyReport", "createAdapter", MRBDebug.DETAILED, "Creating database adapter");
		String outFile = directory + "/Moneydance.xml";
		File extensionData = MRBDirectoryUtils.getExtensionDataDirectory(Constants.PROGRAMNAME);
		String dirName = extensionData.getAbsolutePath();

		try {
			java.io.InputStream in = getClass().getResourceAsStream(Constants.RESOURCES + Constants.DATABASEADAPTER);
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			File outputFile = new File(outFile);
			OutputStream outStream = new FileOutputStream(outputFile);
			String tempStr = new String(buffer, StandardCharsets.UTF_8);
			tempStr = tempStr.replace("##database##", directory + "/Moneydance");
			tempStr = tempStr.replace("##jar##", dirName + "/" + Constants.DATABASEJAR);
			tempStr = tempStr.replace(".jarsav", ".jar");
			buffer = tempStr.getBytes();
			outStream.write(buffer);
			outStream.close();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RWException("Error creating Database Adapter");
		}
	}

	@Override
	public void handle(ActionEvent event) {
		if (event.getSource() instanceof MenuItem) {
			MenuItem mItem = (MenuItem) event.getSource();
			String command = mItem.getText();
			switch (command) {
			case Constants.ITEMFILECLOSE:
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Main.extension.closeConsole();
					}
				});
				break;
			case Constants.ITEMFILESAVE:
				params.save();
				break;
			case Constants.ITEMFILEOPTIONS:
				new FirstRun(this, params);
				if (params.getDataDirectory().equals(Constants.NODIRECTORY)) {
					Alert alert = new Alert(AlertType.ERROR,
							"Extension can not continue without setting the directories");
					alert.showAndWait();
				} else
					params.save();
				resetData();
			}
		}

	}

	private ScreenPanel getFocus() {
		Node node = scene.getFocusOwner();
		while (node != null) {
			if (node == selectionPan)
				return selectionPan;
			if (node == dataPan)
				return dataPan;
			if (node == reportPan)
				return reportPan;
			node = node.getParent();
		}
		return null;
	}

	public void setSizes() {
		setPreferences();
		if (mainScreen != null) {
			SCREENWIDTH = (int) Math.round(mainScreen.getWidth());
			SCREENHEIGHT = (int) Math.round(mainScreen.getHeight());
			updatePreferences(SCREENWIDTH, SCREENHEIGHT);
		}
		Main.rwDebugInst.debugThread("MyReport", "setSizes", MRBDebug.SUMMARY,
				"New size " + "/" + SCREENHEIGHT + "/" + SCREENWIDTH);
	}

	/*
	 * preferences
	 */
	private void setPreferences() {
		iFRAMEWIDTH = Main.preferences.getInt(Constants.PROGRAMNAME + "." + Constants.CRNTFRAMEWIDTH,
				Constants.MAINSCREENWIDTH);
		iFRAMEDEPTH = Main.preferences.getInt(Constants.PROGRAMNAME + "." + Constants.CRNTFRAMEHEIGHT,
				Constants.MAINSCREENHEIGHT);
	}

	private void updatePreferences(int width, int height) {
		SCREENHEIGHT = height;
		SCREENWIDTH = width;
		int dataWidth = (width) / 2;
		int dataHeight = (height) / 2;
		if (dataWidth < Constants.DATASCREENWIDTHMIN)
			dataWidth = Constants.DATASCREENWIDTHMIN;
		if (dataHeight < Constants.DATASCREENHEIGHTMIN)
			dataHeight = Constants.DATASCREENHEIGHTMIN;

		Main.preferences.put(Constants.PROGRAMNAME + "." + Constants.DATAPANEWIDTH, dataWidth);
		Main.preferences.put(Constants.PROGRAMNAME + "." + Constants.DATAPANEHEIGHT, dataHeight);
		Main.preferences.isDirty();
		if (selectionPan != null)
			selectionPan.resize();
		if (reportPan != null)
			reportPan.resize();
		if (templatePan != null)
			templatePan.resize();
		if (dataPan != null)
			dataPan.resize();
		Main.rwDebugInst.debugThread("MyReport", "updatePreferences", MRBDebug.SUMMARY,
				"New size " + width + "/" + height + "/" + dataWidth + "/" + dataHeight);
	}

	public void resetData() {
		if (dataPan != null)
			dataPan.resetData();
		if (selectionPan != null)
			selectionPan.resetData();
		if (templatePan != null)
			templatePan.resetData();
		if (reportPan != null)
			reportPan.resetData();
	}

	public void closeDown() {
		if (templatePan != null)
			templatePan.closeDown();
	}
}
