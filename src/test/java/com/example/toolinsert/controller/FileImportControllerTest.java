package com.example.toolinsert.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FileImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void previewsUploadedFileAsGenericRows() throws Exception {
        String payload = String.join("\n",
                "id\tten\tSĐT\tngay_sinh\tgioi_tinh",
                "1\tĐinh Thế Sang\t972326031\t2001-07-09\tNam",
                "2\tĐoàn Nguyễn\t906146228\t2000-02-02\tNam"
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "drivers.tsv",
                MediaType.TEXT_PLAIN_VALUE,
                payload.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/v1/imports/files/preview").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("drivers.tsv"))
                .andExpect(jsonPath("$.delimiter").value("TAB"))
                .andExpect(jsonPath("$.summary.totalRows").value(2))
                .andExpect(jsonPath("$.summary.previewRows").value(2))
                .andExpect(jsonPath("$.summary.totalColumns").value(5))
                .andExpect(jsonPath("$.columns[2].originalName").value("SĐT"))
                .andExpect(jsonPath("$.columns[2].normalizedKey").value("sdt"))
                .andExpect(jsonPath("$.rows[0].values.ten").value("Đinh Thế Sang"))
                .andExpect(jsonPath("$.rows[1].values.ngay_sinh").value("2000-02-02"));
    }

    @Test
    void rejectsEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "drivers.tsv",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/v1/imports/files/preview").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("File must not be empty."));
    }
}
