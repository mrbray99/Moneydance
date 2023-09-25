package com.moneydance.modules.features.reportwriter2.view.screenctrl;

import java.util.List;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.Utilities;
import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.edit.UndoLayoutManager;
import com.moneydance.modules.features.reportwriter2.edit.UndoRecord;
import com.moneydance.modules.features.reportwriter2.edit.UndoAction;
import com.moneydance.modules.features.reportwriter2.edit.UndoDetail.DetailType;
import com.moneydance.modules.features.reportwriter2.edit.UndoFieldDetail;
import com.moneydance.modules.features.reportwriter2.view.DetailPane;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportStyle;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.util.Callback;

public class StyleDetailController extends DetailPane implements UndoAction {
	private ReportStyle reportStyle;
	private ObservableList<String> fontWeights;
	private ObservableList<String> fontNames;
	private ObservableList<String> fontSizes;
	private boolean sizeError=false;
	private String buttonStyle = "-fx-background-color: "+Main.selectedButtonColour.toString().replace("0x","#");
	private UndoLayoutManager undoManager = UndoLayoutManager.getInstance();
	private LayoutPaneController layout;
	@FXML
	private TextField styleName;
	@FXML
	private ComboBox<String> fontWeight;
	@FXML
	private ComboBox<String> fontSize;
	@FXML
	private ComboBox<String> fontName;
	@FXML
	private Button italicBtn;
	@FXML
	private Button underlineBtn;
	@FXML
	private Button alignLeft;
	@FXML
	private Button alignCentre;
	@FXML
	private Button alignRight;
	@FXML
	private Button alignJustify;
	@FXML
	private ColorPicker colour;
	@FXML
	private Label sampleText;

	public StyleDetailController(ReportTemplate template, LayoutPaneController layout, ReportStyle  style) {
		super(Constants.STYLEDETAILFXML, template);
		this.layout = layout;
		reportStyle = style;
		fontNames = FXCollections.observableArrayList(Font.getFamilies());
		fontName.getItems().addAll(fontNames	);
		fontName.setCellFactory(new Callback<ListView<String>,ListCell<String>>(){
			@Override
			public ListCell<String> call(ListView<String> font) {
				return  new ListCell<String>() {
					@Override
					protected void updateItem(String t, boolean empty) {
						super.updateItem(t,empty);
						if (t==null || empty)
							setText(t);
						else {
							setText(t);
							setFont(new Font(t,10));
						}
					}
				};
			}
			
		});
		fontWeights = FXCollections.observableArrayList();
		fontWeights.addAll(Constants.FONTNORMAL,Constants.FONTBOLD);
		fontWeight.setItems(fontWeights);
		fontSizes = FXCollections.observableArrayList();
		fontSizes.addAll("8","9","10","11","12","14","16","18","20","22","24","26","28","36","48","72");
		fontSize.setItems(fontSizes);
		fontSize.setEditable(true);
		italicBtn.setText("I");
		Font textFont = italicBtn.getFont();
		textFont= Font.font(textFont.getName(), FontPosture.ITALIC, textFont.getSize());
		italicBtn.setFont(textFont);
		underlineBtn.setText("U");
		underlineBtn.setUnderline(true);
		setFields(style);
	}
	public void setFields(ReportStyle style) {
		this.reportStyle= style;
		styleName.setText(style.getName());
		styleName.setDisable(true);
		if (style.getFontName().isEmpty())
			fontName.getSelectionModel().select(Main.reportFont);
		else
			fontName.getSelectionModel().select(style.getFontName());
		if (style.getFontWeight().isEmpty())			
			fontWeight.getSelectionModel().select(Constants.FONTNORMAL);
		else
			fontWeight.getSelectionModel().select(style.getFontWeight());
		if (style.getFontSize() < 7)
			fontSize.getSelectionModel().select(Main.reportFontSize.toString());
		else
			fontSize.getSelectionModel().select(style.getFontSize().toString());
		if (style.isItalic())
			italicBtn.setStyle(buttonStyle);
		else
			italicBtn.setStyle(null);
		if (style.isUnderline())
			underlineBtn.setStyle(buttonStyle);
		else
			underlineBtn.setStyle(null);
		colour.setValue(style.getColour());
		setSampleText();
		
	}
	public void clearFields() {
		styleName.setText("");
		fontName.getSelectionModel().clearSelection();
		fontWeight.getSelectionModel().clearSelection();
		fontSize.getSelectionModel().clearSelection();
		italicBtn.setStyle(null);
		underlineBtn.setStyle(null);
		colour.setValue(Color.BLACK);
		sampleText.setText("");
	}
	@FXML
	private void selectFontName(ActionEvent action) {
		selectFontNameChg(fontName.getSelectionModel().getSelectedItem());
	}
	private void selectFontNameChg(String fontNameStr) {
		UndoRecord undoRec=null;
		if (fontNames.contains(fontNameStr)) {
			if (!undoManager.getUndoUnderway()) {
				String beforeValue=reportStyle.getFontName();
				undoRec=undoManager.newRecord();
				undoRec.newAction(this,DetailType.STYLE, beforeValue,fontNameStr,XmlWriter.STYLEFONT);
			}
			else
				fontName.getSelectionModel().select(fontNameStr);
			reportStyle.setFontName(fontNameStr);
			template.setDirty(true);
			if (!undoManager.getUndoUnderway()) 
				undoManager.addRecord(undoRec);
		}
		setSampleText();
	}

