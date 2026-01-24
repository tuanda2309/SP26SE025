package com.example.SP26SE025.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * DTO cho siêu dữ liệu AI-Service (phiên bản mô hình, ngưỡng, thời gian suy
 * luận)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiMetadataDto {

    private AiModelInfoDto model;
    private Map<String, Double> thresholds;
    private String device;
    private Integer inference_time_ms;

    // Constructors
    public AiMetadataDto() {
    }

    public AiMetadataDto(AiModelInfoDto model, Map<String, Double> thresholds,
            String device, Integer inference_time_ms) {
        this.model = model;
        this.thresholds = thresholds;
        this.device = device;
        this.inference_time_ms = inference_time_ms;
    }

    // Getters and Setters
    public AiModelInfoDto getModel() {
        return model;
    }

    public void setModel(AiModelInfoDto model) {
        this.model = model;
    }

    public Map<String, Double> getThresholds() {
        return thresholds;
    }

    public void setThresholds(Map<String, Double> thresholds) {
        this.thresholds = thresholds;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Integer getInference_time_ms() {
        return inference_time_ms;
    }

    public void setInference_time_ms(Integer inference_time_ms) {
        this.inference_time_ms = inference_time_ms;
    }
}
