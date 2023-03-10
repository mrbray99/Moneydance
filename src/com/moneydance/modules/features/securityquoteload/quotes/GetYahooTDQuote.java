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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.securityquoteload.Constants;
import com.moneydance.modules.features.securityquoteload.QuotePrice;

public class GetYahooTDQuote extends GetQuoteTask {

	private String yahooSecURL = "https://query2.finance.yahoo.com/v7/finance/quote?symbols=";
	private String yahooCurrURL = "https://query2.finance.yahoo.com/v7/finance/quote?symbols=";
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	public GetYahooTDQuote(String tickerp, QuoteListener listenerp, CloseableHttpClient httpClientp,String tickerTypep, String tidp) {
		super(tickerp, listenerp, httpClientp, tickerTypep,  tidp);
		String convTicker = ticker.replace("^", "%5E");
		if (tickerType == Constants.STOCKTYPE)
			url = yahooSecURL+convTicker+"&region=US&lang=en-US&corsDomain=finance.yahoo.com";
		if (tickerType == Constants.CURRENCYTYPE)
			url = yahooCurrURL+convTicker+"&region=US&lang=en-US&corsDomain=finance.yahoo.com";
		debugInst.debug("GetYahooTDQuote","GetYahooTDQuote",MRBDebug.DETAILED,"Executing :"+url);
	}
	@Override
	synchronized public QuotePrice analyseResponse(CloseableHttpResponse response) throws IOException {
		
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
				debugInst.debug("GetYahooTDQuote","analyseResponse",MRBDebug.INFO,"IOException "+a.getMessage());
				throw new IOException(a);
			}
		}
		catch (UnsupportedOperationException e) {
			debugInst.debug("GetYahooTDQuote","analyseResponse",MRBDebug.INFO,"IOException "+e.getMessage());
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
		String result="";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
			String buffer="";
			while ((buffer = reader.readLine()) !=null) {
				result+=buffer;
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
		if (result.isEmpty())
			throw new IOException("Cannot find Yahoo price data");
		return result;
	}

	
	private void parseDoc(JsonNode doc, QuotePrice quotePrice) throws IOException {
		JsonNode quoteNode = null;
		JsonNode resultNode = null;
		String timeZoneStr="";
		Calendar tradeDateCal;
		String tradeDateStr="";
		try {
			quoteNode = doc.findPath("quoteResponse");
			if (quoteNode.isMissingNode())
				throw new IOException("Cannot parse response for "+ticker);				
			resultNode = quoteNode.findPath("result");
			if (resultNode.isMissingNode() || ! resultNode.isArray())
				throw new IOException("Cannot parse response for "+ticker);
			resultNode = resultNode.get(0);
			Iterator<Map.Entry<String, JsonNode>> it = resultNode.fields();
			while(it.hasNext()) {
				Entry<String,JsonNode> itemNode=it.next();
				JsonNode value;
				if (itemNode.getKey().equals("regularMarketPrice")) {
					value= itemNode.getValue();
					quotePrice.setPrice(value.asDouble());
				}
				if (itemNode.getKey().equals("currency")) {
					value= itemNode.getValue();
					quotePrice.setCurrency(value.asText());
				}
				if (itemNode.getKey().equals("regularMarketTime")) {
					value= itemNode.getValue();
					tradeDateStr =value.asText();
				}
				if (itemNode.getKey().equals("exchangeTimezoneShortName")) {
					value= itemNode.getValue();
					timeZoneStr =value.asText();
				}
				if (itemNode.getKey().equals("regularMarketVolume")) {
					value= itemNode.getValue();
					quotePrice.setVolume(value.asLong());

				}
				if (itemNode.getKey().equals("regularMarketDayHigh")) {
					value= itemNode.getValue();
					quotePrice.setHighPrice(value.asDouble());
				}
				if (itemNode.getKey().equals("regularMarketDayLow")) {
					value= itemNode.getValue();
					quotePrice.setLowPrice(value.asDouble());
				}
			if (itemNode.getKey().equals("exchangeTimezoneShortName")) {
					value= itemNode.getValue();
					timeZoneStr =value.asText();
				}
			}
		} catch (IOException e) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e.getMessage(),e);
		} catch (NullPointerException e2) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e2.getMessage(),e2);			
		}  catch (NumberFormatException e3) {
			throw new IOException("Cannot parse response for symbol=" + ticker + e3.getMessage(),e3);			
		}
		if (tradeDateStr.isEmpty() || timeZoneStr.isEmpty())
			throw new IOException("Cannot find trade date for "+ticker);
		tradeDateCal = getLastTrade(tradeDateStr, timeZoneStr);
		quotePrice.setTradeDate(dFormat.format(tradeDateCal.getTime())+"T00:00");

	return;
	}
	    private Calendar  getLastTrade(String regularMarketTime, String exchangeTimezoneName) throws IOException{
		       Calendar lastTrade = null;
		        if ((regularMarketTime != null) && (exchangeTimezoneName != null)) {
		            long longValue;
		            try {
		                longValue = Long.valueOf(regularMarketTime);
		                ZonedDateTime marketZonedDateTime = getMarketZonedDateTime(longValue, exchangeTimezoneName);
		                lastTrade = GregorianCalendar.from(marketZonedDateTime);
		            } catch (NumberFormatException e) {
		               throw new IOException("error calculating trade date");
		            }
		        }
		        return lastTrade;
		    }

		    private final ZonedDateTime getMarketZonedDateTime(long regularMarketTime, String exchangeTimezoneName) {
		        long epoch = regularMarketTime;
		        Instant instant = Instant.ofEpochSecond(epoch);
		        TimeZone timeZone = TimeZone.getTimeZone(exchangeTimezoneName);
		        ZoneId zoneId = timeZone.toZoneId();
		        ZonedDateTime zoneDateTime = ZonedDateTime.ofInstant(instant, zoneId);
		        return zoneDateTime;
		    }

	
}
