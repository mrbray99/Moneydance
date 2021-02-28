package com.moneydance.modules.features.reportwriter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MainMenu extends MenuBar{
	Menu menuFile;
	Menu menuReport;
	Menu menuData;
	Menu menuView;
	Menu menuHelp;
	MenuItem itemFileOpen;
	MenuItem itemFileClose;
	MenuItem itemFileSave;
	MenuItem itemFileSaveAs;
	public MainMenu(EventHandler<ActionEvent> listener) {
		menuFile = new Menu(Constants.MENUFILE);
		menuReport = new Menu(Constants.MENUREPORT);
		menuData = new Menu(Constants.MENUDATA);
		menuView = new Menu(Constants.MENUVIEW);
		menuHelp = new Menu(Constants.MENUHELP);
		getMenus().addAll(menuFile,menuReport,menuData,menuView,menuHelp);
		itemFileOpen = new MenuItem(Constants.ITEMFILEOPTIONS);
		itemFileSave = new MenuItem(Constants.ITEMFILESAVE);
		itemFileSaveAs = new MenuItem(Constants.ITEMFILESAVEAS);
		itemFileClose = new MenuItem(Constants.ITEMFILECLOSE);
		menuFile.getItems().addAll(itemFileOpen,itemFileSave,itemFileSaveAs,itemFileClose);
		itemFileOpen.setOnAction(listener);
		itemFileSave.setOnAction(listener);
		itemFileSaveAs.setOnAction(listener);
		itemFileClose.setOnAction(listener);
		
	}

}
