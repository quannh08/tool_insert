package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DriverEntity;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<DriverEntity, Long> {

    Optional<DriverEntity> findByIdentityNumber(String identityNumber);

    Optional<DriverEntity> findByFullNameAndDob(String fullName, LocalDate dob);
}
