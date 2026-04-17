package com.example.toolinsert.repository;

import com.example.toolinsert.entity.PartyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<PartyEntity, Long> {

    Optional<PartyEntity> findByPhone(String phone);
}
