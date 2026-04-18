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
public class DDriverClassId implements Serializable {

    Long driverId;

    Integer classId;

    Integer serviceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DDriverClassId that = (DDriverClassId) o;
        return Objects.equals(driverId, that.driverId)
                && Objects.equals(classId, that.classId)
                && Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, classId, serviceId);
    }
}
