package com.example.toolinsert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "D_DRIVER")
public class DriverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DRIVER_ID")
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGION_ID")
    private RegionEntity region;

    @Column(name = "IDENTITY_NUMBER", length = 64)
    private String identityNumber;

    @Column(name = "FULL_NAME", length = 255, nullable = false)
    private String fullName;

    @Column(name = "DOB")
    private LocalDate dob;

    @Column(name = "GENDER", length = 32)
    private String gender;

    @Column(name = "STATUS", length = 64)
    private String status;

    @Column(name = "LASTEST_LAT", precision = 10, scale = 7)
    private BigDecimal latestLat;

    @Column(name = "LASTEST_LON", precision = 10, scale = 7)
    private BigDecimal latestLon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFERRER_ID")
    private DriverEntity referrer;

    public DriverEntity() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public RegionEntity getRegion() {
        return region;
    }

    public void setRegion(RegionEntity region) {
        this.region = region;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getLatestLat() {
        return latestLat;
    }

    public void setLatestLat(BigDecimal latestLat) {
        this.latestLat = latestLat;
    }

    public BigDecimal getLatestLon() {
        return latestLon;
    }

    public void setLatestLon(BigDecimal latestLon) {
        this.latestLon = latestLon;
    }

    public DriverEntity getReferrer() {
        return referrer;
    }

    public void setReferrer(DriverEntity referrer) {
        this.referrer = referrer;
    }
}
