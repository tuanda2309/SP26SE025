package com.example.SP26SE025.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * DTO cho phản hồi dự đoán của AI-Service cho một điều kiện bệnh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictionDto {

    private String label;
    private String display_name;
    private Double probability;
    private Integer confidence_score;
    private Boolean detected;
    private String risk_level;
    private String description;
    private String medical_note;

    // Constructors
    public PredictionDto() {
    }

    public PredictionDto(String label, String display_name, Double probability,
            Integer confidence_score, Boolean detected, String risk_level,
            String description, String medical_note) {
        this.label = label;
        this.display_name = display_name;
        this.probability = probability;
        this.confidence_score = confidence_score;
        this.detected = detected;
        this.risk_level = risk_level;
        this.description = description;
        this.medical_note = medical_note;
    }

    // Getters and Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Integer getConfidence_score() {
        return confidence_score;
    }

    public void setConfidence_score(Integer confidence_score) {
        this.confidence_score = confidence_score;
    }

    public Boolean getDetected() {
        return detected;
    }

    public void setDetected(Boolean detected) {
        this.detected = detected;
    }

    public String getRisk_level() {
        return risk_level;
    }

    public void setRisk_level(String risk_level) {
        this.risk_level = risk_level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMedical_note() {
        return medical_note;
    }

    public void setMedical_note(String medical_note) {
        this.medical_note = medical_note;
    }
}
