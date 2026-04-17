package com.example.toolinsert.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DriverDeviceId implements Serializable {

    @Column(name = "DRIVER_ID")
    private Long driverId;

    @Column(name = "DEVICE_ID")
    private Long deviceId;

    protected DriverDeviceId() {
    }

    public DriverDeviceId(Long driverId, Long deviceId) {
        this.driverId = driverId;
        this.deviceId = deviceId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DriverDeviceId that)) {
            return false;
        }
        return Objects.equals(driverId, that.driverId) && Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, deviceId);
    }
}
