package com.example.toolinsert.service;

import com.example.toolinsert.entity.DBankAccEntity;
import com.example.toolinsert.entity.DBankEntity;
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
import com.example.toolinsert.repository.BankRepository;
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
import java.util.function.Supplier;
import com.example.toolinsert.constant.DriverConstants;
import com.example.toolinsert.entity.id.DDriverBankAccId;
import com.example.toolinsert.entity.id.DDriverClassId;
import com.example.toolinsert.entity.id.DDriverPartyId;
import com.example.toolinsert.entity.id.DDriverPropertyId;
import com.example.toolinsert.entity.id.DDriverServiceId;
import org.springframework.stereotype.Service;

@Service
public class DriverDatabaseImportService {

    private static final String IMPORT_ACTOR = "tool";
    private static final String METRIC_DRIVING_EXPERIENCE = DriverConstants.PROPERTY_WORK_EXPERIENCE;
    private static final String REGION_TYPE_PROVINCE = "PROVINCE";
    private static final String REGION_TYPE_WARD = "WARD";
    private static final String DEFAULT_CURRENCY = "VND";
    private static final String DEFAULT_METRIC_UNIT = "YEAR";
    private static final int DEFAULT_ORDINAL = 0;
    private static final int DEFAULT_BANK_COUNTRY_ID = 1;
    private static final int MAX_BANK_ID = 99_999;
    private static final BigDecimal DEFAULT_BONUS_RATE = BigDecimal.ZERO;
    private static final Map<String, String> BANK_CODE_ALIASES = Map.of(
            "tcb", "techcombank",
            "vpb", "vpbank"
    );

