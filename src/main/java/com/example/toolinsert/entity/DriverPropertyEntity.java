package com.example.toolinsert.entity;

import com.example.toolinsert.entity.id.DriverPropertyId;
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
@Table(name = "D_DRIVER_PROPERTY")
public class DriverPropertyEntity {

    @EmbeddedId
    private DriverPropertyId id;

    @MapsId("driverId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_ID")
    private DriverEntity driver;

    @MapsId("propertyId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROPERTY_ID")
    private PropertyEntity property;

    // Only one typed value should be used per row, based on the property's field type.
    @Column(name = "VALUE_TEXT", length = 1000)
    private String valueText;

    @Column(name = "VALUE_NUMBER", precision = 18, scale = 4)
    private BigDecimal valueNumber;

    @Column(name = "VALUE_DATE")
    private LocalDate valueDate;

    @Column(name = "VALUE_FILE", length = 512)
    private String valueFile;

    public DriverPropertyEntity() {
    }

    public DriverPropertyId getId() {
        return id;
    }

    public void setId(DriverPropertyId id) {
        this.id = id;
    }

    public DriverEntity getDriver() {
        return driver;
    }

    public void setDriver(DriverEntity driver) {
        this.driver = driver;
    }

    public PropertyEntity getProperty() {
        return property;
    }

    public void setProperty(PropertyEntity property) {
        this.property = property;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public BigDecimal getValueNumber() {
        return valueNumber;
    }

    public void setValueNumber(BigDecimal valueNumber) {
        this.valueNumber = valueNumber;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public String getValueFile() {
        return valueFile;
    }

    public void setValueFile(String valueFile) {
        this.valueFile = valueFile;
    }
}
