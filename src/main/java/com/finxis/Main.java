package com.finxis;

import cdm.base.datetime.*;
import cdm.base.datetime.daycount.DayCountFractionEnum;
import cdm.base.datetime.daycount.metafields.FieldWithMetaDayCountFractionEnum;
import cdm.base.datetime.metafields.FieldWithMetaBusinessCenterEnum;
import cdm.base.datetime.metafields.FieldWithMetaTimeZone;
import cdm.base.datetime.metafields.ReferenceWithMetaBusinessCenters;
import cdm.base.math.*;
import cdm.base.math.metafields.FieldWithMetaNonNegativeQuantitySchedule;
import cdm.base.math.metafields.ReferenceWithMetaNonNegativeQuantitySchedule;
import cdm.base.staticdata.asset.common.*;
import cdm.base.staticdata.asset.rates.FloatingRateIndexEnum;
import cdm.base.staticdata.identifier.AssignedIdentifier;
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
import cdm.product.asset.*;
import cdm.product.common.schedule.CalculationPeriodDates;
import cdm.product.common.schedule.PayRelativeToEnum;
import cdm.product.common.schedule.PaymentDates;
import cdm.product.common.schedule.RateSchedule;
import cdm.product.common.settlement.ResolvablePriceQuantity;
import cdm.product.common.settlement.SettlementDate;
import cdm.product.common.settlement.SettlementTerms;
import cdm.product.template.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finxis.models.*;
import com.finxis.util.FileWriter;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
import com.rosetta.model.lib.meta.Reference;
import com.rosetta.model.lib.records.Date;
import com.rosetta.model.metafields.FieldWithMetaDate;
import com.rosetta.model.metafields.FieldWithMetaString;
import com.rosetta.model.metafields.MetaFields;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("CDM Bond Demo");

        Main main = new Main();
        FileWriter fileWriter = new FileWriter();
        DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String eventDateTime = localDateTime.format(eventDateFormat);


        //Bond Model
        BondModel bondModel = main.setBondData();
        Product product = main.createBondProduct(bondModel);
        Trade trade = main.createTrade(product, bondModel);

        String tradeJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(trade);
        System.out.println(tradeJson);
        fileWriter.writeEventToFile("bond-example", eventDateTime, tradeJson);


        ObjectMapper rosettaObjectMapper = RosettaObjectMapper.getNewRosettaObjectMapper();
        Trade tradeObj = new Trade.TradeBuilderImpl();
        Trade newTrade = rosettaObjectMapper.readValue(tradeJson, tradeObj.getClass());

        //Bond Future
        BondFutureModel bondFutureModel = main.setBondFutureData();
        product = main.createBondFutureProduct(bondFutureModel);
        trade = main.createFutureTrade(product, bondFutureModel);

        tradeJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(trade);
        System.out.println(tradeJson);
        fileWriter.writeEventToFile("bond-future-example", eventDateTime, tradeJson);

        rosettaObjectMapper = RosettaObjectMapper.getNewRosettaObjectMapper();
        tradeObj = new Trade.TradeBuilderImpl();
        newTrade = rosettaObjectMapper.readValue(tradeJson, tradeObj.getClass());


        //FX Cash

        FXCashModel fxCashModel = main.setFXCashData();
        product = main.createFXCashProduct(fxCashModel);
        TradeModel fxTradeModel = main.setFXCashTradeData();
        trade = main.createFXCashTrade(product, fxTradeModel);

        tradeJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(trade);
        System.out.println(tradeJson);
        fileWriter.writeEventToFile("fxcash-example", eventDateTime, tradeJson);

        rosettaObjectMapper = RosettaObjectMapper.getNewRosettaObjectMapper();
        tradeObj = new Trade.TradeBuilderImpl();
        newTrade = rosettaObjectMapper.readValue(tradeJson, tradeObj.getClass());

        //FX Option

        FXOptionModel fxOptionModel = main.setFXOptionData();
        product = main.createFXOptionProduct(fxOptionModel);
        TradeModel fxOptionTradeModel = main.setFXOptionTradeData();
        trade = main.createFXCashTrade(product, fxOptionTradeModel);

        tradeJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(trade);
        System.out.println(tradeJson);
        fileWriter.writeEventToFile("fxoption-example", eventDateTime, tradeJson);

        rosettaObjectMapper = RosettaObjectMapper.getNewRosettaObjectMapper();
        tradeObj = new Trade.TradeBuilderImpl();
        newTrade = rosettaObjectMapper.readValue(tradeJson, tradeObj.getClass());

        //FX Option

        IrsOtcModel irsOtcModel = main.setIrsOctData();
        product = main.createIrsOtcProduct(irsOtcModel);
        TradeModel irsOtcTradeModel = main.setIrsOtcTradeData();
        trade = main.createFXCashTrade(product, irsOtcTradeModel);

        tradeJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(trade);
        System.out.println(tradeJson);
        fileWriter.writeEventToFile("irsotc-example", eventDateTime, tradeJson);

        rosettaObjectMapper = RosettaObjectMapper.getNewRosettaObjectMapper();
        tradeObj = new Trade.TradeBuilderImpl();
        newTrade = rosettaObjectMapper.readValue(tradeJson, tradeObj.getClass());

    }

    public IrsOtcModel setIrsOctData(){

        IrsOtcModel irs = new IrsOtcModel();
        FixedRateLeg l1 = new FixedRateLeg();
        l1.payReceive = "PAY";
        l1.effectiveDate = "2025-05-20T00:00:00.000+00:00";
        l1.maturityDate = "2030-05-20T00:00:00.000+00:00";
        l1.interestRate = "4.5";
        l1.notional = "5000000";
        l1.payFreq = "Y";
        l1.currency = "USD";

        FloatingRateLeg l2 = new FloatingRateLeg();
        l2.payReceive = "RECEIVE";
        l2.effectiveDate = "2025-05-20T00:00:00.000+00:00";
        l2.maturityDate = "2030-05-20T00:00:00.000+00:00";
        l2.notional = "5000000";
        l2.payFreq = "Y";
        l2.currency = "USD";
        l2.index = "USD_SOFR_OIS_COMPOUND";
        l2.indexFreq = "3";

        irs.fixedRateLegList = List.of(l1);
        irs.floatingRateLegList = List.of(l2);

        return irs;
    }

    public TradeModel setIrsOtcTradeData(){

        TradeModel tradeModel = new TradeModel();
        tradeModel.firmId = "DEALER";
        tradeModel.price = "104.5";
        tradeModel.priceCurrency= "USD";
        tradeModel.quantity = "5000000.00";
        tradeModel.counterparty1Id = "BuyerId";
        tradeModel.counterparty2Id = "SellerId";
        tradeModel.settlementDate = "2025-05-20T00:00:00.000+00:00";
        tradeModel.tradeDate = "2025-05-18T00:00:00.000+00:00";

        return tradeModel;

    }

    public Product createIrsOtcProduct(IrsOtcModel irsOtcModel) {

        Product product = Product.builder()
                .setNonTransferableProduct(NonTransferableProduct.builder()
                        .setEconomicTerms(EconomicTerms.builder()
                                .setPayout(List.of(Payout.builder()
                                                .setInterestRatePayout(InterestRatePayout.builder()
                                                        .setPriceQuantity(ResolvablePriceQuantity.builder()
                                                                .setQuantitySchedule(ReferenceWithMetaNonNegativeQuantitySchedule.builder()
                                                                        .setReference(Reference.builder()
                                                                                .setScope("DOCUMENT")
                                                                                .setReference("quantity-1"))))
                                                        .setDayCountFraction(FieldWithMetaDayCountFractionEnum.builder().setValue(DayCountFractionEnum.ACT_365_FIXED).build())
                                                        .setCalculationPeriodDates(CalculationPeriodDates.builder()
                                                                .setEffectiveDate(AdjustableOrRelativeDate.builder()
                                                                        .setAdjustableDate(AdjustableDate.builder()
                                                                                .setUnadjustedDate(this.createDate(irsOtcModel.floatingRateLegList.get(0).effectiveDate))
                                                                                .setDateAdjustments(BusinessDayAdjustments.builder()
                                                                                        .setBusinessDayConvention(BusinessDayConventionEnum.NONE))))
                                                                .setTerminationDate(AdjustableOrRelativeDate.builder()
                                                                        .setAdjustableDate(AdjustableDate.builder()
                                                                                .setUnadjustedDate(this.createDate(irsOtcModel.floatingRateLegList.get(0).maturityDate))
                                                                                .setDateAdjustments(BusinessDayAdjustments.builder()
                                                                                        .setBusinessDayConvention(BusinessDayConventionEnum.MODFOLLOWING)
                                                                                        .setBusinessCenters(BusinessCenters.builder()
                                                                                                .setBusinessCentersReference(ReferenceWithMetaBusinessCenters.builder()
                                                                                                        .setExternalReference("primaryBusinessCenters")
                                                                                                        .build()))))
                                                                )
                                                                .setCalculationPeriodFrequency(CalculationPeriodFrequency.builder()
                                                                        .setRollConvention(RollConventionEnum._3)
                                                                        .setPeriodMultiplier(6)
                                                                        .setPeriod(PeriodExtendedEnum.M))
                                                                .setCalculationPeriodDatesAdjustments(BusinessDayAdjustments.builder()
                                                                        .setBusinessDayConvention(BusinessDayConventionEnum.MODFOLLOWING)
                                                                        .setBusinessCenters(BusinessCenters.builder()
                                                                                .setBusinessCentersReference(
                                                                                        ReferenceWithMetaBusinessCenters.builder().setExternalReference("primaryBusinessCenters").build()))))

                                                        .setPaymentDates(PaymentDates.builder()
                                                                .setPaymentFrequency(Frequency.builder()
                                                                        .setPeriodMultiplier(3)
                                                                        .setPeriod(PeriodExtendedEnum.M)))

                                                        .setRateSpecification(RateSpecification.builder()
                                                                .setFloatingRateSpecification(FloatingRateSpecification.builder()
                                                                        .setRateOptionValue(InterestRateIndex.builder()
                                                                                .setFloatingRateIndex(FloatingRateIndex.builder()
                                                                                        .setFloatingRateIndexValue(FloatingRateIndexEnum.USD_SOFR_OIS_COMPOUND)
                                                                                        .setIndexTenor(Period.builder()
                                                                                                .setPeriod(PeriodEnum.Y)
                                                                                                .setPeriodMultiplier(1))))))

                                                        .setPayerReceiver(PayerReceiver.builder()
                                                                .setPayer(CounterpartyRoleEnum.PARTY_2)
                                                                .setReceiver(CounterpartyRoleEnum.PARTY_1)))))

                .addPayout(Payout.builder()
                        .setInterestRatePayout(InterestRatePayout.builder()
                .setPriceQuantity(ResolvablePriceQuantity.builder()
                        .setQuantitySchedule(ReferenceWithMetaNonNegativeQuantitySchedule.builder()
                                .setReference(Reference.builder()
                                        .setScope("DOCUMENT")
                                        .setReference("quantity-2"))))
                .setDayCountFraction(FieldWithMetaDayCountFractionEnum.builder().setValue(DayCountFractionEnum._30E_360).build())
                .setCalculationPeriodDates(CalculationPeriodDates.builder()
                        .setEffectiveDate(AdjustableOrRelativeDate.builder()
                                .setAdjustableDate(AdjustableDate.builder()
                                        .setUnadjustedDate(this.createDate(irsOtcModel.fixedRateLegList.get(0).effectiveDate))
                                        .setDateAdjustments(BusinessDayAdjustments.builder()
                                                .setBusinessDayConvention(BusinessDayConventionEnum.NONE))))
                        .setTerminationDate(AdjustableOrRelativeDate.builder()
                                .setAdjustableDate(AdjustableDate.builder()
                                        .setUnadjustedDate(this.createDate(irsOtcModel.fixedRateLegList.get(0).effectiveDate))
                                        .setDateAdjustments(BusinessDayAdjustments.builder()
                                                .setBusinessDayConvention(BusinessDayConventionEnum.MODFOLLOWING)
                                                .setBusinessCenters(BusinessCenters.builder()
                                                        .setBusinessCentersReference(ReferenceWithMetaBusinessCenters.builder()
                                                                .setExternalReference("primaryBusinessCenters"))
                                                        .addBusinessCenter(
                                                                FieldWithMetaBusinessCenterEnum.builder().setValue(BusinessCenterEnum.EUTA).build())))))
                        .setCalculationPeriodFrequency(CalculationPeriodFrequency.builder()
                                .setRollConvention(RollConventionEnum._3)
                                .setPeriodMultiplier(3)
                                .setPeriod(PeriodExtendedEnum.M))
                        .setCalculationPeriodDatesAdjustments(BusinessDayAdjustments.builder()
                                .setBusinessDayConvention(BusinessDayConventionEnum.MODFOLLOWING)
                                .setBusinessCenters(BusinessCenters.builder()
                                        .setBusinessCentersReference(ReferenceWithMetaBusinessCenters.builder()
                                                .setExternalReference("primaryBusinessCenters")))))
                .setPaymentDates(PaymentDates.builder()
                        .setPayRelativeTo(PayRelativeToEnum.CALCULATION_PERIOD_END_DATE)
                        .setPaymentFrequency(Frequency.builder()
                                .setPeriodMultiplier(1)
                                .setPeriod(PeriodExtendedEnum.Y)
                                .build())
                        .build())
                .setRateSpecification(RateSpecification.builder()
                        .setFixedRateSpecification(FixedRateSpecification.builder()
                                .setRateSchedule(RateSchedule.builder()
                                        .setPrice(ReferenceWithMetaPriceSchedule.builder()
                                                .setReference(Reference.builder()
                                                        .setScope("DOCUMENT")
                                                        .setReference("price-1"))
                                                .setValue(Price.builder()
                                                        .setValue(BigDecimal.valueOf(Double.parseDouble(irsOtcModel.fixedRateLegList.get(0).interestRate)))
                                                        .setUnit(UnitType.builder().setCurrencyValue(irsOtcModel.fixedRateLegList.get(0).currency))
                                                        .setPerUnitOf(UnitType.builder().setCurrencyValue(irsOtcModel.fixedRateLegList.get(0).currency))
                                                        .setPriceType(PriceTypeEnum.INTEREST_RATE))))))
                .setPayerReceiver(PayerReceiver.builder()
                        .setPayer(CounterpartyRoleEnum.PARTY_1)
                        .setReceiver(CounterpartyRoleEnum.PARTY_2))
                .build()))));

            return product;

    }

    public Trade createIrsOtcTrade(Product irsOtcProduct, TradeModel irsOctTradeModel) {
        AdjustableOrRelativeDate settlementDate = this.createAdjustableDate(irsOctTradeModel.settlementDate);
        Date tradeDate = this.createDate(irsOctTradeModel.tradeDate);

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
                .setProduct(irsOtcProduct.getNonTransferableProduct())
                .setTradeDate(FieldWithMetaDate.builder()
                        .setValue(tradeDate))
                .setTradeIdentifier(List.of(TradeIdentifier.builder()
                        .setAssignedIdentifier(List.of(AssignedIdentifier.builder()
                                .setIdentifierValue(irsOctTradeModel.tradeId)))))
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
                .setTradeLot(List.of(TradeLot.builder()
                        .setPriceQuantity(List.of(PriceQuantity.builder()
                                .setPrice(List.of(FieldWithMetaPriceSchedule.builder()
                                        .setValue(PriceSchedule.builder()
                                                .setValue(BigDecimal.valueOf(Double.parseDouble(irsOctTradeModel.price)))
                                                .setUnit(UnitType.builder()
                                                        .setCurrencyValue(irsOctTradeModel.priceCurrency))
                                                .setPerUnitOf(UnitType.builder()
                                                        .setCurrencyValue(irsOctTradeModel.priceCurrency))
                                                .setPriceType(PriceTypeEnum.ASSET_PRICE)
                                                .setPriceExpression(PriceExpressionEnum.ABSOLUTE_TERMS)
                                                .setComposite(PriceComposite.builder()
                                                        .setBaseValue(BigDecimal.valueOf(Double.parseDouble(irsOctTradeModel.price)))
                                                        .setOperand(BigDecimal.valueOf(Double.parseDouble(".0213")))
                                                        .setArithmeticOperator(ArithmeticOperationEnum.ADD)
                                                        .setOperandType(PriceOperandEnum.ACCRUED_INTEREST)))))
                                .setQuantity(List.of(FieldWithMetaNonNegativeQuantitySchedule.builder()
                                        .setValue(NonNegativeQuantitySchedule.builder()
                                                .setValue(BigDecimal.valueOf(Double.parseDouble(irsOctTradeModel.quantity)))
                                                .setUnit(UnitType.builder()
                                                        .setCurrencyValue(irsOctTradeModel.priceCurrency)))))
                                .setObservable(FieldWithMetaObservable.builder()
                                        .setValue(Observable.builder()
                                                .setAsset(Asset.builder()
                                                        .setCash(Cash.builder()
                                                                .setIdentifier(List.of(AssetIdentifier.builder()
                                                                        .setIdentifierType(AssetIdTypeEnum.CURRENCY_CODE)
                                                                        .setIdentifierValue(irsOctTradeModel.priceCurrency)))))))))))

                .build();

        return trade;

    }


    public FXOptionModel setFXOptionData(){

        FXOptionModel fxOptionModel = new FXOptionModel();

        fxOptionModel.underLyingAsset = "EUR/USD";
        fxOptionModel.callPut = "CALL";
        fxOptionModel.modelType = "BLACK";
        fxOptionModel.effectiveDate = "2025-05-20T00:00:00.000+00:00";
        fxOptionModel.expirationDate = "2025-08-20T00:00:00.000+00:00";
        fxOptionModel.deliveryDate = "2025-08-22T00:00:00.000+00:00";
        fxOptionModel.notionalCurrency = "USD";
        fxOptionModel.strike = "1.2";
        fxOptionModel.style = "EUROPEAN";
        fxOptionModel.volitility = "10";

        return fxOptionModel;
    }

    public TradeModel setFXOptionTradeData(){

        TradeModel tradeModel = new TradeModel();
        tradeModel.firmId = "DEALER";
        tradeModel.price = "2.55";
        tradeModel.priceCurrency= "USD";
        tradeModel.quantity = "5000000.00";
        tradeModel.counterparty1Id = "BuyerId";
        tradeModel.counterparty2Id = "SellerId";
        tradeModel.settlementDate = "2025-05-20T00:00:00.000+00:00";
        tradeModel.tradeDate = "2025-05-18T00:00:00.000+00:00";

        return tradeModel;

    }

    public Product createFXOptionProduct(FXOptionModel fxOptionModel){

            Product product = Product.builder()
                    .setNonTransferableProduct(NonTransferableProduct.builder()
                            .setEconomicTerms(EconomicTerms.builder()
                                    .setEffectiveDate(this.createAdjustableDate(fxOptionModel.effectiveDate))
                                    .setTerminationDate(this.createAdjustableDate(fxOptionModel.expirationDate))
                                    .setPayout(List.of(Payout.builder()
                                            .setOptionPayout(OptionPayout.builder()
                                                    .setUnderlier(Underlier.builder()
                                                            .setProduct(Product.builder()
                                                                    .setTransferableProduct(TransferableProduct.builder()
                                                                            .setCash(Cash.builder()
                                                                                    .setIdentifier(List.of(AssetIdentifier.builder()
                                                                                            .setIdentifierType(AssetIdTypeEnum.CURRENCY_CODE)
                                                                                            .setIdentifierValue(fxOptionModel.underLyingAsset)))))))
                                                    .setOptionType(OptionTypeEnum.CALL)
                                                    .setExerciseTerms(ExerciseTerms.builder()
                                                            .setStyle(OptionExerciseStyleEnum.EUROPEAN)
                                                            .setExerciseDates(this.createAdjustableDates(fxOptionModel.expirationDate)))
                                                    .setStrike(OptionStrike.builder()
                                                            .setStrikePrice(Price.builder()
                                                                    .setValue(BigDecimal.valueOf(Double.parseDouble(fxOptionModel.strike))))))))))
                    .build();

            return product;

    }

    public Trade createFXOptionTrade(Product fxOptionProduct, TradeModel fxOptionTradeModel) {
        AdjustableOrRelativeDate settlementDate = this.createAdjustableDate(fxOptionTradeModel.settlementDate);
        Date tradeDate = this.createDate(fxOptionTradeModel.tradeDate);

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
                .setProduct(fxOptionProduct.getNonTransferableProduct())
                .setTradeDate(FieldWithMetaDate.builder()
                        .setValue(tradeDate))
                .setTradeIdentifier(List.of(TradeIdentifier.builder()
                        .setAssignedIdentifier(List.of(AssignedIdentifier.builder()
                                .setIdentifierValue(fxOptionTradeModel.tradeId)))))
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
                .setTradeLot(List.of(TradeLot.builder()
                        .setPriceQuantity(List.of(PriceQuantity.builder()
                                .setPrice(List.of(FieldWithMetaPriceSchedule.builder()
                                        .setValue(PriceSchedule.builder()
                                                .setValue(BigDecimal.valueOf(Double.parseDouble(fxOptionTradeModel.price)))
                                                .setUnit(UnitType.builder()
                                                        .setCurrencyValue(fxOptionTradeModel.priceCurrency))
                                                .setPerUnitOf(UnitType.builder()
                                                        .setCurrencyValue(fxOptionTradeModel.priceCurrency))
                                                .setPriceType(PriceTypeEnum.ASSET_PRICE)
                                                .setPriceExpression(PriceExpressionEnum.ABSOLUTE_TERMS)
                                                .setComposite(PriceComposite.builder()
                                                        .setBaseValue(BigDecimal.valueOf(Double.parseDouble(fxOptionTradeModel.price)))
                                                        .setOperand(BigDecimal.valueOf(Double.parseDouble(".0213")))
                                                        .setArithmeticOperator(ArithmeticOperationEnum.ADD)
                                                        .setOperandType(PriceOperandEnum.ACCRUED_INTEREST)))))
                                .setQuantity(List.of(FieldWithMetaNonNegativeQuantitySchedule.builder()
                                        .setValue(NonNegativeQuantitySchedule.builder()
                                                .setValue(BigDecimal.valueOf(Double.parseDouble(fxOptionTradeModel.quantity)))
                                                .setUnit(UnitType.builder()
                                                        .setCurrencyValue(fxOptionTradeModel.priceCurrency)))))
                                .setObservable(FieldWithMetaObservable.builder()
                                        .setValue(Observable.builder()
                                                .setAsset(Asset.builder()
                                                        .setCash(Cash.builder()
                                                                .setIdentifier(List.of(AssetIdentifier.builder()
                                                                        .setIdentifierType(AssetIdTypeEnum.CURRENCY_CODE)
                                                                        .setIdentifierValue(fxOptionTradeModel.priceCurrency)))))))))))

                .build();

        return trade;
    }

    public FXCashModel setFXCashData(){

        FXCashModel fxCashModel = new FXCashModel();
        fxCashModel.currency1 = "USD";
        fxCashModel.currency2 = "JPY";
        fxCashModel.valueDate = "2025-05-20T00:00:00.000+00:00";

        return fxCashModel;

    }

    public TradeModel setFXCashTradeData(){

        TradeModel tradeModel = new TradeModel();
        tradeModel.firmId = "DEALER";
        tradeModel.price = "145.50";
        tradeModel.priceCurrency= "USD";
        tradeModel.quantity = "5000000.00";
        tradeModel.counterparty1Id = "BuyerId";
        tradeModel.counterparty2Id = "SellerId";
        tradeModel.settlementDate = "2025-05-20T00:00:00.000+00:00";
        tradeModel.tradeDate = "2025-05-18T00:00:00.000+00:00";

        return tradeModel;

    }

    public Product createFXCashProduct(FXCashModel fxCashModel){

        AdjustableOrRelativeDate settlementDate = this.createAdjustableDate(fxCashModel.valueDate);

        Product product = Product.builder()
            .setNonTransferableProduct(NonTransferableProduct.builder()
                    .setEconomicTerms(EconomicTerms.builder()
                            .setPayout(List.of(Payout.builder()
                                    .setSettlementPayout(SettlementPayout.builder()
                                            .setUnderlier(Underlier.builder()
                                                    .setProduct(Product.builder()
                                                            .setTransferableProduct(TransferableProduct.builder()
                                                                    .setCash(Cash.builder()
                                                                            .setIdentifier(List.of(AssetIdentifier.builder()
                                                                                            .setIdentifierType(AssetIdTypeEnum.CURRENCY_CODE)
                                                                                            .setIdentifierValue(fxCashModel.currency2)))))))
                                            .setPayerReceiver(PayerReceiver.builder()
                                                    .setPayer(CounterpartyRoleEnum.PARTY_2)
                                                    .setReceiver(CounterpartyRoleEnum.PARTY_1)))))
                            .setTerminationDate(settlementDate)))
                            .build();

       return product;
    }

    public Trade createFXCashTrade(Product fxCashProduct, TradeModel fxTradeModel){

        AdjustableOrRelativeDate settlementDate = this.createAdjustableDate(fxTradeModel.settlementDate);
        Date tradeDate = this.createDate(fxTradeModel.tradeDate);

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
                .setProduct(fxCashProduct.getNonTransferableProduct())
                .setTradeDate(FieldWithMetaDate.builder()
                        .setValue(tradeDate))
                .setTradeIdentifier(List.of(TradeIdentifier.builder()
                                .setAssignedIdentifier(List.of(AssignedIdentifier.builder()
                                                .setIdentifierValue(fxTradeModel.tradeId)))))
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
                .setTradeLot(List.of(TradeLot.builder()
                        .setPriceQuantity(List.of(PriceQuantity.builder()
                                .setPrice(List.of(FieldWithMetaPriceSchedule.builder()
                                        .setValue(PriceSchedule.builder()
                                                .setValue(BigDecimal.valueOf(Double.parseDouble(fxTradeModel.price)))
                                                .setUnit(UnitType.builder()
                                                        .setCurrencyValue(fxTradeModel.priceCurrency))
                                                .setPerUnitOf(UnitType.builder()
                                                        .setCurrencyValue(fxTradeModel.priceCurrency))
                                                .setPriceType(PriceTypeEnum.ASSET_PRICE)
                                                .setPriceExpression(PriceExpressionEnum.ABSOLUTE_TERMS)
                                                .setComposite(PriceComposite.builder()
                                                        .setBaseValue(BigDecimal.valueOf(Double.parseDouble(fxTradeModel.price)))
                                                        .setOperand(BigDecimal.valueOf(Double.parseDouble(".0213")))
                                                        .setArithmeticOperator(ArithmeticOperationEnum.ADD)
                                                        .setOperandType(PriceOperandEnum.ACCRUED_INTEREST)))))
                                .setQuantity(List.of(FieldWithMetaNonNegativeQuantitySchedule.builder()
                                        .setValue(NonNegativeQuantitySchedule.builder()
                                                .setValue(BigDecimal.valueOf(Double.parseDouble(fxTradeModel.quantity)))
                                                .setUnit(UnitType.builder()
                                                        .setCurrencyValue(fxTradeModel.priceCurrency)))))
                                .setObservable(FieldWithMetaObservable.builder()
                                        .setValue(Observable.builder()
                                                .setAsset(Asset.builder()
                                                        .setCash(Cash.builder()
                                                                        .setIdentifier(List.of(AssetIdentifier.builder()
                                                                                .setIdentifierType(AssetIdTypeEnum.CURRENCY_CODE)
                                                                                .setIdentifierValue(fxTradeModel.priceCurrency)))))))))))

                .build();

    return trade;


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
                    .setNonTransferableProduct(NonTransferableProduct.builder()
                            .setIdentifier(List.of(ProductIdentifier.builder()
                                                .setSource(ProductIdTypeEnum.ISIN)
                                                .setIdentifierValue(bondModel.getIsin())))
                                                .addIdentifier(ProductIdentifier.builder()
                                                    .setIdentifier(FieldWithMetaString.builder()
                                                        .setValue(bondModel.instrumentName))
                                                        .setSource(ProductIdTypeEnum.NAME.NAME))
                                                .addIdentifier(ProductIdentifier.builder()
                                                        .setIdentifier(FieldWithMetaString.builder()
                                                                .setValue("LLOYDS"))
                                                        .setSource(ProductIdTypeEnum.NAME.NAME))
                        .setEconomicTerms(EconomicTerms.builder()
                                .setTerminationDate(AdjustableOrRelativeDate.builder()
                                        .setAdjustableDate(AdjustableDate.builder()
                                                .setAdjustedDate(FieldWithMetaDate.builder()
                                                        .setValue(terminationDate))))

                                .addPayout(Payout.builder()
                                        .setSettlementPayout(SettlementPayout.builder()
                                                .setPayerReceiver(PayerReceiver.builder()
                                                        .setPayer(CounterpartyRoleEnum.PARTY_1)
                                                        .setReceiver(CounterpartyRoleEnum.PARTY_2))
                                                .setSettlementTerms(SettlementTerms.builder()
                                                        .setSettlementDate(SettlementDate.builder()
                                                                .setAdjustableOrRelativeDate(AdjustableOrAdjustedOrRelativeDate.builder()
                                                                                .setAdjustedDate(FieldWithMetaDate.builder()
                                                                                        .setValue(settlementDate)))))
                                                .setUnderlier(Underlier.builder()
                                                        .setProduct(Product.builder()
                                                                .setTransferableProduct(TransferableProduct.builder()
                                                                        .setInstrument(Instrument.builder()
                                                                                .setSecurity(Security.builder()
                                                                                        .setIdentifier(List.of(AssetIdentifier.builder()
                                                                                                        .setIdentifierValue(bondModel.getIsin())
                                                                                                        .setIdentifierType(AssetIdTypeEnum.ISIN)))))
                                                                        .setEconomicTerms(EconomicTerms.builder()
                                                                                .setPayout(List.of(Payout.builder()
                                                                                .setInterestRatePayout(InterestRatePayout.builder()
                                                                                        .setDayCountFraction(FieldWithMetaDayCountFractionEnum.builder()
                                                                                                .setValue(DayCountFractionEnum.ACT_365_FIXED))
                                                                                        .setPaymentDates(PaymentDates.builder()
                                                                                                .setPaymentFrequency(Frequency.builder()
                                                                                                        .setPeriod(PeriodExtendedEnum.Y)
                                                                                                        .setPeriodMultiplier(1)
                                                                                                        .build())
                                                                                                .build())
                                                                                        .setRateSpecification(RateSpecification.builder()
                                                                                                .setFixedRateSpecification(FixedRateSpecification.builder()
                                                                                                        .setRateSchedule(RateSchedule.builder()
                                                                                                                .setPriceValue(PriceSchedule.builder()
                                                                                                                        .setPriceExpression(PriceExpressionEnum.PAR_VALUE_FRACTION))
                                                                                                                .setPrice(ReferenceWithMetaPriceSchedule.builder()
                                                                                                                        .setValue(PriceSchedule.builder()
                                                                                                                                .setValue(BigDecimal.valueOf(Double.parseDouble(bondModel.couponRate)
                                                                                                                                ))))))))))))))))))
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
                .setProduct(bond.getNonTransferableProduct())
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
                .setNonTransferableProduct(NonTransferableProduct.builder()
                        .setIdentifier(List.of(ProductIdentifier.builder()
                                        .setIdentifierValue(bondFutureModel.instrumentName)
                                        .setSource(ProductIdTypeEnum.NAME)))
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

    public Trade createFutureTrade(Product bondFuture, BondFutureModel bondFutureModel){

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
                .setProduct(bondFuture.getNonTransferableProduct())
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

    public AdjustableOrRelativeDate createAdjustableDate(String dateStr){

        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        ZonedDateTime maturityDate = ZonedDateTime.parse(dateStr, formatter);
        Date date = Date.of(maturityDate.getYear(), maturityDate.getMonthValue(), maturityDate.getDayOfMonth());


        AdjustableOrRelativeDate adjDate = AdjustableOrRelativeDate.builder()
                .setAdjustableDate(AdjustableDate.builder()
                        .setAdjustedDate(FieldWithMetaDate.builder()
                                .setValue(date)))
                .build();

        return adjDate;
    }

    public AdjustableOrRelativeDates createAdjustableDates(String dateStr){

        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        ZonedDateTime maturityDate = ZonedDateTime.parse(dateStr, formatter);
        Date date = Date.of(maturityDate.getYear(), maturityDate.getMonthValue(), maturityDate.getDayOfMonth());


        AdjustableOrRelativeDates adjDate = AdjustableOrRelativeDates.builder()
                .setAdjustableDates(AdjustableDates.builder()
                                .setAdjustedDate(List.of(FieldWithMetaDate.builder()
                                                .setValue(this.createDate(dateStr)))))
                .build();

        return adjDate;
    }


    public Date createDate(String dateStr){

        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        ZonedDateTime zDate = ZonedDateTime.parse(dateStr, formatter);
        Date date= Date.of(zDate.getYear(), zDate.getMonthValue(), zDate.getDayOfMonth());


        return date;
    }

}
