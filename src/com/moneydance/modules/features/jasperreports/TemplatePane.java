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
package com.moneydance.modules.features.jasperreports;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;


public class TemplatePane extends ScreenPanel {
	private Parameters params;
	private ObservableList<TemplateRow> model;
    private TableView<TemplateRow> thisTable;
    private int SCREENWIDTH; 
    private int SCREENHEIGHT; 
	public TemplatePane(Parameters paramsp) {
		super();
		params = paramsp;
		setUpTable();
		Label templateLbl = new Label("Templates");
		templateLbl.setTextAlignment(TextAlignment.CENTER);
		templateLbl.setFont(Font.font("Veranda",FontWeight.BOLD,20.0));
		add(templateLbl,0,0);
		GridPane.setHalignment(templateLbl, HPos.CENTER);
		add(thisTable,0,1);
		resize();
	}
	

	public void resize() {
		SCREENWIDTH =Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.DATAPANEWIDTH,Constants.DATASCREENWIDTH);
		SCREENHEIGHT =Main.preferences.getInt(Constants.PROGRAMNAME+"."+Constants.DATAPANEHEIGHT,Constants.DATASCREENHEIGHT);
		setPrefSize(SCREENWIDTH,SCREENHEIGHT);
		thisTable.setPrefWidth(SCREENWIDTH);
		thisTable.setPrefHeight(SCREENHEIGHT);
	}
	private void setUpTable () {
		thisTable = new TableView<>();
		thisTable.setEditable(true);
		/*
		 * Name
		 */
		TableColumn<TemplateRow,String> name = new TableColumn<>("Name");
		/*
		 * Ticker
		 */
		TableColumn<TemplateRow,String> lastVerified = new TableColumn<>("Last Verified Date");
		thisTable.getColumns().addAll(name,lastVerified);
		model =FXCollections.observableArrayList(params.getTemplateList());
		thisTable.setItems(model);
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		lastVerified.setCellValueFactory(new PropertyValueFactory<>("lastVerified"));
	}
		
}
