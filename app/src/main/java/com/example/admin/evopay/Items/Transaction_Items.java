package com.example.admin.evopay.Items;

import com.evosnap.commercedriver.cws.transactions.Addendum;

import java.util.Date;

/**
 * Created by admin on 2/20/2018.
 */

public class Transaction_Items {


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }

    public String getCardholderName() {
        return CardholderName;
    }

    public void setCardholderName(String cardholderName) {
        CardholderName = cardholderName;
    }

    public String getPan() {
        return Pan;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getRetrievalReferenceNumber() {
        return retrievalReferenceNumber;
    }

    public void setRetrievalReferenceNumber(String retrievalReferenceNumber) {
        this.retrievalReferenceNumber = retrievalReferenceNumber;
    }



    public String getTenderData() {
        return tenderData;
    }

    public Addendum getAddendum() {
        return addendum;
    }

    public void setAddendum(Addendum addendum) {
        this.addendum = addendum;
    }

    public void setTenderData(String tenderData) {
        this.tenderData = tenderData;
    }

    public String getTrans_Code() {
        return Trans_Code;
    }

    public void setTrans_Code(String trans_Code) {
        Trans_Code = trans_Code;
    }

    public void setPan(String pan) {
        Pan = pan;
    }

    public String getPaydataToken() {
        return PaydataToken;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }



    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    public void setPaydataToken(String paydataToken) {
        PaydataToken = paydataToken;
    }


    public String getCapturedTransId() {
        return CapturedTransId;
    }

    public void setCapturedTransId(String capturedTransId) {
        CapturedTransId = capturedTransId;
    }

    public String getCardType() {
        return CardType;
    }

    public void setCardType(String cardType) {
        CardType = cardType;
    }

    public  String amount,ExpirationDate,CardholderName,Pan,CardType,transactionId,retrievalReferenceNumber,CapturedTransId,
            tenderData,Trans_Code,PaydataToken,applicationId,merchantId;
    Addendum addendum;
    Date settlementDate;
}
