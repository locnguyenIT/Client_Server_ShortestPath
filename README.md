# Client_Server_ShortestPath
ĐỀ TÀI 14: ỨNG DỤNG TÌM ĐƯỜNG ĐI NGẮN NHẤT

******* Yêu cầu về chức năng phía client (phải có GUI) **********

- Nhận dữ liệu đầu vào là 1 file text chứa thông tin các đỉnh đồ thị, các cạnh nối giữa các
đỉnh và trọng số + thông tin đỉnh source - destination. File input cần có tối thiểu 10
đỉnh và không có đỉnh nào tách rời khỏi đồ thị. Cấu trúc file và định dạng file (sử dụng
plaintext, XML hoặc định dạng khác do SV tự quyết định).
- Nếu sử dụng file XML hoặc một dạng file có cấu trúc khác: SV cần phải viết thêm chức
năng convert từ file plaintext sang file có cấu trúc đó để tạo thuận lợi cho người dùng
lúc nhập dữ liệu. Nếu sử dụng file text đơn thuần thì không cần làm chức năng này.
Chức năng này có thể thực hiện ngay phía client, không cần gửi dữ liệu lên server.
- Nhận output từ server là đường đi ngắn nhất từ source -> destination và vẽ cả đồ thị ra
màn hình, riêng đường đi từ source -> destination sẽ được tô đậm hoặc làm nổi bật, in
ra chi phí đường đi đó.
- Chức năng export đồ thị đã được vẽ thành file ảnh. 

******* Yêu cầu về chức năng phía server (phải có GUI) *********
- Kiểm tra định dạng/cấu trúc và dữ liệu đầu vào của file input có hợp lệ.
- Tính toán cách vẽ đồ thị, tính toán đường đi từ source -> destination và gửi về client. Client chỉ
vẽ theo kết quả từ server trả về, không thực hiện tính toán gì thêm.
- Thuật toán tìm đường do nhóm SV tự chọn. 

********** Yêu cầu chung *********:
- Các client phải chạy trên các máy tính khác nhau.
- Mã hóa dữ liệu giữa client – server (Kết hợp mã hóa RSA & AES).

********** Mở Chương trình *********
- Chương trình xây dựng mô hình Client - Server sử dụng giao thức  UDP để kết nối với nhau
- Chạy Server trước rồi Client sau.
