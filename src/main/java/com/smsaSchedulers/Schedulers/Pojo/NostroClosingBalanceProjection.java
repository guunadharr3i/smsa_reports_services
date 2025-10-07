/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Pojo;


import java.math.BigDecimal;

import java.time.LocalDate;

public interface NostroClosingBalanceProjection {

    String getLocation();

    String getSenderBic();

    String getReceiverBic();

    Integer getMessageType();

    String getNostroAccountNumber();

    String getSenderBankName();

    LocalDate getStatementReceivedDate();

    BigDecimal getAmount();

    String getCurrency();

    String getValueDate();

    String getDebitCredit();
}
