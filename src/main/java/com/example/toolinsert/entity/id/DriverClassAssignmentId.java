package com.example.toolinsert.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DriverClassAssignmentId implements Serializable {

    @Column(name = "DRIVER_ID")
    private Long driverId;

    @Column(name = "CLASS_ID")
    private Long classId;

    @Column(name = "SERVICE_ID")
    private Long serviceId;

    protected DriverClassAssignmentId() {
    }

    public DriverClassAssignmentId(Long driverId, Long classId, Long serviceId) {
        this.driverId = driverId;
        this.classId = classId;
        this.serviceId = serviceId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Long getClassId() {
        return classId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DriverClassAssignmentId that)) {
            return false;
        }
        return Objects.equals(driverId, that.driverId)
                && Objects.equals(classId, that.classId)
                && Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, classId, serviceId);
    }
}
