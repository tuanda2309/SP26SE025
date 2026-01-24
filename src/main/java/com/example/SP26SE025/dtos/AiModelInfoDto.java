package com.example.SP26SE025.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * DTO cho thông tin mô hình AI trong siêu dữ liệu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiModelInfoDto {

    private String name;
    private String version;
    private String input_size;
    private List<String> labels;

    // Constructors
    public AiModelInfoDto() {
    }

    public AiModelInfoDto(String name, String version, String input_size, List<String> labels) {
        this.name = name;
        this.version = version;
        this.input_size = input_size;
        this.labels = labels;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInput_size() {
        return input_size;
    }

    public void setInput_size(String input_size) {
        this.input_size = input_size;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
