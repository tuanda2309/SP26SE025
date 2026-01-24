package com.example.SP26SE025.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity để lưu trữ siêu dữ liệu suy luận từ AI-Service
 * Theo dõi inference_id cho quy trình phản hồi và sửa chúa (FR-14/15, FR-19)
 */
@Entity
@Table(name = "inference_metadata")
public class InferenceMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inference_id", unique = true, nullable = false)
    private String inferenceId; // UUID từ AI-Service

    @OneToOne
    @JoinColumn(name = "analysis_record_id", nullable = false)
    private AnalysisRecord analysisRecord; // Liên kết đến kết quả phân tích

    @Column(name = "model_version")
    private String modelVersion; // Phiên bản mô hình AI được sử dụng

    @Column(name = "inference_time_ms")
    private Integer inferenceTimeMs; // Thời gian suy luận

    @Column(name = "device")
    private String device; // GPU/CPU được sử dụng

    @Column(name = "overall_risk")
    private String overallRisk; // CAO, TRUNG BÌNH, THẬP, RẤT THẬP

    @Column(name = "doctor_reviewed")
    private Boolean doctorReviewed = false; // FR-14: Bác sĩ đã nhān xét?

    @Column(name = "doctor_approved")
    private Boolean doctorApproved; // FR-15: Bác sĩ cháp nhẫn kết quả AI?

    @Column(name = "reviewed_by_doctor_id")
    private Long reviewedByDoctorId; // Bác sĩ nào đã nhān xét

    @Column(name = "review_notes", columnDefinition = "NVARCHAR(MAX)")
    private String reviewNotes; // Ghi chú/sửa chúa của bác sĩ

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt; // Khi nào bác sĩ đã nhān xét

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Constructor ---
    public InferenceMetadata() {
    }

    public InferenceMetadata(String inferenceId, AnalysisRecord analysisRecord,
            String modelVersion, Integer inferenceTimeMs, String device, String overallRisk) {
        this.inferenceId = inferenceId;
        this.analysisRecord = analysisRecord;
        this.modelVersion = modelVersion;
        this.inferenceTimeMs = inferenceTimeMs;
        this.device = device;
        this.overallRisk = overallRisk;
    }

    // --- JPA Callbacks ---
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInferenceId() {
        return inferenceId;
    }

    public void setInferenceId(String inferenceId) {
        this.inferenceId = inferenceId;
    }

    public AnalysisRecord getAnalysisRecord() {
        return analysisRecord;
    }

    public void setAnalysisRecord(AnalysisRecord analysisRecord) {
        this.analysisRecord = analysisRecord;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public Integer getInferenceTimeMs() {
        return inferenceTimeMs;
    }

    public void setInferenceTimeMs(Integer inferenceTimeMs) {
        this.inferenceTimeMs = inferenceTimeMs;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getOverallRisk() {
        return overallRisk;
    }

    public void setOverallRisk(String overallRisk) {
        this.overallRisk = overallRisk;
    }

    public Boolean getDoctorReviewed() {
        return doctorReviewed;
    }

    public void setDoctorReviewed(Boolean doctorReviewed) {
        this.doctorReviewed = doctorReviewed;
    }

    public Boolean getDoctorApproved() {
        return doctorApproved;
    }

    public void setDoctorApproved(Boolean doctorApproved) {
        this.doctorApproved = doctorApproved;
    }

    public Long getReviewedByDoctorId() {
        return reviewedByDoctorId;
    }

    public void setReviewedByDoctorId(Long reviewedByDoctorId) {
        this.reviewedByDoctorId = reviewedByDoctorId;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
