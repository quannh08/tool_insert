# Driver CSV Import - Direct Database Insertion Documentation

## 1. Overview

Act as a technical writer for software documentation.

Generate clear documentation for a backend module that imports driver data from a CSV file directly into the target database.

Include:
- overview
- setup instructions
- import flow
- table insert order
- validation rules
- mapping rules
- direct database insert strategy
- error handling
- clean code guidance
- implementation comments

This module must follow these rules:

- Do not use entity-based import flow
- Do not use staging table or intermediate database table
- Read data directly from the import file
- Normalize and validate data in memory
- Insert directly into the target database
- Keep table insert order safe to avoid foreign key conflict
- Ensure every child record belongs to the correct driver
- Use batch insert for performance
- Keep code clean, modular, and maintainable
- Add clear comments in important logic blocks

---

## 2. Goal

The goal of this module is to import driver data from a CSV file directly into the system database in a safe and maintainable way.

The import process must guarantee:

- correct mapping for each driver
- correct insert order between related tables
- no dependency on ORM entity persistence
- no staging or temporary import table
- good performance through batch insert
- clear validation and logging
- idempotent behavior where applicable

This design is intended for projects where the database schema already exists and the application is not allowed to create or control table structure through code.

---

## 3. Import Strategy

### 3.1 Direct import principle

The system must not save imported data into any intermediate table.

Correct flow:

```text
CSV File
   ↓
Parse
   ↓
Validate
   ↓
Normalize
   ↓
Resolve master data
   ↓
Insert directly into target tables
   ↓
Return import summary
```

### 3.2 Key rule

Imported data must be held in memory only during processing.

The system should:

1. read CSV rows directly from uploaded file
2. convert each row into an import model
3. validate and normalize values
4. resolve required foreign keys
5. insert directly into target database in the correct order

---

## 4. Setup Instructions

### 4.1 Database requirements

The target database must already contain:

- `D_DRIVER`
- `D_PARTY`
- `D_DRIVER_PARTY`
- `D_BANK_ACC`
- `D_DRIVER_BANK_ACC`
- `D_DRIVER_SERVICE`
- `D_DRIVER_CLASS`
- `D_DRIVER_PROPERTY`
- `D_DRIVER_DOCUMENT_APPROVAL`
- `D_DRIVER_METRIC`
- `D_REGION`
- `D_SERVICE`
- `D_CLASS`
- `D_PROPERTY`
- `D_CRITERIA`
- bank master table

The application must not rely on automatic DDL generation.

### 4.2 Suggested Spring Boot configuration

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 20MB
      max-request-size: 20MB

logging:
  level:
    root: INFO
    com.example.driverimport: INFO

app:
  import:
    batch-size: 200
    skip-invalid-rows: true
    trim-string-values: true
```

### 4.3 Suggested libraries

- Apache Commons CSV or OpenCSV
- JdbcTemplate or native JDBC batch execution
- Spring transaction management
- utility classes for parsing, normalizing, and mapping

### 4.4 Suggested project structure

```text
controller/
  DriverImportController.java

service/
  DriverImportService.java
  DriverBatchInsertService.java
  DriverMasterDataService.java

model/
  DriverImportRow.java
  DriverImportContext.java
  DriverImportResult.java
  DriverImportError.java

validator/
  DriverImportValidator.java

mapper/
  DriverImportMapper.java

util/
  CsvReaderUtil.java
  DateParserUtil.java
  PhoneNormalizer.java
  FilePathCleaner.java
