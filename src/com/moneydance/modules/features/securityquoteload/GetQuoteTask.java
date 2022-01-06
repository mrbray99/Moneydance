/*
 *  Copyright (c) 2020, Michael Bray and Hung Le.  All rights reserved.
 *  
 *  NOTE: this module contains original work by Mike Bray and Hung Le, no breach of copyright is intended and no 
 *  benefit has been gained from the use of this work
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.moneydance.modules.features.mrbutil.MRBDebug;

public class GetQuoteTask extends QuoteTask<QuotePrice> {
	public GetQuoteTask (String tickerp, QuoteListener listenerp, CloseableHttpClient httpClientp,String tickerTypep,String tidp) {
		super(tickerp,listenerp, httpClientp,tickerTypep,tidp);
	}
	@Override
	public QuotePrice call() throws Exception {
		QuotePrice quotePrice = null;
		CloseableHttpResponse response = null;
		if (ticker.isBlank()) {
			debugInst.debug("GetQuoteTask", "call", MRBDebug.INFO, "Invalid Ticker "+rawTicker);
			sendError();		
		}
			
		URI uri=null;
		try {
			uri= new URI(url.trim());
			debugInst.debugThread("GetQuoteTask", "call", MRBDebug.INFO, "Processing  "+ticker+" URI:"+uri.toASCIIString());		
			HttpGet httpGet = new HttpGet(uri);
			httpGet.addHeader("Accept-Language","en");
			response = httpClient.execute(httpGet);
			quotePrice=null; 
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					quotePrice = analyseResponse(response);
					String doneUrl ="moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.LOADPRICECMD+"?"+Constants.TIDCMD+"="+tid+"&";
					if (tickerType == Constants.STOCKTYPE)
						doneUrl+=Constants.STOCKTYPE;
					else
						doneUrl+=Constants.CURRENCYTYPE;
					doneUrl += "="+ticker+"&p="+String.format("%.8f",quotePrice.getPrice());
					doneUrl += "&"+Constants.TRADEDATETYPE+"="+quotePrice.getTradeDate();
					doneUrl += "&"+Constants.TRADECURRTYPE+"="+quotePrice.getCurrency();
					if (quotePrice.getHighPrice()!= 0.0)
						doneUrl += "&"+Constants.HIGHTYPE+"="+quotePrice.getHighPrice();
					if (quotePrice.getLowPrice()!= 0.0)
						doneUrl += "&"+Constants.LOWTYPE+"="+quotePrice.getLowPrice();
					doneUrl += "&"+Constants.VOLUMETYPE+"="+quotePrice.getVolume();
					Main.context.showURL(doneUrl);
					if (!quotePrice.getHistory().isEmpty()) {
						for (HistoryPrice history : quotePrice.getHistory()) {
							doneUrl ="moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.LOADHISTORYCMD+"?"+Constants.TIDCMD+"="+tid+"&";
							if (tickerType == Constants.STOCKTYPE)
								doneUrl+=Constants.STOCKTYPE;
							else
								doneUrl+=Constants.CURRENCYTYPE;
							doneUrl += "="+ticker+"&p="+String.format("%.8f",history.getPrice());
							if (history.getHighPrice()!= 0.0)
								doneUrl += "&"+Constants.HIGHTYPE+"="+history.getHighPrice();
							if (history.getLowPrice()!= 0.0)
								doneUrl += "&"+Constants.LOWTYPE+"="+history.getLowPrice();
							doneUrl += "&"+Constants.TRADEDATETYPE+"="+history.getDate();
							doneUrl += "&"+Constants.TRADECURRTYPE+"="+quotePrice.getCurrency();
							if (Main.params.getAddVolume() && quotePrice.getVolume() > 0L)
								doneUrl += "&"+Constants.VOLUMETYPE+"="+history.getVolume();
							Main.context.showURL(doneUrl);
				
						}
					}
					listener.doneReturned(ticker);
				}
				catch (IOException e) {
					debugInst.debug("GetQuoteTask", "call", MRBDebug.INFO, "error analysing reply "+e.getMessage());
					sendError();
				}	
			}
			else {
				debugInst.debug("GetQuoteTask", "call", MRBDebug.INFO, "error returned "+response.getStatusLine().getStatusCode());
				sendError();
			}
		} catch (URISyntaxException e) {
			debugInst.debug("getQuoteTask", "call", MRBDebug.DETAILED, "URI invalid "+url);
			sendError();
		}
		if (response != null)
			response.close();
		return quotePrice;
	}
	private void sendError() {
		String errorUrl ="moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.ERRORQUOTECMD+"?"+Constants.TIDCMD+"="+tid+"&";
		if (tickerType == Constants.STOCKTYPE)
			errorUrl+=Constants.STOCKTYPE;
		else
			errorUrl+=Constants.CURRENCYTYPE;
		errorUrl += "="+ticker;
		Main.context.showURL(errorUrl);
		listener.errorReturned(ticker);

		
	}

}
