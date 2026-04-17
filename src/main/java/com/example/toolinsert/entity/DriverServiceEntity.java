package com.example.toolinsert.entity;

import com.example.toolinsert.entity.id.DriverServiceId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "D_DRIVER_SERVICE")
public class DriverServiceEntity {

    @EmbeddedId
    private DriverServiceId id;

    @MapsId("driverId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_ID")
    private DriverEntity driver;

    @MapsId("serviceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_ID")
    private ServiceEntity service;

    public DriverServiceEntity() {
    }

    public DriverServiceId getId() {
        return id;
    }

    public void setId(DriverServiceId id) {
        this.id = id;
    }

    public DriverEntity getDriver() {
        return driver;
    }

    public void setDriver(DriverEntity driver) {
        this.driver = driver;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }
}
