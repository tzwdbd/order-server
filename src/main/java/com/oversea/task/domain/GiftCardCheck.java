package com.oversea.task.domain;

import java.io.Serializable;
import java.util.Date;
public class GiftCardCheck  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5499320338947498950L;
	private Long id;
	private String cardType;
	private String siteName;
	private String currentBalance;
	private String recharge;
	private String refund;
	private String rmb;
	private Long accountId;
	private String payAccount;
	private Date gmtCreate;
	private Date date;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id=id;
	}
	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType=cardType;
	}
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName=siteName;
	}
	public String getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(String currentBalance) {
		this.currentBalance=currentBalance;
	}
	public String getRecharge() {
		return recharge;
	}

	public void setRecharge(String recharge) {
		this.recharge=recharge;
	}
	public String getRefund() {
		return refund;
	}

	public void setRefund(String refund) {
		this.refund=refund;
	}
	public String getRmb() {
		return rmb;
	}

	public void setRmb(String rmb) {
		this.rmb=rmb;
	}
	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId=accountId;
	}
	public String getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(String payAccount) {
		this.payAccount=payAccount;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate=gmtCreate;
	}
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date=date;
	}
}