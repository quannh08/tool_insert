package com.example.toolinsert.service;

import com.example.toolinsert.model.NormalizedDriverImportRow;
import com.example.toolinsert.model.ParsedDelimitedRow;
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
import com.example.toolinsert.constant.DriverConstants;
import org.springframework.stereotype.Component;

@Component
public class DriverImportNormalizer {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");

    public NormalizedDriverImportRow normalize(ParsedDelimitedRow row) {
        Map<String, String> rawValues = row.values();
        Map<String, Object> normalized = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        normalized.put("source_id", rawValues.get(DriverImportMapping.HEADER_SOURCE_ID));
        normalized.put("full_name", normalizeText(rawValues.get(DriverImportMapping.HEADER_FULL_NAME)));
        normalized.put(
                "dob",
                parseDate(rawValues.get(DriverImportMapping.HEADER_DOB), DriverImportMapping.HEADER_DOB, row.rowNumber(), errors)
        );
        normalized.put(
                "gender",
                normalizeGender(rawValues.get(DriverImportMapping.HEADER_GENDER), row.rowNumber(), errors)
        );
        normalized.put(
                "identity_number",
                normalizeDigitString(
                        rawValues.get(DriverImportMapping.HEADER_IDENTITY_NUMBER),
                        DriverImportMapping.HEADER_IDENTITY_NUMBER,
                        row.rowNumber(),
                        errors
                )
        );
        normalized.put("driver_status", normalizeDriverStatus(rawValues.get(DriverImportMapping.HEADER_DRIVER_STATUS)));
        normalized.put("contract_status", normalizeContractStatus(rawValues.get(DriverImportMapping.HEADER_CONTRACT_STATUS)));
        normalized.put("region_name", normalizeText(rawValues.get(DriverImportMapping.HEADER_REGION_NAME)));
        normalized.put("referrer_source_id", normalizeText(rawValues.get(DriverImportMapping.HEADER_REFERRER_SOURCE_ID)));
        normalized.put(
                "phone",
                normalizeDigitString(rawValues.get(DriverImportMapping.HEADER_PHONE), DriverImportMapping.HEADER_PHONE, row.rowNumber(), errors)
        );
        normalized.put(
                "bank_account_number",
                normalizeDigitString(
                        rawValues.get(DriverImportMapping.HEADER_BANK_ACCOUNT_NUMBER),
                        DriverImportMapping.HEADER_BANK_ACCOUNT_NUMBER,
                        row.rowNumber(),
                        errors
                )
        );
        normalized.put("bank_account_name", normalizeText(rawValues.get(DriverImportMapping.HEADER_BANK_ACCOUNT_NAME)));
        normalized.put("bank_name", normalizeText(rawValues.get(DriverImportMapping.HEADER_BANK_NAME)));
        normalized.put("bank_code", normalizeText(rawValues.get(DriverImportMapping.HEADER_BANK_CODE)));
        normalized.put("service_codes", splitMultiValue(rawValues.get(DriverImportMapping.HEADER_SERVICE_CODES)));
        normalized.put("driver_class", normalizeText(rawValues.get(DriverImportMapping.HEADER_DRIVER_CLASS)));
        normalized.put(
                "driving_experience_years",
                parseInteger(
                        rawValues.get(DriverImportMapping.HEADER_DRIVING_EXPERIENCE_YEARS),
                        DriverImportMapping.HEADER_DRIVING_EXPERIENCE_YEARS,
                        row.rowNumber(),
                        errors
                )
        );
        normalized.put(
                "bonus_rate",
                parseDecimal(rawValues.get(DriverImportMapping.HEADER_BONUS_RATE), DriverImportMapping.HEADER_BONUS_RATE, row.rowNumber(), errors)
        );
        normalized.put(
                "registered_at",
                parseDateTime(
                        rawValues.get(DriverImportMapping.HEADER_REGISTERED_AT),
                        DriverImportMapping.HEADER_REGISTERED_AT,
                        row.rowNumber(),
                        errors
                )
        );
        normalized.put(
                "activated_at",
                parseDateTime(
                        rawValues.get(DriverImportMapping.HEADER_ACTIVATED_AT),
                        DriverImportMapping.HEADER_ACTIVATED_AT,
                        row.rowNumber(),
                        errors
                )
        );
        normalized.put("properties", normalizePropertyValues(rawValues, row.rowNumber(), errors));
        normalized.put("document_approvals", normalizeDocumentApprovals(rawValues));

        validateRequired(rawValues, row.rowNumber(), errors);
        return new NormalizedDriverImportRow(
                row.rowNumber(),
                rawValues.get(DriverImportMapping.HEADER_SOURCE_ID),
                normalized,
                List.copyOf(errors)
        );
    }

