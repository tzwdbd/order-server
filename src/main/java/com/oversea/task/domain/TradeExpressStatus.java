package com.oversea.task.domain;

import java.util.Date;

public class TradeExpressStatus {

	private Long id;
	private String orderNo;
	private String expressNo;
	private Integer status;
	private String name;
	private String remark;
	private Date gmtCreate;
	private Date gmtModified;
	private Date occurTime;
	private String operator;
	//分表键值
	private String shardKey;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id=id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status=status;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate=gmtCreate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified=gmtModified;
	}

	public Date getOccurTime() {
		return occurTime;
	}

	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getShardKey() {
		return shardKey;
	}

	public void setShardKey(String shardKey) {
		this.shardKey = shardKey;
	}
}
