package com.example.toolinsert.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DriverServiceId implements Serializable {

    @Column(name = "DRIVER_ID")
    private Long driverId;

    @Column(name = "SERVICE_ID")
    private Long serviceId;

    protected DriverServiceId() {
    }

    public DriverServiceId(Long driverId, Long serviceId) {
        this.driverId = driverId;
        this.serviceId = serviceId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DriverServiceId that)) {
            return false;
        }
        return Objects.equals(driverId, that.driverId) && Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, serviceId);
    }
}
