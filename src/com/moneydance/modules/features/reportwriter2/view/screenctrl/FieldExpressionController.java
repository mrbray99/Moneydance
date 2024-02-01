package com.moneydance.modules.features.reportwriter2.view.screenctrl;

import java.util.List;
import java.util.SortedMap;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.databeans.BeanAnnotations.BEANFIELDTYPE;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Constants.FieldFunction;
import com.moneydance.modules.features.reportwriter2.Constants.NodeType;
import com.moneydance.modules.features.reportwriter2.view.controls.ExpressionProcessor;
import com.moneydance.modules.features.reportwriter2.view.controls.LayoutTreeNode;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class FieldExpressionController extends PopUpController {
	private ReportField field;
	private ReportTemplate template;
	private boolean dirty=false;
	private SortedMap<String,ReportField>variables;
	private SortedMap<String,ReportField>selectedFields;
	private TreeItem<LayoutTreeNode> rootNode;
	private TreeItem<LayoutTreeNode> fieldsNode;
	private TreeItem<LayoutTreeNode> variablesNode;
	private TreeItem<LayoutTreeNode> functionsNode;
	private TreeItem<LayoutTreeNode> dateFunctionNode;
	private TreeItem<LayoutTreeNode> numFunctionNode;
	private TreeItem<LayoutTreeNode> textFunctionNode;
	private TreeItem<LayoutTreeNode> logicalFunctionNode;
	private TreeItem<LayoutTreeNode> logicalOperatorNode;
	@FXML
	private ComboBox<String> dataType;
	@FXML
	private GridPane screenGrid;
	@FXML
	private TreeView<LayoutTreeNode> typeList;
	@FXML
	private ListView<FieldFunction> functionList;
	@FXML
	private TextArea expression;
	@FXML
	private Button validateBtn;
	@FXML
	private Label validLbl;
	public void setUpFields(ReportTemplate template,ReportField field) {
		this.template = template;
		this.field = field;
		dataType.setItems(FXCollections.observableArrayList(Constants.OUTPUTTYPE.NUMBER.getName(),Constants.OUTPUTTYPE.TEXT.getName(),Constants.OUTPUTTYPE.DATE.getName()));
		dataType.getSelectionModel().select(field.getOutputType().getName());
		dataType.valueProperty().addListener((ChangeListener<String>) (ov, oldV, newV) -> {
			field.setOutputType(Constants.OUTPUTTYPE.findName(newV));
			dirty=true;
		});
		typeList.getStyleClass().add("myTree");
		typeList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		typeList.setCellFactory((TreeView<LayoutTreeNode> arg0)-> {
				return new FieldTreeCell();
		});
		functionList.setCellFactory(new Callback<ListView<FieldFunction>, ListCell<FieldFunction>>() {
			@Override
			public ListCell<FieldFunction> call (ListView<FieldFunction> param){
				return new ListCell<FieldFunction>() {
					@Override
					protected void updateItem(FieldFunction item, boolean empty) {
						super.updateItem(item,  empty);
						if (item != null)
							setText(item.getName()+" ("+item.getNumParms()+")");
						else
							setText("");
					}
				};
				
			}
		});
		typeList.setOnMouseClicked((MouseEvent click) ->{
				String selectedText;
				if (typeList.getSelectionModel().getSelectedItem()==null)
					return;
				LayoutTreeNode node = typeList.getSelectionModel().getSelectedItem().getValue();
				if (click.getClickCount() == 2) {
					StringBuilder tempExp=new StringBuilder();
					int cursor=0;
					switch (node.getNodeType()) {
					case DATABASEFIELD:
						tempExp.append("$F("+node.getField().getName()+")");
						cursor = expression.getCaretPosition();
						selectedText = expression.getSelectedText();
						if (selectedText.length()>0)
							expression.deleteText(cursor,cursor+selectedText.length());
						expression.insertText(cursor,tempExp.toString());
						dirty=true;
						break;
					case FUNCTION:
						tempExp.append("#"+node.getVarFunction().getName()+"(");
						FieldFunction crntFunc = node.getVarFunction(); 
						for (int i=0;i<crntFunc.getNumParms();i++) {
							if (i>0)
								tempExp.append(",");
							switch (crntFunc.getParm(i)){
							case DATE:
								tempExp.append("date"+(i+1));
								break;
							case NUMERIC:
								tempExp.append("num"+(i+1));
								break;
							case STRING:
								tempExp.append("text"+(i+1));
								break;
							case OPERATOR:
								tempExp.append("operator"+(i+1));
								break;
							case LOGICAL:
								tempExp.append("logical"+(i+1));
							}
						}
						tempExp.append(")");
						cursor = expression.getCaretPosition();
						selectedText = expression.getSelectedText();
						if (selectedText.length()>0)
							expression.deleteText(cursor,cursor+selectedText.length());
						expression.insertText(cursor,tempExp.toString());
						dirty=true;
						break;
					case VARIABLE:
						tempExp.append("$V("+node.getField().getName()+")");
						cursor = expression.getCaretPosition();
						selectedText = expression.getSelectedText();
						if (selectedText.length()>0)
							expression.deleteText(cursor,cursor+selectedText.length());
						expression.insertText(cursor,tempExp.toString());
						dirty=true;
						break;
					case OPERATOR:
						tempExp.append(node.getText());
						cursor = expression.getCaretPosition();
						selectedText = expression.getSelectedText();
						if (selectedText.length()>0)
							expression.deleteText(cursor,cursor+selectedText.length());
						expression.insertText(cursor,tempExp.toString());
						dirty=true;
					default:
						break;
					}
				}
				if (click.getClickCount()==1) {
					if (node.getNodeType()==NodeType.DATABASEFIELD) {
						buildFunctionList(node.getField().getFieldType());
					}
					else
						functionList.getItems().clear();
				}
		});
		functionList.setOnMouseClicked((MouseEvent click) ->{
				if (functionList.getSelectionModel().getSelectedItem() == null)
					return;
				if (click.getClickCount()==2) {
					LayoutTreeNode node = typeList.getSelectionModel().getSelectedItem().getValue();
					if (node==null)
						return;
					if (node.getNodeType() != NodeType.DATABASEFIELD)
						return;
					String fieldName = node.getField().getName();
					FieldFunction func = functionList.getSelectionModel().getSelectedItem();
					StringBuilder tempExp = new StringBuilder();
					tempExp.append("#"+func.getName()+"($F("+fieldName+")");
					for (int i=1;i<func.getNumParms();i++) {
						tempExp.append(",parm"+(i+1));
					}
					tempExp.append(")");
					int cursor = expression.getCaretPosition();
					String selectedText = expression.getSelectedText();
					if (selectedText.length()>0)
						expression.deleteText(cursor,cursor+selectedText.length());
					expression.insertText(cursor,tempExp.toString());
					dirty=true;				
				}
		});
		functionList.getSelectionModel().selectedItemProperty().addListener((ObservableValue <? extends FieldFunction>ov, FieldFunction oldv, FieldFunction newv) ->{
				Main.rwDebugInst.debugThread("FieldDetailController", "functionList selection", MRBDebug.DETAILED,
						"row changed ");
		});
		rootNode = new TreeItem<>(new LayoutTreeNode("Outline", NodeType.ROOT));
		rootNode.getValue().setItem(rootNode);
		typeList.setRoot(rootNode);
		typeList.setShowRoot(false);
		rootNode.setExpanded(true);
		fieldsNode = new TreeItem<>(new LayoutTreeNode("Fields", NodeType.AVAILABLEFIELDS));
		fieldsNode.getValue().setItem(fieldsNode);
		variablesNode = new TreeItem<>(new LayoutTreeNode("Variables", NodeType.VARIABLES));
		variablesNode.getValue().setItem(variablesNode);
		functionsNode = new TreeItem<>(new LayoutTreeNode("Functions", NodeType.FUNCTIONS));
		functionsNode.getValue().setItem(variablesNode);
		dateFunctionNode = new TreeItem<>(new LayoutTreeNode("Date/Time", NodeType.FUNCTIONS));
		dateFunctionNode.getValue().setItem(variablesNode);
		numFunctionNode = new TreeItem<>(new LayoutTreeNode("Numeric", NodeType.FUNCTIONS));
		numFunctionNode.getValue().setItem(variablesNode);
		textFunctionNode = new TreeItem<>(new LayoutTreeNode("Text", NodeType.FUNCTIONS));
		textFunctionNode.getValue().setItem(variablesNode);
		logicalFunctionNode = new TreeItem<>(new LayoutTreeNode("Logical", NodeType.FUNCTIONS));
		logicalFunctionNode.getValue().setItem(variablesNode);
		rootNode.getChildren().addAll(fieldsNode, variablesNode, functionsNode);
		functionsNode.getChildren().addAll(dateFunctionNode, numFunctionNode, textFunctionNode,logicalFunctionNode);
		selectedFields = template.getSelectedFields();		
		for (ReportField selField : selectedFields.values()) {
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
			fieldItem.getValue().setField(selField);
			fieldItem.getValue().setItem(fieldItem);
			fieldItem.getValue().setNodeType(NodeType.DATABASEFIELD);
			fieldsNode.getChildren().add(fieldItem);
		}
		variables = template.getVariables();		
		for (ReportField variable : variables.values()) {
			if (variable == field)
				continue;
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
			fieldItem.getValue().setField(variable);
			fieldItem.getValue().setItem(fieldItem);
			fieldItem.getValue().setNodeType(NodeType.VARIABLE);
			variablesNode.getChildren().add(fieldItem);
		}
		for (FieldFunction func:FieldFunction.values()) {
			if (func.getFuncType()==Constants.FuncType.NUMERIC) {
				TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
				fieldItem.getValue().setField(field);
				fieldItem.getValue().setItem(fieldItem);
				fieldItem.getValue().setNodeType(NodeType.FUNCTION);
				numFunctionNode.getChildren().add(fieldItem);
				fieldItem.getValue().setVarFunction(func);
			}
			else
			if (func.getFuncType()==Constants.FuncType.DATE) {
				TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
				fieldItem.getValue().setField(field);
				fieldItem.getValue().setItem(fieldItem);
				fieldItem.getValue().setNodeType(NodeType.FUNCTION);
				dateFunctionNode.getChildren().add(fieldItem);
				fieldItem.getValue().setVarFunction(func);
			}
			else
			if (func.getFuncType()==Constants.FuncType.STRING) {
				TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
				fieldItem.getValue().setField(field);
				fieldItem.getValue().setItem(fieldItem);
				fieldItem.getValue().setNodeType(NodeType.FUNCTION);
				textFunctionNode.getChildren().add(fieldItem);
				fieldItem.getValue().setVarFunction(func);
			}
			if (func.getFuncType()==Constants.FuncType.LOGICAL){
				TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode());
				fieldItem.getValue().setField(field);
				fieldItem.getValue().setItem(fieldItem);
				fieldItem.getValue().setNodeType(NodeType.FUNCTION);
				logicalFunctionNode.getChildren().add(fieldItem);
				fieldItem.getValue().setVarFunction(func);
			}
		}
		logicalOperatorNode = new TreeItem<>(new LayoutTreeNode("Operators", NodeType.OPERATORS));
		logicalFunctionNode.getChildren().add(logicalOperatorNode);
		for (Constants.LogicalOperators tempType:Constants.LogicalOperators.values()) {
			TreeItem<LayoutTreeNode> fieldItem = new TreeItem<>(new LayoutTreeNode(tempType.getOperator(),NodeType.OPERATOR));
			logicalOperatorNode.getChildren().add(fieldItem);
			fieldItem.getValue().setVarFunction(Constants.FieldFunction.IF);			
		}
		if (Main.loadedIcons.validateImg == null)
			validateBtn.setText("Validate");
		else
			validateBtn.setGraphic(new ImageView(Main.loadedIcons.validateImg));

		expression.setText(field.getFieldExp());
		expression.textProperty().addListener((obs, old, newV)->{
			validLbl.setText("Not validated");
		});
		Tooltip validateTT = new Tooltip("Validate Expression");
		validateBtn.setTooltip(validateTT);
		validLbl.setText("Not validated");
	}
	private void buildFunctionList(BEANFIELDTYPE type) {
		functionList.getItems().clear();
		Main.rwDebugInst.debugThread("FieldDetailController", "buildFunctionList", MRBDebug.DETAILED,
				"field type "+ type.getValue());
		for (FieldFunction function: FieldFunction.values()) {
			List<Constants.FuncType> types = function.getParms();
			if (types.contains(type)) {
				functionList.getItems().add(function);
			}
			
		}
	}
	@FXML
	private void validateBtnClicked(ActionEvent event) {
		String express = expression.getText();
		ExpressionProcessor processor = new ExpressionProcessor(template, field);
		if(processor.parseExpression(express))
				validLbl.setText("Valid");
		else
			validLbl.setText("INVALID");
	}
	@FXML
	private void expChanged(KeyEvent key) {
		dirty= true;
		template.setDirty(true);
	}
	public boolean close() {
		if (dirty) {
			field.setFieldExp(expression.getText());
			template.setDirty(true);
		}
		return true;
	}
	public boolean cancel() {
		if (dirty) {
			if (OptionMessage.yesnoMessageFX("Expression has been changed. Do you wish to abandon these changes"))
				return true;
		}
		else
			return true;
		return false;
	}
	private class FieldTreeCell extends TreeCell<LayoutTreeNode> {

		public FieldTreeCell() {
			super();
		}
	
		@Override
		public void updateItem(LayoutTreeNode item, boolean empty) {
			super.updateItem(item, empty);
			NodeType nodeType;
			 LayoutTreeNode node = null;
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
			switch (nodeType) {
			case DATABASEFIELD:
				setText(node.getField().getName());
				break;
			case VARIABLE :
				setText(node.getField().getName());
				break;	
			case OPERATORS:
			case OPERATOR:
			case VARIABLES:
				setText(node.getText());
				break;
			case FUNCTION :
				setText(node.getVarFunction().getName() + " - "+node.getVarFunction().getReturnString());
				break;	
			case FUNCTIONS:
				setText(node.getText());
				break;
		default:
				setText(node.getText());
				break;
			}
		}

	}
}
