package com.example.toolinsert.service;

import com.example.toolinsert.entity.BankAccountEntity;
import com.example.toolinsert.entity.CriteriaEntity;
import com.example.toolinsert.entity.DriverBankAccountEntity;
import com.example.toolinsert.entity.DriverClassAssignmentEntity;
import com.example.toolinsert.entity.DriverClassEntity;
import com.example.toolinsert.entity.DriverDocumentApprovalEntity;
import com.example.toolinsert.entity.DriverEntity;
import com.example.toolinsert.entity.DriverMetricEntity;
import com.example.toolinsert.entity.DriverPartyEntity;
import com.example.toolinsert.entity.DriverPropertyEntity;
import com.example.toolinsert.entity.DriverServiceEntity;
import com.example.toolinsert.entity.PartyEntity;
import com.example.toolinsert.entity.PropertyEntity;
import com.example.toolinsert.entity.RegionEntity;
import com.example.toolinsert.entity.ServiceEntity;
import com.example.toolinsert.entity.StagingDriverImportEntity;
import com.example.toolinsert.entity.id.DriverBankAccountId;
import com.example.toolinsert.entity.id.DriverClassAssignmentId;
import com.example.toolinsert.entity.id.DriverDocumentApprovalId;
import com.example.toolinsert.entity.id.DriverMetricId;
import com.example.toolinsert.entity.id.DriverPartyId;
import com.example.toolinsert.entity.id.DriverPropertyId;
import com.example.toolinsert.entity.id.DriverServiceId;
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
import org.springframework.stereotype.Service;

@Service
public class DriverDatabaseImportService {

    private static final String METRIC_DRIVING_EXPERIENCE = "kinh_nghiem_lai_xe";
    private static final Map<String, String> DOCUMENT_TO_PROPERTY = Map.of(
            "cccd_mat_truoc", "cccd_mat_truoc",
            "cccd_mat_sau", "cccd_mat_sau",
            "gplx_mat_truoc", "gplx_mat_truoc",
            "gplx_mat_sau", "gplx_mat_sau",
            "giay_xet_nghiem_ma_tuy", "giay_xet_nghiem_ma_tuy",
            "ly_lich_tu_phap", "ly_lich_tu_phap",
            "giay_xet_nghiem_hiv", "giay_xet_nghiem_hiv"
    );

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

