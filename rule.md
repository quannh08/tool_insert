
## 1. Mục tiêu
Tài liệu này mô tả chiến lược mapping dữ liệu từ bảng nguồn hiện tại vào hệ thống database mới theo hướng migration an toàn, dễ kiểm soát lỗi và có thể chạy lại nhiều lần mà không bị trùng dữ liệu.

---

## 2. Nguyên tắc chung

### 2.1 Không import thẳng vào bảng đích
Dữ liệu nguồn nên được đưa vào bảng staging trước, ví dụ:

- `STG_DRIVER_IMPORT`

Mục đích:
- giữ nguyên dữ liệu gốc
- dễ validate
- dễ log lỗi
- dễ re-run

### 2.2 Chuẩn hóa dữ liệu trước khi insert
Cần normalize trước khi mapping:
- `gioi_tinh`: Nam -> 1, Nữ -> 0
- `trang_thai_tai_xe`: map về enum/status chuẩn
- `trang_thai_hop_dong`: map về status chuẩn
- ngày tháng: convert về `DATE` / `TIMESTAMP`
- file path: làm sạch chuỗi file
- `loai_xe_dang_ky`: tách nhiều giá trị nếu có dạng `Automatic,Manual`
- `SĐT`, `CCCD`, `STK`: lưu dạng chuỗi
- `khu_vuc_hoat_dong`: resolve sang `REGION_ID`
- `nguoi_gioi_thieu`: resolve sang `REFERRER_ID`

### 2.3 Import theo thứ tự phụ thuộc khóa ngoại
Không insert toàn bộ trong một lần. Cần chia phase rõ ràng:
1. master data
2. bảng lõi
3. bảng liên kết
4. bảng động
5. bảng approval / metric

---

## 3. Phân nhóm dữ liệu

## 3.1 Nhóm map vào bảng `D_DRIVER`
Các cột:
- `ten` -> `FULL_NAME`
- `ngay_sinh` -> `DOB`
- `gioi_tinh` -> `GENDER`
- `CCCD` -> `IDENTITY_NUMBER`
- `trang_thai_tai_xe` -> `STATUS`
- `khu_vuc_hoat_dong` -> `REGION_ID`
- `nguoi_gioi_thieu` -> `REFERRER_ID`

## 3.2 Nhóm cần resolve master data trước
- `khu_vuc_hoat_dong` -> `D_REGION`
- `loai_xe_dang_ky` -> `D_SERVICE`
- `hang_tai_xe` -> `D_CLASS`
- `ten_ngan_hang`, `ngan_hang_viet_tat` -> danh mục ngân hàng
- `SĐT` -> `D_PARTY`
- thông tin động như địa chỉ, giấy tờ, ngày cấp... -> `D_PROPERTY`

## 3.3 Nhóm map vào bảng liên kết / bảng động
- `SĐT` -> `D_PARTY` + `D_DRIVER_PARTY`
- `STK`, `ten_nguoi_thu_huong`, bank -> `D_BANK_ACC` + `D_DRIVER_BANK_ACC`
- `loai_xe_dang_ky` -> `D_DRIVER_SERVICE`
- `hang_tai_xe`, `muc_coc`, `loai_xe_dang_ky` -> `D_DRIVER_CLASS`
- thông tin hồ sơ -> `D_DRIVER_PROPERTY`
- trạng thái giấy tờ -> `D_DRIVER_DOCUMENT_APPROVAL`
- `kinh_nghiem_lai_xe` -> `D_DRIVER_METRIC`

---

## 4. Thứ tự import đề xuất

## Phase 0 - Import raw vào staging
Tạo bảng:
- `STG_DRIVER_IMPORT`
- chứa toàn bộ dữ liệu nguồn
- có thêm các cột:
  - `IMPORT_STATUS`
  - `ERROR_MESSAGE`
  - `ROW_NO`

## Phase 1 - Seed master data
Chuẩn bị trước:
- `D_REGION`
- `D_SERVICE`
- `D_CLASS`
- `D_PROPERTY`
- `D_CRITERIA`
- bảng danh mục ngân hàng