    private Map<String, Object> normalizePropertyValues(Map<String, String> rawValues, int rowNumber, List<String> errors) {
        Map<String, Object> properties = new LinkedHashMap<>();
        String rawServiceCodes = rawValues.get(DriverImportMapping.HEADER_SERVICE_CODES);
        properties.put(
                DriverImportMapping.propertyCode(DriverImportMapping.HEADER_CCCD_ISSUE_DATE),
                parseDate(
                        rawValues.get(DriverImportMapping.HEADER_CCCD_ISSUE_DATE),
                        DriverImportMapping.HEADER_CCCD_ISSUE_DATE,
                        rowNumber,
                        errors
                )
        );
        properties.put(
                DriverImportMapping.propertyCode(DriverImportMapping.HEADER_CCCD_ISSUE_PLACE),
                normalizeText(rawValues.get(DriverImportMapping.HEADER_CCCD_ISSUE_PLACE))
        );
        properties.put(
                DriverImportMapping.propertyCode(DriverImportMapping.HEADER_CCCD_EXPIRY_DATE),
                parseDate(
                        rawValues.get(DriverImportMapping.HEADER_CCCD_EXPIRY_DATE),
                        DriverImportMapping.HEADER_CCCD_EXPIRY_DATE,
                        rowNumber,
                        errors
                )
        );
        properties.put(
                DriverImportMapping.licensePropertyCode(DriverImportMapping.HEADER_LICENSE_NUMBER, rawServiceCodes),
                normalizeText(rawValues.get(DriverImportMapping.HEADER_LICENSE_NUMBER))
        );
        properties.put(
                DriverImportMapping.licensePropertyCode(DriverImportMapping.HEADER_LICENSE_SERIAL_NUMBER, rawServiceCodes),
                normalizeText(rawValues.get(DriverImportMapping.HEADER_LICENSE_SERIAL_NUMBER))
        );
        properties.put(
                DriverImportMapping.licensePropertyCode(DriverImportMapping.HEADER_LICENSE_ISSUE_DATE, rawServiceCodes),
                parseDate(
                        rawValues.get(DriverImportMapping.HEADER_LICENSE_ISSUE_DATE),
                        DriverImportMapping.HEADER_LICENSE_ISSUE_DATE,
                        rowNumber,
                        errors
                )
        );
        properties.put(
                DriverImportMapping.licensePropertyCode(DriverImportMapping.HEADER_LICENSE_EXPIRY_DATE, rawServiceCodes),
                parseDate(
                        rawValues.get(DriverImportMapping.HEADER_LICENSE_EXPIRY_DATE),
                        DriverImportMapping.HEADER_LICENSE_EXPIRY_DATE,
                        rowNumber,
                        errors
                )
        );
        properties.put(
                DriverImportMapping.propertyCode(DriverImportMapping.HEADER_PERMANENT_ADDRESS),
                normalizeText(rawValues.get(DriverImportMapping.HEADER_PERMANENT_ADDRESS))
        );
        properties.put(
                DriverImportMapping.propertyCode(DriverImportMapping.HEADER_CURRENT_ADDRESS),
                normalizeText(rawValues.get(DriverImportMapping.HEADER_CURRENT_ADDRESS))
        );
        properties.put(
                DriverImportMapping.propertyCode(DriverImportMapping.HEADER_CCCD_FRONT_IMAGE),
                cleanFilePath(rawValues.get(DriverImportMapping.HEADER_CCCD_FRONT_IMAGE))
        );
        properties.put(
                DriverImportMapping.propertyCode(DriverImportMapping.HEADER_CCCD_BACK_IMAGE),
                cleanFilePath(rawValues.get(DriverImportMapping.HEADER_CCCD_BACK_IMAGE))
        );
        properties.put(
                DriverImportMapping.licensePropertyCode(DriverImportMapping.HEADER_LICENSE_FRONT_IMAGE, rawServiceCodes),
                cleanFilePath(rawValues.get(DriverImportMapping.HEADER_LICENSE_FRONT_IMAGE))
        );
        properties.put(
                DriverImportMapping.licensePropertyCode(DriverImportMapping.HEADER_LICENSE_BACK_IMAGE, rawServiceCodes),
                cleanFilePath(rawValues.get(DriverImportMapping.HEADER_LICENSE_BACK_IMAGE))
        );
        properties.put(
                DriverImportMapping.propertyCode(DriverImportMapping.HEADER_DRUG_TEST_CERTIFICATE),
                cleanFilePath(rawValues.get(DriverImportMapping.HEADER_DRUG_TEST_CERTIFICATE))
        );
        properties.put(
                DriverImportMapping.propertyCode(DriverImportMapping.HEADER_CRIMINAL_RECORD_CERTIFICATE),
                cleanFilePath(rawValues.get(DriverImportMapping.HEADER_CRIMINAL_RECORD_CERTIFICATE))
        );
        properties.put(
                DriverImportMapping.propertyCode(DriverImportMapping.HEADER_HIV_TEST_CERTIFICATE),
                cleanFilePath(rawValues.get(DriverImportMapping.HEADER_HIV_TEST_CERTIFICATE))
        );
        return properties;
    }

