package com.finxis;

import cdm.base.datetime.AdjustableDate;
import cdm.base.datetime.AdjustableOrAdjustedOrRelativeDate;
import cdm.base.datetime.AdjustableOrRelativeDate;
import cdm.base.datetime.TimeZone;
import cdm.base.datetime.daycount.DayCountFractionEnum;
import cdm.base.datetime.daycount.functions.DayCountBasis;
import cdm.base.datetime.daycount.metafields.FieldWithMetaDayCountFractionEnum;
import cdm.base.datetime.metafields.FieldWithMetaTimeZone;
import cdm.base.math.ArithmeticOperationEnum;
import cdm.base.math.FinancialUnitEnum;
import cdm.base.math.NonNegativeQuantitySchedule;
import cdm.base.math.UnitType;
import cdm.base.math.metafields.FieldWithMetaNonNegativeQuantitySchedule;
import cdm.base.staticdata.asset.common.*;
import cdm.base.staticdata.identifier.AssignedIdentifier;
import cdm.base.staticdata.identifier.Identifier;
import cdm.base.staticdata.identifier.TradeIdentifierTypeEnum;
import cdm.base.staticdata.party.*;
import cdm.base.staticdata.party.metafields.ReferenceWithMetaParty;
import cdm.event.common.ExecutionDetails;
import cdm.event.common.Trade;
import cdm.event.common.TradeIdentifier;
import cdm.observable.asset.*;
import cdm.observable.asset.metafields.FieldWithMetaObservable;
import cdm.observable.asset.metafields.FieldWithMetaPriceSchedule;
import cdm.observable.asset.metafields.ReferenceWithMetaPriceSchedule;
import cdm.product.asset.FixedRateSpecification;
import cdm.product.asset.InterestRatePayout;
import cdm.product.asset.RateSpecification;
import cdm.product.common.schedule.RateSchedule;
import cdm.product.common.settlement.SettlementDate;
import cdm.product.common.settlement.SettlementTerms;
import cdm.product.template.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
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
    public static void main(String[] args) throws JsonProcessingException {
        System.out.println("CDM Bond Demo");

        Main main = new Main();
        BondModel bondModel = main.setBondData();
        Product product = main.createBondProduct(bondModel);
        Trade trade = main.createTrade(product, bondModel);

        String tradeJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(trade);
        System.out.println(tradeJson);

        ObjectMapper rosettaObjectMapper = RosettaObjectMapper.getNewRosettaObjectMapper();
        Trade tradeObj = new Trade.TradeBuilderImpl();
        Trade newTrade = rosettaObjectMapper.readValue(tradeJson, tradeObj.getClass());

        BondFutureModel bondFutureModel = main.setBondFutureData();
        product = main.createBondFutureProduct(bondFutureModel);
        trade = main.createFutureTrade(product, bondFutureModel);

        tradeJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(trade);
        System.out.println(tradeJson);

        rosettaObjectMapper = RosettaObjectMapper.getNewRosettaObjectMapper();
        tradeObj = new Trade.TradeBuilderImpl();
        newTrade = rosettaObjectMapper.readValue(tradeJson, tradeObj.getClass());

    }

public BondModel setBondData(){

        BondModel bondModel = new BondModel();

        bondModel.maturityDate = "2026-04-09T00:00:00.000+00:00";
        bondModel.couponRate = "2.375";
        bondModel.country = "D";

        bondModel.tradeDate = "2025-02-27T15:38:16.000+00:00";
        bondModel.tradeTime = "153816";
        bondModel.price = "99.80000000";
        bondModel.nominalQuantity = "500000.00000000";

        bondModel.commission = "20.45";
        bondModel.settlementDate = "2025-03-03T00:00:00.000+00:00";
        bondModel.marketid = "BCF";
        bondModel.plateform = "Bloomberg";
        bondModel.instrumentName = "25VG - LLOYDS 2.375% 09 Apr 2026";
        bondModel.currency = "EUR";
        bondModel.isin = "XS2151069775";


        return bondModel;

}

