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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class GetYahooQuote extends GetQuoteTask {

	private String yahooSecURL = "https://finance.yahoo.com/quote/";
	private String yahooCurrURL = "https://finance.yahoo.com/quote/";
	private ScanDate scanDate=new ScanDate();
	public GetYahooQuote(String tickerp, QuoteListener listenerp, CloseableHttpClient httpClientp,String tickerTypep, String tidp) {
		super(tickerp, listenerp, httpClientp, tickerTypep,  tidp);
		String convTicker = ticker.replace("^", "%5E");
		if (tickerType == Constants.STOCKTYPE)
			url = yahooSecURL+convTicker+"?p="+convTicker+"&.tscr=fin-srch";
		if (tickerType == Constants.CURRENCYTYPE)
			url = yahooCurrURL+convTicker+"?p="+convTicker;
		debugInst.debug("GetYahooQuote","GetYahooQuote",MRBDebug.DETAILED,"Executing :"+url);
	}
	@Override
	synchronized public QuotePrice analyseResponse(CloseableHttpResponse response) throws IOException {
		
		QuotePrice quotePrice = new QuotePrice();
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();
		Document doc = Jsoup.parse(stream, "UTF-8", "http://localhost");
		try {
			try {
				parseDoc(doc, quotePrice);
			}
			catch (IOException a) {
				debugInst.debug("GetQuoteTask","analyseResponse",MRBDebug.INFO,"IOException "+a.getMessage());
				throw new IOException(a);
			}
	}
	catch (UnsupportedOperationException e) {
		throw new IOException(e);
	}
	catch (MalformedURLException e) {
		throw (new IOException (e));
	} catch (ClientProtocolException e) {
		throw (new IOException (e));
	} catch (IOException e) {
		throw (new IOException (e));
	} 

	finally {

	}		

		return quotePrice;
	}

	private void parseDoc(Document doc, QuotePrice quotePrice) throws IOException {
		String query = null;
		Element crntLoc = null;
		Attributes attribs;
		try {
			String formattedTicker = URLDecoder.decode(ticker,StandardCharsets.UTF_8.name());;
			query = "fin-streamer[data-symbol=\""+formattedTicker+"\"][data-field=\"regularMarketPrice\"]";
			crntLoc = doc.selectFirst(query);
			if (crntLoc == null) {
				throw new IOException("Cannot find " + query);
			}		
			attribs = crntLoc.attributes();
			String priceStr = attribs.get("value");
			quotePrice.setPrice(Double.parseDouble(priceStr));
			quotePrice.setTradeDate(Constants.MISSINGDATE);
			quotePrice.setTradeDateInt(19000101);
			Elements spans = doc.getElementsByTag("span");
			if (spans == null) {
				throw new IOException("Cannot find Currency");
			}
			String cur="";
			for (int i=0;i<spans.size();i++) {
				Element elem = spans.get(i);
				List<TextNode> children = elem.textNodes();
				for (TextNode text:children) {
					if (text.text().contains("Currency in")) {
							int place = text.text().indexOf("Currency in")+12;
							cur = text.text().substring(place, place+3);
							break;
						}
				}
				if (!cur.isEmpty())
					break;
			}
			if (cur.isEmpty())
				throw new IOException("Cannot find Currency");
			quotePrice.setCurrency(cur);
		} catch (IOException e) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e.getMessage(),e);
		} catch (NullPointerException e2) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e2.getMessage(),e2);			
		}  catch (NumberFormatException e3) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e3.getMessage(),e3);			
		}
	
	return;
	}
}
