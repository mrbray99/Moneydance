/*
 * Copyright (c) 2021, Michael Bray. All rights reserved.
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
 */
package com.moneydance.modules.features.reportwriter2.view.screenctrl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Constants.BannerType;
import com.moneydance.modules.features.reportwriter2.Constants.FieldFunction;
import com.moneydance.modules.features.reportwriter2.Constants.NodeType;
import com.moneydance.modules.features.reportwriter2.Constants.ReportFieldType;
import com.moneydance.modules.features.reportwriter2.FieldSelectListener;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.XmlWriter;
import com.moneydance.modules.features.reportwriter2.edit.UndoLayoutManager;
import com.moneydance.modules.features.reportwriter2.edit.UndoAction;
import com.moneydance.modules.features.reportwriter2.edit.UndoDetail.DetailType;
import com.moneydance.modules.features.reportwriter2.edit.UndoFieldDetail;
import com.moneydance.modules.features.reportwriter2.edit.UndoRecord;
import com.moneydance.modules.features.reportwriter2.selection.BannerSelectModel;
import com.moneydance.modules.features.reportwriter2.selection.FieldSelectModel;
import com.moneydance.modules.features.reportwriter2.selection.FormatSelectModel;
import com.moneydance.modules.features.reportwriter2.selection.LabelSelectModel;
import com.moneydance.modules.features.reportwriter2.selection.StyleSelectModel;
import com.moneydance.modules.features.reportwriter2.selection.TreeSelectionModel;
import com.moneydance.modules.features.reportwriter2.view.FieldPane;
import com.moneydance.modules.features.reportwriter2.view.LayoutPane;
import com.moneydance.modules.features.reportwriter2.view.PopUpScreen;
import com.moneydance.modules.features.reportwriter2.view.controls.DumbSplitPaneSkin;
import com.moneydance.modules.features.reportwriter2.view.controls.HorizontalRuler;
import com.moneydance.modules.features.reportwriter2.view.controls.LayoutTreeNode;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportBanner;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportFormat;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportLayout;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportStyle;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Callback;

/**
 * The interface between the FXML design and the Screen class. Acts as a data
 * handler for the FXML file
 * <p>
 * 
 * @author Mike Bray
 * @version {@Main#minorBuildNo}
 *
 */
public class LayoutPaneController implements FieldSelectListener, UndoAction {
	/**
	 * Internal variables
	 */
	private List<ReportBanner> banners;
	private SortedMap<String, ReportField> selectedFields;
	private SortedMap<String, ReportField> labels;
	private SortedMap<String, ReportField> variables;
	private ReportTemplate template;
	private BannerSelectModel<ReportBanner> bannerSelectModel;
	private FieldSelectModel<ReportLayout> fieldSelectModel;
	private LabelSelectModel<ReportField> labelSelectModel;
	private FormatSelectModel<ReportFormat> formatSelectModel;
	private StyleSelectModel<ReportStyle> styleSelectModel;
	private ObservableList<String> paperSizesList;
	private ObservableList<String> pageOrientationList;
	private SortedMap<Integer, ReportBanner> allBanners = new TreeMap<>();
	private SortedMap<String, ReportFormat> formats;
	private SortedMap<String, ReportStyle> styles;
	private ObservableList<SplitPane.Divider> dividers;
	private TreeSelectionModel treeSelectModel;
	private double paperWidth = 0.0;
	private double paperHeight = 0.0;
	private DoubleProperty layoutWidth;
	private Line leftMarginLine;
	private Line rightMarginLine;
	private ReportBanner crntBanner = null;
	public HorizontalRuler ruler;
	private Callback<String, Integer> callingPane;
	private double lowestX;
	private double lowestY;
	private double highestX;
	private double highestY;
	private double fieldsWidth;
	private double fieldsHeight;
	private boolean allSelected = false;
	private TreeItem<LayoutTreeNode> rootNode;
	private TreeItem<LayoutTreeNode> fieldNode;
	private TreeItem<LayoutTreeNode> styleNode;
	private TreeItem<LayoutTreeNode> bannerNode;
	private TreeItem<LayoutTreeNode> databaseNode;
	private TreeItem<LayoutTreeNode> labelsNode;
	private TreeItem<LayoutTreeNode> labelNode;
	private TreeItem<LayoutTreeNode> variablesNode;
	private TreeItem<LayoutTreeNode> formatsNode;
	private SortedMap<String, TreeItem<LayoutTreeNode>> recordNodes;
	private LayoutPaneController thisObj;
	private FieldPane fieldsPane;
	private Boolean multiSelection = false;
	private UndoLayoutManager undoManager;
	private boolean showDefaultStyle=true;
	private boolean showDefaultFormat=true;
	/**
	 * Panel fields
	 * 
	 * main containers
	 */
	@FXML
	private SplitPane paneBox;
	@FXML
	private VBox fieldsBox;
	@FXML
	private VBox layoutBox;
	@FXML
	private VBox bannerBox;
	/**
	 * 
	 * Page variables
	 */
	@FXML
	private GridPane pageDetailPane;
	@FXML
	private ComboBox<String> paperSizes;
	@FXML
	private ComboBox<String> pageOrientation;
	@FXML
	private TextField topMargin;
	@FXML
	private TextField leftMargin;
	@FXML
	private TextField rightMargin;
	@FXML
	private TextField bottomMargin;
	/**
	 * Fields variables
	 */
	@FXML
	private TreeView<LayoutTreeNode> fieldsList;

	@FXML
	private HBox alignBox;
	@FXML
	private Button leftAlign;
	@FXML
	private Button topAlign;
	@FXML
	private Button rightAlign;
	@FXML
	private Button bottomAlign;
	@FXML
	private HBox spaceBox;
	@FXML
	private Button horSpace;
	@FXML
	private Button verSpace;
	@FXML
	private HBox otherBox;
	@FXML
	private Button delete;
	@FXML
	private Button selectAll;
	/**
	 * Layout variables
	 */
	@FXML
	private ScrollPane layoutScroll;
	@FXML
	private AnchorPane bannerLayout;
	/**
	 * detail pane
	 */
	@FXML
	private VBox detailHoldingBox;
	@FXML
	private ScrollPane detailScroll;
	@FXML
	private AnchorPane detailPane1;
	@FXML
	private AnchorPane detailPane2;
	@FXML
	private AnchorPane detailPane3;
	@FXML
	private AnchorPane detailPane4;
	/**
	 * Buttons
	 */
	@FXML
	private Button okBtn;
	@FXML
	private Label statusBar;

	public LayoutPaneController() {
		thisObj = this;
		undoManager = UndoLayoutManager.getInstance();
	}
	/*
	 * Sets the data from the template, create banner view
	 * 
	 * @param template the ReportTemplate containing all of the layout data
	 * 
	 * @param callingPane the main Screen class, used to send signals back
	 * 
	 * @return null
	 */

