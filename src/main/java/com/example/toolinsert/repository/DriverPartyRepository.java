package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DriverPartyEntity;
import com.example.toolinsert.entity.DriverEntity;
import com.example.toolinsert.entity.id.DriverPartyId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DriverPartyRepository extends JpaRepository<DriverPartyEntity, DriverPartyId> {

    @Query("""
            select dp.driver
            from DriverPartyEntity dp
            join dp.party party
            where party.phone = :phone
            """)
    Optional<DriverEntity> findDriverByPhone(@Param("phone") String phone);
}
