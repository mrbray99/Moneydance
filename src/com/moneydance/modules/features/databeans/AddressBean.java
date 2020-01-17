package com.moneydance.modules.features.databeans;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.infinitekind.moneydance.model.AddressBookEntry;

public class AddressBean extends DataBean {
@Expose private long id;
@Expose private String name;
@Expose private String street;
@Expose private String phone;
@Expose private String email;
	/*
	 * transient fields
	 */
	private transient AddressBookEntry address;


	public AddressBean() {
	super();
	// TODO Auto-generated constructor stub
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static DataBean deserialize(String beanData) {
		AddressBean bean;
		Type listType = new TypeToken<AddressBean>(){}.getType();
		bean = new Gson().fromJson(beanData,listType);
		return bean;
	}
	@Override
	public void populateData() {
		setName(address.getName());
		setStreet(address.getAddressString());
		setEmail(address.getEmailAddress());
		setId(address.getID());
		setPhone(address.getPhoneNumber());
	}
}
