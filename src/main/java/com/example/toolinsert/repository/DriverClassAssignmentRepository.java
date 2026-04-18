package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DDriverClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.cmsdriversservice.entity.id.DDriverClassId;

public interface DriverClassAssignmentRepository extends JpaRepository<DDriverClassEntity, DDriverClassId> {
}
