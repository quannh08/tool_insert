package com.example.toolinsert.controller;

import com.example.toolinsert.model.DriverImportResponse;
import com.example.toolinsert.service.DriverImportService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/drivers")
public class DriverImportController {

    private final DriverImportService driverImportService;

    public DriverImportController(DriverImportService driverImportService) {
        this.driverImportService = driverImportService;
    }

    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DriverImportResponse importDrivers(@RequestPart("file") @NotNull MultipartFile file) {
        return driverImportService.importFile(file);
    }
}
