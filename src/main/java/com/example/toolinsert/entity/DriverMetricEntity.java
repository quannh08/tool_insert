package com.example.toolinsert.entity;

import com.example.toolinsert.entity.id.DriverMetricId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "D_DRIVER_METRIC")
public class DriverMetricEntity {

    @EmbeddedId
    private DriverMetricId id;

    @MapsId("driverId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_ID")
    private DriverEntity driver;

    @MapsId("criteriaId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CRITERIA_ID")
    private CriteriaEntity criteria;

    // Only one value column should be populated for each metric record.
    @Column(name = "VALUE_NUMBER", precision = 18, scale = 4)
    private BigDecimal valueNumber;

    @Column(name = "VALUE_TEXT", length = 1000)
    private String valueText;

    @Column(name = "VALUE_DATE")
    private LocalDate valueDate;

    public DriverMetricEntity() {
    }

    public DriverMetricId getId() {
        return id;
    }

    public void setId(DriverMetricId id) {
        this.id = id;
    }

    public DriverEntity getDriver() {
        return driver;
    }

    public void setDriver(DriverEntity driver) {
        this.driver = driver;
    }

    public CriteriaEntity getCriteria() {
        return criteria;
    }

    public void setCriteria(CriteriaEntity criteria) {
        this.criteria = criteria;
    }

    public BigDecimal getValueNumber() {
        return valueNumber;
    }

    public void setValueNumber(BigDecimal valueNumber) {
        this.valueNumber = valueNumber;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }
}