```

---

## 5. Database Overview

### 5.1 Core tables

#### `D_DRIVER`
Main table storing driver basic information such as full name, gender, date of birth, identity number, status, region, and referrer.

#### `D_PARTY`
Stores phone/contact information used in the system.

#### `D_BANK_ACC`
Stores bank account information such as account number, account holder name, and bank reference.

### 5.2 Relationship tables

#### `D_DRIVER_PARTY`
Stores the relationship between driver and phone/party record.

#### `D_DRIVER_BANK_ACC`
Stores the relationship between driver and bank account.

#### `D_DRIVER_SERVICE`
Stores the services registered by each driver.

#### `D_DRIVER_CLASS`
Stores class/service assignment for each driver, including bonus or classification data.

### 5.3 Dynamic tables

#### `D_DRIVER_PROPERTY`
Stores dynamic driver attributes in key-value form.

#### `D_DRIVER_DOCUMENT_APPROVAL`
Stores document approval status for each driver.

#### `D_DRIVER_METRIC`
Stores metrics such as driving experience and other measurable criteria.

### 5.4 Master data tables

#### `D_REGION`
Stores region or geographical area.

#### `D_SERVICE`
Stores supported services.

#### `D_CLASS`
Stores driver classes or grades.

#### `D_PROPERTY`
Defines available dynamic property keys.

#### `D_CRITERIA`
Defines available metric keys.

---

## 6. Data Normalization Rules

All imported values must be normalized before insert.

### Examples

- `gioi_tinh`
  - `Nam` -> `1`
  - `Nữ` -> `0`

- `trang_thai_tai_xe`
  - map raw text to internal status code

- `trang_thai_hop_dong`
  - map raw text to internal contract status code

- date fields
  - convert to `DATE` or `TIMESTAMP`

- file path fields
  - clean malformed path strings before saving

- `loai_xe_dang_ky`
  - split comma-separated values such as `Automatic,Manual`

- `SĐT`, `CCCD`, `STK`
  - keep as string to avoid losing leading zero

- `khu_vuc_hoat_dong`
  - resolve to `REGION_ID`

- `nguoi_gioi_thieu`
  - resolve to `REFERRER_ID` if valid

### String normalization rules

- trim all string values
- convert blank string to null when appropriate
- preserve original business meaning
- do not uppercase/lowercase values unless business rules allow it

---

## 7. Insert Order to Avoid Conflict

To avoid foreign key errors and wrong relationship mapping, data must be inserted in the correct order.

### Phase 1 - Load master data into memory

Load all required dictionaries before processing rows:

- `regionName -> regionId`
- `serviceCodeOrName -> serviceId`
- `classCodeOrName -> classId`
- `propertyCode -> propertyId`
- `criteriaCode -> criteriaId`
- `bankCodeOrName -> bankId`

This phase does not insert data.

### Phase 2 - Parse, validate, and normalize CSV rows

For each CSV row:

1. parse raw columns
2. build in-memory import object
3. validate required fields
4. normalize raw values
5. prepare insert payload

Invalid rows should be skipped or collected as errors depending on business rules.

### Phase 3 - Insert `D_DRIVER`

Insert driver core data first.

Main columns:

- `FULL_NAME`
- `DOB`
- `GENDER`
- `IDENTITY_NUMBER`
- `STATUS`
- `REGION_ID`

Important rule:

Every driver must be tracked by a stable business key, for example:

- `CCCD`
- or another guaranteed unique identifier

After insert, store mapping:

```text
driverBusinessKey -> driverId
```

This mapping must be used for every child table insert.

### Phase 4 - Update `REFERRER_ID`

If referrer points to another imported driver:

- first insert all drivers
- then resolve referrer mapping
- then update `REFERRER_ID`

This avoids dependency conflict.

### Phase 5 - Insert `D_PARTY` and `D_DRIVER_PARTY`

Flow:

1. normalize phone number
2. find existing `D_PARTY` by unique phone
3. insert `D_PARTY` if not found
4. create `D_DRIVER_PARTY`

### Phase 6 - Insert `D_BANK_ACC` and `D_DRIVER_BANK_ACC`

Flow:

1. resolve `BANK_ID`
2. find existing account by unique key
3. insert `D_BANK_ACC` if not found
4. create `D_DRIVER_BANK_ACC`

### Phase 7 - Insert `D_DRIVER_SERVICE`

Flow:

1. split `loai_xe_dang_ky`
2. trim each value
3. resolve service
4. insert one row per service

### Phase 8 - Insert `D_DRIVER_CLASS`

Map:

- `DRIVER_ID`
- `CLASS_ID`
- `SERVICE_ID`
- `BONUS_RATE`
- `STATUS`

This step must run only after driver and service are resolved.

### Phase 9 - Insert `D_DRIVER_PROPERTY`

Use this table for dynamic fields such as:

- issue date
- expiry date
- address
- license number
- document file path
- activation date
- registration date

Map to correct value column:

- `VALUE_TEXT`
- `VALUE_NUMBER`
- `VALUE_DATE`
- `VALUE_FILE`

### Phase 10 - Insert `D_DRIVER_DOCUMENT_APPROVAL`

Map document approval statuses such as:

- CCCD front
- CCCD back
- GPLX front
- GPLX back
- HIV test
- criminal record
- phone verification

Example mapping:

- `Đạt` -> `1`
- `Chưa đạt` -> `0` or `2`
- `Chờ duyệt` -> `3`

### Phase 11 - Insert `D_DRIVER_METRIC`

Example:

- `kinh_nghiem_lai_xe` -> `VALUE_NUMBER`

Each metric must link to:

- correct `DRIVER_ID`
- correct `CRITERIA_ID`

---

## 8. Mapping Rules by Table

### 8.1 `D_DRIVER`

- `ten` -> `FULL_NAME`
- `ngay_sinh` -> `DOB`
- `gioi_tinh` -> `GENDER`
- `CCCD` -> `IDENTITY_NUMBER`
- `trang_thai_tai_xe` -> `STATUS`
- `khu_vuc_hoat_dong` -> `REGION_ID`
- `nguoi_gioi_thieu` -> `REFERRER_ID`

### 8.2 `D_PARTY` and `D_DRIVER_PARTY`

- `SĐT` -> `D_PARTY.PHONE`
- create relationship in `D_DRIVER_PARTY`

### 8.3 `D_BANK_ACC` and `D_DRIVER_BANK_ACC`

- `STK` -> `ACC_NUMBER`
- `ten_nguoi_thu_huong` -> `ACC_NAME`
- `ten_ngan_hang`, `ngan_hang_viet_tat` -> `BANK_ID`

### 8.4 `D_DRIVER_SERVICE`

- `loai_xe_dang_ky` -> service mapping

### 8.5 `D_DRIVER_CLASS`

- `hang_tai_xe` -> class mapping
- `muc_coc` -> `BONUS_RATE`

### 8.6 `D_DRIVER_PROPERTY`

- store dynamic text, number, date, and file fields

### 8.7 `D_DRIVER_DOCUMENT_APPROVAL`

- store approval status fields for document workflow

### 8.8 `D_DRIVER_METRIC`

- store driver experience and other criteria values

---

## 9. Data Accuracy Rules Per Driver

This is the most important requirement.

Every inserted child record must belong to the exact driver created from the same CSV row.

### To guarantee this:

- use a stable driver business key during import
- store `driverBusinessKey -> driverId` after inserting `D_DRIVER`
- use this mapping for all child inserts
- never rely on row index alone
- never insert child rows before `DRIVER_ID` is known
- keep one import context object per driver

### Example in-memory mappings

```text
identityNumber -> driverId
phone -> partyId
(bankId + accNumber) -> bankAccId
serviceCode -> serviceId
classCode -> classId
propertyCode -> propertyId
criteriaCode -> criteriaId
```

This prevents:

- wrong foreign key assignment
- child data attached to wrong driver
- duplicated relationships
- invalid batch ordering

---

## 10. API Design

### Endpoint

```http
POST /api/drivers/import
Content-Type: multipart/form-data
```

### Request

- `file`: CSV file

### Processing steps

1. read uploaded CSV file
2. parse and validate each row
3. normalize raw data
4. resolve master data
5. insert directly into database in the correct order
6. collect success and error summary
7. return response

---

## 11. Response Structure

Suggested response fields:

- `totalRows`
- `successRows`
- `failedRows`
- `errors`
- `message`
- `executionTime`

Example:

```json
{
  "totalRows": 500,
  "successRows": 472,
  "failedRows": 28,
  "errors": [
    {
      "rowNo": 12,
      "field": "khu_vuc_hoat_dong",
      "message": "Region not found"
    }
  ],
  "message": "Import completed",
  "executionTime": 3450
}
```

---

## 12. Batch Insert Strategy

### Recommended batch size

- 100 to 500 rows per batch
- configurable by application property

### Why batch insert is required

- reduce database round trips
- improve performance
- reduce transaction overhead
- support large file import
- allow controlled rollback scope

### Recommended batching approach

- parse file continuously
- collect payloads by table
- flush batch when threshold is reached
- clear temporary payload list after successful insert

---

## 13. Idempotent Import Rules

Import should be safe to run again without creating duplicate records.

### Suggested unique constraints

- `D_PARTY(PHONE)`
- `D_BANK_ACC(BANK_ID, ACC_NUMBER)`
- `D_DRIVER_SERVICE(DRIVER_ID, SERVICE_ID)`
- `D_DRIVER_CLASS(DRIVER_ID, CLASS_ID, SERVICE_ID)`
- `D_DRIVER_PARTY(DRIVER_ID, PARTY_ID)`
- `D_DRIVER_BANK_ACC(DRIVER_ID, BANK_ACC_ID)`
- `D_DRIVER_PROPERTY(DRIVER_ID, PROPERTY_ID)`
- `D_DRIVER_METRIC(DRIVER_ID, CRITERIA_ID)`

### Recommended behavior

- check by business key before insert
- insert only when not found
- update only when business rules explicitly allow it

---

## 14. Error Handling Rules

### Principles

- one bad row should not fail the entire file unless business policy requires it
- capture errors per row
- make error messages readable and actionable

### Common error types

- missing required field
- invalid date
- invalid phone
- invalid CCCD
- unknown region
- unknown service
- unknown bank
- duplicate forbidden relationship
- foreign key resolution failure

### Error detail should include

- row number
- field name
- raw value
- message
- optional suggestion

---

## 15. Common Risks

### 15.1 Multi-value field

Example:

- `Automatic,Manual`

Must split and process separately.

### 15.2 Inconsistent status text

Examples:

- `Active`
- `Hồ sơ chưa đạt`
- `Chưa ký`
- `Đã ký`

A clear status mapping table is required.

### 15.3 Dirty file path

Example:

- `["public\\/media\\/driver\\/2\\/id_card_back.png"]`

Must clean before save.

### 15.4 Invalid null or wrong format data

Common risky fields:

- `CCCD`
- `SĐT`
- `STK`
- date fields

---

## 16. Clean Code Guidelines

Implementation should remain clear and maintainable.

### Recommended rules

- keep controller thin
- move business logic into service layer
- separate parser, validator, normalizer, resolver, and insert logic
- avoid large all-in-one import method
- group SQL by table responsibility
- use clear naming for business key and resolved IDs
- isolate repeated logic into helper methods
- keep transaction boundaries explicit

### Good method naming examples

- `parseCsvRow(...)`
- `validateDriverRow(...)`
- `normalizeDriverRow(...)`
- `resolveRegionId(...)`
- `resolveBankId(...)`
- `insertDriversBatch(...)`
- `insertDriverServicesBatch(...)`
- `insertDriverPropertiesBatch(...)`

---

## 17. Comments for Implementation

Comments should explain important business logic only.

### Recommended comment style

```java
// Parse one CSV row into in-memory import object
// Validate required values before any database operation
// Normalize raw text into internal system format
// Insert driver core data first to obtain DRIVER_ID
// Use driverId mapping for all child table inserts
// Flush batch insert when reaching configured batch size
// Skip invalid row and collect error for response summary
```

### Avoid

- redundant comments
- comments that repeat method name
- outdated comments not aligned with actual logic

---

## 18. Suggested Pseudocode

```text
loadMasterData()

