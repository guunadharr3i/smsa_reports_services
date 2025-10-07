/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Entity;

import java.time.LocalDate;
import java.util.List;

public class NostroClosingBalanceFilter {

    private LocalDate fromDate;
    private LocalDate toDate;
    private List<Integer> mtCodes;
    private List<String> geoIds;
    private String accountNumber;
    private String currency;

    // Getters & Setters
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public List<Integer> getMtCodes() {
        return mtCodes;
    }

    public void setMtCodes(List<Integer> mtCodes) {
        this.mtCodes = mtCodes;
    }

    public List<String> getGeoIds() {
        return geoIds;
    }

    public void setGeoIds(List<String> geoIds) {
        this.geoIds = geoIds;
    }


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber the accountNumber to set
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
