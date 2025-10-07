/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Pojo;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class NostroAccountFilterDto {

    private String sender;
    private String receiver;
    private List<String> geography;
    private String accountNo;
    private String transactionReference;
    private String relatedReference;
    private String currency;
    private List<Integer> messageTypes;
    private String text;

    private BigDecimal amountFrom;
    private BigDecimal amountTo;

    private LocalDate valueDateFrom;

    private LocalDate valueDateTo;

    private LocalDate sendRecFromDate;

    private LocalDate sendRecToDate;

    /**
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * @return the geography
     */
    public List<String> getGeography() {
        return geography;
    }

    /**
     * @param geography the geography to set
     */
    public void setGeography(List<String> geography) {
        this.geography = geography;
    }

    /**
     * @return the accountNo
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * @param accountNo the accountNo to set
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    /**
     * @return the transactionReference
     */
    public String getTransactionReference() {
        return transactionReference;
    }

    /**
     * @param transactionReference the transactionReference to set
     */
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    /**
     * @return the relatedReference
     */
    public String getRelatedReference() {
        return relatedReference;
    }

    /**
     * @param relatedReference the relatedReference to set
     */
    public void setRelatedReference(String relatedReference) {
        this.relatedReference = relatedReference;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the messageTypes
     */
    public List<Integer> getMessageTypes() {
        return messageTypes;
    }

    /**
     * @param messageType the messageTypes to set
     */
    public void setMessageTypes(List<Integer> messageTypes) {
        this.messageTypes = messageTypes;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the amountFrom
     */
    public BigDecimal getAmountFrom() {
        return amountFrom;
    }

    /**
     * @param amountFrom the amountFrom to set
     */
    public void setAmountFrom(BigDecimal amountFrom) {
        this.amountFrom = amountFrom;
    }

    /**
     * @return the amountTo
     */
    public BigDecimal getAmountTo() {
        return amountTo;
    }

    /**
     * @param amountTo the amountTo to set
     */
    public void setAmountTo(BigDecimal amountTo) {
        this.amountTo = amountTo;
    }

    /**
     * @return the valueDateFrom
     */
    public LocalDate getValueDateFrom() {
        return valueDateFrom;
    }

    /**
     * @param valueDateFrom the valueDateFrom to set
     */
    public void setValueDateFrom(LocalDate valueDateFrom) {
        this.valueDateFrom = valueDateFrom;
    }

    /**
     * @return the valueDateTo
     */
    public LocalDate getValueDateTo() {
        return valueDateTo;
    }

    /**
     * @param valueDateTo the valueDateTo to set
     */
    public void setValueDateTo(LocalDate valueDateTo) {
        this.valueDateTo = valueDateTo;
    }

    /**
     * @return the sendRecFromDate
     */
    public LocalDate getSendRecFromDate() {
        return sendRecFromDate;
    }

    /**
     * @param sendRecFromDate the sendRecFromDate to set
     */
    public void setSendRecFromDate(LocalDate sendRecFromDate) {
        this.sendRecFromDate = sendRecFromDate;
    }

    /**
     * @return the sendRecToDate
     */
    public LocalDate getSendRecToDate() {
        return sendRecToDate;
    }

    /**
     * @param sendRecToDate the sendRecToDate to set
     */
    public void setSendRecToDate(LocalDate sendRecToDate) {
        this.sendRecToDate = sendRecToDate;
    }
}
