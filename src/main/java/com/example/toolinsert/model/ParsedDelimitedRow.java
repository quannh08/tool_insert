package com.example.toolinsert.model;

import java.util.Map;

// Internal parser model that keeps one normalized row from the uploaded file.
public record ParsedDelimitedRow(int rowNumber, Map<String, String> values) {
}
