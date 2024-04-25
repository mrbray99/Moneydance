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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.time.LocalDate;

import java.nio.charset.StandardCharsets;


import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;


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
    private String convTicker;
    private String currency = "XXX";
    private ScanDate scanDate = new ScanDate();
    private Parameters params;

    public GetYahooHistQuote(String tickerp, QuoteListener listenerp, CloseableHttpClient httpClientp, String tickerTypep, String tidp, Integer lastPriceDatep) {
        super(tickerp, listenerp, httpClientp, tickerTypep, tidp);
        params = Parameters.getParameters();
        LocalDate now = LocalDate.now();
        LocalDate historyDate = now.minusMonths(params.getAmtHistory() + 1);
        historyDateInt = historyDate.getYear() * 10000 + (historyDate.getMonthValue()) * 100 + historyDate.getDayOfMonth();
        lastPriceDate = (lastPriceDatep == null ? 0 : lastPriceDatep);
        if (lastPriceDate < historyDateInt) {
            lastPriceDate = historyDateInt;
            debugInst.debug("GetYahooHistQuote", "construct", MRBDebug.DETAILED, "History date restricted to  " + lastPriceDate);
        }
        convTicker = ticker.replace("^", "%5E");
        if (tickerType.equals(Constants.STOCKTYPE))
            url = yahooSecURL + convTicker + "/history?p=" + convTicker;
        if (tickerType.equals(Constants.CURRENCYTYPE))
            url = yahooCurrURL + convTicker + "/history?p=" + convTicker;
        debugInst.debug("GetYahooHistQuote", "GetYahooHistQuote", MRBDebug.DETAILED, "Executing :" + url);
    }

    @Override
    synchronized public QuotePrice analyseResponse(CloseableHttpResponse response) throws IOException {
        debugInst.debug("GetYahooHistQuote", "analyseResponse", MRBDebug.DETAILED, "Analysing " + convTicker);
        QuotePrice quotePrice = new QuotePrice();
        quotePrice.setTicker(convTicker);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        try {
            String buffer = getJsonString(stream);
            try {
                parseDoc(buffer, quotePrice);
            } catch (IOException a) {
                debugInst.debug("GetYahooHistQuote", "analyseResponse", MRBDebug.INFO, "IOException " + a.getMessage());
                throw new IOException(a);
            }

        } catch (UnsupportedOperationException e) {
            debugInst.debug("GetYahooHistQuote", "analyseResponse", MRBDebug.INFO, "Unsupported Operation  " + e.getMessage());
            throw new IOException(e);
        } catch (MalformedURLException e) {
            debugInst.debug("GetYahooHistQuote", "analyseResponse", MRBDebug.INFO, "MalformedURL " + e.getMessage());
            throw (new IOException(e));
        } catch (ClientProtocolException e) {
            debugInst.debug("GetYahooHistQuote", "analyseResponse", MRBDebug.INFO, "ClientProtocolException " + e.getMessage());
            throw (new IOException(e));
        } catch (IOException e) {
            debugInst.debug("GetYahooHistQuote", "analyseResponse", MRBDebug.INFO, "IOException " + e.getMessage());
            throw (new IOException(e));
        } catch (Exception e) {
            e.printStackTrace();

        }

        return quotePrice;
    }

    private String getJsonString(InputStream stream) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String buffer;
            while ((buffer = reader.readLine()) != null) {
                result.append(buffer);
            }
        }
        if (result.toString().isEmpty())
            throw new IOException("Cannot find Yahoo price data");
        return result.toString();
    }

    private void parseDoc(String buffer, QuotePrice quotePrice) throws IOException {
        debugInst.debug("GetYahooHistQuote", "parseDoc", MRBDebug.INFO, "Parsing document for  " + convTicker);
        boolean priceFound = false;
        Integer historyDate;
        double highValue;
        double lowValue;
        double price;
        long volume;
        int currIndex = buffer.indexOf("Currency in ");
        if (currIndex < 0)
            throw new IOException("Can not find currency");
        String rest = buffer.substring(currIndex);
        currency = rest.substring(12, 15);
        quotePrice.setCurrency(currency);
        int start = rest.indexOf("<tbody>");
        if (start < 0)
            throw new IOException("History Data not found");
        int current = start + 7;
        rest = rest.substring(current);
        while (!rest.isEmpty() && !rest.startsWith("</tbody>")) {
            String row = rest.substring(0, rest.indexOf("</tr>") + 5);
            if (row.indexOf("Dividend") > 0) {
                rest = rest.substring(rest.indexOf("</tr>") + 5);
                continue;
            }
            String restRow;
            String value;
            int dataStart = row.indexOf("<td");
            if (dataStart < 0)
                continue;
            row = row.substring(dataStart);
            String column = getColumn(row); // date
            if (column==null || column.isEmpty())
                continue;
            value = getValue(column);
            if (value == null || value.equals("null")) {
                continue;
            }
            historyDate = scanDate.parseYahooDate(value);
            if (historyDate < lastPriceDate)
                break;

            restRow = row.substring(column.length());
            column = getColumn(restRow); // open
            if (column==null || column.isEmpty())
                continue;
            restRow = restRow.substring(column.length());
            column = getColumn(restRow); // high
            if (column==null || column.isEmpty())
                continue;
            value = getValue(column);
            if (value == null || value.equals("null"))
                highValue = 0.0D;
            else {
                value = value.replace(",", "");
                try {
                    highValue = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    debugInst.debug("GetYahooHistQuote", "parseDoc", MRBDebug.INFO, "Error in high value  " + convTicker);
                    highValue = 0.0;
                }
            }
            restRow = restRow.substring(column.length());
            column = getColumn(restRow); // low
            if (column==null || column.isEmpty())
                continue;
            value = getValue(column);
            if (value == null || value.equals("null"))
                lowValue = 0.0D;
            else {
                value = value.replace(",", "");
                try {
                    lowValue = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    debugInst.debug("GetYahooHistQuote", "parseDoc", MRBDebug.INFO, "Error in low value  " + convTicker);
                    lowValue = 0.0;
                }
            }
            restRow = restRow.substring(column.length());
            column = getColumn(restRow); // close
            if (column==null || column.isEmpty())
                continue;
            value = getValue(column);
            if (value == null || value.equals("null"))
                price = 0.0D;
            else {
                value = value.replace(",", "");
                try {
                    price = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    debugInst.debug("GetYahooHistQuote", "parseDoc", MRBDebug.INFO, "Error in low value  " + convTicker);
                    price = 0.0;
                }
            }
            restRow = restRow.substring(column.length());
            column = getColumn(restRow); // adj close
            if (column==null || column.isEmpty())
                continue;
            restRow = restRow.substring(column.length());
            column = getColumn(restRow); // volume
            if (column==null || column.isEmpty())
                continue;
            value = getValue(column);
            if (value == null || value.equals("null"))
                volume = 0L;
            else {
                value = value.replace(",", "");
                try {
                    volume = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    debugInst.debug("GetYahooHistQuote", "parseDoc", MRBDebug.INFO, "Error in low value  " + convTicker);
                    volume = 0L;
                }
            }
            if (!priceFound) {
                quotePrice.setTradeDateInt(historyDate);
                quotePrice.setTradeDate(historyDate + "T00:00");

                quotePrice.setPrice(price);
                quotePrice.setHighPrice(highValue);
                quotePrice.setLowPrice(lowValue);
                quotePrice.setVolume(volume);
                priceFound = true;
            } else {
                if (params.getHistory() && lastPriceDate != null) {
                    QuotePrice historyPrice = new QuotePrice();
                    historyPrice.setTradeDateInt(historyDate);
                    historyPrice.setTradeDate(historyDate + "T00:00");
                    historyPrice.setPrice(price);
                    historyPrice.setHighPrice(highValue);
                    historyPrice.setLowPrice(lowValue);
                    historyPrice.setVolume(volume);
                    quotePrice.addHistory(historyPrice.getTradeDateInt(), historyPrice.getPrice(), historyPrice.getHighPrice(),
                            historyPrice.getLowPrice(), historyPrice.getVolume());
                }
            }
            if (rest.indexOf("</tr>") > 0)
                rest = rest.substring(rest.indexOf("</tr>") + 5);
            else
                rest = "";
        }
    }

    private String getColumn(String row) {
        int end = row.indexOf("</td>");
        if (end < 0)
            return null;
        return row.substring(0, end + 5);
    }

    private String getValue(String column) {
        int i = 0;
        StringBuilder value = new StringBuilder();
        while (i < column.length() && column.charAt(i) != '<') {
            i++;
        }
        i++;
        while (i < column.length() && column.charAt(i) != '>') {
            i++;
        }
        i++;
        if (i >= column.length())
            return null;
        while (i < column.length() && column.charAt(i) != '<') {
            value.append(column.charAt(i));
            i++;
        }
        return value.toString();

    }
}