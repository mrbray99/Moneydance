package com.moneydance.modules.features.mrbutil;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MRBFXSelectionPanel {
	private ListView<String> listDisplayMissing;
	private ListView<String> listDisplaySelect;
	private ObservableList<String> missingModel;
	private ObservableList<String> selectModel;
	private List<String> missing;
	private List<String> selected;
	private Button selectBtn;
	private Button deselectBtn;
	private Stage stage;
	private Scene scene;
	private GridPane panDisplay;
	public MRBFXSelectionPanel (List<String> missingp,List<String>selectedp) {
		missing = missingp;
		selected = selectedp;
		missingModel = FXCollections.observableArrayList(missing);
		selectModel = FXCollections.observableArrayList(selected);
	}
	public void display() {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		panDisplay = new GridPane();
		scene = new Scene(panDisplay,600,500);
		stage.setScene(scene);
		listDisplayMissing= new ListView<>(missingModel);
		listDisplayMissing.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listDisplaySelect = new ListView<>(selectModel);
		listDisplaySelect.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		int ix=0;
		int iy=0;
		Label lblAccmis = new Label("Available Items");
		panDisplay.add(lblAccmis, ix,iy);
		ix=+2;
		Label lblAccsel = new Label("Included Items");
		panDisplay.add(lblAccsel, ix,iy);
		/*
		 * Accounts Available
		 */
		ix = 0;
		iy++;
		panDisplay.add(listDisplayMissing, ix,iy);
		GridPane.setRowSpan(listDisplayMissing, 2);
		GridPane.setMargin(listDisplayMissing,new Insets(0, 0, 0, 10) );
		/*
		 * Account Buttons
		 */
		ix=1;
		selectBtn = new Button("Sel");
		selectBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				accountSelect();
			}
		});
		panDisplay.add(selectBtn,ix,iy);
		GridPane.setMargin(selectBtn,new Insets(40, 5, 5, 5));
		deselectBtn = new Button("Des");
		deselectBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				accountDeselect();
			}
		});
		panDisplay.add(deselectBtn,ix,iy+1);
		GridPane.setMargin(deselectBtn,new Insets(0, 5, 5, 5));
		/*
		 * Accounts Selected
		 */
		ix=2;
		panDisplay.add(listDisplaySelect,ix,iy);
		GridPane.setRowSpan(listDisplaySelect,2);
		GridPane.setMargin(listDisplaySelect, new Insets(0, 0, 0, 10));
		Button okBtn = new Button("OK");
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
			stage.close();
		}
		});
		Button cancelBtn = new Button("Cancel");
		cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				stage.close();
			}
		});
		ix=0;
		iy=4;
		panDisplay.add(okBtn, ix++, iy);
		GridPane.setMargin(okBtn, new Insets(10,10,10,10));
		panDisplay.add(cancelBtn,ix++, iy);
		GridPane.setMargin(cancelBtn, new Insets(10,10,10,10));		
		stage.showAndWait();

	}
	public List<String> getSelected() {
		return selected;
	}
	private void accountSelect() {
		ObservableList<String> selectedItems = listDisplayMissing.getSelectionModel().getSelectedItems();
		if (selectedItems.size() != 0) {
			for (String temp : selectedItems) {
				selected.add(temp);
				missing.remove(temp);
			}
		}
		resetLists();
	}

	/*
	 * Account Deselect - move selected account lines to available
	 */
	private void accountDeselect() {
		ObservableList<String> selectedItems = listDisplaySelect.getSelectionModel().getSelectedItems();
		if (selectedItems.size() != 0) {
			for (String temp : selectedItems) {
				missing.add(temp);
				selected.remove(temp);
			}
		}
		resetLists();
	}
	private void resetLists() {
		missingModel = FXCollections.observableArrayList(missing);
		selectModel = FXCollections.observableArrayList(selected);
		listDisplayMissing.getSelectionModel().clearSelection();
		listDisplayMissing.getItems().clear();
		listDisplayMissing.setItems(missingModel);
		listDisplaySelect.getSelectionModel().clearSelection();
		listDisplaySelect.getItems().clear();
		listDisplaySelect.setItems(selectModel);
	}

}
