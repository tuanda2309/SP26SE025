package com.example.SP26SE025.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO cho siêu dữ liệu hình ảnh đầu vào trong phản hồi AI-Service
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiInputDto {

    private String filename;
    private String image_format;

    // Constructors
    public AiInputDto() {
    }

    public AiInputDto(String filename, String image_format) {
        this.filename = filename;
        this.image_format = image_format;
    }

    // Getters and Setters
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getImage_format() {
        return image_format;
    }

    public void setImage_format(String image_format) {
        this.image_format = image_format;
    }
}
