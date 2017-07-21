package com.oversea.task.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 用于同步用户的类
 *
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.haitao.task.domain
 * @date 15/7/16 17:18
 */
public class SynProduct implements Serializable {

    private static final long serialVersionUID = 5324126336274542256L;

    private String itemId;

    //产品名(人工填写)
    private String manMadeTitle;

    //产品名(人工填写)
    private String manMadeBrandId;

    //产品名
    private String title;

    private String parentCode;

    //--已经无用--
    private String orgCategory;

    //--已经无用--
    private String category;

    //介绍
    private String description;

    //URL
    private String homeUrl;

    //平台
    private String platform;

    //品牌c
    private String brand;

    //标签
    private String tag;

    private String originUrl;

    //货币
    private String unit;

    //商品实例
    private List<SynProductEntity> productEntitys;

    //属性
    private List<String> productAttibutes;

    //属性-属性值 map
    private Map<String, List<String>> productAttibuteEntitys;

    //境内运费
    private Integer homeFreight;

    //爬取ID
    private String spiderId;

    //品牌描述
    private String brandDesc;

    //品牌图片连接
    private String brandImgUrl;

    private String partnerId;

    private Long standardPrice;

    /**
     * 爬取类型
     */
    private int spiderType;

    public Map<String, List<String>> getProductAttibuteEntitys() {
        return productAttibuteEntitys;
    }

    public void setProductAttibuteEntitys(Map<String, List<String>> productAttibuteEntitys) {
        this.productAttibuteEntitys = productAttibuteEntitys;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOrgCategory() {
        return orgCategory;
    }

    public void setOrgCategory(String orgCategory) {
        this.orgCategory = orgCategory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public Integer getHomeFreight() {
        return homeFreight;
    }

    public void setHomeFreight(Integer homeFreight) {
        this.homeFreight = homeFreight;
    }

    public String getManMadeTitle() {
        return manMadeTitle;
    }

    public void setManMadeTitle(String manMadeTitle) {
        this.manMadeTitle = manMadeTitle;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<SynProductEntity> getProductEntitys() {
        return productEntitys;
    }

    public void setProductEntitys(List<SynProductEntity> productEntitys) {
        this.productEntitys = productEntitys;
    }

    public List<String> getProductAttibutes() {
        return productAttibutes;
    }

    public void setProductAttibutes(List<String> productAttibutes) {
        this.productAttibutes = productAttibutes;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getSpiderId() {
        return spiderId;
    }

    public void setSpiderId(String spiderId) {
        this.spiderId = spiderId;
    }

    public String getBrandImgUrl() {
        return brandImgUrl;
    }

    public void setBrandImgUrl(String brandImgUrl) {
        this.brandImgUrl = brandImgUrl;
    }

    public String getBrandDesc() {
        return brandDesc;
    }

    public void setBrandDesc(String brandDesc) {
        this.brandDesc = brandDesc;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public Long getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(Long standardPrice) {
        this.standardPrice = standardPrice;
    }

    public int getSpiderType() {
        return spiderType;
    }

    public void setSpiderType(int spiderType) {
        this.spiderType = spiderType;
    }

    public String getManMadeBrandId() {
        return manMadeBrandId;
    }

    public void setManMadeBrandId(String manMadeBrandId) {
        this.manMadeBrandId = manMadeBrandId;
    }
}
