package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DDriverPartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.cmsdriversservice.entity.id.DDriverPartyId;

public interface DriverPartyRepository extends JpaRepository<DDriverPartyEntity, DDriverPartyId> {
}
