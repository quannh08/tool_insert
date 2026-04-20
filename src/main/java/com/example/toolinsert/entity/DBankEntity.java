package com.example.toolinsert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "D_BANK")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DBankEntity implements Serializable {

    @Id
    @Column(name = "BANK_ID")
    Integer bankId;

    @Column(name = "COUNTRY_ID", nullable = false)
    Integer countryId;

    @Column(name = "CODE", nullable = false)
    String code;

    @Column(name = "NAME", nullable = false)
    String name;

    @Column(name = "SHORT_NAME")
    String shortName;

    @Column(name = "CREATED", nullable = false)
    LocalDateTime created;

    @Column(name = "CREATOR", nullable = false)
    String creator;

    @Column(name = "MODIFIED")
    LocalDateTime modified;

    @Column(name = "MODIFIER")
    String modifier;
}
