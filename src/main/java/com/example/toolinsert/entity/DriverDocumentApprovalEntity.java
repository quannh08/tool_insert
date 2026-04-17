package com.example.toolinsert.entity;

import com.example.toolinsert.entity.id.DriverDocumentApprovalId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "D_DRIVER_DOCUMENT_APPROVAL")
public class DriverDocumentApprovalEntity {

    @EmbeddedId
    private DriverDocumentApprovalId id;

    @MapsId("driverId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_ID")
    private DriverEntity driver;

    @Column(name = "STATUS", length = 64)
    private String status;

    public DriverDocumentApprovalEntity() {
    }

    public DriverDocumentApprovalId getId() {
        return id;
    }

    public void setId(DriverDocumentApprovalId id) {
        this.id = id;
    }

    public DriverEntity getDriver() {
        return driver;
    }

    public void setDriver(DriverEntity driver) {
        this.driver = driver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
