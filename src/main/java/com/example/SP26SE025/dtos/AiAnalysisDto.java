package com.example.SP26SE025.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import java.util.List;

/**
 * DTO cho trình nhân phân tích phản hồi của AI-Service (FR-3 Đầu ra AI)
 * Chứa kết quả phân tích, dự đoán và siêu dữ liệu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiAnalysisDto {

    private String overall_risk;
    private Boolean normal;
    private List<String> detected_conditions;
    private String clinical_impression;
    private String recommendation;

    // Constructors
    public AiAnalysisDto() {
    }

    public AiAnalysisDto(String overall_risk, Boolean normal, List<String> detected_conditions,
            String clinical_impression, String recommendation) {
        this.overall_risk = overall_risk;
        this.normal = normal;
        this.detected_conditions = detected_conditions;
        this.clinical_impression = clinical_impression;
        this.recommendation = recommendation;
    }

    // Getters and Setters
    public String getOverall_risk() {
        return overall_risk;
    }

    public void setOverall_risk(String overall_risk) {
        this.overall_risk = overall_risk;
    }

    public Boolean getNormal() {
        return normal;
    }

    public void setNormal(Boolean normal) {
        this.normal = normal;
    }

    public List<String> getDetected_conditions() {
        return detected_conditions;
    }

    public void setDetected_conditions(List<String> detected_conditions) {
        this.detected_conditions = detected_conditions;
    }

    public String getClinical_impression() {
        return clinical_impression;
    }

    public void setClinical_impression(String clinical_impression) {
        this.clinical_impression = clinical_impression;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
