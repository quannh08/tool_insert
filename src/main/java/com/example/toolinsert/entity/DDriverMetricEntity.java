package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entity đại diện cho bảng D_DRIVER_METRIC
 *
 * Mô tả: Bảng lưu trữ các chỉ số/metrics của tài xế
 *
 * Ý nghĩa: Bảng này quản lý các chỉ số đánh giá hiệu suất và chất lượng dịch vụ của tài xế như số chuyến đã hoàn
 * thành, tỷ lệ đánh giá tốt, số km đã đi, số giờ làm việc, v.v. Các chỉ số này được sử dụng để đánh giá tài xế, phân
 * loại tài xế vào các lớp khác nhau (VIP, thường), tính toán thưởng và đưa ra quyết định quản lý. Mỗi chỉ số được định
 * nghĩa trong bảng D_CRITERIA và có giá trị cùng đơn vị tính.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "D_DRIVER_METRIC")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverMetricEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_driver_metric_seq")
    @SequenceGenerator(name = "d_driver_metric_seq", sequenceName = "SEQ_D_DRIVER_METRIC", allocationSize = 1)
    @Column(name = "DRIVER_METRIC_ID")
    Long driverMetricId;

    /**
     * ID tài xế
     */
    @Column(name = "DRIVER_ID")
    Long driverId;

    /**
     * ID tiêu chí
     */
    @Column(name = "CRITERIA_ID")
    Integer criteriaId;

    /**
     * Giá trị chỉ số
     */
    @Column(name = "VALUE")
    BigDecimal value;

    /**
     * Đơn vị tính
     */
    @Column(name = "UNIT")
    String unit;
}
