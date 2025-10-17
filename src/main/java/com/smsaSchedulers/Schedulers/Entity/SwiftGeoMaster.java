package com.smsaSchedulers.Schedulers.Entity;

import javax.persistence.*;


@Entity
@Table(name = "SMSA_GEO_MST")
public class SwiftGeoMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "geo_seq")
    @SequenceGenerator(name = "geo_seq", sequenceName = "SMSA_GEOGRAPHY_SEQ", allocationSize = 1)
    @Column(name = "GEOG_ID")
    private Long geogId;

    @Column(name = "GEOG_CODE", nullable = false, length = 50)
    private String geogCode;

    @Column(name = "GEOG_NAME", nullable = false, length = 100)
    private String geogName;

    /**
     * @return the geogId
     */
    public Long getGeogId() {
        return geogId;
    }

    /**
     * @param geogId the geogId to set
     */
    public void setGeogId(Long geogId) {
        this.geogId = geogId;
    }

    /**
     * @return the geogCode
     */
    public String getGeogCode() {
        return geogCode;
    }

    /**
     * @param geogCode the geogCode to set
     */
    public void setGeogCode(String geogCode) {
        this.geogCode = geogCode;
    }

    /**
     * @return the geogName
     */
    public String getGeogName() {
        return geogName;
    }

    /**
     * @param geogName the geogName to set
     */
    public void setGeogName(String geogName) {
        this.geogName = geogName;
    }

   
}
