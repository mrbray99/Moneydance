/*
 * Copyright (c) 2021, Michael Bray.  All rights reserved.
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.moneydance.modules.features.mrbutil.MRBFXSelectionRow;
import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;
import com.moneydance.modules.features.reportwriter.databeans.DataBean;
import com.moneydance.modules.features.reportwriter.databeans.BeanAnnotations.ColumnName;
import com.moneydance.modules.features.reportwriter.databeans.BeanAnnotations.ColumnTitle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FieldSelectionPane {
	private Stage stage;
	private Scene scene;
	private MyGridPane pane;
	private HBox buttons;
	private ObservableList<FieldSelectionRow> model;
	private TableView<FieldSelectionRow> thisTable = null;
	private HBox includeAll;
	private Label includeLbl;
	private CheckBox includeAllCB;
	private List<String> current;
	private String windowName;
	private int SCREENWIDTH;
	private int SCREENHEIGHT;
	private DataBean bean;

	public FieldSelectionPane(String windowName, DataBean bean, List<String> current) {
		this.current = current;
		this.windowName = windowName;
		this.bean = bean;
		Field[] fields = this.bean.getClass().getDeclaredFields();
		List<FieldSelectionRow> fieldList = new ArrayList<FieldSelectionRow>();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(ColumnName.class)) // not database field
				continue;
			ColumnName name = field.getAnnotation(ColumnName.class);
			String fldTitle;
			if (field.isAnnotationPresent(ColumnTitle.class)) {
				ColumnTitle title = field.getAnnotation(ColumnTitle.class);
				fldTitle = title.value();
			} else
				fldTitle = name.value();
			boolean selected;
			if (current.contains(name.value()))
				selected = true;
			else
				selected = false;
			;
			FieldSelectionRow fldRow = new FieldSelectionRow(name.value(), fldTitle, selected);
			fieldList.add(fldRow);

		}
		model = FXCollections.observableArrayList(fieldList);

	}

	public List<String> displayPanel() {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		pane = new MyGridPane(windowName);
		scene = new Scene(pane);
		stage.setScene(scene);
		stage.setTitle("Select " + bean.getScreenTitle() + " Fields");
		stage.widthProperty().addListener((ov, oldv, newv) -> {
			SCREENWIDTH = newv.intValue();
			Main.preferences.put(Constants.PROGRAMNAME + "." + Constants.FIELDPANEWIDTH, SCREENWIDTH);
			if (thisTable != null)
				thisTable.setPrefWidth(SCREENWIDTH);

		});
		stage.heightProperty().addListener((ov, oldv, newv) -> {
			SCREENHEIGHT = newv.intValue();
			Main.preferences.put(Constants.PROGRAMNAME + "." + Constants.FIELDPANEHEIGHT, SCREENHEIGHT);
		});

		int ix = 0;
		int iy = 0;
		setUpTable();
		pane.add(thisTable, ix, iy++);
		GridPane.setColumnSpan(thisTable, 2);
		GridPane.setHgrow(thisTable, Priority.ALWAYS);
		GridPane.setVgrow(thisTable, Priority.ALWAYS);
		ix = 0;
		buttons = new HBox();
		Button selectAllBtn = new Button("Select All");
		selectAllBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				selectAll();
			}
		});
		Button deSelectAllBtn = new Button("Deselect All");
		deSelectAllBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				deSelectAll();
			}
		});
		Button okBtn = new Button();
		if (Main.loadedIcons.okImg == null)
			okBtn.setText("OK");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.okImg));
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				updateParms();
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
		buttons.getChildren().addAll(selectAllBtn, deSelectAllBtn, okBtn, cancelBtn);
		HBox.setMargin(selectAllBtn, new Insets(10, 10, 10, 10));
		HBox.setMargin(deSelectAllBtn, new Insets(10, 10, 10, 10));
		HBox.setMargin(okBtn, new Insets(10, 10, 10, 10));
		HBox.setMargin(cancelBtn, new Insets(10, 10, 10, 10));
		pane.add(buttons, 0, iy);
		GridPane.setColumnSpan(buttons, 2);
		stage.showAndWait();
		return current;
	}

	public void resize() {
		SCREENWIDTH = Main.preferences.getInt(Constants.PROGRAMNAME + "." + Constants.FIELDPANEWIDTH,
				Constants.FIELDSCREENWIDTH);
		SCREENHEIGHT = Main.preferences.getInt(Constants.PROGRAMNAME + "." + Constants.FIELDPANEHEIGHT,
				Constants.FIELDSCREENHEIGHT);
		if (stage != null) {
			if (SCREENWIDTH != Constants.DATASCREENWIDTH)
				stage.setWidth(SCREENWIDTH);
			if (SCREENHEIGHT != Constants.DATASCREENHEIGHT)
				stage.setHeight(SCREENHEIGHT);
		}
	}

	private void selectAll() {
		for (FieldSelectionRow row : model) {
			row.setSelected(true);
		}
		thisTable.refresh();
	}

	private void deSelectAll() {
		for (FieldSelectionRow row : model) {
			row.setSelected(false);
		}
		thisTable.refresh();
	}

	private void updateParms() {
		current = new ArrayList<String>();
		for (FieldSelectionRow row : model) {
			if (row.getSelected())
				current.add(row.getFieldName());
		}
	}

	@SuppressWarnings("unchecked")
	private void setUpTable() {
		thisTable = new TableView<>();
		thisTable.setEditable(true);
		thisTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		/*
		 * Name
		 */
		TableColumn<FieldSelectionRow, String> name = new TableColumn<>("Field Name");
		/*
		 * Last Created
		 */
		TableColumn<FieldSelectionRow, CheckBox> included = new TableColumn<>("Included");
		thisTable.getColumns().addAll(name, included);
		thisTable.setItems(model);
		name.setCellValueFactory(new PropertyValueFactory<>("fieldTitle"));
		included.setCellValueFactory(new PropertyValueFactory<>("included"));
		thisTable.setPrefWidth(SCREENWIDTH);

	}

}
