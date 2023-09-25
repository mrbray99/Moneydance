package com.moneydance.modules.features.reportwriter2.view.screenctrl;

import java.util.List;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.edit.UndoAction;
import com.moneydance.modules.features.reportwriter2.edit.UndoFieldDetail;
import com.moneydance.modules.features.reportwriter2.edit.UndoLayoutManager;
import com.moneydance.modules.features.reportwriter2.edit.UndoRecord;
import com.moneydance.modules.features.reportwriter2.edit.UndoDetail.DetailType;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Constants.ReportFieldType;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.view.DetailPane;
import com.moneydance.modules.features.reportwriter2.view.FieldExpression;
import com.moneydance.modules.features.reportwriter2.view.controls.FieldLabel;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportLayout;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportStyle;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class FieldDetailController extends DetailPane implements UndoAction {
	@FXML
	private GridPane fieldDetailPane;
	@FXML
	private Label fieldName;
	@FXML
	private Label fieldType;
	@FXML
	private Label fieldSource;
	@FXML
	private Label fieldBanner;
	@FXML
	private TextField fieldText;
	@FXML
	private TextField fieldExp;
	@FXML
	private TextField fieldPattern;
	@FXML
	private TextField fieldWidth;
	@FXML
	private TextField fieldHeight;
	@FXML
	private TextField positionX;
	@FXML
	private TextField positionY;
	@FXML
	private CheckBox fieldBlank;
	@FXML
	private Button expBtn;
	@FXML
	private Button patternBtn;
	@FXML
	private ComboBox<ReportStyle> layoutStyles;
	private FieldExpression fldExpression;
	private ReportLayout field;
	private ReportField fldField;
	private LayoutPaneController layout;
	private ObservableList<ReportStyle>styleItems;
	private ReportStyle defaultStyle;
	private UndoLayoutManager undoManager = UndoLayoutManager.getInstance();
	public FieldDetailController(ReportTemplate template, LayoutPaneController layout, ReportLayout field) {
		super(Constants.FIELDDETAILFXML, template);
		Main.rwDebugInst.debugThread("FieldDetailController","const", MRBDebug.DETAILED, "Field Layout created");
		this.layout = layout;
		fieldText.focusedProperty().addListener((obs,oldValue,newValue)->{
			if (!newValue) {
				field.setText(fieldText.getText());
				template.setDirty(true);
				field.draw();
			}
		});

		fieldHeight.focusedProperty().addListener((obs,oldValue,newValue)->{
			if (!newValue) {
				String crntValue = String.valueOf(field.getFieldHeight()/(Constants.LAYOUTDIVIDER*10));
				if (!crntValue.equals(fieldHeight.getText()))		
					fieldHeightChg(fieldHeight.getText());
			}
		});

		fieldWidth.focusedProperty().addListener((obs,oldValue,newValue)->{
			if (!newValue) {
				String crntValue = String.valueOf(field.getFieldWidth()/(Constants.LAYOUTDIVIDER*10));
				if (!crntValue.equals(fieldWidth.getText()))		
					fieldWidthChg(fieldWidth.getText());
			}
		});

	      positionX.focusedProperty().addListener((obs,oldValue,newValue)->{
			if (!newValue) {
				String crntValue = String.valueOf(field.getLayoutX()/(Constants.LAYOUTDIVIDER*10));
				if (!crntValue.equals(positionX.getText()))		
					positionXChg(positionX.getText());
			}
		});

		positionY.focusedProperty().addListener((obs,oldValue,newValue)->{
			if (!newValue) {
				String crntValue = String.valueOf(field.getLayoutY()/(Constants.LAYOUTDIVIDER*10));
				if (!crntValue.equals(positionY.getText()))		
					positionYChg(positionY.getText());
			}
		});

		layoutStyles.valueProperty().addListener(new ChangeListener<ReportStyle>() {
			@Override
			public void changed(ObservableValue<? extends ReportStyle> arg0, ReportStyle oldV, ReportStyle newV) {
				if (layoutStyles.isFocused())
					layoutStylesChg(newV);
			}
		});
		fldField = field.getField();
		if (fldField.getReportType().equals(ReportFieldType.VARIABLE)) {
			expBtn.setDisable(false);
			patternBtn.setDisable(false);
			fieldPattern.setDisable(false);
		}
		else {
			expBtn.setDisable(true);
			patternBtn.setDisable(true);
			fieldPattern.setDisable(true);
		}
		if (Main.loadedIcons.mathBookImg == null)
			expBtn.setText("Expression");
		else
			expBtn.setGraphic(new ImageView(Main.loadedIcons.mathBookImg));
		if (Main.loadedIcons.patternImg == null)
			patternBtn.setText("Pattern");
		else
			patternBtn.setGraphic(new ImageView(Main.loadedIcons.patternImg));

		setLayoutFields(field);
	}
	public FieldDetailController(ReportTemplate template, LayoutPaneController layout, ReportField field) {
		super(Constants.FIELDDETAILFXML, template);
		Main.rwDebugInst.debugThread("FieldDetailController","const", MRBDebug.DETAILED, "Field created");
		this.layout = layout;
		this.field=null;
		fldField = field;
		fieldText.focusedProperty().addListener((obs,oldValue,newValue)->{
			if (!newValue) {
				fldField.setFieldText(fieldText.getText());
				template.setDirty(true);
		}
		});
		fieldWidth.setDisable(true);
		fieldHeight.setDisable(true);
		positionX.setDisable(true);
		positionY.setDisable(true);
		layoutStyles.valueProperty().addListener((arg0, oldV, newV)-> {
			if (layoutStyles.isFocused())
				layoutStylesChg(newV);
		});
		switch (field.getReportType()) {
		case DATABASE:
		case GROUPNAME:
		case LABEL:
		case TOTAL:
			fieldPattern.setDisable(true);
			expBtn.setDisable(true);
			patternBtn.setDisable(true);
			break;
		case VARIABLE:
			fieldPattern.setDisable(false);
			expBtn.setDisable(false);
			patternBtn.setDisable(false);
			break;
		default:
			break;
		
		}
		if (Main.loadedIcons.mathBookImg == null)
			expBtn.setText("Expression");
		else
			expBtn.setGraphic(new ImageView(Main.loadedIcons.mathBookImg));
		if (Main.loadedIcons.patternImg == null)
			patternBtn.setText("Pattern");
		else
			patternBtn.setGraphic(new ImageView(Main.loadedIcons.patternImg));
		setFieldFields(field);
	}
	public void setFieldFields(ReportField field) {
		this.fldField = field;
		positionX.setText("");
		positionY.setText("");
		fieldHeight.setText("");
		fieldWidth.setText("");
		fieldBlank.setSelected(false);
		fieldName.setText(fldField.getName());
		fieldType.setText(fldField.getReportType().toString());
		fieldBanner.setText("");
		fieldText.setText(fldField.getFieldText());
		if (field.getReportType() == ReportFieldType.DATABASE)
			fieldSource.setText(field.getFieldBean().getTableName());
		else
			fieldSource.setText("");
		defaultStyle = new ReportStyle();
		defaultStyle.setName("Use Banner Style");
		styleItems=FXCollections.observableArrayList();
		
		styleItems.add(defaultStyle);
		styleItems.addAll(template.getStyles().values());
		layoutStyles.setItems(styleItems);
		layoutStyles.setCellFactory(new Callback<ListView<ReportStyle>,ListCell<ReportStyle>>(){
			@Override
			public ListCell<ReportStyle> call(ListView<ReportStyle> style) {
				return  new ListCell<ReportStyle>() {
					@Override
					protected void updateItem(ReportStyle t, boolean bln) {
						super.updateItem(t, bln);
						if (t==null || bln){
							setText(null);
							setGraphic(null);
						}
						else {
							FieldLabel newText=new FieldLabel();
							setText("");
							if (t==defaultStyle)
								newText.setText(t.getName());
							else
								t.getDisplayText(newText,t.getName());
							setGraphic(newText);
						}
					}
				};
			}
			
		});

		layoutStyles.setButtonCell(new ListCell<ReportStyle>() {
			@Override
			protected void updateItem(ReportStyle t, boolean bln) {
				super.updateItem(t, bln);
				if (t==null || bln )
				{
					setText(null);
					setGraphic(null);
				}
				else {
					FieldLabel newText=new FieldLabel();
					if (t==defaultStyle)
						newText.setText(t.getName());
					else
						t.getDisplayText(newText,t.getName());
					setText("");
					setGraphic(newText);
				}
			}
		});
		if (field.getLayoutStyle() == null)
			layoutStyles.getSelectionModel().select(defaultStyle);
		else
			layoutStyles.getSelectionModel().select(field.getLayoutStyle());
		if (ReportFieldType.VARIABLE.equals(field.getReportType())) {
				fieldExp.setText(field.getFieldExp());
				fieldPattern.setText(field.getFieldPattern());
		}
	}
	public void setLayoutFields(ReportLayout field) {
		this.field = field;
		fldField = field.getField();
		if (this.field==null) {
			fieldName.setText("**");
			fieldType.setText("**");
			fieldBanner.setText("**");
			positionX.setText("*");
			positionY.setText("*");
			fieldWidth.setText("*");
			fieldHeight.setText("*");
			fieldBlank.setSelected(false);
			fieldText.setText("*");
			fieldSource.setText("");
			layoutStyles.getSelectionModel().clearSelection();
			fieldExp.setText("");
			fieldPattern.setText("");
		}
		else {
			fieldName.setText(this.field.getName());
			fieldType.setText(this.field.getField().getReportType().toString());
			fieldBanner.setText(this.field.getBanner().getName());
			positionX.setText(String.valueOf(this.field.getX()/(Constants.LAYOUTDIVIDER*10)));
			positionY.setText(String.valueOf(this.field.getY()/(Constants.LAYOUTDIVIDER*10)));
			fieldWidth.setText(String.valueOf(this.field.getFieldWidth()/(Constants.LAYOUTDIVIDER*10)));
			fieldHeight.setText(String.valueOf(this.field.getFieldHeight()/(Constants.LAYOUTDIVIDER*10)));
			fieldBlank.setSelected(this.field.getBlank());
			fieldText.setText(this.field.getText());
			this.field.getXProperty().addListener((t->{
				positionX.setText(String.valueOf(this.field.getX()/(Constants.LAYOUTDIVIDER*10)));				
			}));
			this.field.getYProperty().addListener((t->{
				positionY.setText(String.valueOf(this.field.getY()/(Constants.LAYOUTDIVIDER*10)));				
			}));
			if (this.field.getField().getReportType()==ReportFieldType.DATABASE)
				fieldSource.setText(this.field.getField().getFieldBean().getTableName());
			else
				fieldSource.setText("");
			if(this.field.getField().getReportType().equals(ReportFieldType.VARIABLE)) {
				fieldExp.setText(this.field.getField().getFieldExp());
				fieldPattern.setText(this.field.getField().getFieldPattern());
			}
		}
		defaultStyle = new ReportStyle();
		defaultStyle.setName("Use Banner Style");
		styleItems=FXCollections.observableArrayList();
		styleItems.add(defaultStyle);
		styleItems.addAll(template.getStyles().values());
		layoutStyles.setItems(styleItems);
		layoutStyles.setCellFactory(new Callback<ListView<ReportStyle>,ListCell<ReportStyle>>(){
			@Override
			public ListCell<ReportStyle> call(ListView<ReportStyle> style) {
				return  new ListCell<ReportStyle>() {
					@Override
					protected void updateItem(ReportStyle t, boolean bln) {
						super.updateItem(t, bln);
						if (t==null || bln){
							setText(null);
							setGraphic(null);
						}
						else
						{
							FieldLabel newText=new FieldLabel();
							setText("");
							if (t==defaultStyle)
								newText.setText(t.getName());
							else
								t.getDisplayText(newText,t.getName());
							setGraphic(newText);
						}
					}
				};
			}
			
		});
		layoutStyles.setButtonCell(new ListCell<ReportStyle>() {
			@Override
			protected void updateItem(ReportStyle t, boolean bln) {
				super.updateItem(t, bln);
				if (t==null || bln) {
					setText(null);
					setGraphic(null);
				}
				else{
					FieldLabel newText=new FieldLabel();
					if (t==defaultStyle)
						newText.setText(t.getName());
					else
						t.getDisplayText(newText,t.getName());
					setText("");
					setGraphic(newText);
				} 
			}
		});
		if (field.getLayoutStyle() == null)
			layoutStyles.getSelectionModel().select(defaultStyle);
		else
			layoutStyles.getSelectionModel().select(field.getLayoutStyle());
	}
	public void resetStyles() {
		styleItems=FXCollections.observableArrayList();
		styleItems.add(null);
		styleItems.addAll(template.getStyles().values());
		layoutStyles.setItems(styleItems);
		layoutStyles.getSelectionModel().select(field.getLayoutStyle());
	}
	public void clearFields() {
		fieldName.setText("");
		fieldType.setText("");
		fieldBanner.setText("");
		positionX.setText("");
		positionY.setText("");
		fieldWidth.setText("");
		fieldHeight.setText("");
		layoutStyles.getSelectionModel().clearSelection();
	}
	/*
	 *  Field fields
	 * 
	 * 
	 */
	@FXML
	private void expBtnClicked(ActionEvent event) {
		Main.rwDebugInst.debugThread("FieldDetailController","expBtnClicked", MRBDebug.DETAILED, "Field Expression Initiated");
		fldExpression = new FieldExpression(template, fldField);
		fieldExp.setText(fldField.getFieldExp());
	}
	@FXML
	private void patternBtnClicked(ActionEvent event) {
		
	}
	private void fieldWidthChg(String newValue) {
		UndoRecord undoRec=null;
		double d=0.0;
		if (field != null) {
			layout.clearError();
			fieldWidth.setStyle(null);
			boolean error=false;
			try {
				d = Double.parseDouble(newValue);
				double rightMargin = (template.getPaperWidth()/10 - template.getRightMargin());
				if ((field.getRelativeX()+d) > rightMargin) {
					error = true;
					layout.setError("Field outside right margin");
				}
			}
			catch (NumberFormatException e) {
				error=true;
				layout.setError("Field width not numeric");
			}
			if (error) {
				fieldWidth.setStyle("-fx-text-box-border: RED; -fx-focus-color:RED;");
			}
			else {
				if (field != null) {
					if (!undoManager.getUndoUnderway()) {
						Double beforeValue= field.getFieldWidth()/(Constants.LAYOUTDIVIDER*10.0);
						undoRec=undoManager.newRecord();
						undoRec.newAction(this,DetailType.FIELD, String.valueOf(beforeValue),String.valueOf(d),XmlWriter.LAYOUTWIDTH);
					}
					else
						fieldWidth.setText(String.valueOf(d));
					field.setFieldWidth(d*Constants.LAYOUTDIVIDER*10);
					template.setDirty(true);
					field.draw();
					field.drawSelected();
					if (!undoManager.getUndoUnderway()) 
						undoManager.addRecord(undoRec);
				}
			}
		}
	}	
	private void fieldHeightChg(String newValue) {
		UndoRecord undoRec=null;
		double d=0.0;
		if (field != null) {
			layout.clearError();
			fieldHeight.setStyle(null);
			boolean error=false;
			try {
				d = Double.parseDouble(newValue);
				if (field.getRelativeY()+d > field.getBanner().getCanvasHeight()/(Constants.LAYOUTDIVIDER*10.0)) {
					error=true;
					layout.setError("Field outside banner height");
				}
			}
			catch (NumberFormatException e) {
				error=true;
				layout.setError("Field width not numeric");
			}
			if (error) {
				fieldHeight.setStyle("-fx-text-box-border: RED; -fx-focus-color:RED;");
			}
			else {
				if (field != null) {
					if (!undoManager.getUndoUnderway()) {
						Double beforeValue= field.getFieldHeight()/(Constants.LAYOUTDIVIDER*10.0);
						undoRec=undoManager.newRecord();
						undoRec.newAction(this,DetailType.FIELD, String.valueOf(beforeValue),String.valueOf(d),XmlWriter.LAYOUTHEIGHT);
					}
					else
						fieldHeight.setText(String.valueOf(d));
					field.setFieldHeight(d*Constants.LAYOUTDIVIDER*10);
					template.setDirty(true);
					field.draw();
					field.drawSelected();
					if (!undoManager.getUndoUnderway()) 
						undoManager.addRecord(undoRec);
				}
			}
		}
	}
	private void positionXChg(String newValue) {
		UndoRecord undoRec=null;
		double d=0.0;
		if (field != null) {
			boolean error=false;
			try {
				d = Double.parseDouble(newValue);
				if (d < 0.0 || d> ((layout.getPaperWidth()/10.0)-field.getFieldWidth()/(Constants.LAYOUTDIVIDER*10.0))) {
					error=true;
					layout.setError("Field position outside page width");
				}
			}
			catch (NumberFormatException e) {
				error=true;
				layout.setError("Field width not numeric");
			}
			if (error) {
				positionX.setStyle("-fx-text-box-border: RED; -fx-focus-color:RED;");
			}
			else {
				layout.clearError();
				positionX.setStyle(null);
				if (field != null) {
					if (!undoManager.getUndoUnderway()) {
						Double beforeValue= field.getLayoutX()/(Constants.LAYOUTDIVIDER*10.0);
						undoRec=undoManager.newRecord();
						undoRec.newAction(this,DetailType.FIELD, String.valueOf(beforeValue),String.valueOf(d),XmlWriter.LAYOUTX);
					}
					field.move(d*Constants.LAYOUTDIVIDER*10,field.getY());
					template.setDirty(true);
					if (!undoManager.getUndoUnderway()) 
						undoManager.addRecord(undoRec);
				}
			}
		}
	}
	private void positionYChg(String newValue) {
		UndoRecord undoRec=null;
		double d=0.0;
		if (field != null) {
			boolean error=false;
			try {
				d = Double.parseDouble(newValue);
				if (d < 0.0 || d> ((layout.getPaperHeight()/10.0)-field.getFieldHeight()/(Constants.LAYOUTDIVIDER*10.0))) {
					error=true;
					layout.setError("Field position outside page height");
				}
			}
			catch (NumberFormatException e) {
				layout.setError("Field width not numeric");
				error=true;
			}
			if (error) {
				positionY.setStyle("-fx-text-box-border: RED; -fx-focus-color:RED;");
			}
			else {
				positionY.setStyle(null);
				layout.clearError();
				if (field != null) {
					if (!undoManager.getUndoUnderway()) {
						Double beforeValue= field.getLayoutY()/(Constants.LAYOUTDIVIDER*10.0);
						undoRec=undoManager.newRecord();
						undoRec.newAction(this,DetailType.FIELD, String.valueOf(beforeValue),String.valueOf(d),XmlWriter.LAYOUTY);
					}				
					field.move(field.getX(),d*Constants.LAYOUTDIVIDER*10);
					template.setDirty(true);
					if (!undoManager.getUndoUnderway()) 
						undoManager.addRecord(undoRec);
			}
			}
		}
	}
	@FXML
	private void blankAction(ActionEvent event) {
		blankChg(fieldBlank.isSelected());
	}
	private void blankChg(Boolean newValue) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.FIELD, String.valueOf(field.getBlank()),String.valueOf(newValue),XmlWriter.LAYOUTBLANK);
		}
		else
			fieldBlank.setSelected(newValue);
		field.setBlank(newValue);
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
		
	}
	private void layoutStylesChg(ReportStyle  newV) {
		if (newV == null)
			return;
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			undoRec=undoManager.newRecord();
			String beforeValue =field.getLayoutStyle()==null?null: field.getLayoutStyle().getName();
			String afterValue = newV==null||newV==defaultStyle?null:newV.getName();
			undoRec.newAction(this,DetailType.FIELD, beforeValue,afterValue,XmlWriter.LAYOUTSTYLE);
		}
		else 
			layoutStyles.getSelectionModel().select(newV);
		if (newV == defaultStyle)
			field.setLayoutStyle(null);
		else
			field.setLayoutStyle(newV);
		field.resetStyle();
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
	}
	@Override
	public void undo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("FieldDetailController", "undo", MRBDebug.DETAILED, "undo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.LAYOUTWIDTH:
				fieldWidthChg(detailRec.getBeforeValue());
				break;
			case XmlWriter.LAYOUTHEIGHT:
				fieldHeightChg(detailRec.getBeforeValue());
				break;
			case XmlWriter.LAYOUTX:
				positionXChg(detailRec.getBeforeValue());
				break;
			case XmlWriter.LAYOUTY:
				positionYChg(detailRec.getBeforeValue());
				break;
			case XmlWriter.LAYOUTBLANK:
				blankChg(Boolean.valueOf(detailRec.getBeforeValue()));
				break;
			case XmlWriter.LAYOUTSTYLE:
				String beforeValue = detailRec.getBeforeValue();
				if (beforeValue == null)
					layoutStylesChg(defaultStyle);
				else
					layoutStylesChg(template.findStyle(beforeValue));
				break;
			}
		}
				
	}
	@Override
	public void redo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("FieldDetailController", "redo", MRBDebug.DETAILED, "redo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.LAYOUTWIDTH:
				fieldWidthChg(detailRec.getAfterValue());
				break;
			case XmlWriter.LAYOUTHEIGHT:
				fieldHeightChg(detailRec.getAfterValue());
				break;
			case XmlWriter.LAYOUTX:
				positionXChg(detailRec.getAfterValue());
				break;
			case XmlWriter.LAYOUTY:
				positionYChg(detailRec.getAfterValue());
				break;
			case XmlWriter.LAYOUTBLANK:
				blankChg(Boolean.valueOf(detailRec.getAfterValue()));
				break;
			case XmlWriter.LAYOUTSTYLE:
				String afterValue = detailRec.getAfterValue();
				if (afterValue == null)
					layoutStylesChg(defaultStyle);
				else
					layoutStylesChg(template.findStyle(afterValue));
				break;
			}
		}
		
	}
}
