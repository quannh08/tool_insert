package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_SERVICE
 *
 * Mô tả: Bảng định nghĩa các dịch vụ trong hệ thống
 *
 * Ý nghĩa: Bảng này là bảng master định nghĩa các loại dịch vụ mà hệ thống cung cấp, ví dụ: "Lái xe ô tô hộ",
 * "Lái xe máy hộ", "Giao hàng", v.v. Mỗi dịch vụ có mã (code) và tên (name) riêng. Bảng này kết hợp với bảng
 * D_DRIVER_SERVICE để quản lý các dịch vụ mà từng tài xế có thể cung cấp. Việc tách riêng bảng này giúp quản lý tập trung
 * danh mục dịch vụ, dễ dàng thêm/sửa/xóa dịch vụ mà không ảnh hưởng đến dữ liệu tài xế.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_SERVICE",
        indexes = {
            @Index(name = "idx_service_status", columnList = "STATUS"),
            @Index(name = "idx_service_code", columnList = "CODE", unique = true)
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DServiceEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_service_seq")
    @SequenceGenerator(name = "d_service_seq", sequenceName = "SEQ_D_SERVICE", allocationSize = 1)
    @Column(name = "SERVICE_ID")
    Integer serviceId;

    /**
     * Mã dịch vụ
     */
    @Column(name = "CODE")
    String code;

    /**
     * Tên dịch vụ
     */
    @Column(name = "NAME")
    String name;

    /**
     * Status: 1-Active, 0-Inactive
     */
    @Column(name = "STATUS")
    Integer status;

    /**
     * Thời gian tạo
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
     * Người tạo
     */
    @Column(name = "CREATOR")
    String creator;

    /**
     * Người cập nhật
     */
    @Column(name = "MODIFIER")
    String modifier;
}
