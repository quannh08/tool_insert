package com.example.toolinsert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        name = "D_DRIVER_METRIC",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_D_DRIVER_METRIC",
                columnNames = {"DRIVER_ID", "CRITERIA_ID", "SERVICE_ID"}
        )
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DDriverMetricEntity implements Serializable {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_driver_metric_seq")
    @SequenceGenerator(name = "d_driver_metric_seq", sequenceName = "SEQ_D_DRIVER_METRIC", allocationSize = 1)
    @Column(name = "DRIVER_METRIC_ID")
    Long driverMetricId;

    @Column(name = "DRIVER_ID", nullable = false)
    Long driverId;

    @Column(name = "CRITERIA_ID", nullable = false)
    Integer criteriaId;

    @Column(name = "SERVICE_ID")
    Integer serviceId;

    @Column(name = "VALUE", nullable = false)
    BigDecimal value;

    @Column(name = "UNIT", nullable = false)
    String unit;
}
