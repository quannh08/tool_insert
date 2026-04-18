package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DDriverBankAccEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.cmsdriversservice.entity.id.DDriverBankAccId;

public interface DriverBankAccountRepository extends JpaRepository<DDriverBankAccEntity, DDriverBankAccId> {
}
