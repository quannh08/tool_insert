package com.example.toolinsert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "D_REGION")
public class RegionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REGION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private RegionEntity parent;

    @Column(name = "COUNTRY_ID")
    private Long countryId;

    @Column(name = "TYPE", length = 64)
    private String type;

    @Column(name = "CODE", length = 64, nullable = false)
    private String code;

    @Column(name = "NAME", length = 255, nullable = false)
    private String name;

    public RegionEntity() {
    }

    public Long getId() {
        return id;
    }

    public RegionEntity getParent() {
        return parent;
    }

    public void setParent(RegionEntity parent) {
        this.parent = parent;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
