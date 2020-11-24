/*
 * Copyright (c) 2018, Michael Bray.  All rights reserved.
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.infinitekind.moneydance.model.AccountBook;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class Parameters implements Serializable{
	/*
	 * Static and transient fields are not stored 
	 */
	private static final long serialVersionUID = 1L;
	private transient FileInputStream curInFile;
	private transient AccountBook curAcctBook;
	private transient File curFolder;
	private transient String fileName;
	public transient static Integer [] multipliers = {-4,-3,-2,-1,0,1,2,3,4};
	public transient static String[] CURRENCYDATES = {"Trade Date","Today's Date"};
	public transient static int USETRADEDATE = 0;
	public transient static int USETODAYSDATE = 1;
	public transient static Integer [] decimals = {2,3,4,5,6,7,8};
	public transient static String [] maximums = {"No Limit","6","7","8","9"};
	private transient SortedMap<String, Integer> mapAccountsList;
	private transient MRBDebug debugInst = Main.debugInst;
	private transient String[] arrSource = {Constants.DONOTLOAD,Constants.YAHOO,Constants.FT,Constants.YAHOOHIST,Constants.FTHIST};
	private transient String[] curSource = {Constants.DONOTLOAD,Constants.YAHOO,Constants.YAHOOHIST,Constants.FT};
	private transient List<NewAccountLine>listNewAccounts;
	private transient NewParameters newParams;
	private transient ExchangeList exchanges;
	private transient PseudoList pseudoList;
	private transient SortedMap<String, ExchangeLine> mapExchangeLines;
	private transient SortedMap<String,PseudoCurrency> pseudoCurrencies;
	private transient SortedMap<String,String>mapExchangeSelect;
	private transient Boolean addVolume;
	private transient Boolean history;
	private transient boolean export;
	private transient boolean exportAuto;
	private transient String exportFolder;
	private transient boolean isDirty;
	/*
     * The following fields are stored
     */

	private int noDecimals;
	private int newNoDecimals;
	private boolean includeZero;
	private boolean includeCurrency;
	private int currencyDate;
	private boolean roundPrices;
	private List<AccountLine> listAccounts;
	public Parameters() {
		curAcctBook = Main.context.getCurrentAccountBook();
		curFolder = curAcctBook.getRootFolder();
		/*
		 * Determine if the new file exists
		 */
		boolean createNew = false;
		isDirty = false;
		switch (findFile(curFolder)) {
		case NEW2 :
			fileName = curFolder.getAbsolutePath()+"/"+Constants.PARAMETERFILE2;
			try {
				JsonReader reader = new JsonReader(new FileReader(fileName));
				debugInst.debug("Parameters", "Parameters", MRBDebug.DETAILED, "Parameters found "+fileName);
				newParams = new Gson().fromJson(reader,NewParameters.class);
				listNewAccounts = newParams.getListAccounts();
				reader.close();
			}
			catch (JsonParseException e) {
				debugInst.debug("Parameters", "Parameters", MRBDebug.DETAILED, "Parse Exception "+e.getMessage());
				createNew = true;
			}
			catch (IOException e){
				createNew = true;
			}
			break;
		case NEW1 :


			/*
			 * determine if old file already exists
			 */
			fileName = curFolder.getAbsolutePath()+"/"+Constants.PARAMETERFILE2;
			try {
				curInFile = new FileInputStream(fileName);
				ObjectInputStream ois = new ObjectInputStream(curInFile);
				/*
				 * file exists, copy temporary object to this object
				 */
				Parameters tempParams = (Parameters) ois.readObject();
				debugInst.debug("Parameters", "Parameters", MRBDebug.DETAILED, "Parameters found "+fileName);
				newParams = new NewParameters();
				newParams.setIncludeZero(tempParams.includeZero);
				newParams.setIncludeCurrency(tempParams.includeCurrency);
				newParams.setNoDecimals (tempParams.noDecimals);
				listNewAccounts = new ArrayList<>();
				for (AccountLine line : tempParams.getAccountsList()){
					NewAccountLine newLine = new NewAccountLine();
					newLine.setSource(line.getSource());
					newLine.setName(line.getName());
					newLine.setCurrency(line.isCurrency());
					newLine.setExchange(null);
					listNewAccounts.add(newLine);
				}
				newParams.setListAccounts(listNewAccounts);
				curInFile.close();
			}
			catch (IOException | ClassNotFoundException ioException) {
				createNew = true;
			}
			break;
		case OLD2 :
			fileName = curFolder.getAbsolutePath()+"\\"+Constants.PARAMETERFILE2;
			try {
				JsonReader reader = new JsonReader(new FileReader(fileName));
				debugInst.debug("Parameters", "Parameters", MRBDebug.DETAILED, "Parameters found "+fileName);
				newParams = new Gson().fromJson(reader,NewParameters.class);
				listNewAccounts = newParams.getListAccounts();
				reader.close();
			}
			catch (JsonParseException e) {
				debugInst.debug("Parameters", "Parameters", MRBDebug.DETAILED, "Parse Exception "+e.getMessage());
				createNew = true;
			}
			catch (IOException e){
				createNew = true;
			}
			break;
		case OLD1 :
			/*
			 * determine if old file already exists
			 */
			fileName = curFolder.getAbsolutePath()+"\\"+Constants.PARAMETERFILE2;
			try {
				curInFile = new FileInputStream(fileName);
				ObjectInputStream ois = new ObjectInputStream(curInFile);
				/*
				 * file exists, copy temporary object to this object
				 */
				Parameters tempParams = (Parameters) ois.readObject();
				debugInst.debug("Parameters", "Parameters", MRBDebug.DETAILED, "Parameters found "+fileName);
				newParams = new NewParameters();
				newParams.setIncludeZero(tempParams.includeZero);
				newParams.setIncludeCurrency(tempParams.includeCurrency);
				newParams.setNoDecimals (tempParams.noDecimals);
				listNewAccounts = new ArrayList<>();
				for (AccountLine line : tempParams.getAccountsList()){
					NewAccountLine newLine = new NewAccountLine();
					newLine.setSource(line.getSource());
					newLine.setName(line.getName());
					newLine.setCurrency(line.isCurrency());
					newLine.setExchange(null);
					listNewAccounts.add(newLine);
				}
				newParams.setListAccounts(listNewAccounts);
				curInFile.close();
			}	
			catch (IOException | ClassNotFoundException ioException) {
				createNew = true;
			}
			break;
		case NONE :	
			createNew = true;
		
		}

		/*
		 * First time being run change column widths
		 */
		int [] columnWidths;
		columnWidths = Main.preferences.getIntArray(Constants.PROGRAMNAME+"."+Constants.CRNTCOLWIDTH);
		if (columnWidths.length == 0)
			columnWidths = Main.preferences.getIntArray(Constants.CRNTCOLWIDTH);		
		if (columnWidths.length == 0)
			columnWidths = Constants.DEFAULTCOLWIDTH;
		else
		{
			if (columnWidths.length == Constants.NUMTABLECOLS-1){
				int [] newCols = Constants.DEFAULTCOLWIDTH;
				newCols[0] = columnWidths[0];
				newCols[1] = columnWidths[1];
				for (int i=2;i<columnWidths.length;i++) 
					newCols[i+1] = columnWidths[i];
				newCols[2] = Constants.DEFAULTCOLWIDTH[2];
				columnWidths = newCols;
			}
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.CRNTCOLWIDTH, columnWidths);
			Main.preferences.isDirty();
		}
		if (createNew) {
				/*
				 * file does not exist, initialize fields
				 */
				newParams = new  NewParameters();
				fileName = curFolder.getAbsolutePath()+"/"+Constants.PARAMETERFILE2;
			/*
			 * create the file
			 */
			try {
			   FileWriter writer = new FileWriter(fileName);
			   String jsonString = new Gson().toJson(newParams);
			   writer.write(jsonString);
			   writer.close();			  
             } catch (IOException i) {
				 i.printStackTrace();
	
             }
		}
		this.includeZero= newParams.isIncludeZero();
		this.includeCurrency= newParams.isIncludeCurrency();
		if(newParams.getNewNoDecimals() == -1)
			newParams.setNewNoDecimals(newParams.getNoDecimals()+4);
		this.newNoDecimals = newParams.getNewNoDecimals();
		this.noDecimals= newParams.getNewNoDecimals();
		this.listAccounts= new ArrayList<>();
		this.listNewAccounts = newParams.getListAccounts();
		this.addVolume = newParams.isAddVolume();
		this.history = newParams.isHistory();
		this.export = newParams.isExport();
		this.exportAuto = newParams.isExportAuto();
		this.exportFolder = newParams.getExportFolder();
		this.roundPrices = newParams.isRoundPrices();
		mapExchangeSelect = new TreeMap<>();
		for (NewAccountLine line : listNewAccounts){
			AccountLine newLine = new AccountLine(line.getName(),line.getSource());
			listAccounts.add(newLine);
			if (line.getExchange() != null)
				mapExchangeSelect.put(line.getName(),line.getExchange());
		}
		buildAccounts();
		exchanges = new ExchangeList();
		exchanges.getData();
		mapExchangeLines = exchanges.getList();
		pseudoList = new PseudoList();
		pseudoList.getData();
		pseudoCurrencies = pseudoList.getList();
	}
	private Constants.FILEFOUND findFile(File curFolder) {
		FileInputStream testFile;
		String fileName=curFolder.getAbsolutePath()+"/" +Constants.PARAMETERFILE2;
		try {
			testFile = new FileInputStream(fileName);
			testFile.close();
			debugInst.debug("Parameters", "findFile", MRBDebug.DETAILED, "New 2");
			return Constants.FILEFOUND.NEW2;
		}
		catch (IOException e) {
		}
		fileName=curFolder.getAbsolutePath()+"/" +Constants.PARAMETERFILE1;
		try {
			testFile = new FileInputStream(fileName);
			testFile.close();
			debugInst.debug("Parameters", "findFile", MRBDebug.DETAILED, "New 1");
			return Constants.FILEFOUND.NEW1;
		}
		catch (IOException e) {
		}
		fileName=curFolder.getAbsolutePath()+"\\" +Constants.PARAMETERFILE2;
		try {
			testFile = new FileInputStream(fileName);
			testFile.close();
			debugInst.debug("Parameters", "findFile", MRBDebug.DETAILED, "Old 2");
			return Constants.FILEFOUND.OLD2;
		}
		catch (IOException e) {
		}
		fileName=curFolder.getAbsolutePath()+"\\" +Constants.PARAMETERFILE1;
		try {
			testFile = new FileInputStream(fileName);
			testFile.close();
			debugInst.debug("Parameters", "findFile", MRBDebug.DETAILED, "Old 1");
			return Constants.FILEFOUND.OLD1;
		}
		catch (IOException e) {
		}
		debugInst.debug("Parameters", "findFile", MRBDebug.DETAILED, "None");
		return Constants.FILEFOUND.NONE;
	}
	
    public SortedMap<String,PseudoCurrency> ReadPseudo() {
    	SortedMap<String,PseudoCurrency> configuration = new TreeMap<>();
        File file = DirectoryUtil.getCurrencyConfigurationFile();
        List<PseudoCurrency> list = new ArrayList<>();
        if (!file.exists()) {
            PseudoCurrency pCur = new PseudoCurrency();
            pCur.setPseudo("GBX");
            pCur.setMultiplier(0.01D);
            pCur.setReplacement("GBP");
            list.add(pCur);
            WritePseudo(list);
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                Type type = new TypeToken<List<PseudoCurrency>>() {}.getType();
                list = gson.fromJson(reader,type);
            } catch (IOException e) {
    			debugInst.debug("Parameters", "ReadPseudo", MRBDebug.INFO, "Problem reading pseudo currency file "+e.getMessage());       	
             }
        }
        for (PseudoCurrency curr :list){
        	configuration.put(curr.getPseudo(), curr);
        }
        return configuration;
    }

    public void WritePseudo(List<PseudoCurrency>configuration) {
        if (configuration == null) {
            return;
        }
        File file = DirectoryUtil.getCurrencyConfigurationFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            Gson gson = gsonBuilder.create();
            gson.toJson(configuration, writer);
        } catch (IOException e) {
			debugInst.debug("Parameters", "WritePseudo", MRBDebug.INFO, "Problem writing pseudo currency file "+e.getMessage());       	
        }
    }

	/*
	 * Zero
	 */
	public boolean getZero () {
		return includeZero;
	}
	public void setZero (boolean bZerop) {
		includeZero = bZerop;
		isDirty=true;
	}
	/*
	 * Currency
	 */
	public boolean getCurrency () {
		return includeCurrency;
	}
	public void setCurrency (boolean includeCurrencyp) {
		includeCurrency = includeCurrencyp;
		isDirty=true;
	}
	/*
	 * add Volume figures
	 */
	public boolean getAddVolume() {
		return addVolume;
	}
	public void setAddVolume(boolean addVolumep){
		addVolume = addVolumep;
		isDirty=true;
	}
	/*
	 * include history
	 */
	public boolean getHistory() {
		return history;
	}
	public void setHistory(boolean historyp){
		history = historyp;
		isDirty=true;
	}
	public boolean isExport() {
		return export;
	}
	public void setExport(boolean export) {
		this.export = export;
		isDirty=true;
	}
	public boolean isExportAuto() {
		return exportAuto;
	}
	public void setExportAuto(boolean exportAuto) {
		this.exportAuto = exportAuto;
		isDirty=true;
	}
	public String getExportFolder() {
		return exportFolder;
	}
	public void setExportFolder(String exportFolder) {
		this.exportFolder = exportFolder;
		isDirty=true;
	}
	/*
	 * Decimal
	 */
	public int getDecimal () {
		return newNoDecimals;
	}
	public void setDecimal (int noDecimalp) {
		isDirty=true;
		newNoDecimals = noDecimalp;
	}
	/*
	 * Round Prices flag
	 */
	public boolean isRoundPrices() {
		return roundPrices;
	}
	public void setRoundPrices(boolean roundPrices) {
		this.roundPrices = roundPrices;
		isDirty=true;
	}
	/*
	 * Currency Date
	 */
	public int getCurrencyDate () {
		return currencyDate;
	}
	public void setCurrencyDate (int currencyDatep) {
		currencyDate = currencyDatep;
		isDirty=true;
	}
	
	
	public String [] getSourceArray() {
		return arrSource;
	}
	public String [] getCurSourceArray() {
		return curSource;
	}
	public SortedMap<String,PseudoCurrency> getPseudoCurrencies(){
		return pseudoCurrencies;
	}
	/*
	 * Accounts
	 * 
	 * Only valid sources are stored.   'Do Not Load' is not stored
	 */
	private void buildAccounts() {
		if (listAccounts == null)
			listAccounts = new ArrayList<>();
		mapAccountsList = new TreeMap<>();
		for (AccountLine alTemp:listAccounts) {
			if (alTemp.getSource()!= 0)
				mapAccountsList.put(alTemp.getName(), alTemp.getSource());
		}
	}
	
	public List<AccountLine> getAccountsList() {
		return listAccounts;
	}
	public SortedMap<String,Integer> getAccountsMap() {
		return mapAccountsList;
	}
	public String getNewTicker(String ticker, String exchange, int source){
		if (exchange == null)
			return ticker;
		String newTicker = ticker;
		ExchangeLine line = mapExchangeLines.get(exchange);
		if (line!=  null) {
			if (source == Constants.YAHOOINDEX || source == Constants.YAHOOHISTINDEX)
				newTicker = line.getYahooPrefix()+ticker+line.getYahooSuffix();
			if (source == Constants.FTINDEX || source ==Constants.FTHISTINDEX)
				newTicker = line.getFtPrefix()+ticker+line.getFtSuffix();
		}
		return newTicker;
	}
	public SortedMap<String,ExchangeLine> getExchangeLines() {
		return mapExchangeLines;
	}
	public SortedMap<String,String> getExchangeSelect() {
		return mapExchangeSelect;
	}
	public void setExchange(String ticker,String exchange) {
		if (exchange == null)
			mapExchangeSelect.remove(ticker);
		else
			if (mapExchangeSelect.containsKey(ticker))
				mapExchangeSelect.replace(ticker,exchange);
			else
				mapExchangeSelect.put(ticker,exchange);
	}
	
	/*
	 * when changing the source for an account the entry is deleted if set to Do Not Load
	 */
	public void updateAccountSource (String accountName, Integer sourceID){
		Boolean found = false; 
		for (Iterator<AccountLine> itAccounts = listAccounts.listIterator();itAccounts.hasNext();){
			AccountLine alTemp = itAccounts.next();
			if (alTemp.getName().equals(accountName)){
				if (sourceID == 0)
					itAccounts.remove();
				else 
					alTemp.setSource(sourceID);
				found = true;
			}
		}
		if (!found && sourceID != 0) {
			AccountLine alNew = new AccountLine(accountName,sourceID);
			listAccounts.add(alNew);
		}
		if (mapAccountsList.containsKey(accountName)){
			if (sourceID == 0)
				mapAccountsList.remove(accountName);
			else
				mapAccountsList.replace(accountName, sourceID);
		}
		else
			if (sourceID != 0)
				mapAccountsList.put(accountName, sourceID);
		
	}
	public boolean paramsChanged() {
		return isDirty;
	}
	/*
	 * Save
	 */
	public void save() {
		/*
		 * Save the parameters into the specified file
		 */
		newParams.setIncludeCurrency(includeCurrency);
		newParams.setIncludeZero(includeZero);
		newParams.setNoDecimals(noDecimals);
		newParams.setNewNoDecimals(newNoDecimals);
		newParams.setAddVolume(addVolume);
		newParams.setHistory(history);
		newParams.setExportAuto(exportAuto);
		newParams.setExportFolder(exportFolder);
		newParams.setExport(export);
		newParams.setRoundPrices(roundPrices);
		listNewAccounts = new ArrayList<>();
		for (AccountLine line : listAccounts){
			NewAccountLine newLine = new NewAccountLine();
			newLine.setSource(line.getSource());
			newLine.setName(line.getName());
			newLine.setCurrency(line.isCurrency());
			if (mapExchangeSelect.containsKey(line.getName())) {
				debugInst.debug("Parameters","save",MRBDebug.SUMMARY,"Exchange set to "+mapExchangeSelect.get(line.getName()));
				newLine.setExchange(mapExchangeSelect.get(line.getName()));
			}
			listNewAccounts.add(newLine);
		}
		newParams.setListAccounts(listNewAccounts);

		/*
		 * create the file
		 */
		fileName = curFolder.getAbsolutePath()+"/"+Constants.PARAMETERFILE2;
		try {
			   FileWriter writer2 = new FileWriter(fileName);
			   String jsonString = new Gson().toJson(newParams);
			   writer2.write(jsonString);
			   writer2.close();			  
          } catch (IOException i) {
					   i.printStackTrace();
	
          }
		isDirty = false;
	}
	public void print() {
		debugInst.debug("Parameters","Zero ",MRBDebug.SUMMARY,includeZero ? "True": "false");
		debugInst.debug("Parameters","Currency ",MRBDebug.SUMMARY,includeCurrency ? "True": "false");
		debugInst.debug("Parameters","Decimal ",MRBDebug.SUMMARY,String.valueOf(newNoDecimals));
	}

}
