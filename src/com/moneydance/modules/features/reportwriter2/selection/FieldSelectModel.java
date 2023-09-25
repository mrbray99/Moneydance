package com.moneydance.modules.features.reportwriter2.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportLayout;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;


public class FieldSelectModel<T extends ReportLayout> extends MultipleSelectionModel<T> {
	private List<T> fields;
	private SortedMap<Integer,T> selected;
	public FieldSelectModel() {
		fields = new ArrayList<T>();
		selected=new TreeMap<Integer,T>();
	}
	public void addField(T field) {
		if (!fields.contains(field))
			fields.add(field);
	}
	public void removeField(T field) {
		if (fields.contains(field))
			fields.remove(field);
	}
	public List<T> getFields(){
		return fields;
	}
	@Override
	public void clearAndSelect(int arg0) {
		clearSelection();
		if(fields.get(arg0)!=null) {
			if (!selected.containsKey(arg0))
				selected.put(arg0, fields.get(arg0));
			fields.get(arg0).selected(true);
			setSelectedIndex(arg0);
			setSelectedItem(fields.get(arg0));
		}
	}

	@Override
	public void clearSelection() {
		Main.rwDebugInst.debugThread("FieldSelectModel", "clearSelection", MRBDebug.DETAILED, "clear all ");
		for (T field : selected.values())
			field.selected(false);
		selected.clear();
		setSelectedIndex(-1);
		setSelectedItem(null);
	}

	@Override
	public void clearSelection(int arg0) {
		Main.rwDebugInst.debugThread("FieldSelectModel", "clearSelection", MRBDebug.DETAILED, "clear index "+arg0);
		if (selected.containsKey(arg0)) {
			T field = selected.get(arg0);
			field.selected(false);
			selected.remove(arg0);
			if (selected.isEmpty()) {
				setSelectedIndex(-1);
				setSelectedItem(null);
			}
			else
				selectFirst();
		}
	}
	public void clearSelection(T field) {
		Main.rwDebugInst.debugThread("FieldSelectModel", "clearSelection", MRBDebug.DETAILED, "clear object ");
		int index = fields.indexOf(field);
		if (selected.containsKey(index)) {
			field.selected(false);
			selected.remove(index);
			if (selected.isEmpty()) {
				setSelectedIndex(-1);
				setSelectedItem(null);
			}
			else 
				select(selected.firstKey());
		}
	}
	
	@Override
	public boolean isEmpty() {
		if (selected.isEmpty())
			return true;
		return false;
	}

	@Override
	public boolean isSelected(int arg0) {
		if(selected.containsKey(arg0))
			return true;
		return false;
	}
	public boolean isSelected(T field) {
		if (selected.values().contains(field))
			return true;
		return false;
	}
	@Override
	public void select(int arg0) {
		Main.rwDebugInst.debugThread("FieldSelectModel", "select", MRBDebug.DETAILED, "select index "+arg0);
		select(fields.get(arg0));
	}

	@Override
	public void select(T arg0) {
		Main.rwDebugInst.debugThread("FieldSelectModel", "select", MRBDebug.DETAILED, "select object");
		if (fields.contains(arg0)) {
			if (selected.containsValue(arg0)) {
				setSelectedIndex(fields.indexOf(arg0));
				setSelectedItem(arg0);
			}
			else {
				selected.put(fields.indexOf(arg0), arg0);
				setSelectedIndex(fields.indexOf(arg0));
				setSelectedItem(arg0);
			}
			arg0.selected(true);
		}
	}

	@Override
	public void selectFirst() {
		if (!fields.isEmpty()) {
			select(0);
		}
	}

	@Override
	public void selectLast() {
		if (!fields.isEmpty()) {
			int last = fields.size()-1;
			select(last);
		}
	}

	@Override
	public void selectNext() {
		int next = getSelectedIndex()+1;
		if (next <fields.size()) {
			select(next);
		}
	}

	@Override
	public void selectPrevious() {
		int prev = getSelectedIndex()-1;
		if (prev >=0) {
			select(prev);
		}

	}
	@Override
	public ObservableList<Integer> getSelectedIndices() {
		ObservableList<Integer> list = FXCollections.observableArrayList();
		list.addAll(selected.keySet());
		return list;
	}
	@Override
	public ObservableList<T> getSelectedItems() {
		ObservableList<T> list = FXCollections.observableArrayList();
		list.addAll(selected.values());
		return list;
	}
	@Override
	public void selectAll() {
		for (int i=0;i<fields.size();i++) {
			select(i);
		}
		
	}
	@Override
	public void selectIndices(int arg0, int... arg1) {
		select(arg0);
		for (int i:arg1) {
			select(i);
		}	
	}
	@Override
	public void selectRange(int start, int end) {
		for (int i =start;i<=end;i++)
			select(i);
	}


}
