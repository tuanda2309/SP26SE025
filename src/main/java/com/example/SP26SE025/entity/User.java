package com.example.SP26SE025.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private Long id;

    @Column(name = "Username", unique = true, nullable = false)
    private String username;

    // Để nullable = true để hỗ trợ login Google/OAuth2
    @Column(name = "PasswordHash", nullable = true)
    private String password;

    @Column(name = "FullName", columnDefinition = "nvarchar(255)")
    private String fullName;

    @Column(name = "Email", unique = true)
    private String email;

    @Column(name = "Specialist", columnDefinition = "nvarchar(255)")
    private String specialist;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    /**
     * QUAN TRỌNG: Dùng TEXT để lưu chuỗi Base64 ảnh cực dài.
     * nvarchar(255) sẽ gây lỗi 500 khi lưu ảnh.
     */
    @Lob
    @Column(name = "AvatarPath", columnDefinition = "TEXT")
    private String avatarPath;

    @Column(name = "DateOfBirth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @Column(name = "DiabetesType")
    private String diabetesType; // NONE, TYPE_1, TYPE_2, GESTATIONAL

    @Column(name = "Hypertension")
    private Boolean hypertension;

    /**
     * Dùng TEXT để hỗ trợ nội dung khám bệnh dài và có dấu
     */
    @Column(name = "MedicalHistory", columnDefinition = "TEXT")
    private String medicalHistory;

    @Enumerated(EnumType.STRING)
    @Column(name = "Role")
    private Role role;

    @Column(name = "Enabled")
    private Boolean enabled = true;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    // Tự động gán thời gian khi tạo mới user
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.enabled == null) this.enabled = true;
    }

    public User() {
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecialist() { return specialist; }
    public void setSpecialist(String specialist) { this.specialist = specialist; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getDiabetesType() { return diabetesType; }
    public void setDiabetesType(String diabetesType) { this.diabetesType = diabetesType; }

    public Boolean getHypertension() { return hypertension; }
    public void setHypertension(Boolean hypertension) { this.hypertension = hypertension; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean isEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
