package com.moneydance.modules.features.reportwriter2.view.screenctrl;

import java.util.List;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Constants.FieldAlign;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.edit.UndoAction;
import com.moneydance.modules.features.reportwriter2.edit.UndoFieldDetail;
import com.moneydance.modules.features.reportwriter2.edit.UndoLayoutManager;
import com.moneydance.modules.features.reportwriter2.edit.UndoRecord;
import com.moneydance.modules.features.reportwriter2.edit.UndoDetail.DetailType;
import com.moneydance.modules.features.reportwriter2.view.DetailPane;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportFormat;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

public class FormatDetailController extends DetailPane implements UndoAction{
	private ReportFormat reportFormat;
	private UndoLayoutManager undoManager = UndoLayoutManager.getInstance();
	private LayoutPaneController layout;
	@FXML
	private GridPane screenGrid;
	@FXML
	private Label formatName;
	@FXML
	private ComboBox<Constants.FieldPattern> formatType;
	@FXML
	private ComboBox<String> patternText;
	@FXML
	private ComboBox<Integer> numDecPlaces;
	@FXML
	private CheckBox useMMark;
	@FXML
	private CheckBox redNeg;
	@FXML
	private ComboBox<Constants.FieldAlign> alignment;
	@FXML
	private CheckBox useCurr;
	@FXML
	private Label plusExamp;
	@FXML
	private Label negExamp;
	private Boolean dirty=false;
	private ReportTemplate template;
	private ObservableList<String> patterns;


