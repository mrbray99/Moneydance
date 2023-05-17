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
package com.moneydance.modules.features.securityquoteload.quotes;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.securityquoteload.Constants;
import com.moneydance.modules.features.securityquoteload.QuotePrice;
import com.moneydance.modules.features.securityquoteload.ScanDate;

public class GetBloombergQuote extends GetQuoteTask {

	private String bloombergURL="https://bloomberg.com/quote/";
	private ScanDate scanDate=new ScanDate();
	private int depth=0;
	public GetBloombergQuote(String tickerp, QuoteListener listenerp, CloseableHttpClient httpClientp,String tickerTypep, String tidp) {
		super(tickerp, listenerp, httpClientp, tickerTypep,  tidp);
		String convTicker = ticker.replace("^", "%5E");
		url = bloombergURL+convTicker+"?leadSource=uverify%20wall";
		debugInst.debug("GetBloombergQuote","GetBloombergQuote",MRBDebug.DETAILED,"Executing :"+url);
	}
	@Override
	synchronized public QuotePrice analyseResponse(CloseableHttpResponse response) throws IOException {
		
		QuotePrice quotePrice = new QuotePrice();
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();
		Document doc = Jsoup.parse(stream, "UTF-8", "http://localhost");
		try {
			try {
				for (Node node :doc.children()) {
					displayNode(node);
				}

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
		Elements rootEll;
		Elements priceEll;
		Elements currEll;
		Elements tradeEll;
		String priceStr="0.0";
		String currStr="";
		String tradeStr="";
		try {
			rootEll =doc.select("div[id=root]");
			if (rootEll == null || rootEll.isEmpty())
				throw new IOException("Cannot find Root Entry");
			priceEll=rootEll.select("span[class^=pricetext_]");
			if (priceEll!=null) {
				if (priceEll.isEmpty())
					throw new IOException("Cannot find price text ");					
				Element firstNode = priceEll.get(0);
				Node node =  firstNode.childNode(0);
				if (node instanceof TextNode)
					priceStr = ((TextNode) node).text();
			}
			else
				throw new IOException("Cannot find Current Price");
			currEll = rootEll.select("span[class^=currency_]");
			if (currEll!=null) {
				Element firstCurrNode = currEll.get(0);
				Node currNode = firstCurrNode.childNode(0);
				if (currNode instanceof TextNode)
					currStr = ((TextNode) currNode).text();
			}
			else
				throw new IOException("Cannot find Trade Currency");
			tradeEll = rootEll.select("div[class^=time_]");
			String dateStr;
			String checkStr;
			String timeZoneStr;
			if (tradeEll!=null) {
				Element firstDateNode = tradeEll.get(0);
				Node dateNode = firstDateNode.childNode(0);
				if (dateNode instanceof Element )
					dateNode = dateNode.childNode(0);
				if (dateNode instanceof TextNode) {
					tradeStr = ((TextNode) dateNode).text();
					checkStr=tradeStr.toUpperCase();
					int index = checkStr.indexOf("AS OF ");
					if (index < 0) {
						quotePrice.setTradeDate(Constants.MISSINGDATE);
						quotePrice.setTradeDateInt(19000101);
					}
					else {
						index+=6;
						dateStr = tradeStr.substring(index,index+10);
						timeZoneStr = tradeStr.substring(index+11);
						SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy");
						Date date =simpleFormat.parse(dateStr);
						simpleFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
						quotePrice.setTradeDate(simpleFormat.format(date));
					}
				}
			}
			else {
				quotePrice.setTradeDate(Constants.MISSINGDATE);
				quotePrice.setTradeDateInt(19000101);
			}
			quotePrice.setPrice(Double.parseDouble(priceStr));
			quotePrice.setCurrency(currStr);
		} catch (IOException e) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e.getMessage(),e);
		} catch (NullPointerException e2) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e2.getMessage(),e2);			
		}  catch (NumberFormatException e3) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e3.getMessage(),e3);
		} catch (ParseException e4) {
			throw new IOException("Cannot parse trade date for symbol=" + ticker + e4.getMessage(),e4);
		}
		
	
	return;
	}
	private void displayNode(Node node) {
		depth++;
		String line = "";
		for (int i=0;i<depth;i++)
			line+="--";
		if (node instanceof Element) {
			Element elem = (Element)node;
			line+="Element "+elem.className()+" "+elem.tagName();
		}
		if (node instanceof TextNode)
			line+="TextNode "+((TextNode)node).text();
		debugInst.debug("getBloombergQuote", "displayNode", MRBDebug.DETAILED, line);
		if (node.childNodeSize()>0) {
			for (Node child:node.childNodes())
				displayNode(child);
		}
		depth--;
	}
}
