package com.example.toolinsert.service;

import com.example.toolinsert.model.DriverImportError;
import com.example.toolinsert.model.DriverImportResponse;
import com.example.toolinsert.model.FileColumn;
import com.example.toolinsert.model.FileParseResult;
import com.example.toolinsert.model.NormalizedDriverImportRow;
import com.example.toolinsert.model.ParsedDelimitedRow;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DriverImportService {

    private static final Set<String> REQUIRED_HEADERS = Set.of(
            "id",
            "ten",
            "sdt",
            "gioi_tinh",
            "trang_thai_tai_xe",
            "khu_vuc_hoat_dong",
            "loai_xe_dang_ky",
            "hang_tai_xe"
    );

    private final DelimitedFileParser delimitedFileParser;
    private final DriverImportNormalizer driverImportNormalizer;
    private final DriverDatabaseImportService driverDatabaseImportService;

    public DriverImportService(
            DelimitedFileParser delimitedFileParser,
            DriverImportNormalizer driverImportNormalizer,
            DriverDatabaseImportService driverDatabaseImportService
    ) {
        this.delimitedFileParser = delimitedFileParser;
        this.driverImportNormalizer = driverImportNormalizer;
        this.driverDatabaseImportService = driverDatabaseImportService;
    }

    public DriverImportResponse importFile(MultipartFile file) {
        Instant startedAt = Instant.now();
        FileParseResult parseResult = delimitedFileParser.parse(file);
        validateHeaders(parseResult.columns());

        List<DriverImportError> errors = new ArrayList<>();
        List<NormalizedDriverImportRow> normalizedRows = new ArrayList<>();

        for (ParsedDelimitedRow row : parseResult.rows()) {
            NormalizedDriverImportRow normalizedRow = driverImportNormalizer.normalize(row);
            if (normalizedRow.errors().isEmpty()) {
                normalizedRows.add(normalizedRow);
                continue;
            }
            errors.add(new DriverImportError(row.rowNumber(), formatRowErrors(row.rowNumber(), normalizedRow.errors())));
        }

        errors.addAll(driverDatabaseImportService.importValidatedRows(normalizedRows));

        int failedRows = errors.size();
        int successRows = parseResult.rows().size() - failedRows;
        String message = failedRows == 0 ? "Import completed." : "Import completed with row-level errors.";
        long executionTime = Duration.between(startedAt, Instant.now()).toMillis();

        return new DriverImportResponse(
                parseResult.rows().size(),
                successRows,
                failedRows,
                List.copyOf(errors),
                message,
                executionTime
        );
    }

    private void validateHeaders(List<FileColumn> columns) {
        Set<String> availableHeaders = new LinkedHashSet<>();
        for (FileColumn column : columns) {
            availableHeaders.add(column.normalizedKey());
        }

        List<String> missingHeaders = REQUIRED_HEADERS.stream()
                .filter(header -> !availableHeaders.contains(header))
                .sorted()
                .toList();

        if (!missingHeaders.isEmpty()) {
            throw new IllegalArgumentException("Missing required headers: " + String.join(", ", missingHeaders));
        }
    }

    private String formatRowErrors(int rowNumber, List<String> errors) {
        String rowPrefix = "Row " + rowNumber + ": ";
        return errors.stream()
                .map(error -> error.startsWith(rowPrefix) ? error.substring(rowPrefix.length()) : error)
                .collect(Collectors.joining("; "));
    }
}
