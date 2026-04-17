package com.example.toolinsert.entity;

import com.example.toolinsert.entity.id.DriverClassAssignmentId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "D_DRIVER_CLASS")
public class DriverClassAssignmentEntity {

    @EmbeddedId
    private DriverClassAssignmentId id;

    @MapsId("driverId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_ID")
    private DriverEntity driver;

    @MapsId("classId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASS_ID")
    private DriverClassEntity driverClass;

    @MapsId("serviceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_ID")
    private ServiceEntity service;

    @Column(name = "BONUS_RATE", precision = 18, scale = 4)
    private BigDecimal bonusRate;

    public DriverClassAssignmentEntity() {
    }

    public DriverClassAssignmentId getId() {
        return id;
    }

    public void setId(DriverClassAssignmentId id) {
        this.id = id;
    }

    public DriverEntity getDriver() {
        return driver;
    }

    public void setDriver(DriverEntity driver) {
        this.driver = driver;
    }

    public DriverClassEntity getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(DriverClassEntity driverClass) {
        this.driverClass = driverClass;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public BigDecimal getBonusRate() {
        return bonusRate;
    }

    public void setBonusRate(BigDecimal bonusRate) {
        this.bonusRate = bonusRate;
    }
}
