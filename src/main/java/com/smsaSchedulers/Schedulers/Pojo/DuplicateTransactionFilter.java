/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Pojo;

import java.time.LocalDate;
import java.util.List;

public class DuplicateTransactionFilter {

    private List<String> geographies;        // multiple locations
    private String referenceNo;              // txn ref
    private String relatedReferenceNo;       // related ref
    private String currency;                 // currency
    private LocalDate fromDate;              // start date
    private LocalDate toDate;                // end date

    // Getters & Setters
    public List<String> getGeographies() {
        return geographies;
    }

    public void setGeographies(List<String> geographies) {
        this.geographies = geographies;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getRelatedReferenceNo() {
        return relatedReferenceNo;
    }

    public void setRelatedReferenceNo(String relatedReferenceNo) {
        this.relatedReferenceNo = relatedReferenceNo;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

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
}
