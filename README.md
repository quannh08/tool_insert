# Tool Insert

Spring Boot REST API cho 2 nhu cau:

- preview file CSV/TSV truoc khi map
- import driver data vao staging theo batch, co normalize va log ket qua

## API

### 1. Preview file

`POST /api/v1/imports/files/preview`

Request:

- `multipart/form-data`
- part `file`: file CSV hoac TSV

Response:

- thong tin tong hop import
- danh sach cot gom `originalName` va `normalizedKey`
- preview cac dong duoi dang model chung `FileImportRow`

### 2. Import driver vao staging

`POST /api/drivers/import`

Request:

- `multipart/form-data`
- part `file`: file CSV/TSV

Import flow hien tai bam theo `rule.md` o muc foundation:

1. parse file
2. validate header bat buoc
3. normalize tung dong
4. ghi vao `STG_DRIVER_IMPORT`
5. ghi log vao `IMP_DRIVER_IMPORT_JOB`
6. tra summary `totalRows`, `successRows`, `failedRows`, `errors`, `message`, `durationMs`

Luu y:

- import loi tung dong khong lam fail ca file
- du lieu hien duoc dua vao staging truoc, chua insert sang `D_DRIVER` va cac bang lien ket
- batch size cau hinh tai `app.import.batch-size`

## Chay nhanh

```bash
mvn spring-boot:run
```

Preview:

```bash
curl -X POST http://localhost:8080/api/v1/imports/files/preview \
  -H "Content-Type: multipart/form-data" \
  -F "file="
```

Import:

```bash
curl -X POST http://localhost:8080/api/drivers/import \
  -H "Content-Type: multipart/form-data" \
  -F "file="
```
