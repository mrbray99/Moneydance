package com.moneydance.modules.features.extinstaller;

import java.awt.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.SwingWorker;
import com.moneydance.apps.md.controller.Common;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.MDException;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.Platform;

public class CopyFile{
	private ZipFile zipFile;
	private File userModules;
	private File rhumbaDirectory;
	private String fileName;
	private String modulesFile;
	private String tempDirName;
	private String downLoadFile;
	private com.moneydance.apps.md.controller.Main mainObj;
	private Extension ext;
	private boolean errorFound;
	private ExtensionPanel panel;
	public CopyFile(ExtensionPanel panelp,Extension extp,String fileNamep){
		panel = panelp;
		ext=extp;
		fileName = fileNamep;
		mainObj = com.moneydance.apps.md.controller.Main.mainObj;
		Main.debugInst.debug("CopyFile", "construct", MRBDebug.SUMMARY, "Installing "+fileName);
   		downLoadWorker.execute();
	}
	private void installExtension() throws RegistrationException{
		
		try {
			if (fileName.endsWith(".mxt"))
				registerExtension(fileName);
			else
				loadZipFile(downLoadFile);
		}
		
		catch (DownloadException e) {
			Main.debugInst.debug("CopyFile", "installExtension", MRBDebug.INFO, "Error downloading "+fileName);		
			throw new RegistrationException(e.getMessage());
		}
		
		catch (RegistrationException e) {
			Main.debugInst.debug("CopyFile", "installExtension", MRBDebug.INFO, "Error registering "+fileName);		
			throw new RegistrationException(e.getMessage());
		}
	}
	   //Background task for downloading file.
    private SwingWorker<String,Void> downLoadWorker = new SwingWorker<String, Void>() {
        @Override
        public String doInBackground() {
    		modulesFile = Constants.REPOSITORY+fileName;
    		tempDirName = System.getProperty("java.io.tmpdir");
    		if (Platform.isFreeBSD()|| Platform.isUnix())
    			tempDirName +="/";
    		Main.debugInst.debug("CopyFile", "downloadWorker", MRBDebug.SUMMARY, "Downloading "+fileName);		
    		try {
    			Main.debugInst.debug("CopyFile", "downloadWorker", MRBDebug.DETAILED, "Url "+modulesFile);
    			Main.debugInst.debug("CopyFile", "downloadWorker", MRBDebug.DETAILED, "Temp file "+tempDirName+fileName);
    			HttpDownloadUtility.downloadFile(modulesFile, tempDirName);
    			Main.debugInst.debug("CopyFile", "downloadWorker", MRBDebug.SUMMARY, "File "+fileName+" downloaded");
    		} catch (DownloadException e) {
    			Main.debugInst.debug("CopyFile", "downloadWorker", MRBDebug.INFO, "Error downloading "+fileName);
    			return "Error downloading "+fileName + " "+e.getMessage();
    		} catch (IOException e) {
    			Main.debugInst.debug("CopyFile", "downloadWorker", MRBDebug.INFO, "IO Error downloading "+fileName);
    			return "Error downloading "+fileName+ " "+e.getMessage();
    		}
    		return tempDirName+fileName;
        }

        @Override
        public void done() {
            //Remove the "Loading images" label.
            try {
            	downLoadFile = get();
       			Main.debugInst.debug("CopyFile", "downloadWorker", MRBDebug.DETAILED, "Response "+downLoadFile);
                if (downLoadFile.startsWith("Error")) 
                	errorFound=true;
                else
                	installExtension ();
            }
            catch (RegistrationException| ExecutionException | InterruptedException e) {
            	errorFound=true;
            	downLoadFile = "Error installing extension "+e.getMessage();
            }
            Main.extension.frame.setCursor(Cursor.getDefaultCursor());
            panel.processDone(errorFound, downLoadFile);
        }
    };

