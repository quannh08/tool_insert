package com.example.toolinsert.model;

import java.util.List;

public record FileParseResult(String delimiter, List<FileColumn> columns, List<ParsedDelimitedRow> rows) {
}
