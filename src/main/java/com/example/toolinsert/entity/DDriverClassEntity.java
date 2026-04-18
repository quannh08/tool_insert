package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.toolinsert.entity.id.DDriverClassId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_DRIVER_CLASS
 *
 * Mô tả: Bảng liên kết giữa tài xế, lớp (class) và dịch vụ với tỷ lệ thưởng
 *
 * Ý nghĩa: Bảng này quản lý mối quan hệ phức tạp giữa tài xế, lớp dịch vụ và dịch vụ cụ thể. Mỗi tài xế có thể
 * thuộc nhiều lớp khác nhau cho các dịch vụ khác nhau (ví dụ: lớp VIP cho dịch vụ lái xe ô tô hộ, lớp thường cho dịch vụ
 * lái xe máy hộ). Bảng này lưu tỷ lệ thưởng (bonusRate) cho từng kết hợp tài xế-class-service, giúp tính toán thù lao
 * và ưu đãi cho tài xế dựa trên hiệu suất và cấp độ dịch vụ.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_DRIVER_CLASS",
        indexes = {
            @Index(name = "idx_driver_class_driver", columnList = "DRIVER_ID"),
            @Index(name = "idx_driver_class_class", columnList = "CLASS_ID"),
            @Index(name = "idx_driver_class_service", columnList = "SERVICE_ID"),
            @Index(name = "idx_driver_class_driver_class_service", columnList = "DRIVER_ID, CLASS_ID, SERVICE_ID")
        })
@IdClass(DDriverClassId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverClassEntity implements Serializable {
    @Id
    @Column(name = "DRIVER_ID")
    Long driverId;

    @Id
    @Column(name = "CLASS_ID")
    Integer classId;

    @Id
    @Column(name = "SERVICE_ID")
    Integer serviceId;

    /**
     * Tỷ lệ thưởng
     */
    @Column(name = "BONUS_RATE")
    BigDecimal bonusRate;

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
