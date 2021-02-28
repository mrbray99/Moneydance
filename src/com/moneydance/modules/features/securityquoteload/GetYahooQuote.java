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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class GetYahooQuote extends GetQuoteTask {

	private String yahooSecURL = "https://finance.yahoo.com/quote/";
	private String yahooCurrURL = "https://finance.yahoo.com/quote/";
	private String currencyID="";
	public GetYahooQuote(String tickerp, QuoteListener listenerp, CloseableHttpClient httpClientp,String tickerTypep, String tidp) {
		super(tickerp, listenerp, httpClientp, tickerTypep,  tidp);
		String convTicker = ticker.replace("^", "%5E");
		if (tickerType == Constants.STOCKTYPE)
			url = yahooSecURL+convTicker+"?p="+convTicker;
		if (tickerType == Constants.CURRENCYTYPE)
			url = yahooCurrURL+convTicker+"?p="+convTicker;
		debugInst.debug("GetYahooQuote","GetYahooQuote",MRBDebug.DETAILED,"Executing :"+url);
	}
	@Override
	public QuotePrice analyseResponse(CloseableHttpResponse response) throws IOException {
		QuotePrice quotePrice = new QuotePrice();
		HttpEntity entity = response.getEntity();
		try {
			InputStream stream = entity.getContent();
			String buffer = getJsonString(stream);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode nodes = mapper.readTree(buffer);
			try {
				parseDoc(nodes, quotePrice);
			}
			catch (IOException a) {
				debugInst.debug("GetQuoteTask","analyseResponse",MRBDebug.INFO,"IOException "+a.getMessage());
				throw new IOException(a);
			}
		}
		catch (UnsupportedOperationException e) {
			debugInst.debug("GetQuoteTask","analyseResponse",MRBDebug.INFO,"IOException "+e.getMessage());
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
	private String getJsonString(InputStream stream) throws IOException {
		String result=null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
			String buffer;
			while ((buffer = reader.readLine()) !=null) {
				if(buffer.contains("Currency in")) {
					String currency = buffer.substring(buffer.indexOf("Currency in"), buffer.indexOf("Currency in")+15);
					if (!currency.contains("{"))
						currencyID = currency.substring(12, 15);
				}		
				if (buffer.startsWith("root.App.main")) {
					result = buffer;
					break;
				}
			}
		}
		finally {
			if (reader !=null) {
				try {
					reader.close();
				}
				finally {
					
				}
			}	
		}
		if (result == null)
			throw new IOException("Cannot find Yahoo price data");
		if (result.indexOf("{") > 0)
			result = result.substring(result.indexOf("{"));
		return result;
	}
	private void parseDoc(JsonNode nodes, QuotePrice quotePrice) throws IOException {
		JsonNode tempNode;
		try {
			JsonNode priceNode = nodes.findPath("price");
			if (priceNode.isMissingNode())
				throw new IOException("Price node not found");
			JsonNode marketPrice = priceNode.findPath("regularMarketPrice");
			if (marketPrice.isMissingNode() || (tempNode = marketPrice.path("raw")).isMissingNode()) 
				throw new IOException("Market Price not found");
			quotePrice.setPrice(tempNode.asDouble());	
			JsonNode currencyNode = priceNode.findPath("currency");
			if (currencyNode.isMissingNode()) {
				if (currencyID.isEmpty())
					throw new IOException("Currency not found");
				quotePrice.setCurrency(currencyID);
			}
			else
				quotePrice.setCurrency(currencyNode.asText());	
			JsonNode volumeNode = priceNode.findPath("regularMarketVolume");
			if (volumeNode.isMissingNode() || (tempNode = volumeNode.path("raw")).isMissingNode()) 
				quotePrice.setVolume(0l);
			else
				quotePrice.setVolume(tempNode.asLong());	
			JsonNode marketTimeNode = priceNode.findPath("regularMarketTime");
			if (marketTimeNode.isMissingNode()) {
				quotePrice.setTradeDate("19000101T00:00");
				return;
			}
			Long marketTime = marketTimeNode.asLong();
			JsonNode quoteStoreNode = nodes.findPath("QuoteSummaryStore");
			if (quoteStoreNode.isMissingNode()|| (tempNode = quoteStoreNode.path("quoteType")).isMissingNode()) {
				quotePrice.setTradeDate("19000101T00:00");
				return;
			}
			JsonNode queryNode = tempNode.findPath("exchangeTimezoneName");
			if (queryNode.isMissingNode()) {
				quotePrice.setTradeDate("19000101T00:00");
				return;
			}
			String timezone = queryNode.asText();
			TimeZone zone= TimeZone.getTimeZone(timezone);
			Instant time = Instant.ofEpochSecond(marketTime);
			ZoneId zoneId = zone.toZoneId();
			ZonedDateTime dateTime = ZonedDateTime.ofInstant(time, zoneId);
			DateTimeFormatter formatTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
			quotePrice.setTradeDate(formatTime.format(dateTime));
			return;
		} catch (IOException e) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e.getMessage(),e);
		}
	}

}
