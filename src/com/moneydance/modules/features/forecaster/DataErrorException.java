package com.moneydance.modules.features.forecaster;
/**
 * Raised when an inconsistency in the Moneydance file is discovered.  Data needed does not exist
 * @author Mike Bray
 *
 */
public class DataErrorException extends RuntimeException {
	/**
	 * Constructor - 
	 * @param objCause - contains a string describing the error
	 */
	public DataErrorException (Throwable objCause) {
		super (objCause);
	}

}
