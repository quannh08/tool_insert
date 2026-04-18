package com.example.toolinsert.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.toolinsert.entity.id.DClassServiceId;

import java.io.Serializable;

/**
 * Entity đại diện cho bảng D_CLASS_SERVICE
 *
 * Mô tả: Bảng liên kết giữa lớp (class) và dịch vụ (quan hệ many-to-many)
 *
 * Ý nghĩa: Bảng này quản lý mối quan hệ giữa các lớp dịch vụ và các dịch vụ cụ thể, xác định lớp nào có thể áp
 * dụng cho dịch vụ nào. Ví dụ: lớp VIP có thể áp dụng cho dịch vụ "Lái xe ô tô hộ" nhưng không áp dụng cho dịch vụ "Lái
 * xe máy hộ". Bảng này kết hợp với bảng D_DRIVER_CLASS để tạo nên hệ thống phân loại tài xế linh hoạt theo cả lớp và
 * dịch vụ, giúp quản lý chính sách thưởng và ưu đãi một cách chi tiết và chính xác.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "D_CLASS_SERVICE")
@IdClass(DClassServiceId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DClassServiceEntity implements Serializable {
    @Id
    @Column(name = "CLASS_ID")
    Integer classId;

    @Id
    @Column(name = "SERVICE_ID")
    Integer serviceId;

    /**
     * Status: 1-Active, 0-Inactive
     */
    @Column(name = "STATUS")
    Integer status;
}
