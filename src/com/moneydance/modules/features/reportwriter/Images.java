package com.moneydance.modules.features.reportwriter;

import java.io.IOException;
import java.io.InputStream;

import javafx.scene.image.Image;

public class Images {
	public Image editImg=null;
	public Image deleteImg = null;
//	public Image viewImg = null;
	public Image okImg = null;
	public Image cancelImg = null;
	public Image closeImg = null;
	public Image settingsImg = null;
	public Image helpImg = null;
	public Image addImg = null;
	public Image searchImg=null;
	public Image downloadImg = null;
	public Image csvImg = null;
	public Image spreadImg = null;
	public Image dbImg = null;
	public Image copyImg = null;
	public Image mainImg = null; 
	public Images() {
		InputStream stream = getClass().getResourceAsStream(Constants.RESOURCES+"edit_node_32px.png");
		if (stream != null) {
				editImg = new Image(stream);
			try {
				stream.close();
			}
			catch (IOException e) {}
		}
		InputStream streamDel = getClass().getResourceAsStream(Constants.RESOURCES+"delete_node_32px.png");
		if (streamDel != null) {
				deleteImg = new Image(streamDel);
			try {
				streamDel.close();
			}
			catch (IOException e) {}
		}
	/*	InputStream streamView = getClass().getResourceAsStream(Constants.RESOURCES+"print_32px.png");
		if (streamView != null) {
			viewImg = new Image(streamView);
			try {
				streamView.close();
			}
			catch (IOException e) {}
		} */
		InputStream streamOk = getClass().getResourceAsStream(Constants.RESOURCES+"ok_32px.png");
		if (streamOk != null) {
				okImg = new Image(streamOk);
			try {
				streamOk.close();
			}
			catch (IOException e) {}
		}
		InputStream streamCancel = getClass().getResourceAsStream(Constants.RESOURCES+"cancel_32px.png");
		if (streamCancel != null) {
			cancelImg = new Image(streamCancel);
			try {
				streamCancel.close();
			}
			catch (IOException e) {}
		}
		InputStream streamClose = getClass().getResourceAsStream(Constants.RESOURCES+"exit_32px.png");
		if (streamClose != null) {
				closeImg = new Image(streamClose);
			try {
				streamClose.close();
			}
			catch (IOException e) {}
		}
		InputStream streamSettings = getClass().getResourceAsStream(Constants.RESOURCES+"settings_32px.png");
		if (streamSettings!= null) {
			settingsImg = new Image(streamSettings);
			try {
				streamSettings.close();
			}
			catch (IOException e) {}
		}
		InputStream streamAdd = getClass().getResourceAsStream(Constants.RESOURCES+"add_node_32px.png");
		if (streamAdd!= null) {
			addImg = new Image(streamAdd);
			try {
				streamAdd.close();
			}
			catch (IOException e) {}
		}
		InputStream streamHelp = getClass().getResourceAsStream(Constants.RESOURCES+"help_32px.png");
		if (streamHelp!= null) {
			helpImg = new Image(streamHelp);
			try {
				streamHelp.close();
			}
			catch (IOException e) {}
		}
		InputStream streamSearch = getClass().getResourceAsStream(Constants.RESOURCES+"search_folder_32px.png");
		if (streamSearch!= null) {
			searchImg = new Image(streamSearch);
			try {
				streamSearch.close();
			}
			catch (IOException e) {}
		}
		InputStream streamDownload = getClass().getResourceAsStream(Constants.RESOURCES+"download_from_cloud_32px.png");
		if (streamDownload!= null) {
			downloadImg = new Image(streamDownload);
			try {
				streamDownload.close();
			}
			catch (IOException e) {}
		}
		InputStream csvload = getClass().getResourceAsStream(Constants.RESOURCES+"icons8-export-csv-32.png");
		if (csvload!= null) {
			csvImg = new Image(csvload);
			try {
				csvload.close();
			}
			catch (IOException e) {}
		}
		InputStream spreadload = getClass().getResourceAsStream(Constants.RESOURCES+"icons8-spreadsheet-file-32.png");
		if (spreadload!= null) {
			spreadImg = new Image(spreadload);
			try {
				spreadload.close();
			}
			catch (IOException e) {}
		}
		InputStream dbload = getClass().getResourceAsStream(Constants.RESOURCES+"icons8-add-database-32.png");
		if (dbload!= null) {
			dbImg = new Image(dbload);
			try {
				dbload.close();
			}
			catch (IOException e) {}
		}
		InputStream copyload = getClass().getResourceAsStream(Constants.RESOURCES+"icons8-replicate-rows-32.png");
		if (copyload!= null) {
			copyImg = new Image(copyload);
			try {
				copyload.close();
			}
			catch (IOException e) {}
		}
		InputStream mainload = getClass().getResourceAsStream(Constants.RESOURCES+"mrb icon2.png");
		if (mainload!= null) {
			mainImg = new Image(mainload);
			try {
				mainload.close();
			}
			catch (IOException e) {}
		}
	}

}
