package com.example.SP26SE025.dtos;

public class DoctorRegistrationDto {

    private String fullName;    // Họ và tên
    private String username;    // Tên đăng nhập
    private String email;       // Email
    private String password;    // Mật khẩu
    private String specialist;  // Chuyên khoa (VD: Nhãn khoa)

    // Constructor mặc định (Bắt buộc để Spring khởi tạo)
    public DoctorRegistrationDto() {
    }

    // Constructor đầy đủ (Tùy chọn, tiện khi test)
    public DoctorRegistrationDto(String fullName, String username, String email, String password, String specialist) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.specialist = specialist;
    }

    // --- GETTERS VÀ SETTERS (Bắt buộc) ---
    
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }
}