## Phase 2 - Load dictionary vào memory
Tạo các map:
- `regionName -> regionId`
- `serviceCode -> serviceId`
- `classCode/className -> classId`
- `propertyCode -> propertyId`
- `criteriaCode -> criteriaId`
- `bankCode/bankName -> bankId`

## Phase 3 - Insert `D_DRIVER`
Insert trước bảng lõi:
- `FULL_NAME`
- `DOB`
- `GENDER`
- `IDENTITY_NUMBER`
- `STATUS`
- `REGION_ID`

Lưu ý:
- chưa nên set `REFERRER_ID` ngay nếu cần resolve sau

## Phase 4 - Update `REFERRER_ID`
Sau khi insert xong toàn bộ driver:
- dùng map `old_id -> new_driver_id`
- update lại `REFERRER_ID`

## Phase 5 - Insert `D_PARTY` và `D_DRIVER_PARTY`
Flow:
1. normalize phone
2. tạo hoặc tìm `D_PARTY`
3. tạo `D_DRIVER_PARTY`

## Phase 6 - Insert `D_BANK_ACC` và `D_DRIVER_BANK_ACC`
Flow:
1. resolve ngân hàng
2. tạo hoặc tìm `D_BANK_ACC`
3. tạo `D_DRIVER_BANK_ACC`

## Phase 7 - Insert `D_DRIVER_SERVICE`
`loai_xe_dang_ky` cần:
1. split theo dấu phẩy
2. trim từng giá trị
3. resolve `SERVICE_ID`
4. insert từng dòng vào `D_DRIVER_SERVICE`

## Phase 8 - Insert `D_DRIVER_CLASS`
Map:
- `DRIVER_ID`
- `CLASS_ID`
- `SERVICE_ID`
- `BONUS_RATE`
- `STATUS`

Nếu một driver có nhiều service thì tạo nhiều dòng tương ứng.

## Phase 9 - Insert `D_DRIVER_PROPERTY`
Map các field động như:
- `ngay_cap_CCCD`
- `noi_cap_CCCd`
- `ngay_het_han_cCCD`
- `so_GPLX`
- `so_seria_GPLX`
- `ngay_cap_GPLX`
- `ngay_het_han_GPLX`
- `dia_chi`
- `dia_chi_hien_tai`
- `ngay_dang_ky`
- `ngay_kich_hoat`
- file CCCD/GPLX/xét nghiệm

Lưu theo đúng cột:
- `VALUE_TEXT`
- `VALUE_NUMBER`
- `VALUE_DATE`
- `VALUE_FILE`

## Phase 10 - Insert `D_DRIVER_DOCUMENT_APPROVAL`
Map trạng thái duyệt hồ sơ:
- CCCD mặt trước / sau
- GPLX mặt trước / sau
- giấy xét nghiệm ma túy
- lý lịch tư pháp
- giấy xét nghiệm HIV
- xác thực số điện thoại

Ví dụ rule:
- `Đạt` -> 1
- `Chưa đạt` -> 0 hoặc 2
- `Chờ duyệt` -> 3

## Phase 11 - Insert `D_DRIVER_METRIC`
Map:
- `kinh_nghiem_lai_xe` -> `VALUE_NUMBER`

Có thể mở rộng thêm:
- rating
- completed trips
- doanh thu

---

## 5. Mapping chi tiết theo nhóm

## 5.1 Driver core
- `ten` -> `D_DRIVER.FULL_NAME`
- `ngay_sinh` -> `D_DRIVER.DOB`
- `gioi_tinh` -> `D_DRIVER.GENDER`
- `CCCD` -> `D_DRIVER.IDENTITY_NUMBER`
- `trang_thai_tai_xe` -> `D_DRIVER.STATUS`
- `khu_vuc_hoat_dong` -> `D_DRIVER.REGION_ID`
- `nguoi_gioi_thieu` -> `D_DRIVER.REFERRER_ID`

