package com.oversea.task.domain;

import java.io.Serializable;

public class ExchangeDefinition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6923450190228371586L;
	
	private long id;
	private String units;
	private String description;
	private Integer rmb;
	private Integer source;
	
	public Integer getSource() {
		return source;
	}
	public void setSource(Integer source) {
		this.source = source;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getRmb() {
		return rmb;
	}
	public void setRmb(Integer rmb) {
		this.rmb = rmb;
	}

}
