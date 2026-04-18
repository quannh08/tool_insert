package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.toolinsert.entity.id.DDriverPartyId;

import java.io.Serializable;

/**
 * Entity đại diện cho bảng D_DRIVER_PARTY
 *
 * Mô tả: Bảng liên kết giữa tài xế và số điện thoại/đối tác (quan hệ many-to-many)
 *
 * Ý nghĩa: Bảng này quản lý mối quan hệ giữa tài xế và số điện thoại (party). Một tài xế có thể có nhiều số điện
 * thoại (số chính, số phụ, số liên hệ khẩn cấp) và một số điện thoại có thể được sử dụng bởi nhiều tài xế (trong trường
 * hợp số điện thoại dùng chung hoặc số hotline). Bảng này giúp quản lý thông tin liên lạc, booking và xác thực tài xế
 * thông qua số điện thoại.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_DRIVER_PARTY",
        indexes = {
            @Index(name = "idx_driver_party_driver", columnList = "DRIVER_ID"),
            @Index(name = "idx_driver_party_party", columnList = "PARTY_ID"),
            @Index(name = "idx_driver_party_status", columnList = "STATUS"),
            @Index(name = "idx_driver_party_party_status", columnList = "PARTY_ID, STATUS")
        })
@IdClass(DDriverPartyId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverPartyEntity implements Serializable {

    @Id
    @Column(name = "DRIVER_ID")
    Long driverId;

    /**
     * ID đối tác/người dùng
     */
    @Id
    @Column(name = "PARTY_ID")
    Long partyId;

    /**
     * Status: 1-Active, 0-Inactive
     */
    @Column(name = "STATUS")
    Integer status;
}
