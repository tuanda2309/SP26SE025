package com.example.SP26SE025.dtos;

/**
 * DTO cho việc tải lên hình ảnh lên AI-Service
 * Chứa siêu dữ liệu cần thiết bởi vi dịch vụ AI cho theo dõi và chẩn đoán
 */
public class AiRequestDto {

    private String test_id; // Test/analysis record ID from Spring Boot
    private String patient_id; // Patient ID from Spring Boot
    private String doctor_id; // Optional: doctor reviewing the image
    private String image_filename; // Original filename for reference

    // Constructors
    public AiRequestDto() {
    }

    public AiRequestDto(String test_id, String patient_id) {
        this.test_id = test_id;
        this.patient_id = patient_id;
    }

    public AiRequestDto(String test_id, String patient_id, String doctor_id, String image_filename) {
        this.test_id = test_id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.image_filename = image_filename;
    }

    // Getters and Setters
    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getImage_filename() {
        return image_filename;
    }

    public void setImage_filename(String image_filename) {
        this.image_filename = image_filename;
    }
}
