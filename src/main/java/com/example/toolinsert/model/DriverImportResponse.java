package com.example.toolinsert.model;

import java.util.List;

public record DriverImportResponse(
        int totalRows,
        int successRows,
        int failedRows,
        List<DriverImportError> errors,
        String message,
        long executionTime
) {
}
