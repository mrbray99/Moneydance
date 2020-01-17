package com.moneydance.modules.features.extinstaller;

public class RegistrationException extends RuntimeException {
	/**
	 * Constructor - 
	 * @param objCause - contains a string describing the error
	 */
	public RegistrationException (String msg) {
		super (msg);
	}

}
