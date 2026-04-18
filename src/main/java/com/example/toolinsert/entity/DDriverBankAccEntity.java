package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.cmsdriversservice.entity.id.DDriverBankAccId;

import java.io.Serializable;

/**
 * Entity đại diện cho bảng D_DRIVER_BANK_ACC
 *
 * Mô tả: Bảng liên kết giữa tài xế và tài khoản ngân hàng (quan hệ many-to-many)
 *
 * Ý nghĩa: Bảng này quản lý mối quan hệ giữa tài xế và tài khoản ngân hàng, cho phép một tài xế có nhiều tài
 * khoản ngân hàng và một tài khoản có thể được sử dụng bởi nhiều tài xế. Bảng này lưu trạng thái liên kết (active/inactive)
 * giữa tài xế và tài khoản ngân hàng, giúp quản lý việc thanh toán và rút tiền cho tài xế.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_DRIVER_BANK_ACC",
        indexes = {
            @Index(name = "idx_driver_bank_driver", columnList = "DRIVER_ID"),
            @Index(name = "idx_driver_bank_acc", columnList = "BANK_ACC_ID"),
            @Index(name = "idx_driver_bank_status", columnList = "STATUS")
        })
@IdClass(DDriverBankAccId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverBankAccEntity implements Serializable {
    @Id
    @Column(name = "DRIVER_ID")
    Long driverId;

    @Id
    @Column(name = "BANK_ACC_ID")
    Long bankAccId;

    /**
     * Status: 0-Default, 1-Non-default, 2-Inactive
     * Chỉ một tài khoản có thể là mặc định (status = 0) cho mỗi tài xế
     */
    @Column(name = "STATUS")
    Integer status;
}
