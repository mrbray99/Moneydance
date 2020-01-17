package com.moneydance.modules.features.loadsectrans;

import javax.swing.JCheckBox;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.Platform;


public class MyCheckBox extends JCheckBox{
	int size=16;
	public MyCheckBox() {
		super();
		this.setHorizontalAlignment(CENTER);
		if (Platform.isFreeBSD() || Platform.isUnix()) {
			if (Main.extension.selectedIcon != null) {
				MRBDebug.getInstance().debug("MyCheckBox", "construct no label", MRBDebug.DETAILED,"Using check box sel icons");
				setSelectedIcon(Main.extension.selectedIcon);
				setDisabledSelectedIcon(Main.extension.selectedIcon);
				setRolloverSelectedIcon(Main.extension.selectedIcon);
			}
			if (Main.extension.unselectedIcon != null) {
				MRBDebug.getInstance().debug("MyCheckBox", "construct  no label", MRBDebug.DETAILED,"Using check box unsel icons");
				setIcon(Main.extension.unselectedIcon);
				setDisabledIcon(Main.extension.unselectedIcon);
				setRolloverIcon(Main.extension.unselectedIcon);
			}
		}
	}
	public MyCheckBox(String labelp) {
		super (labelp);
		this.setHorizontalAlignment(CENTER);
		if (Platform.isFreeBSD() || Platform.isUnix()) {
			if (Main.extension.selectedIcon != null) {
				MRBDebug.getInstance().debug("MyCheckBox", "construct label", MRBDebug.DETAILED,"Using check box sel icons");
				setSelectedIcon(Main.extension.selectedIcon);
				setDisabledSelectedIcon(Main.extension.selectedIcon);
				setRolloverSelectedIcon(Main.extension.selectedIcon);
			}
			if (Main.extension.unselectedIcon != null) {
				MRBDebug.getInstance().debug("MyCheckBox", "construct label", MRBDebug.DETAILED,"Using check box unsel icons");
				setIcon(Main.extension.unselectedIcon);
				setDisabledIcon(Main.extension.unselectedIcon);
				setRolloverIcon(Main.extension.unselectedIcon);
			} 
		}
	}
}
