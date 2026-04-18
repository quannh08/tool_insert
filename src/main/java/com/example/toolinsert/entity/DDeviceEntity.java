package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_DEVICE
 *
 * Mô tả: Bảng lưu trữ thông tin thiết bị di động trong hệ thống
 *
 * Ý nghĩa: Bảng này quản lý thông tin các thiết bị di động (smartphone, tablet) được sử dụng để đăng nhập vào
 * hệ thống. Mỗi thiết bị có thông tin về device ID, token cho push notification, hệ điều hành, phiên bản ứng dụng, model,
 * hãng sản xuất, v.v. Bảng này kết hợp với bảng D_DRIVER_DEVICE để quản lý mối quan hệ giữa tài xế và thiết bị, giúp quản
 * lý phiên đăng nhập, gửi thông báo đẩy, và bảo mật thiết bị (block thiết bị không hợp lệ, quản lý đăng nhập đa thiết bị).
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "D_DEVICE")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDeviceEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_device_seq")
    @SequenceGenerator(name = "d_device_seq", sequenceName = "SEQ_D_DEVICE", allocationSize = 1)
    @Column(name = "ID")
    Long id;

    /**
     * Mã định danh thiết bị (Device Identifier)
     */
    @Column(name = "DEVICE_ID")
    String deviceId;

    /**
     * Token thiết bị (push notification token)
     */
    @Column(name = "DEVICE_TOKEN")
    String deviceToken;

    /**
     * Loại hệ điều hành (iOS, Android, etc.)
     */
    @Column(name = "OS_TYPE")
    String osType;

    /**
     * Phiên bản hệ điều hành
     */
    @Column(name = "OS_VERSION")
    String osVersion;

    /**
     * Phiên bản ứng dụng
     */
    @Column(name = "APP_VERSION")
    String appVersion;

    /**
     * Model thiết bị
     */
    @Column(name = "DEVICE_MODEL")
    String deviceModel;

    /**
     * Hãng sản xuất thiết bị
     */
    @Column(name = "DEVICE_BRAND")
    String deviceBrand;

    /**
     * Trạng thái kích hoạt: 1-Activated, 0-Deactivated
     */
    @Column(name = "IS_ACTIVED")
    Integer isActived;

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
     * Thời gian đăng nhập lần cuối
     */
    @Column(name = "LAST_LOGIN")
    LocalDateTime lastLogin;
}
