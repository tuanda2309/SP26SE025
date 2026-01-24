package com.example.SP26SE025.dtos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportSimpleDTO {
    private Long id;
    private String patientCode;
    private String patientName;
    private String analysisDate;
    private String doctorName;

    public ReportSimpleDTO(Long id, Long patientId, String patientName, LocalDateTime date, String doctorName) {
        this.id = id;
        this.patientCode = "BN-" + patientId;
        this.patientName = patientName;
        // Format ngày tháng thành dd/MM/yyyy
        this.analysisDate = date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
        this.doctorName = doctorName != null ? doctorName : "Chưa chỉ định";
    }

    // Getters
    public Long getId() { return id; }
    public String getPatientCode() { return patientCode; }
    public String getPatientName() { return patientName; }
    public String getAnalysisDate() { return analysisDate; }
    public String getDoctorName() { return doctorName; }
}