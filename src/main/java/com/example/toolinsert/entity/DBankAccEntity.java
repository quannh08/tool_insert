package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_BANK_ACC
 *
 * Mô tả: Bảng lưu trữ thông tin tài khoản ngân hàng trong hệ thống
 *
 * Ý nghĩa: Bảng này quản lý thông tin các tài khoản ngân hàng bao gồm số tài khoản, tên chủ tài khoản, loại
 * tài khoản, loại tiền tệ và trạng thái kích hoạt. Các tài khoản này có thể được liên kết với tài xế thông qua bảng
 * D_DRIVER_BANK_ACC để quản lý tài khoản nhận thanh toán của từng tài xế.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "D_BANK_ACC")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DBankAccEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_bank_acc_seq")
    @SequenceGenerator(name = "d_bank_acc_seq", sequenceName = "SEQ_D_BANK_ACC", allocationSize = 1)
    @Column(name = "BANK_ACC_ID")
    Long bankAccId;

    /**
     * ID ngân hàng
     */
    @Column(name = "BANK_ID")
    Integer bankId;

    /**
     * Số tài khoản ngân hàng
     */
    @Column(name = "ACC_NUMBER", nullable = false)
    String accNumber;

    /**
     * Tên chủ tài khoản
     */
    @Column(name = "ACC_NAME", nullable = false)
    String accName;

    /**
     * Loại tài khoản (Checking, Savings, etc.)
     */
    @Column(name = "ACC_TYPE")
    String accType;

    /**
     * Loại tiền tệ (VND, USD, etc.)
     */
    @Column(name = "CURRENCY", nullable = false)
    String currency;

    /**
     * Trạng thái kích hoạt: 1-Activated, 0-Deactivated
     */
    @Column(name = "IS_ACTIVED")
    Integer isActived;

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
