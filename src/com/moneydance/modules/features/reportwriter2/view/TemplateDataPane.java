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
package com.moneydance.modules.features.reportwriter2.view;

import java.io.IOException;


import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.Parameters;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.TemplateDataPaneController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TemplateDataPane extends ScreenDataPane {
	private Parameters params;
	private ReportTemplate template;
	private TemplatePane templatePane;
	private boolean addTemplate;
	/*
	 * Screen variables
	 */
	private Scene scene;
	private GridPane mainScreen;
	private TemplateDataPaneController controller;
	private FXMLLoader loader;
	public  FieldPane selectFieldPane=null;
	public int iFRAMEWIDTH;
	public int iFRAMEDEPTH;
	public int SCREENWIDTH = 0;
	public int SCREENHEIGHT = 0;

	public TemplateDataPane(TemplatePane templatePane,Parameters params,ReportTemplate template) {
		this.templatePane=templatePane;
		this.params = params;
		this.template = template;
		addTemplate=false;
	}

	public TemplateDataPane(TemplatePane templatePane,Parameters params) {
		this.templatePane=templatePane;
		this.params = params;
		this.template = new ReportTemplate(this.params);
		addTemplate=true;
	}
	public ReportTemplate displayPanel(){
		try {
			loader = new FXMLLoader(getClass().getResource(Constants.RESOURCES+Constants.TEMPLATESCREENFXML));
			controller = new TemplateDataPaneController();
			loader.setController(controller);	
			mainScreen = loader.load();
			controller.setFields(templatePane,this,template);
			if (addTemplate)
				controller.setAdd();
			setSizes();
		} 
		catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		setStage(new Stage());
		stage.initModality(Modality.APPLICATION_MODAL);
		scene = new Scene(mainScreen);
		scene.getStylesheets().add("file:///"+Main.extensionDir.replace("\\","/") +"/"+ Constants.STYLESHEET);
		stage.setScene(scene);
		stage.setOnCloseRequest(ev->{
			if (template.isDirty()) {
				if (OptionMessage.yesnoMessage("Parameters have changed.  Do you wish to abandon them?")) {
					template = null;
					stage.close();
				}
				else
					ev.consume();
			}
			else {
				template = null;
				stage.close();
			}
		});
		Main.accels.setSceneClose(scene, new Runnable() {
			@Override
			public void run() {
				Main.rwDebugInst.debugThread("TemplateDatapane", "createScene", MRBDebug.DETAILED, "Accelerator close ");
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
				Main.rwDebugInst.debugThread("TemplateDatapane", "createScene", MRBDebug.DETAILED, "Accelerator open ");
			}
		});
		Main.accels.setSceneDelete(scene, new Runnable() {
			@Override
			public void run() {
				Main.rwDebugInst.debugThread("TemplateDatapane", "createScene", MRBDebug.DETAILED, "Accelerator delete ");
			}
		});
		Main.accels.setSceneNew(scene, new Runnable() {
			@Override
			public void run() {
				Main.rwDebugInst.debugThread("TemplateDatapane", "createScene", MRBDebug.DETAILED, "Accelerator new ");
			}
		});
		stage.showAndWait();
		return template;
	}





	public void setSizes() {
		setPreferences();
		mainScreen.setPrefWidth(iFRAMEWIDTH);
		mainScreen.setPrefHeight(iFRAMEDEPTH);
		Main.rwDebugInst.debugThread("TemplateDatapane", "setSizes", MRBDebug.SUMMARY,
				"New size " + "/" + iFRAMEDEPTH + "/" + iFRAMEWIDTH);
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
	public void requestExtFocus() {
		stage.requestFocus();
	}

	public void closeDown() {
		stage.hide();
		templatePane.requestExtFocus();
	}
}
