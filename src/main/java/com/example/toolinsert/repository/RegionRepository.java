package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DRegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<DRegionEntity, Long> {
}
