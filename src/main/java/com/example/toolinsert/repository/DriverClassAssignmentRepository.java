package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DriverClassAssignmentEntity;
import com.example.toolinsert.entity.id.DriverClassAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverClassAssignmentRepository extends JpaRepository<DriverClassAssignmentEntity, DriverClassAssignmentId> {
}
