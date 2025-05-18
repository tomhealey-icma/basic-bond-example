package com.finxis.models;

public class TradeModel {

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    public String getCounterparty1Id() {
        return counterparty1Id;
    }

    public void setCounterparty1Id(String counterparty1Id) {
        this.counterparty1Id = counterparty1Id;
    }

    public String getCounterparty2Id() {
        return counterparty2Id;
    }

    public void setCounterparty2Id(String counterparty2Id) {
        this.counterparty2Id = counterparty2Id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String tradeId;
    public String tradeDate;
    public String settlementDate;
    public String firmId;
    public String counterparty1Id;
    public String counterparty2Id;
    public String direction;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String price;

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public String priceCurrency;

    public String quantity;

}
