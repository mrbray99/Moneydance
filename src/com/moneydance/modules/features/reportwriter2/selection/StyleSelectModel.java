package com.moneydance.modules.features.reportwriter2.selection;

import java.util.ArrayList;
import java.util.List;

import com.moneydance.modules.features.reportwriter2.view.controls.ReportStyle;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.LayoutPaneController;

import javafx.scene.control.SingleSelectionModel;


public class StyleSelectModel  <T extends ReportStyle>extends SingleSelectionModel<T> {
	private List<T> styles;
	private T selected;
	private LayoutPaneController controller;
	public StyleSelectModel() {
		styles= new ArrayList<T>();
	}
	public void addStyle(T style) {
		styles.add(style);
	}
	public void removestyle(T style) {
		styles.remove(style);
	}
	public void setController(LayoutPaneController controller) {
		this.controller = controller;
	}

	@Override
	public void clearAndSelect(int arg0) {
		for (T style :styles) {
			if (style==selected) {
				style.setSelected(false,null);
				selected=null;
				setSelectedItem(null);
			}
		}
		select(arg0);
	}
	@Override
	public void clearSelection() {
		for (T style : styles) {
			if (style==selected) {
				style.setSelected(false,null);
			}
		}
		selected=null;
		setSelectedItem(null);
	}

	@Override
	public void clearSelection(int arg0) {
		if (arg0< styles.size() && styles.get(arg0)==selected) {
			styles.get(arg0).setSelected(false,null);
			selected=null;
			setSelectedItem(null);
		}
	}

	@Override
	public boolean isEmpty() {
		if (styles.isEmpty())
			return true;
		return false;
	}

	@Override
	public boolean isSelected(int arg0) {
		if(selected != null && arg0<styles.size() && selected ==styles.get(arg0))
			return true;
		return false;
	}

	@Override
	public void select(int arg0) {
		if (arg0<styles.size())
			select(styles.get(arg0));
	}

	@Override
	public void select(T arg0) {
		if (styles.contains(arg0)) {
				arg0.setSelected(true,controller);
				selected=arg0;
				setSelectedItem(arg0);
				setSelectedIndex(styles.indexOf(arg0));
		}
	}

	@Override
	public void selectFirst() {
		clearSelection();
		if (!styles.isEmpty()) {
			select(styles.get(0));
			setSelectedItem(selected);
		}
	}

	@Override
	public void selectLast() {
		clearSelection();
		if (!styles.isEmpty()) {
			select(styles.get(styles.size()-1));
			setSelectedItem(selected);
		}
	}

	@Override
	protected int getItemCount() {
		return styles.size();
	}

	@Override
	protected T getModelItem(int arg0) {
		return styles.isEmpty()?null:styles.get(arg0);
	}

}
