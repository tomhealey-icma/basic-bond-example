package com.finxis.models;

public class FXOptionModel {

    public String underLyingAsset;
    public String effectiveDate;

    public String getUnderLyingAsset() {
        return underLyingAsset;
    }

    public void setUnderLyingAsset(String underLyingAsset) {
        this.underLyingAsset = underLyingAsset;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCallPut() {
        return callPut;
    }

    public void setCallPut(String callPut) {
        this.callPut = callPut;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStrike() {
        return strike;
    }

    public void setStrike(String strike) {
        this.strike = strike;
    }

    public String getMoneyPercentage() {
        return moneyPercentage;
    }

    public void setMoneyPercentage(String moneyPercentage) {
        this.moneyPercentage = moneyPercentage;
    }

    public String getNotionalCurrency() {
        return notionalCurrency;
    }

    public void setNotionalCurrency(String notionalCurrency) {
        this.notionalCurrency = notionalCurrency;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getVolitility() {
        return volitility;
    }

    public void setVolitility(String volitility) {
        this.volitility = volitility;
    }

    public String style;
    public String callPut;
    public String expirationDate;
    public String deliveryDate;
    public String strike;
    public String moneyPercentage;
    public String notionalCurrency;
    public String modelType;
    public String volitility;

}
