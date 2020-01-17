/**
 * Copyright 2018 hleofxquotes@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moneydance.modules.features.jasperreports;

import java.util.Date;

public class QuotePrice {
    private Symbol symbol;
    private Date date;
    private Double price;
    private String quoteSource;
    private String currency;
    private Long volume;
    private Exception exception;
    private Exception invokeException;
    private boolean cached;
    private Date requestedDate;
    private Date reponsedDate;
    private long delta;

    public String getQuoteSource() {
        return quoteSource;
    }

    public void setQuoteSource(String quoteSource) {
        this.quoteSource = quoteSource;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getInvokeException() {
        return invokeException;
    }

    public void setInvokeException(Exception invokeException) {
        this.invokeException = invokeException;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public Date getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Date requestedDate) {
        this.requestedDate = requestedDate;
    }

    public Date getReponsedDate() {
        return reponsedDate;
    }

    public void setReponsedDate(Date reponsedDate) {
        this.reponsedDate = reponsedDate;
    }

    public long getDelta() {
        return delta;
    }

    public void setDelta(long delta) {
        this.delta = delta;
    }

    public Long getVolume() {
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}

	@Override
    public String toString() {
        return "QuotePrice [symbol=" + symbol + ", date=" + date + ", price=" + price + ", quoteSource=" + quoteSource
                + ", currency=" + currency + ", exception=" + exception + ", invokeException=" + invokeException + "]";
    }
}
