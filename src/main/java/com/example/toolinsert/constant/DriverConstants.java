package com.example.toolinsert.constant;

import java.util.List;

/**
 * Constants class chứa các giá trị cố định được sử dụng trong ứng dụng
 * Tránh magic numbers và strings trong code
 */
public final class DriverConstants {
    private DriverConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_INACTIVE = 0;

    public static final Integer DRIVER_STATUS_INACTIVE = 0;
    public static final Integer DRIVER_STATUS_ACTIVE = 1;
    public static final Integer DRIVER_STATUS_PENDING = 2;
    public static final Integer DRIVER_STATUS_SUSPENDED = 3;
    public static final Integer DRIVER_STATUS_REJECTED = 4;
    public static final Integer DRIVER_STATUS_VERIFICATION_REQUIRED = 5;

    public static final String PROPERTY_EMAIL = "EMAIL";
    public static final String PROPERTY_EMAIL_CUSTOMER = "EMAIL_CUSTOMER";
    public static final String PROPERTY_PASSWORD = "PASSWORD";
    public static final String PROPERTY_ADDRESS = "ADDRESS";
    public static final String PROPERTY_CURRENT_ADDRESS = "CURRENT_ADDRESS";
    public static final String PROPERTY_PERMANENT_ADDRESS = "PERMANENT_ADDRESS";
    public static final String PROPERTY_ADDRESS_CUSTOMER = "ADDRESS_CUSTOMER";
    public static final String PROPERTY_DISTRICT = "DISTRICT";
    public static final String PROPERTY_CURRENT_DISTRICT = "CURRENT_DISTRICT";
    public static final String PROPERTY_PERMANENT_DISTRICT = "PERMANENT_DISTRICT";
    public static final String PROPERTY_CITY = "CITY";
    public static final String PROPERTY_CURRENT_CITY = "CURRENT_CITY";
    public static final String PROPERTY_PERMANENT_CITY = "PERMANENT_CITY";
    public static final String PROPERTY_PROVINCE_ID = "CURRENT_PROVINCE_ID";
    public static final String PROPERTY_CURRENT_PROVINCE_ID = PROPERTY_PROVINCE_ID;
    public static final String PROPERTY_PERMANENT_PROVINCE_ID = "PERMANENT_PROVINCE_ID";
    public static final String PROPERTY_DISTRICT_ID = "CURRENT_DISTRICT_ID";
    public static final String PROPERTY_CURRENT_DISTRICT_ID = PROPERTY_DISTRICT_ID;
    public static final String PROPERTY_PERMANENT_DISTRICT_ID = "PERMANENT_DISTRICT_ID";
    public static final String PROPERTY_LICENSE_TYPE = "LICENSE_TYPE";
    public static final String PROPERTY_CCCD_ISSUE_DATE = "CCCD_ISSUE_DATE";
    public static final String PROPERTY_CUSTOMER_CCCD_ISSUE_DATE = "CUSTOMER_CCCD_ISSUE_DATE";
    public static final String PROPERTY_CCCD_ISSUE_PLACE = "CCCD_ISSUE_PLACE";
    public static final String PROPERTY_CCCD_PLACE = "CCCD_PLACE";

    public static final String PROPERTY_MOTORCYCLE_LICENSE_NUMBER = "MOTORCYCLE_LICENSE_NUMBER";
    public static final String PROPERTY_MOTORCYCLE_LICENSE_EXPIRY_DATE = "MOTORCYCLE_LICENSE_EXPIRY_DATE";
    public static final String PROPERTY_MOTORCYCLE_LICENSE_ISSUE_DATE = "MOTORCYCLE_LICENSE_ISSUE_DATE";
    public static final String PROPERTY_MOTORCYCLE_LICENSE_FRONT_IMAGE = "MOTORCYCLE_LICENSE_FRONT_IMAGE";
    public static final String PROPERTY_MOTORCYCLE_LICENSE_BACK_IMAGE = "MOTORCYCLE_LICENSE_BACK_IMAGE";

