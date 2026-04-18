package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DDriverPropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.toolinsert.entity.id.DDriverPropertyId;

public interface DriverPropertyRepository extends JpaRepository<DDriverPropertyEntity, DDriverPropertyId> {
}
