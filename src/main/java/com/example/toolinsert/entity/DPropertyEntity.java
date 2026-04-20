package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_PROPERTY
 *
 * Mô tả: Bảng định nghĩa các thuộc tính (properties) có thể được gán cho tài xế
 *
 * Ý nghĩa: Bảng này là bảng master định nghĩa các loại thuộc tính có thể được sử dụng trong hệ thống, ví dụ:
 * EMAIL, PASSWORD, ADDRESS, LICENSE_TYPE, MOTORCYCLE_LICENSE_FRONT_IMAGE, CAR_LICENSE_FRONT_IMAGE, CCCD_FRONT_IMAGE, v.v. Mỗi thuộc tính có các thông tin
 * như mã (code), tên (name), kiểu dữ liệu (fieldTypeId), quy tắc validation, có bắt buộc hay không, có nhạy cảm hay
 * không, v.v. Bảng này kết hợp với bảng D_DRIVER_PROPERTY tạo nên mô hình key-value linh hoạt để lưu trữ các thuộc tính
 * động của tài xế mà không cần thay đổi cấu trúc bảng chính.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_PROPERTY",
        indexes = {
            @Index(name = "idx_property_code", columnList = "CODE", unique = true),
            @Index(name = "idx_property_field_type", columnList = "FIELD_TYPE_ID")
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DPropertyEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_property_seq")
    @SequenceGenerator(name = "d_property_seq", sequenceName = "SEQ_D_PROPERTY", allocationSize = 1)
    @Column(name = "PROPERTY_ID")
    Integer propertyId;

    /**
     * ID loại thuộc tính
     */
    @Column(name = "PROPERTY_TYPE_ID")
    Integer propertyTypeId;

    /**
     * ID nhóm thuộc tính
     */
    @Column(name = "PROPERTY_GROUP_ID")
    Integer propertyGroupId;

    /**
     * ID kiểu trường (Text, Number, Date, etc.)
     */
    @Column(name = "FIELD_TYPE_ID")
    Integer fieldTypeId;

    /**
     * Mã thuộc tính
     */
    @Column(name = "CODE", nullable = false)
    String code;

    /**
     * Tên thuộc tính
     */
    @Column(name = "NAME", nullable = false)
    String name;

    /**
     * Mô tả thuộc tính
     */
    @Column(name = "DESCRIPTION")
    String description;

    /**
     * Quy tắc validation
     */
    @Lob
    @Column(name = "VALIDATION_RULE")
    String validationRule;

    /**
     * Giá trị mặc định
     */
    @Column(name = "DEFAULT_VALUE")
    String defaultValue;

    /**
     * Bắt buộc: 1-Mandatory, 0-Optional
     */
    @Column(name = "MANDATORY")
    Integer mandatory;

    /**
     * Dữ liệu nhạy cảm: 1-Sensitive, 0-Non-sensitive
     */
    @Column(name = "IS_SENSITIVE")
    Integer isSensitive;

    /**
     * Có thể tìm kiếm: 1-Searchable, 0-Not searchable
     */
    @Column(name = "IS_SEARCHABLE")
    Integer isSearchable;

    /**
     * Có thể lọc: 1-Filterable, 0-Not filterable
     */
    @Column(name = "IS_FILTERABLE")
    Integer isFilterable;

    /**
     * Gợi ý giao diện người dùng
     */
    @Column(name = "UI_HINT")
    String uiHint;

    /**
     * Thứ tự sắp xếp
     */
    @Column(name = "ORD", nullable = false)
    Integer ord;

    /**
     * Trạng thái hoạt động: 1-Active, 0-Inactive
     */
    @Column(name = "IS_ACTIVE")
    Integer isActive;

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
