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
import com.moneydance.modules.features.reportwriter2.databeans.BeanAnnotations.BEANFIELDTYPE;
import com.moneydance.modules.features.reportwriter2.view.DetailPane;
import com.moneydance.modules.features.reportwriter2.view.FieldExpression;
import com.moneydance.modules.features.reportwriter2.view.controls.FieldLabel;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportFormat;
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
	private ComboBox<ReportFormat> patternCombo;
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
	private ComboBox<ReportStyle> layoutStyles;
	private FieldExpression fldExpression;
	private ReportLayout layoutField=null;
	private ReportField fldField=null;
	private LayoutPaneController layout;
	private ObservableList<ReportStyle>styleItems;
	private ObservableList<ReportFormat>formatItems;
	private ObservableList<ReportFormat>patternItems;
	private ReportStyle defaultStyle;
	private UndoLayoutManager undoManager = UndoLayoutManager.getInstance();
	private Constants.FieldDetailType fldType;
	private Boolean setField= false;
	public FieldDetailController(ReportTemplate template, LayoutPaneController layout, ReportLayout field) {
		super(Constants.FIELDDETAILFXML, template);
		Main.rwDebugInst.debugThread("FieldDetailController","const", MRBDebug.DETAILED, "Field Layout created");
		this.layout = layout;
		this.layoutField = field;
		fldType=Constants.FieldDetailType.LAYOUT;
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
		patternCombo.valueProperty().addListener(new ChangeListener<ReportFormat>() {
			@Override
			public void changed(ObservableValue<? extends ReportFormat> arg0, ReportFormat oldV, ReportFormat newV) {
				if (setField)
					return;
				if (patternCombo.isFocused()) {
					patternChanged(newV);
				}
			}
		});
		patternCombo.setCellFactory(listView->new FieldPatternListCell());
		patternCombo.setButtonCell(new FieldPatternListCell());

		fieldBlank.setDisable(false);
		fldField = field.getField();
		switch (fldField.getReportType()) {
		case DATABASE:
			patternCombo.setDisable(false);
			expBtn.setDisable(true);
			break;
		case LABEL:
			patternCombo.setDisable(true);
			expBtn.setDisable(true);
			break;
		case VARIABLE:
			expBtn.setDisable(false);
			patternCombo.setDisable(false);
			break;
		default:
			break;		
		}
		if (Main.loadedIcons.mathBookImg == null)
			expBtn.setText("Expression");
		else
			expBtn.setGraphic(new ImageView(Main.loadedIcons.mathBookImg));
	
		setLayoutFields(field);
	}
	public FieldDetailController(ReportTemplate template, LayoutPaneController layout, ReportField field) {
		super(Constants.FIELDDETAILFXML, template);
		Main.rwDebugInst.debugThread("FieldDetailController","const", MRBDebug.DETAILED, "Field created");
		this.layout = layout;
		this.layoutField=null;
		fldType=Constants.FieldDetailType.FIELD;
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
		fieldBlank.setDisable(true);
		layoutStyles.valueProperty().addListener((arg0, oldV, newV)-> {
			if (layoutStyles.isFocused())
				layoutStylesChg(newV);
		});
		switch (fldField.getReportType()) {
		case DATABASE:
			patternCombo.setDisable(false);
			expBtn.setDisable(true);
			break;
		case LABEL:
			patternCombo.setDisable(true);
			expBtn.setDisable(true);
			break;
		case VARIABLE:
			expBtn.setDisable(false);
			patternCombo.setDisable(false);
			break;
		default:
			break;
		
		}
		patternCombo.valueProperty().addListener(new ChangeListener<ReportFormat>() {
			@Override
			public void changed(ObservableValue<? extends ReportFormat> arg0,ReportFormat oldV, ReportFormat newV) {
				if (setField)
					return;
				if (patternCombo.isFocused()) {
					patternChanged(newV);
				}				
			}
		});
		patternCombo.setCellFactory(listView->new FieldPatternListCell());
		patternCombo.setButtonCell(new FieldPatternListCell());


		if (Main.loadedIcons.mathBookImg == null)
			expBtn.setText("Expression");
		else
			expBtn.setGraphic(new ImageView(Main.loadedIcons.mathBookImg));
		setFieldFields(field);
	}
	public void setFieldFields(ReportField field) {
		this.fldField = field;
		setField=true;
		positionX.setText("");
		positionY.setText("");
		fieldHeight.setText("");
		fieldWidth.setText("");
		fieldBlank.setSelected(false);
		fieldName.setText(fldField.getName());
		switch (fldField.getReportType()){
		case DATABASE:
			fieldType.setText(fldField.getReportType().toString()+"/"+fldField.getFieldType().toString());
			break;
		case LABEL:
			fieldType.setText(fldField.getReportType().toString());
			break;
		case VARIABLE:
			String type = fldField.getOutputType()==Constants.OUTPUTTYPE.NUMBER?"NUMBER": fldField.getOutputType()==Constants.OUTPUTTYPE.TEXT?"TEXT":"DATE";
			fieldType.setText(fldField.getReportType().toString()+"/"+type);
			break;
		default:
			break;
			
		}
		fieldBanner.setText("");
		fieldText.setText(fldField.getFieldText());
		if (field.getReportType() == ReportFieldType.DATABASE)
			fieldSource.setText(field.getFieldBean().getTableName());
		else
			fieldSource.setText("");
		patternItems = FXCollections.observableArrayList(template.getFormats().values());
		patternCombo.setItems(patternItems);
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
		if (field.getFormat() != null) {
			switch (field.getReportType()) {
			case DATABASE:
				patternCombo.getSelectionModel().select(field.getFormat());
				break;
			case LABEL:
				break;
			case VARIABLE:
				patternCombo.getSelectionModel().select(field.getFormat());
				fieldExp.setText(field.getFieldExp());
				break;
			default:
				break;
			
			}
		}
		if (ReportFieldType.VARIABLE.equals(field.getReportType())) {
				fieldExp.setText(field.getFieldExp());
		}
		setField=false;

	}
	public void setLayoutFields(ReportLayout field) {
		this.layoutField = field;
		setField=true;
		patternItems = FXCollections.observableArrayList(template.getFormats().values());
		patternCombo.setItems(patternItems);

		fldField = field.getField();
		if (this.layoutField==null) {
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
		}
		else {
			fieldName.setText(this.layoutField.getName());
			switch (fldField.getReportType()){
			case DATABASE:
				fieldType.setText(fldField.getReportType().toString()+"/"+fldField.getFieldType().toString());
				break;
			case LABEL:
				fieldType.setText(fldField.getReportType().toString());
				break;
			case VARIABLE:
				String type = fldField.getOutputType()==Constants.OUTPUTTYPE.NUMBER?"NUMBER": fldField.getOutputType()==Constants.OUTPUTTYPE.TEXT?"TEXT":"DATE";
				fieldType.setText(fldField.getReportType().toString()+"/"+type);
				break;
			default:
				break;
				
			}
			fieldBanner.setText(this.layoutField.getBanner().getName());
			positionX.setText(String.valueOf(this.layoutField.getX()/(Constants.LAYOUTDIVIDER*10)));
			positionY.setText(String.valueOf(this.layoutField.getY()/(Constants.LAYOUTDIVIDER*10)));
			fieldWidth.setText(String.valueOf(this.layoutField.getFieldWidth()/(Constants.LAYOUTDIVIDER*10)));
			fieldHeight.setText(String.valueOf(this.layoutField.getFieldHeight()/(Constants.LAYOUTDIVIDER*10)));
			fieldBlank.setSelected(this.layoutField.getBlank());
			fieldText.setText(this.layoutField.getText());
			this.layoutField.getXProperty().addListener((t->{
				positionX.setText(String.valueOf(this.layoutField.getX()/(Constants.LAYOUTDIVIDER*10)));				
			}));
			this.layoutField.getYProperty().addListener((t->{
				positionY.setText(String.valueOf(this.layoutField.getY()/(Constants.LAYOUTDIVIDER*10)));				
			}));
			if (this.layoutField.getField().getReportType()==ReportFieldType.DATABASE)
				fieldSource.setText(this.layoutField.getField().getFieldBean().getTableName());
			else
				fieldSource.setText("");
			if(this.layoutField.getField().getReportType().equals(ReportFieldType.VARIABLE)) {
				fieldExp.setText(this.layoutField.getField().getFieldExp());
			}
			if (this.layoutField.getFormat()!=null)
				patternCombo.getSelectionModel().select(this.layoutField.getFormat());
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
		setField=false;
	}
	public void resetStyles() {
		styleItems=FXCollections.observableArrayList();
		styleItems.add(null);
		styleItems.addAll(template.getStyles().values());
		layoutStyles.setItems(styleItems);
		layoutStyles.getSelectionModel().select(layoutField.getLayoutStyle());
	}
	public void resetFormats() {
		formatItems=FXCollections.observableArrayList();
		formatItems.add(null);
		formatItems.addAll(template.getFormats().values());
		layoutStyles.setItems(styleItems);
		layoutStyles.getSelectionModel().select(layoutField.getLayoutStyle());
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
		expChanged(fldField.getFieldExp());
	}
	private void fieldWidthChg(String newValue) {
		UndoRecord undoRec=null;
		double d=0.0;
		if (layoutField != null) {
			layout.clearError();
			fieldWidth.setStyle(null);
			boolean error=false;
			try {
				d = Double.parseDouble(newValue);
				double rightMargin = (template.getPaperWidth()/10 - template.getRightMargin());
				if ((layoutField.getRelativeX()+d) > rightMargin) {
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
				if (layoutField != null) {
					if (!undoManager.getUndoUnderway()) {
						Double beforeValue= layoutField.getFieldWidth()/(Constants.LAYOUTDIVIDER*10.0);
						undoRec=undoManager.newRecord();
						undoRec.newAction(this,DetailType.FIELD, String.valueOf(beforeValue),String.valueOf(d),XmlWriter.LAYOUTWIDTH);
					}
					else
						fieldWidth.setText(String.valueOf(d));
					layoutField.setFieldWidth(d*Constants.LAYOUTDIVIDER*10);
					template.setDirty(true);
					layoutField.draw();
					layoutField.drawSelected();
					if (!undoManager.getUndoUnderway()) 
						undoManager.addRecord(undoRec);
				}
			}
		}
	}	
	private void fieldHeightChg(String newValue) {
		UndoRecord undoRec=null;
		double d=0.0;
		if (layoutField != null) {
			layout.clearError();
			fieldHeight.setStyle(null);
			boolean error=false;
			try {
				d = Double.parseDouble(newValue);
				if (layoutField.getRelativeY()+d > layoutField.getBanner().getCanvasHeight()/(Constants.LAYOUTDIVIDER*10.0)) {
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
				if (layoutField != null) {
					if (!undoManager.getUndoUnderway()) {
						Double beforeValue= layoutField.getFieldHeight()/(Constants.LAYOUTDIVIDER*10.0);
						undoRec=undoManager.newRecord();
						undoRec.newAction(this,DetailType.FIELD, String.valueOf(beforeValue),String.valueOf(d),XmlWriter.LAYOUTHEIGHT);
					}
					else
						fieldHeight.setText(String.valueOf(d));
					layoutField.setFieldHeight(d*Constants.LAYOUTDIVIDER*10);
					template.setDirty(true);
					layoutField.draw();
					layoutField.drawSelected();
					if (!undoManager.getUndoUnderway()) 
						undoManager.addRecord(undoRec);
				}
			}
		}
	}
	private void positionXChg(String newValue) {
		UndoRecord undoRec=null;
		double d=0.0;
		if (layoutField != null) {
			boolean error=false;
			try {
				d = Double.parseDouble(newValue);
				if (d < 0.0 || d> ((layout.getPaperWidth()/10.0)-layoutField.getFieldWidth()/(Constants.LAYOUTDIVIDER*10.0))) {
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
				if (layoutField != null) {
					if (!undoManager.getUndoUnderway()) {
						Double beforeValue= layoutField.getLayoutX()/(Constants.LAYOUTDIVIDER*10.0);
						undoRec=undoManager.newRecord();
						undoRec.newAction(this,DetailType.FIELD, String.valueOf(beforeValue),String.valueOf(d),XmlWriter.LAYOUTX);
					}
					layoutField.move(d*Constants.LAYOUTDIVIDER*10,layoutField.getY());
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
		if (layoutField != null) {
			boolean error=false;
			try {
				d = Double.parseDouble(newValue);
				if (d < 0.0 || d> ((layout.getPaperHeight()/10.0)-layoutField.getFieldHeight()/(Constants.LAYOUTDIVIDER*10.0))) {
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
				if (layoutField != null) {
					if (!undoManager.getUndoUnderway()) {
						Double beforeValue= layoutField.getLayoutY()/(Constants.LAYOUTDIVIDER*10.0);
						undoRec=undoManager.newRecord();
						undoRec.newAction(this,DetailType.FIELD, String.valueOf(beforeValue),String.valueOf(d),XmlWriter.LAYOUTY);
					}				
					layoutField.move(layoutField.getX(),d*Constants.LAYOUTDIVIDER*10);
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
			undoRec.newAction(this,DetailType.FIELD, String.valueOf(layoutField.getBlank()),String.valueOf(newValue),XmlWriter.LAYOUTBLANK);
		}
		else
			fieldBlank.setSelected(newValue);
		layoutField.setBlank(newValue);
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
			String beforeValue =layoutField.getLayoutStyle()==null?null: layoutField.getLayoutStyle().getName();
			String afterValue = newV==null||newV==defaultStyle?null:newV.getName();
			undoRec.newAction(this,DetailType.FIELD, beforeValue,afterValue,XmlWriter.LAYOUTSTYLE);
		}
		else 
			layoutStyles.getSelectionModel().select(newV);
		if (fldType==Constants.FieldDetailType.LAYOUT) {
			if (newV == defaultStyle)
				layoutField.setLayoutStyle(null);
			else
				layoutField.setLayoutStyle(newV);
		}
		else {
			if (newV == defaultStyle)
				fldField.setLayoutStyle(null);
			else
				fldField.setLayoutStyle(newV);
		}
		layoutField.resetStyle();
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
	}
	private void expChanged(String newV) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			undoRec=undoManager.newRecord();
			String beforeValue =fieldExp.getText();
			String afterValue = newV;
			undoRec.newAction(this,DetailType.FIELD, beforeValue,afterValue,XmlWriter.SRCFLDEXP);
		}
		fieldExp.setText(fldField.getFieldExp());
		template.setDirty(true);
		if (!undoManager.getUndoUnderway())
			undoManager.addRecord(undoRec);			
	}
	private Boolean checkPatternCombo(Constants.FieldPattern pattern) {
		if (pattern==null)
			return true;
		Integer fieldPatternType = 0;
		ReportField tmpField = null;
		if (fldType==Constants.FieldDetailType.FIELD) 
			tmpField = fldField;
		else
			if (layoutField != null)
				tmpField = layoutField.getField();
		if (tmpField !=null) {
			switch (tmpField.getReportType()) {
			case DATABASE:
				fieldPatternType = fldField.getFieldType().getTypeNum();
				break;
			case LABEL:
				break;
			case VARIABLE:
				switch (fldField.getOutputType()) {
				case DATE:
					 fieldPatternType=BEANFIELDTYPE.DATEINT.getTypeNum();
					break;
				case NUMBER:
					 fieldPatternType=BEANFIELDTYPE.NUMERIC.getTypeNum();
					break;
				case TEXT:
					 fieldPatternType=BEANFIELDTYPE.STRING.getTypeNum();
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
		return pattern.isAllowed(fieldPatternType);
	}
	private void patternChanged(ReportFormat newV) {
		if (newV == null)
			return;
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			undoRec=undoManager.newRecord();
			String beforeValue =patternCombo.getSelectionModel().getSelectedItem().toString();
			String afterValue = newV==null?null:newV.toString();
			undoRec.newAction(this,DetailType.FIELD, beforeValue,afterValue,
					fldType==Constants.FieldDetailType.FIELD?XmlWriter.SRCFLDFORMAT:XmlWriter.LAYOUTFORMAT);
		}
		else 
			patternCombo.getSelectionModel().select(newV);
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
			case XmlWriter.LAYOUTFORMAT:
			case XmlWriter.SRCFLDFORMAT:
				patternChanged(template.getFormats().get( detailRec.getBeforeValue()));
				break;
			case XmlWriter.SRCFLDEXP:
				expChanged(detailRec.getBeforeValue());
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
			case XmlWriter.LAYOUTFORMAT:
			case XmlWriter.SRCFLDFORMAT:
				patternChanged(template.getFormats().get( detailRec.getAfterValue()));
				break;
			case XmlWriter.SRCFLDEXP:
				expChanged(detailRec.getAfterValue());
				break;

			}
		}
		
	}
	private static class FieldPatternListCell extends ListCell<ReportFormat> {
		@Override
		public void updateItem(ReportFormat item, boolean empty) {
			super.updateItem(item, empty);
			if (item !=null)
				setText(item.getName());
			else
				setText(null);
		}
	}
	
}
