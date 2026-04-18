package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DBankAccEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<DBankAccEntity, Long> {

    Optional<DBankAccEntity> findByBankIdAndAccNumber(Integer bankId, String accNumber);
}