    private Map<String, Object> normalizeDocumentApprovals(Map<String, String> rawValues) {
        Map<String, Object> approvals = new LinkedHashMap<>();
        String rawServiceCodes = rawValues.get(DriverImportMapping.HEADER_SERVICE_CODES);
        putDocumentApproval(approvals, DriverImportMapping.HEADER_CCCD_FRONT_STATUS, rawValues, rawServiceCodes);
        putDocumentApproval(approvals, DriverImportMapping.HEADER_CCCD_BACK_STATUS, rawValues, rawServiceCodes);
        putDocumentApproval(approvals, DriverImportMapping.HEADER_LICENSE_FRONT_STATUS, rawValues, rawServiceCodes);
        putDocumentApproval(approvals, DriverImportMapping.HEADER_LICENSE_BACK_STATUS, rawValues, rawServiceCodes);
        putDocumentApproval(approvals, DriverImportMapping.HEADER_DRUG_TEST_STATUS, rawValues, rawServiceCodes);
        putDocumentApproval(approvals, DriverImportMapping.HEADER_CRIMINAL_RECORD_STATUS, rawValues, rawServiceCodes);
        putDocumentApproval(approvals, DriverImportMapping.HEADER_HIV_TEST_STATUS, rawValues, rawServiceCodes);
        putDocumentApproval(approvals, DriverImportMapping.HEADER_PHONE_VERIFICATION_STATUS, rawValues, rawServiceCodes);
        return approvals;
    }

    private void putDocumentApproval(
            Map<String, Object> approvals,
            String header,
            Map<String, String> rawValues,
            String rawServiceCodes
    ) {
        String documentCode = DriverImportMapping.documentCode(header, rawServiceCodes);
        if (documentCode == null) {
            return;
        }
        approvals.put(documentCode, normalizeApprovalStatus(rawValues.get(header)));
    }

    private void validateRequired(Map<String, String> rawValues, int rowNumber, List<String> errors) {
        if (rawValues.get(DriverImportMapping.HEADER_FULL_NAME) == null) {
            errors.add("Row " + rowNumber + ": field 'ten' is required.");
        }
        if (rawValues.get(DriverImportMapping.HEADER_PHONE) == null) {
            errors.add("Row " + rowNumber + ": field 'sdt' is required.");
        }
    }

    private Integer normalizeGender(String value, int rowNumber, List<String> errors) {
        if (value == null) {
            return null;
        }
        String normalized = canonicalize(value);
        if ("nam".equals(normalized)) {
            return DriverConstants.GENDER_MALE;
        }
        if ("nu".equals(normalized)) {
            return DriverConstants.GENDER_FEMALE;
        }
        errors.add("Row " + rowNumber + ": unsupported value for 'gioi_tinh'.");
        return null;
    }

    private Integer normalizeDriverStatus(String value) {
        if (value == null) {
            return null;
        }
        return switch (canonicalize(value)) {
            case "active", "da_ky" -> DriverConstants.DRIVER_STATUS_ACTIVE;
            case "ho_so_chua_dat", "inactive", "chua_ky" -> DriverConstants.DRIVER_STATUS_INACTIVE;
            default -> DriverConstants.DRIVER_STATUS_INACTIVE;
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
            case "dat" -> DriverConstants.DOCUMENT_STATUS_APPROVED;
            case "cho_duyet" -> DriverConstants.DOCUMENT_STATUS_PENDING;
            case "chua_dat" -> DriverConstants.DOCUMENT_STATUS_REJECTED;
            default -> DriverConstants.DOCUMENT_STATUS_REJECTED;
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
