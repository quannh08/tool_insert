package com.example.toolinsert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "IMP_DRIVER_IMPORT_JOB")
public class DriverImportJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMPORT_JOB_ID")
    private Long id;

    @Column(name = "FILE_NAME", length = 255, nullable = false)
    private String fileName;

    @Column(name = "TOTAL_ROWS")
    private int totalRows;

    @Column(name = "SUCCESS_ROWS")
    private int successRows;

    @Column(name = "FAILED_ROWS")
    private int failedRows;

    @Column(name = "SKIPPED_ROWS")
    private int skippedRows;

    @Column(name = "STATUS", length = 64, nullable = false)
    private String status;

    @Column(name = "MESSAGE", length = 1000)
    private String message;

    @Column(name = "STARTED_AT", nullable = false)
    private Instant startedAt;

    @Column(name = "FINISHED_AT")
    private Instant finishedAt;

    public DriverImportJobEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSuccessRows() {
        return successRows;
    }

    public void setSuccessRows(int successRows) {
        this.successRows = successRows;
    }

    public int getFailedRows() {
        return failedRows;
    }

    public void setFailedRows(int failedRows) {
        this.failedRows = failedRows;
    }

    public int getSkippedRows() {
        return skippedRows;
    }

    public void setSkippedRows(int skippedRows) {
        this.skippedRows = skippedRows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }
}
