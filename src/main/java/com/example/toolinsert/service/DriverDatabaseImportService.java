package com.example.toolinsert.service;

import com.example.toolinsert.entity.DBankAccEntity;
import com.example.toolinsert.entity.DClassEntity;
import com.example.toolinsert.entity.DCriteriaEntity;
import com.example.toolinsert.entity.DDriverBankAccEntity;
import com.example.toolinsert.entity.DDriverClassEntity;
import com.example.toolinsert.entity.DDriverDocumentApprovalEntity;
import com.example.toolinsert.entity.DDriverEntity;
import com.example.toolinsert.entity.DDriverMetricEntity;
import com.example.toolinsert.entity.DDriverPartyEntity;
import com.example.toolinsert.entity.DDriverPropertyEntity;
import com.example.toolinsert.entity.DDriverServiceEntity;
import com.example.toolinsert.entity.DPartyEntity;
import com.example.toolinsert.entity.DPropertyEntity;
import com.example.toolinsert.entity.DRegionEntity;
import com.example.toolinsert.entity.DServiceEntity;
import com.example.toolinsert.model.DriverImportError;
import com.example.toolinsert.model.NormalizedDriverImportRow;
import com.example.toolinsert.repository.BankAccountRepository;
import com.example.toolinsert.repository.CriteriaRepository;
import com.example.toolinsert.repository.DriverBankAccountRepository;
import com.example.toolinsert.repository.DriverClassAssignmentRepository;
import com.example.toolinsert.repository.DriverClassRepository;
import com.example.toolinsert.repository.DriverDocumentApprovalRepository;
import com.example.toolinsert.repository.DriverMetricRepository;
import com.example.toolinsert.repository.DriverPartyRepository;
import com.example.toolinsert.repository.DriverPropertyRepository;
import com.example.toolinsert.repository.DriverRepository;
import com.example.toolinsert.repository.DriverServiceRepository;
import com.example.toolinsert.repository.PartyRepository;
import com.example.toolinsert.repository.PropertyRepository;
import com.example.toolinsert.repository.RegionRepository;
import com.example.toolinsert.repository.ServiceRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.CRC32;
import com.example.toolinsert.constant.DriverConstants;
import com.example.toolinsert.entity.id.DDriverBankAccId;
import com.example.toolinsert.entity.id.DDriverClassId;
import com.example.toolinsert.entity.id.DDriverPartyId;
import com.example.toolinsert.entity.id.DDriverPropertyId;
import com.example.toolinsert.entity.id.DDriverServiceId;
import org.springframework.stereotype.Service;

@Service
public class DriverDatabaseImportService {

    private static final String METRIC_DRIVING_EXPERIENCE = "kinh_nghiem_lai_xe";

    private final DriverRepository driverRepository;
    private final PartyRepository partyRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ServiceRepository serviceRepository;
    private final DriverClassRepository driverClassRepository;
    private final RegionRepository regionRepository;
    private final PropertyRepository propertyRepository;
    private final CriteriaRepository criteriaRepository;
    private final DriverPartyRepository driverPartyRepository;
    private final DriverBankAccountRepository driverBankAccountRepository;
    private final DriverServiceRepository driverServiceRepository;
    private final DriverClassAssignmentRepository driverClassAssignmentRepository;
    private final DriverPropertyRepository driverPropertyRepository;
    private final DriverDocumentApprovalRepository driverDocumentApprovalRepository;
    private final DriverMetricRepository driverMetricRepository;
    private final DriverBatchInsertService driverBatchInsertService;

    public DriverDatabaseImportService(
            DriverRepository driverRepository,
            PartyRepository partyRepository,
            BankAccountRepository bankAccountRepository,
            ServiceRepository serviceRepository,
            DriverClassRepository driverClassRepository,
            RegionRepository regionRepository,
            PropertyRepository propertyRepository,
            CriteriaRepository criteriaRepository,
            DriverPartyRepository driverPartyRepository,
            DriverBankAccountRepository driverBankAccountRepository,
            DriverServiceRepository driverServiceRepository,
            DriverClassAssignmentRepository driverClassAssignmentRepository,
            DriverPropertyRepository driverPropertyRepository,
            DriverDocumentApprovalRepository driverDocumentApprovalRepository,
            DriverMetricRepository driverMetricRepository,
            DriverBatchInsertService driverBatchInsertService
    ) {
        this.driverRepository = driverRepository;
        this.partyRepository = partyRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.serviceRepository = serviceRepository;
        this.driverClassRepository = driverClassRepository;
        this.regionRepository = regionRepository;
        this.propertyRepository = propertyRepository;
        this.criteriaRepository = criteriaRepository;
        this.driverPartyRepository = driverPartyRepository;
        this.driverBankAccountRepository = driverBankAccountRepository;
        this.driverServiceRepository = driverServiceRepository;
        this.driverClassAssignmentRepository = driverClassAssignmentRepository;
        this.driverPropertyRepository = driverPropertyRepository;
        this.driverDocumentApprovalRepository = driverDocumentApprovalRepository;
        this.driverMetricRepository = driverMetricRepository;
        this.driverBatchInsertService = driverBatchInsertService;
    }