## 5.2 Phone
- `SĐT` -> `D_PARTY.PHONE`
- sau đó tạo `D_DRIVER_PARTY`

## 5.3 Bank
- `STK` -> `D_BANK_ACC.ACC_NUMBER`
- `ten_nguoi_thu_huong` -> `D_BANK_ACC.ACC_NAME`
- `ten_ngan_hang`, `ngan_hang_viet_tat` -> resolve `BANK_ID`
- sau đó tạo `D_DRIVER_BANK_ACC`

## 5.4 Service
- `loai_xe_dang_ky` -> `D_SERVICE.CODE`
- sau đó tạo `D_DRIVER_SERVICE`

## 5.5 Class
- `hang_tai_xe` -> `D_CLASS.CODE` hoặc `D_CLASS.NAME`
- `muc_coc` -> `D_DRIVER_CLASS.BONUS_RATE`

## 5.6 Dynamic property
- `ngay_cap_CCCD` -> `D_DRIVER_PROPERTY.VALUE_DATE`
- `noi_cap_CCCd` -> `D_DRIVER_PROPERTY.VALUE_TEXT`
- `ngay_het_han_cCCD` -> `D_DRIVER_PROPERTY.VALUE_DATE`
- `so_GPLX` -> `D_DRIVER_PROPERTY.VALUE_TEXT`
- `so_seria_GPLX` -> `D_DRIVER_PROPERTY.VALUE_TEXT`
- `ngay_cap_GPLX` -> `D_DRIVER_PROPERTY.VALUE_DATE`
- `ngay_het_han_GPLX` -> `D_DRIVER_PROPERTY.VALUE_DATE`
- `dia_chi` -> `D_DRIVER_PROPERTY.VALUE_TEXT`
- `dia_chi_hien_tai` -> `D_DRIVER_PROPERTY.VALUE_TEXT`
- `ngay_dang_ky` -> `D_DRIVER_PROPERTY.VALUE_DATE`
- `ngay_kich_hoat` -> `D_DRIVER_PROPERTY.VALUE_DATE`
- file giấy tờ -> `D_DRIVER_PROPERTY.VALUE_FILE`

## 5.7 Document approval
- `trang_thai_cccd_mat_truoc`
- `trang_thai_cccd_mat_sau`
- `trang_thai_gplx_mat_truoc`
- `trang_thai_gplx_mat_sau`
- `trang_thai_giay_xet_nghiem_ma_tuy`
- `trang_thai_ly_lich_tu_phap`
- `trang_thai_giay_xet_nghiem_hiv`
- `trang_thai_xac_thuc_sdt`

## 5.8 Metric
- `kinh_nghiem_lai_xe` -> `D_DRIVER_METRIC.VALUE_NUMBER`

---

## 6. Rule kỹ thuật nên áp dụng

### 6.1 Idempotent import
Import lại không được tạo trùng dữ liệu.

Đề xuất unique key:
- `D_PARTY(PHONE)`
- `D_BANK_ACC(BANK_ID, ACC_NUMBER)`
- `D_DRIVER_SERVICE(DRIVER_ID, SERVICE_ID)`
- `D_DRIVER_CLASS(DRIVER_ID, CLASS_ID, SERVICE_ID)`
- `D_DRIVER_PARTY(DRIVER_ID, PARTY_ID)`
- `D_DRIVER_BANK_ACC(DRIVER_ID, BANK_ACC_ID)`
- `D_DRIVER_PROPERTY(DRIVER_ID, PROPERTY_ID)`
- `D_DRIVER_METRIC(DRIVER_ID, CRITERIA_ID)`

### 6.2 Import theo batch
Không nên commit từng dòng.
Đề xuất:
- batch size 100 đến 500
- log lỗi theo từng row

### 6.3 Có bảng log import
Nên có:
- số dòng thành công
- số dòng lỗi
- số dòng bị skip
- lý do lỗi
- thời gian chạy
- file import

---

## 7. Các rủi ro cần xử lý trước

