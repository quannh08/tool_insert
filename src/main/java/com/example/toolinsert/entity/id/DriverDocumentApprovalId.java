package com.example.toolinsert.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DriverDocumentApprovalId implements Serializable {

    @Column(name = "DRIVER_ID")
    private Long driverId;

    @Column(name = "DOCUMENT_TYPE", length = 64)
    private String documentType;

    @Column(name = "DOCUMENT_ID", length = 255)
    private String documentId;

    protected DriverDocumentApprovalId() {
    }

    public DriverDocumentApprovalId(Long driverId, String documentType, String documentId) {
        this.driverId = driverId;
        this.documentType = documentType;
        this.documentId = documentId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentId() {
        return documentId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DriverDocumentApprovalId that)) {
            return false;
        }
        return Objects.equals(driverId, that.driverId)
                && Objects.equals(documentType, that.documentType)
                && Objects.equals(documentId, that.documentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, documentType, documentId);
    }
}
