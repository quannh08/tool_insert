package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_REGION
 *
 * Mô tả: Bảng lưu trữ thông tin khu vực địa lý (tỉnh/thành, quận/huyện, phường/xã)
 *
 * Ý nghĩa: Bảng này quản lý cấu trúc phân cấp địa lý trong hệ thống, hỗ trợ cấu trúc cây (tree structure) thông
 * qua parentId. Bảng này được sử dụng để quản lý khu vực hoạt động của tài xế, phân bổ đơn hàng theo khu vực, tính toán
 * phí vận chuyển, và các chức năng liên quan đến địa lý. Mỗi khu vực có thông tin về tọa độ (latitude, longitude) để
 * hỗ trợ tính toán khoảng cách và hiển thị trên bản đồ.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "D_REGION")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DRegionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_region_seq")
    @SequenceGenerator(name = "d_region_seq", sequenceName = "SEQ_D_REGION", allocationSize = 1)
    @Column(name = "REGION_ID")
    Long regionId;

    /**
     * ID khu vực cha (Parent Region ID)
     */
    @Column(name = "PARENT_ID")
    Long parentId;

    /**
     * ID quốc gia
     */
    @Column(name = "COUNTRY_ID")
    Integer countryId;

    /**
     * Loại khu vực (Province, District, Ward, etc.)
     */
    @Column(name = "TYPE")
    String type;

    /**
     * Mã khu vực
     */
    @Column(name = "CODE")
    String code;

    /**
     * Tên khu vực
     */
    @Column(name = "NAME")
    String name;

    /**
     * Tên chi tiết khu vực
     */
    @Column(name = "DETAIL_NAME")
    String detailName;

    /**
     * Vĩ độ (Latitude)
     */
    @Column(name = "LATITUDE")
    String latitude;

    /**
     * Kinh độ (Longitude)
     */
    @Column(name = "LONGITUDE")
    String longitude;

    /**
     * Thứ tự sắp xếp
     */
    @Column(name = "ORD")
    Integer ord;

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
