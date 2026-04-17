package com.example.toolinsert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "D_BANK_ACC",
        uniqueConstraints = @UniqueConstraint(name = "UK_D_BANK_ACC_BANK_NO", columnNames = {"BANK_ID", "ACC_NUMBER"})
)
public class BankAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANK_ACC_ID")
    private Long id;

    @Column(name = "BANK_ID")
    private Long bankId;

    @Column(name = "ACC_NUMBER", length = 64)
    private String accountNumber;

    @Column(name = "ACC_NAME", length = 255)
    private String accountName;

    public BankAccountEntity() {
    }

    public Long getId() {
        return id;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
