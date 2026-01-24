package com.example.SP26SE025.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity để lưu trữ phản hồi bác sĩ về dự đoán AI (FR-14/15, FR-19)
 * Theo dõi đề xuất, sửa chúa và cháp nhẫn của bác sĩ
 */
@Entity
@Table(name = "ai_feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feedback_id", unique = true, nullable = false)
    private String feedbackId; // UUID for tracking

    @OneToOne
    @JoinColumn(name = "inference_metadata_id", nullable = false)
    private InferenceMetadata inferenceMetadata; // Link to inference result

    @Column(name = "doctor_id")
    private Long doctorId; // Which doctor reviewed

    @Column(name = "approved")
    private Boolean approved; // true=approve AI, false=disagree, null=pending

    @Column(name = "corrected_overall_risk")
    private String correctedOverallRisk; // Doctor's corrected risk assessment

    @Column(name = "corrected_conditions", columnDefinition = "NVARCHAR(MAX)")
    private String correctedConditions; // JSON array of conditions doctor agrees with

    @Column(name = "disagreed_conditions", columnDefinition = "NVARCHAR(MAX)")
    private String disagreedConditions; // JSON array of conditions doctor disagrees

    @Column(name = "medical_notes", columnDefinition = "NVARCHAR(MAX)")
    private String medicalNotes; // Doctor's clinical notes & corrections

    @Column(name = "confidence_adjustment")
    private Double confidenceAdjustment; // -1.0 to 1.0 to adjust AI confidence

    @Column(name = "is_feedback_for_retraining")
    private Boolean isFeedbackForRetraining = true; // Use this feedback to retrain model?

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Constructor ---
    public Feedback() {
    }

    public Feedback(String feedbackId, InferenceMetadata inferenceMetadata, Long doctorId) {
        this.feedbackId = feedbackId;
        this.inferenceMetadata = inferenceMetadata;
        this.doctorId = doctorId;
    }

    // --- JPA Callbacks ---
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }
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

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public InferenceMetadata getInferenceMetadata() {
        return inferenceMetadata;
    }

    public void setInferenceMetadata(InferenceMetadata inferenceMetadata) {
        this.inferenceMetadata = inferenceMetadata;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getCorrectedOverallRisk() {
        return correctedOverallRisk;
    }

    public void setCorrectedOverallRisk(String correctedOverallRisk) {
        this.correctedOverallRisk = correctedOverallRisk;
    }

    public String getCorrectedConditions() {
        return correctedConditions;
    }

    public void setCorrectedConditions(String correctedConditions) {
        this.correctedConditions = correctedConditions;
    }

    public String getDisagreedConditions() {
        return disagreedConditions;
    }

    public void setDisagreedConditions(String disagreedConditions) {
        this.disagreedConditions = disagreedConditions;
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
    }

    public Double getConfidenceAdjustment() {
        return confidenceAdjustment;
    }

    public void setConfidenceAdjustment(Double confidenceAdjustment) {
        this.confidenceAdjustment = confidenceAdjustment;
    }

    public Boolean getIsFeedbackForRetraining() {
        return isFeedbackForRetraining;
    }

    public void setIsFeedbackForRetraining(Boolean isFeedbackForRetraining) {
        this.isFeedbackForRetraining = isFeedbackForRetraining;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
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
