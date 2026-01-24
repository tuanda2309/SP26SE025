package com.example.SP26SE025.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người mua

    private String planName;      // Tên gói (VD: Gói Cơ Bản)
    private Double price;         // Giá tiền lúc mua
    private LocalDateTime startDate; // Ngày bắt đầu
    private LocalDateTime endDate;   // Ngày hết hạn
    
    private String status;        // ACTIVE (Đang dùng), EXPIRED (Hết hạn)

    // --- Constructor ---
    public Subscription() {
        this.startDate = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}