	private void loadZipFile(String zipFileName) throws DownloadException, RegistrationException {
		File foundFile = new File(zipFileName);
		if (!foundFile.exists()) {
			Main.debugInst.debug("CopyFile", "loadZipFile", MRBDebug.INFO, "Zip file does not exist "+fileName);
			throw new DownloadException("Can not open zip file "+zipFileName);
		}
		try {
			Main.debugInst.debug("CopyFile", "loadZipFile", MRBDebug.SUMMARY, "Opening zip file "+fileName);
			zipFile = new ZipFile(foundFile);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();				
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				copyZipFileEntry(zipEntry);
			}
		}
		catch (IOException e) {
			Main.debugInst.debug("CopyFile", "loadZipFile", MRBDebug.INFO, "Error opening "+fileName+" "+e.getMessage());
			throw new DownloadException(e.getMessage());
		}
		try {
			zipFile.close();
		}
		catch (IOException e) {
			Main.debugInst.debug("CopyFile", "loadZipFile", MRBDebug.INFO, "Error closing "+fileName+" "+e.getMessage());
			return;
		}
		try {
			copyJar();
			registerExtension(Constants.QUOTELOADER);
			registerExtension(Constants.RHUMBA);
		}
		catch (DownloadException e) {
			Main.debugInst.debug("CopyFile", "loadZipFile", MRBDebug.INFO, "Error installing "+zipFileName+" "+e.getMessage());
			throw new DownloadException(e.getMessage());			
		}
		catch (RegistrationException e) {
			Main.debugInst.debug("CopyFile", "loadZipFile", MRBDebug.INFO, "Error registering files in "+zipFileName+" "+e.getMessage());
			throw new RegistrationException(e.getMessage());			
			
		}
	}

	private void copyZipFileEntry(ZipEntry zipEntry) throws DownloadException{
		String extName = new File(zipEntry.getName()).getName();
		String outFile = tempDirName+extName;
		FileOutputStream outStream;
		InputStream inStream;
		byte[] buffer = new byte[1024];
		int noOfBytes=0;
		try {
			outStream = new FileOutputStream(outFile);
			try {
				inStream = zipFile.getInputStream(zipEntry);
				while ((noOfBytes = inStream.read(buffer)) !=-1) {
					outStream.write(buffer, 0, noOfBytes);
				}
				outStream.close();
				inStream.close();
				Main.debugInst.debug("CopyFile", "copyZipFileEntry", MRBDebug.SUMMARY, "File "+extName+" extracted");
			}
			catch (IOException e) {
				Main.debugInst.debug("CopyFile", "copyZipFileEntry", MRBDebug.INFO, "Error extracting "+extName+" "+e.getMessage());
				throw new DownloadException(e.getMessage());
			}
		}
		catch (FileNotFoundException e) {
			Main.debugInst.debug("CopyFile", "copyZipFileEntry", MRBDebug.INFO, "Zip entry not found "+extName+" "+e.getMessage());
			throw new DownloadException(e.getMessage());
		}

	}
	private void copyJar() throws DownloadException {
		OutputStream jarStreamOut;
		InputStream jarStreamIn;
		byte[] buffer = new byte[1024];
		if (rhumbaDirectory == null) {
			userModules = Common.getFeatureModulesDirectory();
			rhumbaDirectory = new File(userModules, ".rhumba");
	        if (!rhumbaDirectory.exists()) {
	            rhumbaDirectory.mkdirs();
	        }
		}
		String jarOutFile = rhumbaDirectory.getAbsolutePath()+"/hleOfxQuotes.jar";
		String jarInFile = tempDirName+"hleOfxQuotes.jar";
		int noOfBytes=0;
		try {
			jarStreamOut = new FileOutputStream(jarOutFile);
			try {
				jarStreamIn = new FileInputStream(jarInFile);
				while ((noOfBytes = jarStreamIn.read(buffer)) !=-1) {
					jarStreamOut.write(buffer, 0, noOfBytes);
				}
				jarStreamOut.close();
				jarStreamIn.close();
				Main.debugInst.debug("CopyFile", "copyJar", MRBDebug.SUMMARY, "File hleOfxQuotes.jar copied");
			}
			catch (IOException e) {
				Main.debugInst.debug("CopyFile", "copyJar", MRBDebug.INFO, "Error copying hleofxquotes.jar "+e.getMessage());
				throw new DownloadException(e.getMessage());
		}
		}
		catch (FileNotFoundException e) {
			Main.debugInst.debug("CopyFile", "copyJar", MRBDebug.INFO, "File hleofxquotes.jar not found "+e.getMessage());
			e.printStackTrace();
			throw new DownloadException(e.getMessage());
		}
	}
	private void registerExtension(String extension) throws RegistrationException {
		Main.debugInst.debug("CopyFile", "registerExtension", MRBDebug.SUMMARY, "Registering "+tempDirName+fileName);		
		File moduleFile = new File(tempDirName+extension);
		try {
			if ((!moduleFile.exists()) || (!moduleFile.canRead())) {
				Main.debugInst.debug("CopyFile", "registerExtension", MRBDebug.INFO, "Error registering "+fileName);		
				throw new RegistrationException("Can not read extension file");
			}
		} catch (Exception e) {
			Main.debugInst.debug("CopyFile", "registerExtension", MRBDebug.INFO, "Error registering Extension file "+extension+" "+e.getMessage());
			throw new RegistrationException(e.getMessage());
		}
		try {
			installFromFile(moduleFile, com.moneydance.apps.md.controller.ModuleLoader.guessModuleIDFromFile(moduleFile));
		}
		catch (RegistrationException e) {
			throw new RegistrationException(e.getMessage());
		}
	}
	public void installFromFile(File moduleFile, String extensionID) throws RegistrationException	   {
		if (mainObj.getSourceInformation().getExcludedExtensions().contains(extensionID.toLowerCase())) {
			Main.debugInst.debug("LoadFilesWindow", "installFromFile", MRBDebug.SUMMARY, "excluded_extension_message");
			return;
		}
		Main.debugInst.debug("CopyFile", "registerExtension", MRBDebug.SUMMARY, "Installing "+extensionID);		
		boolean verifyModule = true;
		FeatureModule module = null;
		try {
			module = mainObj.getExternalFeatureModule(extensionID, moduleFile, true);
			ext.setFeature(module);
		} catch (MDException e) {
			if (e.getCode() == 1001) {
				try {
					module = mainObj.getExternalFeatureModule(extensionID, moduleFile, false);
					ext.setFeature(module);
					verifyModule = false;
				}
				catch (Exception e2) {
					Main.debugInst.debug("LoadFilesWindow", "installFromFile", MRBDebug.SUMMARY,"Error loading (no signature) "+extensionID);
					throw new RegistrationException(e.getMessage());
				}
			} else {
				Main.debugInst.debug("LoadFilesWindow", "installFromFile", MRBDebug.SUMMARY,"MD Exception "+e.getCode()+" loading (signature) "+extensionID);
				throw new RegistrationException(e.getMessage());
			}
		} catch (Exception e) {
			throw new RegistrationException(e.getMessage());
		}


		try {
			mainObj.installModule(moduleFile, extensionID, verifyModule);
			Main.debugInst.debug("CopyFile", "registerExtension", MRBDebug.SUMMARY, extensionID+" Installed");		
		}
		catch (Exception e) {
			Main.debugInst.debug("LoadFilesWindow", "installFromFile", MRBDebug.SUMMARY,"There was an error installing the extension - "+e.getMessage());
			throw new RegistrationException(e.getMessage());
		}
	}
}
