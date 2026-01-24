package com.example.SP26SE025.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Patients")
public class Patient {

    @Id
    @Column(name = "PatientID")
    private String patientId; // Mã hồ sơ (VD: BN-2025001)

    // Liên kết với tài khoản User (Role = CUSTOMER)
    // OneToOne: Một Customer có một hồ sơ Patient
    @OneToOne
    @JoinColumn(name = "UserId", referencedColumnName = "UserId")
    private User user;

    @Column(name = "FullName") // Có thể lấy từ User, nhưng lưu riêng cũng được để tiện truy vấn
    private String fullName;

    @Column(name = "DateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "LastVisitDate")
    private LocalDate lastVisitDate;

    @Column(name = "LastAiResult")
    private String lastAiResult; // VD: "Nguy cơ cao (92%)"

    public Patient() {}

    // Getters & Setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public LocalDate getLastVisitDate() { return lastVisitDate; }
    public void setLastVisitDate(LocalDate lastVisitDate) { this.lastVisitDate = lastVisitDate; }

    public String getLastAiResult() { return lastAiResult; }
    public void setLastAiResult(String lastAiResult) { this.lastAiResult = lastAiResult; }
}