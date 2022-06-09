package com.moneydance.modules.features.securityquoteload;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class ScanDate {
	private enum MonthStr{
		Jan("jan","January",0),
		Feb("feb","February",1),
		Mar("mar","March",2),
		Apr("apr","April",3),
		May("may","May",4),
		Jun("jun","June",5),
		Jul("jul","July",6),
		Aug("aug","August",7),
		Sep("sep","September",8),
		Oct("oct","October",9),
		Nov("nov","November",10),
		Dec("dec","December",11);
		private String shortName;
		private String longName;
		private Integer month;
		MonthStr(String shortName, String longName, Integer month){
			this.shortName = shortName;
			this.longName = longName;
			this.month = month;
		}
		public String getShortName() {
			return shortName;
		}
		public String getLongName() {
			return longName;
		}
		public Integer getMonth() {
			return month;
		}
		public static MonthStr  findShortMonth(String shortMonth) {
			for (MonthStr  tmpType:MonthStr.values()) {
				if (shortMonth.equalsIgnoreCase(tmpType.getShortName()))
					return tmpType;
			}
			return null;
		}
		public static MonthStr  findLongMonth(String longMonth) {
			for (MonthStr  tmpType:MonthStr.values()) {
				if (longMonth.equalsIgnoreCase(tmpType.getLongName()))
					return tmpType;
			}
			return null;
		}
	}
	public Date parseString(String dateStr) throws IOException{
		String [] parts= new String[5];
		String dayOfWeek;
		String month;
		String dayStr="";
		String yearStr="";
		MonthStr monthEntry=null;;
		Integer monthNum;
		Integer day;
		Integer year;
		if (dateStr.indexOf(',')>0)
			parts = dateStr.split(",");
		else 
			parts = dateStr.split(" ");
		if (parts[0].length() > 3 && parts[0].substring(parts[0].length()-3).equalsIgnoreCase("day")) {
			dayOfWeek = parts[0];
			parts[1]=parts[1].trim();
			int ind = parts[1].indexOf(' ');
			month = parts[1].substring(0,ind);
			monthEntry = MonthStr.findLongMonth(month);
			dayStr = parts[1].substring(ind+1).strip().replaceAll("\\p{Punct}","");
			yearStr = parts[2].strip().replaceAll("\\p{Punct}","");
		}
		else {
			if (parts[0].length()==3) {
				month = parts[0];
				monthEntry = MonthStr.findShortMonth(month);
				dayStr = parts[1].strip().replaceAll("\\p{Punct}","");
				yearStr = parts[2].strip().replaceAll("\\p{Punct}","");
			}	
		}
		if (monthEntry == null || dayStr.isEmpty()|| yearStr.isEmpty())
			throw new IOException("Invalid date \""+dateStr+"\"");
		try {
			day = Integer.parseInt(dayStr);
			year = Integer.parseInt(yearStr);
		}
		catch (NumberFormatException e) {
			throw new IOException("Invalid date \""+dateStr+"\"");
		}
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthEntry.getMonth(),day);
		return new Date(cal.getTimeInMillis());
	}

}
