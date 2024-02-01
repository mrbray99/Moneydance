package com.moneydance.modules.features.reportwriter2.selection;

import java.util.ArrayList;
import java.util.List;

import com.moneydance.modules.features.reportwriter2.view.controls.ReportFormat;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.LayoutPaneController;

import javafx.scene.control.SingleSelectionModel;

public class FormatSelectModel  <T extends ReportFormat>extends SingleSelectionModel<T>{
	private List<T> formats;
	private T selected;
	private LayoutPaneController controller;
	public FormatSelectModel() {
		formats= new ArrayList<T>();
	}
	public void addFormat(T format) {
		formats.add(format);
	}
	public void removeFormat(T format) {
		formats.remove(format);
	}
	public void setController(LayoutPaneController controller) {
		this.controller = controller;
	}

	@Override
	public void clearAndSelect(int arg0) {
		for (T format :formats) {
			if (format==selected) {
				format.setSelected(false,null);
				selected=null;
				setSelectedItem(null);
			}
		}
		select(arg0);
	}
	@Override
	public void clearSelection() {
		for (T format : formats) {
			if (format==selected) {
				format.setSelected(false,null);
			}
		}
		selected=null;
		setSelectedItem(null);
	}

	@Override
	public void clearSelection(int arg0) {
		if (arg0< formats.size() && formats.get(arg0)==selected) {
			formats.get(arg0).setSelected(false,null);
			selected=null;
			setSelectedItem(null);
		}
	}

	@Override
	public boolean isEmpty() {
		if (formats.isEmpty())
			return true;
		return false;
	}

	@Override
	public boolean isSelected(int arg0) {
		if(selected != null && arg0<formats.size() && selected ==formats.get(arg0))
			return true;
		return false;
	}

	@Override
	public void select(int arg0) {
		if (arg0<formats.size())
			select(formats.get(arg0));
	}

	@Override
	public void select(T arg0) {
		if (formats.contains(arg0)) {
				arg0.setSelected(true,controller);
				selected=arg0;
				setSelectedItem(arg0);
				setSelectedIndex(formats.indexOf(arg0));
		}
	}

	@Override
	public void selectFirst() {
		clearSelection();
		if (!formats.isEmpty()) {
			select(formats.get(0));
			setSelectedItem(selected);
		}
	}

	@Override
	public void selectLast() {
		clearSelection();
		if (!formats.isEmpty()) {
			select(formats.get(formats.size()-1));
			setSelectedItem(selected);
		}
	}
	@Override
	protected int getItemCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected T getModelItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
