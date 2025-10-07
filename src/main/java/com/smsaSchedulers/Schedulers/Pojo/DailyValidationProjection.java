/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Pojo;

import java.time.LocalDate;

/**
 *
 * @author abcom
 */
public interface DailyValidationProjection {
    Long getSerialNo();
    LocalDate getSendRecDate();
    String getIoFlag();
    Integer getMessageType();
    String getSender();
    String getReceiver();
    String getTransactionReference();
    String getAccountNo();
    String getCurrency();
    String getGeography();
    String getTeam();
    String getRemark();
}
