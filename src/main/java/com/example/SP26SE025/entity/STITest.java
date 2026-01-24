package com.example.SP26SE025.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class STITest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "test_name")
    private String testName;   
    @Column(name = "booking_time")         // Tên xét nghiệm (VD: HIV, HPV...)
    private LocalDateTime bookingTime; 
    @Column(name = "result_time") // Thời gian người dùng đặt lịch
    private LocalDateTime resultTime;   // Thời gian có kết quả
    private String result;              // Kết quả xét nghiệm
    private String status;              // pending, completed, canceled...

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public LocalDateTime getResultTime() {
        return resultTime;
    }

    public void setResultTime(LocalDateTime resultTime) {
        this.resultTime = resultTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }
}
