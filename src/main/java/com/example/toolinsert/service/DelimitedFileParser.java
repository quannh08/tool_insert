package com.example.toolinsert.service;

import com.example.toolinsert.config.ImportProperties;
import com.example.toolinsert.model.FileColumn;
import com.example.toolinsert.model.FileParseResult;
import com.example.toolinsert.model.ParsedDelimitedRow;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DelimitedFileParser {

    private static final char TAB = '\t';

    private final ImportProperties properties;

    public DelimitedFileParser(ImportProperties properties) {
        this.properties = properties;
    }

    public FileParseResult parse(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty.");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to read uploaded file.", exception);
        }

        char delimiter = detectDelimiter(bytes);
        CsvParserSettings settings = buildSettings(delimiter);
        List<FileColumn> columns;
        List<ParsedDelimitedRow> rows = new ArrayList<>();

        try (Reader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8))) {
            CsvParser parser = new CsvParser(settings);
            List<String[]> parsedRows = parser.parseAll(reader);
            String[] headers = parser.getContext().headers();
            if (headers == null || headers.length == 0) {
                throw new IllegalArgumentException("File must contain a header row.");
            }
            columns = toColumns(headers);

            int rowNumber = 2;
            for (String[] parsedRow : parsedRows) {
                if (rows.size() >= properties.getMaxRows()) {
                    throw new IllegalArgumentException("File exceeds max supported rows: " + properties.getMaxRows());
                }
                String[] normalizedRow = normalizeParsedRow(headers, parsedRow, delimiter);
                Map<String, String> values = toValueMap(headers, normalizedRow);
                if (!properties.isSkipBlankRows() || values.values().stream().anyMatch(value -> value != null && !value.isBlank())) {
                    rows.add(new ParsedDelimitedRow(rowNumber, values));
                }
                rowNumber++;
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to parse uploaded file.", exception);
        }

        return new FileParseResult(delimiterName(delimiter), columns, rows);
    }

    private CsvParserSettings buildSettings(char delimiter) {
        CsvParserSettings settings = new CsvParserSettings();
        CsvFormat format = settings.getFormat();
        format.setDelimiter(delimiter);
        settings.setHeaderExtractionEnabled(true);
        settings.setSkipEmptyLines(properties.isSkipBlankRows());
        settings.setLineSeparatorDetectionEnabled(true);
        settings.setIgnoreLeadingWhitespaces(false);
        settings.setIgnoreTrailingWhitespaces(false);
        settings.setReadInputOnSeparateThread(false);
        settings.setEmptyValue(null);
        settings.setNullValue(null);
        settings.setMaxCharsPerColumn(100_000);
        return settings;
    }

    private Map<String, String> toValueMap(String[] headers, String[] row) {
        Map<String, String> values = new LinkedHashMap<>();
        for (int index = 0; index < headers.length; index++) {
            String header = normalizeHeader(headers[index]);
            String value = index < row.length ? normalizeValue(row[index]) : null;
            values.put(header, value);
        }
        return values;
    }

    private String[] normalizeParsedRow(String[] headers, String[] row, char delimiter) {
        if (row.length != 1 || headers.length <= 1) {
            return row;
        }

        String singleValue = row[0];
        if (singleValue == null || singleValue.indexOf(delimiter) < 0) {
            return row;
        }

        String[] reparsed = parseSingleRow(singleValue, delimiter);
        return reparsed.length > 1 ? reparsed : row;
    }

    private List<FileColumn> toColumns(String[] headers) {
        List<FileColumn> columns = new ArrayList<>();
        for (int index = 0; index < headers.length; index++) {
            columns.add(new FileColumn(index, headers[index], normalizeHeader(headers[index])));
        }
        return List.copyOf(columns);
    }

    private char detectDelimiter(byte[] bytes) {
        String sample = new String(bytes, StandardCharsets.UTF_8);
        for (String line : sample.split("\\R")) {
            if (!line.isBlank()) {
                int tabCount = count(line, TAB);
                int commaCount = count(line, ',');
                int semicolonCount = count(line, ';');
                if (tabCount >= commaCount && tabCount >= semicolonCount && tabCount > 0) {
                    return TAB;
                }
                if (semicolonCount > commaCount) {
                    return ';';
                }
                return ',';
            }
        }
        return ',';
    }

    private String[] parseSingleRow(String row, char delimiter) {
        CsvParserSettings settings = buildSettings(delimiter);
        settings.setHeaderExtractionEnabled(false);
        CsvParser parser = new CsvParser(settings);
        return parser.parseLine(row);
    }

    private int count(String line, char character) {
        int count = 0;
        for (int index = 0; index < line.length(); index++) {
            if (line.charAt(index) == character) {
                count++;
            }
        }
        return count;
    }

    private String normalizeHeader(String value) {
        if (value == null) {
            return null;
        }
        // Normalize headers once at the ingestion boundary so later mapping code can rely on stable keys.
        String cleaned = value.replace("\uFEFF", "").trim();
        String normalized = Normalizer.normalize(cleaned, Normalizer.Form.NFD)
                .replace("\u0110", "D")
                .replace("\u0111", "d")
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");
        return normalized;
    }

    private String normalizeValue(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || "null".equalsIgnoreCase(trimmed)) {
            return null;
        }
        return trimmed;
    }

    private String delimiterName(char delimiter) {
        return delimiter == TAB ? "TAB" : String.valueOf(delimiter);
    }
}
