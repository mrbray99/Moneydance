package com.moneydance.modules.features.reportwriter.view;

import com.moneydance.modules.features.mrbutil.Platform;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;


public class AccelKeys {
	private KeyCombination deleteKey;
	private KeyCombination openKey;
	private KeyCombination saveKey;
	private KeyCombination closeKey1;
	private KeyCombination closeKey2;
	private KeyCombination newKey;
	public AccelKeys() {
		deleteKey = new KeyCodeCombination(KeyCode.D,KeyCombination.SHORTCUT_DOWN);
		openKey = new KeyCodeCombination(KeyCode.O,KeyCombination.SHORTCUT_DOWN);
		saveKey = new KeyCodeCombination(KeyCode.S,KeyCombination.SHORTCUT_DOWN);
		newKey = new KeyCodeCombination(KeyCode.N,KeyCombination.SHORTCUT_DOWN);
		closeKey1 = new KeyCodeCombination(KeyCode.W,KeyCombination.SHORTCUT_DOWN);
		closeKey2 = new KeyCodeCombination(KeyCode.F4,KeyCombination.SHORTCUT_DOWN);
		
	}
	public void setSceneDelete(Scene scene, Runnable rn) {
		scene.getAccelerators().put(deleteKey, rn);	
	}
	public void setSceneOpen(Scene scene, Runnable rn) {
		scene.getAccelerators().put(openKey, rn);	
	}
	public void setSceneSave(Scene scene, Runnable rn) {
		scene.getAccelerators().put(saveKey, rn);	
	}
	public void setSceneNew(Scene scene, Runnable rn) {
		scene.getAccelerators().put(newKey, rn);	
	}
	public void setSceneClose(Scene scene, Runnable rn) {
		scene.getAccelerators().put(closeKey1, rn);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ESCAPE)
					rn.run();
			}
		});
		if (Platform.isWindows())
			scene.getAccelerators().put(closeKey2, rn);
		
	}

}
