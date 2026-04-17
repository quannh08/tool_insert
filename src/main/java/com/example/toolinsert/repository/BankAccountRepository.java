package com.example.toolinsert.repository;

import com.example.toolinsert.entity.BankAccountEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {

    Optional<BankAccountEntity> findByBankIdAndAccountNumber(Long bankId, String accountNumber);
}
