package com.example.toolinsert.model;

import java.util.List;
import java.util.Map;

public record NormalizedDriverImportRow(
        int rowNumber,
        String sourceId,
        Map<String, Object> normalizedValues,
        List<String> errors
) {
}
