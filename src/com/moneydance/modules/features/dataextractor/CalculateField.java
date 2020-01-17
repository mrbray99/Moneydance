package com.moneydance.modules.features.dataextractor;

import java.util.Map;


public class CalculateField implements java.io.Serializable{
	/*
	 * transient fields
	 */
	/*
	 * Saved fields
	 */

	private String name;
	private String formula;
	private Map<String,String> lookupValues;
	private Constants.CalculateFieldType type;
	
	public CalculateField () {
		
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the type
	 */
	public Constants.CalculateFieldType getType() {
		return type;
	}
	public String getTypeDesc(){
		return getDesc(type);
	}
	public static String getDesc(Constants.CalculateFieldType typep){
		switch (typep) {
		case FORMULA:
			return Main.locale.getString(Constants.CF_FORMULA,"Formula");
		case STATUSLOOKUP:
			return Main.locale.getString(Constants.CF_STATUSLOOK,"Status ");
		case TYPELOOKUP:
			return Main.locale.getString(Constants.CF_TYPELOOK,"Type ");
		case DATERANGE:
			return Main.locale.getString(Constants.CF_DATERANGE,"Date Range");
		case CHEQUERANGE:
			return Main.locale.getString(Constants.CF_CHEQUERANGE,"Cheque Range");
		case CATEGORYLOOKUP:
			return Main.locale.getString(Constants.CF_CATEGORY,"Category Lookup");
		case TAGLOOKUP:
			return Main.locale.getString(Constants.CF_TAG,"Tag Lookup");
		case DESCRIPTION:
			return Main.locale.getString(Constants.CF_DESCRIPTION,"Description Match");
		
		}
		return "";
	}
	public static String[] getTypeArray(){
		String[] types = new String[8];
		Constants.CalculateFieldType[] names = Constants.CalculateFieldType.values();
		int i=0;
		for (Constants.CalculateFieldType type : names){
			types[i++] = getDesc(type);
		}
		return types;
	}
	/**
	 * @return the formula
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * @return the lookupValues
	 */
	public Map<String, String> getLookupValues() {
		return lookupValues;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Constants.CalculateFieldType type) {
		this.type = type;
	}

	/**
	 * @param formula the formula to set
	 */
	public void setFormula(String formula) {
		this.formula = formula;
	}

	/**
	 * @param lookupValues the lookupValues to set
	 */
	public void setLookupValues(Map<String, String> lookupValues) {
		this.lookupValues = lookupValues;
	}
}
