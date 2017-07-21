package com.oversea.task.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.haitao.task.domain
 * @date 15/7/16 17:43
 */
public class SynProductEntity implements Serializable {

    private static final long serialVersionUID = 5078436307354326952L;

    private String code;

    private String listPrice;

    private String price;

    private List images;

    private String sku;

    private String ean;

    private String upc;

    private String weight;

    private String currencyCode;

    private String storeNumber;

    private Integer limitBuy;

    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getListPrice() {
        return listPrice;
    }

    public void setListPrice(String listPrice) {
        this.listPrice = listPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List getImages() {
        return images;
    }

    public void setImages(List images) {
        this.images = images;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public Integer getLimitBuy() {
        return limitBuy;
    }

    public void setLimitBuy(Integer limitBuy) {
        this.limitBuy = limitBuy;
    }
}
