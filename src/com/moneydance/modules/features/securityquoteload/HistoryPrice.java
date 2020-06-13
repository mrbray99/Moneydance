package com.moneydance.modules.features.securityquoteload;

public class HistoryPrice {
	private Integer date;
	private Double price;
	private Long volume;
	
	public HistoryPrice(Integer date, Double price,Long volume) {
		super();
		this.date = date;
		this.price = price;
		this.volume = volume;
	}
	public Integer getDate() {
		return date;
	}
	public void setDate(Integer date) {
		this.date = date;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Long getVolume() {
		return volume;
	}
	public void setVolume(Long volume) {
		this.volume = volume;
	}

}
