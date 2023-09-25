package com.moneydance.modules.features.reportwriter2.view.screenctrl;

import java.util.List;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.edit.UndoLayoutManager;
import com.moneydance.modules.features.reportwriter2.edit.UndoRecord;
import com.moneydance.modules.features.reportwriter2.edit.UndoAction;
import com.moneydance.modules.features.reportwriter2.edit.UndoDetail.DetailType;
import com.moneydance.modules.features.reportwriter2.edit.UndoFieldDetail;
import com.moneydance.modules.features.reportwriter2.view.DetailPane;
import com.moneydance.modules.features.reportwriter2.view.controls.FieldLabel;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportBanner;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportStyle;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;


public class BannerDetailController extends DetailPane implements UndoAction {
	private ReportBanner banner;
	private UndoLayoutManager undoManager;
	private ObservableList<ReportStyle>styleItems;
	private ReportStyle defaultStyle;
	private LayoutPaneController layout;
	@FXML
	private Label bannerName;
	@FXML
	private Label bannerType;
	@FXML
	private CheckBox bannerNewPage;
	@FXML
	private CheckBox bannerRepeat;
	@FXML
	private CheckBox bannerTopLine;
	@FXML
	private CheckBox bannerBotLine;
	@FXML
	private CheckBox bannerVisible;
	@FXML
	private TextField bannerHeight;
	@FXML
	private ComboBox<ReportStyle> bannerStyles;
	public BannerDetailController(ReportTemplate template, LayoutPaneController layout) {
		super(Constants.BANNERDETAILFXML,template);
		this.undoManager =UndoLayoutManager.getInstance();
		this.layout = layout;
		bannerHeight.focusedProperty().addListener((obs,oldValue,newValue)->{
			if (!newValue)
				bannerHeightChg(bannerHeight.getText());
		});
		bannerStyles.valueProperty().addListener(new ChangeListener<ReportStyle>() {
			@Override
			public void changed(ObservableValue<? extends ReportStyle> arg0, ReportStyle oldV, ReportStyle newV) {
				if (bannerStyles.isFocused())
					bannerStyleChg(newV);
			}
		});
	}
	public void setFields(ReportBanner banner) {
		this.banner = banner;
		bannerName.setText(banner.getName());
		bannerType.setText(banner.getBannerType().toString());
		bannerNewPage.setSelected(banner.isNewPage());
		bannerRepeat.setSelected(banner.isRepeat());
		bannerTopLine.setSelected(banner.isTopLine());
		bannerBotLine.setSelected(banner.isBottomLine());
		bannerVisible.setSelected(banner.isBannerVisible());
		bannerHeight.setText(String.valueOf(banner.getCanvasHeight()/(Constants.LAYOUTDIVIDER*10.0)));
		defaultStyle = new ReportStyle();
		defaultStyle.setName("Use Template Default");
		styleItems=FXCollections.observableArrayList();
		styleItems.add(defaultStyle);
		styleItems.addAll(template.getStyles().values());
		bannerStyles.setItems(styleItems);
		bannerStyles.setCellFactory(new Callback<ListView<ReportStyle>,ListCell<ReportStyle>>(){
			@Override
			public ListCell<ReportStyle> call(ListView<ReportStyle> style) {
				final ListCell<ReportStyle> cell = new ListCell<ReportStyle>() {
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
				return cell;
			}
			
		});
		bannerStyles.setButtonCell(new ListCell<ReportStyle>() {
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
		});
		if (banner.getBannerStyle()==null)
			bannerStyles.getSelectionModel().select(defaultStyle);
		else
			bannerStyles.getSelectionModel().select(banner.getBannerStyle());
		
		
	}
	public void resetStyles() {
		styleItems=FXCollections.observableArrayList();
		styleItems.add(null);
		styleItems.addAll(template.getStyles().values());
		bannerStyles.setItems(styleItems);
		bannerStyles.getSelectionModel().select(banner.getBannerStyle());
	}
	public void clearFields() {
		bannerName.setText("");
		bannerType.setText("");
		bannerNewPage.setSelected(false);
		bannerRepeat.setSelected(false);
		bannerTopLine.setSelected(false);
		bannerBotLine.setSelected(false);
		bannerHeight.setText("");
		bannerStyles.getSelectionModel().clearSelection();
	}
	@FXML
	private void checkBotLine(ActionEvent action) {
		checkBotLineChg(bannerBotLine.isSelected());
	}
	private void checkBotLineChg(Boolean botLine) {
		UndoRecord undoRec=null;
		if (banner != null) {
			if (!undoManager.getUndoUnderway()) {
				undoRec=undoManager.newRecord();
				undoRec.newAction(this,DetailType.BANNER, String.valueOf(banner.isBottomLine()),String.valueOf(botLine),XmlWriter.BANNERBOTTOMLINE);
			}
			else
				bannerBotLine.setSelected(botLine);
			banner.setBottomLine(botLine);
			template.setDirty(true);
			if (!undoManager.getUndoUnderway()) 
				undoManager.addRecord(undoRec);
		}
		
	}
	@FXML
	private void checkNewPage(ActionEvent action) {
		checkNewPageChg(bannerNewPage.isSelected());
	}
	private void checkNewPageChg(Boolean newPage) {
		UndoRecord undoRec=null;
		if (banner != null) {
			if (!undoManager.getUndoUnderway()) {
				undoRec=undoManager.newRecord();
				undoRec.newAction(this,DetailType.BANNER, String.valueOf(banner.isNewPage()),String.valueOf(newPage),XmlWriter.BANNERNEWPAGE);
			}
			else
				bannerNewPage.setSelected(newPage);
			banner.setNewPage(newPage);
			template.setDirty(true);
			if (!undoManager.getUndoUnderway()) 
				undoManager.addRecord(undoRec);
		}
	}
	@FXML
	private void checkRepeat(ActionEvent action) {
		checkRepeatChg(bannerRepeat.isSelected());
	}
	private void checkRepeatChg(Boolean repeat) {
		UndoRecord undoRec=null;
		if (banner != null) {
			if (!undoManager.getUndoUnderway()) {
				undoRec=undoManager.newRecord();
				undoRec.newAction(this,DetailType.BANNER, String.valueOf(banner.isRepeat()),String.valueOf(repeat),XmlWriter.BANNERREPEAT);
			}
			else
				bannerRepeat.setSelected(repeat);
			banner.setRepeat(repeat);
			template.setDirty(true);
			if (!undoManager.getUndoUnderway()) 
				undoManager.addRecord(undoRec);
		}
	}
	@FXML
	private void checkTopLine(ActionEvent action) {
		checkTopLineChg(bannerTopLine.isSelected());
	}
	private void checkTopLineChg(Boolean topLine) {
		UndoRecord undoRec=null;
		if (banner != null) {
			if (!undoManager.getUndoUnderway()) {
				undoRec=undoManager.newRecord();
				undoRec.newAction(this,DetailType.BANNER, String.valueOf(banner.isTopLine()),String.valueOf(topLine),XmlWriter.BANNERTOPLINE);
			}
			else
				bannerTopLine.setSelected(topLine);
			banner.setTopLine(topLine);
			template.setDirty(true);
			if (!undoManager.getUndoUnderway()) 
				undoManager.addRecord(undoRec);
		}
		
	}
	@FXML
	private void checkVisible(ActionEvent action) {
		checkVisibleChg(bannerVisible.isSelected());
	}
	private void checkVisibleChg(Boolean visible) {
		UndoRecord undoRec=null;
		if (banner !=null) {
			if (!undoManager.getUndoUnderway()) {
				undoRec=undoManager.newRecord();
				undoRec.newAction(this,DetailType.BANNER, String.valueOf(banner.isVisible()),String.valueOf(visible),XmlWriter.BANNERVISIBLE);
			}
			else
				bannerVisible.setSelected(visible);
			banner.setBannerVisible(visible);
			template.setDirty(true);
			if (!undoManager.getUndoUnderway()) 
				undoManager.addRecord(undoRec);
		}
	}
	private void bannerHeightChg(String newValue) {
		UndoRecord undoRec=null;
		double d=0.0;
		if (banner != null) {
			boolean error=false;
			try {
				d = Double.parseDouble(newValue)*Constants.LAYOUTDIVIDER*10.0;
				if (d < 0.0 || d> ((layout.getPaperHeight()*Constants.LAYOUTDIVIDER)-banner.getCanvasHeight())) {
					error=true;
					layout.setError("Banner Height outside page height");
				}
				if (d<banner.findLastField()) {
					error=true;
					layout.setError("Banner Height less than last field");
				}
			}
			catch (NumberFormatException e) {
				error=true;
				layout.setError("Banner Height not numeric");
			}
			if (error) {
				bannerHeight.setStyle("-fx-text-box-border: RED; -fx-focus-color:RED;");
			}
			else {
				if (!undoManager.getUndoUnderway()) {
					undoRec=undoManager.newRecord();
					undoRec.newAction(this,DetailType.BANNER, String.valueOf(banner.getCanvasHeight()/(Constants.LAYOUTDIVIDER*10.0)),String.valueOf(d/(Constants.LAYOUTDIVIDER*10.0)),XmlWriter.BANNERHEIGHT);
				}
				else 
					bannerHeight.setText(newValue);
				layout.clearError();
				bannerHeight.setStyle(null);
				banner.setCanvasHeight(d);
				layout.resizeBanners();
				template.setDirty(true);
				if (!undoManager.getUndoUnderway()) 
					undoManager.addRecord(undoRec);
				}
		}
	}
	private void bannerStyleChg(ReportStyle newV) {
		if (newV == null)
			return;
		UndoRecord undoRec=null;
		String beforeValue = banner.getBannerStyle()==null?null:banner.getBannerStyle().getName();
		String afterValue = newV==null||newV==defaultStyle?null:newV.getName();
		if (newV == defaultStyle)
			banner.setBannerStyle(null);
		else
			banner.setBannerStyle(newV);
		banner.resetStyle();
		if (!undoManager.getUndoUnderway()) {
			undoRec=undoManager.newRecord();
			undoRec.newAction(this,DetailType.BANNER,beforeValue,afterValue,XmlWriter.BANNERSTYLE);
		}
		else 
			bannerStyles.getSelectionModel().select(newV);
		if (!undoManager.getUndoUnderway()) 
			undoManager.addRecord(undoRec);
	}
	@Override
	public void undo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("BannerDetailController", "undo", MRBDebug.DETAILED, "undo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.BANNERNEWPAGE:
				Main.rwDebugInst.debugThread("BannerDetailController", "undo", MRBDebug.DETAILED, "New Page ");
				checkNewPageChg(Boolean.valueOf(detailRec.getBeforeValue()));
				break;
			case XmlWriter.BANNERREPEAT:
				Main.rwDebugInst.debugThread("BannerDetailController", "undo", MRBDebug.DETAILED, "Repeat ");
				checkRepeatChg(Boolean.valueOf(detailRec.getBeforeValue()));
				break;
			case XmlWriter.BANNERTOPLINE:
				Main.rwDebugInst.debugThread("BannerDetailController", "undo", MRBDebug.DETAILED, "Top Line ");
				checkTopLineChg(Boolean.valueOf(detailRec.getBeforeValue()));
				break;
			case XmlWriter.BANNERBOTTOMLINE:
				Main.rwDebugInst.debugThread("BannerDetailController", "undo", MRBDebug.DETAILED, "Bot Line ");
				checkBotLineChg(Boolean.valueOf(detailRec.getBeforeValue()));
				break;
			case XmlWriter.BANNERVISIBLE:
				Main.rwDebugInst.debugThread("BannerDetailController", "undo", MRBDebug.DETAILED, "Visible ");
				checkVisibleChg(Boolean.valueOf(detailRec.getBeforeValue()));
				break;
			case XmlWriter.BANNERHEIGHT:
				Main.rwDebugInst.debugThread("BannerDetailController", "undo", MRBDebug.DETAILED, "Height");
				bannerHeightChg(detailRec.getBeforeValue());
				break;
			case XmlWriter.BANNERSTYLE:
				Main.rwDebugInst.debugThread("BannerDetailController", "undo", MRBDebug.DETAILED, "Style");
				if (detailRec.getBeforeValue()==null)
					bannerStyleChg(defaultStyle);
				else
					bannerStyleChg(template.findStyle(detailRec.getBeforeValue()));
				break;
			}
		}
		
	}
	@Override
	public void redo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("BannerDetailController", "redo", MRBDebug.DETAILED, "redo being run ");
		for (UndoFieldDetail detailRec:changes) {
			switch (detailRec.getFieldName()) {
			case XmlWriter.BANNERNEWPAGE:
				checkNewPageChg(Boolean.valueOf(detailRec.getAfterValue()));
				break;
			case XmlWriter.BANNERREPEAT:
				Main.rwDebugInst.debugThread("BannerDetailController", "redo", MRBDebug.DETAILED, "Repeat ");
				checkRepeatChg(Boolean.valueOf(detailRec.getAfterValue()));
				break;
			case XmlWriter.BANNERTOPLINE:
				checkTopLineChg(Boolean.valueOf(detailRec.getAfterValue()));
				break;
			case XmlWriter.BANNERBOTTOMLINE:
				checkBotLineChg(Boolean.valueOf(detailRec.getAfterValue()));
				break;
			case XmlWriter.BANNERVISIBLE:
				checkVisibleChg(Boolean.valueOf(detailRec.getAfterValue()));
				break;
			case XmlWriter.BANNERHEIGHT:
				bannerHeightChg(detailRec.getAfterValue());
				break;
			case XmlWriter.BANNERSTYLE:
				if (detailRec.getAfterValue()==null)
					bannerStyleChg(defaultStyle);
				else
					bannerStyleChg(template.findStyle(detailRec.getAfterValue()));
				break;
			}
		}	
		
	}
}
