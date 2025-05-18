package com.finxis.models;

public class FixedRateLeg {

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

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
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

    public String getCalcType() {
        return calcType;
    }

    public void setCalcType(String calcType) {
        this.calcType = calcType;
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
    public String interestRate;
    public String payFreq;
    public String dayCount;
    public String calcType;
    public String marketValue;
    public String dv01;
    public String accrued;
    public String premium;

}
