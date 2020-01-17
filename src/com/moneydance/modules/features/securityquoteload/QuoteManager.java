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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class QuoteManager implements QuoteListener {
	private Charset charSet = Charset.forName("UTF-8");
	private MRBDebug debugInst = MRBDebug.getInstance();
	private String source;
	private String tid;
	private List<String> stocks;
	private List<String> currencies;
	private ExecutorService threadPool;
	private CloseableHttpClient httpClient;
	private int totalQuotes=0;
	private int successful=0;
	private int failed=0;
	public QuoteManager () {
		threadPool = Executors.newFixedThreadPool(4);
		
	}
	 
	public void getQuotes(String request) {
		stocks = new ArrayList<String>();
		currencies = new ArrayList<String>();
		debugInst.debug("QuoteManager", "getQuotes", MRBDebug.INFO, "URI "+request);
		URI uri=null;
		try {
			uri = new URI(request);
		} catch (URISyntaxException e) {
			debugInst.debug("QuoteManager", "getQuotes", MRBDebug.DETAILED, "URI invalid "+request);
			e.printStackTrace();
			return;
		}
		List<NameValuePair> results = URLEncodedUtils.parse(uri, charSet);
		source="";
		for (NameValuePair item : results) {
			switch (item.getName()) {
			case Constants.SOURCETYPE :
				source = item.getValue();
				break;
			case Constants.TIDCMD :
				tid=item.getValue();
				break;
			case Constants.STOCKTYPE:
				stocks.add(item.getValue());
				break;
			case Constants.CURRENCYTYPE :
				currencies.add(item.getValue());
				break;
				
			}
		}
		httpClient = HttpClients.createDefault();
		List<GetQuoteTask> tasks = new ArrayList<GetQuoteTask>();
		if (source.equals(Constants.SOURCEFT)) {
			for (String stock : stocks) {
				GetQuoteTask task = new GetFTQuote(stock, this, httpClient,Constants.STOCKTYPE,tid);
				tasks.add(task);
				totalQuotes++;
			}
			for (String currency : currencies) {
				GetQuoteTask task = new GetFTQuote(currency, this, httpClient,Constants.CURRENCYTYPE,tid);
				tasks.add(task);
				totalQuotes++;
			}
          List<Future<QuotePrice>> futures = null;
           try {
                futures = threadPool.invokeAll(tasks, 120L, TimeUnit.SECONDS);
                debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"FT Tasks invoked "+tasks.size());
            } catch (InterruptedException e) {
                debugInst.debug("QuoteManager","getQuotes",MRBDebug.INFO,e.getMessage());
            }

            if (futures == null) {
                debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"FT Failed to invokeAll");
                return;
            }
            for (Future<QuotePrice> future : futures) {
                if (future.isCancelled()) {
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"FT One of the tasks has timeout.");
                    continue;
                }
                try {
                    future.get();
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.DETAILED,"FT task completed");
              } catch (InterruptedException e) {
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.DETAILED,e.getMessage());
                } catch (ExecutionException e) {
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.DETAILED,e.getMessage());
                } finally {
                }
            }
			String doneUrl ="moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.DONEQUOTECMD+"?"+Constants.TIDCMD+"="+tid;
			doneUrl += "&"+Constants.TOTALTYPE+"="+totalQuotes;
			doneUrl += "&"+Constants.OKTYPE +"="+successful;
			doneUrl += "&" + Constants.ERRTYPE+"="+failed;
			Main.context.showURL(doneUrl);
		}
		if (source.equals(Constants.SOURCEYAHOO)) {
			for (String stock : stocks) {
				GetQuoteTask task = new GetYahooQuote(stock, this, httpClient,Constants.STOCKTYPE,tid);
				tasks.add(task);
				totalQuotes++;
			}
			for (String currency : currencies) {
				GetQuoteTask task = new GetYahooQuote(currency, this, httpClient,Constants.CURRENCYTYPE,tid);
				tasks.add(task);
				totalQuotes++;
			}
          List<Future<QuotePrice>> futures = null;
           try {
                futures = threadPool.invokeAll(tasks, 120L, TimeUnit.SECONDS);
                debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"Yahoo Tasks invoked "+tasks.size());
            } catch (InterruptedException e) {
                debugInst.debug("QuoteManager","getQuotes",MRBDebug.INFO,e.getMessage());
            }

            if (futures == null) {
                debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"Yahoo Failed to invokeAll");
                return;
            }
            for (Future<QuotePrice> future : futures) {
                if (future.isCancelled()) {
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"Yahoo One of the tasks has timeout.");
                    continue;
                }
                try {
                    future.get();
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"Yahoo task completed");
                } catch (InterruptedException e) {
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.DETAILED,e.getMessage());
                } catch (ExecutionException e) {
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.DETAILED,e.getMessage());
                } finally {
                }
            }
			String doneUrl ="moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.DONEQUOTECMD+"?"+Constants.TIDCMD+"="+tid;
			doneUrl += "&"+Constants.TOTALTYPE+"="+totalQuotes;
			doneUrl += "&"+Constants.OKTYPE +"="+successful;
			doneUrl += "&" + Constants.ERRTYPE+"="+failed;
			Main.context.showURL(doneUrl);
		}
		if (source.equals(Constants.SOURCEYAHOOHIST)) {
			for (String stock : stocks) {
				GetQuoteTask task = new GetYahooHistQuote(stock, this, httpClient,Constants.STOCKTYPE,tid);
				tasks.add(task);
				totalQuotes++;
			}
			for (String currency : currencies) {
				GetQuoteTask task = new GetYahooHistQuote(currency, this, httpClient,Constants.CURRENCYTYPE,tid);
				tasks.add(task);
				totalQuotes++;
			}
          List<Future<QuotePrice>> futures = null;
           try {
                futures = threadPool.invokeAll(tasks, 120L, TimeUnit.SECONDS);
                debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"Yahoo History Tasks invoked "+tasks.size());
            } catch (InterruptedException e) {
                debugInst.debug("QuoteManager","getQuotes",MRBDebug.INFO,e.getMessage());
            }

            if (futures == null) {
                debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"Yahoo History Failed to invokeAll");
                return;
            }
            for (Future<QuotePrice> future : futures) {
                if (future.isCancelled()) {
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"Yahoo History One of the tasks has timeout.");
                    continue;
                }
                try {
                    future.get();
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.SUMMARY,"Yahoo History task completed");
                } catch (InterruptedException e) {
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.DETAILED,e.getMessage());
                } catch (ExecutionException e) {
                    debugInst.debug("QuoteManager","getQuotes",MRBDebug.DETAILED,e.getMessage());
                } finally {
                }
            }
			String doneUrl ="moneydance:fmodule:" + Constants.PROGRAMNAME + ":"+Constants.DONEQUOTECMD+"?"+Constants.TIDCMD+"="+tid;
			doneUrl += "&"+Constants.TOTALTYPE+"="+totalQuotes;
			doneUrl += "&"+Constants.OKTYPE +"="+successful;
			doneUrl += "&" + Constants.ERRTYPE+"="+failed;
			Main.context.showURL(doneUrl);
		}
		try {
			httpClient.close();
			threadPool.shutdown();
		}
		catch (IOException e) {
			e.printStackTrace();
			 debugInst.debug("QuoteManager","getQuotes",MRBDebug.DETAILED,e.getMessage());
		}
		
	}
	public void errorReturned(String tickerp) {
		failed++;	
	}
	public void doneReturned(String tickerp) {
		successful++;
	}}
