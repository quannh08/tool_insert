package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DPartyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<DPartyEntity, Long> {

    Optional<DPartyEntity> findByPhone(String phone);
}
