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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.securityquoteload.Constants;
import com.moneydance.modules.features.securityquoteload.QuotePrice;

public class GetYahooQuote extends GetQuoteTask {

    private String yahooSecURL = "https://query1.finance.yahoo.com/v8/finance/chart/";
    private String yahooCurrURL = "https://query1.finance.yahoo.com/v8/finance/chart/";
    private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

    public GetYahooQuote(String tickerp, QuoteListener listenerp, CloseableHttpClient httpClientp, String tickerTypep, String tidp) {
        super(tickerp, listenerp, httpClientp, tickerTypep, tidp);
        String convTicker = ticker.replace("^", "%5E");
        if (tickerType == Constants.STOCKTYPE)
            url = yahooSecURL + convTicker + "?p=" + convTicker + "&.tscr=fin-srch";
        if (tickerType == Constants.CURRENCYTYPE)
            url = yahooCurrURL + convTicker + "?p=" + convTicker;
        debugInst.debug("GetYahooQuote", "GetYahooQuote", MRBDebug.DETAILED, "Executing :" + url);
    }

    @Override
    synchronized public QuotePrice analyseResponse(CloseableHttpResponse response) throws IOException {

        QuotePrice quotePrice = new QuotePrice();
        HttpEntity entity = response.getEntity();
        try {
            InputStream stream = entity.getContent();
            String buffer = getJsonString(stream);
            JsonObject nodes = JsonParser.parseString(buffer).getAsJsonObject();
            try {
                parseDoc(nodes, quotePrice);
            } catch (IOException a) {
                debugInst.debug("GetQuoteTask", "analyseResponse", MRBDebug.INFO, "IOException " + a.getMessage());
                throw new IOException(a);
            }
        } catch (UnsupportedOperationException e) {
            throw new IOException(e);
        } catch (MalformedURLException e) {
            throw (new IOException(e));
        } catch (ClientProtocolException e) {
            throw (new IOException(e));
        } catch (IOException e) {
            throw (new IOException(e));
        } catch (Exception e) {
            e.printStackTrace();

        }

        return quotePrice;
    }

    private String getJsonString(InputStream stream) throws IOException {
        String result = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String buffer = "";
            while ((buffer = reader.readLine()) != null) {
                result += buffer;
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } finally {

                }
            }
        }
        if (result.isEmpty())
            throw new IOException("Cannot find Yahoo price data");
        return result;
    }

    private void parseDoc(JsonObject doc, QuotePrice quotePrice) throws IOException {
        Object quoteNode = null;
        Object resultNode = null;
        String timeZoneStr = "";
        Calendar tradeDateCal;
        String tradeDateStr = "";
        String highValue;
        String lowValue;
        String volume;
        quoteNode = doc.get("chart");
        if (quoteNode == null || !(quoteNode instanceof JsonObject))
            throw new IOException("Cannot parse response for " + ticker);
        resultNode = ((JsonObject) quoteNode).get("result");
        if (resultNode == null || !(resultNode instanceof JsonArray))
            throw new IOException("Cannot parse response for " + ticker);
        JsonElement meta = ((JsonArray) resultNode).get(0);
        if (resultNode == null || !(resultNode instanceof JsonArray))
            throw new IOException("Cannot parse response for " + ticker);
        JsonObject data = meta.getAsJsonObject();
        JsonObject fields = data.getAsJsonObject("meta");
        JsonElement itemNode = fields.get("regularMarketPrice");
        String value;
        if (itemNode != null) {
            try {
                quotePrice.setPrice(itemNode.getAsDouble());
            } catch (ClassCastException | IllegalStateException e) {
                debugInst.debug("GetQuoteTask", "parseDoc", MRBDebug.INFO, "Invalid price value ");
                quotePrice.setPrice(0.0D);
            }
        }
        itemNode = fields.get("currency");
        if (itemNode != null) {
            try {
                quotePrice.setCurrency(itemNode.getAsString());
            } catch (ClassCastException | IllegalStateException e) {
                debugInst.debug("GetQuoteTask", "parseDoc", MRBDebug.INFO, "Invalid currency ");
                quotePrice.setCurrency("XXX");
            }
        }
        itemNode = fields.get("regularMarketTime");
        if (itemNode != null) {
            try {
                tradeDateStr = itemNode.getAsString();
            } catch (ClassCastException | IllegalStateException e) {
                debugInst.debug("GetQuoteTask", "parseDoc", MRBDebug.INFO, "Invalid trade date ");
            }
        }
        itemNode = fields.get("regularMarketVolume");
        if (itemNode != null) {
            try {
                quotePrice.setVolume(itemNode.getAsLong());
            } catch (ClassCastException | IllegalStateException e) {
                debugInst.debug("GetQuoteTask", "parseDoc", MRBDebug.INFO, "Invalid volume ");
                quotePrice.setVolume(0L);
            }
        }
        itemNode = fields.get("timezone");
        if (itemNode != null) {
            try {
                timeZoneStr = itemNode.getAsString();
            } catch (ClassCastException | IllegalStateException e) {
                debugInst.debug("GetQuoteTask", "parseDoc", MRBDebug.INFO, "Invalid Time Zone ");
            }
        }
        itemNode = fields.get("regularMarketDayHigh");
        if (itemNode != null) {
            try {
                quotePrice.setHighPrice(itemNode.getAsDouble());
            } catch (ClassCastException | IllegalStateException e) {
                debugInst.debug("GetQuoteTask", "parseDoc", MRBDebug.INFO, "Invalid Market High ");
                quotePrice.setHighPrice(0.0D);
            }
        }
        itemNode = fields.get("regularMarketDayLow");
        if (itemNode != null) {
            try {
                quotePrice.setLowPrice(itemNode.getAsDouble());
            } catch (ClassCastException | IllegalStateException e) {
                debugInst.debug("GetQuoteTask", "parseDoc", MRBDebug.INFO, "Invalid Market High ");
                quotePrice.setLowPrice(0.0D);
            }
        }
        if (tradeDateStr.isEmpty() || timeZoneStr.isEmpty())
            throw new IOException("Cannot find trade date for " + ticker);
        tradeDateCal = getLastTrade(tradeDateStr, timeZoneStr);
        quotePrice.setTradeDate(dFormat.format(tradeDateCal.getTime()) + "T00:00");

        return;
    }

    private Calendar getLastTrade(String regularMarketTime, String exchangeTimezoneName) throws IOException {
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
