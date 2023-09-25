package com.moneydance.modules.features.reportwriter2.view.controls;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Constants.FieldFunction;
import com.moneydance.modules.features.reportwriter2.Constants.FuncType;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.RWException;
import com.moneydance.modules.features.reportwriter2.databeans.BeanAnnotations.BEANFIELDTYPE;
import com.moneydance.modules.features.reportwriter2.report.FieldValue;

public class ExpressionProcessor {
	private ReportField field;
	private SortedMap<String, ReportField> variables;
	private SortedMap<String, ReportField> selectedFields;
	private List<ReportField> usedVariables;

	private enum STATE {
		NORMAL, OPERATOREXPECTED, INVARNAME, INFUNCNAME, INFIELDNAME, INFUNCPARM, DOLLARFND, EXPECTNAME, EXPVARNAME,
		EXPFIELDNAME, INNUMBER, INDECIMAL, INSTRING,INOPERATOR
	};

	private enum STACKTYPE {
		VAR, FLD, STR, FUN, NUM,  OP
	};

	private STATE[] stateStack = new STATE[10];
	private StackEntry[] outputStack = new StackEntry[20];
	private StackEntry[] operatorStack = new StackEntry[20];
	private FieldValue[] valueStack = new FieldValue[20];
	private String stringValue;
	private int outputIndex;
	private int operatorInd;
	private int valueIndex;
	private String numValue;
	private ExpressionNode root;
	private ExpressionNode tail;
	private boolean errorFnd = false;
	private int errIndex = 0;
	private String errorMsg = "";
	private int seqNum;


	public ExpressionProcessor(ReportTemplate template, ReportField field) {
		this.field = field;
		variables = template.getVariables();
		selectedFields = template.getSelectedFields();
		usedVariables = new ArrayList<ReportField>();
	}

	public List<ReportField> getUsedVariables() {
		return usedVariables;
	}

