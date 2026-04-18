package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.toolinsert.entity.id.DDriverDeviceId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_DRIVER_DEVICE
 *
 * Mô tả: Bảng liên kết giữa tài xế và thiết bị (quan hệ many-to-many)
 *
 * Ý nghĩa: Bảng này quản lý mối quan hệ giữa tài xế và thiết bị di động (smartphone, tablet) mà tài xế sử dụng
 * để đăng nhập và làm việc. Một tài xế có thể đăng nhập từ nhiều thiết bị khác nhau, và một thiết bị có thể được sử dụng
 * bởi nhiều tài xế (trong trường hợp thiết bị dùng chung). Bảng này giúp quản lý phiên đăng nhập, push notification và
 * bảo mật thiết bị.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_DRIVER_DEVICE",
        indexes = {
            @Index(name = "idx_driver_device_driver", columnList = "DRIVER_ID"),
            @Index(name = "idx_driver_device_device", columnList = "DEVICE_ID"),
            @Index(name = "idx_driver_device_status", columnList = "STATUS"),
            @Index(name = "idx_driver_device_driver_status", columnList = "DRIVER_ID, STATUS"),
            @Index(name = "idx_driver_device_driver_device_status", columnList = "DRIVER_ID, DEVICE_ID, STATUS")
        })
@IdClass(DDriverDeviceId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverDeviceEntity implements Serializable {
    @Id
    @Column(name = "DRIVER_ID")
    Long driverId;

    @Id
    @Column(name = "DEVICE_ID")
    Long deviceId;

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
     * Status: 1-Active, 0-Inactive
     * Dùng để soft delete - khi unlink device, set status = 0 thay vì xóa bản ghi
     */
    @Column(name = "STATUS")
    Integer status;
}