    public static final String PROPERTY_CAR_LICENSE_NUMBER = "CAR_LICENSE_NUMBER";
    public static final String PROPERTY_CAR_LICENSE_EXPIRY_DATE = "CAR_LICENSE_EXPIRY_DATE";
    public static final String PROPERTY_CAR_LICENSE_ISSUE_DATE = "CAR_LICENSE_ISSUE_DATE";
    public static final String PROPERTY_CAR_LICENSE_SERIAL_NUMBER = "CAR_LICENSE_SERIAL_NUMBER";
    public static final String PROPERTY_CAR_LICENSE_FRONT_IMAGE = "CAR_LICENSE_FRONT_IMAGE";
    public static final String PROPERTY_CAR_LICENSE_BACK_IMAGE = "CAR_LICENSE_BACK_IMAGE";
    public static final String PROPERTY_CCCD_FRONT_IMAGE = "CCCD_FRONT_IMAGE";
    public static final String PROPERTY_CCCD_BACK_IMAGE = "CCCD_BACK_IMAGE";

    public static final String PROPERTY_PHONE_VERIFICATION_CODE = "PHONE_VERIFICATION_CODE";
    public static final String PROPERTY_PHONE_VERIFICATION_OTP = "PHONE_VERIFICATION_OTP";
    public static final String PROPERTY_CCCD_EXPIRY_DATE = "CCCD_EXPIRY_DATE";
    public static final String PROPERTY_CUSTOMER_CCCD_EXPIRY_DATE = "CUSTOMER_CCCD_EXPIRY_DATE";
    public static final String PROPERTY_SERVICE_TYPE = "SERVICE_TYPE";
    public static final String PROPERTY_MOTORCYCLE_LICENSE_TYPE = "MOTORCYCLE_LICENSE_TYPE";
    public static final String PROPERTY_CAR_LICENSE_TYPE = "CAR_LICENSE_TYPE";
    public static final String PROPERTY_DRIVING_EXPERIENCE_YEARS = "DRIVING_EXPERIENCE_YEARS";
    public static final String PROPERTY_DRUG_TEST_CERTIFICATE = "DRUG_TEST_CERTIFICATE";
    public static final String PROPERTY_HIV_TEST_CERTIFICATE = "HIV_TEST_CERTIFICATE";
    public static final String PROPERTY_CRIMINAL_RECORD_CERTIFICATE = "CRIMINAL_RECORD_CERTIFICATE";
    public static final String PROPERTY_WORK_EXPERIENCE = "WORK_EXPERIENCE";

    public static final String PROPERTY_AVATAR = "AVATAR";
    public static final String PROPERTY_PROFILE_IMAGE = "PROFILE_IMAGE";
    public static final String PROPERTY_PERSONAL_IMAGE = "PERSONAL_IMAGE";
    public static final String PROPERTY_PORTRAIT_IMAGE = "PORTRAIT_IMAGE";

    public static final String SERVICE_TYPE_MOTORCYCLE = "MOTORCYCLE";
    public static final String SERVICE_TYPE_MOTOBIKE = "MOTOBIKE";
    public static final String SERVICE_TYPE_CAR = "CAR";
    public static final String SERVICE_TYPE_BOTH = "BOTH";

    public static final String CRITERIA_CODE_RATING_DRIVER = "RATING_DRIVER";

    public static final String DOCUMENT_TYPE_CCCD = "CCCD";
    public static final String DOCUMENT_TYPE_HEALTH = "HEALTH";
    public static final String DOCUMENT_TYPE_CRIMINAL = "CRIMINAL";

    public static final String MOTORCYCLE_LICENSE_A1 = "A1";
    public static final String MOTORCYCLE_LICENSE_A2 = "A2";
    public static final String CAR_LICENSE_B1 = "B1";
    public static final String CAR_LICENSE_B2 = "B2";

    public static final String LICENSE_STATUS_VALID = "VALID";
    public static final String LICENSE_STATUS_EXPIRING_SOON = "EXPIRING_SOON";
    public static final String LICENSE_STATUS_EXPIRED = "EXPIRED";
    public static final String LICENSE_STATUS_NOT_SET = "NOT_SET";

    public static final Integer DEFAULT_WARNING_DAYS_BEFORE_EXPIRY = 30;

