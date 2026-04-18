package com.example.toolinsert.service;

import com.example.toolinsert.model.NormalizedDriverImportRow;
import com.example.toolinsert.model.ParsedDelimitedRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class DriverImportNormalizer {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");

    public NormalizedDriverImportRow normalize(ParsedDelimitedRow row) {
        Map<String, String> rawValues = row.values();
        Map<String, Object> normalized = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        normalized.put("source_id", rawValues.get("id"));
        normalized.put("full_name", normalizeText(rawValues.get("ten")));
        normalized.put("dob", parseDate(rawValues.get("ngay_sinh"), "ngay_sinh", row.rowNumber(), errors));
        normalized.put("gender", normalizeGender(rawValues.get("gioi_tinh"), row.rowNumber(), errors));
        normalized.put("identity_number", normalizeDigitString(rawValues.get("cccd"), "cccd", row.rowNumber(), errors));
        normalized.put("driver_status", normalizeDriverStatus(rawValues.get("trang_thai_tai_xe")));
        normalized.put("contract_status", normalizeContractStatus(rawValues.get("trang_thai_hop_dong")));
        normalized.put("region_name", normalizeText(rawValues.get("khu_vuc_hoat_dong")));
        normalized.put("referrer_source_id", normalizeText(rawValues.get("nguoi_gioi_thieu")));
        normalized.put("phone", normalizeDigitString(rawValues.get("sdt"), "sdt", row.rowNumber(), errors));
        normalized.put("bank_account_number", normalizeDigitString(rawValues.get("stk"), "stk", row.rowNumber(), errors));
        normalized.put("bank_account_name", normalizeText(rawValues.get("ten_nguoi_thu_huong")));
        normalized.put("bank_name", normalizeText(rawValues.get("ten_ngan_hang")));
        normalized.put("bank_code", normalizeText(rawValues.get("ngan_hang_viet_tat")));
        normalized.put("service_codes", splitMultiValue(rawValues.get("loai_xe_dang_ky")));
        normalized.put("driver_class", normalizeText(rawValues.get("hang_tai_xe")));
        normalized.put("driving_experience_years", parseInteger(rawValues.get("kinh_nghiem_lai_xe"), "kinh_nghiem_lai_xe", row.rowNumber(), errors));
        normalized.put("bonus_rate", parseDecimal(rawValues.get("muc_coc"), "muc_coc", row.rowNumber(), errors));
        normalized.put("registered_at", parseDateTime(rawValues.get("ngay_dang_ky"), "ngay_dang_ky", row.rowNumber(), errors));
        normalized.put("activated_at", parseDateTime(rawValues.get("ngay_kich_hoat"), "ngay_kich_hoat", row.rowNumber(), errors));
        normalized.put("properties", normalizePropertyValues(rawValues, row.rowNumber(), errors));
        normalized.put("document_approvals", normalizeDocumentApprovals(rawValues));

        validateRequired(rawValues, row.rowNumber(), errors);
        return new NormalizedDriverImportRow(row.rowNumber(), rawValues.get("id"), normalized, List.copyOf(errors));
    }

    private Map<String, Object> normalizePropertyValues(Map<String, String> rawValues, int rowNumber, List<String> errors) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("ngay_cap_cccd", parseDate(rawValues.get("ngay_cap_cccd"), "ngay_cap_cccd", rowNumber, errors));
        properties.put("noi_cap_cccd", normalizeText(rawValues.get("noi_cap_cccd")));
        properties.put("ngay_het_han_cccd", parseDate(rawValues.get("ngay_het_han_cccd"), "ngay_het_han_cccd", rowNumber, errors));
        properties.put("so_gplx", normalizeText(rawValues.get("so_gplx")));
        properties.put("so_seria_gplx", normalizeText(rawValues.get("so_seria_gplx")));
        properties.put("ngay_cap_gplx", parseDate(rawValues.get("ngay_cap_gplx"), "ngay_cap_gplx", rowNumber, errors));
        properties.put("ngay_het_han_gplx", parseDate(rawValues.get("ngay_het_han_gplx"), "ngay_het_han_gplx", rowNumber, errors));
        properties.put("dia_chi", normalizeText(rawValues.get("dia_chi")));
        properties.put("dia_chi_hien_tai", normalizeText(rawValues.get("dia_chi_hien_tai")));
        properties.put("cccd_mat_truoc", cleanFilePath(rawValues.get("cccd_mat_truoc")));
        properties.put("cccd_mat_sau", cleanFilePath(rawValues.get("cccd_mat_sau")));
        properties.put("gplx_mat_truoc", cleanFilePath(rawValues.get("gplx_mat_truoc")));
        properties.put("gplx_mat_sau", cleanFilePath(rawValues.get("gplx_mat_sau")));
        properties.put("giay_xet_nghiem_ma_tuy", cleanFilePath(rawValues.get("giay_xet_nghiem_ma_tuy")));
        properties.put("ly_lich_tu_phap", cleanFilePath(rawValues.get("ly_lich_tu_phap")));
        properties.put("giay_xet_nghiem_hiv", cleanFilePath(rawValues.get("giay_xet_nghiem_hiv")));
        return properties;
    }

    private Map<String, Object> normalizeDocumentApprovals(Map<String, String> rawValues) {
        Map<String, Object> approvals = new LinkedHashMap<>();
        approvals.put("cccd_mat_truoc", normalizeApprovalStatus(rawValues.get("trang_thai_cccd_mat_truoc")));
        approvals.put("cccd_mat_sau", normalizeApprovalStatus(rawValues.get("trang_thai_cccd_mat_sau")));
        approvals.put("gplx_mat_truoc", normalizeApprovalStatus(rawValues.get("trang_thai_gplx_mat_truoc")));
        approvals.put("gplx_mat_sau", normalizeApprovalStatus(rawValues.get("trang_thai_gplx_mat_sau")));
        approvals.put("giay_xet_nghiem_ma_tuy", normalizeApprovalStatus(rawValues.get("trang_thai_giay_xet_nghiem_ma_tuy")));
        approvals.put("ly_lich_tu_phap", normalizeApprovalStatus(rawValues.get("trang_thai_ly_lich_tu_phap")));
        approvals.put("giay_xet_nghiem_hiv", normalizeApprovalStatus(rawValues.get("trang_thai_giay_xet_nghiem_hiv")));
        approvals.put("xac_thuc_sdt", normalizeApprovalStatus(rawValues.get("trang_thai_xac_thuc_sdt")));
        return approvals;
    }

    private void validateRequired(Map<String, String> rawValues, int rowNumber, List<String> errors) {
        if (rawValues.get("ten") == null) {
            errors.add("Row " + rowNumber + ": field 'ten' is required.");
        }
        if (rawValues.get("sdt") == null) {
            errors.add("Row " + rowNumber + ": field 'sdt' is required.");
        }
    }

    private Integer normalizeGender(String value, int rowNumber, List<String> errors) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if ("nam".equals(normalized)) {
            return 1;
        }
        if ("nu".equals(normalized) || "nữ".equals(normalized)) {
            return 0;
        }
        errors.add("Row " + rowNumber + ": unsupported value for 'gioi_tinh'.");
        return null;
    }

    private Integer normalizeDriverStatus(String value) {
        if (value == null) {
            return null;
        }
        return switch (canonicalize(value)) {
            case "active", "da_ky" -> 1;
            case "ho_so_chua_dat", "inactive", "chua_ky" -> 0;
            default -> 0;
        };
    }

    private String normalizeContractStatus(String value) {
        if (value == null) {
            return null;
        }
        return switch (canonicalize(value)) {
            case "da_ky" -> "SIGNED";
            case "chua_ky" -> "UNSIGNED";
            default -> "UNKNOWN";
        };
    }

    private Integer normalizeApprovalStatus(String value) {
        if (value == null) {
            return null;
        }
        return switch (canonicalize(value)) {
            case "dat" -> 1;
            case "chua_dat" -> 0;
            case "cho_duyet" -> 3;
            default -> 2;
        };
    }

    private LocalDate parseDate(String value, String field, int rowNumber, List<String> errors) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            errors.add("Row " + rowNumber + ": field '" + field + "' must match yyyy-MM-dd.");
            return null;
        }
    }

    private LocalDateTime parseDateTime(String value, String field, int rowNumber, List<String> errors) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            errors.add("Row " + rowNumber + ": field '" + field + "' must match yyyy-MM-dd H:mm:ss.");
            return null;
        }
    }

    private Integer parseInteger(String value, String field, int rowNumber, List<String> errors) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            errors.add("Row " + rowNumber + ": field '" + field + "' must be an integer.");
            return null;
        }
    }

    private BigDecimal parseDecimal(String value, String field, int rowNumber, List<String> errors) {
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            errors.add("Row " + rowNumber + ": field '" + field + "' must be numeric.");
            return null;
        }
    }

    private List<String> splitMultiValue(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split(",")).stream()
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .toList();
    }

    private String normalizeDigitString(String value, String field, int rowNumber, List<String> errors) {
        if (value == null) {
            return null;
        }
        String digitsOnly = value.replaceAll("[^0-9]", "");
        if (digitsOnly.isEmpty()) {
            errors.add("Row " + rowNumber + ": field '" + field + "' must contain digits.");
            return null;
        }
        return digitsOnly;
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
    }

    private String cleanFilePath(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .replace("\\/", "/")
                .trim();
    }

    private String canonicalize(String value) {
        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replace("\u0110", "D")
                .replace("\u0111", "d")
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_");
        return normalized.replaceAll("^_+", "").replaceAll("_+$", "");
    }
}
