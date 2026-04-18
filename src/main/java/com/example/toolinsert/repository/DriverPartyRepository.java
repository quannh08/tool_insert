package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DDriverPartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.toolinsert.entity.id.DDriverPartyId;

public interface DriverPartyRepository extends JpaRepository<DDriverPartyEntity, DDriverPartyId> {
}
