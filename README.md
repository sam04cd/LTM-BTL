<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
   Gửi tin nhắn Broadcast qua UDP
</h2>
<div align="center">
    <p align="center">
        <img alt="AIoTLab Logo" width="170" src="https://github.com/user-attachments/assets/711a2cd8-7eb4-4dae-9d90-12c0a0a208a2" />
        <img alt="AIoTLab Logo" width="180" src="https://github.com/user-attachments/assets/dc2ef2b8-9a70-4cfa-9b4b-f6c2f25f1660" />
        <img alt="DaiNam University Logo" width="200" src="https://github.com/user-attachments/assets/77fe0fd1-2e55-4032-be3c-b1a705a1b574" />
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>

## 1. Giới thiệu

Trong mô hình này, một máy tính có thể gửi một gói tin đến **tất cả các thiết bị trong cùng mạng LAN** mà không cần biết địa chỉ IP cụ thể của từng máy. Điều này giúp việc **truyền thông điệp nhanh chóng và tiện lợi**, đặc biệt hữu ích trong các tình huống như:  
- Gửi thông báo hệ thống cho nhiều người dùng.  
- Ứng dụng chat nội bộ trong mạng LAN.  
- Tự động phát hiện dịch vụ (service discovery).  

## 2. Công nghệ sử dụng
- **Ngôn ngữ lập trình:** Java (JDK 21)  
- **Giao diện:** Java Swing  
- **Giao thức mạng:** UDP (User Datagram Protocol)  
- **IDE:** Eclipse  
## 3. Hình ảnh các chức năng
<img width="604" height="487" alt="image" src="https://github.com/user-attachments/assets/532f6192-8716-4fb3-8720-c859e4b560bb" />
<p align = "center">Hình 1: Giao diện server </p>

<img width="338" height="153" alt="image" src="https://github.com/user-attachments/assets/9820f2cd-dc5e-4d80-9a50-65f33be45bc1" />
<p align = "center">Hình 2: Nhập ip server </p>

<img width="620" height="476" alt="image" src="https://github.com/user-attachments/assets/7d538fc9-f57e-4903-849d-dd319b26f5cd" />
<p align = "center">Hình 3: Giao diện client </p>

## 4. Các bước cài đặt
### Yêu cầu hệ thống
- JDK 21 hoặc cao hơn
- Eclipse IDE (khuyến nghị bản mới nhất)
- Git đã cài trên máy

### Bước 1: Clone project từ GitHub
```bash
git clone https://github.com/sam04cd/LTM-Gui-tin-nhan-Broadcast-qua-UDP.git
```
###Bước 2: Import project vào Eclipse

- Mở Eclipse
- Vào File → Import
- Chọn Existing Projects into Workspace
- Chọn thư mục project vừa clone về
- Nhấn Finish

Bước 3: Kiểm tra môi trường

- Đảm bảo project chạy trên JavaSE-21 (hoặc phiên bản JDK bạn đã cài).
- Nếu thiếu thư viện, vào Project → Properties → Java Build Path để thêm JDK phù hợp.

Bước 4: Chạy ứng dụng

- Mở class Server → Run để khởi động server.
- Mở class Client → Run để khởi động client.
- Có thể mở nhiều client cùng lúc để test broadcast.

Bước 5: Gửi và nhận tin nhắn

- Nhập nội dung tin nhắn → nhấn Send.
- Tất cả client khác trong cùng mạng LAN sẽ nhận được tin nhắn broadcast.