public BondFutureModel setBondFutureData(){

        BondFutureModel bondFutureModel = new BondFutureModel();
        bondFutureModel.instrumentName = "TN MAR5 Future";
        bondFutureModel.marketid = "CME";
        bondFutureModel.side = "Buy";
        bondFutureModel.tradeDate = "2025-02-07T10:00:00.000+00:00";
        bondFutureModel.underlyingIsin = "DE000F1C2NG8";
        bondFutureModel.price = "158.10000000";
        bondFutureModel.currency = "EUR";
        bondFutureModel.traderName = "CDMCXTRD018";
        bondFutureModel.nominalQuantity = "12.00000000";
        bondFutureModel.maturityDate = "2025-06-08T00:00:00.000+00:00";

        return bondFutureModel;

}

public Product createBondProduct(BondModel bondModel){

    DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");
    ZonedDateTime maturityDate = ZonedDateTime.parse(bondModel.maturityDate, formatter);
    Date terminationDate = Date.of(maturityDate.getYear(), maturityDate.getMonthValue(), maturityDate.getDayOfMonth());

    ZonedDateTime zonedSettlementDate = ZonedDateTime.parse(bondModel.settlementDate, formatter);
    Date settlementDate = Date.of(zonedSettlementDate.getYear(), zonedSettlementDate.getMonthValue(), zonedSettlementDate.getDayOfMonth());

        Product product = Product.builder()
                .setTransferableProduct(TransferableProduct.builder()
                        .setInstrument(Instrument.builder()
                                .setSecurity(Security.builder()
                                        .setDebtType(DebtType.builder()
                                                .setDebtClass(DebtClassEnum.VANILLA))
                                        .setIdentifier(List.of(AssetIdentifier.builder()
                                                .setIdentifierType(AssetIdTypeEnum.ISIN)
                                                .setIdentifierValue(bondModel.getIsin())))
                                                .addIdentifier(AssetIdentifier.builder()
                                                    .setIdentifier(FieldWithMetaString.builder()
                                                        .setValue(bondModel.instrumentName))
                                                        .setIdentifierType(AssetIdTypeEnum.NAME))
                                                .addIdentifier(AssetIdentifier.builder()
                                                        .setIdentifier(FieldWithMetaString.builder()
                                                                .setValue("LLOYDS"))
                                                        .setIdentifierType(AssetIdTypeEnum.NAME))))
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
                                                                                .setPriceExpression(PriceExpressionEnum.PAR_VALUE_FRACTION))
                                                                        .setPrice(ReferenceWithMetaPriceSchedule.builder()
                                                                                .setValue(PriceSchedule.builder()
                                                                                        .setValue(BigDecimal.valueOf(Double.parseDouble(bondModel.couponRate)))))))))))
                                .addPayout(Payout.builder()
                                        .setSettlementPayout(SettlementPayout.builder()
                                                .setPayerReceiver(PayerReceiver.builder()
                                                        .setPayer(CounterpartyRoleEnum.PARTY_1)
                                                        .setReceiver(CounterpartyRoleEnum.PARTY_2))
                                                .setSettlementTerms(SettlementTerms.builder()
                                                        .setSettlementDate(SettlementDate.builder()
                                                                .setAdjustableOrRelativeDate(AdjustableOrAdjustedOrRelativeDate.builder()
                                                                                .setAdjustedDate(FieldWithMetaDate.builder()
                                                                                        .setValue(settlementDate)))))))))

                .build();

        return product;

}