	public void setFields(ReportTemplate template, Callback<String, Integer> callingPane) {
		this.template = template;
		this.callingPane = callingPane;
		bannerSelectModel = new BannerSelectModel<ReportBanner>();
		fieldSelectModel = new FieldSelectModel<ReportLayout>();
		labelSelectModel = new LabelSelectModel<ReportField>();
		formatSelectModel = new FormatSelectModel<ReportFormat>();
		styleSelectModel = new StyleSelectModel<ReportStyle>();
		layoutWidth = new SimpleDoubleProperty();
		/*
		 * Handle changes in screen size
		 */
		bannerLayout.heightProperty().addListener((ov, oldVal, newVal) -> {
			double paneHeight = newVal.doubleValue();
			if (leftMarginLine != null)
				leftMarginLine.setEndY(paneHeight - 10.0);
			if (rightMarginLine != null)
				rightMarginLine.setEndY(paneHeight - 10.0);
		});
		/*
		 * Handle user changing dividers between the three boxes
		 */
		dividers = paneBox.getDividers();
		dividers.get(0).positionProperty().addListener((ov, oldVal, newVal) -> {
			Main.preferences.put(Constants.PROGRAMNAME + "." + Constants.LAYOUTCOL1, newVal);
			Main.preferences.isDirty();
		});
		dividers.get(1).positionProperty().addListener((ov, oldVal, newVal) -> {
			Main.preferences.put(Constants.PROGRAMNAME + "." + Constants.LAYOUTCOL2, newVal);
			Main.preferences.isDirty();
		});
		/*
		 * paper sizes
		 * 
		 */
		paperSizesList = FXCollections.observableArrayList(Constants.PaperSizes);
		paperSizes.setItems(paperSizesList);
		paperSizes.getSelectionModel().select(template.getPaperSize().toString());
		pageOrientationList = FXCollections.observableArrayList(Constants.PageOrientations);
		pageOrientation.setItems(pageOrientationList);
		pageOrientation.getSelectionModel().select(template.getOrientation().toString());
		/*
		 * pageOrientation.focusedProperty().addListener((obs, oldValue, newValue) -> {
		 * if (Boolean.FALSE.equals(newValue))
		 * selectOrientationChg(pageOrientation.getSelectionModel().getSelectedItem(),
		 * true); });
		 */

		setPageSize();
		/*
		 * page margins
		 * 
		 */
		topMargin.setText(String.format("%2.1f", template.getTopMargin()));
		topMargin.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (Boolean.FALSE.equals(newValue))
				topMarginChg(topMargin.getText());
		});
		topMargin.addEventFilter(KeyEvent.ANY, e -> {
			if (e.getCode() == KeyCode.Z && e.isShortcutDown()) {
				e.consume();
				undo();
			}
			if (e.getCode() == KeyCode.Y && e.isShortcutDown()) {
				e.consume();
				redo();
			}
		});
		leftMargin.setText(String.format("%2.1f", template.getLeftMargin()));
		leftMargin.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (Boolean.FALSE.equals(newValue))
				leftMarginChg(leftMargin.getText());
		});
		leftMargin.addEventFilter(KeyEvent.ANY, e -> {
			if (e.getCode() == KeyCode.Z && e.isShortcutDown()) {
				e.consume();
				undo();
			}
			if (e.getCode() == KeyCode.Y && e.isShortcutDown()) {
				e.consume();
				redo();
			}
		});
		rightMargin.setText(String.format("%2.1f", template.getRightMargin()));
		rightMargin.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (Boolean.FALSE.equals(newValue))
				rightMarginChg(rightMargin.getText());
		});
		rightMargin.addEventFilter(KeyEvent.ANY, e -> {
			if (e.getCode() == KeyCode.Z && e.isShortcutDown()) {
				e.consume();
				undo();
			}
			if (e.getCode() == KeyCode.Y && e.isShortcutDown()) {
				e.consume();
				redo();
			}
		});
		bottomMargin.setText(String.format("%2.1f", template.getBottomMargin()));
		bottomMargin.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (Boolean.FALSE.equals(newValue))
				bottomMarginChg(bottomMargin.getText());
		});
		bottomMargin.addEventFilter(KeyEvent.ANY, e -> {
			if (e.getCode() == KeyCode.Z && e.isShortcutDown()) {
				e.consume();
				undo();
			}
			if (e.getCode() == KeyCode.Y && e.isShortcutDown()) {
				e.consume();
				redo();
			}
		});
		/*
		 * 
		 * set up field tree
		 */
		banners = template.getBanners();
		formats= template.getFormats();
		styles = template.getStyles();
		selectedFields = template.getSelectedFields();
		labels = template.getLabels();
		variables = template.getVariables();
		fieldsList.getStyleClass().add("myTree");
		fieldsList.setCellFactory((TreeView<LayoutTreeNode> arg0) -> {
			return new FieldTreeCell();
		});
		treeSelectModel = new TreeSelectionModel(fieldsList.getSelectionModel(), fieldsList, this);
		fieldsList.setSelectionModel(treeSelectModel);
		rootNode = new TreeItem<>(new LayoutTreeNode("Outline", NodeType.ROOT));
		rootNode.getValue().setItem(rootNode);
		fieldNode = new TreeItem<>(new LayoutTreeNode("Available fields", NodeType.AVAILABLEFIELDS));
		fieldNode.getValue().setItem(fieldNode);
		databaseNode = new TreeItem<>(new LayoutTreeNode("Database Fields", NodeType.DATABASE));
		databaseNode.getValue().setItem(databaseNode);
		variablesNode = new TreeItem<>(new LayoutTreeNode("Variables", NodeType.VARIABLES));
		variablesNode.getValue().setItem(variablesNode);
		bannerNode = new TreeItem<>(new LayoutTreeNode("Banners", NodeType.BANNERS));
		bannerNode.getValue().setItem(bannerNode);
		styleNode = new TreeItem<>(new LayoutTreeNode("Styles", NodeType.STYLES));
		styleNode.getValue().setItem(styleNode);
		labelsNode = new TreeItem<>(new LayoutTreeNode("Labels", NodeType.LABELS));
		labelsNode.getValue().setItem(labelNode);
		formatsNode=new TreeItem<>(new LayoutTreeNode("Formats", NodeType.FORMATS));
		formatsNode.getValue().setItem(formatsNode);
		fieldsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		fieldsList.setRoot(rootNode);
		TreeItem<LayoutTreeNode> treeRecord;
		recordNodes = new TreeMap<>();
		rootNode.getChildren().addAll(fieldNode, bannerNode, formatsNode,styleNode);
		fieldNode.getChildren().addAll(databaseNode, labelsNode, variablesNode);
		for (ReportField field : selectedFields.values()) {
			if (recordNodes.containsKey(field.getFieldBean().getShortTableName()))
				treeRecord = recordNodes.get(field.getFieldBean().getShortTableName());
			else {
				treeRecord = new TreeItem<>(
						new LayoutTreeNode(field.getFieldBean().getScreenTitle(), NodeType.RECORD));
				treeRecord.getValue().setItem(treeRecord);
				databaseNode.getChildren().add(treeRecord);
				recordNodes.put(field.getFieldBean().getShortTableName(), treeRecord);
				labelSelectModel.addField(field);
			}
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode(field));
			field.setTreeItem(fieldItem);
			treeRecord.getChildren().add(fieldItem);
		}
		for (ReportField label : labels.values()) {
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
			fieldItem.getValue().setField(label);
			label.setTreeItem(fieldItem);
			fieldItem.getValue().setNodeType(NodeType.LABEL);
			labelsNode.getChildren().add(fieldItem);
			labelSelectModel.addField(label);

		}
		for (ReportField variable : variables.values()) {
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
			fieldItem.getValue().setField(variable);
			variable.setTreeItem(fieldItem);
			fieldItem.getValue().setNodeType(NodeType.VARIABLE);
			variablesNode.getChildren().add(fieldItem);
			labelSelectModel.addField(variable);

		}
		fieldsList.prefWidthProperty().bind(fieldsBox.widthProperty().subtract(5.0));
		fieldsList.minWidthProperty().bind(pageDetailPane.widthProperty());
		layoutScroll.prefHeightProperty().bind(paneBox.heightProperty().subtract(50));
		ruler = new HorizontalRuler(template.getPaperSize(), template.getOrientation());
		ruler.drawRuler();
		ruler.setLayoutX(0);
		ruler.setLayoutY(0);
		ruler.setTopMargin(template.getTopMargin());
		ruler.setLeftMargin(template.getLeftMargin());
		ruler.setRightMargin(template.getRightMargin());
		ruler.setBottomMargin(template.getBottomMargin());
		bannerLayout.getChildren().add(ruler);
		formatSelectModel.setController(thisObj);
		styleSelectModel.setController(thisObj);
		if (banners != null) {
			for (ReportBanner banner : banners) {
				allBanners.put(banner.getPosition(), banner);
				banner.setReportBanner(template, bannerSelectModel, fieldSelectModel, thisObj);
				insertBanner(banner);
				banner.prefWidthProperty().bind(layoutWidth);
				banner.draw();
				banner.drawFields();
			}
			double crntY = ruler.getRulerHeight();
			for (ReportBanner tmpBanner : allBanners.values()) {
				tmpBanner.moveBanner(0, crntY);
				tmpBanner.reset(bannerLayout.getWidth());
				crntY += tmpBanner.getCanvasHeight();
			}
			for (ReportBanner banner : allBanners.values()) {
				TreeItem<LayoutTreeNode> bannerItem = new TreeItem<LayoutTreeNode>(new LayoutTreeNode(banner));
				bannerItem.expandedProperty().addListener((ov, oldV, newV) -> {
					if (!bannerItem.isExpanded()) {
						deselectAllNodes(bannerItem, NodeType.FIELD);
					}
				});
				bannerNode.getValue().setItem(bannerNode);
				bannerNode.getChildren().add(bannerItem);
				banner.setTreeItem(bannerItem);
				for (ReportLayout layout : banner.getFields()) {
					TreeItem<LayoutTreeNode> fieldItem = new TreeItem<LayoutTreeNode>(
							new LayoutTreeNode(layout));
					bannerItem.getChildren().add(fieldItem);
					layout.setTreeItem(fieldItem);
					fieldSelectModel.addField(layout);
				}
			}
		}
		if (formats !=null && !formats.isEmpty()) {
			for (ReportFormat format:formats.values()) {
				TreeItem<LayoutTreeNode> formatItem = new TreeItem<LayoutTreeNode>(new LayoutTreeNode(format));
				formatItem.getValue().setItem(formatItem);
				formatsNode.getChildren().add(formatItem);
				formatSelectModel.addFormat(format);
				format.setTreeItem(formatItem);
			}
		}
		if (styles != null && !styles.isEmpty()) {
			for (ReportStyle style : styles.values()) {
				if (style.isDefaultStyle()&&!showDefaultStyle)
					continue;
				TreeItem<LayoutTreeNode> styleItem = new TreeItem<LayoutTreeNode>(new LayoutTreeNode(style));
				styleItem.getValue().setItem(styleItem);
				styleNode.getChildren().add(styleItem);
				styleSelectModel.addStyle(style);
				style.setTreeItem(styleItem);
			}
		}

		if (Main.loadedIcons.closeImg == null)
			okBtn.setText("Close");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.closeImg));
		if (template.getLeftMargin() > 0.0) {
			setLeftMarginLine();
		}
		if (template.getRightMargin() > 0.0) {
			setRightMarginLine();
		}
		paneBox.setSkin(new DumbSplitPaneSkin(paneBox));
		paneBox.setDividerPosition(0,
				Main.preferences.getDouble(Constants.PROGRAMNAME + "." + Constants.LAYOUTCOL1, 0.2));
		paneBox.setDividerPosition(1,
				Main.preferences.getDouble(Constants.PROGRAMNAME + "." + Constants.LAYOUTCOL2, 0.8));
		/*
		 * following required for resizing last banner
		 */
		layoutBox.setOnDragOver((t) -> {
			Main.rwDebugInst.debugThread("LayoutPaneController", "layoutbox dragover", MRBDebug.DETAILED,
					"drag over detected");
			Dragboard db = t.getDragboard();
			String content = db.hasString() ? db.getString() : "";
			if (!content.equals(Constants.RESIZEBANNER) && LayoutPane.nextBannerBeingResized != null) {
				t.consume();
				return;
			}
			t.acceptTransferModes(TransferMode.MOVE);
			LayoutPane.bannerBeingResized.setMoveLineY(t.getY());
			LayoutPane.bannerBeingResized.setMoveLineVisible(true);
		});
		layoutBox.setOnDragDropped((t) -> {
			Main.rwDebugInst.debugThread("LayoutPaneController", "layoutbox dragover", MRBDebug.DETAILED,
					"drag drop detected");
			LayoutPane.bannerBeingResized.setCanvasHeight(t.getY() - LayoutPane.bannerBeingResized.getLayoutY());
			resizeBanners();
			template.setDirty(true);
			t.setDropCompleted(true);
		});

		alignBox.setDisable(true);
		spaceBox.setDisable(true);
		detailScroll.prefWidthProperty().bind(bannerBox.widthProperty());
	}

	public boolean checkFieldDelete(ReportField field) {
		for (ReportBanner banner : allBanners.values()) {
			for (ReportLayout layout : banner.getFields()) {
				if (layout.getField() == field)
					return true;
			}
		}
		return false;
	}

	void clearError() {

		statusBar.setText("");
	}
	@FXML
	private void okBtnClick(ActionEvent action) {
		closeScreen();
	}
	/*
	 * gets/sets
	 */
	public double getPaperWidth() {
		return paperWidth;
	}

	public void setPaperWidth(double paperWidth) {
		this.paperWidth = paperWidth;
	}

	public double getPaperHeight() {
		return paperHeight;
	}

	public void setPaperHeight(double paperHeight) {
		this.paperHeight = paperHeight;
	}

	/*
	 * close screen
	 */
	private void closeScreen() {
		fieldSelectModel.clearSelection();
		bannerLayout.getChildren().clear();
		bannerSelectModel.clearSelection();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				callingPane.call(Constants.CLOSELAYOUTSCREEN);
			}
		});
	}
	/* 
	 * fieldselectlistener methods 
	 */
	@Override
	public void fieldSelectionAdded(ReportField field) {
	}

	@Override
	public void fieldSelectionRemoved(ReportField field) {
	}

	@Override
	public void fieldSelectionUpdated(ReportField field, boolean selected) {

	}
	/* 
	 * drag and drop field movement 
	 */
	public void moveSelected(KeyEvent event, int xmove, int ymove) {
		if (fieldSelectModel.isEmpty())
			return;
		for (ReportLayout field : fieldSelectModel.getSelectedItems()) {
			if ((field.getX() + xmove) < template.getLeftMargin() * Constants.LAYOUTDIVIDER * 10)
				continue;
			if ((field.getX() + xmove) > +field.getBanner().getWidth() - field.getFieldWidth()
					- template.getRightMargin() * Constants.LAYOUTDIVIDER * 10)
				continue;
			if ((field.getY() + ymove) < 0)
				continue;
			if ((field.getY() + ymove) > field.getBanner().getHeight() - field.getFieldHeight())
				continue;
			field.move(field.getLayoutX() + xmove, field.getLayoutY() + ymove);
			template.setDirty(true);
		}
		if (crntBanner.checkOverlap())
			setError(Constants.ERRFIELDSOVERLAP);
		crntBanner.redrawFields();
		event.consume();
	}

	/*
	 * Recalculates the position of all banners based on height
	 */

	public void resizeBanners() {
		Main.rwDebugInst.debugThread("LayoutPaneController", "resizeBanners", MRBDebug.DETAILED,
				"resizing banners");
		double crntY = ruler.getRulerHeight();
		for (ReportBanner tmpBanner : allBanners.values()) {
			tmpBanner.moveBanner(0, crntY);
			tmpBanner.reset(bannerLayout.getWidth());
			crntY += tmpBanner.getCanvasHeight();
			tmpBanner.redrawFields();
		}
	}
	/*
	 *  Selection methods
	 */
	/**
	 * select tree items, only used when clicking in a banner
	 */
	public void selectTreeItem(TreeItem<LayoutTreeNode> node) {
		switch (node.getValue().getNodeType()) {
		case FIELD:
			if (!multiSelection) {
				treeSelectModel.clearType(NodeType.FIELD);
				fieldSelectModel.clearSelection();
			}
			node.setExpanded(true);
			treeSelectModel.select(node);
			setAlignSpaceBtns();
			break;
		case BANNER:
			if (!multiSelection) {
				treeSelectModel.clearType(NodeType.BANNER);
				bannerSelectModel.clearSelection();
			}
			node.setExpanded(true);
			treeSelectModel.select(node);
			break;
		}
	}

	public void deselectTreeItem(TreeItem<LayoutTreeNode> node) {
		treeSelectModel.clearSelection(node);
	}

	/**
	 * Triggered when tree selection changed
	 * 
	 * 
	 * @param c - Add Change item from selection model - contains a list of adds
	 */
	public void selectItem(TreeItem<LayoutTreeNode> item) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "selectItem", MRBDebug.DETAILED, "row selected ");
		LayoutTreeNode node = item.getValue();
		if (node != null) {
			switch (node.getNodeType()) {
			case STYLE:
				if (node.getStyle() != null && node.getStyle() == styleSelectModel.getSelectedItem())
					return;
				styleSelectModel.clearSelection();
				styleSelectModel.select(node.getStyle());
				selectStyle(styleSelectModel.getSelectedItem());
				break;
			case FORMAT:
				if (node.getFormat() != null && node.getFormat() == formatSelectModel.getSelectedItem())
					return;
				formatSelectModel.clearSelection();
				formatSelectModel.select(node.getFormat());
				selectFormat(formatSelectModel.getSelectedItem());
				break;
				
			case BANNER: // rules 3,6
				Main.rwDebugInst.debugThread("LayoutPaneController", "selectItem", MRBDebug.DETAILED,
						"banner selected " + node.getBanner().getName());

				selectBanner(node.getBanner());
				break;
			case FIELD: // rules 4,5,7,8
				ReportLayout layout = node.getLayout();
				Main.rwDebugInst.debugThread("LayoutPaneController", "selectItem", MRBDebug.DETAILED,
						"field selected " + layout.getName());
				selectField(layout);
				break;
			case LABEL:
				ReportField label = node.getField();
				Main.rwDebugInst.debugThread("LayoutPaneController", "selectItem", MRBDebug.DETAILED,
						"label selected " + label.getName());
				labelSelectModel.clearSelection();
				labelSelectModel.select(label);
				selectField(label);
				break;
			case VARIABLE:
				ReportField var = node.getField();
				Main.rwDebugInst.debugThread("LayoutPaneController", "selectItem", MRBDebug.DETAILED,
						"variable selected " + var.getName());

				labelSelectModel.clearSelection();
				labelSelectModel.select(var);
				selectField(var);
				break;
			case DATABASEFIELD:
				ReportField dbField = node.getField();
				Main.rwDebugInst.debugThread("LayoutPaneController", "selectItem", MRBDebug.DETAILED,
						"database field  selected " + dbField.getName());
				labelSelectModel.clearSelection();
				labelSelectModel.select(dbField);
				selectField(dbField);

			default:
				break;
			}
		} else
			Main.rwDebugInst.debugThread("LayoutPaneController", "selectItem", MRBDebug.DETAILED, "no node");
	}

	/**
	 * Triggered when tree selection changed
	 * 
	 * @param c - Remove Change item from selection model - contains a list of
	 *          removals
	 */

	public void deselectItem(TreeItem<LayoutTreeNode> item) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "deselectItem", MRBDebug.DETAILED, "deselecting row ");
		LayoutTreeNode node = item.getValue();
		if (node != null) {
			switch (node.getNodeType()) {
			case STYLE:
				styleSelectModel.clearSelection();
				deselectStyle();
				break;
			case BANNER:
				Main.rwDebugInst.debugThread("LayoutPaneController", "deselectItem", MRBDebug.DETAILED,
						"banner deselected " + node.getBanner().getName());
				if (!bannerSelectModel.isSelected(node.getBanner()))
					break;
				deselectBanner(node.getBanner());
				break;
			case FIELD:
				ReportLayout layout = node.getLayout();
				Main.rwDebugInst.debugThread("LayoutPaneController", "deselectItem", MRBDebug.DETAILED,
						"field deselected " + layout.getName());
				deselectField(layout);
				ruler.deselectField(layout);
				break;
			default:
				break;

			}
		} else
			Main.rwDebugInst.debugThread("LayoutPaneController", "fieldslist selection", MRBDebug.DETAILED,
					"no node");
	}

	/**
	 * Deselects a banner and removes the details from the Banner Detail pane
	 * 
	 * @param banner - the ReportBanner to be deselect
	 */
	public void deselectBanner(ReportBanner banner) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "deselectBanner", MRBDebug.DETAILED,
				"banner deselect ");
		bannerSelectModel.clearSelection(banner);
		for (TreeItem<LayoutTreeNode> item : bannerNode.getChildren()) {
			if (item.getValue().getBanner().equals(banner))
				treeSelectModel.clearSelection(item);
		}
		if (!detailPane1.getChildren().isEmpty()) {
			((BannerDetailController) detailPane1.getChildren().get(0)).clearFields();
		}
		banner.setSelected(false);

	}

	/**
	 * Selects a banner and sets up the Banner Detail pane when banner selected
	 * 
	 * @param banner - banner to be selected
	 */
	public void selectBanner(ReportBanner banner) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "selectBanner", MRBDebug.DETAILED, "banner select ");
		crntBanner = banner;
		if (!multiSelection) { // rule 3
			List<ReportBanner> selectedBanners = bannerSelectModel.getSelectedItems();
			for (ReportBanner tmpBanner : selectedBanners)
				tmpBanner.setSelected(false); // action A3
			bannerSelectModel.clearSelection(); // action A3
			detailPane1.getChildren().clear();
			/*
			 * if (detailPane1 != null && !detailPane1.getChildren().isEmpty()) {
			 * ((BannerDetailController) detailPane1.getChildren().get(0)).clearFields(); //
			 * action A17 }
			 */
		}
		bannerSelectModel.select(banner); // action A5
		BannerDetailController bannerDetail;
		if (detailPane1.getChildren().isEmpty()) {
			bannerDetail = new BannerDetailController(template, thisObj);
			detailPane1.getChildren().add(bannerDetail);
		} else
			bannerDetail = (BannerDetailController) detailPane1.getChildren().get(0);
		bannerDetail.setFields(banner); // action A15
		banner.setSelected(true);
	}

	/**
	 * Select a layout field - set up Field Detail pane
	 * 
	 * @param field - Report Layout to be selected
	 */
	public void selectField(ReportLayout field) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "selectField(layout)", MRBDebug.DETAILED,
				"field select");
		if (!multiSelection) { // rule 4
			fieldSelectModel.clearSelection(); // action A4
			detailPane2.getChildren().clear();
		}

		fieldSelectModel.select(field); // action A6
		setAlignSpaceBtns();
		FieldDetailController fieldDetail;
		detailPane2.getChildren().clear(); // action A18
		fieldDetail = new FieldDetailController(template, thisObj, field);
		detailPane2.getChildren().add(fieldDetail);
		fieldDetail.setLine(); // action A16
	}

	private void setAlignSpaceBtns() {
		boolean fieldsAllSelected = true;
		for (TreeItem<LayoutTreeNode> item : bannerNode.getChildren()) {
			for (TreeItem<LayoutTreeNode> itemField : item.getChildren()) {
				if (itemField.getValue().getNodeType().equals(NodeType.FIELD)) {
					if (!itemField.getValue().getLayout().isSelected())
						fieldsAllSelected = false;
				}
			}
		}
		if (fieldsAllSelected) {
			allSelected = true;
			selectAll.setText("Deselect All");
		} else {
			allSelected = false;
			selectAll.setText("Select All");
		}
		int selSize = fieldSelectModel.getSelectedIndices().size();
		Main.rwDebugInst.debugThread("LayoutPaneController", "setAlignSpaceBtns", MRBDebug.DETAILED,
				"num fields selected " + selSize);
		if (selSize > 1) {
			alignBox.setDisable(false);
			if (selSize > 2)
				spaceBox.setDisable(false);
			else
				spaceBox.setDisable(true);
			return;
		} else {
			alignBox.setDisable(true);
			spaceBox.setDisable(true);
		}
	}

	/**
	 * Selects a database field - set up Field Detail pane
	 * 
	 * @param field - Report Field to select
	 */
	private void selectField(ReportField field) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "selectField(field)", MRBDebug.DETAILED,
				"field select");
		alignBox.setDisable(true);
		spaceBox.setDisable(true);
		FieldDetailController fieldDetail;
		detailPane2.getChildren().clear();
		fieldDetail = new FieldDetailController(template, thisObj, field);
		detailPane2.getChildren().add(fieldDetail);
		fieldDetail.setLine();
	}

	/**
	 * Deselect all tree children of a tree node Recursive calls are made if child
	 * has children of its own
	 * 
	 * @param node - Tree Node holding the nodes to be deselected
	 * @param type - node type of tree node to be deselected
	 */
	private void deselectAllNodes(TreeItem<LayoutTreeNode> node, NodeType type) {

		for (TreeItem<LayoutTreeNode> selectedNode : node.getChildren()) {
			int nodeIndex = fieldsList.getRow(selectedNode);
			if (selectedNode.getChildren().size() > 0)
				deselectAllNodes(selectedNode, type);
			LayoutTreeNode treeNode = selectedNode.getValue();
			if (treeNode != null) {
				if (treeNode.getNodeType() != type)
					continue;
				treeSelectModel.clearSelection(selectedNode);
				switch (treeNode.getNodeType()) {
				case STYLE:
					Main.rwDebugInst.debugThread("LayoutPaneController", "deselectAllNodes",
							MRBDebug.DETAILED, "style deselected");
					break;
				case FORMAT:
					Main.rwDebugInst.debugThread("LayoutPaneController", "deselectAllNodes",
							MRBDebug.DETAILED, "format deselected");
					break;
				case BANNER:
					Main.rwDebugInst.debugThread("LayoutPaneController", "deselectAllNodes",
							MRBDebug.DETAILED,
							"banner deselected " + treeNode.getBanner().getName() + " " + nodeIndex);
					break;
				case FIELD:
					if (!treeNode.getLayout().isSelected())
						continue;
					Main.rwDebugInst.debugThread("LayoutPaneController", "deselectAllNodes",
							MRBDebug.DETAILED, "field deselected " + treeNode.getLayout().getText());
					break;
				default:
				}
			}

		}
	}

	/**
	 * Select all tree children of a tree node Recursive calls are made if child has
	 * children of its own
	 * 
	 * @param node - Tree Node holding the nodes to be selected
	 * @param type - node type of tree node to be selected
	 */
	private void selectAllNodes(TreeItem<LayoutTreeNode> node, NodeType type) {

		for (TreeItem<LayoutTreeNode> selectedNode : node.getChildren()) {
			int nodeIndex = fieldsList.getRow(selectedNode);
			if (selectedNode.getChildren().size() > 0)
				selectAllNodes(selectedNode, type);
			LayoutTreeNode treeNode = selectedNode.getValue();
			if (treeNode != null) {
				if (treeNode.getNodeType() != type)
					continue;
				treeSelectModel.select(selectedNode);
				switch (treeNode.getNodeType()) {
				case STYLE:
					Main.rwDebugInst.debugThread("LayoutPaneController", "selectAllNodes", MRBDebug.DETAILED,
							"style selected");
					break;
				case FORMAT:
					Main.rwDebugInst.debugThread("LayoutPaneController", "selectAllNodes", MRBDebug.DETAILED,
							"format selected");
					break;
				case BANNER:
					Main.rwDebugInst.debugThread("LayoutPaneController", "deselectAllNodes",
							MRBDebug.DETAILED,
							"banner deselected " + treeNode.getBanner().getName() + " " + nodeIndex);
					treeNode.getBanner().setSelected(true);
					break;
				case FIELD:
					treeNode.getLayout().setSelected(true);
					Main.rwDebugInst.debugThread("LayoutPaneController", "deselectAllNodes",
							MRBDebug.DETAILED, "field deselected " + treeNode.getLayout().getText());
					break;
				default:
				}
			}

		}
	}

	/**
	 * Deselects the field and clears the Field Detail pane
	 * 
	 * @param field - the ReportLayout to be deselected
	 */
	public void deselectField(ReportLayout field) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "deselectField", MRBDebug.DETAILED, "field deselect ");
		fieldSelectModel.clearSelection(field);
		if (!detailPane2.getChildren().isEmpty()) {
			((FieldDetailController) detailPane2.getChildren().get(0)).clearFields();
		}
		setAlignSpaceBtns();

	}

	/**
	 * Selects a format- sets up the Format Detail Pane
	 * 
	 * @param format - the Report Format to select
	 */
	private void selectFormat(ReportFormat format) {
		FormatDetailController formatPane;
		if (detailPane4.getChildren().isEmpty()) {
			formatPane = new FormatDetailController(template, thisObj, format);
			formatPane.setLine();
			detailPane4.getChildren().add(formatPane);
		} else {
			formatPane = (FormatDetailController) detailPane4.getChildren().get(0);
			formatPane.clearFields();
		}
		formatPane.setFields(format);
	}

	/**
	 * Clears the Format Detail pane
	 */
	private void deselectFormat() {
		detailPane4.getChildren().clear();

	}

	/**
	 * Selects a style - sets up the Style Detail Pane
	 * 
	 * @param style - the Report Style to select
	 */
	private void selectStyle(ReportStyle style) {
		StyleDetailController stylePane;
		if (detailPane3.getChildren().isEmpty()) {
			stylePane = new StyleDetailController(template, thisObj, style);
			stylePane.setLine();
			detailPane3.getChildren().add(stylePane);
		} else
			stylePane = (StyleDetailController) detailPane3.getChildren().get(0);
		stylePane.setFields(style);
	}

	/**
	 * Clears the Style Detail pane
	 */
	private void deselectStyle() {
		detailPane3.getChildren().clear();

	}

	/*
	 * end of selection methods
	 */
	public void setError(String message) {
		statusBar.setText(message);
	}

	public void setNextBanner(ReportBanner banner) {
		LayoutPane.nextBannerBeingResized = null;
		boolean found = false;
		for (ReportBanner tmpBanner : allBanners.values()) {
			if (tmpBanner == banner)
				found = true;
			else {
				if (found) {
					LayoutPane.nextBannerBeingResized = tmpBanner;
					found = false;
					break;
				}
			}
		}
		return;
	}

	public void setSelectingMany(Boolean selectingMany) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "setSelectedMany", MRBDebug.DETAILED,
				"selecting many set to " + selectingMany);
		multiSelection = selectingMany;
	}

	
	private void determinePoints(ObservableList<ReportLayout> selFields) {
		lowestX = Double.MAX_VALUE;
		lowestY = Double.MAX_VALUE;
		highestX = 0.0;
		highestY = 0.0;
		fieldsWidth = 0.0;
		fieldsHeight = 0.0;
		for (ReportLayout field : selFields) {
			if (field.getX() < lowestX) {
				lowestX = field.getX();
			}
			if (field.getX() + field.getFieldWidth() > highestX) {
				highestX = field.getX() + field.getFieldWidth();
			}
			if (field.getY() < lowestY)
				lowestY = field.getY();
			if (field.getY() + field.getFieldHeight() > highestY)
				highestY = field.getY() + field.getFieldHeight();
			fieldsWidth += field.getFieldWidth();
			fieldsHeight += field.getFieldHeight();
		}
	}

	/*	
	 set/reset methods 
	 */
	public void addField(ReportLayout field) {
		for (TreeItem<LayoutTreeNode> item : bannerNode.getChildren()) {
			if (item.getValue().getBanner() != null && item.getValue().getBanner() == field.getBanner()) {
				TreeItem<LayoutTreeNode> fieldItem = new TreeItem<LayoutTreeNode>(new LayoutTreeNode(field));
				item.getChildren().add(fieldItem);
				fieldSelectModel.addField(field);
				field.setTreeItem(fieldItem);
			}
		}
	}
	public void removeLayoutNode(ReportBanner banner, ReportLayout layout) {
		TreeItem<LayoutTreeNode> found=null;
		TreeItem<LayoutTreeNode> bannerFound=null;
		for (TreeItem<LayoutTreeNode> item : bannerNode.getChildren()) {
			if (item.getValue().getBanner() != null && item.getValue().getBanner() == banner) {
				for (TreeItem<LayoutTreeNode> layoutItem : item.getChildren()) {
					if (layoutItem.getValue().getNodeType()==NodeType.FIELD) {
						if (layoutItem.getValue().getLayout()==layout) {
							bannerFound=item;
							found=layoutItem;
							break;
						}
					}
				}
			}
		}
		if (found != null)
			bannerFound.getChildren().remove(found);
	}
	public void removeVariableNode( ReportField variable) {
		TreeItem<LayoutTreeNode> found=null; 
		for (TreeItem<LayoutTreeNode> item : variablesNode.getChildren()) {
			if (item.getValue().getNodeType()==NodeType.VARIABLE) {
						if (item.getValue().getField()==variable)
							found = item;
			}
		}
		if (found!=null)
			variablesNode.getChildren().remove(found);
	}
	private boolean insertBanner(ReportBanner banner) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "insertBanner", MRBDebug.DETAILED,
				"inserting banner " + banner.getName());
		if (!bannerLayout.getChildren().contains(banner))
			bannerLayout.getChildren().add(banner);
		else
			Main.rwDebugInst.debugThread("LayoutPaneController", "insertBanner", MRBDebug.DETAILED,
					" duplicate banner " + banner.getName());
		banner.setLayoutX(0);
		bannerSelectModel.addBanner(banner);
		Main.rwDebugInst.debugThread("LayoutPaneController", "insertBanner", MRBDebug.DETAILED,
				" new Y for banner " + banner.getBannerType().getPosition() + " " + banner.getLayoutY());
		return false;
	}


	/*
	 * reset fields to catch change in margins/styles
	 */
	private void resetFields() {
		for (ReportBanner banner : allBanners.values()) {
			banner.redrawFields();
		}
	}

	/*
	 * reset the available fields in the tree view
	 */
	private void resetDatabaseFields() {
		databaseNode.getChildren().clear();
		TreeItem<LayoutTreeNode> record;
		selectedFields = template.getSelectedFields();
		recordNodes = new TreeMap<String, TreeItem<LayoutTreeNode>>();
		for (ReportField field : selectedFields.values()) {
			if (recordNodes.containsKey(field.getFieldBean().getShortTableName()))
				record = recordNodes.get(field.getFieldBean().getShortTableName());
			else {
				record = new TreeItem<LayoutTreeNode>(
						new LayoutTreeNode(field.getFieldBean().getScreenTitle(), NodeType.RECORD));
				record.getValue().setItem(record);
				databaseNode.getChildren().add((TreeItem<LayoutTreeNode>) record);
				recordNodes.put(field.getFieldBean().getShortTableName(), record);
			}
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<LayoutTreeNode>(new LayoutTreeNode(field));
			fieldItem.getValue().setItem(fieldItem);
			record.getChildren().add(fieldItem);
			field.setTreeItem(fieldItem);
		}
	}
	/*
	 * reset the formats in the tree view
	 */
	private void resetTreeFormats() {
		formatsNode.getChildren().clear();
		formats = template.getFormats();
		for (ReportFormat format : formats.values()) {
			if (format.isDefaultFormat() && !showDefaultFormat)
				continue;
			TreeItem<LayoutTreeNode> formatItem = new TreeItem<LayoutTreeNode>(new LayoutTreeNode(format));
			formatItem.getValue().setItem(formatItem);
			formatsNode.getChildren().add(formatItem);
			format.setTreeItem(formatItem);
			formatSelectModel.addFormat(format);
		}
	}
	/*
	 * reset the styles in the tree view
	 */
	private void resetTreeStyles() {
		styleNode.getChildren().clear();
		styles = template.getStyles();
		for (ReportStyle style : styles.values()) {
			if (style.isDefaultStyle() && !showDefaultStyle)
				continue;
			TreeItem<LayoutTreeNode> styleItem = new TreeItem<LayoutTreeNode>(new LayoutTreeNode(style));
			styleItem.getValue().setItem(styleItem);
			styleNode.getChildren().add(styleItem);
			style.setTreeItem(styleItem);
			styleSelectModel.addStyle(style);
		}
	}
	/*
	 * Go through banners and reset field formats
	 */
	public void resetFormats() {
		if (!detailPane2.getChildren().isEmpty()) {
			((FieldDetailController) (detailPane2.getChildren().get(0))).resetFormats();
		}

	}
	/*
	 * Go through banners and reset field styles
	 */
	public void resetStyles() {
		for (ReportBanner banner : banners) {
			banner.resetStyle();
		}
		if (!detailPane1.getChildren().isEmpty()) {
			((BannerDetailController) (detailPane1.getChildren().get(0))).resetStyles();
		}
		if (!detailPane2.getChildren().isEmpty()) {
			((FieldDetailController) (detailPane2.getChildren().get(0))).resetStyles();
		}

	}

	/*
	 * reset tree lines for fields in a banner
	 */
	private void resetTreeFields(ReportBanner banner) {
		fieldsList.getSelectionModel().clearSelection();
		for (TreeItem<LayoutTreeNode> node : bannerNode.getChildren()) {
			if (node.getValue().getNodeType() == NodeType.BANNER) {
				if (node.getValue().getBanner() == banner) {
					for (TreeItem<LayoutTreeNode> fieldNode : node.getChildren()) {
						if (fieldNode.getValue().getNodeType() == NodeType.FIELD)
							fieldSelectModel.removeField(fieldNode.getValue().getLayout());
					}
					node.getChildren().clear();
					fieldSelectModel.clearSelection();
					for (ReportLayout field : banner.getFields()) {
						TreeItem<LayoutTreeNode> fieldItem = new TreeItem<LayoutTreeNode>(
								new LayoutTreeNode(field));
						node.getChildren().add(fieldItem);
						fieldSelectModel.addField(field);
						field.setTreeItem(fieldItem);
					}
				}
			}
		}
	}

	/*
	 * reset tree lines for labels
	 */
	private void resetTreeLabels(ReportField newLabel) {
		TreeItem<LayoutTreeNode> newItem = null;
		labelsNode.getChildren().clear();
		for (ReportField label : template.getLabels().values()) {
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
			if (label == newLabel)
				newItem = fieldItem;
			fieldItem.getValue().setNodeType(NodeType.LABEL);
			fieldItem.getValue().setField(label);
			fieldItem.getValue().setItem(fieldItem);
			labelsNode.getChildren().add(fieldItem);
			label.setTreeItem(fieldItem);
		}
		if (newItem != null) {
			fieldsList.getSelectionModel().clearSelection();
			fieldsList.getSelectionModel().select(newItem);
		}
	}

	/*
	 * reset tree lines for Variables adding new variable
	 */
	private void resetTreeVariables(ReportField newVariable) {
		TreeItem<LayoutTreeNode> newItem = null;
		variablesNode.getChildren().clear();
		for (ReportField variable : template.getVariables().values()) {
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
			if (variable == newVariable)
				newItem = fieldItem;
			fieldItem.getValue().setNodeType(NodeType.VARIABLE);
			fieldItem.getValue().setField(variable);
			fieldItem.getValue().setItem(fieldItem);
			variable.setTreeItem(fieldItem);
			variablesNode.getChildren().add(fieldItem);
		}
		if (newItem != null) {
			fieldsList.getSelectionModel().clearSelection();
			fieldsList.getSelectionModel().select(newItem);
		}
	}
	/*
	 * reset tree lines for Variables after delete
	 */
	private void resetTreeVariables() {
		variablesNode.getChildren().clear();
		for (ReportField variable : template.getVariables().values()) {
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
			fieldItem.getValue().setNodeType(NodeType.VARIABLE);
			fieldItem.getValue().setField(variable);
			fieldItem.getValue().setItem(fieldItem);
			variable.setTreeItem(fieldItem);
			variablesNode.getChildren().add(fieldItem);
		}
		
	}
	private void resetAllTreeLabels() {
		labelsNode.getChildren().clear();
		for (ReportField label : template.getLabels().values()) {
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
			fieldItem.getValue().setNodeType(NodeType.LABEL);
			fieldItem.getValue().setField(label);
			fieldItem.getValue().setItem(fieldItem);
			labelsNode.getChildren().add(fieldItem);
			label.setTreeItem(fieldItem);
		}
	}
