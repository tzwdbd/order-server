package com.oversea.task.domain;

import java.io.Serializable;
import java.util.Date;
public class MallDefinition implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8417490877373436476L;
	
	private Long id;
	private String name;
	private String logo;
	private String icon;
	private Long countryId;
	private String country;
	private String countryIcon;
	private String website;
	private String commissionRate;
	private String firstWeight;
	private String firstWeightFee;
	private String secondWeight;
	private String secondWeightFee;
	private String leastFee;
	private Date gmtCreate;
	private Date gmtModified;
	private String insurancePercent;
	private String explainFeeUrl;
	private String pcCountryIcon;
	private String description;
	private String expressCycle;
	private String expressType;
	private String expressFee;
	private String mallFee;
	private String mallFeeCondition;
	private String useExpress100;
	private String spiderWebsite;
	private String spiderName;
	private String spiderSwitch;
	private String needIdCard;
	private String priceChangeRate;
	private String priceChangeRemind;
	private String mailRemind;
	private String needInsurance;
	private String isAutoOrder;
	private String isAutoOrderSupportPromoCode;
	private Integer buyToExpressTime;
	private Integer expressToSignTime;
	private Integer expressToFlightTime;
	private Integer clearFinishTime;
	private String status;
	private String operatorStatus;
	private String operVersion;
	private String isAutoExpress;
	private String tags;
	private String isBatchSpider;
	private String isOpSupport;
	private Integer limitNum;
	private String orderStatus;
	private String reduceMallFee;
	private String mallFeeCode;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id=id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo=logo;
	}
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon=icon;
	}
	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId=countryId;
	}
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country=country;
	}
	public String getCountryIcon() {
		return countryIcon;
	}

	public void setCountryIcon(String countryIcon) {
		this.countryIcon=countryIcon;
	}
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website=website;
	}
	public String getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(String commissionRate) {
		this.commissionRate=commissionRate;
	}
	public String getFirstWeight() {
		return firstWeight;
	}

	public void setFirstWeight(String firstWeight) {
		this.firstWeight=firstWeight;
	}
	public String getFirstWeightFee() {
		return firstWeightFee;
	}

	public void setFirstWeightFee(String firstWeightFee) {
		this.firstWeightFee=firstWeightFee;
	}
	public String getSecondWeight() {
		return secondWeight;
	}

	public void setSecondWeight(String secondWeight) {
		this.secondWeight=secondWeight;
	}
	public String getSecondWeightFee() {
		return secondWeightFee;
	}

	public void setSecondWeightFee(String secondWeightFee) {
		this.secondWeightFee=secondWeightFee;
	}
	public String getLeastFee() {
		return leastFee;
	}

	public void setLeastFee(String leastFee) {
		this.leastFee=leastFee;
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
	public String getInsurancePercent() {
		return insurancePercent;
	}

	public void setInsurancePercent(String insurancePercent) {
		this.insurancePercent=insurancePercent;
	}
	public String getExplainFeeUrl() {
		return explainFeeUrl;
	}

	public void setExplainFeeUrl(String explainFeeUrl) {
		this.explainFeeUrl=explainFeeUrl;
	}
	public String getPcCountryIcon() {
		return pcCountryIcon;
	}

	public void setPcCountryIcon(String pcCountryIcon) {
		this.pcCountryIcon=pcCountryIcon;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description=description;
	}
	public String getExpressCycle() {
		return expressCycle;
	}

	public void setExpressCycle(String expressCycle) {
		this.expressCycle=expressCycle;
	}
	public String getExpressType() {
		return expressType;
	}

	public void setExpressType(String expressType) {
		this.expressType=expressType;
	}
	public String getExpressFee() {
		return expressFee;
	}

	public void setExpressFee(String expressFee) {
		this.expressFee=expressFee;
	}
	public String getMallFee() {
		return mallFee;
	}

	public void setMallFee(String mallFee) {
		this.mallFee=mallFee;
	}
	public String getMallFeeCondition() {
		return mallFeeCondition;
	}

	public void setMallFeeCondition(String mallFeeCondition) {
		this.mallFeeCondition=mallFeeCondition;
	}
	public String getUseExpress100() {
		return useExpress100;
	}

	public void setUseExpress100(String useExpress100) {
		this.useExpress100=useExpress100;
	}
	public String getSpiderWebsite() {
		return spiderWebsite;
	}

	public void setSpiderWebsite(String spiderWebsite) {
		this.spiderWebsite=spiderWebsite;
	}
	public String getSpiderName() {
		return spiderName;
	}

	public void setSpiderName(String spiderName) {
		this.spiderName=spiderName;
	}
	public String getSpiderSwitch() {
		return spiderSwitch;
	}

	public void setSpiderSwitch(String spiderSwitch) {
		this.spiderSwitch=spiderSwitch;
	}
	public String getNeedIdCard() {
		return needIdCard;
	}

	public void setNeedIdCard(String needIdCard) {
		this.needIdCard=needIdCard;
	}
	public String getPriceChangeRate() {
		return priceChangeRate;
	}

	public void setPriceChangeRate(String priceChangeRate) {
		this.priceChangeRate=priceChangeRate;
	}
	public String getPriceChangeRemind() {
		return priceChangeRemind;
	}

	public void setPriceChangeRemind(String priceChangeRemind) {
		this.priceChangeRemind=priceChangeRemind;
	}
	public String getMailRemind() {
		return mailRemind;
	}

	public void setMailRemind(String mailRemind) {
		this.mailRemind=mailRemind;
	}
	public String getNeedInsurance() {
		return needInsurance;
	}

	public void setNeedInsurance(String needInsurance) {
		this.needInsurance=needInsurance;
	}
	public String getIsAutoOrder() {
		return isAutoOrder;
	}

	public void setIsAutoOrder(String isAutoOrder) {
		this.isAutoOrder=isAutoOrder;
	}
	public String getIsAutoOrderSupportPromoCode() {
		return isAutoOrderSupportPromoCode;
	}

	public void setIsAutoOrderSupportPromoCode(String isAutoOrderSupportPromoCode) {
		this.isAutoOrderSupportPromoCode=isAutoOrderSupportPromoCode;
	}
	public Integer getBuyToExpressTime() {
		return buyToExpressTime;
	}

	public void setBuyToExpressTime(Integer buyToExpressTime) {
		this.buyToExpressTime=buyToExpressTime;
	}
	public Integer getExpressToSignTime() {
		return expressToSignTime;
	}

	public void setExpressToSignTime(Integer expressToSignTime) {
		this.expressToSignTime=expressToSignTime;
	}
	public Integer getExpressToFlightTime() {
		return expressToFlightTime;
	}

	public void setExpressToFlightTime(Integer expressToFlightTime) {
		this.expressToFlightTime=expressToFlightTime;
	}
	public Integer getClearFinishTime() {
		return clearFinishTime;
	}

	public void setClearFinishTime(Integer clearFinishTime) {
		this.clearFinishTime=clearFinishTime;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status=status;
	}
	public String getOperatorStatus() {
		return operatorStatus;
	}

	public void setOperatorStatus(String operatorStatus) {
		this.operatorStatus=operatorStatus;
	}
	public String getOperVersion() {
		return operVersion;
	}

	public void setOperVersion(String operVersion) {
		this.operVersion=operVersion;
	}
	public String getIsAutoExpress() {
		return isAutoExpress;
	}

	public void setIsAutoExpress(String isAutoExpress) {
		this.isAutoExpress=isAutoExpress;
	}
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags=tags;
	}
	public String getIsBatchSpider() {
		return isBatchSpider;
	}

	public void setIsBatchSpider(String isBatchSpider) {
		this.isBatchSpider=isBatchSpider;
	}
	public String getIsOpSupport() {
		return isOpSupport;
	}

	public void setIsOpSupport(String isOpSupport) {
		this.isOpSupport=isOpSupport;
	}
	public Integer getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(Integer limitNum) {
		this.limitNum=limitNum;
	}
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus=orderStatus;
	}
	public String getReduceMallFee() {
		return reduceMallFee;
	}

	public void setReduceMallFee(String reduceMallFee) {
		this.reduceMallFee=reduceMallFee;
	}
	public String getMallFeeCode() {
		return mallFeeCode;
	}

	public void setMallFeeCode(String mallFeeCode) {
		this.mallFeeCode=mallFeeCode;
	}
}