	@FXML
	private void selectFontWeight(ActionEvent action) {
		selectFontWeightChg(fontWeight.getSelectionModel().getSelectedItem());
	}
	private void selectFontWeightChg(String fontWeightStr) {
		UndoRecord undoRec=null;
		if (fontWeightStr != null) {
			if (fontWeights.contains(fontWeight.getSelectionModel().getSelectedItem())) {
				if (!undoManager.getUndoUnderway()) {
					String beforeValue=reportStyle.getFontWeight();
					undoRec=undoManager.newRecord();
					undoRec.newAction(this,DetailType.STYLE, beforeValue,fontWeightStr,XmlWriter.STYLEWEIGHT);
				}
				else
					fontWeight.getSelectionModel().select(fontWeightStr);
				reportStyle.setFontWeight(fontWeightStr);
				template.setDirty(true);
				if (!undoManager.getUndoUnderway()) 
					undoManager.addRecord(undoRec);
			}
		}
		setSampleText();
	}
	@FXML
	private void selectFontSize(ActionEvent action) {
		selectFontSizeChg(fontSize.getSelectionModel().getSelectedItem());
	}
	private void selectFontSizeChg(String fontSizeStr) {
		UndoRecord undoRec=null;
		Integer selectedItem=null;
		sizeError= false;
		if (fontSizeStr==null)
			return;
		try {
			selectedItem = Integer.parseInt(fontSizeStr);
		}
		catch (NumberFormatException e) {
			fontSize.getStyleClass().add("fielderror");
			fontSize.requestFocus();
			sizeError = true;
			return;
		}
		if (selectedItem> 7
				&& selectedItem < 73) {
			if (!undoManager.getUndoUnderway()) {
				Integer beforeValue=reportStyle.getFontSize();
				undoRec=undoManager.newRecord();
				undoRec.newAction(this,DetailType.STYLE, String.valueOf(beforeValue),fontSizeStr,XmlWriter.STYLESIZE);
			}
			else
				fontSize.getSelectionModel().select(fontSizeStr);
			
			reportStyle.setFontSize(selectedItem);
			template.setDirty(true);
			fontSize.getStyleClass().removeIf(style -> style.equals("fielderror"));
			if (!undoManager.getUndoUnderway()) 
				undoManager.addRecord(undoRec);
		}
		else {
			fontSize.getStyleClass().add("fielderror");
			fontSize.requestFocus();
			sizeError=true;
			return;
		}
		setSampleText();
	}

