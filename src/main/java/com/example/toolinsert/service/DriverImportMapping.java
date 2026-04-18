package com.example.toolinsert.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import com.example.toolinsert.constant.DriverConstants;

public final class DriverImportMapping {

    public static final String HEADER_SOURCE_ID = "id";
    public static final String HEADER_FULL_NAME = "ten";
    public static final String HEADER_DOB = "ngay_sinh";
    public static final String HEADER_GENDER = "gioi_tinh";
    public static final String HEADER_IDENTITY_NUMBER = "cccd";
    public static final String HEADER_DRIVER_STATUS = "trang_thai_tai_xe";
    public static final String HEADER_CONTRACT_STATUS = "trang_thai_hop_dong";
    public static final String HEADER_REGION_NAME = "khu_vuc_hoat_dong";
    public static final String HEADER_REFERRER_SOURCE_ID = "nguoi_gioi_thieu";
    public static final String HEADER_PHONE = "sdt";
    public static final String HEADER_BANK_ACCOUNT_NUMBER = "stk";
    public static final String HEADER_BANK_ACCOUNT_NAME = "ten_nguoi_thu_huong";
    public static final String HEADER_BANK_NAME = "ten_ngan_hang";
    public static final String HEADER_BANK_CODE = "ngan_hang_viet_tat";
    public static final String HEADER_SERVICE_CODES = "loai_xe_dang_ky";
    public static final String HEADER_DRIVER_CLASS = "hang_tai_xe";
    public static final String HEADER_DRIVING_EXPERIENCE_YEARS = "kinh_nghiem_lai_xe";
    public static final String HEADER_BONUS_RATE = "muc_coc";
    public static final String HEADER_REGISTERED_AT = "ngay_dang_ky";
    public static final String HEADER_ACTIVATED_AT = "ngay_kich_hoat";

    public static final String PROPERTY_REGISTERED_AT = HEADER_REGISTERED_AT;
    public static final String PROPERTY_ACTIVATED_AT = HEADER_ACTIVATED_AT;

    public static final String HEADER_CCCD_ISSUE_DATE = "ngay_cap_cccd";
    public static final String HEADER_CCCD_ISSUE_PLACE = "noi_cap_cccd";
    public static final String HEADER_CCCD_EXPIRY_DATE = "ngay_het_han_cccd";
    public static final String HEADER_LICENSE_NUMBER = "so_gplx";
    public static final String HEADER_LICENSE_SERIAL_NUMBER = "so_seria_gplx";
    public static final String HEADER_LICENSE_ISSUE_DATE = "ngay_cap_gplx";
    public static final String HEADER_LICENSE_EXPIRY_DATE = "ngay_het_han_gplx";
    public static final String HEADER_PERMANENT_ADDRESS = "dia_chi";
    public static final String HEADER_CURRENT_ADDRESS = "dia_chi_hien_tai";
    public static final String HEADER_CCCD_FRONT_IMAGE = "cccd_mat_truoc";
    public static final String HEADER_CCCD_BACK_IMAGE = "cccd_mat_sau";
    public static final String HEADER_LICENSE_FRONT_IMAGE = "gplx_mat_truoc";
    public static final String HEADER_LICENSE_BACK_IMAGE = "gplx_mat_sau";
    public static final String HEADER_DRUG_TEST_CERTIFICATE = "giay_xet_nghiem_ma_tuy";
    public static final String HEADER_CRIMINAL_RECORD_CERTIFICATE = "ly_lich_tu_phap";
    public static final String HEADER_HIV_TEST_CERTIFICATE = "giay_xet_nghiem_hiv";

    public static final String HEADER_CCCD_FRONT_STATUS = "trang_thai_cccd_mat_truoc";
    public static final String HEADER_CCCD_BACK_STATUS = "trang_thai_cccd_mat_sau";
    public static final String HEADER_LICENSE_FRONT_STATUS = "trang_thai_gplx_mat_truoc";
    public static final String HEADER_LICENSE_BACK_STATUS = "trang_thai_gplx_mat_sau";
    public static final String HEADER_DRUG_TEST_STATUS = "trang_thai_giay_xet_nghiem_ma_tuy";
    public static final String HEADER_CRIMINAL_RECORD_STATUS = "trang_thai_ly_lich_tu_phap";
    public static final String HEADER_HIV_TEST_STATUS = "trang_thai_giay_xet_nghiem_hiv";
    public static final String HEADER_PHONE_VERIFICATION_STATUS = "trang_thai_xac_thuc_sdt";

    private static final Map<String, String> PROPERTY_CODES_BY_HEADER = Map.ofEntries(
            Map.entry(HEADER_CCCD_ISSUE_DATE, DriverConstants.PROPERTY_CCCD_ISSUE_DATE),
            Map.entry(HEADER_CCCD_ISSUE_PLACE, DriverConstants.PROPERTY_CCCD_ISSUE_PLACE),
            Map.entry(HEADER_CCCD_EXPIRY_DATE, DriverConstants.PROPERTY_CCCD_EXPIRY_DATE),
            Map.entry(HEADER_PERMANENT_ADDRESS, DriverConstants.PROPERTY_ADDRESS),
            Map.entry(HEADER_CURRENT_ADDRESS, DriverConstants.PROPERTY_CURRENT_ADDRESS),
            Map.entry(HEADER_CCCD_FRONT_IMAGE, DriverConstants.PROPERTY_CCCD_FRONT_IMAGE),
            Map.entry(HEADER_CCCD_BACK_IMAGE, DriverConstants.PROPERTY_CCCD_BACK_IMAGE),
            Map.entry(HEADER_DRUG_TEST_CERTIFICATE, DriverConstants.PROPERTY_DRUG_TEST_CERTIFICATE),
            Map.entry(HEADER_CRIMINAL_RECORD_CERTIFICATE, DriverConstants.PROPERTY_CRIMINAL_RECORD_CERTIFICATE),
            Map.entry(HEADER_HIV_TEST_CERTIFICATE, DriverConstants.PROPERTY_HIV_TEST_CERTIFICATE)
    );

