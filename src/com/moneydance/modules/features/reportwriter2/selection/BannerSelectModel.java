package com.moneydance.modules.features.reportwriter2.selection;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportBanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;


public class BannerSelectModel<T extends ReportBanner> extends MultipleSelectionModel<T> {
	private SortedMap<Integer, T> banners;
	private SortedMap<Integer,T> selected;
	public BannerSelectModel() {
		banners = new TreeMap<Integer,T>();;
		selected = new TreeMap<Integer,T>();
	}
	public void addBanner(T banner) {
		banners.put(banner.getPosition(),banner);
	}
	public void removeBanner(T banner) {
		banners.remove(banner.getPosition());
		if (selected.containsValue(banner))
			selected.remove(banner.getPosition());
		if (getSelectedItem()==banner) {
			setSelectedItem(null);
			setSelectedIndex(-1);
		}
	}
	@Override
	public void clearAndSelect(int arg0) {
		selected.clear();
		setSelectedItem(null);
		setSelectedIndex(-1);
		select(arg0);
	}

	@Override
	public void clearSelection() {
		Main.rwDebugInst.debugThread("BannerSelectModel", "clearSelection", MRBDebug.DETAILED, "banners cleared");
		for (ReportBanner banner : selected.values())
			banner.setSelected(false);
		selected.clear();
		setSelectedItem(null);		
		setSelectedIndex(-1);
	}
	public void clearSelection(ReportBanner banner) {
		Main.rwDebugInst.debugThread("BannerSelectModel", "clearSelection banner", MRBDebug.DETAILED, "banner deselected");
		int foundKey=-1;
		for (Entry<Integer,T>entry : selected.entrySet()) {
			if (entry.getValue().equals(banner))
				foundKey = entry.getKey();
		}
		if (foundKey > -1) {
			selected.get(foundKey).setSelected(false);
			selected.remove(foundKey);
		}
		if (selected.isEmpty()) {
			setSelectedItem(null);
			setSelectedIndex(-1);
		}
		else {
			setSelectedItem(selected.get(selected.firstKey()));
			setSelectedIndex(selected.firstKey());
		}
	}
	@Override
	public void clearSelection(int arg0) {
		for (T banner : selected.values()) {
			if (banner.getPosition()==arg0) {
				banner.setSelected(false);
			}
		}
		if (selected.isEmpty()) {
			setSelectedItem(null);
			setSelectedIndex(-1);
		}
		else {
			setSelectedItem(selected.get(selected.firstKey()));
			setSelectedIndex(selected.firstKey());
		}
	}

	@Override
	public boolean isEmpty() {
		if (banners.isEmpty())
			return true;
		return false;
	}

	@Override
	public boolean isSelected(int arg0) {
		if (selected.containsKey(arg0))
			return true;
		return false;
	}
	public boolean isSelected(T arg0) {
		if(selected.containsValue(arg0))
				return true;
		return false;
	}

	@Override
	public void select(int arg0) {
		if (banners.containsKey(arg0)) {
			select(banners.get(arg0));
		}
	}

	@Override
	public void select(T arg0) {
		Main.rwDebugInst.debugThread("BannerSelectModel", "select", MRBDebug.DETAILED, "selecting "+arg0.getName());
		if (banners.containsKey(arg0.getPosition()) && !selected.containsKey(arg0.getPosition())) {
				selected.put(arg0.getPosition(),arg0);
				setSelectedItem(arg0);
				setSelectedIndex(arg0.getPosition());
		}
	}

	@Override
	public void selectFirst() {
		clearSelection();
		if (!banners.isEmpty()) {
			selected.put(banners.get(0).getPosition(),banners.get(0));
			banners.get(0).setSelected(true);
			setSelectedItem(banners.get(0));
		}
	}

	@Override
	public void selectLast() {
		clearSelection();
		for (int i = banners.size();i>0;i--) {
			if (banners.get(i) != null) {
				selected.put(banners.get(i).getPosition(),banners.get(i));
				banners.get(i).setSelected(true);
				setSelectedItem(banners.get(i));
			}
		}
	}

	@Override
	public void selectNext() {
		T found=null;
		T next = null;
		T selectedItem = getSelectedItem();
		if (selectedItem != null) {
			int i;
			int j;
			for (i=0;i<banners.size();i++) {
				if (banners.get(i) == selectedItem)
					found = banners.get(i);
			}
			if (found !=null) {
				for (j=i+1;j<banners.size();j++) {
					if (banners.get(j) != null)
						next = banners.get(j);
				}
			}
			if (next != null && !next.isSelected()) {
				selected.put(next.getPosition(),next);
				next.setSelected(true);
				setSelectedItem(next);
			}
		}
	}

	@Override
	public void selectPrevious() {
		T found=null;
		T previous= null;
		T selectedItem=getSelectedItem();
		if (selectedItem != null) {
			int i;
			int j;
			for (i=banners.size();i>=0;i--) {
				if (banners.get(i) == selectedItem)
					found = banners.get(i);
			}
			if (found !=null) {
				for (j=i-1;j>=0;j--) {
					if (banners.get(j) != null)
						previous = banners.get(j);
				}
			}
			if (previous != null && !previous.isSelected()) {
				clearSelection();
				selected.put(previous.getPosition(),previous);
				previous.setSelected(true);
				setSelectedItem(previous);
			}
		}
	}
	@Override
	public ObservableList<Integer> getSelectedIndices() {
		ObservableList<Integer> list = FXCollections.observableArrayList();
		for (T banner:selected.values()) {
			list.add(banner.getPosition());
		}
		return list;
	}
	@Override
	public ObservableList<T> getSelectedItems() {
		ObservableList<T> list = FXCollections.observableArrayList();
		for (T banner:selected.values()) {
			list.add(banner);
		}
		return list;
	}
	@Override
	public void selectAll() {
		selected.clear();
		for (T banner:banners.values()) {
			selected.put(banner.getPosition(), banner);
		}
		T last = selected.get(selected.lastKey());
		setSelectedItem(last);
		setSelectedIndex(last.getPosition());
		
	}
	@Override
	public void selectIndices(int arg0, int... arg1) {
		select(arg0);
		for (int arg : arg1)
			select(arg);
		
	}


}
