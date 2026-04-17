package com.example.toolinsert.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DriverPartyId implements Serializable {

    @Column(name = "DRIVER_ID")
    private Long driverId;

    @Column(name = "PARTY_ID")
    private Long partyId;

    protected DriverPartyId() {
    }

    public DriverPartyId(Long driverId, Long partyId) {
        this.driverId = driverId;
        this.partyId = partyId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Long getPartyId() {
        return partyId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DriverPartyId that)) {
            return false;
        }
        return Objects.equals(driverId, that.driverId) && Objects.equals(partyId, that.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, partyId);
    }
}
