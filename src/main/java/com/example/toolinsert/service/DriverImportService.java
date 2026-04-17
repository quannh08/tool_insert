package com.example.toolinsert.service;

import com.example.toolinsert.entity.DriverImportJobEntity;
import com.example.toolinsert.entity.StagingDriverImportEntity;
import com.example.toolinsert.model.DriverImportError;
import com.example.toolinsert.model.DriverImportResponse;
import com.example.toolinsert.model.FileColumn;
import com.example.toolinsert.model.FileParseResult;
import com.example.toolinsert.model.NormalizedDriverImportRow;
import com.example.toolinsert.model.ParsedDelimitedRow;
import com.example.toolinsert.repository.DriverImportJobRepository;
import com.example.toolinsert.repository.StagingDriverImportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DriverImportService {

    private static final int MAX_STAGING_SOURCE_ID_LENGTH = 64;
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
    private final DriverBatchInsertService driverBatchInsertService;
    private final DriverImportJobRepository driverImportJobRepository;
    private final StagingDriverImportRepository stagingDriverImportRepository;
    private final ObjectMapper objectMapper;

    public DriverImportService(
            DelimitedFileParser delimitedFileParser,
            DriverImportNormalizer driverImportNormalizer,
            DriverDatabaseImportService driverDatabaseImportService,
            DriverBatchInsertService driverBatchInsertService,
            DriverImportJobRepository driverImportJobRepository,
            StagingDriverImportRepository stagingDriverImportRepository,
            ObjectMapper objectMapper
    ) {
        this.delimitedFileParser = delimitedFileParser;
        this.driverImportNormalizer = driverImportNormalizer;
        this.driverDatabaseImportService = driverDatabaseImportService;
        this.driverBatchInsertService = driverBatchInsertService;
        this.driverImportJobRepository = driverImportJobRepository;
        this.stagingDriverImportRepository = stagingDriverImportRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public DriverImportResponse importFile(MultipartFile file) {
        Instant startedAt = Instant.now();
        FileParseResult parseResult = delimitedFileParser.parse(file);
        validateHeaders(parseResult.columns());

        DriverImportJobEntity importJob = createImportJob(file.getOriginalFilename(), parseResult.rows().size(), startedAt);
        List<DriverImportError> errors = new ArrayList<>();
        List<NormalizedDriverImportRow> normalizedRows = new ArrayList<>();
        List<StagingDriverImportEntity> stagingRows = new ArrayList<>();

        for (ParsedDelimitedRow row : parseResult.rows()) {
            NormalizedDriverImportRow normalizedRow = driverImportNormalizer.normalize(row);
            normalizedRows.add(normalizedRow);
            StagingDriverImportEntity stagingRow = buildStagingRow(importJob, row, normalizedRow);
            stagingRows.add(stagingRow);

            if (!normalizedRow.errors().isEmpty()) {
                errors.add(new DriverImportError(row.rowNumber(), formatRowErrors(row.rowNumber(), normalizedRow.errors())));
            }
        }

        driverBatchInsertService.saveInBatches(stagingRows, stagingDriverImportRepository);
        errors.addAll(driverDatabaseImportService.importValidatedRows(normalizedRows, stagingRows));
        driverBatchInsertService.saveInBatches(stagingRows, stagingDriverImportRepository);

        int failedRows = errors.size();
        int successRows = parseResult.rows().size() - failedRows;
        importJob.setSuccessRows(successRows);
        importJob.setFailedRows(failedRows);
        importJob.setSkippedRows(0);
        importJob.setStatus(failedRows == 0 ? "IMPORTED" : "IMPORTED_WITH_ERRORS");
        importJob.setMessage(failedRows == 0
                ? "Imported file into database successfully."
                : "Imported file into database with row-level errors.");
        importJob.setFinishedAt(Instant.now());
        driverImportJobRepository.save(importJob);

        long durationMs = Duration.between(startedAt, importJob.getFinishedAt()).toMillis();
        return new DriverImportResponse(
                importJob.getId(),
                parseResult.rows().size(),
                successRows,
                failedRows,
                List.copyOf(errors),
                importJob.getMessage(),
                durationMs
        );
    }

    private DriverImportJobEntity createImportJob(String fileName, int totalRows, Instant startedAt) {
        DriverImportJobEntity importJob = new DriverImportJobEntity();
        importJob.setFileName(fileName == null ? "unknown.csv" : fileName);
        importJob.setTotalRows(totalRows);
        importJob.setSuccessRows(0);
        importJob.setFailedRows(0);
        importJob.setSkippedRows(0);
        importJob.setStatus("PROCESSING");
        importJob.setMessage("Import job created.");
        importJob.setStartedAt(startedAt);
        return driverImportJobRepository.save(importJob);
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

    private StagingDriverImportEntity buildStagingRow(
            DriverImportJobEntity importJob,
            ParsedDelimitedRow rawRow,
            NormalizedDriverImportRow normalizedRow
    ) {
        StagingDriverImportEntity stagingRow = new StagingDriverImportEntity();
        stagingRow.setImportJob(importJob);
        stagingRow.setRowNo(rawRow.rowNumber());
        stagingRow.setSourceId(toStagingSourceId(normalizedRow.sourceId()));
        stagingRow.setImportStatus(normalizedRow.errors().isEmpty() ? "VALIDATED" : "FAILED");
        stagingRow.setErrorMessage(normalizedRow.errors().isEmpty() ? null : formatRowErrors(rawRow.rowNumber(), normalizedRow.errors()));
        stagingRow.setRawPayload(writeJson(rawRow.values()));
        stagingRow.setNormalizedPayload(writeJson(normalizedRow.normalizedValues()));
        return stagingRow;
    }

    private String formatRowErrors(int rowNumber, List<String> errors) {
        String rowPrefix = "Row " + rowNumber + ": ";
        return errors.stream()
                .map(error -> error.startsWith(rowPrefix) ? error.substring(rowPrefix.length()) : error)
                .collect(java.util.stream.Collectors.joining("; "));
    }

    private String writeJson(Map<?, ?> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Unable to serialize import payload.", exception);
        }
    }

    private String toStagingSourceId(String sourceId) {
        if (sourceId == null || sourceId.isBlank()) {
            return null;
        }
        String trimmed = sourceId.trim();
        if (trimmed.length() <= MAX_STAGING_SOURCE_ID_LENGTH) {
            return trimmed;
        }

        // Staging must keep the import progressing even when source identifiers are malformed.
        return trimmed.substring(0, MAX_STAGING_SOURCE_ID_LENGTH);
    }
}