    public List<DriverImportError> importValidatedRows(List<NormalizedDriverImportRow> normalizedRows) {
        List<DriverImportError> errors = new ArrayList<>();

        // Load master data once so every row can resolve foreign keys in memory before linking child tables.
        Map<String, DRegionEntity> regions = indexRegions(regionRepository.findAll());
        Map<String, DServiceEntity> services = indexServices(serviceRepository.findAll());
        Map<String, DClassEntity> classes = indexDriverClasses(driverClassRepository.findAll());
        Map<String, DPropertyEntity> properties = indexProperties(propertyRepository.findAll());
        Map<String, DCriteriaEntity> criteria = indexCriteria(criteriaRepository.findAll());
        Map<String, DPartyEntity> partyCache = new LinkedHashMap<>();
        Map<String, DBankAccEntity> bankAccountCache = new LinkedHashMap<>();
        Map<String, DDriverEntity> importedDriversBySourceId = new LinkedHashMap<>();
        Map<DDriverPartyId, DDriverPartyEntity> driverPartyLinks = new LinkedHashMap<>();
        Map<DDriverBankAccId, DDriverBankAccEntity> driverBankAccountLinks = new LinkedHashMap<>();
        Map<DDriverServiceId, DDriverServiceEntity> driverServiceLinks = new LinkedHashMap<>();
        Map<DDriverClassId, DDriverClassEntity> driverClassLinks = new LinkedHashMap<>();
        Map<DDriverPropertyId, DDriverPropertyEntity> driverProperties = new LinkedHashMap<>();
        Map<String, DDriverDocumentApprovalEntity> documentApprovals = new LinkedHashMap<>();
        Map<String, DDriverMetricEntity> driverMetrics = new LinkedHashMap<>();
        List<RowContext> contexts = new ArrayList<>();

        for (NormalizedDriverImportRow row : normalizedRows) {
            try {
                RowContext context = importRow(
                        row,
                        regions,
                        services,
                        classes,
                        properties,
                        criteria,
                        partyCache,
                        bankAccountCache,
                        driverPartyLinks,
                        driverBankAccountLinks,
                        driverServiceLinks,
                        driverClassLinks,
                        driverProperties,
                        documentApprovals,
                        driverMetrics
                );
                contexts.add(context);
                if (row.sourceId() != null && !row.sourceId().isBlank()) {
                    importedDriversBySourceId.put(row.sourceId().trim(), context.driver());
                }
            } catch (Exception exception) {
                errors.add(new DriverImportError(row.rowNumber(), rootCauseMessage(exception)));
            }
        }

        // Referrers depend on imported drivers being known first, so update them after driver creation.
        updateReferrers(contexts, importedDriversBySourceId);
        saveLinkTables(
                driverPartyLinks,
                driverBankAccountLinks,
                driverServiceLinks,
                driverClassLinks,
                driverProperties,
                documentApprovals,
                driverMetrics
        );
        return errors;
    }

