package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DriverMetricEntity;
import com.example.toolinsert.entity.id.DriverMetricId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverMetricRepository extends JpaRepository<DriverMetricEntity, DriverMetricId> {
}
