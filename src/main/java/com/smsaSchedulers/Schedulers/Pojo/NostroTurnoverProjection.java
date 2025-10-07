/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface NostroTurnoverProjection {

    Long getSmsaMessageId();

    String getSmsaMsgIo();

    String getSmsaGeoId();

    String getAccountNumber();

    String getNostroName();

    Integer getMessageType();

    String getSenderBic();

    String getReceiverBic();

    LocalDateTime getSendRecTime();

    String getCurrency();

    String getValueDate();

    String getEntryDate();

    String getCode();

    String getReferenceNumber();

    BigDecimal getAmount();

    String getDebitCredit();
}
