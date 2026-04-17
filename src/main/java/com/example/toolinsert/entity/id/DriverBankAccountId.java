package com.example.toolinsert.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DriverBankAccountId implements Serializable {

    @Column(name = "DRIVER_ID")
    private Long driverId;

    @Column(name = "BANK_ACC_ID")
    private Long bankAccountId;

    protected DriverBankAccountId() {
    }

    public DriverBankAccountId(Long driverId, Long bankAccountId) {
        this.driverId = driverId;
        this.bankAccountId = bankAccountId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DriverBankAccountId that)) {
            return false;
        }
        return Objects.equals(driverId, that.driverId) && Objects.equals(bankAccountId, that.bankAccountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, bankAccountId);
    }
}
