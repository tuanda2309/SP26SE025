package com.example.SP26SE025.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_packages")
public class ServicePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String packageName;   // Tên gói (VD: Gói Cơ Bản)
    private Double price;         // Giá tiền (VD: 500000)
    private String period;        // Chu kỳ (VD: /tháng, /năm)
    private String description;   // Mô tả ngắn

    @Column(columnDefinition = "TEXT") 
    private String features;      // Quyền lợi, ngăn cách bởi dấu gạch đứng (|)

    private boolean isPopular;    // Gói HOT (True/False)
    private boolean isActive;     // Đang hoạt động (True/False) - Dùng để Xóa mềm

    // --- Constructor ---
    public ServicePackage() {}

    public ServicePackage(String packageName, Double price, String period, String description, String features, boolean isPopular, boolean isActive) {
        this.packageName = packageName;
        this.price = price;
        this.period = period;
        this.description = description;
        this.features = features;
        this.isPopular = isPopular;
        this.isActive = isActive;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    public boolean isPopular() { return isPopular; }
    public void setPopular(boolean popular) { isPopular = popular; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    // Hàm tách chuỗi features thành mảng để Frontend dùng vòng lặp
    public String[] getFeatureList() {
        if (this.features == null || this.features.isEmpty()) return new String[]{};
        return this.features.split("\\|"); 
    }
}