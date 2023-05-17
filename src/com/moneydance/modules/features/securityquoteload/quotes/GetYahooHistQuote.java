/*
 *  Copyri ght (c) 2020, Michael Bray and Hung Le.  All rights reserved.
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
import java.time.LocalDate;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.securityquoteload.Constants;
import com.moneydance.modules.features.securityquoteload.Parameters;
import com.moneydance.modules.features.securityquoteload.QuotePrice;
import com.moneydance.modules.features.securityquoteload.ScanDate;

public class GetYahooHistQuote extends GetQuoteTask {

	private String yahooSecURL = "https://finance.yahoo.com/quote/";
	private String yahooCurrURL = "https://finance.yahoo.com/quote/";
	private Integer lastPriceDate;
	private Integer historyDateInt;
	private String convTicker="";
	private ScanDate scanDate=new ScanDate();
	private Parameters params;
	public GetYahooHistQuote(String tickerp, QuoteListener listenerp, CloseableHttpClient httpClientp,String tickerTypep, String tidp,Integer lastPriceDatep) {
		super(tickerp, listenerp, httpClientp, tickerTypep,  tidp);
		params = Parameters.getParameters();
		LocalDate now = LocalDate.now();
		LocalDate historyDate =now.minusMonths(params.getAmtHistory()+1);
		historyDateInt = historyDate.getYear()*10000+(historyDate.getMonthValue())*100+historyDate.getDayOfMonth();
		lastPriceDate = (lastPriceDatep==null?0:lastPriceDatep);
		if (lastPriceDate < historyDateInt) {
			lastPriceDate = historyDateInt;
			debugInst.debug("GetYahooHistQuote","construct",MRBDebug.DETAILED,"History date restricted to  "+lastPriceDate);
		}
		convTicker = ticker.replace("^", "%5E");
		if (tickerType == Constants.STOCKTYPE)
			url = yahooSecURL+convTicker+"/history?p="+convTicker;
		if (tickerType == Constants.CURRENCYTYPE)
			url = yahooCurrURL+convTicker+"/history?p="+convTicker;
	    debugInst.debug("GetYahooHistQuote","GetYahooHistQuote",MRBDebug.DETAILED,"Executing :"+url);
	}
	@Override
	synchronized public QuotePrice analyseResponse(CloseableHttpResponse response) throws IOException {
		debugInst.debug("GetYahooHistQuote","analyseResponse",MRBDebug.DETAILED,"Analysing "+convTicker);
		QuotePrice quotePrice = new QuotePrice();
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();
		Document doc = Jsoup.parse(stream,"UTF-8","http://localhost");
		try {
			try {
				parseDoc(doc, quotePrice);
			}
			catch (IOException a) {
				debugInst.debug("GetYahooHistQuote","analyseResponse",MRBDebug.INFO,"IOException "+a.getMessage());
				throw new IOException(a);
			}
		}
		catch (UnsupportedOperationException e) {
			debugInst.debug("GetYahooHistQuote","analyseResponse",MRBDebug.INFO,"Unsupported Operation  "+e.getMessage());
			throw new IOException(e);
		}
		catch (MalformedURLException e) {
			debugInst.debug("GetYahooHistQuote","analyseResponse",MRBDebug.INFO,"MalformedURL "+e.getMessage());
			throw (new IOException (e));
		} catch (ClientProtocolException e) {
			debugInst.debug("GetYahooHistQuote","analyseResponse",MRBDebug.INFO,"ClientProtocolException "+e.getMessage());
		throw (new IOException (e));
		} catch (IOException e) {
			debugInst.debug("GetYahooHistQuote","analyseResponse",MRBDebug.INFO,"IOException "+e.getMessage());
			throw (new IOException (e));
		} 

		finally {

		}		

		return quotePrice;
	}

	private void parseDoc(Document doc, QuotePrice quotePrice) throws IOException {
		debugInst.debug("GetYahooHistQuote","parseDoc",MRBDebug.INFO,"Parsing document for  "+convTicker);
		Elements dataRows=null;
		boolean priceFound = false;
		try {
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
			dataRows =doc.getElementsByAttributeValue("class","BdT Bdc($seperatorColor) Ta(end) Fz(s) Whs(nw)");
			for (Element row:dataRows) {
				if (row.childNodeSize()==2) {
					Elements childrenList = row.child(1).children();
					if (childrenList.isEmpty())
						continue;
					Element child1 = childrenList.get(1);
					String text="";
					if (child1.hasText())
						text=child1.text();
					if(text.equals("Dividend"))
						continue;
					break;
				}
				if (row.childNodeSize()<7)
					break;
				String tradeDate =getTextNode(row,0);
				String highStr =getTextNode(row,2);
				highStr=highStr.replace(",", "");
				String lowStr =getTextNode(row,3);
				lowStr=lowStr.replace(",", "");
				String priceStr =getTextNode(row,5);
				priceStr=priceStr.replace(",", "");
				String volumeStr=getTextNode(row,6);
				if (highStr.isEmpty() && lowStr.isEmpty()&& priceStr.isEmpty())
					continue;
				if (!priceFound) {
					int tradeDateInt =scanDate.parseYahooDate(tradeDate);
					if (tradeDateInt == 19000101)
						throw new IOException("Error in trade date " + tradeDate);
					quotePrice.setTradeDateInt( tradeDateInt);
					quotePrice.setTradeDate(String.valueOf(tradeDateInt)+"T00:00");

					if (priceStr.isEmpty())
						throw new IOException("Error in price " + priceStr);
					else
						quotePrice.setPrice(Double.parseDouble(priceStr));
					quotePrice.setHighPrice(highStr.isEmpty()?0.0:Double.parseDouble(highStr));
					quotePrice.setLowPrice(lowStr.isEmpty()?0.0:Double.parseDouble(lowStr));
					if (volumeStr.isEmpty())
						quotePrice.setVolume(0L);
					else {	
						volumeStr=volumeStr.replace(",", "");
						quotePrice.setVolume(Long.parseLong(volumeStr));
					}
					priceFound = true;
				}
				else {
					if (!params.getHistory()|| lastPriceDate == null)
						break;
					QuotePrice historyPrice = new QuotePrice();
					int tradeDateInt =scanDate.parseYahooDate(tradeDate);
					if (tradeDateInt == 19000101)
						throw new IOException("Error in trade date " + tradeDate);
					if (tradeDateInt <= lastPriceDate){
						break;
					}
					historyPrice.setTradeDateInt( tradeDateInt);
					historyPrice.setTradeDate(tradeDate.toString()+"T00:00");
					if (priceStr.isEmpty())
						throw new IOException("Error in price " + priceStr);
					else
						historyPrice.setPrice(Double.parseDouble(priceStr));
					historyPrice.setHighPrice(highStr.isEmpty()?0.0:Double.parseDouble(highStr));
					historyPrice.setLowPrice(lowStr.isEmpty()?0.0:Double.parseDouble(lowStr));
					if (volumeStr.isEmpty())
						quotePrice.setVolume(0L);
					else {	
						volumeStr=volumeStr.replace(",", "");
						historyPrice.setVolume(Long.parseLong(volumeStr));
					}
					quotePrice.addHistory(historyPrice.getTradeDateInt(),historyPrice.getPrice(), historyPrice.getHighPrice(), 
							historyPrice.getLowPrice(), historyPrice.getVolume());
				}
			}
		} catch (Exception e1) {
			debugInst.debug("GetYahooHistQuote","parseDoc",MRBDebug.INFO,"Cannot parse response for  "+convTicker+" "+e1.getMessage());
			throw new IOException("Cannot parse response for symbol=" + ticker + e1.getMessage(),e1);
			
		}
	}
	private String getTextNode(Element row, int child) {
		if (row.childNodeSize()< child+1)
			return "";
		if (row.child(child) == null)
			return "";
		Elements childrenList = row.child(child).children();
		if (childrenList.isEmpty())
			return "";
		if (row.child(child).child(0)== null)
			return "";
		if (row.child(child).child(0).textNodes().isEmpty())
			return "";
		if (row.child(child).child(0).textNodes().get(0)==null)
			return "";
		if (row.child(child).child(0).textNodes().get(0).text()==null)
			return "";
		return row.child(child).child(0).textNodes().get(0).text();
	}
}
