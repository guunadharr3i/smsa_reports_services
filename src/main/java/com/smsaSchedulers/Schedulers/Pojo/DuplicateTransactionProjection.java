/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Pojo;

public interface DuplicateTransactionProjection {

    String getIdentifier();

    String getSender();

    String getReceiver();

    String getMessageType();

    String getReferenceNo();

    String getRelatedReferenceNo();

    String getFileType();

    String getValueDate();

    String getCurrency();

    Double getAmount();

    String getLocation();

    String getSendRecDate();

    String getSendRecTime();

    String getMirMor();
}
