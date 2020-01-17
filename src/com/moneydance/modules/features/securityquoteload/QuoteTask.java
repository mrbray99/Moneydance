/*
 *  Copyright (c) 2020, Michael Bray and Hung Le.  All rights reserved.
 *  
 *  NOTE: this module is based on work by Hung Le, no breach of copyright is intended and no 
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
import java.util.concurrent.Callable;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import com.moneydance.modules.features.mrbutil.MRBDebug;

public abstract class QuoteTask<V> implements Callable<V> {
	protected MRBDebug debugInst = MRBDebug.getInstance();
	protected String ticker;
	protected QuoteListener listener;
	protected CloseableHttpClient httpClient;
	protected String url;
	protected String tickerType;
	protected String tid;
	public QuoteTask(String tickerp, QuoteListener listenerp, CloseableHttpClient httpClientp, String tickerTypep,String tidp) {
		ticker = tickerp;
		listener = listenerp;
		httpClient = httpClientp;
		tickerType = tickerTypep;
		tid = tidp;
	}
	@Override
	public V call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public void setListener(QuoteListener listenerp) {
		listener = listenerp;
	}
	
	public QuotePrice analyseResponse(CloseableHttpResponse response) throws IOException {
		return null;
	}
	

}