    private RowContext importRow(
            NormalizedDriverImportRow row,
            Map<String, DRegionEntity> regions,
            Map<String, DServiceEntity> services,
            Map<String, DClassEntity> classes,
            Map<String, DPropertyEntity> properties,
            Map<String, DCriteriaEntity> criteria,
            Map<String, DPartyEntity> partyCache,
            Map<String, DBankAccEntity> bankAccountCache,
            Map<DDriverPartyId, DDriverPartyEntity> driverPartyLinks,
            Map<DDriverBankAccId, DDriverBankAccEntity> driverBankAccountLinks,
            Map<DDriverServiceId, DDriverServiceEntity> driverServiceLinks,
            Map<DDriverClassId, DDriverClassEntity> driverClassLinks,
            Map<DDriverPropertyId, DDriverPropertyEntity> driverProperties,
            Map<String, DDriverDocumentApprovalEntity> documentApprovals,
            Map<String, DDriverMetricEntity> driverMetrics
    ) {
        Map<String, Object> values = row.normalizedValues();
        DPartyEntity party = resolveParty(stringValue(values.get("phone")), partyCache);
        DRegionEntity region = resolveRegion(stringValue(values.get("region_name")), regions);
        DDriverEntity driver = resolveDriver(values, region);
        List<DServiceEntity> resolvedServices = resolveServices(values.get("service_codes"), services);
        DClassEntity driverClass = resolveDriverClass(
                stringValue(values.get("driver_class")),
                bigDecimalValue(values.get("bonus_rate")),
                classes
        );
        DBankAccEntity bankAccount = resolveBankAccount(values, bankAccountCache);

        if (party != null) {
            DDriverPartyEntity link = new DDriverPartyEntity();
            link.setDriverId(driver.getDriverId());
            link.setPartyId(party.getPartyId());
            link.setStatus(1);
            driverPartyLinks.put(new DDriverPartyId(driver.getDriverId(), party.getPartyId()), link);
        }

        if (bankAccount != null) {
            DDriverBankAccEntity link = new DDriverBankAccEntity();
            link.setDriverId(driver.getDriverId());
            link.setBankAccId(bankAccount.getBankAccId());
            link.setStatus(0);
            driverBankAccountLinks.put(new DDriverBankAccId(driver.getDriverId(), bankAccount.getBankAccId()), link);
        }

        for (DServiceEntity service : resolvedServices) {
            DDriverServiceEntity driverService = new DDriverServiceEntity();
            driverService.setDriverId(driver.getDriverId());
            driverService.setServiceId(service.getServiceId());
            driverService.setStatus(1);
            driverServiceLinks.put(new DDriverServiceId(driver.getDriverId(), service.getServiceId()), driverService);

            if (driverClass != null) {
                DDriverClassEntity assignment = new DDriverClassEntity();
                assignment.setDriverId(driver.getDriverId());
                assignment.setClassId(driverClass.getClassId());
                assignment.setServiceId(service.getServiceId());
                assignment.setBonusRate(bigDecimalValue(values.get("bonus_rate")));
                assignment.setStatus(1);
                driverClassLinks.put(
                        new DDriverClassId(driver.getDriverId(), driverClass.getClassId(), service.getServiceId()),
                        assignment
                );
            }
        }

        addPropertyRows(driver, values, properties, driverProperties);
        addDocumentApprovalRows(driver, values, documentApprovals);
        addMetricRows(driver, values, criteria, driverMetrics);
        return new RowContext(row, driver, values);
    }

    private void updateReferrers(List<RowContext> contexts, Map<String, DDriverEntity> importedDriversBySourceId) {
        List<DDriverEntity> driversToUpdate = new ArrayList<>();
        for (RowContext context : contexts) {
            String referrerSourceId = stringValue(context.values().get("referrer_source_id"));
            if (referrerSourceId == null) {
                continue;
            }

            DDriverEntity referrer = importedDriversBySourceId.get(referrerSourceId);
            if (referrer == null || Objects.equals(referrer.getDriverId(), context.driver().getDriverId())) {
                continue;
            }

            context.driver().setReferrerId(referrer.getDriverId());
            driversToUpdate.add(context.driver());
        }

        driverBatchInsertService.saveInBatches(driversToUpdate, driverRepository);
    }