    public List<DriverImportError> importValidatedRows(
            List<NormalizedDriverImportRow> normalizedRows,
            List<StagingDriverImportEntity> stagingRows
    ) {
        List<DriverImportError> errors = new ArrayList<>();
        Map<String, RegionEntity> regions = indexRegions(regionRepository.findAll());
        Map<String, ServiceEntity> services = indexServices(serviceRepository.findAll());
        Map<String, DriverClassEntity> classes = indexDriverClasses(driverClassRepository.findAll());
        Map<String, PropertyEntity> properties = indexProperties(propertyRepository.findAll());
        Map<String, CriteriaEntity> criteria = indexCriteria(criteriaRepository.findAll());
        Map<String, PartyEntity> partyCache = new LinkedHashMap<>();
        Map<String, BankAccountEntity> bankAccountCache = new LinkedHashMap<>();
        Map<String, DriverEntity> importedDriversBySourceId = new LinkedHashMap<>();
        Map<DriverPartyId, DriverPartyEntity> driverPartyLinks = new LinkedHashMap<>();
        Map<DriverBankAccountId, DriverBankAccountEntity> driverBankAccountLinks = new LinkedHashMap<>();
        Map<DriverServiceId, DriverServiceEntity> driverServiceLinks = new LinkedHashMap<>();
        Map<DriverClassAssignmentId, DriverClassAssignmentEntity> driverClassLinks = new LinkedHashMap<>();
        Map<DriverPropertyId, DriverPropertyEntity> driverProperties = new LinkedHashMap<>();
        Map<DriverDocumentApprovalId, DriverDocumentApprovalEntity> documentApprovals = new LinkedHashMap<>();
        Map<DriverMetricId, DriverMetricEntity> driverMetrics = new LinkedHashMap<>();
        List<RowContext> contexts = new ArrayList<>();

        for (int index = 0; index < normalizedRows.size(); index++) {
            NormalizedDriverImportRow row = normalizedRows.get(index);
            StagingDriverImportEntity stagingRow = stagingRows.get(index);
            if (!row.errors().isEmpty()) {
                continue;
            }

            try {
                RowContext context = importRow(
                        row,
                        stagingRow,
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
                stagingRow.setImportStatus("IMPORTED");
                stagingRow.setErrorMessage(null);
            } catch (Exception exception) {
                String message = rootCauseMessage(exception);
                stagingRow.setImportStatus("FAILED");
                stagingRow.setErrorMessage(message);
                errors.add(new DriverImportError(row.rowNumber(), message));
            }
        }

        updateReferrers(contexts, importedDriversBySourceId);
        saveLinkTables(driverPartyLinks, driverBankAccountLinks, driverServiceLinks, driverClassLinks, driverProperties, documentApprovals, driverMetrics);
        return errors;
    }

    private RowContext importRow(
            NormalizedDriverImportRow row,
            StagingDriverImportEntity stagingRow,
            Map<String, RegionEntity> regions,
            Map<String, ServiceEntity> services,
            Map<String, DriverClassEntity> classes,
            Map<String, PropertyEntity> properties,
            Map<String, CriteriaEntity> criteria,
            Map<String, PartyEntity> partyCache,
            Map<String, BankAccountEntity> bankAccountCache,
            Map<DriverPartyId, DriverPartyEntity> driverPartyLinks,
            Map<DriverBankAccountId, DriverBankAccountEntity> driverBankAccountLinks,
            Map<DriverServiceId, DriverServiceEntity> driverServiceLinks,
            Map<DriverClassAssignmentId, DriverClassAssignmentEntity> driverClassLinks,
            Map<DriverPropertyId, DriverPropertyEntity> driverProperties,
            Map<DriverDocumentApprovalId, DriverDocumentApprovalEntity> documentApprovals,
            Map<DriverMetricId, DriverMetricEntity> driverMetrics
    ) {
        Map<String, Object> values = row.normalizedValues();
        String phone = stringValue(values.get("phone"));
        PartyEntity party = resolveParty(phone, partyCache);
        RegionEntity region = resolveRegion(stringValue(values.get("region_name")), regions);
        DriverEntity driver = resolveDriver(values, party, region);
        List<ServiceEntity> resolvedServices = resolveServices(values.get("service_codes"), services);
        DriverClassEntity driverClass = resolveDriverClass(stringValue(values.get("driver_class")), classes);
        BankAccountEntity bankAccount = resolveBankAccount(values, bankAccountCache);

        if (party != null) {
            DriverPartyEntity link = new DriverPartyEntity();
            link.setId(new DriverPartyId(driver.getId(), party.getId()));
            link.setDriver(driver);
            link.setParty(party);
            driverPartyLinks.put(link.getId(), link);
        }

        if (bankAccount != null) {
            DriverBankAccountEntity link = new DriverBankAccountEntity();
            link.setId(new DriverBankAccountId(driver.getId(), bankAccount.getId()));
            link.setDriver(driver);
            link.setBankAccount(bankAccount);
            driverBankAccountLinks.put(link.getId(), link);
        }

        for (ServiceEntity service : resolvedServices) {
            DriverServiceEntity driverService = new DriverServiceEntity();
            driverService.setId(new DriverServiceId(driver.getId(), service.getId()));
            driverService.setDriver(driver);
            driverService.setService(service);
            driverServiceLinks.put(driverService.getId(), driverService);

            if (driverClass != null) {
                DriverClassAssignmentEntity assignment = new DriverClassAssignmentEntity();
                assignment.setId(new DriverClassAssignmentId(driver.getId(), driverClass.getId(), service.getId()));
                assignment.setDriver(driver);
                assignment.setDriverClass(driverClass);
                assignment.setService(service);
                assignment.setBonusRate(bigDecimalValue(values.get("bonus_rate")));
                driverClassLinks.put(assignment.getId(), assignment);
            }
        }

        addPropertyRows(driver, values, properties, driverProperties);
        addDocumentApprovalRows(driver, row.sourceId(), values, documentApprovals);
        addMetricRows(driver, values, criteria, driverMetrics);

        return new RowContext(row, stagingRow, driver, values);
    }

    private void updateReferrers(List<RowContext> contexts, Map<String, DriverEntity> importedDriversBySourceId) {
        List<DriverEntity> driversToUpdate = new ArrayList<>();
        for (RowContext context : contexts) {
            String referrerSourceId = stringValue(context.values().get("referrer_source_id"));
            if (referrerSourceId == null) {
                continue;
            }

            DriverEntity referrer = importedDriversBySourceId.get(referrerSourceId);
            if (referrer == null || Objects.equals(referrer.getId(), context.driver().getId())) {
                continue;
            }

            context.driver().setReferrer(referrer);
            driversToUpdate.add(context.driver());
        }

        driverBatchInsertService.saveInBatches(driversToUpdate, driverRepository);
    }

    private void saveLinkTables(
            Map<DriverPartyId, DriverPartyEntity> driverPartyLinks,
            Map<DriverBankAccountId, DriverBankAccountEntity> driverBankAccountLinks,
            Map<DriverServiceId, DriverServiceEntity> driverServiceLinks,
            Map<DriverClassAssignmentId, DriverClassAssignmentEntity> driverClassLinks,
            Map<DriverPropertyId, DriverPropertyEntity> driverProperties,
            Map<DriverDocumentApprovalId, DriverDocumentApprovalEntity> documentApprovals,
            Map<DriverMetricId, DriverMetricEntity> driverMetrics
    ) {
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverPartyLinks.values()), driverPartyRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverBankAccountLinks.values()), driverBankAccountRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverServiceLinks.values()), driverServiceRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverClassLinks.values()), driverClassAssignmentRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverProperties.values()), driverPropertyRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(documentApprovals.values()), driverDocumentApprovalRepository);
        driverBatchInsertService.saveInBatches(new ArrayList<>(driverMetrics.values()), driverMetricRepository);
    }

    private DriverEntity resolveDriver(Map<String, Object> values, PartyEntity party, RegionEntity region) {
        String identityNumber = stringValue(values.get("identity_number"));
        String fullName = stringValue(values.get("full_name"));
        LocalDate dob = localDateValue(values.get("dob"));
        DriverEntity driver = null;

        if (identityNumber != null) {
            driver = driverRepository.findByIdentityNumber(identityNumber).orElse(null);
        }
        if (driver == null && party != null) {
            driver = driverPartyRepository.findDriverByPhone(party.getPhone()).orElse(null);
        }
        if (driver == null && fullName != null && dob != null) {
            driver = driverRepository.findByFullNameAndDob(fullName, dob).orElse(null);
        }
        if (driver == null) {
            driver = new DriverEntity();
        }

        driver.setFullName(fullName);
        driver.setDob(dob);
        driver.setGender(stringValue(values.get("gender")));
        driver.setIdentityNumber(identityNumber);
        driver.setStatus(stringValue(values.get("driver_status")));
        driver.setRegion(region);
        return driverRepository.save(driver);
    }

    private PartyEntity resolveParty(String phone, Map<String, PartyEntity> partyCache) {
        if (phone == null) {
            return null;
        }
        return partyCache.computeIfAbsent(phone, key -> {
            PartyEntity party = partyRepository.findByPhone(key).orElseGet(PartyEntity::new);
            party.setPhone(key);
            return partyRepository.save(party);
        });
    }

    private BankAccountEntity resolveBankAccount(Map<String, Object> values, Map<String, BankAccountEntity> bankAccountCache) {
        String accountNumber = stringValue(values.get("bank_account_number"));
        if (accountNumber == null) {
            return null;
        }

        String bankCode = stringValue(values.get("bank_code"));
        String bankName = stringValue(values.get("bank_name"));
        Long bankId = resolveBankId(bankCode, bankName);
        String cacheKey = bankId + "|" + accountNumber;
        return bankAccountCache.computeIfAbsent(cacheKey, key -> {
            BankAccountEntity bankAccount = bankAccountRepository.findByBankIdAndAccountNumber(bankId, accountNumber)
                    .orElseGet(BankAccountEntity::new);
            bankAccount.setBankId(bankId);
            bankAccount.setAccountNumber(accountNumber);
            bankAccount.setAccountName(stringValue(values.get("bank_account_name")));
            return bankAccountRepository.save(bankAccount);
        });
    }

    private RegionEntity resolveRegion(String regionName, Map<String, RegionEntity> regions) {
        if (regionName == null) {
            return null;
        }
        String key = canonicalize(regionName);
        RegionEntity existing = regions.get(key);
        if (existing != null) {
            return existing;
        }

        RegionEntity region = new RegionEntity();
        region.setCode(regionName.trim());
        region.setName(regionName.trim());
        RegionEntity saved = regionRepository.save(region);
        regions.put(canonicalize(saved.getCode()), saved);
        regions.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private List<ServiceEntity> resolveServices(Object rawServices, Map<String, ServiceEntity> services) {
        List<String> serviceCodes = stringList(rawServices);
        if (serviceCodes.isEmpty()) {
            return List.of();
        }

        List<ServiceEntity> resolved = new ArrayList<>();
        Set<Long> seenIds = new LinkedHashSet<>();
        for (String serviceCode : serviceCodes) {
            String key = canonicalize(serviceCode);
            ServiceEntity service = services.get(key);
            if (service == null) {
                service = new ServiceEntity();
                service.setCode(serviceCode);
                service.setName(serviceCode);
                service = serviceRepository.save(service);
                services.put(canonicalize(service.getCode()), service);
                services.put(canonicalize(service.getName()), service);
            }
            if (seenIds.add(service.getId())) {
                resolved.add(service);
            }
        }
        return resolved;
    }

    private DriverClassEntity resolveDriverClass(String className, Map<String, DriverClassEntity> classes) {
        if (className == null) {
            return null;
        }
        String key = canonicalize(className);
        DriverClassEntity driverClass = classes.get(key);
        if (driverClass == null) {
            driverClass = new DriverClassEntity();
            driverClass.setCode(className);
            driverClass.setName(className);
        }
        DriverClassEntity saved = driverClassRepository.save(driverClass);
        classes.put(canonicalize(saved.getCode()), saved);
        classes.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private void addPropertyRows(
            DriverEntity driver,
            Map<String, Object> values,
            Map<String, PropertyEntity> properties,
            Map<DriverPropertyId, DriverPropertyEntity> driverProperties
    ) {
        Map<String, Object> propertyValues = new LinkedHashMap<>(mapValue(values.get("properties")));
        propertyValues.put("ngay_dang_ky", values.get("registered_at"));
        propertyValues.put("ngay_kich_hoat", values.get("activated_at"));

        for (Map.Entry<String, Object> entry : propertyValues.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            PropertyEntity property = resolveProperty(entry.getKey(), properties);
            DriverPropertyEntity driverProperty = new DriverPropertyEntity();
            driverProperty.setId(new DriverPropertyId(driver.getId(), property.getId()));
            driverProperty.setDriver(driver);
            driverProperty.setProperty(property);
            applyPropertyValue(driverProperty, value);
            driverProperties.put(driverProperty.getId(), driverProperty);
        }
    }

    private void addDocumentApprovalRows(
            DriverEntity driver,
            String sourceId,
            Map<String, Object> values,
            Map<DriverDocumentApprovalId, DriverDocumentApprovalEntity> documentApprovals
    ) {
        Map<String, Object> approvals = mapValue(values.get("document_approvals"));
        Map<String, Object> properties = mapValue(values.get("properties"));
        String phone = stringValue(values.get("phone"));

        for (Map.Entry<String, Object> entry : approvals.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            String documentType = entry.getKey();
            String documentId = resolveDocumentId(documentType, sourceId, phone, properties);
            DriverDocumentApprovalEntity approval = new DriverDocumentApprovalEntity();
            approval.setId(new DriverDocumentApprovalId(driver.getId(), documentType, documentId));
            approval.setDriver(driver);
            approval.setStatus(String.valueOf(entry.getValue()));
            documentApprovals.put(approval.getId(), approval);
        }
    }

    private void addMetricRows(
            DriverEntity driver,
            Map<String, Object> values,
            Map<String, CriteriaEntity> criteria,
            Map<DriverMetricId, DriverMetricEntity> driverMetrics
    ) {
        BigDecimal drivingExperience = bigDecimalValue(values.get("driving_experience_years"));
        if (drivingExperience == null) {
            return;
        }

        CriteriaEntity criterion = resolveCriteria(METRIC_DRIVING_EXPERIENCE, criteria);
        DriverMetricEntity metric = new DriverMetricEntity();
        metric.setId(new DriverMetricId(driver.getId(), criterion.getId()));
        metric.setDriver(driver);
        metric.setCriteria(criterion);
        metric.setValueNumber(drivingExperience);
        driverMetrics.put(metric.getId(), metric);
    }

    private PropertyEntity resolveProperty(String code, Map<String, PropertyEntity> properties) {
        String key = canonicalize(code);
        PropertyEntity property = properties.get(key);
        if (property != null) {
            return property;
        }

        PropertyEntity created = new PropertyEntity();
        created.setCode(code);
        created.setName(code);
        created.setMandatory(Boolean.FALSE);
        PropertyEntity saved = propertyRepository.save(created);
        properties.put(canonicalize(saved.getCode()), saved);
        properties.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private CriteriaEntity resolveCriteria(String code, Map<String, CriteriaEntity> criteria) {
        String key = canonicalize(code);
        CriteriaEntity criterion = criteria.get(key);
        if (criterion != null) {
            return criterion;
        }

        CriteriaEntity created = new CriteriaEntity();
        created.setCode(code);
        created.setName(code);
        CriteriaEntity saved = criteriaRepository.save(created);
        criteria.put(canonicalize(saved.getCode()), saved);
        criteria.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private void applyPropertyValue(DriverPropertyEntity driverProperty, Object value) {
        LocalDate dateValue = localDateValue(value);
        if (dateValue != null) {
            driverProperty.setValueDate(dateValue);
            return;
        }

        BigDecimal numberValue = bigDecimalValue(value);
        if (numberValue != null && !(value instanceof String)) {
            driverProperty.setValueNumber(numberValue);
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

    private String resolveDocumentId(String documentType, String sourceId, String phone, Map<String, Object> properties) {
        String propertyCode = DOCUMENT_TO_PROPERTY.get(documentType);
        if (propertyCode != null) {
            String path = stringValue(properties.get(propertyCode));
            if (path != null) {
                return path;
            }
        }
        if ("xac_thuc_sdt".equals(documentType) && phone != null) {
            return phone;
        }
        if (sourceId != null && !sourceId.isBlank()) {
            return sourceId.trim() + ":" + documentType;
        }
        return documentType;
    }

    private Long resolveBankId(String bankCode, String bankName) {
        String keySource = bankCode != null ? bankCode : bankName;
        String stableKey = canonicalize(keySource == null ? "unknown_bank" : keySource);
        CRC32 crc32 = new CRC32();
        crc32.update(stableKey.getBytes(StandardCharsets.UTF_8));
        long bankId = crc32.getValue();
        return bankId == 0 ? 1L : bankId;
    }

    private Map<String, RegionEntity> indexRegions(List<RegionEntity> entities) {
        Map<String, RegionEntity> index = new LinkedHashMap<>();
        for (RegionEntity entity : entities) {
            index.put(canonicalize(entity.getCode()), entity);
            index.put(canonicalize(entity.getName()), entity);
        }
        return index;
    }

    private Map<String, ServiceEntity> indexServices(List<ServiceEntity> entities) {
        Map<String, ServiceEntity> index = new LinkedHashMap<>();
        for (ServiceEntity entity : entities) {
            index.put(canonicalize(entity.getCode()), entity);
            index.put(canonicalize(entity.getName()), entity);
        }
        return index;
    }

    private Map<String, DriverClassEntity> indexDriverClasses(List<DriverClassEntity> entities) {
        Map<String, DriverClassEntity> index = new LinkedHashMap<>();
        for (DriverClassEntity entity : entities) {
            index.put(canonicalize(entity.getCode()), entity);
            index.put(canonicalize(entity.getName()), entity);
        }
        return index;
    }

    private Map<String, PropertyEntity> indexProperties(List<PropertyEntity> entities) {
        Map<String, PropertyEntity> index = new LinkedHashMap<>();
        for (PropertyEntity entity : entities) {
            index.put(canonicalize(entity.getCode()), entity);
            index.put(canonicalize(entity.getName()), entity);
        }
        return index;
    }

    private Map<String, CriteriaEntity> indexCriteria(List<CriteriaEntity> entities) {
        Map<String, CriteriaEntity> index = new LinkedHashMap<>();
        for (CriteriaEntity entity : entities) {
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

    private LocalDate localDateValue(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.toLocalDate();
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
            StagingDriverImportEntity stagingRow,
            DriverEntity driver,
            Map<String, Object> values
    ) {
    }
}
