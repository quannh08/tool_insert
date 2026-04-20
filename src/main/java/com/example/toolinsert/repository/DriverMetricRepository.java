package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DDriverMetricEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverMetricRepository extends JpaRepository<DDriverMetricEntity, Long> {

    Optional<DDriverMetricEntity> findByDriverIdAndCriteriaIdAndServiceId(Long driverId, Integer criteriaId, Integer serviceId);

    Optional<DDriverMetricEntity> findByDriverIdAndCriteriaIdAndServiceIdIsNull(Long driverId, Integer criteriaId);
}
