package com.moneydance.modules.features.extinstaller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.moneydance.modules.features.mrbutil.MRBDebug;


public class HttpDownloadUtility {
    private static final int BUFFER_SIZE = 4096;
    
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String saveDir) throws DownloadException, IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        Main.debugInst.debug("HttpDownloadUtility","downloadFile",MRBDebug.SUMMARY,"Connection Made");
        int responseCode = httpConn.getResponseCode();
 
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Main.debugInst.debug("HttpDownloadUtility","downloadFile",MRBDebug.SUMMARY,"Response OK");
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
            Main.debugInst.debug("HttpDownloadUtility","downloadFile",MRBDebug.SUMMARY,"File found "+fileName); 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
             
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
            Main.debugInst.debug("HttpDownloadUtility","downloadFile",MRBDebug.SUMMARY,"File Copied");
        } else {
            Main.debugInst.debug("HttpDownloadUtility","downloadFile",MRBDebug.SUMMARY,"Error downloading file-response "+responseCode); 
            httpConn.disconnect();
            throw new DownloadException ("File does not exist "+fileURL);
        }
        httpConn.disconnect();
    }
}
