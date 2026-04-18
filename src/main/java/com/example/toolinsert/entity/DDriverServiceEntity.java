package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.cmsdriversservice.entity.id.DDriverServiceId;

import java.io.Serializable;

/**
 * Entity đại diện cho bảng D_DRIVER_SERVICE
 *
 * Mô tả: Bảng liên kết giữa tài xế và dịch vụ (quan hệ many-to-many)
 *
 * Ý nghĩa: Bảng này quản lý các dịch vụ mà tài xế có thể cung cấp, ví dụ: lái xe ô tô hộ, lái xe máy hộ. Một tài
 * xế có thể cung cấp nhiều dịch vụ khác nhau và một dịch vụ có thể được cung cấp bởi nhiều tài xế. Bảng này lưu trạng
 * thái liên kết (active/inactive) giữa tài xế và dịch vụ, giúp quản lý khả năng và quyền hạn của tài xế trong từng loại
 * dịch vụ. Khi tài xế đăng ký hoặc hủy một dịch vụ, bản ghi trong bảng này sẽ được tạo hoặc cập nhật trạng thái.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "D_DRIVER_SERVICE")
@IdClass(DDriverServiceId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverServiceEntity implements Serializable {
    @Id
    @Column(name = "DRIVER_ID")
    Long driverId;

    @Id
    @Column(name = "SERVICE_ID")
    Integer serviceId;

    /**
     * Status: 1-Active, 0-Inactive
     */
    @Column(name = "STATUS")
    Integer status;
}
