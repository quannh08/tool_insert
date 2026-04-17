package com.example.toolinsert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "D_PROPERTY")
public class PropertyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROPERTY_ID")
    private Long id;

    @Column(name = "CODE", length = 64, nullable = false)
    private String code;

    @Column(name = "NAME", length = 255, nullable = false)
    private String name;

    @Column(name = "FIELD_TYPE_ID")
    private Long fieldTypeId;

    @Column(name = "VALIDATION_RULE", length = 1000)
    private String validationRule;

    @Column(name = "MANDATORY")
    private Boolean mandatory;

    public PropertyEntity() {
    }

    public Long getId() {
        return id;
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

    public Long getFieldTypeId() {
        return fieldTypeId;
    }

    public void setFieldTypeId(Long fieldTypeId) {
        this.fieldTypeId = fieldTypeId;
    }

    public String getValidationRule() {
        return validationRule;
    }

    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }
}
