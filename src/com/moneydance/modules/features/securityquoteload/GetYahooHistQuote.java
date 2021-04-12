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
import java.nio.charset.StandardCharsets;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinitekind.util.DateUtil;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class GetYahooHistQuote extends GetQuoteTask {

	private String yahooSecURL = "https://finance.yahoo.com/quote/";
	private String yahooCurrURL = "https://finance.yahoo.com/quote/";
	private String currencyID="";
	private Integer lastPriceDate;
	public GetYahooHistQuote(String tickerp, QuoteListener listenerp, CloseableHttpClient httpClientp,String tickerTypep, String tidp,Integer lastPriceDatep) {
		super(tickerp, listenerp, httpClientp, tickerTypep,  tidp);
		lastPriceDate = lastPriceDatep;
		String convTicker = ticker.replace("^", "%5E");
		if (tickerType == Constants.STOCKTYPE)
			url = yahooSecURL+convTicker+"/history?p="+convTicker;
		if (tickerType == Constants.CURRENCYTYPE)
			url = yahooCurrURL+convTicker+"/history?p="+convTicker;
	    debugInst.debug("GetYahooHistQuote","GetYahooHistQuote",MRBDebug.DETAILED,"Executing :"+url);
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
				debugInst.debug("GetYahooHistQuote","analyseResponse",MRBDebug.INFO,"IOException "+a.getMessage());
				throw new IOException(a);
			}
		}
		catch (UnsupportedOperationException e) {
			debugInst.debug("GetYahooHistQuote","analyseResponse",MRBDebug.INFO,"IOException "+e.getMessage());
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
			throw new IOException("Cannot find Yahoo history price data");
		if (result.indexOf("{") > 0)
			result = result.substring(result.indexOf("{"));
		return result;
	}
	private void parseDoc(JsonNode nodes, QuotePrice quotePrice) throws IOException {
		JsonNode tempNode;
		String timezone;
		Long marketTime; 
		TimeZone zone=null;
		Instant time=null;
		ZoneId zoneId=null;
		ZonedDateTime dateTime=null;
		DateTimeFormatter formatTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		try {
			JsonNode priceNode = nodes.findPath("prices");
			JsonNode marketPrice;
			JsonNode volumeNode;
			JsonNode highNode;
			JsonNode lowNode;
			JsonNode marketTimeNode;
			if (priceNode.isMissingNode())
				throw new IOException("Prices node not found");
			JsonNode quoteStoreNode = nodes.findPath("QuoteSummaryStore");
			if (quoteStoreNode.isMissingNode()|| (tempNode = quoteStoreNode.path("quoteType")).isMissingNode()) 
				zoneId = null;
			else {
				JsonNode queryNode = tempNode.findPath("exchangeTimezoneName");
				if (queryNode.isMissingNode()) 
					zoneId = null;
				else {
					timezone = queryNode.asText();
					zone= TimeZone.getTimeZone(timezone);
					zoneId = zone.toZoneId();
				}
			}
			if (!priceNode.isArray()) {
				debugInst.debug("GetYahooHistQuote","parseDoc",MRBDebug.DETAILED,"No table ");
				marketPrice = priceNode.findPath("close");
				if (marketPrice.isMissingNode()) 
					throw new IOException("Market Price not found");
				quotePrice.setPrice(marketPrice.asDouble());
				highNode = priceNode.findPath("high");
				if (highNode.isMissingNode())
					quotePrice.setHighPrice(quotePrice.getPrice());
				else
					quotePrice.setHighPrice(highNode.asDouble());
				lowNode = priceNode.findPath("low");
				if (lowNode.isMissingNode())
					quotePrice.setLowPrice(quotePrice.getPrice());
				else
					quotePrice.setLowPrice(lowNode.asDouble());
				volumeNode = priceNode.findPath("volume");
				if (volumeNode.isMissingNode()) 
					quotePrice.setVolume(0l);
				else
					quotePrice.setVolume(volumeNode.asLong());	
				quotePrice.setCurrency(currencyID);
				marketTimeNode = priceNode.findPath("date");
				if (marketTimeNode.isMissingNode()) 
					quotePrice.setTradeDate("19000101T00:00");
				else {
					marketTime = marketTimeNode.asLong();
					time = Instant.ofEpochSecond(marketTime);
					if (zoneId == null)
						quotePrice.setTradeDate("19000101T00:00");
					else {
						dateTime = ZonedDateTime.ofInstant(time, zoneId);
						quotePrice.setTradeDate(formatTime.format(dateTime));
					}
				}
				return;
			}
			boolean priceFound = false;
			QuotePrice historyPrice = new QuotePrice();
			for (final JsonNode objNode :priceNode) {
				JsonNode typeNode = objNode.findPath("type");
				if (!typeNode.isMissingNode() && typeNode.asText().contentEquals("DIVIDEND"))
					continue;
				marketPrice = objNode.findPath("close");
				if (marketPrice.isMissingNode()  || marketPrice.isNull()) {
					debugInst.debug("GetYahooHistQuote","parseDoc",MRBDebug.DETAILED,"missing price");
					continue;
				}
				if (priceFound) {
					if(!Main.params.getHistory() || lastPriceDate ==null || zone==null) 
						break;
					historyPrice.setPrice(marketPrice.asDouble());	
					highNode = objNode.findPath("high");
					if (highNode.isMissingNode())
						historyPrice.setHighPrice(historyPrice.getPrice());
					else
						historyPrice.setHighPrice(highNode.asDouble());
					lowNode = objNode.findPath("low");
					if (lowNode.isMissingNode())
						historyPrice.setLowPrice(historyPrice.getPrice());
					else
						historyPrice.setLowPrice(lowNode.asDouble());
					volumeNode = objNode.findPath("volume");
					if (volumeNode.isMissingNode()) 
						historyPrice.setVolume(0l);
					else
						historyPrice.setVolume(volumeNode.asLong());	
					historyPrice.setCurrency(currencyID);
					marketTimeNode = objNode.findPath("date");
					if (marketTimeNode.isMissingNode()) 
						break;
					marketTime = marketTimeNode.asLong();
					time = Instant.ofEpochSecond(marketTime);
					dateTime = ZonedDateTime.ofInstant(time, zoneId);
					historyPrice.setTradeDate(formatTime.format(dateTime));
					historyPrice.setTradeDateInt(DateUtil.convertDateToInt(Date.from(time)));
					if (historyPrice.getTradeDateInt()<= lastPriceDate)
						break;
					quotePrice.addHistory(historyPrice.getTradeDateInt(), historyPrice.getPrice(),historyPrice.getHighPrice(), historyPrice.getLowPrice(),historyPrice.getVolume());
				}
				else {
					debugInst.debug("GetYahooHistQuote","parseDoc",MRBDebug.DETAILED,"Price found");
					quotePrice.setPrice(marketPrice.asDouble());	
					highNode =objNode.findPath("high");
					if (highNode.isMissingNode())
						quotePrice.setHighPrice(quotePrice.getPrice());
					else
						quotePrice.setHighPrice(highNode.asDouble());
					lowNode = objNode.findPath("low");
					if (lowNode.isMissingNode())
						quotePrice.setLowPrice(quotePrice.getPrice());
					else
						quotePrice.setLowPrice(lowNode.asDouble());
					volumeNode = objNode.findPath("volume");
					if (volumeNode.isMissingNode()) 
						quotePrice.setVolume(0l);
					else
						quotePrice.setVolume(volumeNode.asLong());	
					quotePrice.setCurrency(currencyID);
					marketTimeNode = objNode.findPath("date");
					if (marketTimeNode.isMissingNode()) 
						break;
					marketTime = marketTimeNode.asLong();
					time = Instant.ofEpochSecond(marketTime);
					dateTime = ZonedDateTime.ofInstant(time, zoneId);
					quotePrice.setTradeDate(formatTime.format(dateTime));
					quotePrice.setTradeDateInt(DateUtil.convertDateToInt(Date.from(time)));
					priceFound = true;
					if(Main.params.getHistory() && lastPriceDate !=null && zone!=null) {
						debugInst.debug("GetYahooHistQuote","parseDoc",MRBDebug.DETAILED,"Getting history up to "+lastPriceDate);
					}
				}
			}
			return;
		} catch (IOException e) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e.getMessage(),e);
		}
	}

}
