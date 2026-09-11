# Bài Tập Tổng Hợp Session 07 - FinBank Microservices

Repository này chứa mã nguồn cho hệ thống Microservices của FinBank, thực hiện đầy đủ các yêu cầu của **Bài Tập Tổng Hợp 1, 2, 3 và 4** trong Session 07.

## Cấu trúc hệ thống
1. **config-server (8888):** Cung cấp cấu hình tập trung (native profile).
2. **eureka-server (8761):** Service Registry cho toàn bộ hệ thống.
3. **api-gateway (8222):** API Gateway định tuyến request thông qua Spring Cloud Gateway.
4. **customer-service (8081):** Quản lý thông tin khách hàng.
5. **account-service (8082, 8092, 8102):** Quản lý tài khoản và số dư (Hỗ trợ Load Balancing).
6. **transaction-service (8083):** Quản lý giao dịch chuyển tiền (Sử dụng OpenFeign).

## Thứ tự khởi động (Rất quan trọng)
Để hệ thống hoạt động đúng, vui lòng khởi động theo thứ tự sau (chờ service trước hoàn tất khởi động rồi mới chạy service tiếp theo):
1. Khởi động `config-server` (Port 8888)
2. Khởi động `eureka-server` (Port 8761)
3. Khởi động `customer-service` (Port 8081)
4. Khởi động `account-service` (Port 8082)
   - *Tùy chọn cho Bài 2 (Load Balancing): Chạy thêm 2 instance của account-service trên port 8092 và 8102.*
     - Mở terminal trong thư mục `account-service` và chạy: `./gradlew bootRun --args='--server.port=8092'`
     - Mở thêm 1 terminal khác chạy: `./gradlew bootRun --args='--server.port=8102'`
5. Khởi động `transaction-service` (Port 8083)
6. Cuối cùng, khởi động `api-gateway` (Port 8222)

## Cách kiểm thử
1. Đảm bảo tất cả service đã đăng ký thành công trên Eureka Dashboard: [http://localhost:8761](http://localhost:8761).
2. Import file `FinBank.postman_collection.json` (nằm trong thư mục `postman/`) vào ứng dụng Postman.
3. **Bài 1:** Chạy request `Bài 1 - Customer Service` để kiểm tra Gateway.
4. **Bài 2:** Chạy request `Bài 2 - Load Balancing Account Info` nhiều lần và quan sát port trả về luân phiên (Round Robin) giữa 8082, 8092 và 8102.
5. **Bài 3 & 4:** 
   - Chạy các request chuyển tiền với các kịch bản thành công/thất bại tương ứng.
   - Khi chuyển tiền thành công, copy `Mã GD` trong response và thay vào biến `TRANSACTION_ID` trong request `Bài 3&4 - Lịch sử giao dịch chi tiết` để xem thông tin giao dịch có đính kèm thông tin Customer.

## Nhận xét cá nhân về RestTemplate vs OpenFeign (Bài 4)
- **Độ dễ đọc:** OpenFeign cho phép viết mã rất gọn gàng và tường minh bằng interface (nhìn giống như khai báo một service cục bộ). Nó ẩn đi hoàn toàn các chi tiết kỹ thuật như xây dựng URL thủ công hay parse JSON body.
- **Khả năng bảo trì:** Khi có thay đổi về endpoint (thêm path variable, đổi phương thức HTTP), ta chỉ cần sửa duy nhất annotation trong interface của Feign. Ngược lại với RestTemplate, nếu logic gọi nằm rải rác nhiều nơi, việc tìm và sửa string URL rất rủi ro.
- **Tổng quan:** OpenFeign là lựa chọn ưu việt hơn hẳn RestTemplate cho giao tiếp đồng bộ giữa các Microservice nhờ giảm thiểu lượng code "boilerplate" và tăng tính dễ đọc.

## Bài 5: Dịch vụ Khoản vay & Gateway Filter
1. **Khởi động thêm loan-service (Port 8084)** sau khi các service khác đã chạy.
2. Dịch vụ này sử dụng `FeignClient` để liên kết với `customer-service` và `account-service` phục vụ cho nghiệp vụ đăng ký khoản vay.
3. API Gateway đã được cấu hình thêm một `LoggingFilter` (Global Filter). Khi gọi qua Gateway (ví dụ POST `/api/loans/apply`), bạn sẽ thấy:
   - Log ghi nhận ở console của api-gateway.
   - Header `X-Response-Time` trong response của Postman (tab Headers).