	public FormatDetailController(ReportTemplate template, LayoutPaneController layout, ReportFormat  format) {
		super(Constants.FORMATDETAILFXML, template);
		this.layout = layout;
		reportFormat = format;
		formatType.setCellFactory(listView->new FormatTypeListCell());
		formatType.setButtonCell(new FormatTypeListCell());
		setFields(format);
	}
	public void setFields(ReportFormat format) {
		reportFormat = format;
		formatName.setText(format.getName());
		ObservableList<Constants.FieldPattern> types = FXCollections.observableArrayList(Constants.FieldPattern.values());
		formatType.setItems(types);
		if (reportFormat.getFormatType()!=null)
			formatType.getSelectionModel().select(reportFormat.getFormatType());
		ObservableList<Integer> decPlaces = FXCollections.observableArrayList();
		for (int i=0;i<9;i++)
			decPlaces.add(Integer.valueOf(i));
		numDecPlaces.setItems(decPlaces);
		ObservableList<Constants.FieldAlign>alignCombo = FXCollections.observableArrayList(Constants.FieldAlign.values());
		alignment.setItems(alignCombo);
		numDecPlaces.getSelectionModel().select(format.getPatternDecPlaces());
		useMMark.setSelected(format.isUseMMark());
		redNeg.setSelected(format.isUseRedNeg());
		useCurr.setSelected(format.isUseCurrSign());
		if (format.getFieldAlign()==null)
			alignment.getSelectionModel().select(-1);
		else {
			switch (format.getFieldAlign()) {
			case CENTRE:
				alignment.getSelectionModel().select(1);
				break;
			case LEFT:
				alignment.getSelectionModel().select(0);
				break;
			case RIGHT:
				alignment.getSelectionModel().select(2);
				break;
			default:
				alignment.getSelectionModel().select(-1);
				break;
			}
		}
		if (reportFormat.getFormatType()!=null)
			setFieldDisable();
		else {
			formatType.setDisable(false);
			patternText.setDisable(true);
			useMMark.setDisable(true);
			useCurr.setDisable(true);
			redNeg.setDisable(true);
			alignment.setDisable(true);
			numDecPlaces.setDisable(true);
		}
		if (reportFormat.isDefaultFormat()) 
			formatType.setDisable(true);
	}
	private void setFieldDisable() {
		patternText.getItems().clear();
		switch (reportFormat.getFormatType()) {
		case CURRENCY:
			patterns = FXCollections.observableArrayList();
			String line1 = Main.baseCurrency.getPrefix()+"0"+Main.decimalChar+"00"+Main.baseCurrency.getSuffix();
			patterns.add(line1);
			String line2 = Main.baseCurrency.getPrefix()+"#"+Main.decimalChar+"00"+Main.baseCurrency.getSuffix();
			patterns.add(line2);
			patternText.setItems(patterns);
			if (reportFormat.getPatternText()!=null)
				patternText.getSelectionModel().select(reportFormat.getPatternText());
			patternText.setDisable(false);
			useMMark.setDisable(false);
			useCurr.setDisable(true);
			useCurr.setSelected(true);
			redNeg.setDisable(false);
			alignment.setDisable(true);
			alignment.getSelectionModel().select(FieldAlign.RIGHT);
			numDecPlaces.setDisable(false);
			break;
		case DATE:
			patterns = FXCollections.observableArrayList(Constants.DATEPATTERNS);
			patternText.setItems(patterns);
			if (reportFormat.getPatternText()!=null)
				patternText.getSelectionModel().select(reportFormat.getPatternText());			
			patternText.setDisable(false);
			useMMark.setDisable(true);
			useCurr.setDisable(true);
			redNeg.setDisable(true);
			alignment.setDisable(false);
			numDecPlaces.setDisable(true);
			break;
		case NUMBER:
			patterns = FXCollections.observableArrayList();
			String line3 = "0"+Main.decimalChar+"00";
			patterns.add(line3);
			String line4 = "#"+Main.decimalChar+"00";
			patterns.add(line4);
			patternText.setItems(patterns);
			if (reportFormat.getPatternText()!=null)
				patternText.getSelectionModel().select(reportFormat.getPatternText());
			patternText.setDisable(false);
			useMMark.setDisable(false);
			useCurr.setDisable(true);
			useCurr.setSelected(false);
			redNeg.setDisable(false);
			alignment.setDisable(false);
			alignment.getSelectionModel().select(FieldAlign.RIGHT);
			numDecPlaces.setDisable(false);
			break;
		case PERCENTAGE:
			patterns = FXCollections.observableArrayList();
			String line5 = "0"+Main.decimalChar+"00%";
			patterns.add(line5);
			String line6 = "#"+Main.decimalChar+"00%";
			patterns.add(line6);
			patternText.setItems(patterns);
			if (reportFormat.getPatternText()!=null)
				patternText.getSelectionModel().select(reportFormat.getPatternText());
			patternText.setDisable(false);
			useMMark.setDisable(false);
			useCurr.setDisable(true);
			useCurr.setSelected(false);
			redNeg.setDisable(false);
			alignment.setDisable(false);
			alignment.getSelectionModel().select(FieldAlign.RIGHT);
			numDecPlaces.setDisable(false);

			break;
		case TEXT:
			patternText.setDisable(true);
			useMMark.setDisable(true);
			useCurr.setDisable(true);
			redNeg.setDisable(true);
			alignment.setDisable(false);
			numDecPlaces.setDisable(true);
			break;
		case YESNO:
			patterns = FXCollections.observableArrayList(Constants.BOOLEANPATTERNS);
			patternText.setItems(patterns);
			if (reportFormat.getPatternText()!=null)
				patternText.getSelectionModel().select(reportFormat.getPatternText());
			patternText.setDisable(false);
			useMMark.setDisable(true);
			useCurr.setDisable(true);
			useCurr.setSelected(false);
			redNeg.setDisable(true);
			alignment.setDisable(false);
			numDecPlaces.setDisable(true);
			break;
		default:
			formatType.setDisable(false);
			patternText.setDisable(true);
			useMMark.setDisable(true);
			useCurr.setDisable(true);
			redNeg.setDisable(true);
			alignment.setDisable(false);
			numDecPlaces.setDisable(true);
			break;
		
		}
	}
	public void clearFields() {
		formatType.getSelectionModel().clearSelection();
		patternText.getSelectionModel().clearSelection();
		patternText.getItems().clear();
		numDecPlaces.getSelectionModel().clearSelection();
		useMMark.setSelected(false);
		redNeg.setSelected(false);
		useCurr.setSelected(false);
		alignment.getSelectionModel().clearSelection();
		setFieldDisable();
	}
	@FXML
	private void selectFormatType(ActionEvent action) {
		selectFormatTypeChg(formatType.getSelectionModel().getSelectedItem());
	}
	private void selectFormatTypeChg(Constants.FieldPattern formatType) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			String beforeValue="";
			if (reportFormat.getFormatType()==null)
				beforeValue = "";
			else
			     beforeValue=reportFormat.getFormatType().toString();
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.FORMAT, beforeValue,formatType.toString(),XmlWriter.FORTYPE);
		}
		reportFormat.setFormatType(formatType);
		setFieldDisable();
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
		dirty=true;
	}
	@FXML
	private void selectPatternText(ActionEvent action) {
		selectPatternTextChg(patternText.getSelectionModel().getSelectedItem());
	}
	private void selectPatternTextChg(String newPatternText) {
		UndoRecord undoRec=null;
		if (patterns.contains(newPatternText)) {
			if (!undoManager.getUndoUnderway()) {
				String beforeValue=reportFormat.getName();
				undoRec=undoManager.newRecord();
				undoRec.newAction(this,DetailType.FORMAT, beforeValue,newPatternText,XmlWriter.FORPATTEXT);
			}
			reportFormat.setPatternText(newPatternText);
			template.setDirty(true);
			if (!undoManager.getUndoUnderway()) 
				undoManager.addRecord(undoRec);
		}
		dirty=true;
	}

	@FXML
	private void selectDecPlaces(ActionEvent action) {
		selectDecPlacesChg(numDecPlaces.getSelectionModel().getSelectedIndex());
	}
	private void selectDecPlacesChg(Integer decIndex) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			Integer beforeValue=reportFormat.getPatternDecPlaces();
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.FORMAT, String.valueOf(beforeValue),String.valueOf(decIndex),XmlWriter.FORPATDEC);
		}
		reportFormat.setPatternDecPlaces(decIndex);
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
		dirty=true;
	}

	@FXML
	private void selectMMark(ActionEvent action) {
		selectMMarkChg(useMMark.isSelected());
	}
	private void selectMMarkChg(Boolean useMMark) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			String beforeValue=reportFormat.isUseMMark()?"true":"false";
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.FORMAT, beforeValue,useMMark?"true":"false",XmlWriter.FORPATMMARK);
		}
		reportFormat.setUseMMark(useMMark);
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
	}

	@FXML
	private void selectRedNeg(ActionEvent action) {
		selectRedNegChg(redNeg.isSelected());
	}
	private void selectRedNegChg(Boolean redNeg) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			String beforeValue=reportFormat.isUseRedNeg()?"true":"false";
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.FORMAT, beforeValue,redNeg?"true":"false",XmlWriter.FORPATREDNEG);
		}

		reportFormat.setUseRedNeg(redNeg);
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
	}
	@FXML
	private void selectAlignment(ActionEvent action) {
		selectAlignmentChg(alignment.getSelectionModel().getSelectedItem());
	}
	private void selectAlignmentChg(Constants.FieldAlign align) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			String beforeValue=reportFormat.getFieldAlign().toString();
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.FORMAT, beforeValue,align.toString(),XmlWriter.FORALIGN);
		}

		reportFormat.setFieldAlign(align);
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
	}
	@FXML
	private void selectCurr(ActionEvent action) {
		selectCurrChg(useCurr.isSelected());
		
	}
	private void selectCurrChg(Boolean useCurr) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			String beforeValue=reportFormat.isUseCurrSign()?"true":"false";
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.FORMAT, beforeValue,useCurr?"true":"false",XmlWriter.FORPATCURR);
		}

		reportFormat.setUseCurrSign(useCurr);
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
		
	}
	@Override
	public void undo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("StyleDetailController", "undo", MRBDebug.DETAILED, "undo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.FORPATTEXT:
				selectPatternTextChg(detailRec.getBeforeValue());
				break;
			case XmlWriter.FORPATDEC:
				selectDecPlacesChg(Integer.valueOf(detailRec.getBeforeValue()));
				break;
			case XmlWriter.FORPATMMARK:
				selectMMarkChg(detailRec.getBeforeValue().equals("true")?true:false);
				break;
			case XmlWriter.FORPATREDNEG:
				selectRedNegChg(detailRec.getBeforeValue().equals("true")?true:false);
				break;
			case XmlWriter.FORPATCURR:
				selectCurrChg(detailRec.getBeforeValue().equals("true")?true:false);
				break;
			case XmlWriter.FORALIGN:
				selectAlignmentChg(Constants.FieldAlign.valueOf(detailRec.getBeforeValue()));
				break;
			case XmlWriter.FORTYPE:
				selectFormatTypeChg(Constants.FieldPattern.valueOf(detailRec.getBeforeValue()));
				break;

			}
		}
		
	}
	@Override
	public void redo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("StyleDetailController", "undo", MRBDebug.DETAILED, "redo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.STYLEFONT:
			case XmlWriter.FORPATTEXT:
				selectPatternTextChg(detailRec.getAfterValue());
				break;
			case XmlWriter.FORPATDEC:
				selectDecPlacesChg(Integer.valueOf(detailRec.getAfterValue()));
				break;
			case XmlWriter.FORPATMMARK:
				selectMMarkChg(detailRec.getAfterValue().equals("true")?true:false);
				break;
			case XmlWriter.FORPATREDNEG:
				selectRedNegChg(detailRec.getAfterValue().equals("true")?true:false);
				break;
			case XmlWriter.FORPATCURR:
				selectCurrChg(detailRec.getAfterValue().equals("true")?true:false);
				break;
			case XmlWriter.FORALIGN:
				selectAlignmentChg(Constants.FieldAlign.valueOf(detailRec.getAfterValue()));
				break;
			case XmlWriter.FORTYPE:
				selectFormatTypeChg(Constants.FieldPattern.valueOf(detailRec.getAfterValue()));
				break;
			}
		}
		
		
	}
	private static class FormatTypeListCell extends ListCell<Constants.FieldPattern> {
		@Override
		public void updateItem(Constants.FieldPattern item, boolean empty) {
			super.updateItem(item, empty);
			if (item !=null)
				setText(item.getName());
			else
				setText(null);
		}
	}
}
