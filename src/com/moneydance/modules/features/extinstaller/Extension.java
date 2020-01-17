package com.moneydance.modules.features.extinstaller;

import com.moneydance.apps.md.controller.FeatureModule;

public class Extension {
	private String name;
	private String extensionID;
	private String downloadName;
	private String type;
	private String build;
	private String currentBuild;
	private boolean loaded;
	private boolean updateAvailable;
	private String description;
	private FeatureModule feature;
	private FeatureModule rhumbaFeature;
	private Extension rhumbaExt;
	private Extension webServerExt;
	private String url;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExtensionID() {
		return extensionID;
	}
	public void setExtensionID(String extensionID) {
		this.extensionID = extensionID;
	}
	public String getDownloadName() {
		return downloadName;
	}
	public void setDownloadName(String downloadName) {
		this.downloadName = downloadName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBuild() {
		if (build == null)
			build = " ";
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	public boolean isLoaded() {
		return loaded;
	}
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	public String getCurrentBuild() {
		if (currentBuild == null)
			currentBuild = " ";
		return currentBuild;
	}
	public void setCurrentBuild(String currentBuild) {
		this.currentBuild = currentBuild;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isUpdateAvailable() {
		return updateAvailable;
	}
	public void setUpdateAvailable(boolean updateAvailable) {
		this.updateAvailable = updateAvailable;
	}
	public FeatureModule getFeature() {
		return feature;
	}
	public void setFeature(FeatureModule feature) {
		this.feature = feature;
	}
	public FeatureModule getRhumbaFeature() {
		return rhumbaFeature;
	}
	public void setRhumbaFeature(FeatureModule rhumbaFeature) {
		this.rhumbaFeature = rhumbaFeature;
	}
	public Extension getRhumbaExt() {
		return rhumbaExt;
	}
	public void setRhumbaExt(Extension rhumbaExt) {
		this.rhumbaExt = rhumbaExt;
	}
	public Extension getWebServerExt() {
		return webServerExt;
	}
	public void setWebServerExt(Extension webServerExt) {
		this.webServerExt = webServerExt;
	}

}
