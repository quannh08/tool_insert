package com.example.toolinsert.entity.id;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverPartyId implements Serializable {

    Long driverId;

    Long partyId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DDriverPartyId that = (DDriverPartyId) o;
        return Objects.equals(driverId, that.driverId) && Objects.equals(partyId, that.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, partyId);
    }
}
