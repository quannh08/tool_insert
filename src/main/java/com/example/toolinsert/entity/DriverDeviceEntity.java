package com.example.toolinsert.entity;

import com.example.toolinsert.entity.id.DriverDeviceId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "D_DRIVER_DEVICE")
public class DriverDeviceEntity {

    @EmbeddedId
    private DriverDeviceId id;

    @MapsId("driverId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_ID")
    private DriverEntity driver;

    @MapsId("deviceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID")
    private DeviceEntity device;

    protected DriverDeviceEntity() {
    }

    public DriverDeviceId getId() {
        return id;
    }

    public void setId(DriverDeviceId id) {
        this.id = id;
    }

    public DriverEntity getDriver() {
        return driver;
    }

    public void setDriver(DriverEntity driver) {
        this.driver = driver;
    }

    public DeviceEntity getDevice() {
        return device;
    }

    public void setDevice(DeviceEntity device) {
        this.device = device;
    }
}
