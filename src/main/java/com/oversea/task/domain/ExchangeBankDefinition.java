package com.oversea.task.domain;

import java.io.Serializable;
import java.util.Date;
public class ExchangeBankDefinition implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7146495744821486267L;
	private Long id;
	private String units;
	private String bankName;
	private String description;
	private Integer source;
	private Integer rmb;
	private Date gmtCreate;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id=id;
	}
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units=units;
	}
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName=bankName;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description=description;
	}
	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source=source;
	}
	public Integer getRmb() {
		return rmb;
	}

	public void setRmb(Integer rmb) {
		this.rmb=rmb;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate=gmtCreate;
	}
}