package com.moneydance.modules.features.databeans;

public class TransactionFactory {
	public static java.util.Collection generateCollection() {
		java.util.Vector<DataBean>  collection= new java.util.Vector<>();
		collection.add (new TransactionBean("Account","Address","check","cleared","16/11/18","17/11/18","description","status","tax",100.0D,"transfer"));
		return collection;
	}

}
