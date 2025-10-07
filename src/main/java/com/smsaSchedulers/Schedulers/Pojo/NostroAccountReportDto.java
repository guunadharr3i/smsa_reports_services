/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Pojo;


import java.math.BigDecimal;
import java.time.LocalDate;

public class NostroAccountReportDto {

    private String fileName;
    private String messageId;
    private String msgIo;
    private Integer messageType;
    private String senderBic;
    private String receiverBic;
    private String accountNumber;
    private String nostroName;
    private LocalDate statementReceivedDate;

    private String transactionCode;
    private LocalDate valueDate62F;
    private String debitCredit62F;
    private String currency62F;
    private BigDecimal closingBalance62F;

    private BigDecimal availableBalance64;
    private String debitCredit64;

    private LocalDate previousOpenValueDate60F;
    private String previousOpenCreditDebit60F;
    private String previousOpenCurrency60F;
    private BigDecimal previousOpenBalance60F;

    // Transaction level info from JSON_TABLE
    private LocalDate transactionValueDate;
    private LocalDate transactionEntryDate;
    private String transactionReference;
    private BigDecimal transactionAmount;
    private String transactionFCode;

    // ================== Getters & Setters ==================

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessageId() {
        return messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMsgIo() {
        return msgIo;
    }
    public void setMsgIo(String msgIo) {
        this.msgIo = msgIo;
    }

    public Integer getMessageType() {
        return messageType;
    }
    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getSenderBic() {
        return senderBic;
    }
    public void setSenderBic(String senderBic) {
        this.senderBic = senderBic;
    }

    public String getReceiverBic() {
        return receiverBic;
    }
    public void setReceiverBic(String receiverBic) {
        this.receiverBic = receiverBic;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getNostroName() {
        return nostroName;
    }
    public void setNostroName(String nostroName) {
        this.nostroName = nostroName;
    }

    public LocalDate getStatementReceivedDate() {
        return statementReceivedDate;
    }
    public void setStatementReceivedDate(LocalDate statementReceivedDate) {
        this.statementReceivedDate = statementReceivedDate;
    }

    public String getTransactionCode() {
        return transactionCode;
    }
    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public LocalDate getValueDate62F() {
        return valueDate62F;
    }
    public void setValueDate62F(LocalDate valueDate62F) {
        this.valueDate62F = valueDate62F;
    }

    public String getDebitCredit62F() {
        return debitCredit62F;
    }
    public void setDebitCredit62F(String debitCredit62F) {
        this.debitCredit62F = debitCredit62F;
    }

    public String getCurrency62F() {
        return currency62F;
    }
    public void setCurrency62F(String currency62F) {
        this.currency62F = currency62F;
    }

    public BigDecimal getClosingBalance62F() {
        return closingBalance62F;
    }
    public void setClosingBalance62F(BigDecimal closingBalance62F) {
        this.closingBalance62F = closingBalance62F;
    }

    public BigDecimal getAvailableBalance64() {
        return availableBalance64;
    }
    public void setAvailableBalance64(BigDecimal availableBalance64) {
        this.availableBalance64 = availableBalance64;
    }

    public String getDebitCredit64() {
        return debitCredit64;
    }
    public void setDebitCredit64(String debitCredit64) {
        this.debitCredit64 = debitCredit64;
    }

    public LocalDate getPreviousOpenValueDate60F() {
        return previousOpenValueDate60F;
    }
    public void setPreviousOpenValueDate60F(LocalDate previousOpenValueDate60F) {
        this.previousOpenValueDate60F = previousOpenValueDate60F;
    }

    public String getPreviousOpenCreditDebit60F() {
        return previousOpenCreditDebit60F;
    }
    public void setPreviousOpenCreditDebit60F(String previousOpenCreditDebit60F) {
        this.previousOpenCreditDebit60F = previousOpenCreditDebit60F;
    }

    public String getPreviousOpenCurrency60F() {
        return previousOpenCurrency60F;
    }
    public void setPreviousOpenCurrency60F(String previousOpenCurrency60F) {
        this.previousOpenCurrency60F = previousOpenCurrency60F;
    }

    public BigDecimal getPreviousOpenBalance60F() {
        return previousOpenBalance60F;
    }
    public void setPreviousOpenBalance60F(BigDecimal previousOpenBalance60F) {
        this.previousOpenBalance60F = previousOpenBalance60F;
    }

    public LocalDate getTransactionValueDate() {
        return transactionValueDate;
    }
    public void setTransactionValueDate(LocalDate transactionValueDate) {
        this.transactionValueDate = transactionValueDate;
    }

    public LocalDate getTransactionEntryDate() {
        return transactionEntryDate;
    }
    public void setTransactionEntryDate(LocalDate transactionEntryDate) {
        this.transactionEntryDate = transactionEntryDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }
    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionFCode() {
        return transactionFCode;
    }
    public void setTransactionFCode(String transactionFCode) {
        this.transactionFCode = transactionFCode;
    }
}
