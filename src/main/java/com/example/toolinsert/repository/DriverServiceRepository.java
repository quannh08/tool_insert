package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DriverServiceEntity;
import com.example.toolinsert.entity.id.DriverServiceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverServiceRepository extends JpaRepository<DriverServiceEntity, DriverServiceId> {
}
