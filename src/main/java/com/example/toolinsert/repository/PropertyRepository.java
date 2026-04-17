package com.example.toolinsert.repository;

import com.example.toolinsert.entity.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<PropertyEntity, Long> {
}
