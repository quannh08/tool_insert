package com.example.toolinsert.service;

import com.example.toolinsert.config.ImportProperties;
import com.example.toolinsert.model.FileImportPreviewResponse;
import com.example.toolinsert.model.FileImportRow;
import com.example.toolinsert.model.FileImportSummary;
import com.example.toolinsert.model.FileParseResult;
import com.example.toolinsert.model.ParsedDelimitedRow;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileImportPreviewService {

    private final DelimitedFileParser delimitedFileParser;
    private final ImportProperties properties;

    public FileImportPreviewService(DelimitedFileParser delimitedFileParser, ImportProperties properties) {
        this.delimitedFileParser = delimitedFileParser;
        this.properties = properties;
    }

    public FileImportPreviewResponse preview(MultipartFile file) {
        FileParseResult parseResult = delimitedFileParser.parse(file);
        List<FileImportRow> previewRows = buildPreviewRows(parseResult.rows());

        FileImportSummary summary = new FileImportSummary(
                parseResult.rows().size(),
                previewRows.size(),
                parseResult.columns().size()
        );

        return new FileImportPreviewResponse(
                file.getOriginalFilename(),
                parseResult.delimiter(),
                summary,
                parseResult.columns(),
                previewRows
        );
    }

    private List<FileImportRow> buildPreviewRows(List<ParsedDelimitedRow> rows) {
        // Only return a preview slice so the API stays lightweight even for larger files.
        return rows.stream()
                .limit(properties.getPreviewRows())
                .map(row -> new FileImportRow(row.rowNumber(), row.values()))
                .toList();
    }
}
