package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng D_PARTY
 *
 * Mô tả: Bảng lưu trữ thông tin số điện thoại/đối tác trong hệ thống
 *
 * Ý nghĩa: Bảng này quản lý thông tin các số điện thoại được sử dụng trong hệ thống. Số điện thoại có thể được
 * liên kết với tài xế thông qua bảng D_DRIVER_PARTY để quản lý thông tin liên lạc, booking và xác thực. Bảng này giúp
 * tái sử dụng số điện thoại, tránh trùng lặp và quản lý tập trung thông tin liên lạc trong hệ thống.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "D_PARTY")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DPartyEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_party_seq")
    @SequenceGenerator(name = "d_party_seq", sequenceName = "SEQ_D_PARTY", allocationSize = 1)
    @Column(name = "PARTY_ID")
    Long partyId;

    /**
     * Số điện thoại
     */
    @Column(name = "PHONE")
    String phone;

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
