package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entity đại diện cho bảng D_CLASS_CONFIG
 *
 * Mô tả: Bảng cấu hình điều kiện để tài xế được phân vào các lớp
 *
 * Ý nghĩa: Bảng này định nghĩa các điều kiện (ngưỡng giá trị min/max) cho từng tiêu chí (criteria) để một tài xế
 * có thể được phân vào một lớp (class) cụ thể. Ví dụ: để vào lớp VIP, tài xế cần có "Số chuyến đã hoàn thành" >= 1000,
 * "Tỷ lệ đánh giá tốt" >= 4.5/5. Bảng này giúp tự động hóa việc phân loại tài xế dựa trên các chỉ số/metrics, đảm bảo
 * tính công bằng và minh bạch trong việc đánh giá và phân loại tài xế.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "D_CLASS_CONFIG")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DClassConfigEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_class_config_seq")
    @SequenceGenerator(name = "d_class_config_seq", sequenceName = "SEQ_D_CLASS_CONFIG", allocationSize = 1)
    @Column(name = "CONFIG_ID")
    Integer configId;

    /**
     * ID lớp (Class ID)
     */
    @Column(name = "CLASS_ID")
    Integer classId;

    /**
     * ID tiêu chí (Criteria ID)
     */
    @Column(name = "CRITERIA_ID")
    Integer criteriaId;

    /**
     * Giá trị tối thiểu
     */
    @Column(name = "MIN_VALUE")
    BigDecimal minValue;

    /**
     * Giá trị tối đa
     */
    @Column(name = "MAX_VALUE")
    BigDecimal maxValue;

    /**
     * Đơn vị tính
     */
    @Column(name = "UNIT")
    String unit;
}
