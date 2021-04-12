package com.moneydance.modules.features.securityquoteload;

public class ExtraFields {
	private Long volume;
	private Double high;
	private Double low;
	public ExtraFields (Long volume, Double high, Double low) {
		this.volume = volume;
		this.high= high;
		this.low = low;
	}
	public Long getVolume() {
		return volume;
	}
	public Double getHigh() {
		return high;
	}
	public Double getLow() {
		return low;
	}
	
}
