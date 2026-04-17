package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DriverPropertyEntity;
import com.example.toolinsert.entity.id.DriverPropertyId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverPropertyRepository extends JpaRepository<DriverPropertyEntity, DriverPropertyId> {
}
