/*
 * Copyright (c) 2018, Michael Bray.  All rights reserved.
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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;
import com.moneydance.modules.features.reportwriter.Parameters;
import com.moneydance.modules.features.reportwriter.samples.DownloadException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class TemplatePane extends ScreenPanel {
	private Parameters params;
	private ObservableList<TemplateRow> model;
	private TableView<TemplateRow> thisTable;
	private Button copyBtn;
    private Path watchPath;
    private WatchKey watchKey;
    private WatchService watcher;
    private Thread watchThread;
    private Boolean dataPaneVisible=false;
    private Boolean directoryChanged= false;


	public TemplatePane(Parameters paramsp) {
		super();
		params = paramsp;
		watchPath = Paths.get(params.getReportDirectory());
		try {
			watcher = FileSystems.getDefault().newWatchService();
			watchKey =watchPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		}
		catch (IOException x) {
			x.printStackTrace();
		}
		watchThread = new Thread() {
			public void run() {
				watchReports();
			}
		};
		watchThread.start();
		setUpTable();
		Label templateLbl = new Label("Jasper Reports Templates");
		templateLbl.setTextAlignment(TextAlignment.CENTER);
		templateLbl.setFont(Font.font("Veranda", FontWeight.BOLD, 20.0));
		add(templateLbl, 0, 0);
		GridPane.setHalignment(templateLbl, HPos.CENTER);
		copyBtn = new Button();
		if (Main.loadedIcons.downloadImg == null)
			copyBtn.setText("Download");
		else
			copyBtn.setGraphic(new ImageView(Main.loadedIcons.downloadImg));
		copyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				downloadTemplates();
				resetData();
			}
		});

		add(thisTable, 0, 1);
		add(copyBtn,0,2);
		GridPane.setMargin(copyBtn,new Insets(10, 10, 10, 10));
		resize();
	}
	private void watchReports() {
		boolean poll = true;
		Main.rwDebugInst.debug("TemplatePane", "watchReports",MRBDebug.DETAILED,"Started");
		while (poll) {
			try {
				Main.rwDebugInst.debug("TemplatePane", "watchReports",MRBDebug.DETAILED,"Looking for watch event");
				watchKey = watcher.take();
			}
			catch (InterruptedException e) {
				Main.rwDebugInst.debug("TemplatePane", "watchReports",MRBDebug.DETAILED,"Interrupted");
				poll = false;
				return;
			}
			for (WatchEvent<?> event :watchKey.pollEvents()) {
				Main.rwDebugInst.debug("TemplatePane", "watchReports",MRBDebug.DETAILED,"Watch event "+event.context().toString());
				String fileName = event.context().toString();
				if (fileName.toLowerCase().endsWith(Constants.TEMPLATEEXTENSION)){
					directoryChanged();
				}
				
			}
			watchKey.reset();
			Main.rwDebugInst.debug("TemplatePane", "watchReports",MRBDebug.DETAILED,"All events processed");			
		}
	}
	private void directoryChanged() {
		Main.rwDebugInst.debug("TemplatePane", "directoryChanged",MRBDebug.DETAILED,"dataPaneVisible = "+dataPaneVisible);
		if (dataPaneVisible) {
			directoryChanged = true;
			return;
		}
       Platform.runLater(new Runnable() {
	            @Override 
	            public void run() {
					resetData();
				}
		});
	}
	@Override
	public void resize() {
		super.resize();
		thisTable.setPrefWidth(SCREENWIDTH);
		thisTable.setPrefHeight(SCREENHEIGHT);
	}

	private void setUpTable() {
		thisTable = new TableView<>();
		thisTable.setEditable(true);
		thisTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		thisTable.setMaxWidth(Double.MAX_VALUE);
		thisTable.setMaxHeight(Double.MAX_VALUE);
		/*
		 * Name
		 */
		TableColumn<TemplateRow, String> name = new TableColumn<>("Name");
		/*
		 * Ticker
		 */
		TableColumn<TemplateRow, String> lastVerified = new TableColumn<>("Last Verified Date");
		thisTable.getColumns().addAll(name, lastVerified);
		model = FXCollections.observableArrayList(params.getTemplateList());
		thisTable.setItems(model);
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		lastVerified.setCellValueFactory(new PropertyValueFactory<>("lastVerified"));
	}

	public void resetData() {
		Main.rwDebugInst.debug("TemplatePane", "watchReports",MRBDebug.DETAILED,"Templates reset");
		params.setReportTemplates();
		model = FXCollections.observableArrayList(params.getTemplateList());
		thisTable.setItems(model);
		thisTable.refresh();
	}

	private void downloadTemplates() throws DownloadException {
		TemplateDataPane dataPane = new TemplateDataPane(params);
		dataPaneVisible = true;
		dataPane.displayPanel();
		dataPaneVisible = false;
		if (directoryChanged)
			resetData();
		directoryChanged = false;
		
	}
	public void closeDown() {
		Main.rwDebugInst.debugThread("TemplatePane", "closeDown", MRBDebug.SUMMARY,
				"Closing background thread");
		if (watchThread!=null)
			watchThread.interrupt();
	}

}
