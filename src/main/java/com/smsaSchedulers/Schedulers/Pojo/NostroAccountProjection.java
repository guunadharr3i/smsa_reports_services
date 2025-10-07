/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Pojo;

import java.time.LocalDate;

public interface NostroAccountProjection {
    String getSmsaFileName();
    Long getSmsaMessageId();
    String getSmsaMsgIo();
    String getMessageType();
    String getSmsaSenderBic();
    String getSmsaReceiverBic();
    String getAccountNumber();
    String getNostroName();
    LocalDate getStatementReceivedDate();
    String getTransactionCode();
    String getValueDate62f();
    String getDebitCredit62f();
    String getCurrency62f();
    String getClosingBalance62f();
    String getAvailableBalance64();
    String getDebitCredit64();
    String getPrevOpenValdate60f();
    String getPrevOpenCreditDebit60f();
    String getPrevOpenCurrency60f();
    String getPrevOpenBalance60f();
}

