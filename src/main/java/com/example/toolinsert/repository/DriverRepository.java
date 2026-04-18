package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DDriverEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<DDriverEntity, Long> {

    Optional<DDriverEntity> findByIdentityNumber(String identityNumber);

    Optional<DDriverEntity> findByFullNameAndDob(String fullName, LocalDateTime dob);
}
