package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.toolinsert.entity.id.DDriverPropertyId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_DRIVER_PROPERTY
 *
 * Mô tả: Bảng lưu trữ các thuộc tính động của tài xế (key-value pairs)
 *
 * Ý nghĩa: Bảng này quản lý các thuộc tính mở rộng của tài xế theo mô hình key-value, cho phép lưu trữ các thông
 * tin không cố định như email, địa chỉ, loại bằng lái, URL ảnh giấy phép lái xe, URL ảnh CCCD, mật khẩu đã hash, v.v.
 * Bảng này linh hoạt hơn so với việc tạo các cột cố định, cho phép thêm thuộc tính mới mà không cần thay đổi cấu trúc
 * bảng. Mỗi thuộc tính được định nghĩa trong bảng D_PROPERTY và có thể lưu giá trị dạng text, number, date, CLOB (cho
 * dữ liệu lớn như ảnh) hoặc file path.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_DRIVER_PROPERTY",
        indexes = {
            @Index(name = "idx_driver_property_driver", columnList = "DRIVER_ID"),
            @Index(name = "idx_driver_property_code", columnList = "PROPERTY_ID"),
            @Index(name = "idx_driver_property_status", columnList = "STATUS"),
            @Index(name = "idx_driver_property_driver_code", columnList = "DRIVER_ID, PROPERTY_ID", unique = true)
        })
@IdClass(DDriverPropertyId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverPropertyEntity implements Serializable {
    @Id
    @Column(name = "PROPERTY_ID")
    Integer propertyId;

    /**
     * ID tài xế
     */
    @Id
    @Column(name = "DRIVER_ID")
    Long driverId;

    /**
     * Giá trị dạng text (email, address, license type, etc.)
     */
    @Column(name = "VALUE_TEXT")
    String valueText;

    /**
     * Giá trị dạng số (province_id, district_id, etc.)
     */
    @Column(name = "VALUE_NUMBER")
    Long valueNumber;

    /**
     * Giá trị dạng ngày tháng
     */
    @Column(name = "VALUE_DATE")
    LocalDateTime valueDate;

    /**
     * Giá trị dạng CLOB - dùng cho dữ liệu lớn như JSON text hoặc metadata
     */
    @Lob
    @Column(name = "VALUE_CLOB")
    String valueClob;

    /**
     * Đường dẫn file hoặc tên file
     */
    @Column(name = "VALUE_FILE")
    String valueFile;

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

    /**
     * Status: 1-Active, 0-Inactive
     * Dùng để soft delete - khi xóa property, set status = 0 thay vì xóa bản ghi
     */
    @Column(name = "STATUS")
    Integer status;
}
