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
public class DDriverBankAccId implements Serializable {

    Long driverId;

    Long bankAccId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DDriverBankAccId that = (DDriverBankAccId) o;
        return Objects.equals(driverId, that.driverId) && Objects.equals(bankAccId, that.bankAccId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, bankAccId);
    }
}
