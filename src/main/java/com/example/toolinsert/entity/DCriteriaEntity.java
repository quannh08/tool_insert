package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_CRITERIA
 *
 * Mô tả: Bảng định nghĩa các tiêu chí/metrics để đánh giá tài xế
 *
 * Ý nghĩa: Bảng này là bảng master định nghĩa các tiêu chí đánh giá hiệu suất và chất lượng dịch vụ của tài xế,
 * ví dụ: "Số chuyến đã hoàn thành", "Tỷ lệ đánh giá tốt", "Số km đã đi", "Số giờ làm việc", "Tỷ lệ hủy chuyến", v.v.
 * Mỗi tiêu chí có mã (code) và tên (name) riêng. Bảng này kết hợp với bảng D_DRIVER_METRIC để lưu giá trị cụ thể của từng
 * tiêu chí cho từng tài xế. Các tiêu chí này được sử dụng để đánh giá, phân loại tài xế và tính toán thưởng.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "D_CRITERIA")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DCriteriaEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_criteria_seq")
    @SequenceGenerator(name = "d_criteria_seq", sequenceName = "SEQ_D_CRITERIA", allocationSize = 1)
    @Column(name = "CRITERIA_ID")
    Integer criteriaId;

    /**
     * Mã tiêu chí
     */
    @Column(name = "CODE", nullable = false)
    String code;

    /**
     * Tên tiêu chí
     */
    @Column(name = "NAME", nullable = false)
    String name;

    /**
     * Status: 1-Active, 0-Inactive
     */
    @Column(name = "STATUS", nullable = false)
    Integer status;

    /**
     * Thời gian tạo
     */
    @CreationTimestamp
    @Column(name = "CREATED", nullable = false)
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
    @Column(name = "CREATOR", nullable = false)
    String creator;

    /**
     * Người cập nhật
     */
    @Column(name = "MODIFIER")
    String modifier;
}
