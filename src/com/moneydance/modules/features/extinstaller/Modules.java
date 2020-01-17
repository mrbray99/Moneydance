package com.moneydance.modules.features.extinstaller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedMap;
import java.util.TreeMap;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class Modules {
	private com.moneydance.apps.md.controller.Main mainObj;
	private FeatureModule[] modulesLoaded;
	private String modulesFile;
	private String tempDirName;
	private BufferedReader buildNumbers;
	private String line;
	private SortedMap<String,Extension> listExtensions;
	public Modules () throws DownloadException{
		listExtensions = new TreeMap<String,Extension>();
		loadExtensions();
		loadModulesFile();
		loadCurrentModules();
	}
	public SortedMap<String,Extension> getModules() {
		return listExtensions;
	}
	/*
	 * set up main table from constants
	 */
	private void loadExtensions() {
		for (Constants.ModuleInfo info : Constants.modules) {
			Extension ext = new Extension();
			ext.setName(info.name);
			ext.setExtensionID(info.fileName);
			ext.setDescription(info.description);
			ext.setUrl(info.url);
			ext.setUpdateAvailable(false);
			listExtensions.put(ext.getExtensionID(), ext);
			if (ext.getExtensionID().equals(Constants.QUOTELOADER)) 
				Main.context.showURL("moneydance:fmodule:" + Constants.QUOTELOADERPGM + ":"+Constants.GETBUILDNUM+","+Constants.PROGRAMNAME);		
		}
		Extension ext1 = listExtensions.get(Constants.QUOTELOADER);
		Extension ext2 = listExtensions.get(Constants.RHUMBA);
		Extension ext3 = listExtensions.get(Constants.WEBSERVER);
		if (ext1 != null) {
			ext1.setRhumbaExt(ext2);
			ext1.setWebServerExt(ext3);
		}
	}
	/*
	 * download buildnumbers from repository
	 */
	private void loadModulesFile() {
		modulesFile = Constants.REPOSITORY+Constants.MODULESFILE;
		tempDirName = System.getProperty("java.io.tmpdir");
		try {
			Main.debugInst.debug("CopyFile", "loadModulesFile", MRBDebug.DETAILED, "Url "+modulesFile);
			Main.debugInst.debug("CopyFile", "loadModulesFile", MRBDebug.DETAILED, "Temp file "+tempDirName+Constants.MODULESFILE);
			HttpDownloadUtility.downloadFile(modulesFile, tempDirName);
			Main.debugInst.debug("CopyFile", "loadModulesFile", MRBDebug.SUMMARY, "File "+Constants.MODULESFILE+" downloaded");
		} catch (DownloadException e) {
			Main.debugInst.debug("CopyFile", "loadModulesFile", MRBDebug.INFO, "Error downloading "+Constants.MODULESFILE);
			throw new DownloadException("File "+Constants.MODULESFILE+" does not exist on the download site.");
		} catch (IOException e) {
			Main.debugInst.debug("CopyFile", "loadModulesFile", MRBDebug.INFO, "Error downloading "+Constants.MODULESFILE);
			throw new DownloadException("IO Exception "+e.getMessage());
		}		
		try {
			buildNumbers = new BufferedReader(new FileReader(tempDirName+"/"+Constants.MODULESFILE));
			while ((line = buildNumbers.readLine()) !=null) {
				String [] fields = line.split(",");
				Extension ext = listExtensions.get(fields[1]);
				if ( ext != null) {
					ext.setDownloadName(fields[2]);
					ext.setBuild(fields[4]);
					ext.setType(fields[3]);
					ext.setUpdateAvailable(true);
				}
			}
			buildNumbers.close();
			Extension ext1 = listExtensions.get(Constants.QUOTELOADER);
			Extension ext2 = listExtensions.get(Constants.RHUMBA);
			Extension ext3 = listExtensions.get(Constants.WEBSERVER);
			String buildNos = ext1 !=null?ext1.getBuild()+"/":"/";
			buildNos = ext2!=null?buildNos+ext2.getBuild()+"/":buildNos+"/";
			buildNos = ext3!=null?buildNos+ext3.getBuild():buildNos;
			if (ext1 != null)
				ext1.setBuild(buildNos);
		} catch (IOException  e) {
			Main.debugInst.debug("Modules", "construct", MRBDebug.INFO, "Error loading build numbers  "+e.getMessage());
			throw new DownloadException("File Not Found "+e.getMessage());
		}

	}
	/*
	 * load current modules
	 */
	private void loadCurrentModules() {
		mainObj = com.moneydance.apps.md.controller.Main.mainObj;
		modulesLoaded = mainObj.getLoadedModules();
		for (FeatureModule module : modulesLoaded) {
			Path path = Paths.get(module.getSourceFile().toString());
			String fileName = path.getFileName().toString();
			Extension ext = listExtensions.get(fileName);
			if (ext != null) {
				ext.setLoaded(true);
				String buildNo;
				if (fileName.equals(Constants.QUOTELOADER))
					if (Main.qlBuildNo != null && !Main.qlBuildNo.isEmpty())
						buildNo = Main.qlBuildNo+"/"+Main.rhumbaBuildNo+"/"+Main.hleBuildNo;
					else
						buildNo = Integer.toString(module.getBuild());
				else
					buildNo = Integer.toString(module.getBuild());
				ext.setCurrentBuild(buildNo);
				ext.setFeature(module);
			}
		}
		Extension ext1 = listExtensions.get(Constants.QUOTELOADER);
		Extension ext2 = listExtensions.get(Constants.RHUMBA);
		if ((Main.qlBuildNo == null || Main.qlBuildNo.isEmpty())&& ext1.isLoaded()) {
			String buildNos = ext1 !=null?ext1.getCurrentBuild()+"/":"/";
			buildNos = ext2!=null?buildNos+ext2.getCurrentBuild()+"/NA":buildNos+"/NA";
			if (ext1 != null)
				ext1.setCurrentBuild(buildNos);
		}
		/*
		 * save rhumba entry into quote loader entry for use when deleted
		 */
		if (ext2 != null && ext1 != null)
			ext1.setRhumbaFeature(ext2.getFeature());
		/*
		 * Accessing the extension messes up the debug static variable
		 */
		Main.extension.resetDebug();
	}
	
}
