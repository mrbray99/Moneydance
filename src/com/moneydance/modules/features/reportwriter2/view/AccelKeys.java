/*
 * Copyright (c) 2022, Michael Bray.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - The name of the author may not used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.moneydance.modules.features.reportwriter2.view;

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
		if (Platform.isWindows()) {
			scene.getAccelerators().put(closeKey2, rn);
		}
	}

}
