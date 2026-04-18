package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DDriverBankAccEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.toolinsert.entity.id.DDriverBankAccId;

public interface DriverBankAccountRepository extends JpaRepository<DDriverBankAccEntity, DDriverBankAccId> {
}
