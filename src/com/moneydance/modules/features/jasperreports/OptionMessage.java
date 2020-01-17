package com.moneydance.modules.features.jasperreports;

import java.util.Optional;

import javax.swing.JOptionPane;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public abstract class OptionMessage {
	
	public static void displayMessage(String message) {
		if (Thread.currentThread().getName().contains("FX"))
			displayMessageFX(message);
		else
			displayMessageSwing(message);
	}
	private static void displayMessageFX(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	private static void displayMessageSwing(String message) {
		JOptionPane.showMessageDialog(null, message);
	}
	public static boolean yesnoMessage(String message) {
		if (Thread.currentThread().getName().contains("FX"))
			return yesnoMessageFX(message);
		else
			return yesnoMessageSwing(message);

	}
	public static boolean yesnoMessageFX(String message) {
		ButtonType yesButton = new ButtonType("Yes",ButtonBar.ButtonData.YES);
		ButtonType noButton = new ButtonType("No",ButtonBar.ButtonData.NO);
		Alert alert = new Alert(AlertType.CONFIRMATION,message,yesButton,noButton);
		alert.setTitle("Confirmation");
		alert.setHeaderText(null);
		alert.getButtonTypes().setAll(yesButton, noButton);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == yesButton)
			return true;
		else
			return false;
	}
	
	private static boolean yesnoMessageSwing(String message) {
        if (JOptionPane.showConfirmDialog(null,message,"Confirm", 
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
        	return true;
        }
        return false;
	}
}