	@FXML
	private void selectItalic(ActionEvent action) {
		selectItalicChg(!reportStyle.isItalic());
	}
	private void selectItalicChg(Boolean italic) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			String beforeValue=reportStyle.isItalic()?"true":"false";
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.STYLE, beforeValue,italic?"true":"false",XmlWriter.STYLEITALIC);
		}
		reportStyle.setItalic(italic);
		if (reportStyle.isItalic())
			italicBtn.setStyle(buttonStyle);
		else
			italicBtn.setStyle(null);
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
		setSampleText();
	}

	@FXML
	private void selectUnderline(ActionEvent action) {
		selectUnderlineChg(!reportStyle.isUnderline());
	}
	private void selectUnderlineChg(Boolean underline) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			String beforeValue=reportStyle.isUnderline()?"true":"false";
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.STYLE, beforeValue,underline?"true":"false",XmlWriter.STYLEUNDERLINE);
		}

		reportStyle.setUnderline(!reportStyle.isUnderline());
		if (reportStyle.isUnderline())
			underlineBtn.setStyle(buttonStyle);
		else
			underlineBtn.setStyle(null);
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
		setSampleText();
	}
	@FXML
	private void selectAlignLeft(ActionEvent action) {
		
	}
	@FXML
	private void selectAlignCentre(ActionEvent action) {
		
	}
	@FXML
	private void selectAlignRight(ActionEvent action) {
		
	}
	@FXML
	private void selectAlignJustify(ActionEvent action) {
		
	}
	@FXML
	private void selectColour(ActionEvent action) {
		selectColourChg(colour.getValue());
	}
	private void selectColourChg(Color colourValue) {
		UndoRecord undoRec=null;
		if (!undoManager.getUndoUnderway()) {
			String beforeValue=Utilities.colorToWeb(reportStyle.getColour());
			String afterValue=Utilities.colorToWeb(colourValue);
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.STYLE, beforeValue,afterValue,XmlWriter.STYLECOLOUR);
		}
		else
			colour.setValue(colourValue);
		reportStyle.setColour(colour.getValue());
		template.setDirty(true);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
		setSampleText();
	}
	@FXML
	private void selectCloseBtn(ActionEvent action) {
		
	}
	private void setSampleText() {
		reportStyle.getDisplayText(sampleText, "Sample Text");
		layout.resetStyles();
	}
	@Override
	public void undo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("StyleDetailController", "undo", MRBDebug.DETAILED, "undo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.STYLEFONT:
				selectFontNameChg(detailRec.getBeforeValue());
				break;
			case XmlWriter.STYLEWEIGHT:
				selectFontWeightChg(detailRec.getBeforeValue());
				break;
			case XmlWriter.STYLESIZE:
				selectFontSizeChg(detailRec.getBeforeValue());
				break;
			case XmlWriter.STYLEITALIC:
				boolean italic = detailRec.getBeforeValue().equals("true")?true:false;
				selectItalicChg(italic);
				break;
			case XmlWriter.STYLEUNDERLINE:
				boolean underline = detailRec.getBeforeValue().equals("true")?true:false;
				selectUnderlineChg(underline);
				break;
			case XmlWriter.STYLECOLOUR:
				Color beforeValue = Color.valueOf(detailRec.getBeforeValue());
				selectColourChg(beforeValue);
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
				selectFontNameChg(detailRec.getAfterValue());
				break;
			case XmlWriter.STYLEWEIGHT:
				selectFontWeightChg(detailRec.getAfterValue());
				break;
			case XmlWriter.STYLESIZE:
				selectFontSizeChg(detailRec.getAfterValue());
				break;
			case XmlWriter.STYLEITALIC:
				boolean italic = detailRec.getAfterValue().equals("true")?true:false;
				selectItalicChg(italic);
				break;
			case XmlWriter.STYLEUNDERLINE:
				boolean underline = detailRec.getAfterValue().equals("true")?true:false;
				selectUnderlineChg(underline);
				break;
			case XmlWriter.STYLECOLOUR:
				Color afterValue = Color.valueOf(detailRec.getAfterValue());
				selectColourChg(afterValue);
				break;
			}
		}
		
		
	}
}
