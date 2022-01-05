package com.moneydance.modules.features.mrbutil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;



public class MRBFXSelectionPanel implements Callback<Object, String> {
	private TableView<MRBFXSelectionRow> table;
	private ObservableList<MRBFXSelectionRow> listModel;
	private List<String> selected=new ArrayList<String>();
	private List<MRBFXSelectionRow> list;
	private String inActiveStr;
	private Button inActiveBtn;
	private Stage stage;
	private Scene scene;
	private GridPane panDisplay;
	private String title;
	public Image cancelImg = null;
	public Image okImg = null;
	public MRBFXSelectionPanel (List<MRBFXSelectionRow> list, String inActiveStr, String title) {
		this.list = list;
		this.inActiveStr = inActiveStr;
		this.title=title;
		InputStream streamOk = getClass().getResourceAsStream("/com/moneydance/modules/features/mrbutil/resources/ok_32px.png");
		if (streamOk != null) {
				okImg = new Image(streamOk);
			try {
				streamOk.close();
			}
			catch (IOException e) {}
		}
		InputStream streamCancel = getClass().getResourceAsStream("/com/moneydance/modules/features/mrbutil/resources/cancel_32px.png");
		if (streamCancel != null) {
			cancelImg = new Image(streamCancel);
			try {
				streamCancel.close();
			}
			catch (IOException e) {}
		}
		
	}
	public void display() {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		panDisplay = new GridPane();
		scene = new Scene(panDisplay,600,500);
		stage.setScene(scene);
		stage.setTitle(title);
		listModel = FXCollections.observableList(list);
		for (MRBFXSelectionRow row : listModel)
			row.setPanel(this);
		table= new TableView<MRBFXSelectionRow>();
		table.setItems(listModel);
		TableColumn<MRBFXSelectionRow,CheckBox> col1 = new TableColumn<MRBFXSelectionRow,CheckBox>("");
		TableColumn<MRBFXSelectionRow,HBox> col2 = new TableColumn<MRBFXSelectionRow,HBox>("");
		table.getColumns().addAll(Arrays.asList(col1,col2));
		col1.setCellValueFactory(new PropertyValueFactory<MRBFXSelectionRow,CheckBox>("col1"));
		col1.setPrefWidth(30);
		col2.setCellValueFactory(new PropertyValueFactory<MRBFXSelectionRow,HBox>("col2"));
		col2.prefWidthProperty().bind(panDisplay.widthProperty().subtract(50));
		GridPane.setHgrow(table, Priority.ALWAYS);
		table.prefWidthProperty().bind(panDisplay.widthProperty());
		int ix=0;
		int iy=0;
		panDisplay.add(table, ix,iy,5,1);
		Button okBtn = new Button();
		if (okImg == null)
			okBtn.setText("OK");
		else
			okBtn.setGraphic(new ImageView(okImg));
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				selected.clear();
				for (MRBFXSelectionRow row : list) {
					if (row.isSelected())
						selected.add(row.getRowId());
				}
				stage.close();
			}
		});
		Button cancelBtn = new Button();
		if (cancelImg == null)
			cancelBtn.setText("Cancel");
		else
			cancelBtn.setGraphic(new ImageView(cancelImg));
		cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				stage.close();
			}
		});
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
		if (inActiveStr != null && !inActiveStr.isEmpty()) {
			inActiveBtn = new Button(inActiveStr);
			inActiveBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					selectInactive();
				}
			});
		}
		ix=0;
		iy=2;
		panDisplay.add(selectAllBtn, ix++, iy,1,1);
		GridPane.setMargin(selectAllBtn, new Insets(10,10,10,10));
		panDisplay.add(deSelectAllBtn, ix++, iy,1,1);
		GridPane.setMargin(deSelectAllBtn, new Insets(10,10,10,10));
		if (inActiveStr != null && !inActiveStr.isEmpty()) {
			panDisplay.add(inActiveBtn, ix++, iy,1,1);
			GridPane.setMargin(inActiveBtn, new Insets(10,10,10,10));
		}
		panDisplay.add(okBtn, ix++, iy,1,1);
		GridPane.setMargin(okBtn, new Insets(10,10,10,10));
		panDisplay.add(cancelBtn,ix++, iy,1,1);
		GridPane.setMargin(cancelBtn, new Insets(10,10,10,10));
		HBox.setHgrow(table, Priority.ALWAYS);
		stage.showAndWait();
	}
	@Override
	public String call(Object lineItem) {
		return "";
	}
	public List<String> getSelected(){
		return selected;
	}
	private void selectAll() {
		for (MRBFXSelectionRow row : listModel) {
			row.setSelected(true);
		}
		table.refresh();
	}
	private void deSelectAll() {
		for (MRBFXSelectionRow row : listModel) {
			row.setSelected(false);
		}
		table.refresh();
	}
	private void selectInactive() {
		for (MRBFXSelectionRow row : listModel) {
			row.setSelected(!row.isInActive());
		}
		table.refresh();
	}
	public void setChildren(MRBFXSelectionRow rowSelected, Boolean select) {
		Boolean found=false;
		Integer depthSelected=0;
		for (MRBFXSelectionRow row :listModel) {
			if (row == rowSelected) {
				found = true;
				depthSelected = row.getDepth();
			}
			else {
				if (found) {
					if (row.getDepth() > depthSelected)
						row.setSelected(select);
					else
					{
						found=false;
						break;
					}
				}
			}
		}
		table.refresh();
	}

}

