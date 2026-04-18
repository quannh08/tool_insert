package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_DRIVER_DOCUMENT_APPROVAL
 *
 * Mô tả: Bảng lưu trữ trạng thái phê duyệt và lịch sử phê duyệt tài liệu của tài xế
 *
 * Ý nghĩa: Bảng này quản lý workflow phê duyệt tài liệu (CCCD, bằng lái xe) của tài xế.
 * Mỗi khi tài xế upload hoặc cập nhật tài liệu, một bản ghi mới sẽ được tạo với trạng thái PENDING.
 * Khi admin phê duyệt hoặc từ chối, trạng thái sẽ được cập nhật và lưu lại lịch sử.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_DRIVER_DOCUMENT_APPROVAL",
        indexes = {
            @Index(name = "idx_doc_approval_driver", columnList = "DRIVER_ID"),
            @Index(name = "idx_doc_approval_status", columnList = "STATUS"),
            @Index(name = "idx_doc_approval_type", columnList = "DOCUMENT_TYPE"),
            @Index(name = "idx_doc_approval_driver_status", columnList = "DRIVER_ID, STATUS"),
            @Index(name = "idx_doc_approval_driver_type_status", columnList = "DRIVER_ID, DOCUMENT_TYPE, STATUS"),
            @Index(name = "idx_doc_approval_status_created", columnList = "STATUS, CREATED"),
            @Index(name = "idx_doc_approval_approved_at", columnList = "APPROVED_AT")
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverDocumentApprovalEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_driver_doc_approval_seq")
    @SequenceGenerator(
            name = "d_driver_doc_approval_seq",
            sequenceName = "SEQ_D_DRIVER_DOCUMENT_APPROVAL",
            allocationSize = 1)
    @Column(name = "APPROVAL_ID")
    Long approvalId;

    /**
     * ID tài xế
     */
    @Column(name = "DRIVER_ID")
    Long driverId;

    /**
     * Mã tài liệu (property code): CCCD_FRONT_IMAGE, CCCD_BACK_IMAGE, MOTORCYCLE_LICENSE_FRONT_IMAGE,
     * MOTORCYCLE_LICENSE_BACK_IMAGE, CAR_LICENSE_FRONT_IMAGE, CAR_LICENSE_BACK_IMAGE,
     * DRUG_TEST_CERTIFICATE, HIV_TEST_CERTIFICATE, CRIMINAL_RECORD_CERTIFICATE
     */
    @Column(name = "DOCUMENT_TYPE")
    String documentType;

    /**
     * Trạng thái phê duyệt:
     * 0 - PENDING (Chờ phê duyệt)
     * 1 - APPROVED (Đã phê duyệt)
     * 2 - REJECTED (Đã từ chối)
     */
    @Column(name = "STATUS")
    Integer status;

    /**
     * Lý do từ chối (nếu status = REJECTED)
     */
    @Column(name = "REJECTION_REASON")
    String rejectionReason;

    /**
     * Người phê duyệt/từ chối (admin user ID hoặc username)
     */
    @Column(name = "APPROVED_BY")
    String approvedBy;

    /**
     * Thời gian phê duyệt/từ chối
     */
    @Column(name = "APPROVED_AT")
    LocalDateTime approvedAt;

    /**
     * Ghi chú của người phê duyệt
     */
    @Column(name = "APPROVER_NOTES")
    String approverNotes;

    /**
     * Thời gian tạo (khi upload tài liệu)
     */
    @CreationTimestamp
    @Column(name = "CREATED")
    LocalDateTime created;

    /**
     * Thời gian cập nhật
     */
    @UpdateTimestamp
    @Column(name = "MODIFIED")
    LocalDateTime modified;

    /**
     * Người tạo (tài xế upload)
     */
    @Column(name = "CREATOR")
    String creator;

    /**
     * Người cập nhật
     */
    @Column(name = "MODIFIER")
    String modifier;
}
