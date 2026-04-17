package com.example.toolinsert.repository;

import com.example.toolinsert.entity.DriverDocumentApprovalEntity;
import com.example.toolinsert.entity.id.DriverDocumentApprovalId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverDocumentApprovalRepository extends JpaRepository<DriverDocumentApprovalEntity, DriverDocumentApprovalId> {
}
