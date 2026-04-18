package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<DServiceEntity, Integer> {
}
