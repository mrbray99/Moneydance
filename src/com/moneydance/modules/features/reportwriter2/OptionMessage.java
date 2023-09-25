package com.moneydance.modules.features.reportwriter2;

import java.util.Optional;

import javax.swing.JOptionPane;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;

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
		DialogPane dialogPane=alert.getDialogPane();
		dialogPane.getStyleClass().add(Constants.OPTIONMESSAGES);
		dialogPane.getStylesheets().add("file:///"+Main.extensionDir.replace("\\","/") +"/"+ Constants.STYLESHEET);
		alert.showAndWait();
	}
	private static void displayMessageSwing(String message) {
		JOptionPane.showMessageDialog(null, message,"Report Writer",JOptionPane.INFORMATION_MESSAGE);
	}
	public static void displayErrorMessage(String message) {
		if (Thread.currentThread().getName().contains("FX"))
			displayErrorMessageFX(message);
		else
			displayErrorMessageSwing(message);
	}
	private static void displayErrorMessageFX(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(message);
		DialogPane dialogPane=alert.getDialogPane();
		dialogPane.getStyleClass().add(Constants.OPTIONMESSAGES);
		dialogPane.getStylesheets().add("file:///"+Main.extensionDir.replace("\\","/") +"/"+ Constants.STYLESHEET);
		alert.showAndWait();
	}
	private static void displayErrorMessageSwing(String message) {
		JOptionPane.showMessageDialog(null, message,"Report Writer",JOptionPane.ERROR_MESSAGE);
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
		alert.setTitle("Confirm");
		alert.setHeaderText(null);
		alert.getButtonTypes().setAll(yesButton, noButton);
		DialogPane dialogPane=alert.getDialogPane();
		dialogPane.getStyleClass().add(Constants.OPTIONMESSAGES);
		dialogPane.getStylesheets().add("file:///"+Main.extensionDir.replace("\\","/") +"/"+ Constants.STYLESHEET);
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
	public static boolean okMessage(String message) {
		if (Thread.currentThread().getName().contains("FX"))
			return okMessageFX(message);
		else
			return okMessageSwing(message);

	}
	public static boolean okMessageFX(String message) {
		ButtonType okButton = new ButtonType("OK",ButtonBar.ButtonData.OK_DONE);
		Alert alert = new Alert(AlertType.CONFIRMATION,message,okButton);
		alert.setTitle("Confirm");
		alert.setHeaderText(null);
		alert.getButtonTypes().setAll(okButton);
		DialogPane dialogPane=alert.getDialogPane();
		dialogPane.getStyleClass().add(Constants.OPTIONMESSAGES);
		dialogPane.getStylesheets().add("file:///"+Main.extensionDir.replace("\\","/") +"/"+ Constants.STYLESHEET);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == okButton)
			return true;
		else
			return false;
	}
	private static boolean okMessageSwing(String message) {
        if (JOptionPane.showConfirmDialog(null,message,"Confirm", 
	            JOptionPane.OK_OPTION,
	            JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION){
        	return true;
        }
        return false;
	}
	public static String inputMessage(String message) {
		if (Thread.currentThread().getName().contains("FX"))
			return inputMessageFX(message);
		else
			return inputMessageSwing(message);

	}
	private static String inputMessageFX(String message) {
		TextInputDialog tf = new TextInputDialog();
		tf.setTitle("Data Input");
		tf.setHeaderText(message);
		DialogPane dialogPane=tf.getDialogPane();
		dialogPane.getStyleClass().add(Constants.OPTIONMESSAGES);
		dialogPane.getStylesheets().add("file:///"+Main.extensionDir.replace("\\","/") +"/"+ Constants.STYLESHEET);
		Optional<String> result = tf.showAndWait();
		if (result.isPresent())
			return tf.getResult();
		return Constants.CANCELPRESSED;
	}
	private static String inputMessageSwing(String message) {
        return JOptionPane.showInputDialog(null,message);
	}
	public static void customBtnMessageSwing(String message,String buttonMsg, BtnCallBack btnAction) {
		Object[] options = {"OK",buttonMsg};
		int n = JOptionPane.showOptionDialog(null, message,null, JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE,null,options,options[0]);
		if (n==1)
			btnAction.callbackAction();
	}
}
abstract interface BtnCallBack {
	void callbackAction();
}