	public void setUsedVariables(List<ReportField> usedVariables) {
		this.usedVariables = usedVariables;
	}
	public boolean parseExpression(String expression) {
		root = new ExpressionNode(null);
		root.setType(Constants.ExpNodeType.ROOT);
		tail=root;
		int stateIndex = 0;
		stateStack[stateIndex] = STATE.NORMAL;
		String crntName = "";
		outputIndex = 0;
		seqNum=1;
		int i = 0;
		int j=0;
		char crntNode=' ';
		boolean endFnd = false;
		ExpressionNode child=null;

		/*
		 * process string
		 */
		while (i < expression.length() && !errorFnd) {
			switch (expression.charAt(i)) {
			case '$' :
				/*
				 * Field or Variable
				 */
				if ((i+1) >= expression.length()) {
					errorFnd = true;
					errIndex = i;
					errorMsg = "$ found in wrong place";
					continue;
				}
				if (expression.charAt(i+1)!='F' &&expression.charAt(i+1)!='V') {
					errorFnd = true;
					errIndex = i;
					errorMsg = "$ not follwed by F or V";
					continue;
				}
				crntNode = expression.charAt(i+1);
				if (expression.charAt(i+2)!='(' ) {
					errorFnd = true;
					errIndex = i;
					errorMsg = "F or V  not follwed by (";
					continue;
				}
				crntName = "";
				j=i+3;
				endFnd = true;
				while (j<expression.length() && !errorFnd) {
					if (expression.charAt(j) == ')') {
						 child = new ExpressionNode(tail);
						tail.addChild(child);
						if (crntNode =='F')
							child.setType(Constants.ExpNodeType.FIELD);
						else
							child.setType(Constants.ExpNodeType.VARIABLE);
						child.setValue(crntName);
						j++;
						endFnd=false;
						break;
					}
					if (String.valueOf(expression.charAt(j)).matches("[a-z]|[A-Z]|[0-9]|\\s")) {
						crntName += expression.charAt(j);
						j++;
						continue;
					}
					errorFnd=true;
					errIndex=j;
					errorMsg="Invalid character ("+expression.charAt(j)+") in field/variable name ";
					continue;
				}
				if (endFnd) {
					errorFnd=true;
					errIndex=j;
					errorMsg= "Unexpected end of expression, missing )";
				}
				if (child != null) {
					child.setStart(i);
					child.setEnd(j);
				}
				i=j;
				break;
			case '#':
				/*
				 * Function
				 */
				j=i+1;
				crntName = "";
				endFnd = true;
				while (j<expression.length() &&!errorFnd) {
					if (String.valueOf(expression.charAt(j)).matches("[a-z]|[A-Z]|[0-9]")) {
						crntName += expression.charAt(j);
						j++;
						continue;
					}
					if (expression.charAt(j)=='(') {
						child = new ExpressionNode(tail);
						tail.addChild(child);
						child.setType(Constants.ExpNodeType.FUNCTION);
						child.setValue(crntName);
						tail=child;					
						j++;
						endFnd=false;
						if (expression.charAt(j)== ')') { // no parameters in function
							j++;
							tail=tail.getParent();
						}
						else {
							child = new ExpressionNode(tail);
							tail.addChild(child);
							child.setType(Constants.ExpNodeType.PARAMETER);
							tail = child;
						}
						break;
					}
					errorFnd=true;
					errIndex=j;
					errorMsg="Invalid character ("+expression.charAt(j)+") in function name ";
					continue;
				}
				if (endFnd) {
					errorFnd=true;
					errIndex=j;
					errorMsg= "Unexpected end of expression, missing (";
				}
				if (child != null) {
					child.setStart(i);
					child.setEnd(j);
				}
				i=j;
				break;
			case '(':
				/*
				 * Start of sub expression
				 */
				child = new ExpressionNode(tail);
				tail.addChild(child);
				child.setType(Constants.ExpNodeType.BRACKET);
				tail=child;
				if (child != null) {
					child.setStart(i);
					child.setEnd(i);
				}
				i++;
				break;
			case '"' :
				/*
				 * Start of string
				 */
				child = new ExpressionNode(tail);
				tail.addChild(child);
				child.setType(Constants.ExpNodeType.STRING);
				stringValue = "";
				j=i+1;
				boolean stringFnd = false;
				while (j<expression.length() && !errorFnd && !stringFnd) {
					if (expression.charAt(j)=='"') {
						stringFnd = true;
						child.setValue(stringValue);
						break;
					}
					stringValue += expression.charAt(j);
					j++;
				}
				if (!stringFnd) {
					errorFnd= true;
					errIndex = j;
					errorMsg = "String not terminated";
					break;
				}
				if (child != null) {
					child.setStart(i);
					child.setEnd(j+1);
				}
				i=j+1;
				break;
			case ',' :
				/*
				 * parameter
				 * 
				 */
				if (tail.getType()!=Constants.ExpNodeType.PARAMETER) {
					errorFnd= true;
					errIndex = i;
					errorMsg = "Comma [,] found outside a function";
					break;
				}
				tail=tail.getParent();
				child = new ExpressionNode(tail);
				tail.addChild(child);
				child.setType(Constants.ExpNodeType.PARAMETER);
				tail = child;
				i++;
				break;
			case ')' :
				/*
				 * end of subexpression or function parameters
				 */
				if (tail.getType()==Constants.ExpNodeType.PARAMETER) {
					tail = tail.getParent();
					tail = tail.getParent();
					i++;
					break;
				}
				if (tail.getType()==Constants.ExpNodeType.BRACKET) {
					tail=tail.getParent();
					i++;
					break;
				}
				errorFnd = true;
				errIndex = i;
				errorMsg = "Misplaced ')' found";
				break;
			case '+':
			case '-':
			case '*':
			case '/':
				/*
				 * operator
				 */
				child = new ExpressionNode(tail);
				tail.addChild(child);
				child.setType(Constants.ExpNodeType.OPERATOR);
				child.setValue(String.valueOf(expression.charAt(i)));
				if (child != null) {
					child.setStart(i);
					child.setEnd(i);
				}
				i++;
				break;
			default:
				/*
				 * Check if Number
				 */
				String tempStr = String.valueOf(expression.charAt(i));
				if (tempStr.matches("[0-9]")) {
					/*
					 * number found
					 */
					j=i+1;
					numValue=tempStr;
					boolean endNumFnd=false;
					boolean decFnd=false;
					while (j<expression.length() && !endNumFnd&&!errorFnd) {
						if (expression.charAt(j) == '.') {
							if (decFnd) {
								errorFnd = true;
								errIndex = j;
								errorMsg="Duplicate period [.] found in number";
								break;
							}
							else {
								decFnd = true;
								numValue += expression.charAt(j);
								j++;
							}
						}
						else {
							if (String.valueOf(expression.charAt(j)).matches("[0-9]")) {
								numValue+=expression.charAt(j);
								j++;
							}
							else {
								child = new ExpressionNode(tail);
								tail.addChild(child);
								child.setType(Constants.ExpNodeType.NUMBER);
								child.setValue(numValue);
								endNumFnd=true;
							}
						}
					}
					if (child != null) {
						child.setStart(i);
						child.setEnd(j);
					}
					i=j;
				}
				else {
					errorFnd = true;
					errIndex = j;
					errorMsg="Invalid character found ["+tempStr+"]";					
				break;
				}
			}
		}
		if (errorFnd) {
			OptionMessage.displayErrorMessage("Error " + errorMsg + " at character " + errIndex + " "
					+ expression.substring(0, errIndex));
			return false;
		}
		tail=root;
		endFnd = false;
		errorFnd = false;
		outputIndex = 0;
		operatorInd = 0;
		processNode(root);
		if (errorFnd) {
			OptionMessage.displayErrorMessage("Error " + errorMsg + " at character " + errIndex + " "
					+ expression.substring(0, errIndex));
			return false;
		}
		emptyOperatorStack(0);
		return true;
	}
	private Boolean processNode(ExpressionNode node) {
		switch (node.getType()) {
		case BRACKET:
			int brackOperatorInd = operatorInd;
			StackEntry brackEntry = new StackEntry(STACKTYPE.OP, "", "(", 0.0,seqNum++);
			operatorStack[operatorInd++]= brackEntry;
			for (ExpressionNode brackNode:node.getChildren()) {
				if (processNode(brackNode))
					break;
				node.setDataType(brackNode.getDataType());
			}
			emptyOperatorStackInd(brackOperatorInd);
			break;
		case FIELD:
			ReportField tmpField = null;
			for (ReportField selField:selectedFields.values()) {
				if (selField.getName().equalsIgnoreCase(node.getValue()))
					tmpField = selField;
			}
			if (tmpField==null) {
				errorFnd = true;
				errIndex=node.getStart();
				errorMsg="Unknown Field - "+node.getValue();
				break;
			}
			if (field.getOutputType()==Constants.NUMBEROUTPUTTYPE) {
				if (tmpField.getFieldType()!=BEANFIELDTYPE.DOUBLE
					&&tmpField.getFieldType()!=BEANFIELDTYPE.INTEGER
					&&tmpField.getFieldType()!=BEANFIELDTYPE.LONG
					&&tmpField.getFieldType()!=BEANFIELDTYPE.MONEY
					&&tmpField.getFieldType()!=BEANFIELDTYPE.PERCENT){
					errorFnd=true;
					errIndex=node.getStart();
					errorMsg="Expecting Numeric field ";
				}
				else
					node.setDataType(Constants.FuncType.NUMERIC);
			}
			if (field.getOutputType()==Constants.TEXTOUTPUTTYPE) {
				if ( tmpField.getFieldType()!= BEANFIELDTYPE.STRING
					&&tmpField.getFieldType()!=BEANFIELDTYPE.DATEINT) {
					errorFnd=true;
					errIndex=node.getStart();
					errorMsg="Expecting Text  field ";
				}
				else
					node.setDataType(Constants.FuncType.STRING);
			}
			String fieldName =node.getValue();
			StackEntry fldEntry = new StackEntry(STACKTYPE.FLD, fieldName, "", 0.0,seqNum++);
			fldEntry.setNode(node);
			node.setField(tmpField);
			outputStack[outputIndex++] = fldEntry;
		
			break;
		case FUNCTION:
			FieldFunction tmpFunction = FieldFunction.findFunction(node.getValue());
			if (tmpFunction == null) {
				errorFnd=true;
				errIndex = node.getStart();
				errorMsg = "Unknown function - "+node.getValue();
			}
			else {
				int numParms = 0;
				for (ExpressionNode tmpNode:node.getChildren())
					if (tmpNode.getType()==Constants.ExpNodeType.PARAMETER)
						numParms++;
				if (tmpFunction.getNumParms()!= numParms) {
					errorFnd=true;
					errIndex = node.getStart();
					errorMsg = "Invalid number of parameters - "+tmpFunction.getNumParms()+ " expected "+node.getChildren().size()+" found";
				}
				int parmInd = 0;
				if (errorFnd)
					break;
				node.setFunc(tmpFunction);
				String funcName = node.getValue();
				StackEntry funcEntry = new StackEntry(STACKTYPE.FUN, funcName, "", 0.0,seqNum++);
				int oldOperatorInd = operatorInd;
				operatorStack[operatorInd++] = funcEntry;
				funcEntry.setNode(node);
				funcEntry.setPrecedence(1);
				if (node.getChildren().size()>0) {
					for (ExpressionNode childNode : node.getChildren()) {
						if (processNode(childNode)) {
							break;
						}
						if (childNode.getDataType()!= tmpFunction.getParms().get(parmInd)) {
								errorFnd = true;
								errIndex=node.getStart();
								errorMsg = "Parameter "+(parmInd+1)+" data type does not match function definition "+node.getValue();
								break;
							}
						parmInd++;
					}
					
					if (errorFnd)
						break;
				}
				emptyOperatorStackInd(oldOperatorInd);
			}
			break;
		case NUMBER:
			if (field.getOutputType() != Constants.NUMBEROUTPUTTYPE) {
				errorFnd = true;
				errIndex = node.getStart();
			}
			node.setDataType(Constants.FuncType.NUMERIC);
			StackEntry numEntry = new StackEntry(STACKTYPE.NUM, "", "",Double.valueOf(node.getValue()),seqNum++);
			outputStack[outputIndex++] = numEntry;
			numEntry.setNode(node);
			break;
		case OPERATOR:
			if (field.getOutputType()!=Constants.NUMBEROUTPUTTYPE) {
				errorFnd = true;
				errIndex =node.getStart();
				errorMsg = "Numeric Operator found in Text Expression";
			}
			processOperator(node.getValue());
			break;
		case PARAMETER:
			for (ExpressionNode parmNode: node.getChildren()) {
				if (processNode(parmNode))
					break;
				node.setDataType(parmNode.getDataType());
			}
			break;
		case ROOT:
			for (ExpressionNode rootNode:node.getChildren()) {
				if (processNode(rootNode))
					break;
			}

			break;
		case STRING:
			if (field.getOutputType()!=Constants.TEXTOUTPUTTYPE) {
				errorFnd = true;
				errIndex =node.getStart();
				errorMsg = "Text found in Numeric Expression";
			}
			node.setDataType(Constants.FuncType.STRING);
			StackEntry strEntry = new StackEntry(STACKTYPE.STR, "", "", 0.0,seqNum++);
			outputStack[outputIndex++] = strEntry;
			strEntry.setNode(node);
			break;
		case VARIABLE:
			ReportField tmpVariable = variables.get("variable."+node.getValue());
			if (tmpVariable==null) {
				errorFnd = true;
				errIndex = node.getStart();
				errorMsg = "Unknown Variable - "+node.getValue();
			}
			else {
				 if (tmpVariable.getOutputType() == Constants.NUMBEROUTPUTTYPE && field.getOutputType()!=Constants.NUMBEROUTPUTTYPE) {
					errorFnd=true;
					errIndex=node.getStart();
					errorMsg="Expecting Numeric variable ";
				 }	
				 if (tmpVariable.getOutputType() == Constants.TEXTOUTPUTTYPE && field.getOutputType()!=Constants.TEXTOUTPUTTYPE) {
					errorFnd=true;
					errIndex=node.getStart();
					errorMsg="Expecting Text variable ";
				 }	
			}
			if (tmpVariable.getOutputType()==Constants.NUMBEROUTPUTTYPE)
				node.setDataType(Constants.FuncType.NUMERIC);
			else
				node.setDataType(Constants.FuncType.STRING);
			String variableName = node.getValue();
			StackEntry varEntry = new StackEntry(STACKTYPE.VAR, variableName, "", 0.0,seqNum++);
			node.setField(tmpVariable);
			varEntry.setNode(node);
			outputStack[outputIndex++] = varEntry;
			break;
		default:
			break;
		
		}
		return errorFnd;
	}
	private void emptyOperatorStack(int precedence) {
		int i = operatorInd-1;
		while (i>=0 &&operatorStack[i].getPrecedence()>precedence) {
			if (!(operatorStack[i].getType()==STACKTYPE.OP && (operatorStack[i].getTextValue().charAt(0)== '(' ||operatorStack[i].getTextValue().charAt(0)==')')))
				outputStack[outputIndex++]= operatorStack[i];
			i--;
		}
		operatorInd = i+1;
	}
	private void emptyOperatorStackInd(int index) {
		int i = operatorInd-1;
		while (i>=index) {
			if (!(operatorStack[i].getType()==STACKTYPE.OP && (operatorStack[i].getTextValue().charAt(0)== '(' ||operatorStack[i].getTextValue().charAt(0)==')')))
				outputStack[outputIndex++]= operatorStack[i];
			i--;
		}
		operatorInd =index;
	}
	public FieldValue calculateValue(String expression, ResultSet results, SortedMap<Integer, FieldValue> values,SortedMap<String,FieldValue>variables,
			ReportTemplate template, boolean increment) throws RWException {
		/*
		 * validate expression and generate output string
		 */
		if (!parseExpression(expression)) {
			throw new RWException("Error parsing expression");
		}
		for (int i=0;i<outputIndex;i++) {
			StackEntry entry = outputStack[i];
			StackEntry parm1;
			StackEntry parm2;
			StackEntry parm3;
			StackEntry parm4;
			StackEntry parm5;
			FieldValue parm1Value;
			FieldValue parm2Value;
			FieldValue parm3Value;
			FieldValue parm4Value;
			FieldValue funcValue;
			Double newValue;
			FieldValue tmpValue=null;
			switch (entry.type) {
			case FLD:
				ExpressionNode fldNode =entry.getNode();
				ReportField tmpField = fldNode.getField();
				tmpValue = tmpField.getResultsValue(results);
				valueStack[valueIndex++]= tmpValue;
				break;
			case FUN:
				if (values.get(entry.getSequenceNum())==null)
					values.put(entry.getSequenceNum(),new FieldValue("",0.0,""));
				switch (entry.getNode().getFunc()) {
				case AVR:
					parm1 = outputStack[i];
					if (valueIndex > 0)
						parm1Value = valueStack[valueIndex-1];
					else 
						parm1Value=new FieldValue("",0.0,"");
					funcValue = values.get(entry.getSequenceNum());
					if (increment) {
						newValue= (funcValue.getNumeric()*funcValue.getCount()+parm1Value.getNumeric())/(funcValue.getCount()+1);
						funcValue.setNumeric(newValue);
						funcValue.setCount(parm1Value.getCount()+1);
					}
					valueStack[valueIndex-1]=funcValue;
					break;
				case CONCATENATE:
					break;
				case COUNT:
					parm1Value = values.get(entry.getSequenceNum());
					if (increment) 
						parm1Value.setCount(parm1Value.getCount()+1);
					valueStack[valueIndex-1]=parm1Value;
					break;
				case COUNTS:
					parm1Value = values.get(entry.getSequenceNum());
					if (increment) 
						parm1Value.setCount(parm1Value.getCount()+1);					
					valueStack[valueIndex-1]=parm1Value;
					break;
				case DATE:
					tmpValue = new FieldValue("",0.0,"dd/mm/yy");
					valueStack[valueIndex++]= tmpValue;
					break;
				case DAY:
					if (valueIndex > 0)
						parm1Value = valueStack[valueIndex-1];
					else
						parm1Value=new FieldValue("",0.0,"");
					break;
				case IF:
					break;
				case IFS:
					break;
				case LEFT:
					break;
				case MAX:
					if (valueIndex > 0)
						parm1Value = valueStack[valueIndex-1];
					else
						parm1Value=new FieldValue("",0.0,"");
					funcValue = values.get(entry.getSequenceNum());
					if (increment) {
						if (parm1Value.getNumeric()>funcValue.getNumeric())
							funcValue.setNumeric(parm1Value.getNumeric());
					}
					valueStack[valueIndex-1]=funcValue;
					break;
				case MID:
					break;
				case MIN:
					if (valueIndex > 0)
						parm1Value = valueStack[valueIndex-1];
					else
						parm1Value=new FieldValue("",0.0,"");
					funcValue = values.get(entry.getSequenceNum());
					if (increment) {
						if (parm1Value.getNumeric()<funcValue.getNumeric())
							funcValue.setNumeric(parm1Value.getNumeric());
					}
					valueStack[valueIndex-1]=funcValue;
					break;
				case MONTH:
					break;
				case RIGHT:
					break;
				case SUM:
					parm1 = outputStack[i];
					if (valueIndex > 0)
						parm1Value = valueStack[valueIndex-1];
					else
						parm1Value=new FieldValue("",0.0,"");
					funcValue = values.get(entry.getSequenceNum());
					if (increment) {
						newValue= funcValue.getNumeric()+parm1Value.getNumeric();
						funcValue.setNumeric(newValue);
						funcValue.setCount(parm1Value.getCount()+1);
					}
					valueStack[valueIndex-1]=funcValue;
						break;
				case TIME:
					break;
				case WEEKDAY:
					break;
				case WEEKNUM:
					break;
				case YEAR:
					break;
				default:
					break;
		
				}
				break;
			case NUM:
				tmpValue = new FieldValue("",entry.getNumValue(),"");
				valueStack[valueIndex++]= tmpValue;
				break;
			case OP:
				Double value1=0.0;
				Double value2=0.0;
				if (valueIndex > 2) {
					value1 = valueStack[valueIndex-2].getNumeric();
					value2 = valueStack[valueIndex-1].getNumeric();
					switch (entry.getNode().getValue().charAt(0)) {
					case '+':
						value1 += value2;
						break;
					case '-':
						value1 = value1 - value2;
						break;
					case '*':
						value1 = value1*value2;
						break;
					case '/':
						value1 = value1/value2;
					}
					valueStack[valueIndex-2].setNumeric(value1);
					for (int k =valueIndex-1;k<valueStack.length-1;k++)
						valueStack[k]=valueStack[k+1];
				}
				break;
			case STR:
				tmpValue = new FieldValue("",0.0,entry.getTextValue());
				valueStack[valueIndex++]= tmpValue;
				break;
			case VAR:
				ExpressionNode varNode =entry.getNode();
				ReportField tmpVar = varNode.getField();
				tmpValue = values.get(tmpVar.getName());
				valueStack[valueIndex++]= tmpValue;
				break;
			default:
				break;
			
			}
		
		}
		
		return valueStack[0];
	}

