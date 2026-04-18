package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DPropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<DPropertyEntity, Integer> {
}
