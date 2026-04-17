package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DriverBankAccountEntity;
import com.example.toolinsert.entity.id.DriverBankAccountId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverBankAccountRepository extends JpaRepository<DriverBankAccountEntity, DriverBankAccountId> {
}