    private final DriverRepository driverRepository;
    private final PartyRepository partyRepository;
    private final BankRepository bankRepository;
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
            BankRepository bankRepository,
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
        this.bankRepository = bankRepository;
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
        Map<String, DBankEntity> banks = indexBanks(bankRepository.findAll());
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
                        banks,
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
                saveRowArtifacts(context);
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
        return errors;
    }

    private RowContext importRow(
            NormalizedDriverImportRow row,
            Map<String, DRegionEntity> regions,
            Map<String, DServiceEntity> services,
            Map<String, DClassEntity> classes,
            Map<String, DPropertyEntity> properties,
            Map<String, DCriteriaEntity> criteria,
            Map<String, DBankEntity> banks,
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
        List<DDriverPartyEntity> rowDriverPartyLinks = new ArrayList<>();
        List<DDriverBankAccEntity> rowDriverBankAccountLinks = new ArrayList<>();
        List<DDriverServiceEntity> rowDriverServiceLinks = new ArrayList<>();
        List<DDriverClassEntity> rowDriverClassLinks = new ArrayList<>();
        List<DDriverPropertyEntity> rowDriverProperties = new ArrayList<>();
        List<DDriverDocumentApprovalEntity> rowDocumentApprovals = new ArrayList<>();
        List<DDriverMetricEntity> rowDriverMetrics = new ArrayList<>();
        DPartyEntity party = resolveParty(stringValue(values.get("phone")), partyCache);
        DRegionEntity region = resolveRegion(stringValue(values.get("region_name")), regions);
        DDriverEntity driver = resolveDriver(values, region);
        List<DServiceEntity> resolvedServices = resolveServices(values.get("service_codes"), services);
        DClassEntity driverClass = resolveDriverClass(
                stringValue(values.get("driver_class")),
                bigDecimalValue(values.get("bonus_rate")),
                classes
        );
        DBankAccEntity bankAccount = resolveBankAccount(values, banks, bankAccountCache);

        if (party != null) {
            DDriverPartyEntity link = new DDriverPartyEntity();
            link.setDriverId(driver.getDriverId());
            link.setPartyId(party.getPartyId());
            link.setStatus(1);
            DDriverPartyId linkId = new DDriverPartyId(driver.getDriverId(), party.getPartyId());
            if (driverPartyLinks.putIfAbsent(linkId, link) == null) {
                rowDriverPartyLinks.add(link);
            }
        }

        if (bankAccount != null) {
            DDriverBankAccEntity link = new DDriverBankAccEntity();
            link.setDriverId(driver.getDriverId());
            link.setBankAccId(bankAccount.getBankAccId());
            link.setStatus(0);
            DDriverBankAccId linkId = new DDriverBankAccId(driver.getDriverId(), bankAccount.getBankAccId());
            if (driverBankAccountLinks.putIfAbsent(linkId, link) == null) {
                rowDriverBankAccountLinks.add(link);
            }
        }

        for (DServiceEntity service : resolvedServices) {
            DDriverServiceEntity driverService = new DDriverServiceEntity();
            driverService.setDriverId(driver.getDriverId());
            driverService.setServiceId(service.getServiceId());
            driverService.setStatus(1);
            DDriverServiceId serviceId = new DDriverServiceId(driver.getDriverId(), service.getServiceId());
            if (driverServiceLinks.putIfAbsent(serviceId, driverService) == null) {
                rowDriverServiceLinks.add(driverService);
            }

            if (driverClass != null) {
                DDriverClassId classId = new DDriverClassId(driver.getDriverId(), driverClass.getClassId(), service.getServiceId());
                DDriverClassEntity assignment = resolveDriverClassAssignment(classId);
                assignment.setBonusRate(defaultBonusRate(bigDecimalValue(values.get("bonus_rate"))));
                assignment.setStatus(1);
                applyAuditFields(assignment);
                if (driverClassLinks.putIfAbsent(classId, assignment) == null) {
                    rowDriverClassLinks.add(assignment);
                }
            }
        }

        addPropertyRows(driver, values, properties, driverProperties, rowDriverProperties);
        addDocumentApprovalRows(driver, values, documentApprovals, rowDocumentApprovals);
        addMetricRows(driver, resolvedServices, values, criteria, driverMetrics, rowDriverMetrics);
        return new RowContext(
                row,
                driver,
                values,
                List.copyOf(rowDriverPartyLinks),
                List.copyOf(rowDriverBankAccountLinks),
                List.copyOf(rowDriverServiceLinks),
                List.copyOf(rowDriverClassLinks),
                List.copyOf(rowDriverProperties),
                List.copyOf(rowDocumentApprovals),
                List.copyOf(rowDriverMetrics)
        );
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

        withDatabaseContext(
                "D_DRIVER.REFERRER_ID",
                () -> driverBatchInsertService.saveInBatches(driversToUpdate, driverRepository)
        );
    }

    private void saveRowArtifacts(RowContext context) {
        withDatabaseContext(
                "D_DRIVER_PARTY.STATUS",
                () -> driverBatchInsertService.saveInBatches(context.driverPartyLinks(), driverPartyRepository)
        );
        withDatabaseContext(
                "D_DRIVER_BANK_ACC.STATUS",
                () -> driverBatchInsertService.saveInBatches(context.driverBankAccountLinks(), driverBankAccountRepository)
        );
        withDatabaseContext(
                "D_DRIVER_SERVICE.STATUS",
                () -> driverBatchInsertService.saveInBatches(context.driverServiceLinks(), driverServiceRepository)
        );
        withDatabaseContext(
                "D_DRIVER_CLASS.BONUS_RATE",
                () -> driverBatchInsertService.saveInBatches(context.driverClassLinks(), driverClassAssignmentRepository)
        );
        withDatabaseContext(
                "D_DRIVER_PROPERTY.VALUE_NUMBER",
                () -> driverBatchInsertService.saveInBatches(context.driverProperties(), driverPropertyRepository)
        );
        withDatabaseContext(
                "D_DRIVER_DOCUMENT_APPROVAL.STATUS",
                () -> driverBatchInsertService.saveInBatches(context.documentApprovals(), driverDocumentApprovalRepository)
        );
        withDatabaseContext(
                "D_DRIVER_METRIC.VALUE",
                () -> driverBatchInsertService.saveInBatches(context.driverMetrics(), driverMetricRepository)
        );
    }

    private DDriverEntity resolveDriver(Map<String, Object> values, DRegionEntity region) {
        String identityNumber = resolveIdentityNumber(values);
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
        DDriverEntity driverToSave = driver;
        return withDatabaseContext("D_DRIVER.STATUS", () -> driverRepository.save(driverToSave));
    }

    private String resolveIdentityNumber(Map<String, Object> values) {
        String identityNumber = stringValue(values.get("identity_number"));
        if (identityNumber != null) {
            return identityNumber;
        }

        String phone = stringValue(values.get("phone"));
        if (phone != null) {
            return phone;
        }

        return stringValue(values.get("source_id"));
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
            applyAuditFields(party);
            return withDatabaseContext("D_PARTY.STATUS", () -> partyRepository.save(party));
        });
    }

    private DBankAccEntity resolveBankAccount(
            Map<String, Object> values,
            Map<String, DBankEntity> banks,
            Map<String, DBankAccEntity> bankAccountCache
    ) {
        String accountNumber = stringValue(values.get("bank_account_number"));
        if (accountNumber == null) {
            return null;
        }

        DBankEntity bank = resolveOrCreateBank(stringValue(values.get("bank_code")), stringValue(values.get("bank_name")), banks);
        Integer bankId = bank.getBankId();
        String cacheKey = bankId + "|" + accountNumber;
        return bankAccountCache.computeIfAbsent(cacheKey, key -> {
            DBankAccEntity bankAccount = bankAccountRepository.findByBankIdAndAccNumber(bankId, accountNumber)
                    .orElseGet(DBankAccEntity::new);
            bankAccount.setBankId(bankId);
            bankAccount.setAccNumber(accountNumber);
            String accountName = stringValue(values.get("bank_account_name"));
            bankAccount.setAccName(accountName != null ? accountName : accountNumber);
            bankAccount.setCurrency(DEFAULT_CURRENCY);
            if (bankAccount.getIsActived() == null) {
                bankAccount.setIsActived(1);
            }
            applyAuditFields(bankAccount);
            return withDatabaseContext("D_BANK_ACC.BANK_ID", () -> bankAccountRepository.save(bankAccount));
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
        region.setType(inferRegionType(regionName));
        region.setOrd(DEFAULT_ORDINAL);
        region.setStatus(1);
        applyAuditFields(region);
        DRegionEntity saved = withDatabaseContext("D_REGION.ORD", () -> regionRepository.save(region));
        regions.put(canonicalize(saved.getCode()), saved);
        regions.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private String inferRegionType(String regionName) {
        String normalized = canonicalize(regionName);
        if (normalized.startsWith("phuong_")
                || normalized.startsWith("xa_")
                || normalized.startsWith("thi_tran_")
                || normalized.startsWith("ward_")) {
            return REGION_TYPE_WARD;
        }
        return REGION_TYPE_PROVINCE;
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
                service.setName(DriverImportMapping.serviceDisplayName(serviceCode));
                service.setStatus(1);
                applyAuditFields(service);
                DServiceEntity serviceToSave = service;
                service = withDatabaseContext("D_SERVICE.STATUS", () -> serviceRepository.save(serviceToSave));
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
            driverClass.setBonusRate(defaultBonusRate(bonusRate));
        }
        applyAuditFields(driverClass);
        DClassEntity classToSave = driverClass;
        DClassEntity saved = withDatabaseContext("D_CLASS.BONUS_RATE", () -> driverClassRepository.save(classToSave));
        classes.put(canonicalize(saved.getCode()), saved);
        classes.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private DDriverClassEntity resolveDriverClassAssignment(DDriverClassId classId) {
        return driverClassAssignmentRepository.findById(classId).orElseGet(() -> {
            DDriverClassEntity assignment = new DDriverClassEntity();
            assignment.setDriverId(classId.getDriverId());
            assignment.setClassId(classId.getClassId());
            assignment.setServiceId(classId.getServiceId());
            return assignment;
        });
    }

    private void addPropertyRows(
            DDriverEntity driver,
            Map<String, Object> values,
            Map<String, DPropertyEntity> properties,
            Map<DDriverPropertyId, DDriverPropertyEntity> driverProperties,
            List<DDriverPropertyEntity> rowDriverProperties
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
            DDriverPropertyId propertyId = new DDriverPropertyId(property.getPropertyId(), driver.getDriverId());
            DDriverPropertyEntity driverProperty = resolveDriverProperty(propertyId);
            driverProperty.setStatus(1);
            applyAuditFields(driverProperty);
            applyPropertyValue(driverProperty, value);
            if (driverProperties.putIfAbsent(propertyId, driverProperty) == null) {
                rowDriverProperties.add(driverProperty);
            }
        }
    }

    private void addDocumentApprovalRows(
            DDriverEntity driver,
            Map<String, Object> values,
            Map<String, DDriverDocumentApprovalEntity> documentApprovals,
            List<DDriverDocumentApprovalEntity> rowDocumentApprovals
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
            applyAuditFields(approval);
            String approvalKey = driver.getDriverId() + "|" + entry.getKey();
            if (documentApprovals.putIfAbsent(approvalKey, approval) == null) {
                rowDocumentApprovals.add(approval);
            }
        }
    }

    private void addMetricRows(
            DDriverEntity driver,
            List<DServiceEntity> resolvedServices,
            Map<String, Object> values,
            Map<String, DCriteriaEntity> criteria,
            Map<String, DDriverMetricEntity> driverMetrics,
            List<DDriverMetricEntity> rowDriverMetrics
    ) {
        BigDecimal drivingExperience = bigDecimalValue(values.get("driving_experience_years"));
        if (drivingExperience == null) {
            return;
        }

        DCriteriaEntity criterion = resolveCriteria(METRIC_DRIVING_EXPERIENCE, criteria);
        if (resolvedServices.isEmpty()) {
            addMetricRow(driver, criterion.getCriteriaId(), null, drivingExperience, driverMetrics, rowDriverMetrics);
            return;
        }

        for (DServiceEntity service : resolvedServices) {
            addMetricRow(
                    driver,
                    criterion.getCriteriaId(),
                    service.getServiceId(),
                    drivingExperience,
                    driverMetrics,
                    rowDriverMetrics
            );
        }
    }

    private void addMetricRow(
            DDriverEntity driver,
            Integer criteriaId,
            Integer serviceId,
            BigDecimal drivingExperience,
            Map<String, DDriverMetricEntity> driverMetrics,
            List<DDriverMetricEntity> rowDriverMetrics
    ) {
        String metricKey = driver.getDriverId() + "|" + criteriaId + "|" + serviceId;
        DDriverMetricEntity metric = resolveDriverMetric(driver.getDriverId(), criteriaId, serviceId);
        metric.setDriverId(driver.getDriverId());
        metric.setCriteriaId(criteriaId);
        metric.setServiceId(serviceId);
        metric.setValue(drivingExperience);
        metric.setUnit(DEFAULT_METRIC_UNIT);
        if (driverMetrics.putIfAbsent(metricKey, metric) == null) {
            rowDriverMetrics.add(metric);
        }
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
        created.setOrd(DEFAULT_ORDINAL);
        created.setIsActive(DriverConstants.STATUS_ACTIVE);
        applyAuditFields(created);
        DPropertyEntity saved = withDatabaseContext("D_PROPERTY.FIELD_TYPE_ID", () -> propertyRepository.save(created));
        properties.put(canonicalize(saved.getCode()), saved);
        properties.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private BigDecimal defaultBonusRate(BigDecimal bonusRate) {
        return bonusRate != null ? bonusRate : DEFAULT_BONUS_RATE;
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
        applyAuditFields(created);
        DCriteriaEntity saved = withDatabaseContext("D_CRITERIA.STATUS", () -> criteriaRepository.save(created));
        criteria.put(canonicalize(saved.getCode()), saved);
        criteria.put(canonicalize(saved.getName()), saved);
        return saved;
    }

    private DDriverPropertyEntity resolveDriverProperty(DDriverPropertyId propertyId) {
        return driverPropertyRepository.findById(propertyId).orElseGet(() -> {
            DDriverPropertyEntity driverProperty = new DDriverPropertyEntity();
            driverProperty.setPropertyId(propertyId.getPropertyId());
            driverProperty.setDriverId(propertyId.getDriverId());
            return driverProperty;
        });
    }

    private DDriverMetricEntity resolveDriverMetric(Long driverId, Integer criteriaId, Integer serviceId) {
        if (serviceId == null) {
            return driverMetricRepository.findByDriverIdAndCriteriaIdAndServiceIdIsNull(driverId, criteriaId)
                    .orElseGet(DDriverMetricEntity::new);
        }
        return driverMetricRepository.findByDriverIdAndCriteriaIdAndServiceId(driverId, criteriaId, serviceId)
                .orElseGet(DDriverMetricEntity::new);
    }

    private void applyAuditFields(DPartyEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
    }

    private void applyAuditFields(DBankAccEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
        if (entity.getCreated() == null) {
            entity.setCreated(LocalDateTime.now());
        }
    }

    private void applyAuditFields(DBankEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
        if (entity.getCreated() == null) {
            entity.setCreated(LocalDateTime.now());
        }
    }

    private void applyAuditFields(DRegionEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
    }

    private void applyAuditFields(DServiceEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
    }

    private void applyAuditFields(DClassEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
    }

    private void applyAuditFields(DPropertyEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
    }

    private void applyAuditFields(DCriteriaEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
    }

    private void applyAuditFields(DDriverClassEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
    }

    private void applyAuditFields(DDriverPropertyEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
    }

    private void applyAuditFields(DDriverDocumentApprovalEntity entity) {
        if (entity.getCreator() == null) {
            entity.setCreator(IMPORT_ACTOR);
        }
        if (entity.getModifier() == null) {
            entity.setModifier(IMPORT_ACTOR);
        }
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

    private DBankEntity resolveOrCreateBank(String bankCode, String bankName, Map<String, DBankEntity> banks) {
        for (String lookupKey : bankLookupKeys(bankCode, bankName)) {
            DBankEntity bank = banks.get(lookupKey);
            if (bank != null) {
                return bank;
            }
        }

        if (bankCode == null && bankName == null) {
            throw new IllegalArgumentException("bank code or bank name is required when bank account number is provided.");
        }

        DBankEntity created = new DBankEntity();
        created.setBankId(nextBankId(banks));
        created.setCountryId(DEFAULT_BANK_COUNTRY_ID);
        created.setCode(resolveBankMasterCode(bankCode, bankName));
        created.setName(resolveBankMasterName(bankCode, bankName));
        created.setShortName(resolveBankShortName(bankCode, bankName));
        applyAuditFields(created);
        DBankEntity saved = withDatabaseContext("D_BANK.CODE", () -> bankRepository.save(created));
        indexBank(banks, saved);
        return saved;
    }

    private List<String> bankLookupKeys(String bankCode, String bankName) {
        List<String> lookupKeys = new ArrayList<>();
        addBankLookupKey(lookupKeys, bankCode);
        addBankLookupKey(lookupKeys, bankName);
        return lookupKeys;
    }

    private void addBankLookupKey(List<String> lookupKeys, String rawValue) {
        if (rawValue == null) {
            return;
        }

        String canonical = canonicalize(rawValue);
        if (!lookupKeys.contains(canonical)) {
            lookupKeys.add(canonical);
        }

        String alias = BANK_CODE_ALIASES.get(canonical);
        if (alias != null && !lookupKeys.contains(alias)) {
            lookupKeys.add(alias);
        }
    }

    private Map<String, DBankEntity> indexBanks(List<DBankEntity> entities) {
        Map<String, DBankEntity> index = new LinkedHashMap<>();
        for (DBankEntity entity : entities) {
            indexBank(index, entity);
        }
        return index;
    }

    private void indexBank(Map<String, DBankEntity> banks, DBankEntity bank) {
        banks.putIfAbsent(canonicalize(bank.getCode()), bank);
        banks.putIfAbsent(canonicalize(bank.getName()), bank);
        if (bank.getShortName() != null) {
            banks.putIfAbsent(canonicalize(bank.getShortName()), bank);
        }
    }

    private Integer nextBankId(Map<String, DBankEntity> banks) {
        int nextId = banks.values().stream()
                .map(DBankEntity::getBankId)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) + 1;
        if (nextId > MAX_BANK_ID) {
            throw new IllegalStateException("bank master id exceeded precision limit.");
        }
        return nextId;
    }

    private String resolveBankMasterCode(String bankCode, String bankName) {
        if (bankCode != null) {
            String canonicalCode = canonicalize(bankCode);
            return BANK_CODE_ALIASES.getOrDefault(canonicalCode, canonicalCode).toUpperCase(Locale.ROOT);
        }

        String extractedShortName = extractBankShortName(bankName);
        if (extractedShortName != null) {
            String canonicalCode = canonicalize(extractedShortName);
            return BANK_CODE_ALIASES.getOrDefault(canonicalCode, canonicalCode).toUpperCase(Locale.ROOT);
        }

        return canonicalize(bankName).toUpperCase(Locale.ROOT);
    }

    private String resolveBankMasterName(String bankCode, String bankName) {
        String cleanedName = cleanBankName(bankName);
        if (cleanedName != null) {
            return cleanedName;
        }
        return bankCode.trim().toUpperCase(Locale.ROOT);
    }

    private String resolveBankShortName(String bankCode, String bankName) {
        if (bankCode != null) {
            return bankCode.trim().toUpperCase(Locale.ROOT);
        }
        String extractedShortName = extractBankShortName(bankName);
        return extractedShortName == null ? null : extractedShortName.toUpperCase(Locale.ROOT);
    }

    private String cleanBankName(String bankName) {
        if (bankName == null) {
            return null;
        }
        String cleanedName = bankName.replaceAll("\\s*\\([^)]*\\)\\s*$", "").trim();
        return cleanedName.isBlank() ? null : cleanedName;
    }

    private String extractBankShortName(String bankName) {
        if (bankName == null) {
            return null;
        }
        int openIndex = bankName.lastIndexOf('(');
        int closeIndex = bankName.lastIndexOf(')');
        if (openIndex >= 0 && closeIndex > openIndex) {
            String shortName = bankName.substring(openIndex + 1, closeIndex).trim();
            return shortName.isBlank() ? null : shortName;
        }
        return null;
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
        String databaseTarget = databaseTarget(exception);
        Throwable current = exception;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        String prefix = databaseTarget == null
                ? "database import failed"
                : "database import failed at " + databaseTarget;
        if (message == null || message.isBlank()) {
            return prefix + ".";
        }
        return prefix + ": " + message;
    }

    private String databaseTarget(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof DatabaseImportContextException contextException) {
                return contextException.databaseTarget();
            }
            current = current.getCause();
        }
        return null;
    }

    private void withDatabaseContext(String databaseTarget, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            throw new DatabaseImportContextException(databaseTarget, exception);
        }
    }

    private <T> T withDatabaseContext(String databaseTarget, Supplier<T> action) {
        try {
            return action.get();
        } catch (RuntimeException exception) {
            throw new DatabaseImportContextException(databaseTarget, exception);
        }
    }

    private static final class DatabaseImportContextException extends RuntimeException {

        private final String databaseTarget;

        private DatabaseImportContextException(String databaseTarget, RuntimeException cause) {
            super(cause);
            this.databaseTarget = databaseTarget;
        }

        private String databaseTarget() {
            return databaseTarget;
        }
    }

    private record RowContext(
            NormalizedDriverImportRow row,
            DDriverEntity driver,
            Map<String, Object> values,
            List<DDriverPartyEntity> driverPartyLinks,
            List<DDriverBankAccEntity> driverBankAccountLinks,
            List<DDriverServiceEntity> driverServiceLinks,
            List<DDriverClassEntity> driverClassLinks,
            List<DDriverPropertyEntity> driverProperties,
            List<DDriverDocumentApprovalEntity> documentApprovals,
            List<DDriverMetricEntity> driverMetrics
    ) {
    }
}