/*
 *  margins
  */
	private void bottomMarginChg(String newValue) {
		UndoRecord undoRec = null;
		String oldValue = "";
		if (!undoManager.getUndoUnderway()) {
			undoRec = undoManager.newRecord();
			oldValue = String.valueOf(template.getBottomMargin());
		}
		boolean error = false;
		double d = 0.0;
		try {
			d = Double.parseDouble(newValue);
			if (d < 0.0 || d > paperHeight) {
				error = true;
				setError("Margin outside page height");
			}
		} catch (NumberFormatException e) {
			setError("Bottom Margin not numeric");
			error = true;
		}
		if (error) {
			bottomMargin.setStyle("-fx-text-box-border: RED; -fx-focus-color:RED;");
		} else {
			clearError();
			bottomMargin.setStyle(null);
			template.setBottomMargin(d);
			template.setDirty(true);
			if (ruler != null)
				ruler.setBottomMargin(d);
			bottomMargin.setStyle(null);
		}
		if (!undoManager.getUndoUnderway()) {
			undoRec.newAction(this, DetailType.MARGIN, oldValue, newValue, XmlWriter.BOTTOMMARGIN);
			undoManager.addRecord(undoRec);
		}
	}
	private void leftMarginChg(String newValue) {
		UndoRecord undoRec = null;
		String oldValue = "";
		if (!undoManager.getUndoUnderway()) {
			undoRec = undoManager.newRecord();
			oldValue = String.valueOf(template.getLeftMargin());
		}

		boolean error = false;
		double d = 0.0;
		try {
			d = Double.parseDouble(newValue);
			if (d < 0.0 || d > paperWidth) {
				error = true;
				setError("Margin outside page width");
			}
		} catch (NumberFormatException e) {
			error = true;
			setError("Left Margin not numeric");
		}
		if (error) {
			leftMargin.setStyle("-fx-text-box-border: RED; -fx-focus-color:RED;");
		} else {
			clearError();
			leftMargin.setStyle(null);
			template.setLeftMargin(d);
			template.setDirty(true);
			if (ruler != null)
				ruler.setLeftMargin(d);
			leftMargin.setStyle(null);
			if (d > 0.0)
				setLeftMarginLine();
		}
		resetFields();
		if (!undoManager.getUndoUnderway()) {
			undoRec.newAction(this, DetailType.MARGIN, oldValue, newValue, XmlWriter.LEFTMARGIN);
			undoManager.addRecord(undoRec);
		}
	}
	private void setLeftMarginLine() {
		if (leftMarginLine != null)
			bannerLayout.getChildren().remove(leftMarginLine);
		leftMarginLine = new Line();
		leftMarginLine.setStartX(template.getLeftMargin() * Constants.LAYOUTDIVIDER);
		leftMarginLine.setStartY(0.0);
		leftMarginLine.setEndX(leftMarginLine.getStartX());
		leftMarginLine.setEndY(bannerLayout.getHeight());
		leftMarginLine.setStroke(Color.LIGHTGRAY);
		leftMarginLine.setStrokeWidth(0.5);
		bannerLayout.getChildren().add(leftMarginLine);
		leftMarginLine.toBack();

	}

	private void setPageSize() {
		paperWidth = template.getPaperWidth();
		paperHeight = template.getPaperHeight();
		layoutWidth.set(paperWidth * Constants.LAYOUTDIVIDER);
	}

	private void setRightMarginLine() {
		if (rightMarginLine != null)
			bannerLayout.getChildren().remove(rightMarginLine);
		rightMarginLine = new Line();
		rightMarginLine.setStartX(
				(ruler.getPaperWidth() - template.getRightMargin()) * Constants.LAYOUTDIVIDER );
		rightMarginLine.setStartY(0.0);
		rightMarginLine.setEndX(rightMarginLine.getStartX());
		rightMarginLine.setEndY(bannerLayout.getHeight());
		rightMarginLine.setStroke(Color.LIGHTGRAY);
		rightMarginLine.setStrokeWidth(0.5);
		bannerLayout.getChildren().add(rightMarginLine);
		rightMarginLine.toBack();
	}

	private void rightMarginChg(String newValue) {
		UndoRecord undoRec = null;
		String oldValue = "";
		if (!undoManager.getUndoUnderway()) {
			undoRec = undoManager.newRecord();
			oldValue = String.valueOf(template.getRightMargin());
		}
		boolean error = false;
		double d = 0.0;
		try {
			d = Double.parseDouble(newValue);
			if (d < 0.0 || d > paperWidth) {
				error = true;
				setError("Margin outside page width");
			}
		} catch (NumberFormatException e) {
			error = true;
			setError("Right Margin not numeric");
		}
		if (error) {
			rightMargin.setStyle("-fx-text-box-border: RED; -fx-focus-color:RED;");
		} else {
			clearError();
			rightMargin.setStyle(null);
			template.setRightMargin(d);
			template.setDirty(true);
			if (ruler != null)
				ruler.setRightMargin(d);
			rightMargin.setStyle(null);
			if (d > 0.0)
				setRightMarginLine();
			else
				rightMarginLine.setVisible(false);
		}
		if (!undoManager.getUndoUnderway()) {
			undoRec.newAction(this, DetailType.MARGIN, oldValue, newValue, XmlWriter.RIGHTMARGIN);
			undoManager.addRecord(undoRec);
		}
		resetFields();
	}

	private void topMarginChg(String newValue) {
		UndoRecord undoRec = null;
		String oldValue = "";
		if (!undoManager.getUndoUnderway()) {
			oldValue = String.valueOf(template.getTopMargin());
			undoRec = undoManager.newRecord();
		}
		boolean error = false;
		double d = 0.0;
		try {
			d = Double.parseDouble(newValue);
			if (d < 0.0 || d > paperHeight) {
				error = true;
				setError("Margin outside page height");
			}
		} catch (NumberFormatException e) {
			error = true;
			setError("Top Margin not numeric");
		}
		if (error) {
			topMargin.setStyle("-fx-text-box-border: RED; -fx-focus-color:RED;");
		} else {
			clearError();
			topMargin.setStyle(null);
			template.setTopMargin(d);
			template.setDirty(true);
			if (ruler != null)
				ruler.setTopMargin(d);
			topMargin.setStyle(null);
		}
		if (!undoManager.getUndoUnderway()) {
			undoRec.newAction(this, DetailType.MARGIN, oldValue, newValue, XmlWriter.TOPMARGIN);
			undoManager.addRecord(undoRec);
		}
	}

	/*
	 * Banner fields
	 * 
	 * 
	 */

	@FXML
	private void selectDelete(ActionEvent action) {
		if (fieldSelectModel.isEmpty()) {
			action.consume();
			return;
		}
		String message;
		if (fieldSelectModel.getSelectedIndices().size() == 1)
			message = "Do you wish to delete field: " + fieldSelectModel.getSelectedItem().getText();
		else
			message = "Do you wish to delete all selected fields";
		if (OptionMessage.yesnoMessage(message)) {
			ObservableList<ReportLayout> selFields = fieldSelectModel.getSelectedItems();
			crntBanner.removeFields(selFields);
			for (ReportLayout field : selFields) {
				fieldSelectModel.removeField(field);
			}
			fieldSelectModel.clearSelection();
			crntBanner.redrawFields();
			template.setDirty(true);
		}
	}
	@FXML
	private void selectSelectAll(ActionEvent action) {
		if (allSelected) {
			fieldSelectModel.clearSelection();
			deselectAllNodes(bannerNode, NodeType.FIELD);
			allSelected = false;
			selectAll.setText("Select All");
		} else {
			deselectAllNodes(bannerNode, NodeType.FIELD);
			multiSelection = true;
			selectAllNodes(bannerNode, NodeType.FIELD);
			selectAll.setText("Deselect All");
			allSelected = true;
			multiSelection = false;
		}
	}


	/*
	 * Page fields
	 * 
	 * 
	 */
	@FXML
	private void selectOrientation(ActionEvent action) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "selectOrientation", MRBDebug.DETAILED,
				"  action event called - " + action.getEventType().getName());
		selectOrientationChg(pageOrientation.getSelectionModel().getSelectedItem());
	}

	private void selectOrientationChg(String newValue) {
		UndoRecord undoRec = null;
		if (!undoManager.getUndoUnderway())
			undoRec = undoManager.newRecord();
		if (!undoManager.getUndoUnderway())
			undoRec.newAction(this, DetailType.PAPER, template.getOrientation().toString(), newValue,
					XmlWriter.PAGELAYOUT);
		else
			pageOrientation.getSelectionModel().select(newValue);
		template.setOrientation(PageOrientation.valueOf(pageOrientation.getSelectionModel().getSelectedItem()));
		paperWidth = template.getPaperWidth();
		template.setDirty(true);
		setPageSize();
		if (ruler != null)
			ruler.setLayout(template.getOrientation());
		layoutWidth.set(paperWidth * Constants.LAYOUTDIVIDER);
		if (banners != null) {
			for (ReportBanner banner : banners) {
				banner.reset(layoutWidth.get());
			}
		}
		if (ruler != null)
			ruler.setRightMargin(template.getRightMargin());
		if (template.getRightMargin() > 0.0)
			setRightMarginLine();
		if (!undoManager.getUndoUnderway())
			undoManager.addRecord(undoRec);
		resetFields();
	}

	@FXML

	private void selectPaperSize(ActionEvent action) {
		String newSize = paperSizes.getValue();
		paperSizeChg(newSize);
	}

	private void paperSizeChg(String newSize) {
		UndoRecord undoRec = null;
		if (!undoManager.getUndoUnderway())
			undoRec = undoManager.newRecord();
		for (Paper paper : Constants.PaperSizesObjects)
			if (newSize.equals(paper.toString())) {
				if (!undoManager.getUndoUnderway())
					undoRec.newAction(this, DetailType.PAPER, template.getPaperSize().toString(),
							paper.toString(), XmlWriter.PAPERSIZE);
				else
					paperSizes.getSelectionModel().select(newSize);
				template.setPaperSize(paper);
				template.setDirty(true);
			}
		setPageSize();
		if (ruler != null)
			ruler.setPaper(template.getPaperSize());
		if (banners != null) {
			for (ReportBanner banner : banners) {
				banner.reset(paperWidth * Constants.LAYOUTDIVIDER);
			}
		}
		if (!undoManager.getUndoUnderway())
			undoManager.addRecord(undoRec);
		resetFields();
	}
