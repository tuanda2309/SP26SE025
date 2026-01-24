package com.example.SP26SE025.dtos;

import java.time.LocalDateTime;

/**
 * DTO cho phản hồi phản hồi (FR-14/15)
 * Trả lời trạng thái gửi phản hồi và chi tiết
 */
public class FeedbackResponseDto {

    private String feedbackId;
    private String inferenceId;
    private String doctorId;
    private Boolean approved;
    private String medicalNotes;
    private LocalDateTime submittedAt;
    private String status; // "submitted", "approved", "rejected"
    private String message;

    // Constructors
    public FeedbackResponseDto() {
    }

    public FeedbackResponseDto(String feedbackId, String inferenceId, String doctorId,
            Boolean approved, String status, String message) {
        this.feedbackId = feedbackId;
        this.inferenceId = inferenceId;
        this.doctorId = doctorId;
        this.approved = approved;
        this.status = status;
        this.message = message;
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getInferenceId() {
        return inferenceId;
    }

    public void setInferenceId(String inferenceId) {
        this.inferenceId = inferenceId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
