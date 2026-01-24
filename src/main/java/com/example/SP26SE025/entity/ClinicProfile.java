package com.example.SP26SE025.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clinic_profiles")
public class ClinicProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- THAY ĐỔI LỚN: BỎ QUAN HỆ BẢNG, CHỈ LƯU USERNAME ---
    // Không dùng @OneToOne, không @JoinColumn => Không bao giờ lỗi mapping nữa
    @Column(name = "username_link") 
    private String usernameLink; 
    // --------------------------------------------------------

    @Column(name = "clinic_name", columnDefinition = "nvarchar(255)")
    private String clinicName;

    @Column(name = "representative_name", columnDefinition = "nvarchar(255)")
    private String representativeName;

    @Column(columnDefinition = "nvarchar(500)")
    private String address;

    private String phone;
    private String website;

    @Column(columnDefinition = "nvarchar(1000)")
    private String description;

    private String taxId;
    private String businessLicenseUrl;
    private String medicalLicenseUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    public ClinicProfile() {}

    // --- GETTERS & SETTERS MỚI ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsernameLink() { return usernameLink; }
    public void setUsernameLink(String usernameLink) { this.usernameLink = usernameLink; }

    // Các getter/setter khác giữ nguyên
    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }
    public String getRepresentativeName() { return representativeName; }
    public void setRepresentativeName(String representativeName) { this.representativeName = representativeName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public String getBusinessLicenseUrl() { return businessLicenseUrl; }
    public void setBusinessLicenseUrl(String url) { this.businessLicenseUrl = url; }
    public String getMedicalLicenseUrl() { return medicalLicenseUrl; }
    public void setMedicalLicenseUrl(String url) { this.medicalLicenseUrl = url; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus status) { this.verificationStatus = status; }
}