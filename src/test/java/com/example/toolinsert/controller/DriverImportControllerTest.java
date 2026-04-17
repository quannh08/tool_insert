package com.example.toolinsert.controller;

import com.example.toolinsert.repository.DriverImportJobRepository;
import com.example.toolinsert.repository.StagingDriverImportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DriverImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverImportJobRepository driverImportJobRepository;

    @Autowired
    private StagingDriverImportRepository stagingDriverImportRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("delete from D_DRIVER_DOCUMENT_APPROVAL");
        jdbcTemplate.execute("delete from D_DRIVER_METRIC");
        jdbcTemplate.execute("delete from D_DRIVER_PROPERTY");
        jdbcTemplate.execute("delete from D_DRIVER_CLASS");
        jdbcTemplate.execute("delete from D_DRIVER_SERVICE");
        jdbcTemplate.execute("delete from D_DRIVER_BANK_ACC");
        jdbcTemplate.execute("delete from D_DRIVER_PARTY");
        jdbcTemplate.execute("delete from D_BANK_ACC");
        jdbcTemplate.execute("delete from D_PARTY");
        jdbcTemplate.execute("delete from D_DRIVER");
        jdbcTemplate.execute("delete from D_CRITERIA");
        jdbcTemplate.execute("delete from D_PROPERTY");
        jdbcTemplate.execute("delete from D_CLASS");
        jdbcTemplate.execute("delete from D_SERVICE");
        jdbcTemplate.execute("delete from D_REGION");
        stagingDriverImportRepository.deleteAll();
        driverImportJobRepository.deleteAll();
    }

    @Test
    void importsCsvIntoStagingAndReturnsSummary() throws Exception {
        String payload = String.join("\n",
                "id,ten,sdt,gioi_tinh,trang_thai_tai_xe,khu_vuc_hoat_dong,loai_xe_dang_ky,hang_tai_xe,cccd,stk,ten_nguoi_thu_huong,ten_ngan_hang,ngan_hang_viet_tat,kinh_nghiem_lai_xe,muc_coc",
                "1,Driver One,0972326031,Nam,Active,Ha Noi,Manual,Hang 1,123456789012,00112233,DRIVER ONE,Techcombank,TCB,3,2000000",
                "2,Driver Two,abc,Nam,Active,Ha Noi,Automatic,Hang 1,NULL,NULL,NULL,NULL,NULL,2,0"
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "drivers.csv",
                MediaType.TEXT_PLAIN_VALUE,
                payload.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/drivers/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRows").value(2))
                .andExpect(jsonPath("$.successRows").value(1))
                .andExpect(jsonPath("$.failedRows").value(1))
                .andExpect(jsonPath("$.errors[0].rowNumber").value(3))
                .andExpect(jsonPath("$.errors[0].message").value("field 'sdt' must contain digits."));

        assertThat(countRows("D_DRIVER")).isEqualTo(1);
        assertThat(countRows("D_PARTY")).isEqualTo(1);
        assertThat(countRows("D_DRIVER_PARTY")).isEqualTo(1);
        assertThat(countRows("D_SERVICE")).isEqualTo(1);
        assertThat(countRows("D_CLASS")).isEqualTo(1);
        assertThat(countRows("D_DRIVER_SERVICE")).isEqualTo(1);
        assertThat(countRows("D_DRIVER_CLASS")).isEqualTo(1);
        assertThat(countRows("D_BANK_ACC")).isEqualTo(1);
        assertThat(countRows("D_DRIVER_BANK_ACC")).isEqualTo(1);
        assertThat(countRows("D_CRITERIA")).isEqualTo(1);
        assertThat(countRows("D_DRIVER_METRIC")).isEqualTo(1);
    }

    @Test
    void rejectsFilesMissingRequiredHeaders() throws Exception {
        String payload = String.join("\n",
                "id,ten",
                "1,Driver One"
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "drivers.csv",
                MediaType.TEXT_PLAIN_VALUE,
                payload.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/drivers/import").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required headers: gioi_tinh, hang_tai_xe, khu_vuc_hoat_dong, loai_xe_dang_ky, sdt, trang_thai_tai_xe"));
    }

    @Test
    void truncatesOversizedSourceIdInsteadOfFailingTheWholeImport() throws Exception {
        String longSourceId = "X".repeat(700);
        String payload = String.join("\n",
                "id,ten,sdt,gioi_tinh,trang_thai_tai_xe,khu_vuc_hoat_dong,loai_xe_dang_ky,hang_tai_xe",
                longSourceId + ",Driver One,0972326031,Nam,Active,Ha Noi,Manual,Hang 1"
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "drivers.csv",
                MediaType.TEXT_PLAIN_VALUE,
                payload.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/drivers/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRows").value(1))
                .andExpect(jsonPath("$.failedRows").value(0));
    }

    @Test
    void returnsRowErrorsWithoutRepeatingTheRowNumberInsideTheMessage() throws Exception {
        String payload = String.join("\n",
                "id,ten,sdt,gioi_tinh,trang_thai_tai_xe,khu_vuc_hoat_dong,loai_xe_dang_ky,hang_tai_xe",
                "1,,,Nam,Active,Ha Noi,Manual,Hang 1"
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "drivers.csv",
                MediaType.TEXT_PLAIN_VALUE,
                payload.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/drivers/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].rowNumber").value(2))
                .andExpect(jsonPath("$.errors[0].message").value("field 'ten' is required.; field 'sdt' is required."));
    }

    @Test
    void importsRowsThatWrapTheEntireCsvRowInQuotes() throws Exception {
        String payload = String.join("\n",
                "id,ten,sdt,gioi_tinh,trang_thai_tai_xe,khu_vuc_hoat_dong,loai_xe_dang_ky,hang_tai_xe",
                "1,Driver One,0972326031,Nam,Active,Ha Noi,Manual,Hang 1",
                "\"2,Driver Two,0906146228,Nam,Active,Ha Noi,Manual,Hang 1\""
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "drivers.csv",
                MediaType.TEXT_PLAIN_VALUE,
                payload.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/drivers/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRows").value(2))
                .andExpect(jsonPath("$.successRows").value(2))
                .andExpect(jsonPath("$.failedRows").value(0));
    }

    private long countRows(String tableName) {
        Long count = jdbcTemplate.queryForObject("select count(*) from " + tableName, Long.class);
        return count == null ? 0L : count;
    }
}
