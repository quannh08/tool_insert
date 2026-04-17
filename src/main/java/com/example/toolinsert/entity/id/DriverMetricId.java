package com.example.toolinsert.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DriverMetricId implements Serializable {

    @Column(name = "DRIVER_ID")
    private Long driverId;

    @Column(name = "CRITERIA_ID")
    private Long criteriaId;

    protected DriverMetricId() {
    }

    public DriverMetricId(Long driverId, Long criteriaId) {
        this.driverId = driverId;
        this.criteriaId = criteriaId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Long getCriteriaId() {
        return criteriaId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DriverMetricId that)) {
            return false;
        }
        return Objects.equals(driverId, that.driverId) && Objects.equals(criteriaId, that.criteriaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, criteriaId);
    }
}
