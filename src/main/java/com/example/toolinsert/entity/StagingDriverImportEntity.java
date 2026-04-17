package com.example.toolinsert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "STG_DRIVER_IMPORT",
        uniqueConstraints = @UniqueConstraint(name = "UK_STG_DRIVER_IMPORT_JOB_ROW", columnNames = {"IMPORT_JOB_ID", "ROW_NO"})
)
public class StagingDriverImportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STAGING_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMPORT_JOB_ID", nullable = false)
    private DriverImportJobEntity importJob;

    @Column(name = "ROW_NO", nullable = false)
    private int rowNo;

    @Column(name = "SOURCE_ID", length = 64)
    private String sourceId;

    @Column(name = "IMPORT_STATUS", length = 64, nullable = false)
    private String importStatus;

    @Column(name = "ERROR_MESSAGE", length = 2000)
    private String errorMessage;

    @Lob
    @Column(name = "RAW_PAYLOAD", nullable = false)
    private String rawPayload;

    @Lob
    @Column(name = "NORMALIZED_PAYLOAD")
    private String normalizedPayload;

    public StagingDriverImportEntity() {
    }

    public Long getId() {
        return id;
    }

    public DriverImportJobEntity getImportJob() {
        return importJob;
    }

    public void setImportJob(DriverImportJobEntity importJob) {
        this.importJob = importJob;
    }

    public int getRowNo() {
        return rowNo;
    }

    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(String importStatus) {
        this.importStatus = importStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public String getNormalizedPayload() {
        return normalizedPayload;
    }

    public void setNormalizedPayload(String normalizedPayload) {
        this.normalizedPayload = normalizedPayload;
    }
}
