package com.moneydance.modules.features.securityquoteload;

public class HistoryPrice {
	private Integer date;
	private Double price;
	private Double highPrice;
	private Double lowPrice;
	private Long volume;
	
	public HistoryPrice(Integer date, Double price,Double high, Double low, Long volume) {
		super();
		this.date = date;
		this.price = price;
		this.highPrice = high;
		this.lowPrice = low;
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
	public Double getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}
	public Double getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}
	public Long getVolume() {
		return volume;
	}
	public void setVolume(Long volume) {
		this.volume = volume;
	}

}
