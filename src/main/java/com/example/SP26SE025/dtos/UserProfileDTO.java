package com.example.SP26SE025.dtos;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class UserProfileDTO {

    // 1. Khai báo các thuộc tính (Fields)
    private String fullName;
    private String email;
    private String avatarPath;  // Đường dẫn ảnh đại diện
    
    @Pattern(regexp = "^$|^0[0-9]{9}$", message = "Số điện thoại phải đúng 10 ký tự, bắt đầu bằng số 0 (hoặc để trống)")
    private String phone;
    
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;           // Ngày sinh - optional
    
    private String diabetesType;     // Loại tiểu đường: NONE, TYPE_1, TYPE_2, GESTATIONAL
    private Boolean hypertension;    // Cao huyết áp: true (Có), false (Không), null (Chưa xác định)
    private String medicalHistory;   // Tiền sử bệnh án

    // 2. Constructor mặc định (No-args constructor)
    public UserProfileDTO() {
    }

    // 3. Constructor đầy đủ tham số (All-args constructor)
    public UserProfileDTO(String fullName, String email, String phone, LocalDate dob, String diabetesType, boolean hypertension, String medicalHistory) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.diabetesType = diabetesType;
        this.hypertension = hypertension;
        this.medicalHistory = medicalHistory;
    }

    // 4. Các phương thức Getter và Setter
    
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getDiabetesType() {
        return diabetesType;
    }

    public void setDiabetesType(String diabetesType) {
        this.diabetesType = diabetesType;
    }

    public Boolean getHypertension() {
        return hypertension;
    }

    public void setHypertension(Boolean hypertension) {
        this.hypertension = hypertension;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    @Override
    public String toString() {
        return "UserProfileDTO{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", dob=" + dob +
                ", diabetesType='" + diabetesType + '\'' +
                ", hypertension=" + hypertension +
                ", medicalHistory='" + medicalHistory + '\'' +
                '}';
    }
}