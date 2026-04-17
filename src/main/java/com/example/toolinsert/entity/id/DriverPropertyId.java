package com.example.toolinsert.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DriverPropertyId implements Serializable {

    @Column(name = "DRIVER_ID")
    private Long driverId;

    @Column(name = "PROPERTY_ID")
    private Long propertyId;

    protected DriverPropertyId() {
    }

    public DriverPropertyId(Long driverId, Long propertyId) {
        this.driverId = driverId;
        this.propertyId = propertyId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DriverPropertyId that)) {
            return false;
        }
        return Objects.equals(driverId, that.driverId) && Objects.equals(propertyId, that.propertyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, propertyId);
    }
}
