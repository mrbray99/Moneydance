package com.moneydance.modules.features.mrbutil;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class 	MRBDebug {
	private int iLevel = 0;
	public  static final int  OFF = 0;
	public  static final int INFO = 1;
	public  static final int SUMMARY = 2;
	public static int DETAILED = 3;
	private DateTimeFormatter dtf =DateTimeFormatter.ofPattern("HH:mm:ss"); 
	private String extensionName = "";
	public int getDebugLevel () {
		return iLevel;
	}
	public void setDebugLevel (int iLevelp) {
		iLevel = iLevelp;
	}
	public void setExtension (String strExtensionp) {
		extensionName = strExtensionp;
	}
	public  synchronized  void debugThread(final String strClass,  final String strMethod, final int iLevelp,  final String strMessage) {
		final String type;
		final String thread = Thread.currentThread().getName();
		if (iLevel != OFF && iLevelp <= iLevel) {
			switch (iLevelp) {
			case INFO:
				type = "INFOT";
				break;
			case SUMMARY:
				type = "SUMMT";
				break;
			default :
				type = "DETT";
			}
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					debugMessage(type, thread, strClass, strMethod, strMessage);
				}
			});
		}
  
	}
	public  synchronized  void debug (String strClass, String strMethod,int iLevelp, String strMessage) {
		if (iLevel != OFF && iLevelp <= iLevel) {
			final String type;
			switch (iLevelp) {
			case INFO:
				type = "INFO";
				break;
			case SUMMARY:
				type = "SUMM";
				break;
			default :
				type = "DET";
			}
			if (javax.swing.SwingUtilities.isEventDispatchThread())
				debugMessage(type,Thread.currentThread().getName(),strClass,strMethod,strMessage);
			else {
				final String thread = Thread.currentThread().getName();
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						debugMessage(type, thread, strClass, strMethod, strMessage);
					}
				});
			}
		}
	}	
	public synchronized  void debugMessage (String type, String thread,String strClass, String strMethod, String strMessage) {
		LocalTime now= LocalTime.now();
		System.err.println(extensionName + ">"+type+":"+now.format(dtf)+"-"+thread+"("+strClass+","+strMethod+ ") " +strMessage);
	}
}
