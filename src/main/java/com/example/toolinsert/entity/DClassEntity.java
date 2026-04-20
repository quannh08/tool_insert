package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_CLASS
 *
 * Mô tả: Bảng định nghĩa các lớp/cấp độ dịch vụ trong hệ thống
 *
 * Ý nghĩa: Bảng này quản lý các lớp dịch vụ như VIP, Premium, Standard, v.v. Mỗi lớp có tỷ lệ thưởng (bonusRate)
 * riêng. Bảng này kết hợp với bảng D_DRIVER_CLASS để phân loại tài xế vào các lớp khác nhau cho từng dịch vụ, giúp tính
 * toán thù lao và ưu đãi dựa trên hiệu suất và cấp độ dịch vụ của tài xế. Tài xế có thể được nâng cấp hoặc hạ cấp lớp
 * dựa trên các chỉ số/metrics trong bảng D_DRIVER_METRIC.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_CLASS",
        indexes = {
            @Index(name = "idx_class_status", columnList = "STATUS"),
            @Index(name = "idx_class_bonus_rate", columnList = "BONUS_RATE"),
            @Index(name = "idx_class_status_bonus_rate", columnList = "STATUS, BONUS_RATE")
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DClassEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_class_seq")
    @SequenceGenerator(name = "d_class_seq", sequenceName = "SEQ_D_CLASS", allocationSize = 1)
    @Column(name = "CLASS_ID")
    Integer classId;

    /**
     * Mã lớp
     */
    @Column(name = "CODE", nullable = false)
    String code;

    /**
     * Tên lớp
     */
    @Column(name = "NAME", nullable = false)
    String name;

    /**
     * Tỷ lệ thưởng
     */
    @Column(name = "BONUS_RATE", nullable = false)
    BigDecimal bonusRate;

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
