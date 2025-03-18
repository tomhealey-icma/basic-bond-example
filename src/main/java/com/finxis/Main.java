package com.finxis;

import cdm.base.datetime.AdjustableDate;
import cdm.base.datetime.AdjustableOrRelativeDate;
import cdm.base.datetime.TimeZone;
import cdm.base.datetime.daycount.DayCountFractionEnum;
import cdm.base.datetime.daycount.functions.DayCountBasis;
import cdm.base.datetime.daycount.metafields.FieldWithMetaDayCountFractionEnum;
import cdm.base.datetime.metafields.FieldWithMetaTimeZone;
import cdm.base.math.UnitType;
import cdm.base.staticdata.asset.common.*;
import cdm.base.staticdata.identifier.Identifier;
import cdm.event.common.Trade;
import cdm.observable.asset.*;
import cdm.observable.asset.metafields.ReferenceWithMetaPriceSchedule;
import cdm.product.asset.FixedRateSpecification;
import cdm.product.asset.InterestRatePayout;
import cdm.product.asset.RateSpecification;
import cdm.product.common.schedule.RateSchedule;
import cdm.product.template.*;
import com.rosetta.model.lib.records.Date;
import com.rosetta.model.metafields.FieldWithMetaDate;
import com.rosetta.model.metafields.FieldWithMetaString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("CDM Bond Demo");

        Main main = new Main();
        BondModel bondModel = main.setBondData();
        Product product = main.createBondProduct(bondModel);
        Trade trade = main.createTrade(product, bondModel);

    }

public BondModel setBondData(){

        BondModel bondModel = new BondModel();

        bondModel.maturityDate = "2026-04-09";
        bondModel.couponRate = "2.375";
        bondModel.country = "D";

        bondModel.tradeDate = "2025-02-27";
        bondModel.tradeTime = "153816";
        bondModel.price = "99.80000000";
        bondModel.nominalQuantity = "500000.00000000";

        return setBondData();

}

public Product createBondProduct(BondModel bondModel){

    DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    ZonedDateTime maturityDate = ZonedDateTime.parse(bondModel.maturityDate, formatter);
    Date terminationDate = Date.of(maturityDate.getYear(), maturityDate.getMonthValue(), maturityDate.getDayOfMonth());

        Product product = Product.builder()
                .setTransferableProduct(TransferableProduct.builder()
                        .setInstrument(Instrument.builder()
                                .setSecurity(Security.builder()
                                        .setDebtType(DebtType.builder()
                                                .setDebtClass(DebtClassEnum.VANILLA))
                                        .setIdentifier(List.of(AssetIdentifier.builder()
                                                        .setIdentifierType(AssetIdTypeEnum.ISIN)
                                                .setIdentifierValue(bondModel.getIsin())))))
                        .setEconomicTerms(EconomicTerms.builder()
                                .setTerminationDate(AdjustableOrRelativeDate.builder()
                                        .setAdjustableDate(AdjustableDate.builder()
                                                .setAdjustedDate(FieldWithMetaDate.builder()
                                                        .setValue(terminationDate))))
                                .setPayout(List.of(Payout.builder()
                                        .setInterestRatePayout(InterestRatePayout.builder()
                                                .setDayCountFraction(FieldWithMetaDayCountFractionEnum.builder()
                                                        .setValue(DayCountFractionEnum.ACT_365_FIXED))
                                                .setRateSpecification(RateSpecification.builder()
                                                        .setFixedRateSpecification(FixedRateSpecification.builder()
                                                                .setRateSchedule(RateSchedule.builder()
                                                                        .setPriceValue(PriceSchedule.builder()
                                                                                .setPriceExpression(PriceExpressionEnum.PERCENTAGE_OF_NOTIONAL))
                                                                        .setPrice(ReferenceWithMetaPriceSchedule.builder()
                                                                                .setValue(PriceSchedule.builder()
                                                                                        .setValue(BigDecimal.valueOf(Long.parseLong(bondModel.couponRate)))))))))))))

                .build();

        return product;

}

public Trade createTrade(Product bond, BondModel bondModel){

    DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    ZonedDateTime tradeDate = ZonedDateTime.parse(bondModel.tradeDate, formatter);
    Date tradeDateStr= Date.of(tradeDate.getYear(), tradeDate.getMonthValue(), tradeDate.getDayOfMonth());

        Trade trade = Trade.builder()
                .setTradeDate(FieldWithMetaDate.builder()
                        .setValue(tradeDateStr))
                .setTradeTime(FieldWithMetaTimeZone.builder()
                        .setValue(TimeZone.builder()
                                .setLocation(FieldWithMetaString.builder()
                                        .setValue("UTC"))))
                .setTradeLot(TradeLot.builder()
                        .setPriceQuantity(List.of(PriceQuantity.builder()
                                .setPrice
                                        .setValue(BigDecimal.valueOf(Long.parseLong(bondModel.price)))))))
                        .build();


        return trade;
}


}
