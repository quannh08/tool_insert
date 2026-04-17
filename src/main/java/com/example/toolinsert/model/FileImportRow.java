package com.example.toolinsert.model;

import java.util.Map;

public record FileImportRow(int rowNumber, Map<String, String> values) {
}
