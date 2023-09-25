package com.moneydance.modules.features.reportwriter2.view.controls;

import javafx.scene.control.SplitPane;
import javafx.scene.control.skin.SplitPaneSkin;

public class DumbSplitPaneSkin extends SplitPaneSkin {

	  public DumbSplitPaneSkin(SplitPane splitPane) {
	    super(splitPane);
	  }

	  @Override
	  protected void layoutChildren(double x, double y, double w, double h) {
	    double[] dividerPositions = getSkinnable().getDividerPositions();
	    super.layoutChildren(x, y, w, h);
	    getSkinnable().setDividerPositions(dividerPositions);
	  }
	}