	/*
	 * while ( there is an operator o2 at the top of the operator stack which is not
	 * a left parenthesis, and (o2 has greater precedence than o1 or (o1 and o2 have
	 * the same precedence and o1 is left-associative)) ): pop o2 from the operator
	 * stack into the output queue push o1 onto the operator stack
	 */
	private void processOperator(String operator) {
		int precedence = 0;
		switch (operator.charAt(0)) {
		case '(':
		case ')':
			precedence = 1;
			break;
		case '+':
		case '-':
			precedence = 2;
			break;
		case '*':
		case '/':
			precedence = 3;
		}
		emptyOperatorStack(precedence);
		StackEntry entry = new StackEntry(STACKTYPE.OP, "Operator",String.valueOf(operator), 0.0,seqNum++);
		operatorStack[operatorInd++] = entry;
		entry.setPrecedence(precedence);

	}

	public class StackEntry {
		private STACKTYPE type;
		private String name;
		private String textValue;
		private Double numValue;
		private ExpressionNode node;
		private int precedence;
		private int sequenceNum;


		public StackEntry(STACKTYPE type, String name, String textValue, Double numValue,int seqNum) {
			this.type = type;
			this.name = name;
			this.textValue = textValue;
			this.numValue = numValue;
			this.sequenceNum = seqNum;
			

		}