/* 
 * alignment methods 
 * 
 */
 	@FXML
	private void selectRightAlign(ActionEvent action) {
		UndoRecord undoRec = undoManager.newRecord();
		ObservableList<ReportLayout> selFields = fieldSelectModel.getSelectedItems();
		List<ReportBanner> touchedBanners = new ArrayList<ReportBanner>();
		determinePoints(selFields);
		for (ReportLayout field : selFields) {
			undoRec.newAction(field, DetailType.LAYOUT, String.valueOf(field.getX()),
					String.valueOf(highestX - field.getFieldWidth()), XmlWriter.LAYOUTX);
			field.move(highestX - field.getFieldWidth(), field.getY());
			if (!touchedBanners.contains(field.getBanner()))
				touchedBanners.add(field.getBanner());
		}
		undoManager.addRecord(undoRec);
		for (ReportBanner banner : touchedBanners) {
			if (banner.checkOverlap())
				banner.redrawFields();
		}

	}

	@FXML
	private void selectLeftAlign(ActionEvent action) {
		UndoRecord undoRec = undoManager.newRecord();
		ObservableList<ReportLayout> selFields = fieldSelectModel.getSelectedItems();
		List<ReportBanner> touchedBanners = new ArrayList<ReportBanner>();
		determinePoints(selFields);
		for (ReportLayout field : selFields) {
			undoRec.newAction(field, DetailType.LAYOUT, String.valueOf(field.getX()), String.valueOf(lowestX),
					XmlWriter.LAYOUTX);
			field.move(lowestX, field.getY());
			if (!touchedBanners.contains(field.getBanner()))
				touchedBanners.add(field.getBanner());
		}
		undoManager.addRecord(undoRec);
		for (ReportBanner banner : touchedBanners) {
			if (banner.checkOverlap())
				banner.redrawFields();
		}
	}


	@FXML
	private void selectTopAlign(ActionEvent action) {
		UndoRecord undoRec = undoManager.newRecord();
		ObservableList<ReportLayout> selFields = fieldSelectModel.getSelectedItems();
		List<ReportBanner> touchedBanners = new ArrayList<ReportBanner>();
		determinePoints(selFields);
		for (ReportLayout field : selFields) {
			undoRec.newAction(field, DetailType.LAYOUT, String.valueOf(field.getY()), String.valueOf(lowestY),
					XmlWriter.LAYOUTY);
			field.move(field.getX(), lowestY);
			if (!touchedBanners.contains(field.getBanner()))
				touchedBanners.add(field.getBanner());
		}
		undoManager.addRecord(undoRec);
		for (ReportBanner banner : touchedBanners) {
			if (banner.checkOverlap())
				banner.redrawFields();
		}
	}
	@FXML
	private void selectBottomAlign(ActionEvent action) {
		UndoRecord undoRec = undoManager.newRecord();
		ObservableList<ReportLayout> selFields = fieldSelectModel.getSelectedItems();
		List<ReportBanner> touchedBanners = new ArrayList<ReportBanner>();
		determinePoints(selFields);
		for (ReportLayout field : selFields) {
			undoRec.newAction(field, DetailType.LAYOUT, String.valueOf(field.getY()),
					String.valueOf(highestY - field.getFieldHeight()), XmlWriter.LAYOUTY);
			field.move(field.getX(), highestY - field.getFieldHeight());
			if (!touchedBanners.contains(field.getBanner()))
				touchedBanners.add(field.getBanner());
		}
		undoManager.addRecord(undoRec);
		for (ReportBanner banner : touchedBanners) {
			if (banner.checkOverlap())
				banner.redrawFields();
		}

	}
