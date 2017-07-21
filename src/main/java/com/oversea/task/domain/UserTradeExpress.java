package com.oversea.task.domain;

import java.io.Serializable;
import java.util.Date;

public class UserTradeExpress implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4345756129730543704L;

	private Long id;

    private String orderNo;

    private String expressNo;

    private Integer companyType;

    private String overseaName;

    private String overseaTrackNo;

    private String name;

    private String trackNo;

    private Integer status;

    private String weight;

    private String version;

    private Date gmtCreate;

    private Date gmtModified;

    private String productIdList;

    private String remark;

    private String length;

    private String width;

    private String height;

    private String segmentCode;

    private String dutysImgUrl;

    private String realTax;

    private Integer externalStatus;

    private String externalStatusDesc;

    private String logisticsCost;

    private String packageCost;

    private String operatingCost;

    private String insuranceCost;

    private String otherCost;

    private String airTakeOff;

    private String airlines;

    private String flight;

    private String clientIdentifier;

    private Integer orderStatus;

    private String firstName;

    private Date checkinTime;

    private Integer sendStatus;

    private Byte expressType;

    private String operator;

    private String changeTrackNoList;

    private Integer changeSendStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo == null ? null : expressNo.trim();
    }

    public Integer getCompanyType() {
        return companyType;
    }

    public void setCompanyType(Integer companyType) {
        this.companyType = companyType;
    }

    public String getOverseaName() {
        return overseaName;
    }

    public void setOverseaName(String overseaName) {
        this.overseaName = overseaName == null ? null : overseaName.trim();
    }

    public String getOverseaTrackNo() {
        return overseaTrackNo;
    }

    public void setOverseaTrackNo(String overseaTrackNo) {
        this.overseaTrackNo = overseaTrackNo == null ? null : overseaTrackNo.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(String trackNo) {
        this.trackNo = trackNo == null ? null : trackNo.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight == null ? null : weight.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getProductIdList() {
        return productIdList;
    }

    public void setProductIdList(String productIdList) {
        this.productIdList = productIdList == null ? null : productIdList.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length == null ? null : length.trim();
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width == null ? null : width.trim();
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height == null ? null : height.trim();
    }

    public String getSegmentCode() {
        return segmentCode;
    }

    public void setSegmentCode(String segmentCode) {
        this.segmentCode = segmentCode == null ? null : segmentCode.trim();
    }

    public String getDutysImgUrl() {
        return dutysImgUrl;
    }

    public void setDutysImgUrl(String dutysImgUrl) {
        this.dutysImgUrl = dutysImgUrl == null ? null : dutysImgUrl.trim();
    }

    public String getRealTax() {
        return realTax;
    }

    public void setRealTax(String realTax) {
        this.realTax = realTax == null ? null : realTax.trim();
    }

    public Integer getExternalStatus() {
        return externalStatus;
    }

    public void setExternalStatus(Integer externalStatus) {
        this.externalStatus = externalStatus;
    }

    public String getExternalStatusDesc() {
        return externalStatusDesc;
    }

    public void setExternalStatusDesc(String externalStatusDesc) {
        this.externalStatusDesc = externalStatusDesc == null ? null : externalStatusDesc.trim();
    }

    public String getLogisticsCost() {
        return logisticsCost;
    }

    public void setLogisticsCost(String logisticsCost) {
        this.logisticsCost = logisticsCost == null ? null : logisticsCost.trim();
    }

    public String getPackageCost() {
        return packageCost;
    }

    public void setPackageCost(String packageCost) {
        this.packageCost = packageCost == null ? null : packageCost.trim();
    }

    public String getOperatingCost() {
        return operatingCost;
    }

    public void setOperatingCost(String operatingCost) {
        this.operatingCost = operatingCost == null ? null : operatingCost.trim();
    }

    public String getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(String insuranceCost) {
        this.insuranceCost = insuranceCost == null ? null : insuranceCost.trim();
    }

    public String getOtherCost() {
        return otherCost;
    }

    public void setOtherCost(String otherCost) {
        this.otherCost = otherCost == null ? null : otherCost.trim();
    }

    public String getAirTakeOff() {
        return airTakeOff;
    }

    public void setAirTakeOff(String airTakeOff) {
        this.airTakeOff = airTakeOff == null ? null : airTakeOff.trim();
    }

    public String getAirlines() {
        return airlines;
    }

    public void setAirlines(String airlines) {
        this.airlines = airlines == null ? null : airlines.trim();
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight == null ? null : flight.trim();
    }

    public String getClientIdentifier() {
        return clientIdentifier;
    }

    public void setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = clientIdentifier == null ? null : clientIdentifier.trim();
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName == null ? null : firstName.trim();
    }

    public Date getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(Date checkinTime) {
        this.checkinTime = checkinTime;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Byte getExpressType() {
        return expressType;
    }

    public void setExpressType(Byte expressType) {
        this.expressType = expressType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public String getChangeTrackNoList() {
        return changeTrackNoList;
    }

    public void setChangeTrackNoList(String changeTrackNoList) {
        this.changeTrackNoList = changeTrackNoList == null ? null : changeTrackNoList.trim();
    }

    public Integer getChangeSendStatus() {
        return changeSendStatus;
    }

    public void setChangeSendStatus(Integer changeSendStatus) {
        this.changeSendStatus = changeSendStatus;
    }

	@Override
	public String toString() {
		return "UserTradeExpress [id=" + id + ", orderNo=" + orderNo
				+ ", expressNo=" + expressNo + ", companyType=" + companyType
				+ ", overseaName=" + overseaName + ", overseaTrackNo="
				+ overseaTrackNo + ", name=" + name + ", trackNo=" + trackNo
				+ ", status=" + status + ", weight=" + weight + ", version="
				+ version + ", gmtCreate=" + gmtCreate + ", gmtModified="
				+ gmtModified + ", productIdList=" + productIdList
				+ ", remark=" + remark + ", length=" + length + ", width="
				+ width + ", height=" + height + ", segmentCode=" + segmentCode
				+ ", dutysImgUrl=" + dutysImgUrl + ", realTax=" + realTax
				+ ", externalStatus=" + externalStatus
				+ ", externalStatusDesc=" + externalStatusDesc
				+ ", logisticsCost=" + logisticsCost + ", packageCost="
				+ packageCost + ", operatingCost=" + operatingCost
				+ ", insuranceCost=" + insuranceCost + ", otherCost="
				+ otherCost + ", airTakeOff=" + airTakeOff + ", airlines="
				+ airlines + ", flight=" + flight + ", clientIdentifier="
				+ clientIdentifier + ", orderStatus=" + orderStatus
				+ ", firstName=" + firstName + ", checkinTime=" + checkinTime
				+ ", sendStatus=" + sendStatus + ", expressType=" + expressType
				+ ", operator=" + operator + ", changeTrackNoList="
				+ changeTrackNoList + ", changeSendStatus=" + changeSendStatus
				+ "]";
	}
}