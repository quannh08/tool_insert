package com.example.toolinsert.model;

import java.util.List;

public record FileImportPreviewResponse(
        String fileName,
        String delimiter,
        FileImportSummary summary,
        List<FileColumn> columns,
        List<FileImportRow> rows
) {
}
