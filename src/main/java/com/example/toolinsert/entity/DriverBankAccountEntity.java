package com.example.toolinsert.entity;

import com.example.toolinsert.entity.id.DriverBankAccountId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "D_DRIVER_BANK_ACC")
public class DriverBankAccountEntity {

    @EmbeddedId
    private DriverBankAccountId id;

    @MapsId("driverId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_ID")
    private DriverEntity driver;

    @MapsId("bankAccountId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANK_ACC_ID")
    private BankAccountEntity bankAccount;

    @Column(name = "STATUS", length = 64)
    private String status;

    public DriverBankAccountEntity() {
    }

    public DriverBankAccountId getId() {
        return id;
    }

    public void setId(DriverBankAccountId id) {
        this.id = id;
    }

    public DriverEntity getDriver() {
        return driver;
    }

    public void setDriver(DriverEntity driver) {
        this.driver = driver;
    }

    public BankAccountEntity getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccountEntity bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
