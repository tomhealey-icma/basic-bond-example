package com.finxis.models;

import java.util.List;

public class IrsOtcModel {

    public List<FixedRateLeg> getFixedRateLegList() {
        return fixedRateLegList;
    }

    public void setFixedRateLegList(List<FixedRateLeg> fixedRateLegList) {
        this.fixedRateLegList = fixedRateLegList;
    }

    public List<FloatingRateLeg> getFloatingRateLegList() {
        return floatingRateLegList;
    }

    public void setFloatingRateLegList(List<FloatingRateLeg> floatingRateLegList) {
        this.floatingRateLegList = floatingRateLegList;
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

    public List<FixedRateLeg> fixedRateLegList;
    public List<FloatingRateLeg> floatingRateLegList;
    public String marketValue;
    public String dv01;
    public String accrued;
    public String premium;
}


