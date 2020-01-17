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

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JProgressBar;

import com.moneydance.modules.features.mrbutil.MRBDebug;



final class GetQuotesProgressMonitor {

	private AtomicInteger subTaskSize = new AtomicInteger(0);
	private AtomicInteger completedTasks = new AtomicInteger(0);
	private JProgressBar progressBar = null;
	private MRBDebug debugInst = MRBDebug.getInstance();
	private TaskListener window;
	private SortedMap<String,TaskCounts> uuidStatus;
	private AtomicBoolean completed;
	private SortedMap<String, Integer> tickerStatus;
	private SortedMap<String,String>tids;
	public GetQuotesProgressMonitor(JProgressBar progressBar, TaskListener objWindow, SortedMap<String,Integer> tickerStatusp) {
		this.progressBar = progressBar;
		this.window = objWindow;
		tickerStatus = tickerStatusp;
		tickerStatus.clear();
		uuidStatus = new TreeMap<String,TaskCounts>();
		tids= new TreeMap<String,String>();
		completed = new AtomicBoolean(false);
	}

	public synchronized void started(String strStock,String uuid) {
		debugInst.debug("GetQuotesProgressMonitor","started",MRBDebug.SUMMARY,"> STARTED stock=" + strStock);
		if (!uuidStatus.containsKey(uuid))
			uuidStatus.put(uuid, new TaskCounts());
		if (strStock.startsWith(Constants.CURRENCYID))
			strStock = strStock.substring(3);
		if (tickerStatus.containsKey(strStock))
			tickerStatus.replace(strStock, Constants.TASKSTARTED);
		else
			tickerStatus.put(strStock, Constants.TASKSTARTED);
		TaskCounts counts = uuidStatus.get(uuid);
		counts.incTotal();
		if (tids.containsKey(strStock))
			tids.replace(strStock,uuid);
		else
			tids.put(strStock, uuid);
	}
	public void failed(String stock){
		failed(stock,tids.get(stock));
	}
	public synchronized void failed(String strStock,String uuid){
		debugInst.debug("GetQuotesProgressMonitor","failed",MRBDebug.SUMMARY,"> FAILED stock=" + strStock);
		if (strStock.startsWith(Constants.CURRENCYID))
			strStock = strStock.substring(3);
		if (tickerStatus.containsKey(strStock)) {
			/*
			 * Only increment once if failed, there can be several messages
			 */
			if (tickerStatus.get(strStock) == Constants.TASKSTARTED){
				debugInst.debug("GetQuotesProgressMonitor","Completed Count Incremented",MRBDebug.SUMMARY,"> FAILED stock=" + strStock);
				completedTasks.getAndIncrement();
				tickerStatus.replace(strStock, Constants.TASKFAILED);
				if (uuidStatus.containsKey(uuid.toString())) {
					TaskCounts counts = uuidStatus.get(uuid.toString());
					counts.incFailed();
				}
			}
		}

		updateProgress();
	}

	public synchronized void ended(String strStock,String uuid) {
		debugInst.debug("GetQuotesProgressMonitor","ended",MRBDebug.SUMMARY,"> ENDED stock=" + strStock);
		completedTasks.getAndIncrement();
		debugInst.debug("GetQuotesProgressMonitor","Completed Count Incremented",MRBDebug.SUMMARY,"> Ended stock=" + strStock);
		if (strStock.startsWith(Constants.CURRENCYID))
			strStock = strStock.substring(3);
		if (tickerStatus.containsKey(strStock)) {
			if (tickerStatus.get(strStock)== Constants.TASKSTARTED) {
				tickerStatus.replace(strStock, Constants.TASKCOMPLETED);
				if (uuidStatus.containsKey(uuid.toString())) {
					TaskCounts counts = uuidStatus.get(uuid.toString());
					counts.incSuccess();
				}
			}
		}
		updateProgress();
	}
	public synchronized void done(String uuid,int totalQuotes,int successful, int failed){
		debugInst.debug("GetQuotesProgressMonitor","done",MRBDebug.INFO,"Done message total="+totalQuotes+" ok="+successful+" error="+failed);
		TaskCounts counts=uuidStatus.containsKey(uuid)?uuidStatus.get(uuid):new TaskCounts();
		if (totalQuotes != counts.getTotal())
			debugInst.debug("GetQuotesProgressMonitor","done",MRBDebug.INFO,"Totals disagree received="+totalQuotes+" sent="+subTaskSize.get()+" count="+counts.getTotal());
		if (successful != counts.getSuccess())
			debugInst.debug("GetQuotesProgressMonitor","done",MRBDebug.INFO,"Completed task numbers disagree received="+successful+" processed="+counts.getSuccess());
		for (Entry<String, Integer> status : tickerStatus.entrySet()){
			if (status.getValue()==Constants.TASKSTARTED)
				if (uuid.equals(tids.get(status.getKey())))
					failed (status.getKey(),uuid);
		}
		uuidStatus.remove(uuid);
		if (uuidStatus.isEmpty()){
			debugInst.debug("GetQuotesProgressMonitor","done",MRBDebug.DETAILED,"  no more quotes");
			if (progressBar != null) {
				progressBar.setValue(100);
				window.Update();
			}
			window.TasksCompleted();       
		}
	}
	private void updateProgress() {
		int percentage = completedTasks.get() * 100 / 
				subTaskSize.get();
		debugInst.debug("GetQuotesProgressMonitor","ended-run",MRBDebug.DETAILED,"  progressBar % " + percentage);
		debugInst.debug("GetQuotesProgressMonitor","ended-run",MRBDebug.DETAILED,"Size=" + subTaskSize.get()+" completed="+completedTasks.get());
		if (progressBar != null) {
			progressBar.setValue(percentage);
			window.Update();
		}
	}
	public boolean checkTid(String uuid){
		debugInst.debug("GetQuotesProgressMonitor","checkTid",MRBDebug.DETAILED,"  checking " + uuid);
		if (uuidStatus.containsKey(uuid)) {
			debugInst.debug("GetQuotesProgressMonitor","checkTid",MRBDebug.DETAILED,"found " + uuid);
			return true;
		}
		debugInst.debug("GetQuotesProgressMonitor","checkTid",MRBDebug.DETAILED,"not found " + uuid);
		return false;
	}
	public void setSubTaskSize(int size) {
		debugInst.debug("GetQuotesProgressMonitor","setSubTaskSize",MRBDebug.DETAILED,"setSubTaskSize=" + size);
		this.subTaskSize.set(size);
		this.completedTasks.set(0);
		completed.set(false);
	}
	private class TaskCounts {
		private Integer total;
		private Integer success;
		private Integer failed;	

		public TaskCounts() {
			total = 0;
			success = 0;
			failed = 0;
		}
		/**
		 * @return the total
		 */
		protected synchronized Integer getTotal() {
			return total;
		}

		/**
		 * @return the success
		 */
		protected synchronized Integer getSuccess() {
			return success;
		}


		/**
		 * @param total the total to set
		 */
		protected synchronized void incTotal() {
			this.total++;
		}

		/**
		 * @param success the success to set
		 */
		protected synchronized void incSuccess() {
			this.success++;
		}

		/**
		 * @param failed the failed to set
		 */
		protected synchronized void incFailed() {
			this.failed++;
		}


	}

}