package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverClassRepository extends JpaRepository<DClassEntity, Integer> {
}
