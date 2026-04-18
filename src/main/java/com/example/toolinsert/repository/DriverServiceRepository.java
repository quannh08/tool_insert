package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DDriverServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.toolinsert.entity.id.DDriverServiceId;

public interface DriverServiceRepository extends JpaRepository<DDriverServiceEntity, DDriverServiceId> {
}
