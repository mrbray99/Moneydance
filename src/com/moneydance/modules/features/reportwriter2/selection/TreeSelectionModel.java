package com.moneydance.modules.features.reportwriter2.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants.NodeType;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.view.controls.LayoutTreeNode;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportBanner;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportLayout;
import com.moneydance.modules.features.reportwriter2.view.screenctrl.LayoutPaneController;

import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeSelectionModel extends MultipleSelectionModel<TreeItem<LayoutTreeNode>> {

	        private final MultipleSelectionModel<TreeItem<LayoutTreeNode>> selectionModel ;
	        private final TreeView<LayoutTreeNode> tree ;
	        private final LayoutPaneController controller;

	        public TreeSelectionModel(MultipleSelectionModel<TreeItem<LayoutTreeNode>> selectionModel, TreeView<LayoutTreeNode> tree, LayoutPaneController controller) {
	            this.selectionModel = selectionModel ;
	            this.tree = tree ;
	            this.controller = controller;
	            selectionModeProperty().bindBidirectional(selectionModel.selectionModeProperty());
	        }
	        @Override
	        public ObservableList<Integer> getSelectedIndices() {
	            return selectionModel.getSelectedIndices() ;
	        }

	        @Override
	        public ObservableList<TreeItem<LayoutTreeNode>> getSelectedItems() {
	            return selectionModel.getSelectedItems() ;
	        }

	        @Override
	        public void selectIndices(int index, int... indices) {
				Main.rwDebugInst.debugThread("TreeSelectionModel", "selectIndices", MRBDebug.DETAILED,
						"selection changed");

	            List<Integer> indicesToSelect = Stream.concat(Stream.of(index), IntStream.of(indices).boxed())
	                    .filter(i -> isNodeSelectable(tree.getTreeItem(i).getValue()))
	                    .collect(Collectors.toList());


	            if (indicesToSelect.isEmpty()) {
	                return ;
	            }
	            selectionModel.selectIndices(indicesToSelect.get(0), 
	                    indicesToSelect.stream().skip(1).mapToInt(Integer::intValue).toArray());

	        }

	        @Override
	        public void selectAll() {
				Main.rwDebugInst.debugThread("TreeSelectionModel", "selectAll", MRBDebug.DETAILED,
						"selection changed");
	            List<Integer> indicesToSelect = IntStream.range(0, tree.getExpandedItemCount())
	                    .filter(i -> isNodeSelectable(tree.getTreeItem(i).getValue()))
	                    .boxed()
	                    .collect(Collectors.toList());
	            if (indicesToSelect.isEmpty()) {
	                return ;
	            }
	            selectionModel.selectIndices(0, 
	                    indicesToSelect.stream().skip(1).mapToInt(Integer::intValue).toArray());
	        }

	        @Override
	        public void selectFirst() {
				Main.rwDebugInst.debugThread("TreeSelectionModel", "selectFirst", MRBDebug.DETAILED,
						"selection changed");
	            IntStream.range(0, tree.getExpandedItemCount())
	                .filter(i -> isNodeSelectable(tree.getTreeItem(i).getValue()))
	                .findFirst()
	                .ifPresent(selectionModel::select);
	        }

	        @Override
	        public void selectLast() {
				Main.rwDebugInst.debugThread("TreeSelectionModel", "selectLast", MRBDebug.DETAILED,
						"selection changed");
	            IntStream.iterate(tree.getExpandedItemCount() - 1, i -> i - 1)
	                .limit(tree.getExpandedItemCount())
	                .filter(i -> isNodeSelectable(tree.getTreeItem(i).getValue()))
	                .findFirst()
	                .ifPresent(selectionModel::select);
	        }

	        @Override
	        public void clearAndSelect(int index) {
			Main.rwDebugInst.debugThread("TreeSelectionModel", "clear and select int", MRBDebug.DETAILED,
						"selection changed");
			TreeItem <LayoutTreeNode> node = tree.getTreeItem(index);
			if (node != null && node.getValue()!=null && ! isNodeSelectable(node.getValue()))
				return;
			List<Integer> selectedInd = new ArrayList<Integer>(selectionModel.getSelectedIndices());
			for (int ind : selectedInd)
				clearSelection(ind);
			selectionModel.select(index);
			controller.selectItem(node);
	        }
	        

	        @Override
	        public void select(int index) {
			Main.rwDebugInst.debugThread("TreeSelectionModel", "select int", MRBDebug.DETAILED,
						"selection changed");
			TreeItem <LayoutTreeNode> node = tree.getTreeItem(index);
			if (node != null && node.getValue()!=null && ! isNodeSelectable(node.getValue()))
				return;
			switch (node.getValue().getNodeType()) {
			case VARIABLE:
			case LABEL:
			case DATABASEFIELD:
				List<Integer> selectedInd = new ArrayList<Integer>(selectionModel.getSelectedIndices());
				for (int ind : selectedInd)
					clearSelection(ind);
				selectionModel.select(index);
				break;
			default:
				selectionModel.select(index);
			}
			controller.selectItem(node);
	        }

	        @Override
	        public void select(TreeItem<LayoutTreeNode> obj) {
				Main.rwDebugInst.debugThread("TreeSelectionModel", "select obj", MRBDebug.DETAILED,
						"selection changed");
	            if (isNodeSelectable(obj.getValue())) {
				switch (obj.getValue().getNodeType()) {
				case VARIABLE:
				case LABEL:
				case DATABASEFIELD:
					List<Integer> selectedInd = new ArrayList<Integer>(selectionModel.getSelectedIndices());
					for (int ind : selectedInd)
						clearSelection(ind);
					selectionModel.select(obj);
					break;
				default:
					selectionModel.select(obj);
				}
	                controller.selectItem(obj);
	            }
	        }

	        @Override
	        public void clearSelection(int index) {
			Main.rwDebugInst.debugThread("TreeSelectionModel", "clearSelection int", MRBDebug.DETAILED,
						"clearing tree item "+index);
			 selectionModel.clearSelection(index);
  			controller.deselectItem(tree.getTreeItem(index));
	        }
	        public void clearSelection(TreeItem<LayoutTreeNode> obj) {
				Main.rwDebugInst.debugThread("TreeSelectionModel", "clearSelection obj", MRBDebug.DETAILED,
						"clearing tree item "+obj.getValue().getText());
		       selectionModel.clearSelection(tree.getRow(obj));
		 	 controller.deselectItem(obj);
      }
	        @Override
	        public void clearSelection() {
			Main.rwDebugInst.debugThread("TreeSelectionModel", "clearSelection", MRBDebug.DETAILED,
						"clearing all tree items");
			List<Integer> selectedInd = new ArrayList<Integer>(selectionModel.getSelectedIndices());
			for (int ind : selectedInd)
				clearSelection(ind);
	        }
	        public void clearType(NodeType type) {
				Main.rwDebugInst.debugThread("TreeSelectionModel", "clearSelection", MRBDebug.DETAILED,
						"clearing all tree items of type "+type.name());
			List<Integer> selectedInd = new ArrayList<Integer>(selectionModel.getSelectedIndices());
			for (int ind : selectedInd) {
				if (tree.getTreeItem(ind).getValue().getNodeType().equals(type))
					clearSelection(ind);			
			}
	
	        }

	        @Override
	        public boolean isSelected(int index) {
	            return selectionModel.isSelected(index);
	        }

	        @Override
	        public boolean isEmpty() {
	            return selectionModel.isEmpty();
	        }

	        @Override
	        public void selectPrevious() {
				Main.rwDebugInst.debugThread("TreeSelectionModel", "selectPrevious", MRBDebug.DETAILED,
						"selection changed");
	            int current = selectionModel.getSelectedIndex() ;
	            if (current > 0) {
	                IntStream.iterate(current - 1, i -> i - 1).limit(current)
	                    .filter(i ->  isNodeSelectable(tree.getTreeItem(i).getValue()))
	                    .findFirst()
	                    .ifPresent(selectionModel::select);
	            }
	        }

	        @Override
	        public void selectNext() {
				Main.rwDebugInst.debugThread("TreeSelectionModel", "selectNext", MRBDebug.DETAILED,
						"selection changed");
	            int current = selectionModel.getSelectedIndex() ;
	            if (current < tree.getExpandedItemCount() - 1) {
	                IntStream.range(current + 1, tree.getExpandedItemCount())
	                    .filter(i ->  isNodeSelectable(tree.getTreeItem(i).getValue()))
	                    .findFirst()
	                    .ifPresent(selectionModel::select);
	            }
	        }
	        public boolean isNodeSelectable (LayoutTreeNode node) {
	      	  switch ( node.getNodeType()){
	  		case ROOT:
			case OUTLINE:
			case AVAILABLEFIELDS:
			case BANNERS:
			case LABELS:
			case FUNCTIONS:
			case DATABASE:
			case RECORD:
			case STYLES:
			case FORMATS:
			case VARIABLES:
				Main.rwDebugInst.debugThread("TreeSelectionModel", "isNodeSelectable", MRBDebug.DETAILED,
						"node type "+node.getNodeType().name()+" not selectable");
				return false; 
			case BANNER:
			case LABEL:
			case VARIABLE:
			case FIELD:
			case DATABASEFIELD:
			case FUNCTION:
			case FORMAT:
			case STYLE:
				Main.rwDebugInst.debugThread("TreeSelectionModel", "isNodeSelectable", MRBDebug.DETAILED,
						"node type "+node.getNodeType().name()+"selectable");
				return true;
	      	  }
	      	  return false;
	        }
}
