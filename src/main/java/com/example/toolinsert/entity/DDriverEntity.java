package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_DRIVER
 *
 * Mô tả: Bảng lưu trữ thông tin cơ bản của tài xế trong hệ thống
 *
 * Ý nghĩa: Đây là bảng chính quản lý thông tin tài xế, bao gồm thông tin cá nhân (họ tên, CCCD, ngày sinh,
 * giới tính), thông tin liên kết (userId, regionId), vị trí hiện tại (lastestLat, lastestLon), thời gian online,
 * và thông tin cho tài xế lái xe hộ (referrerId). Bảng này là trung tâm của hệ thống quản lý tài xế, các bảng khác
 * sẽ tham chiếu đến bảng này thông qua DRIVER_ID.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_DRIVER",
        indexes = {
            @Index(name = "idx_driver_status", columnList = "STATUS"),
            @Index(name = "idx_driver_region", columnList = "REGION_ID"),
            @Index(name = "idx_driver_gender", columnList = "GENDER"),
            @Index(name = "idx_driver_identity", columnList = "IDENTITY_NUMBER", unique = true),
            @Index(name = "idx_driver_status_region", columnList = "STATUS, REGION_ID"),
            @Index(name = "idx_driver_status_gender", columnList = "STATUS, GENDER")
            // REMOVED: idx_driver_created - no queries use CREATED column
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverEntity implements Serializable {
    /**
     * ID tài xế
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_driver_seq")
    @SequenceGenerator(name = "d_driver_seq", sequenceName = "SEQ_D_DRIVER", allocationSize = 1)
    @Column(name = "DRIVER_ID")
    Long driverId;

    /**
     * User ID liên kết với tài khoản người dùng
     */
    @Column(name = "USER_ID")
    Long userId;

    /**
     * Region ID - ID khu vực
     */
    @Column(name = "REGION_ID")
    Long regionId;

    /**
     * Số CCCD/CMND của tài xế
     */
    @Column(name = "IDENTITY_NUMBER", nullable = false)
    String identityNumber;

    /**
     * Họ và tên đầy đủ của tài xế
     */
    @Column(name = "FULL_NAME", nullable = false)
    String fullName;

    /**
     * Ngày sinh (Date of Birth)
     */
    @Column(name = "DOB")
    LocalDateTime dob;

    /**
     * Giới tính: 1-Nam, 0-Nữ
     */
    @Column(name = "GENDER")
    Integer gender;

    /**
     * Status: 1-Active, 0-Inactive
     */
    @Column(name = "STATUS")
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
     * Vĩ độ (Latitude) vị trí mới nhất
     */
    @Column(name = "LASTEST_LAT")
    String lastestLat;

    /**
     * Kinh độ (Longitude) vị trí mới nhất
     */
    @Column(name = "LASTEST_LON")
    String lastestLon;

    /**
     * ID người được lái hộ (Referrer ID) - cho tài xế lái xe hộ
     */
    @Column(name = "REFERRER_ID")
    Long referrerId;
}
