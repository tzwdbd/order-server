package com.oversea.task.domain;

import java.io.Serializable;
import java.util.Date;
public class ThirdPayDetail  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -272807432025996039L;
	private Long id;
	private String orderNo;
	private String siteName;
	private Long productId;
	private Long productEntityId;
	private Integer num;
	private String skuPrice;
	private String solePrice;
	private String totalPrice;
	private String rmbPrice;
	private String units;
	private Integer payStatus;
	private Long accountId;
	private Date gmtCreate;
	private Date modifyTime;
	private Date orderTime;
	private Integer company;
	private String isDirect;
	private String operatorCompany;
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
		this.orderNo=orderNo;
	}
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName=siteName;
	}
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId=productId;
	}
	public Long getProductEntityId() {
		return productEntityId;
	}

	public void setProductEntityId(Long productEntityId) {
		this.productEntityId=productEntityId;
	}
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num=num;
	}
	public String getSkuPrice() {
		return skuPrice;
	}

	public void setSkuPrice(String skuPrice) {
		this.skuPrice=skuPrice;
	}
	public String getSolePrice() {
		return solePrice;
	}

	public void setSolePrice(String solePrice) {
		this.solePrice=solePrice;
	}
	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice=totalPrice;
	}
	public String getRmbPrice() {
		return rmbPrice;
	}

	public void setRmbPrice(String rmbPrice) {
		this.rmbPrice=rmbPrice;
	}
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units=units;
	}
	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus=payStatus;
	}
	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId=accountId;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate=gmtCreate;
	}
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime=modifyTime;
	}
	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime=orderTime;
	}
	public Integer getCompany() {
		return company;
	}

	public void setCompany(Integer company) {
		this.company=company;
	}
	public String getIsDirect() {
		return isDirect;
	}

	public void setIsDirect(String isDirect) {
		this.isDirect=isDirect;
	}
	public String getOperatorCompany() {
		return operatorCompany;
	}

	public void setOperatorCompany(String operatorCompany) {
		this.operatorCompany=operatorCompany;
	}
}