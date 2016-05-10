package com.paygo.domain;

/**
 * class for credit card transaction
 */
public class Transaction {

    private String transactionId;
    private String transactionStatus;
    private String validationStatus;
    private String transactionType;
    private String transactionIdExternal;
    private String transactionTag;
    /*amount in cents */
    private int amount;
    private Currency currency;
    private String correlationID;
    private String bankResponseCode;
    private String bankMessage;
    private String gatewayResponseCode;
    private String gatewayMessage;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionIdExternal() {
        return transactionIdExternal;
    }

    public void setTransactionIdExternal(String transactionIdExternal) {
        this.transactionIdExternal = transactionIdExternal;
    }

    public String getTransactionTag() {
        return transactionTag;
    }

    public void setTransactionTag(String transactionTag) {
        this.transactionTag = transactionTag;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public String getBankResponseCode() {
        return bankResponseCode;
    }

    public void setBankResponseCode(String bankResponseCode) {
        this.bankResponseCode = bankResponseCode;
    }

    public String getBankMessage() {
        return bankMessage;
    }

    public void setBankMessage(String bankMessage) {
        this.bankMessage = bankMessage;
    }

    public String getGatewayResponseCode() {
        return gatewayResponseCode;
    }

    public void setGatewayResponseCode(String gatewayResponseCode) {
        this.gatewayResponseCode = gatewayResponseCode;
    }

    public String getGatewayMessage() {
        return gatewayMessage;
    }

    public void setGatewayMessage(String gatewayMessage) {
        this.gatewayMessage = gatewayMessage;
    }



}