public Trade createTrade(Product bond, BondModel bondModel){

    DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");
    ZonedDateTime tradeDate = ZonedDateTime.parse(bondModel.tradeDate, formatter);
    Date tradeDateStr= Date.of(tradeDate.getYear(), tradeDate.getMonthValue(), tradeDate.getDayOfMonth());

    Party buyerParty = Party.builder()
                    .setPartyId(List.of(PartyIdentifier.builder()
                            .setIdentifierValue("ClientId"))).build();


    Party sellerParty = Party.builder()
                    .setPartyId(List.of(PartyIdentifier.builder()
                            .setIdentifierValue("DealerId"))).build();

    Party traderParty = Party.builder()
                    .setPartyId(List.of(PartyIdentifier.builder()
                            .setIdentifierValue("Trader"))).build();

    List<Party> partyList = List.of(buyerParty, sellerParty, traderParty);

    PartyRole buyer = PartyRole.builder()
            .setRole(PartyRoleEnum.BUYER)
            .setPartyReference(ReferenceWithMetaParty.builder()
                    .setValue(buyerParty))
            .build();

    PartyRole seller = PartyRole.builder()
            .setRole(PartyRoleEnum.SELLER)
            .setPartyReference(ReferenceWithMetaParty.builder()
                    .setValue(sellerParty))
                    .build();

    PartyRole trader = PartyRole.builder()
            .setRole(PartyRoleEnum.BOOKING_PARTY)
            .setPartyReference(ReferenceWithMetaParty.builder()
                    .setValue(traderParty))
            .build();

    List<PartyRole> partyRoleList = List.of(buyer, seller, trader);


        Trade trade = Trade.builder()
                .setTradeDate(FieldWithMetaDate.builder()
                        .setValue(tradeDateStr))
                .setTradeTime(FieldWithMetaTimeZone.builder()
                        .setValue(TimeZone.builder()
                                .setLocation(FieldWithMetaString.builder()
                                        .setValue("UTC"))))
                .setTradeIdentifier(List.of(TradeIdentifier.builder()
                        .setIdentifierType(TradeIdentifierTypeEnum.UNIQUE_TRANSACTION_IDENTIFIER)
                                .setAssignedIdentifier(List.of(AssignedIdentifier.builder()
                                                .setIdentifierValue("UTI123")))))
                .setTradeLot(List.of(TradeLot.builder()
                        .setPriceQuantity(List.of(PriceQuantity.builder()
                                .setPrice(List.of(FieldWithMetaPriceSchedule.builder()
                                        .setValue(PriceSchedule.builder()
                                                .setValue(BigDecimal.valueOf(Double.parseDouble(bondModel.price)))
                                                .setUnit(UnitType.builder()
                                                        .setCurrencyValue(bondModel.currency))
                                                .setPerUnitOf(UnitType.builder()
                                                        .setCurrencyValue(bondModel.currency))
                                                .setPriceType(PriceTypeEnum.ASSET_PRICE)
                                                .setPriceExpression(PriceExpressionEnum.PAR_VALUE_FRACTION)
                                                .setComposite(PriceComposite.builder()
                                                        .setBaseValue(BigDecimal.valueOf(Double.parseDouble(bondModel.price)))
                                                        .setOperand(BigDecimal.valueOf(Double.parseDouble(".0213")))
                                                        .setArithmeticOperator(ArithmeticOperationEnum.ADD)
                                                        .setOperandType(PriceOperandEnum.ACCRUED_INTEREST)))))
                                        .setQuantity(List.of(FieldWithMetaNonNegativeQuantitySchedule.builder()
                                                        .setValue(NonNegativeQuantitySchedule.builder()
                                                                .setValue(BigDecimal.valueOf(Double.parseDouble(bondModel.nominalQuantity)))
                                                                .setUnit(UnitType.builder()
                                                                        .setCurrencyValue(bondModel.currency)))))
                                        .setObservable(FieldWithMetaObservable.builder()
                                                .setValue(Observable.builder()
                                                        .setAsset(Asset.builder()
                                                                .setInstrument(Instrument.builder()
                                                                        .setSecurity(Security.builder()
                                                                                .setIdentifier(List.of(AssetIdentifier.builder()
                                                                                        .setIdentifierType(AssetIdTypeEnum.ISIN)
                                                                                        .setIdentifierValue(bondModel.getIsin())))
                                                                                        .addIdentifier(AssetIdentifier.builder()
                                                                                                .setIdentifier(FieldWithMetaString.builder()
                                                                                                        .setValue(bondModel.instrumentName))
                                                                                                .setIdentifierType(AssetIdTypeEnum.NAME)))))))))
                        .addPriceQuantity(PriceQuantity.builder()
                                .setPrice(List.of(FieldWithMetaPriceSchedule.builder()
                                        .setValue(PriceSchedule.builder()
                                                .setCashPrice(CashPrice.builder()
                                                        .setCashPriceType(CashPriceTypeEnum.FEE))
                                                .setValue(BigDecimal.valueOf(Double.parseDouble(bondModel.commission)))))))))

                .setCounterparty(List.of(Counterparty.builder()
                                .setPartyReference(ReferenceWithMetaParty.builder()
                                        .setExternalReference("Client"))
                                .setRole(CounterpartyRoleEnum.PARTY_1)))
                .addCounterparty(Counterparty.builder()
                        .setPartyReference(ReferenceWithMetaParty.builder()
                                .setExternalReference("Dealer"))
                        .setRole(CounterpartyRoleEnum.PARTY_2))
                .setParty(partyList)
                .setPartyRole(partyRoleList)
                .setExecutionDetails(ExecutionDetails.builder()
                        .setExecutionVenue(LegalEntity.builder()
                                .setEntityId(List.of(FieldWithMetaString.builder()
                                                .setValue(bondModel.marketid)))
                                .addEntityId(FieldWithMetaString.builder()
                                                .setValue(bondModel.plateform))))
                        .build();


        return trade;
}


    public Product createBondFutureProduct(BondFutureModel bondFutureModel){

        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        ZonedDateTime maturityDate = ZonedDateTime.parse(bondFutureModel.maturityDate, formatter);
        Date terminationDate = Date.of(maturityDate.getYear(), maturityDate.getMonthValue(), maturityDate.getDayOfMonth());

        ZonedDateTime zonedSettlementDate = ZonedDateTime.parse(bondFutureModel.tradeDate, formatter);
        Date settlementDate = Date.of(zonedSettlementDate.getYear(), zonedSettlementDate.getMonthValue(), zonedSettlementDate.getDayOfMonth());

        Product product = Product.builder()
                .setTransferableProduct(TransferableProduct.builder()
                        .setInstrument(Instrument.builder()
                                .setListedDerivative(ListedDerivative.builder()
                                        .setDeliveryTerm("H")
                                        .setInstrumentType(InstrumentTypeEnum.LISTED_DERIVATIVE)
                                        .setIdentifier(List.of(AssetIdentifier.builder()
                                                .setIdentifierType(AssetIdTypeEnum.EXCHANGE_CODE)
                                                .setIdentifierValue(bondFutureModel.instrumentName)))))
                        .setEconomicTerms(EconomicTerms.builder()
                                .setTerminationDate(AdjustableOrRelativeDate.builder()
                                        .setAdjustableDate(AdjustableDate.builder()
                                                .setAdjustedDate(FieldWithMetaDate.builder()
                                                        .setValue(terminationDate))))
                                .setPayout(List.of(Payout.builder()
                                                .setAssetPayout(AssetPayout.builder()
                                                        .setUnderlier(Asset.builder()
                                                                .setInstrument(Instrument.builder()
                                                                        .setSecurity(Security.builder()
                                                                                .setDebtType(DebtType.builder()
                                                                                        .setDebtClass(DebtClassEnum.VANILLA)
                                                                                        .build())
                                                                                .setIdentifier(List.of(AssetIdentifier.builder()
                                                                                        .setIdentifierType(AssetIdTypeEnum.ISIN)
                                                                                        .setIdentifierValue(bondFutureModel.underlyingIsin)))))))))

                                .addPayout(Payout.builder()
                                        .setSettlementPayout(SettlementPayout.builder()
                                                .setPayerReceiver(PayerReceiver.builder()
                                                        .setPayer(CounterpartyRoleEnum.PARTY_1)
                                                        .setReceiver(CounterpartyRoleEnum.PARTY_2))
                                                .setSettlementTerms(SettlementTerms.builder()
                                                        .setSettlementDate(SettlementDate.builder()
                                                                .setAdjustableOrRelativeDate(AdjustableOrAdjustedOrRelativeDate.builder()
                                                                        .setAdjustedDate(FieldWithMetaDate.builder()
                                                                                .setValue(settlementDate)))))))))

                .build();

        return product;

    }

    public Trade createFutureTrade(Product bond, BondFutureModel bondFutureModel){

        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        ZonedDateTime tradeDate = ZonedDateTime.parse(bondFutureModel.tradeDate, formatter);
        Date tradeDateStr= Date.of(tradeDate.getYear(), tradeDate.getMonthValue(), tradeDate.getDayOfMonth());

        Party buyerParty = Party.builder()
                .setPartyId(List.of(PartyIdentifier.builder()
                        .setIdentifierValue("ClientId"))).build();


        Party sellerParty = Party.builder()
                .setPartyId(List.of(PartyIdentifier.builder()
                        .setIdentifierValue("DealerId"))).build();

        Party traderParty = Party.builder()
                .setPartyId(List.of(PartyIdentifier.builder()
                        .setIdentifierValue("Trader"))).build();

        List<Party> partyList = List.of(buyerParty, sellerParty, traderParty);

        PartyRole buyer = PartyRole.builder()
                .setRole(PartyRoleEnum.BUYER)
                .setPartyReference(ReferenceWithMetaParty.builder()
                        .setValue(buyerParty))
                .build();

        PartyRole seller = PartyRole.builder()
                .setRole(PartyRoleEnum.SELLER)
                .setPartyReference(ReferenceWithMetaParty.builder()
                        .setValue(sellerParty))
                .build();

        PartyRole trader = PartyRole.builder()
                .setRole(PartyRoleEnum.BOOKING_PARTY)
                .setPartyReference(ReferenceWithMetaParty.builder()
                        .setValue(traderParty))
                .build();

        List<PartyRole> partyRoleList = List.of(buyer, seller, trader);


        Trade trade = Trade.builder()
                .setTradeDate(FieldWithMetaDate.builder()
                        .setValue(tradeDateStr))
                .setTradeTime(FieldWithMetaTimeZone.builder()
                        .setValue(TimeZone.builder()
                                .setLocation(FieldWithMetaString.builder()
                                        .setValue("UTC"))))
                .setTradeIdentifier(List.of(TradeIdentifier.builder()
                        .setIdentifierType(TradeIdentifierTypeEnum.UNIQUE_TRANSACTION_IDENTIFIER)
                        .setAssignedIdentifier(List.of(AssignedIdentifier.builder()
                                .setIdentifierValue("UTI123")))))
                .setTradeLot(List.of(TradeLot.builder()
                        .setPriceQuantity(List.of(PriceQuantity.builder()
                                .setPrice(List.of(FieldWithMetaPriceSchedule.builder()
                                        .setValue(PriceSchedule.builder()
                                                .setValue(BigDecimal.valueOf(Double.parseDouble(bondFutureModel.price)))
                                                .setUnit(UnitType.builder()
                                                        .setFinancialUnit(FinancialUnitEnum.CONTRACT)
                                                        .setCurrencyValue(bondFutureModel.currency))
                                                .setPerUnitOf(UnitType.builder()
                                                        .setFinancialUnit(FinancialUnitEnum.CONTRACT)
                                                        .setCurrencyValue(bondFutureModel.currency))
                                                .setPriceType(PriceTypeEnum.ASSET_PRICE))))
                                .setQuantity(List.of(FieldWithMetaNonNegativeQuantitySchedule.builder()
                                        .setValue(NonNegativeQuantitySchedule.builder()
                                                .setValue(BigDecimal.valueOf(Double.parseDouble(bondFutureModel.nominalQuantity)))
                                                .setUnit(UnitType.builder()
                                                        .setCurrencyValue(bondFutureModel.currency)))))
                                .setObservable(FieldWithMetaObservable.builder()
                                        .setValue(Observable.builder()
                                                .setAsset(Asset.builder()
                                                        .setInstrument(Instrument.builder()
                                                                .setSecurity(Security.builder()
                                                                        .setIdentifier(List.of(AssetIdentifier.builder()
                                                                                .setIdentifierType(AssetIdTypeEnum.ISIN)
                                                                                .setIdentifierValue(bondFutureModel.underlyingIsin))))))))))))

                .setCounterparty(List.of(Counterparty.builder()
                        .setPartyReference(ReferenceWithMetaParty.builder()
                                .setExternalReference("Client"))
                        .setRole(CounterpartyRoleEnum.PARTY_1)))
                .addCounterparty(Counterparty.builder()
                        .setPartyReference(ReferenceWithMetaParty.builder()
                                .setExternalReference("Dealer"))
                        .setRole(CounterpartyRoleEnum.PARTY_2))
                .setParty(partyList)
                .setPartyRole(partyRoleList)
                .setExecutionDetails(ExecutionDetails.builder()
                        .setExecutionVenue(LegalEntity.builder()
                                .setEntityId(List.of(FieldWithMetaString.builder()
                                        .setValue(bondFutureModel.marketid)))))
                .build();


        return trade;
    }

}