		public STACKTYPE getType() {
			return type;
		}

		public void setType(STACKTYPE type) {
			this.type = type;
		}

		public String getTextValue() {
			return textValue;
		}

		public void setTextValue(String textValue) {
			this.textValue = textValue;
		}

		public Double getNumValue() {
			return numValue;
		}

		public void setNumValue(Double numValue) {
			this.numValue = numValue;
		}

		public int getPrecedence() {
			return precedence;
		}

		public void setPrecedence(int precedence) {
			this.precedence = precedence;
		}

		public ExpressionNode getNode() {
			return node;
		}

		public void setNode(ExpressionNode node) {
			this.node = node;
		}

		public int getSequenceNum() {
			return sequenceNum;
		}

		public void setSequenceNum(int sequenceNum) {
			this.sequenceNum = sequenceNum;
		}
	

	}

	public class ParamEntry {
		private FuncType type;
		private String value;
		private ReportField field;

		public FuncType getType() {
			return type;
		}

		public void setType(FuncType type) {
			this.type = type;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public ReportField getField() {
			return field;
		}

		public void setField(ReportField field) {
			this.field = field;
		}

	}
	public class ExpressionNode {
		private Constants.ExpNodeType type;
		private List<ExpressionNode> children;
		private ExpressionNode parent;
		private FuncType dataType;
		private String value;
		private FieldFunction func;
		private ReportField field;
		private int start;
		private int end;
		public ExpressionNode(ExpressionNode parent) {
			this.parent = parent;
			children = new ArrayList<ExpressionNode>();
		}
		public void addChild(ExpressionNode child) {
			children.add(child);
		}
		public Constants.ExpNodeType getType() {
			return type;
		}
		public void setType(Constants.ExpNodeType type) {
			this.type = type;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public List<ExpressionNode> getChildren(){
			return children;
		}
		public ExpressionNode getParent() {
			return parent;
		}
		public void setParent(ExpressionNode parent) {
			this.parent = parent;
		}
		public FuncType getDataType() {
			return dataType;
		}
		public void setDataType(FuncType dataType) {
			this.dataType = dataType;
		}
		public int getStart() {
			return start;
		}
		public void setStart(int start) {
			this.start = start;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}
		public FieldFunction getFunc() {
			return func;
		}
		public void setFunc(FieldFunction func) {
			this.func = func;
		}
		public ReportField getField() {
			return field;
		}
		public void setField(ReportField field) {
			this.field = field;
		}
		
		
		
	}
}