    public static final String[] DOCUMENT_PROPERTY_CODES = {
            PROPERTY_CCCD_FRONT_IMAGE,
            PROPERTY_CCCD_BACK_IMAGE,
            PROPERTY_MOTORCYCLE_LICENSE_FRONT_IMAGE,
            PROPERTY_MOTORCYCLE_LICENSE_BACK_IMAGE,
            PROPERTY_CAR_LICENSE_FRONT_IMAGE,
            PROPERTY_CAR_LICENSE_BACK_IMAGE,
            PROPERTY_DRUG_TEST_CERTIFICATE,
            PROPERTY_HIV_TEST_CERTIFICATE,
            PROPERTY_CRIMINAL_RECORD_CERTIFICATE,
            PROPERTY_PHONE_VERIFICATION_CODE,
    };

    public static final String[] REQUIRED_REGISTRATION_DOCUMENT_CODES = {
            PROPERTY_CCCD_FRONT_IMAGE,
            PROPERTY_CCCD_BACK_IMAGE,
            PROPERTY_DRUG_TEST_CERTIFICATE,
            PROPERTY_HIV_TEST_CERTIFICATE,
            PROPERTY_CRIMINAL_RECORD_CERTIFICATE
    };

    public static final Integer DOCUMENT_STATUS_PENDING = 0;
    public static final Integer DOCUMENT_STATUS_APPROVED = 1;
    public static final Integer DOCUMENT_STATUS_REJECTED = 2;

    public static final List<Integer> DRIVER_LIFECYCLE_STATUSES = List.of(
            DRIVER_STATUS_INACTIVE,
            DRIVER_STATUS_ACTIVE,
            DRIVER_STATUS_PENDING,
            DRIVER_STATUS_SUSPENDED,
            DRIVER_STATUS_REJECTED,
            DRIVER_STATUS_VERIFICATION_REQUIRED);

    public static final Integer DEFAULT_FIELD_TYPE_ID = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer DEFAULT_PAGE_NO = 0;
    public static final Integer DEFAULT_DRIVER_PAGE_SIZE = 5;

    public static final String PROPERTIES_SERVICE_NAME = "PropertiesService";
    public static final String PROPERTIES_API_URL = "https://apigwdev.taixe247.vn/api/v1/common/properties";

    public static final Integer GENDER_MALE = 1;
    public static final Integer GENDER_FEMALE = 0;

    public static final String ERROR_ENTITY_NOT_FOUND = "Entity not found with id: %s";
    public static final String ERROR_DRIVER_NOT_FOUND = "Driver not found with id: %s";
    public static final String ERROR_DRIVER_BANK_ACCOUNT_NOT_FOUND =
            "Driver bank account not found with driverId: %s and bankAccId: %s";
    public static final String ERROR_DRIVER_DEVICE_NOT_FOUND =
            "Driver device not found with driverId: %s and deviceId: %s";
    public static final String ERROR_DRIVER_PARTY_NOT_FOUND =
            "Driver party not found with driverId: %s and partyId: %s";
    public static final String ERROR_DRIVER_SERVICE_NOT_FOUND =
            "Driver service not found with driverId: %s and serviceId: %s";
    public static final String ERROR_DRIVER_CLASS_NOT_FOUND =
            "Driver class not found with driverId: %s, classId: %s, serviceId: %s";
    public static final String ERROR_DRIVER_PROPERTY_NOT_FOUND =
            "Driver property not found with propertyId: %s and driverId: %s";
    public static final String ERROR_PASSWORD_MISMATCH = "Password and rePassword do not match";
    public static final String ERROR_PASSWORD_NULL_OR_EMPTY = "Password cannot be null or empty";
    public static final String ERROR_TYPE_RESOURCE_NOT_FOUND = "Resource Not Found";
    public static final String ERROR_TYPE_VALIDATION_FAILED = "Validation Failed";
    public static final String ERROR_TYPE_CONSTRAINT_VIOLATION = "Constraint Violation";
    public static final String ERROR_TYPE_INVALID_ARGUMENT = "Invalid Argument";
    public static final String ERROR_TYPE_INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String ERROR_TYPE_AUTH_ERROR = "Authentication Error";
    public static final String ERROR_TYPE_AUTH_SERVICE_ERROR = "Auth Service Error";
    public static final String ERROR_TYPE_AUTH_TOKEN_ERROR = "Auth Token Error";
}