for each csvRow:
    row = parse(csvRow)
    validate(row)
    normalize(row)
    collect driver core payload

insert D_DRIVER batch
build driverBusinessKey -> driverId map

for each valid row:
    resolve and insert D_PARTY / D_DRIVER_PARTY
    resolve and insert D_BANK_ACC / D_DRIVER_BANK_ACC
    insert D_DRIVER_SERVICE
    insert D_DRIVER_CLASS
    insert D_DRIVER_PROPERTY
    insert D_DRIVER_DOCUMENT_APPROVAL
    insert D_DRIVER_METRIC

update REFERRER_ID if needed

return import summary
```

---

## 19. Conclusion

The correct import strategy for this project is:

- direct CSV import
- no staging table
- no entity-based persistence flow
- validate and normalize in memory
- insert directly into database in safe dependency order
- maintain stable mapping to guarantee correct child ownership per driver
- use batch insert for performance
- keep code clean and well-commented

Recommended final order:

1. load master data
2. parse and validate file
3. insert `D_DRIVER`
4. update `REFERRER_ID`
5. insert `D_PARTY` and `D_DRIVER_PARTY`
6. insert `D_BANK_ACC` and `D_DRIVER_BANK_ACC`
7. insert `D_DRIVER_SERVICE`
8. insert `D_DRIVER_CLASS`
9. insert `D_DRIVER_PROPERTY`
10. insert `D_DRIVER_DOCUMENT_APPROVAL`
11. insert `D_DRIVER_METRIC`

This is the safest direct-import design to ensure:

- no foreign key conflict
- correct linkage for each driver
- maintainable and production-ready backend code
