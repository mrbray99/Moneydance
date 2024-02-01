package com.moneydance.modules.features.reportwriter2.view.controls;

import com.moneydance.modules.features.reportwriter2.Constants.FieldFunction;
import com.moneydance.modules.features.reportwriter2.Constants.NodeType;

import javafx.scene.control.TreeItem;

public class LayoutTreeNode  {
	public NodeType nodeType;
	private String text;
	private ReportField field;
	private ReportBanner banner;
	private ReportStyle style;
	private ReportLayout layout;
	private ReportFormat format;
	private TreeItem<LayoutTreeNode> item;
	private FieldFunction varFunction;
	public LayoutTreeNode() {
		
	}
	public LayoutTreeNode(String text,NodeType nodeType) {
		this.text=text;
		this.nodeType=nodeType;
	}
	public LayoutTreeNode(ReportField field) {
		text =field.getFieldBean().getScreenTitle();
		nodeType = NodeType.DATABASEFIELD;
		this.field = field;
	}
	public LayoutTreeNode(ReportBanner banner) {
		text = banner.getName();
		nodeType = NodeType.BANNER;
		this.banner = banner;
	}
	public LayoutTreeNode(ReportFormat format) {
		this.format = format;
		text=format.getName();
		nodeType=NodeType.FORMAT;
	}
	public LayoutTreeNode(ReportStyle style) {
		this.style = style;
		text=style.getName();
		nodeType=NodeType.STYLE;
	}
	public LayoutTreeNode(ReportLayout layout) {
		text=layout.getField().getName();
		this.layout = layout;
		nodeType= NodeType.FIELD;
	}
	public ReportField getField() {
		return field;
	}
	public void setField(ReportField field) {
		this.field = field;
	}
	
	public ReportLayout getLayout() {
		return layout;
	}
	public void setLayout(ReportLayout layout) {
		this.layout = layout;
	}
	public ReportBanner getBanner() {
		return banner;
	}
	public void setBanner(ReportBanner banner) {
		this.banner = banner;
	}
	public NodeType getNodeType() {
		return nodeType;
	}
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ReportFormat getFormat() {
		return format;
	}
	public void setFormat(ReportFormat format) {
		this.format = format;
	}
	public ReportStyle getStyle() {
		return style;
	}
	public void setStyle(ReportStyle style) {
		this.style = style;
	}
	public TreeItem<LayoutTreeNode> getItem() {
		return item;
	}
	public void setItem(TreeItem<LayoutTreeNode> item) {
		this.item = item;
	}
	public FieldFunction getVarFunction() {
		return varFunction;
	}
	public void setVarFunction(FieldFunction varFunction) {
		this.varFunction = varFunction;
	}
	
}
