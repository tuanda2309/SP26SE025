package com.example.SP26SE025.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO cho yêu cầu phản hồi bác sĩ (FR-14/15)
 * Bác sĩ gửi sửa chúa và nhận xét về dự đoán AI
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackRequestDto {

    private String inferenceId; // Kết quả AI nào để gửi phản hồi
    private String doctorId; // Bác sĩ gửi phản hồi
    private String correctedOverallRisk; // Đánh giá mức độ rủi ro của bác sĩ
    private Boolean approveAiResult; // Bác sĩ cháp nhẫn kết quả AI?
    private String[] correctedConditions; // Which conditions doctor agrees with
    private String[] disagreedConditions; // Which conditions doctor disagrees
    private String medicalNotes; // Doctor's clinical notes (NVARCHAR(MAX))
    private Double confidenceAdjustment; // Adjust confidence score (-1.0 to 1.0)

    // Constructors
    public FeedbackRequestDto() {
    }

    public FeedbackRequestDto(String inferenceId, String doctorId, Boolean approveAiResult, String medicalNotes) {
        this.inferenceId = inferenceId;
        this.doctorId = doctorId;
        this.approveAiResult = approveAiResult;
        this.medicalNotes = medicalNotes;
    }

    // Getters and Setters
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

    public String getCorrectedOverallRisk() {
        return correctedOverallRisk;
    }

    public void setCorrectedOverallRisk(String correctedOverallRisk) {
        this.correctedOverallRisk = correctedOverallRisk;
    }

    public Boolean getApproveAiResult() {
        return approveAiResult;
    }

    public void setApproveAiResult(Boolean approveAiResult) {
        this.approveAiResult = approveAiResult;
    }

    public String[] getCorrectedConditions() {
        return correctedConditions;
    }

    public void setCorrectedConditions(String[] correctedConditions) {
        this.correctedConditions = correctedConditions;
    }

    public String[] getDisagreedConditions() {
        return disagreedConditions;
    }

    public void setDisagreedConditions(String[] disagreedConditions) {
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
}
