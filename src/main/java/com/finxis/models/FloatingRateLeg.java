package com.finxis.models;

public class FloatingRateLeg {

    public String getPayReceive() {
        return payReceive;
    }

    public void setPayReceive(String payReceive) {
        this.payReceive = payReceive;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNotional() {
        return notional;
    }

    public void setNotional(String notional) {
        this.notional = notional;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndexFreq() {
        return indexFreq;
    }

    public void setIndexFreq(String indexFreq) {
        this.indexFreq = indexFreq;
    }

    public String getSpread() {
        return spread;
    }

    public void setSpread(String spread) {
        this.spread = spread;
    }

    public String getResetFreq() {
        return resetFreq;
    }

    public void setResetFreq(String resetFreq) {
        this.resetFreq = resetFreq;
    }

    public String getPayFreq() {
        return payFreq;
    }

    public void setPayFreq(String payFreq) {
        this.payFreq = payFreq;
    }

    public String getDayCount() {
        return dayCount;
    }

    public void setDayCount(String dayCount) {
        this.dayCount = dayCount;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }

    public String getDv01() {
        return dv01;
    }

    public void setDv01(String dv01) {
        this.dv01 = dv01;
    }

    public String getAccrued() {
        return accrued;
    }

    public void setAccrued(String accrued) {
        this.accrued = accrued;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String payReceive;
    public String currency;
    public String notional;
    public String effectiveDate;
    public String maturityDate;
    public String index;
    public String indexFreq;
    public String spread;
    public String resetFreq;
    public String payFreq;
    public String dayCount;
    public String marketValue;
    public String dv01;
    public String accrued;
    public String premium;
}
