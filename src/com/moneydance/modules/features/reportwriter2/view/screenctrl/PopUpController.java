package com.moneydance.modules.features.reportwriter2.view.screenctrl;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public abstract class PopUpController {
		@FXML
		private VBox scenePane;
		@FXML 
		private HBox buttons;
		public HBox getButtons() {
			return buttons;
		}
}
