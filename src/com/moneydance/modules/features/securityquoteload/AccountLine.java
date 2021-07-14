/*
 * Copyright (c) 2018, Michael Bray.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - The name of the author may not used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.moneydance.modules.features.securityquoteload;

import java.io.Serializable;

import com.moneydance.modules.features.mrbutil.MRBDebug;
/**
 * Class to store the source from selected by the user.  It will be stored in the 
 * parameter file
 * 
 * @author Mike Bray
 *
 */
@SuppressWarnings("serial")
public class AccountLine implements Serializable{
	private String name;
	private int source;
	private Boolean currencyFound;
	/**
	 * Create class, uses the structure of the Ticker to determine if the account is a
	 * currency
	 * @param namep the Ticker of the account
	 * @param sourcep the Source selected by the user
	 */
	public AccountLine(String namep, int sourcep){
		name = namep;
		source = sourcep;
		currencyFound = false;
		if (name.length() > 2)
			if (name.substring(0, 3).equals(Constants.CURRENCYID))
				currencyFound = true;
			else 
				/*
				 * Double check currencies
				 */
					if (name.substring(0,3).equals("???")) {
						name= Constants.CURRENCYID+name.substring(3);
						currencyFound =true;
						Main.debugInst.debug("Parameters","init",MRBDebug.INFO,"currency changed from oldCurrency to new currency");
				}
		}
	/**
	 * Get the Source
	 * @return Source
	 */
	public int getSource() {
		return source;
	}
	/**
	 * Get the Ticker
	 * @return Ticker
	 */
	public String getName() {
		return name;
	}
	/**
	 * set the source 
	 * @param sourcep the source
	 */
	public void setSource (int sourcep) {
		source = sourcep;
	}
	/**
	 * Returns whether or not the account is a currency
	 * @return true/false
	 */
	public Boolean isCurrency() {
		return currencyFound;
	}
}