    private void saveLinkTables(
            Map<DDriverPartyId, DDriverPartyEntity> driverPartyLinks,
            Map<DDriverBankAccId, DDriverBankAccEntity> driverBankAccountLinks,
            Map<DDriverServiceId, DDriverServiceEntity> driverServiceLinks,
            Map<DDriverClassId, DDriverClassEntity> driverClassLinks,
            Map<DDriverPropertyId, DDriverPropertyEntity> driverProperties,
            Map<String, DDriverDocumentApprovalEntity> documentApprovals,
            Map<String, DDriverMetricEntity> driverMetrics
    ) {
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverPartyLinks.values()), driverPartyRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverBankAccountLinks.values()), driverBankAccountRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverServiceLinks.values()), driverServiceRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverClassLinks.values()), driverClassAssignmentRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverProperties.values()), driverPropertyRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(documentApprovals.values()), driverDocumentApprovalRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverMetrics.values()), driverMetricRepository);
    }

    private DDriverEntity resolveDriver(Map<String, Object> values, DRegionEntity region) {
        String identityNumber = stringValue(values.get("identity_number"));
        String fullName = stringValue(values.get("full_name"));
        LocalDateTime dob = toLocalDateTime(values.get("dob"));
        DDriverEntity driver = null;

        if (identityNumber != null) {
            driver = driverRepository.findByIdentityNumber(identityNumber).orElse(null);
        }
        if (driver == null && fullName != null && dob != null) {
            driver = driverRepository.findByFullNameAndDob(fullName, dob).orElse(null);
        }
        if (driver == null) {
            driver = new DDriverEntity();
        }

        driver.setFullName(fullName);
        driver.setDob(dob);
        driver.setGender(integerValue(values.get("gender")));
        driver.setIdentityNumber(identityNumber);
        driver.setStatus(integerValue(values.get("driver_status")));
        driver.setRegionId(region == null ? null : region.getRegionId());
        return driverRepository.save(driver);
    }

    private DPartyEntity resolveParty(String phone, Map<String, DPartyEntity> partyCache) {
        if (phone == null) {
            return null;
        }
        return partyCache.computeIfAbsent(phone, key -> {
            DPartyEntity party = partyRepository.findByPhone(key).orElseGet(DPartyEntity::new);
            party.setPhone(key);
            if (party.getStatus() == null) {
                party.setStatus(1);
            }
            return partyRepository.save(party);
        });
    }

    private DBankAccEntity resolveBankAccount(Map<String, Object> values, Map<String, DBankAccEntity> bankAccountCache) {
        String accountNumber = stringValue(values.get("bank_account_number"));
        if (accountNumber == null) {
            return null;
        }

        Integer bankId = resolveBankId(stringValue(values.get("bank_code")), stringValue(values.get("bank_name")));
        String cacheKey = bankId + "|" + accountNumber;
        return bankAccountCache.computeIfAbsent(cacheKey, key -> {
            DBankAccEntity bankAccount = bankAccountRepository.findByBankIdAndAccNumber(bankId, accountNumber)
                    .orElseGet(DBankAccEntity::new);
            bankAccount.setBankId(bankId);
            bankAccount.setAccNumber(accountNumber);
            bankAccount.setAccName(stringValue(values.get("bank_account_name")));
            if (bankAccount.getIsActived() == null) {
                bankAccount.setIsActived(1);
            }
            return bankAccountRepository.save(bankAccount);
        });
    }

    private DRegionEntity resolveRegion(String regionName, Map<String, DRegionEntity> regions) {
        if (regionName == null) {
            return null;
        }
        String key = canonicalize(regionName);
        DRegionEntity existing = regions.get(key);
        if (existing != null) {
            return existing;
        }

        DRegionEntity region = new DRegionEntity();
        region.setCode(regionName.trim());
        region.setName(regionName.trim());
        region.setStatus(1);
        DRegionEntity saved = regionRepository.save(region);
        regions.put(canonicalize(saved.getCode()), saved);
        regions.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private List<DServiceEntity> resolveServices(Object rawServices, Map<String, DServiceEntity> services) {
        List<String> serviceCodes = stringList(rawServices);
        if (serviceCodes.isEmpty()) {
            return List.of();
        }

        List<DServiceEntity> resolved = new ArrayList<>();
        Set<Integer> seenIds = new LinkedHashSet<>();
        for (String serviceCode : serviceCodes) {
            String key = canonicalize(serviceCode);
            DServiceEntity service = services.get(key);
            if (service == null) {
                service = new DServiceEntity();
                service.setCode(serviceCode);
                service.setName(serviceCode);
                service.setStatus(1);
                service = serviceRepository.save(service);
                services.put(canonicalize(service.getCode()), service);
                services.put(canonicalize(service.getName()), service);
            }
            if (seenIds.add(service.getServiceId())) {
                resolved.add(service);
            }
        }
        return resolved;
    }

    private DClassEntity resolveDriverClass(String className, BigDecimal bonusRate, Map<String, DClassEntity> classes) {
        if (className == null) {
            return null;
        }
        String key = canonicalize(className);
        DClassEntity driverClass = classes.get(key);
        if (driverClass == null) {
            driverClass = new DClassEntity();
            driverClass.setCode(className);
            driverClass.setName(className);
            driverClass.setStatus(1);
        }
        if (driverClass.getBonusRate() == null) {
            driverClass.setBonusRate(bonusRate);
        }
        DClassEntity saved = driverClassRepository.save(driverClass);
        classes.put(canonicalize(saved.getCode()), saved);
        classes.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private void addPropertyRows(
            DDriverEntity driver,
            Map<String, Object> values,
            Map<String, DPropertyEntity> properties,
            Map<DDriverPropertyId, DDriverPropertyEntity> driverProperties
    ) {
        Map<String, Object> propertyValues = new LinkedHashMap<>(mapValue(values.get("properties")));
        propertyValues.put(DriverImportMapping.PROPERTY_REGISTERED_AT, values.get("registered_at"));
        propertyValues.put(DriverImportMapping.PROPERTY_ACTIVATED_AT, values.get("activated_at"));

        for (Map.Entry<String, Object> entry : propertyValues.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            DPropertyEntity property = resolveProperty(entry.getKey(), properties);
            DDriverPropertyEntity driverProperty = new DDriverPropertyEntity();
            driverProperty.setDriverId(driver.getDriverId());
            driverProperty.setPropertyId(property.getPropertyId());
            driverProperty.setStatus(1);
            applyPropertyValue(driverProperty, value);
            driverProperties.put(new DDriverPropertyId(property.getPropertyId(), driver.getDriverId()), driverProperty);
        }
    }

    private void addDocumentApprovalRows(
            DDriverEntity driver,
            Map<String, Object> values,
            Map<String, DDriverDocumentApprovalEntity> documentApprovals
    ) {
        Map<String, Object> approvals = mapValue(values.get("document_approvals"));
        for (Map.Entry<String, Object> entry : approvals.entrySet()) {
            Integer status = integerValue(entry.getValue());
            if (status == null) {
                continue;
            }

            DDriverDocumentApprovalEntity approval = new DDriverDocumentApprovalEntity();
            approval.setDriverId(driver.getDriverId());
            approval.setDocumentType(entry.getKey());
            approval.setStatus(status);
            documentApprovals.put(driver.getDriverId() + "|" + entry.getKey(), approval);
        }
    }

    private void addMetricRows(
            DDriverEntity driver,
            Map<String, Object> values,
            Map<String, DCriteriaEntity> criteria,
            Map<String, DDriverMetricEntity> driverMetrics
    ) {
        BigDecimal drivingExperience = bigDecimalValue(values.get("driving_experience_years"));
        if (drivingExperience == null) {
            return;
        }

        DCriteriaEntity criterion = resolveCriteria(METRIC_DRIVING_EXPERIENCE, criteria);
        DDriverMetricEntity metric = new DDriverMetricEntity();
        metric.setDriverId(driver.getDriverId());
        metric.setCriteriaId(criterion.getCriteriaId());
        metric.setValue(drivingExperience);
        driverMetrics.put(driver.getDriverId() + "|" + criterion.getCriteriaId(), metric);
    }

    private DPropertyEntity resolveProperty(String code, Map<String, DPropertyEntity> properties) {
        String key = canonicalize(code);
        DPropertyEntity property = properties.get(key);
        if (property != null) {
            return property;
        }

        DPropertyEntity created = new DPropertyEntity();
        created.setCode(code);
        created.setName(code);
        created.setFieldTypeId(DriverConstants.DEFAULT_FIELD_TYPE_ID);
        created.setMandatory(0);
        created.setIsActive(DriverConstants.STATUS_ACTIVE);
        DPropertyEntity saved = propertyRepository.save(created);
        properties.put(canonicalize(saved.getCode()), saved);
        properties.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private DCriteriaEntity resolveCriteria(String code, Map<String, DCriteriaEntity> criteria) {
        String key = canonicalize(code);
        DCriteriaEntity criterion = criteria.get(key);
        if (criterion != null) {
            return criterion;
        }

        DCriteriaEntity created = new DCriteriaEntity();
        created.setCode(code);
        created.setName(code);
        created.setStatus(1);
        DCriteriaEntity saved = criteriaRepository.save(created);
        criteria.put(canonicalize(saved.getCode()), saved);
        criteria.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private void applyPropertyValue(DDriverPropertyEntity driverProperty, Object value) {
        LocalDateTime dateValue = toLocalDateTime(value);
        if (dateValue != null) {
            driverProperty.setValueDate(dateValue);
            return;
        }

        BigDecimal numberValue = bigDecimalValue(value);
        if (numberValue != null && numberValue.scale() <= 0) {
            driverProperty.setValueNumber(numberValue.longValue());
            return;
        }

        String textValue = stringValue(value);
        if (textValue == null) {
            return;
        }
        if (looksLikeFilePath(textValue)) {
            driverProperty.setValueFile(textValue);
        } else {
            driverProperty.setValueText(textValue);
        }
    }

    private boolean looksLikeFilePath(String value) {
        return value.contains("/") || value.contains("\\");
    }

    private Integer resolveBankId(String bankCode, String bankName) {
        String keySource = bankCode != null ? bankCode : bankName;
        String stableKey = canonicalize(keySource == null ? "unknown_bank" : keySource);
        CRC32 crc32 = new CRC32();
        crc32.update(stableKey.getBytes(StandardCharsets.UTF_8));
        long bankId = crc32.getValue();
        return (int) (bankId == 0 ? 1L : bankId);
    }

    private Map<String, DRegionEntity> indexRegions(List<DRegionEntity> entities) {
        Map<String, DRegionEntity> index = new LinkedHashMap<>();
        for (DRegionEntity entity : entities) {
            index.put(canonicalize(entity.getCode()), entity);
            index.put(canonicalize(entity.getName()), entity);
        }
        return index;
    }

    private Map<String, DServiceEntity> indexServices(List<DServiceEntity> entities) {
        Map<String, DServiceEntity> index = new LinkedHashMap<>();
        for (DServiceEntity entity : entities) {
            index.put(canonicalize(entity.getCode()), entity);
            index.put(canonicalize(entity.getName()), entity);
        }
        return index;
    }

    private Map<String, DClassEntity> indexDriverClasses(List<DClassEntity> entities) {
        Map<String, DClassEntity> index = new LinkedHashMap<>();
        for (DClassEntity entity : entities) {
            index.put(canonicalize(entity.getCode()), entity);
            index.put(canonicalize(entity.getName()), entity);
        }
        return index;
    }

    private Map<String, DPropertyEntity> indexProperties(List<DPropertyEntity> entities) {
        Map<String, DPropertyEntity> index = new LinkedHashMap<>();
        for (DPropertyEntity entity : entities) {
            index.put(canonicalize(entity.getCode()), entity);
            index.put(canonicalize(entity.getName()), entity);
        }
        return index;
    }

    private Map<String, DCriteriaEntity> indexCriteria(List<DCriteriaEntity> entities) {
        Map<String, DCriteriaEntity> index = new LinkedHashMap<>();
        for (DCriteriaEntity entity : entities) {
            index.put(canonicalize(entity.getCode()), entity);
            index.put(canonicalize(entity.getName()), entity);
        }
        return index;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> mapValue(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private List<String> stringList(Object value) {
        if (!(value instanceof List<?> items)) {
            return List.of();
        }

        return items.stream()
                .map(this::stringValue)
                .filter(Objects::nonNull)
                .toList();
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof LocalDate localDate) {
            return localDate.atStartOfDay();
        }
        return null;
    }

    private BigDecimal bigDecimalValue(Object value) {
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Integer integer) {
            return BigDecimal.valueOf(integer.longValue());
        }
        if (value instanceof Long longValue) {
            return BigDecimal.valueOf(longValue);
        }
        if (value instanceof Double doubleValue) {
            return BigDecimal.valueOf(doubleValue);
        }
        return null;
    }

    private Integer integerValue(Object value) {
        if (value instanceof Integer integer) {
            return integer;
        }
        if (value instanceof Long longValue) {
            return longValue.intValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            return Integer.valueOf(text);
        }
        return null;
    }

    private String stringValue(Object value) {
        if (value == null) {
            return null;
        }
        String text = value instanceof String string ? string : String.valueOf(value);
        String trimmed = text.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String canonicalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replace("\u0110", "D")
                .replace("\u0111", "d")
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_");
        return normalized.replaceAll("^_+", "").replaceAll("_+$", "");
    }

    private String rootCauseMessage(Exception exception) {
        Throwable current = exception;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        if (message == null || message.isBlank()) {
            return "database import failed.";
        }
        return "database import failed: " + message;
    }

    private record RowContext(
            NormalizedDriverImportRow row,
            DDriverEntity driver,
            Map<String, Object> values
    ) {
    }
}