    private static final Map<String, String> DOCUMENT_CODES_BY_HEADER = Map.ofEntries(
            Map.entry(HEADER_CCCD_FRONT_STATUS, DriverConstants.PROPERTY_CCCD_FRONT_IMAGE),
            Map.entry(HEADER_CCCD_BACK_STATUS, DriverConstants.PROPERTY_CCCD_BACK_IMAGE),
            Map.entry(HEADER_DRUG_TEST_STATUS, DriverConstants.PROPERTY_DRUG_TEST_CERTIFICATE),
            Map.entry(HEADER_CRIMINAL_RECORD_STATUS, DriverConstants.PROPERTY_CRIMINAL_RECORD_CERTIFICATE),
            Map.entry(HEADER_HIV_TEST_STATUS, DriverConstants.PROPERTY_HIV_TEST_CERTIFICATE),
            Map.entry(HEADER_PHONE_VERIFICATION_STATUS, DriverConstants.PROPERTY_PHONE_VERIFICATION_CODE)
    );

    private DriverImportMapping() {
    }

    public static String propertyCode(String header) {
        return PROPERTY_CODES_BY_HEADER.getOrDefault(header, header);
    }

    public static String licensePropertyCode(String header, String rawServiceCodes) {
        boolean useMotorcycleCode = useMotorcycleLicenseCode(rawServiceCodes);
        return switch (header) {
            case HEADER_LICENSE_NUMBER -> useMotorcycleCode
                    ? DriverConstants.PROPERTY_MOTORCYCLE_LICENSE_NUMBER
                    : DriverConstants.PROPERTY_CAR_LICENSE_NUMBER;
            case HEADER_LICENSE_SERIAL_NUMBER -> DriverConstants.PROPERTY_CAR_LICENSE_SERIAL_NUMBER;
            case HEADER_LICENSE_ISSUE_DATE -> useMotorcycleCode
                    ? DriverConstants.PROPERTY_MOTORCYCLE_LICENSE_ISSUE_DATE
                    : DriverConstants.PROPERTY_CAR_LICENSE_ISSUE_DATE;
            case HEADER_LICENSE_EXPIRY_DATE -> useMotorcycleCode
                    ? DriverConstants.PROPERTY_MOTORCYCLE_LICENSE_EXPIRY_DATE
                    : DriverConstants.PROPERTY_CAR_LICENSE_EXPIRY_DATE;
            case HEADER_LICENSE_FRONT_IMAGE -> useMotorcycleCode
                    ? DriverConstants.PROPERTY_MOTORCYCLE_LICENSE_FRONT_IMAGE
                    : DriverConstants.PROPERTY_CAR_LICENSE_FRONT_IMAGE;
            case HEADER_LICENSE_BACK_IMAGE -> useMotorcycleCode
                    ? DriverConstants.PROPERTY_MOTORCYCLE_LICENSE_BACK_IMAGE
                    : DriverConstants.PROPERTY_CAR_LICENSE_BACK_IMAGE;
            default -> propertyCode(header);
        };
    }

    public static String documentCode(String statusHeader, String rawServiceCodes) {
        if (HEADER_LICENSE_FRONT_STATUS.equals(statusHeader)) {
            return licensePropertyCode(HEADER_LICENSE_FRONT_IMAGE, rawServiceCodes);
        }
        if (HEADER_LICENSE_BACK_STATUS.equals(statusHeader)) {
            return licensePropertyCode(HEADER_LICENSE_BACK_IMAGE, rawServiceCodes);
        }
        return DOCUMENT_CODES_BY_HEADER.get(statusHeader);
    }

    private static boolean useMotorcycleLicenseCode(String rawServiceCodes) {
        List<String> tokens = splitServiceCodes(rawServiceCodes);
        boolean hasMotorcycle = tokens.stream().anyMatch(DriverImportMapping::isMotorcycleToken);
        boolean hasCar = tokens.stream().anyMatch(DriverImportMapping::isCarToken);
        return hasMotorcycle && !hasCar;
    }

    private static List<String> splitServiceCodes(String rawServiceCodes) {
        if (rawServiceCodes == null || rawServiceCodes.isBlank()) {
            return List.of();
        }
        return List.of(rawServiceCodes.split(",")).stream()
                .map(DriverImportMapping::canonicalize)
                .filter(token -> !token.isBlank())
                .toList();
    }

    private static boolean isMotorcycleToken(String token) {
        return token.equals("motorcycle")
                || token.equals("motobike")
                || token.equals("bike")
                || token.equals("xe_may");
    }

    private static boolean isCarToken(String token) {
        return token.equals("car")
                || token.equals("manual")
                || token.equals("automatic")
                || token.equals("oto")
                || token.equals("o_to");
    }

    private static String canonicalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replace("\u0110", "D")
                .replace("\u0111", "d")
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");
    }
}
