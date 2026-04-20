package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DBankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<DBankEntity, Integer> {
}
