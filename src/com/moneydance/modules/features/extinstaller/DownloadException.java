package com.moneydance.modules.features.extinstaller;

public class DownloadException extends RuntimeException {
	/**
	 * Constructor - 
	 * @param objCause - contains a string describing the error
	 */
	public DownloadException (String msg) {
		super (msg);
	}

}