/* spacing methods */
	@FXML
	private void selectHorSpace(ActionEvent action) {
		UndoRecord undoRec = undoManager.newRecord();
		Comparator<ReportLayout> comp = Comparator.comparing(ReportLayout::getX);
		ObservableList<ReportLayout> selFields = fieldSelectModel.getSelectedItems().sorted(comp);
		determinePoints(selFields);
		double overallWidth = highestX - lowestX;
		double space = (overallWidth - fieldsWidth) / (selFields.size() - 1);
		double nextX = 0.0;
		for (ReportLayout field : selFields) {
			if (field.getX() == lowestX) {
				nextX = field.getX() + field.getFieldWidth() + space;
				continue;
			}
			if (field.getX() + field.getFieldWidth() == highestX)
				continue;
			undoRec.newAction(field, DetailType.LAYOUT, String.valueOf(field.getX()), String.valueOf(nextX),
					XmlWriter.LAYOUTX);
			field.move(nextX, field.getY());
			nextX = field.getX() + field.getFieldWidth() + space;
		}
		undoManager.addRecord(undoRec);
	}

	@FXML
	private void selectVerSpace(ActionEvent action) {
		UndoRecord undoRec = undoManager.newRecord();
		Comparator<ReportLayout> comp = Comparator.comparing(ReportLayout::getY);
		ObservableList<ReportLayout> selFields = fieldSelectModel.getSelectedItems().sorted(comp);
		determinePoints(selFields);
		double overallHeight = highestY - lowestY;
		double space = (overallHeight - fieldsHeight) / (selFields.size() - 1);
		double nextY = 0.0;
		for (ReportLayout field : selFields) {
			if (field.getY() == lowestY) {
				nextY = field.getY() + field.getFieldHeight() + space;
				continue;
			}
			if (field.getY() + field.getFieldHeight() == highestY)
				continue;
			undoRec.newAction(field, DetailType.LAYOUT, String.valueOf(field.getY()), String.valueOf(nextY),
					XmlWriter.LAYOUTY);
			field.move(field.getX(), nextY);
			nextY = field.getY() + field.getFieldHeight() + space;
		}
		undoManager.addRecord(undoRec);
	}

	/*
	 * menu actions
	 */
	private void deleteField(TreeItem<LayoutTreeNode> cell) {
		ReportLayout layout = cell.getValue().getLayout();
		TreeItem<LayoutTreeNode> bannerNode = cell.getParent();
		ReportBanner banner = bannerNode.getValue().getBanner();
		if (layout == null || banner == null)
			return;
		layout.deleteField();
		banner.getFields().remove(layout);
		fieldSelectModel.removeField(layout);
		resetTreeFields(banner);
		template.isDirty();
	}

	private void removeBanner(ReportBanner banner) {
		ReportBanner footer = null;
		if (banner == null)
			return;
		Main.rwDebugInst.debugThread("LayoutPaneController", "removeBanner", MRBDebug.DETAILED,
				"  banner " + banner.getPosition() + "  being removed");
		if (!banner.getFields().isEmpty())
			if (!OptionMessage.yesnoMessageFX("Banner has fields. Are you sure you wish to delete?"))
				return;
		UndoRecord undoRec = null;
		if (!undoManager.getUndoUnderway()) {
			undoRec = undoManager.newRecord();
		}
		if (banner.getBannerType() == BannerType.GROUPHEAD) {
			if (banner.getHasFooter()) {
				if (!OptionMessage.yesnoMessageFX(
						"Banner Group has Footer, deleting the header will delete the footer. Do you wish to continue?"))
					return;
				int pos = banner.getOtherPos();
				footer = allBanners.get(pos);
				if (footer == null) {
					Main.rwDebugInst.debugThread("LayoutPaneController", "removeBanner", MRBDebug.DETAILED,
							"  footer banner " + pos + "  missing");
				} else {
					bannerSelectModel.removeBanner(footer);
					template.removeBanner(footer);
					allBanners.remove(footer.getPosition());
					bannerLayout.getChildren().remove(footer);
					if (!undoManager.getUndoUnderway()) {
						String newValue = footer.getBannerType().getName();
						newValue += "/" + footer.getGroupField().getKey();
						undoRec.newAction(this, DetailType.DELETEBANNER, "", newValue, XmlWriter.BANNER);
						Main.rwDebugInst.debugThread("LayoutPaneController", "removeBanner",
								MRBDebug.DETAILED,
								" footer removed " + footer.getGroupField().getName());

					}
				}
			}
			if (banner.getGroupField() != null) {
				banner.getGroupField().setGroupSelected(false);
			}
		}
		if (banner.getBannerType() == BannerType.GROUPFOOT) {
			int headPos = banner.getOtherPos();
			ReportBanner tmpBanner = allBanners.get(headPos);
			if (tmpBanner == null) {
				Main.rwDebugInst.debugThread("LayoutPaneController", "removeBanner", MRBDebug.DETAILED,
						" head  banner " + headPos + "  missing");
			} else {
				tmpBanner.setHasFooter(false);
				undoRec.newAction(this, DetailType.UNSETBANNERFOOTER, "",
						BannerType.GROUPFOOT.getName() + "/" + tmpBanner.getGroupField().getKey(),
						XmlWriter.BANNER);
				undoManager.addRecord(undoRec);
				Main.rwDebugInst.debugThread("LayoutPaneController", "removeBanner", MRBDebug.DETAILED,
						" header Has Footer unset " + tmpBanner.getGroupField().getName());

			}
		}

		bannerSelectModel.removeBanner(banner);
		template.removeBanner(banner);
		allBanners.remove(banner.getPosition());
		bannerLayout.getChildren().remove(banner);

		if (!undoManager.getUndoUnderway()) {
			String newValue = banner.getBannerType().getName();
			if (banner.getBannerType() == BannerType.GROUPFOOT || banner.getBannerType() == BannerType.GROUPHEAD)
				newValue += "/" + banner.getGroupField().getKey();
			Main.rwDebugInst.debugThread("LayoutPaneController", "removeBanner", MRBDebug.DETAILED,
					" banner removed  " + newValue);
			undoRec.newAction(this, DetailType.DELETEBANNER, "", newValue, XmlWriter.BANNER);
			undoManager.addRecord(undoRec);
		}
		resetGroups();
		double crntY = ruler.getRulerHeight();
		for (ReportBanner tmpBanner : allBanners.values()) {
			tmpBanner.moveBanner(0, crntY);
			tmpBanner.reset(bannerLayout.getWidth());
			crntY += tmpBanner.getCanvasHeight();
		}
		TreeItem<LayoutTreeNode> foundItem = null;
		TreeItem<LayoutTreeNode> foundFooter = null;
		for (TreeItem<LayoutTreeNode> node : bannerNode.getChildren()) {
			if (node.getValue().getNodeType() == NodeType.BANNER) {
				if (node.getValue().getBanner() == banner)
					foundItem = node;
				if (footer != null && node.getValue().getBanner() == footer)
					foundFooter = node;
			}
		}
		if (foundItem != null)
			bannerNode.getChildren().remove(foundItem);
		if (foundFooter != null)
			bannerNode.getChildren().remove(foundFooter);
		Main.rwDebugInst.debugThread("LayoutPaneController", "removeBanner", MRBDebug.DETAILED,
				"  banner " + banner.getPosition() + "  removed");
		return;
	}

	private void addNewBanner() {
		List<BannerType> selectedBanners = new ArrayList<BannerType>();
		int numMissing = 7;
		PopUpScreen popUp = new PopUpScreen((Integer parm) -> {
			if (parm == PopUpScreen.OKPRESSED)
				return PopUpScreen.CLOSESCREEN;
			if (parm == PopUpScreen.CANCELPRESSED)
				return PopUpScreen.CANCELLED;
			return PopUpScreen.LEAVEOPEN;
		});
		GridPane pane = popUp.getPane();
		Label intro = new Label("The following banners are available:");
		RadioButton title = new RadioButton(Constants.BannerType.TITLE.getName());
		title.setOnAction((t) -> {
			if (title.isSelected())
				selectedBanners.add(BannerType.TITLE);
			else
				selectedBanners.remove(BannerType.TITLE);
		});
		RadioButton pageHeader = new RadioButton(Constants.BannerType.PAGEHEAD.getName());
		pageHeader.setOnAction((t) -> {
			if (pageHeader.isSelected())
				selectedBanners.add(BannerType.PAGEHEAD);
			else
				selectedBanners.remove(BannerType.PAGEHEAD);
		});
		RadioButton colHeader = new RadioButton(Constants.BannerType.COLUMNHEAD.getName());
		colHeader.setOnAction((t) -> {
			if (colHeader.isSelected())
				selectedBanners.add(BannerType.COLUMNHEAD);
			else
				selectedBanners.remove(BannerType.COLUMNHEAD);
		});
		RadioButton detail = new RadioButton(Constants.BannerType.DETAIL.getName());
		detail.setOnAction((t) -> {
			if (detail.isSelected())
				selectedBanners.add(BannerType.DETAIL);
			else
				selectedBanners.remove(BannerType.DETAIL);
			
		});
		RadioButton colFooter = new RadioButton(Constants.BannerType.COLUMNFOOT.getName());
		colFooter.setOnAction((t) -> {
			if (colFooter.isSelected())
				selectedBanners.add(BannerType.COLUMNFOOT);
			else
				selectedBanners.remove(BannerType.COLUMNFOOT);
		});
		RadioButton pageFooter = new RadioButton(Constants.BannerType.PAGEFOOT.getName());
		pageFooter.setOnAction((t) -> {
			if (pageFooter.isSelected())
				selectedBanners.add(BannerType.PAGEFOOT);
			else
				selectedBanners.remove(BannerType.PAGEFOOT);
		});
		RadioButton endPage = new RadioButton(Constants.BannerType.END.getName());
		endPage.setOnAction((t) -> {
			if (endPage.isSelected())
				selectedBanners.add(BannerType.END);
			else
				selectedBanners.remove(BannerType.END);
		});
		pane.add(intro, 0, 0);
		GridPane.setMargin(intro, new Insets(5, 5, 5, 5));
		pane.add(title, 0, 1);
		GridPane.setMargin(title, new Insets(5, 5, 5, 5));
		pane.add(pageHeader, 0, 2);
		GridPane.setMargin(pageHeader, new Insets(5, 5, 5, 5));
		pane.add(colHeader, 0, 3);
		GridPane.setMargin(colHeader, new Insets(5, 5, 5, 5));
		pane.add(detail, 0, 4);
		GridPane.setMargin(detail, new Insets(5, 5, 5, 5));
		pane.add(colFooter, 0, 5);
		GridPane.setMargin(colFooter, new Insets(5, 5, 5, 5));
		pane.add(pageFooter, 0, 6);
		GridPane.setMargin(pageFooter, new Insets(5, 5, 5, 5));
		pane.add(endPage, 0, 7);
		GridPane.setMargin(endPage, new Insets(5, 5, 5, 5));
		for (ReportBanner banner : allBanners.values()) {
			switch (banner.getBannerType()) {
			case COLUMNFOOT:
				colFooter.setVisible(false);
				numMissing--;
				break;
			case COLUMNHEAD:
				colHeader.setVisible(false);
				numMissing--;
				break;
			case DETAIL:
				detail.setVisible(false);
				numMissing--;
				break;
			case END:
				endPage.setVisible(false);
				numMissing--;
				break;
			case PAGEFOOT:
				pageFooter.setVisible(false);
				numMissing--;
				break;
			case PAGEHEAD:
				pageHeader.setVisible(false);
				numMissing--;
				break;
			case TITLE:
				title.setVisible(false);
				numMissing--;
				break;
			default:
				break;
			}
		}
		if (numMissing < 1) {
			OptionMessage.displayErrorMessage("All available banners have been created");
			return;
		}
		popUp.display();
		if (popUp.getResult().equals(PopUpScreen.CANCELLED))
			return;
		for (BannerType type : selectedBanners) {
			createBanner(type);
		}
		resetBanners();
		template.setDirty(true);
	}

	private void createBanner(BannerType type) {
		UndoRecord undoRec = null;
		if (!undoManager.getUndoUnderway()) {
			undoRec = undoManager.newRecord();
		}
		ReportBanner tmpBanner = new ReportBanner(type, type.getName(), type.getPosition(), template,
				bannerSelectModel, fieldSelectModel, thisObj);
		allBanners.put(tmpBanner.getPosition(), tmpBanner);
		template.addBanner(tmpBanner);
		insertBanner(tmpBanner);
		TreeItem<LayoutTreeNode> tmpNode = new TreeItem<LayoutTreeNode>(new LayoutTreeNode(tmpBanner));
		bannerNode.getChildren().add(tmpNode);
		tmpBanner.setTreeItem(tmpNode);
		tmpBanner.draw();
		if (!undoManager.getUndoUnderway()) {
			undoRec.newAction(this, DetailType.ADDBANNER, "", type.getName(), XmlWriter.BANNER);
			undoManager.addRecord(undoRec);
		}

	}

	private void resetGroups() {
		int groupHead = BannerType.GROUPHEAD.getPosition();
		for (int i = BannerType.GROUPHEAD.getPosition(); i < BannerType.DETAIL.getPosition(); i++) {
			ReportBanner banner = allBanners.get(i);
			if (banner == null)
				continue;
			if (banner.getBannerType() == BannerType.GROUPHEAD) {
				if (banner.getHasFooter()) {
					ReportBanner footer = allBanners.get(banner.getOtherPos());
					if (footer != null) {
						int pos = 9 - (groupHead - BannerType.GROUPHEAD.getPosition())
								+ BannerType.GROUPFOOT.getPosition();
						footer.setPosition(pos);
						banner.setPosition(groupHead);
						banner.setOtherPos(pos);
						footer.setOtherPos(groupHead);
					} else {
						banner.setHasFooter(false);
						banner.setOtherPos(0);
					}
				}
				groupHead++;
			}
		}
		allBanners.clear();
		for (ReportBanner banner : template.getBanners()) {
			allBanners.put(banner.getPosition(), banner);
		}
	}

	private void resetBanners() {
		double crntY = ruler.getRulerHeight();
		for (Entry<Integer,ReportBanner> bannerEntry : allBanners.entrySet()) {
			ReportBanner tmpBanner = bannerEntry.getValue();
			tmpBanner.moveBanner(0, crntY);
			tmpBanner.reset(bannerLayout.getWidth());
			crntY += tmpBanner.getCanvasHeight();
		}
		ObservableList<TreeItem<LayoutTreeNode>> children = bannerNode.getChildren();
		children.sort(new childComparator());
	}

	private void manageGroup(ReportBanner banner) {
		/*
		 *  select field for group
		 * 
		 */
		SortedMap<String, ReportField> groupFields = template.getSelectedFields();  // get all fields selected for banner
		GroupSelectController controller = new GroupSelectController();
		PopUpScreen popUp = new PopUpScreen(Constants.GROUPSELECTPANE, controller, (Integer parm) -> {
			if (parm == PopUpScreen.OKPRESSED) {
				if (controller.valid()) {
					if (controller.getField() == null) {
						OptionMessage.displayErrorMessage("The group must be based on a field");
						return PopUpScreen.LEAVEOPEN;
					}
					return PopUpScreen.CLOSESCREEN;
				}
			}
			if (parm == PopUpScreen.CANCELPRESSED) {
				if (controller.isDirty()) {
					if (OptionMessage
							.yesnoMessage("Group details have been changed, do you wish to abandon them"))
						return PopUpScreen.CLOSESCREEN;
				}
			}
			return PopUpScreen.LEAVEOPEN;
		});
		controller.setUpFields(template, groupFields, banner); // adds fields to table where field is not already a group field
		popUp.display(); // display popup to select field
		ReportField selectedField = controller.getField();
		if (selectedField == null)
			return;
		FieldFunction function = controller.getFunction();
		List<ReportBanner> groups = new ArrayList<ReportBanner>();
		if (banner == null) {
			int pos = -1;
			for (Entry<Integer, ReportBanner> entry : allBanners.entrySet()) {
				if (entry.getKey() >= BannerType.GROUPHEAD.getPosition()
						&& entry.getKey() < BannerType.DETAIL.getPosition()) {
					groups.add(entry.getValue());
				}
			}
			if (groups.size() > 3) {
				OptionMessage.displayErrorMessage("All available groups have been created");
				return;
			}
			if (groups.size() < 1)
				pos = BannerType.GROUPHEAD.getPosition();
			else {
				ReportBanner lastGrp = groups.get(groups.size() - 1);
				pos = lastGrp.getPosition() + 1;
			}
			// create group header banner
			createGroupBanner(selectedField,function,pos);
		}
		resetGroups();
		resetBanners();
		template.setDirty(true);
	}
	private void createGroupBanner(ReportField selectedField, FieldFunction function,int pos) {
		UndoRecord undoRec = null;
		if (!undoManager.getUndoUnderway()) {
			undoRec = undoManager.newRecord();
		}
		ReportBanner tmpBanner = new ReportBanner(BannerType.GROUPHEAD,
				"Group Header - " + selectedField.getName(), pos, template, bannerSelectModel,
				fieldSelectModel, thisObj);
		allBanners.put(tmpBanner.getPosition(), tmpBanner);
		template.addBanner(tmpBanner);
		insertBanner(tmpBanner);
		TreeItem<LayoutTreeNode> tmpNode = new TreeItem<LayoutTreeNode>(new LayoutTreeNode(tmpBanner));
		bannerNode.getChildren().add(tmpNode);
		tmpBanner.setTreeItem(tmpNode);
		ReportField tmpVar;
		if (function != null) {
			/*
			 * if function selected create variable for function and add to variables
			 */
			tmpVar = new ReportField(template);
			tmpVar.setName("Group Variable - " + selectedField.getName());
			tmpVar.setKey("variable." + "grpvar" + selectedField.getName());
			tmpVar.setReportType(ReportFieldType.VARIABLE);
			tmpVar.setFieldExp(function.getName() + "(" + selectedField.getName() + ")");
			tmpVar.setOutputType(selectedField.getOutputType());
			template.addVariable(tmpVar);
			resetTreeVariables(tmpVar);
		} else
			tmpVar = selectedField;
		// create layout field for selected field
		ReportLayout tmpLayout = new ReportLayout(template, tmpBanner, tmpVar, template.getLeftMargin() * Constants.LAYOUTDIVIDER * 10.0, 0.0);
		tmpBanner.setGroupField(tmpVar);
		tmpLayout.setName(tmpVar.getName());
		tmpLayout.setText(tmpVar.getName());
		tmpVar.setGroupSelected(true);
		tmpBanner.addField(tmpLayout);
		tmpBanner.draw();
		tmpBanner.drawFields();
		if (!undoManager.getUndoUnderway()) {
			undoRec.newAction(this, DetailType.ADDBANNER, "", tmpBanner.getName(), XmlWriter.BANNER);
			undoRec.newAction(tmpBanner, DetailType.ADDLAYOUT, "", tmpLayout.getName(), XmlWriter.BANNER);
			if(function != null)
				undoRec.newAction(this, DetailType.ADDVARIABLE, "", tmpVar.getKey()+"/"+tmpVar.getName(), XmlWriter.BANNER);
			undoManager.addRecord(undoRec);
		}

		bannerSelectModel.clearAndSelect(pos);
		fieldSelectModel.addField(tmpLayout);
		fieldSelectModel.clearSelection();
		Main.rwDebugInst.debugThread("LayoutPaneController", "createGroupBanner", MRBDebug.DETAILED,
				" banner " + tmpBanner.getName() + " created at position" + tmpBanner.getPosition());
	}

	private void moveGroupUp(ReportBanner banner) {
		int pos = banner.getPosition();
		if (pos == Constants.BannerType.GROUPHEAD.getPosition()) {
			OptionMessage.displayErrorMessage("Group is already at highest level, cannot be moved up");
			return;
		}
		int prevPos = pos - 1;
		ReportBanner prevBanner = allBanners.get(prevPos);
		if (prevBanner != null) {
			prevBanner.setPosition(pos);
			banner.setPosition(prevPos);
			allBanners.put(prevBanner.getPosition(), prevBanner);
			allBanners.put(banner.getPosition(), banner);
			Main.rwDebugInst.debugThread("LayoutPaneController", "moveGroupUp", MRBDebug.DETAILED,
					" banner " + banner.getName() + " moved to position" + pos);
			Main.rwDebugInst.debugThread("LayoutPaneController", "moveGroupUp", MRBDebug.DETAILED,
					" banner " + prevBanner.getName() + " moved to position" + prevPos);

			ReportBanner footer = allBanners.get(banner.getOtherPos());
			ReportBanner prevFooter = allBanners.get(prevBanner.getOtherPos());
			if (footer != null) {
				footer.setPosition(9 - pos - Constants.BannerType.GROUPHEAD.getPosition()
						+ Constants.BannerType.GROUPFOOT.getPosition());
				footer.setOtherPos(pos);
				banner.setOtherPos(footer.getPosition());
				allBanners.put(footer.getPosition(), footer);
				Main.rwDebugInst.debugThread("LayoutPaneController", "moveGroupUp", MRBDebug.DETAILED,
						" banner " + footer.getName() + " moved from to position" + footer.getPosition());
			}
			if (prevFooter != null) {
				prevFooter.setPosition(9 - prevPos - Constants.BannerType.GROUPHEAD.getPosition()
						+ Constants.BannerType.GROUPFOOT.getPosition());
				prevFooter.setOtherPos(prevPos);
				prevBanner.setOtherPos(prevFooter.getPosition());
				allBanners.put(prevFooter.getPosition(), prevFooter);
				Main.rwDebugInst.debugThread("LayoutPaneController", "moveGroupUp", MRBDebug.DETAILED,
						" banner " + prevFooter.getName() + " moved to position"
								+ prevFooter.getPosition());
			}
			resetBanners();
			template.setDirty(true);
		}
	}

	private void moveGroupDown(ReportBanner banner) {

	}

	private void manageGroupFooter(ReportBanner banner) {
		if (banner.getHasFooter()) {
			OptionMessage.displayErrorMessage("Group already has footer");
			return;
		}
		int headPos = banner.getPosition();
		int footPos = 9 - headPos - Constants.BannerType.GROUPHEAD.getPosition()
				+ Constants.BannerType.GROUPFOOT.getPosition();
		ReportBanner tmpBanner = new ReportBanner(BannerType.GROUPFOOT,
				"Group Footer - " + banner.getGroupField().getName(), footPos, template, bannerSelectModel,
				fieldSelectModel, thisObj);
		banner.setHasFooter(true);
		banner.setOtherPos(footPos);
		tmpBanner.setOtherPos(headPos);
		Main.rwDebugInst.debugThread("LayoutPaneController", "manageGroupFooter", MRBDebug.DETAILED,
				" banner " + tmpBanner.getName() + " created at position" + tmpBanner.getPosition());
		allBanners.put(tmpBanner.getPosition(), tmpBanner);
		template.addBanner(tmpBanner);
		insertBanner(tmpBanner);
		TreeItem<LayoutTreeNode> tmpNode = new TreeItem<LayoutTreeNode>(new LayoutTreeNode(tmpBanner));
		bannerNode.getChildren().add(tmpNode);
		tmpBanner.setTreeItem(tmpNode);
		tmpBanner.setGroupField(banner.getGroupField());
		tmpBanner.draw();
		resetGroups();
		resetBanners();
		template.setDirty(true);

	}
	private void deleteFormat(ReportFormat format) {
		if (checkFormatUse(format))
			OptionMessage.displayErrorMessage("Format in use, can not be deleted");
		else {
			template.removeFormat(format);
			resetTreeFormats();
			resetFormats();
			deselectFormat();
			template.setDirty(true);
		}
	}
	private void deleteStyle(ReportStyle style) {
		if (checkStyleUse(style))
			OptionMessage.displayErrorMessage("Style in use, can not be deleted");
		else {
			template.removeStyle(style);
			resetTreeStyles();
			resetStyles();
			deselectStyle();
			template.setDirty(true);
		}
	}
	
	private void deleteVariable(ReportField variable) {
		if (checkVariableUse(variable))
			OptionMessage.displayErrorMessage("Variable in use, can not be deleted");
		else {
			template.deleteVariable(variable);
			resetTreeVariables();
			resetStyles();
			deselectStyle();
			template.setDirty(true);
		}
		
	}

	private void createLabel() {
		boolean done = false;
		String name = "";
		while (!done) {
			name = OptionMessage.inputMessage("Enter name of new Label");
			if (name.equals(Constants.CANCELPRESSED))
				return;
			if (template.getLabels().containsKey(name.toLowerCase())) {
				OptionMessage.displayErrorMessage(name + " already exists");
				continue;
			}
			done = true;
		}
		ReportField newField = new ReportField(template);
		newField.setName(name);
		newField.setReportType(ReportFieldType.LABEL);
		newField.setFieldText(name);
		newField.setKey(("label." + name).toLowerCase());
		template.addLabel(newField);
		resetTreeLabels(newField);

	}

	public void deleteLabel(TreeItem<LayoutTreeNode> cell) {
		ReportField label = cell.getValue().getField();
		if (checkFieldDelete(label)) {
			OptionMessage.displayErrorMessage("Label can not be deleted as it is in use");
			return;
		}
		template.deleteLabel(label);
		cell.getParent().getChildren().remove(cell);
		resetAllTreeLabels();
		template.isDirty();
	}
	private boolean checkFormatUse(ReportFormat format) {
		for (ReportBanner banner : allBanners.values()) {
			for (ReportLayout layout : banner.getFields()) {
				if (layout.getFormat() == format)
					return true;
			}
		}
		for (ReportField field:selectedFields.values())
			if (field.getFormat() == format) {
				return true;
			}
		for (ReportField field:variables.values())
			if (field.getFormat() == format) {
				return true;
			}
		return false;
	}

	private boolean checkStyleUse(ReportStyle style) {
		for (ReportBanner banner : allBanners.values()) {
			if (banner.getBannerStyle() == style)
				return true;
			for (ReportLayout layout : banner.getFields()) {
				if (layout.getLayoutStyle() == style)
					return true;
			}
		}
		return false;
	}
	private boolean checkVariableUse(ReportField variable) {
		for (ReportBanner banner : allBanners.values()) {
			for (ReportLayout layout : banner.getFields()) {
				if (layout.getField() == variable)
					return true;
			}
		}
		for (ReportField tmpVar:template.getVariables().values()) {
			if (tmpVar == variable)
				continue;
			if (tmpVar.getFieldExp().contains(variable.getName()))
				return true;
		}
		return false;
		
	}

	private class childComparator implements Comparator<TreeItem<LayoutTreeNode>> {
		@Override
		public int compare(TreeItem<LayoutTreeNode> banner1, TreeItem<LayoutTreeNode> banner2) {
			Integer i1 = banner1.getValue().getBanner().getPosition();
			Integer i2 = banner2.getValue().getBanner().getPosition();
			return i1.compareTo(i2);
		}
	}

	private class FieldTreeCell extends TreeCell<LayoutTreeNode> {
		private ContextMenu cellContextMenu;
		private MenuItem menu1;
		private MenuItem menu2;
		private MenuItem menu3;
		private MenuItem menu4;
		private MenuItem menu5;
		private MenuItem menu6;
		private NodeType nodeType;
		private TreeCell<LayoutTreeNode> thisCell;
		private LayoutTreeNode node = null;

		public FieldTreeCell() {
			super();
			thisCell = this;
			cellContextMenu = new ContextMenu();
			menu1 = new MenuItem("Item 1");
			menu1.setOnAction((t) -> {
				Main.rwDebugInst.debugThread("FieldTreeCell", "construct", MRBDebug.DETAILED,
						" menu action called");
				menuAction(t);
			});
			menu2 = new MenuItem("Item 2");
			menu2.setOnAction((t) -> {
				menuAction(t);
			});
			menu3 = new MenuItem("Item 3");
			menu3.setOnAction((t) -> {
				menuAction(t);
			});
			menu4 = new MenuItem("Item 4");
			menu4.setOnAction((t) -> {
				menuAction(t);
			});
			menu5 = new MenuItem("Item 5");
			menu5.setOnAction((t) -> {
				menuAction(t);
			});
			menu6 = new MenuItem("Item 6");
			menu6.setOnAction((t) -> {
				menuAction(t);
			});
			menu1.setVisible(false);
			menu2.setVisible(false);
			menu3.setVisible(false);
			menu4.setVisible(false);
			menu5.setVisible(false);
			menu6.setVisible(false);
			cellContextMenu.getItems().addAll(menu1, menu2, menu3, menu4, menu5, menu6);
			nodeType = null;
		}

		@Override
		public void updateItem(LayoutTreeNode item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
				setText(null);
				setGraphic(null);
				setContextMenu(null);
				return;
			}
			node = item;
			nodeType = node.getNodeType();
			if (nodeType == null) {
				setText(null);
				setGraphic(null);
				setContextMenu(null);
				return;
			}
			TreeCell<LayoutTreeNode> thisCell = this;
			switch (nodeType) {
			case DATABASEFIELD:
				setContextMenu(cellContextMenu);
				setText(node.getField().getName());
				setOnDragDetected((t) -> {
					/* drag was detected, start a drag-and-drop gesture */
					/* allow any transfer mode */
					Main.rwDebugInst.debugThread("FieldTreeCell", "Dragdetected", MRBDebug.DETAILED,
							"drag detected on " + getText());
					Dragboard db = thisCell.startDragAndDrop(TransferMode.ANY);
					/* Put a string on a dragboard */
					ClipboardContent content = new ClipboardContent();
					content.putString(Constants.PLACEFIELD);
					db.setContent(content);
					LayoutPane.itemBeingDragged = node.getField();
				});
				menu1.setVisible(true);
				menu2.setVisible(true);
				menu1.setText(Constants.ACTIONDELETE);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONDELETE);
				menu2.setText(Constants.ACTIONSHOW);
				menu2.setId(nodeType.toString() + "#" + Constants.ACTIONSHOW);
				break;
			case BANNER:
				ReportBanner banner = node.getBanner();
				setText(banner.getName());
				setContextMenu(cellContextMenu);
				menu1.setVisible(true);
				menu2.setVisible(true);
				menu1.setText(Constants.ACTIONDELETE);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONDELETE);
				menu2.setText(Constants.ACTIONSELECTALL);
				menu2.setId(nodeType.toString() + "#" + Constants.ACTIONSELECTALL);
				if (banner.getBannerType() == BannerType.GROUPHEAD) {
					menu3.setText(Constants.ACTIONEDIT);
					menu3.setId(nodeType.toString() + "#" + Constants.ACTIONEDIT);
					menu4.setText(Constants.ACTIONMOVEUP);
					menu4.setId(nodeType.toString() + "#" + Constants.ACTIONMOVEUP);
					menu5.setText(Constants.ACTIONMOVEDOWN);
					menu5.setId(nodeType.toString() + "#" + Constants.ACTIONMOVEDOWN);
					if (!banner.getHasFooter()) {
						menu6.setText(Constants.ACTIONADDFOOTER);
						menu6.setId(nodeType.toString() + "#" + Constants.ACTIONADDFOOTER);
						menu6.setVisible(true);
					}
					menu2.setVisible(true);
					menu3.setVisible(true);
					menu4.setVisible(true);
					menu5.setVisible(true);
				}
				break;
			case DATABASE:
				setText(node.getText());
				menu1.setVisible(true);
				menu1.setText(Constants.ACTIONSELECT);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONSELECT);
				setContextMenu(cellContextMenu);
				break;
			case BANNERS:
				setText(node.getText());
				menu1.setVisible(true);
				menu2.setVisible(true);
				menu1.setText(Constants.ACTIONADDBANNER);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONADDBANNER);
				menu2.setText(Constants.ACTIONADDGROUP);
				menu2.setId(nodeType.toString() + "#" + Constants.ACTIONADDGROUP);
				menu3.setVisible(true);
				menu3.setText(Constants.ACTIONSELECTALL);
				menu3.setId(nodeType.toString() + "#" + Constants.ACTIONSELECTALL);
				setContextMenu(cellContextMenu);
				break;
			case FORMAT:
				setText(node.getText());
				menu1.setVisible(true);
				menu2.setVisible(true);
				menu1.setText(Constants.ACTIONSHOW);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONSHOW);
				menu2.setText(Constants.ACTIONDELETE);
				menu2.setId(nodeType.toString() + "#" + Constants.ACTIONDELETE);
				setContextMenu(cellContextMenu);
				break;
			case FORMATS:
				setText(node.getText());
				menu1.setVisible(true);
				menu2.setVisible(true);
				menu1.setText(Constants.ACTIONSHOWFORMAT);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONSHOWFORMAT);
				menu2.setText(Constants.ACTIONADDFORMAT);
				menu2.setId(nodeType.toString() + "#" + Constants.ACTIONADDFORMAT);
				setContextMenu(cellContextMenu);
				break;
				
			case STYLE:
				setText(node.getText());
				menu1.setVisible(true);
				menu2.setVisible(true);
				menu1.setText(Constants.ACTIONSHOW);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONSHOWSTYLE);
				menu2.setText(Constants.ACTIONDELETE);
				menu2.setId(nodeType.toString() + "#" + Constants.ACTIONDELETE);
				setContextMenu(cellContextMenu);
				break;
			case STYLES:
				setText(node.getText());
				menu1.setVisible(true);
				menu2.setVisible(true);
				menu1.setText(Constants.ACTIONSHOWSTYLE);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONSHOWSTYLE);
				menu2.setText(Constants.ACTIONADDSTYLE);
				menu2.setId(nodeType.toString() + "#" + Constants.ACTIONADDSTYLE);
				setContextMenu(cellContextMenu);
				break;
			case FIELD:
				setText(node.getText());
				menu1.setVisible(true);
				menu2.setVisible(false);
				menu1.setText(Constants.ACTIONDELETE);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONDELETE);
				setContextMenu(cellContextMenu);
				break;
			case LABELS:
				setText(node.getText());
				menu1.setVisible(true);
				menu1.setText(Constants.ACTIONADDLABEL);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONADDLABEL);
				setContextMenu(cellContextMenu);
				break;
			case LABEL:
			case VARIABLE:
				setText(node.getField().getName());
				menu1.setVisible(true);
				menu1.setText(Constants.ACTIONDELETE);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONDELETE);
				setOnDragDetected((t) -> {
					/* drag was detected, start a drag-and-drop gesture */
					/* allow any transfer mode */
					Main.rwDebugInst.debugThread("FieldTreeCell", "Dragdetected", MRBDebug.DETAILED,
							"drag detected on " + getText());
					Dragboard db = thisCell.startDragAndDrop(TransferMode.ANY);
					/* Put a string on a dragboard */
					ClipboardContent content = new ClipboardContent();
					content.putString(Constants.PLACEFIELD);
					db.setContent(content);
					LayoutPane.itemBeingDragged = node.getField();
				});
				setContextMenu(cellContextMenu);
				break;
			case VARIABLES:
				setText(node.getText());
				menu1.setVisible(true);
				menu1.setText(Constants.ACTIONADDVARIABLE);
				menu1.setId(nodeType.toString() + "#" + Constants.ACTIONADDVARIABLE);
				setContextMenu(cellContextMenu);
				break;
			default:
				setText(node.getText());
				setContextMenu(null);
				break;
			}
		}

		private void menuAction(ActionEvent action) {
			MenuItem item = (MenuItem) action.getSource();
			String id = item.getId();
			String menuNode = id.substring(0, id.indexOf("#"));
			String menuAction = id.substring(id.indexOf("#") + 1);
			NodeType nodeType = NodeType.valueOf(menuNode);
			TreeItem<LayoutTreeNode> bannerNode;
			TreeItem<LayoutTreeNode> treeItem;
			ReportStyle style;
			ReportFormat format;
			ReportField variable;
			switch (nodeType) {
			case AVAILABLEFIELDS:
				break;
			case BANNER:
				bannerNode = thisCell.getTreeItem();
				ReportBanner banner = bannerNode.getValue().getBanner();
				switch (menuAction) {
				case Constants.ACTIONDELETE:
					if (banner != null) {
						removeBanner(banner);
					}
					break;
				case Constants.ACTIONEDIT:
					manageGroup(banner);
				case Constants.ACTIONMOVEUP:
					moveGroupUp(banner);
					break;
				case Constants.ACTIONMOVEDOWN:
					moveGroupDown(banner);
					break;
				case Constants.ACTIONADDFOOTER:
					manageGroupFooter(banner);
					break;
				case Constants.ACTIONSELECTALL:
					fieldsList.getSelectionModel().clearSelection();
					bannerNode = thisCell.getTreeItem();
					multiSelection = true;
					for (TreeItem<LayoutTreeNode> selectNode : bannerNode.getChildren()) {
						fieldsList.getSelectionModel().select(selectNode);
					}
					multiSelection = false;
					break;
				}
				break;
			case BANNERS:
				switch (menuAction) {
				case Constants.ACTIONADDBANNER:
					addNewBanner();
					break;
				case Constants.ACTIONADDGROUP:
					manageGroup(null);
					break;
				}
				break;
			case DATABASE:
				switch (menuAction) {
				case Constants.ACTIONSELECT:
					fieldsPane = new FieldPane(template);
					fieldsPane.addFieldListener(thisObj);
					fieldsPane.showFields();
					resetDatabaseFields();
					break;
				}
				break;
			case DATABASEFIELD:
				switch (menuAction) {
				case Constants.ACTIONDELETE:
					break;
				case Constants.ACTIONSHOW:
					break;
				}
				break;
			case FIELD:
				switch (menuAction) {
				case Constants.ACTIONDELETE:
					deleteField(thisCell.getTreeItem());
					break;
				}
				break;
			case LABELS:
				switch (menuAction) {
				case Constants.ACTIONADDLABEL:
					createLabel();
					break;
				}
				break;
			case LABEL:
				switch (menuAction) {
				case Constants.ACTIONDELETE:
					deleteLabel(thisCell.getTreeItem());
					break;
				}
				break;
			case OUTLINE:
				break;
			case RECORD:
				break;
			case ROOT:
				break;
			case FORMAT:
				switch (menuAction) {
				case Constants.ACTIONDELETE:
					treeItem = thisCell.getTreeItem();
					format = treeItem.getValue().getFormat();
					if (format.isDefaultFormat()) {
						OptionMessage.displayErrorMessage("You can not delete a default format");
						break;
					}
					deleteFormat(format);
					break;
				case Constants.ACTIONSHOW:
					treeItem = fieldsList.getSelectionModel().getSelectedItem();
					format= treeItem.getValue().getFormat();
					selectFormat(format);
					break;
				}
				break;
			case STYLE:
				switch (menuAction) {
				case Constants.ACTIONDELETE:
					treeItem = thisCell.getTreeItem();
					style = treeItem.getValue().getStyle();
					if (style.isDefaultStyle()) {
						OptionMessage.displayErrorMessage("You can not delete a default style");
						break;
					}
					deleteStyle(style);
					break;
				case Constants.ACTIONSHOW:
					treeItem = fieldsList.getSelectionModel().getSelectedItem();
					style = treeItem.getValue().getStyle();
					selectStyle(style);
					break;
				}
				break;
			case FORMATS:
				switch (menuAction) {
				case Constants.ACTIONSHOWFORMAT:
					showDefaultFormat = !showDefaultFormat;
					resetTreeFormats();
					break;
				case Constants.ACTIONADDFORMAT:
					String name = OptionMessage.inputMessage("Enter name of new Format");
					if (name.equals(Constants.CANCELPRESSED))
						break;
					if (formats == null)
						formats = new TreeMap<String, ReportFormat>();
					if (formats.containsKey(name.toLowerCase())) {
						OptionMessage.displayErrorMessage("Format Name Already Exists");
						break;
					}
					format = new ReportFormat();
					format.setName(name);
					format.setDefaultFormat(false);
					template.addFormat(format);
					formatSelectModel.addFormat(format);
					formatSelectModel.select(format);
					selectFormat(format);
					resetFormats();
					resetTreeFormats();
					break;
				}
				break;
			case STYLES:
				switch (menuAction) {
				case Constants.ACTIONSHOWSTYLE:
					showDefaultStyle = !showDefaultStyle;
					resetTreeStyles();
					break;
				case Constants.ACTIONADDSTYLE:
					String name = OptionMessage.inputMessage("Enter name of new Style");
					if (name.equals(Constants.CANCELPRESSED))
						break;
					if (styles == null)
						styles = new TreeMap<String, ReportStyle>();
					if (name.equalsIgnoreCase(Constants.DEFAULTSTYLENAME)
							|| styles.containsKey(name.toLowerCase())) {
						OptionMessage.displayErrorMessage("Style Name Already Exists");
						break;
					}
					style = new ReportStyle();
					style.setName(name);
					template.addStyle(style);
					styleSelectModel.addStyle(style);
					styleSelectModel.select(style);
					selectStyle(style);
					resetStyles();
					resetTreeStyles();
					break;
				}
				break;
			case VARIABLE:
				switch (menuAction) {
				case Constants.ACTIONDELETE:
					treeItem = thisCell.getTreeItem();
					variable= treeItem.getValue().getField();
					deleteVariable(variable);
					template.setDirty(true);
				}
				break;
			case VARIABLES:
				switch (menuAction) {
				case Constants.ACTIONADDVARIABLE:
					String name = OptionMessage.inputMessage("Enter name of new Variable");
					if (name.equals(Constants.CANCELPRESSED))
						break;
					if (variables == null)
						variables = new TreeMap<>();
					if (variables.containsKey(name.toLowerCase())) {
						OptionMessage.displayErrorMessage("Variable Name Already Exists");
						break;
					}
					variable = new ReportField(template);
					variable.setName(name);
					variable.setKey("variable." + name);
					variable.setReportType(ReportFieldType.VARIABLE);
					variable.setOutputType(Constants.OUTPUTTYPE.TEXT);
					template.addVariable(variable);
					resetTreeVariables(variable);
					template.setDirty(true);
					break;
				default:
					break;
				}

				break;
			default:
				break;

			}

		}
	}

	/**
	 * Undo/Redo handling
	 * 
	 * General undo/redo for all classes, triggered by ctrl-z and ctrl-y in
	 * LayoutPane
	 */
	public void undo() {
		Main.rwDebugInst.debugThread("LayoutPaneController", "undo", MRBDebug.DETAILED, "undo being run ");
		undoManager.setUndoUnderway(true);
		undoManager.undo();
		undoManager.setUndoUnderway(false);
		resetFields();
	}

	public void redo() {
		Main.rwDebugInst.debugThread("LayoutPaneController", "undo", MRBDebug.DETAILED, "redo being run ");
		undoManager.setUndoUnderway(true);
		undoManager.redo();
		undoManager.setUndoUnderway(false);
		resetFields();

	}

	/*
	 * Used to bubble up undo/redo to LayoutPane
	 */
	public void undoDetected() {

	}

	/*
	 * Undo/Redo handling for actions controlled by this class
	 */
	@Override
	public void undo(List<UndoFieldDetail> changes) {
		Main.rwDebugInst.debugThread("LayoutPaneController", "undo (changes)", MRBDebug.DETAILED,
				"  changes - " + changes.size());
		for (UndoFieldDetail detail : changes) {
			switch (detail.getFieldName()) {
			case XmlWriter.BOTTOMMARGIN:
				bottomMargin.setText(detail.getBeforeValue());
				bottomMarginChg(detail.getBeforeValue());
				break;
			case XmlWriter.LEFTMARGIN:
				leftMargin.setText(detail.getBeforeValue());
				leftMarginChg(detail.getBeforeValue());
				break;
			case XmlWriter.RIGHTMARGIN:
				rightMargin.setText(detail.getBeforeValue());
				rightMarginChg(detail.getBeforeValue());
				break;
			case XmlWriter.TOPMARGIN:
				topMargin.setText(detail.getBeforeValue());
				topMarginChg(detail.getBeforeValue());
				break;
			case XmlWriter.PAPERSIZE:
				paperSizeChg(detail.getBeforeValue());
				break;
			case XmlWriter.PAGELAYOUT:
				selectOrientationChg(detail.getBeforeValue());
				break;
			case XmlWriter.BANNER:
				DetailType type = DetailType.findType(detail.getAction());
				switch (type) {
				case ADDBANNER:
					for (ReportBanner banner : allBanners.values()) {
						if (banner.getName().equals(detail.getAfterValue())) {
							removeBanner(banner);
							resetBanners();
							template.setDirty(true);
							break;
						}
					}
					break;
				case ADDVARIABLE:
					removeVariable(detail);
					break;
				case ADDLAYOUT:
					removeLayout(detail);
					break;
				case DELETEBANNER:
					break;
				case UNSETBANNERFOOTER:
					break;
				}
				break;
			}
		}
	}

	@Override
	public void redo(List<UndoFieldDetail> changes) {
		FieldFunction function=null;
		ReportField variable=null;
		for (UndoFieldDetail detail : changes) {
			switch (detail.getFieldName()) {
			case XmlWriter.BOTTOMMARGIN:
				bottomMargin.setText(detail.getAfterValue());
				bottomMarginChg(detail.getAfterValue());
				break;
			case XmlWriter.LEFTMARGIN:
				leftMargin.setText(detail.getAfterValue());
				leftMarginChg(detail.getAfterValue());
				break;
			case XmlWriter.RIGHTMARGIN:
				rightMargin.setText(detail.getAfterValue());
				rightMarginChg(detail.getAfterValue());
				break;
			case XmlWriter.TOPMARGIN:
				topMargin.setText(detail.getAfterValue());
				topMarginChg(detail.getAfterValue());
				break;
			case XmlWriter.PAPERSIZE:
				paperSizeChg(detail.getAfterValue());
				break;
			case XmlWriter.PAGELAYOUT:
				selectOrientationChg(detail.getAfterValue());
				break;
			case XmlWriter.BANNER:
				DetailType type = DetailType.findType(detail.getAction());
				switch (type) {
				case ADDBANNER:
					BannerType bannerType =BannerType.findType(detail.getAfterValue());
					if ( bannerType != BannerType.GROUPHEAD)
						createBanner(bannerType);
					else
						
					resetBanners();
					template.setDirty(true);
					break;
				case ADDVARIABLE:
					function = findFunction(detail);
					break;
				case ADDLAYOUT:
					variable=findVariable(detail);
					break;
				case DELETEBANNER:
					break;
				case UNSETBANNERFOOTER:
					break;
				}			
				break;
			}
		}

	}
	private void removeVariable(UndoFieldDetail detail) {
		ReportField field = findVariable(detail);
		if (field != null) {
				removeVariableNode(field);
				template.deleteVariable(field);
		}
	}
	private FieldFunction findFunction(UndoFieldDetail detail) {
		String fieldDetails[] = detail.getAfterValue().split("/");
		return FieldFunction.findFunction(fieldDetails[1]);
	}
	private ReportField findVariable (UndoFieldDetail detail) {
		String fieldDetails[] = detail.getAfterValue().split("/");
		for (ReportField field :variables.values()) {
			if (field.getKey().equals(fieldDetails[0]) ) 
				return field;
		}
		return null;
	}
	private void removeLayout(UndoFieldDetail detail) {
		
	}
}