### 7.1 `loai_xe_dang_ky` có nhiều giá trị
Ví dụ:
- `Automatic,Manual`

Cần split thành nhiều service.

### 7.2 Trạng thái không đồng nhất
Ví dụ:
- `Active`
- `Hồ sơ chưa đạt`
- `Chưa ký`
- `Đã ký`

Cần bảng mapping status rõ ràng.

### 7.3 File path bẩn
Ví dụ:
- `["public\\/media\\/driver\\/2\\/id_card_back.png"]"`

Cần clean trước khi lưu.

### 7.4 Dữ liệu null / sai format
Các trường:
- `CCCD`
- `SĐT`
- `STK`
- ngày tháng

Cần validate trước.

---

## 8. Kết luận

Chiến lược phù hợp nhất là:

1. staging
2. normalize
3. seed master
4. insert `D_DRIVER`
5. insert bảng phụ
6. insert bảng liên kết
7. insert property động
8. insert approval
9. insert metric
10. reconcile dữ liệu

Đây là hướng migration an toàn, dễ kiểm soát, dễ rollback từng phần và phù hợp cho hệ thống backend enterprise.
"""
---

## 9. Yêu cầu phát triển API import CSV

Cần xây dựng 1 API tại tầng `Controller` để nhận file CSV từ phía client và thực hiện import dữ liệu vào database.

### 9.1 Mô tả API
- API nhận request upload 1 file CSV
- Content type dự kiến: `multipart/form-data`
- File upload là dữ liệu nguồn để phục vụ cho quá trình mapping và import vào hệ thống

Ví dụ hướng thiết kế:
- Endpoint: `POST /api/drivers/import`
- Request:
  - `file`: file CSV

### 9.2 Yêu cầu xử lý nghiệp vụ
Sau khi nhận file CSV, hệ thống cần:
1. đọc dữ liệu từ file
2. validate cấu trúc file và dữ liệu từng dòng
3. chuẩn hóa dữ liệu trước khi mapping
4. thực hiện mapping theo đúng chiến lược import đã mô tả ở trên
5. ghi dữ liệu vào database theo đúng thứ tự phụ thuộc

### 9.3 Yêu cầu kỹ thuật khi import
- Hạn chế insert từng bản ghi một
- Ưu tiên gom dữ liệu theo batch rồi mới insert xuống database
- Nên chia batch theo kích thước phù hợp, ví dụ:
  - 100 bản ghi / batch
  - 200 bản ghi / batch
  - hoặc cấu hình động theo hệ thống

### 9.4 Lý do cần import theo batch
Import theo batch giúp:
- giảm số lần gọi xuống database
- tăng tốc độ xử lý
- giảm chi phí transaction
- hạn chế nghẽn hệ thống khi file lớn
- dễ kiểm soát rollback theo từng nhóm dữ liệu

### 9.5 Đề xuất kỹ thuật triển khai
- Sử dụng service riêng cho chức năng import, ví dụ:
  - `DriverImportController`
  - `DriverImportService`
  - `DriverBatchInsertService`
- Có thể đọc file CSV theo stream hoặc theo từng block dữ liệu
- Sau khi parse xong, gom dữ liệu thành từng batch trước khi insert
- Với các bảng liên kết hoặc bảng động, cũng nên xử lý batch nếu số lượng lớn

### 9.6 Yêu cầu trả kết quả
API nên trả về thông tin tổng hợp sau khi import:
- tổng số dòng trong file
- số dòng import thành công
- số dòng import thất bại
- danh sách lỗi nếu có
- thời gian xử lý

Ví dụ response:
- `totalRows`
- `successRows`
- `failedRows`
- `errors`
- `message`

### 9.7 Lưu ý quan trọng
- Không để 1 dòng lỗi làm fail toàn bộ file nếu không cần thiết
- Nên log lỗi theo từng dòng để dễ kiểm tra
- Cần đảm bảo import có thể chạy lại mà không sinh dữ liệu trùng
- Nên kết hợp cơ chế validate + batch insert + log import để đảm bảo tính ổn định