package com.example.toolinsert.controller;

import com.example.toolinsert.model.FileImportPreviewResponse;
import com.example.toolinsert.service.FileImportPreviewService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/imports/files")
public class FileImportController {

    private final FileImportPreviewService fileImportPreviewService;

    public FileImportController(FileImportPreviewService fileImportPreviewService) {
        this.fileImportPreviewService = fileImportPreviewService;
    }

    @PostMapping(
            path = "/preview",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    // This endpoint only exposes a neutral preview model and intentionally does not map to any database entity yet.
    public FileImportPreviewResponse preview(@RequestPart("file") @NotNull MultipartFile file) {
        return fileImportPreviewService.preview(file);
    }
}
