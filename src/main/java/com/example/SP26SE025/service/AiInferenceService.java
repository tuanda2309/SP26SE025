package com.example.SP26SE025.service;

import com.example.SP26SE025.dtos.AiResponseDto;
import com.example.SP26SE025.entity.AnalysisRecord;
import com.example.SP26SE025.entity.InferenceMetadata;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.repository.AnalysisRecordRepository;
import com.example.SP26SE025.repository.InferenceMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dịch vụ lưu trữ kết quả suy luận AI vào cơ sở dữ liệu (Giai đoạn 1)
 * Lưu AnalysisRecord và InferenceMetadata cho theo dõi và phản hồi
 */
@Service
public class AiInferenceService {

    private static final Logger logger = LoggerFactory.getLogger(AiInferenceService.class);

    // Thư mục tải lên để lưu trữ ảnh
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

    @Autowired
    private AnalysisRecordRepository analysisRecordRepository;

    @Autowired
    private InferenceMetadataRepository inferenceMetadataRepository;

    /**
     * Lưu kết quả suy luận vào cơ sở dữ liệu với cả AnalysisRecord và
     * InferenceMetadata
     * Đợc gọi sau khi AI-Service trả về dự đoán
     * 
     * @param imageFile  Tệp gốc được tải lên
     * @param user       Người dùng tải lên (có thể null)
     * @param aiResponse Phản hồi phân tích AI
     * @param testId     ID bài kiểm tra/phân tích
     * @param patientId  ID bệnh nhân
     * @return AnalysisRecord đã lưu
     */
    @Transactional
    public AnalysisRecord saveInferenceResult(MultipartFile imageFile, User user,
            AiResponseDto aiResponse, String testId, String patientId) {
        try {
            // Bước 1: Lưu tệp ảnh vào đĩa
            String fileName = saveImageFile(imageFile);
            String imageUrl = "/images/ai-uploads/" + fileName;

            logger.info("Image saved: {} -> {}", imageFile.getOriginalFilename(), fileName);

            // Bước 2: Chuyển đổi phản hồi AI thành chuỗi JSON để lưu trữ
            String aiResultJson = convertAiResponseToJson(aiResponse);

            // Bước 3: Tạo và lưu AnalysisRecord
            AnalysisRecord analysisRecord = new AnalysisRecord();
            analysisRecord.setImageName(fileName);
            analysisRecord.setImageUrl(imageUrl);
            analysisRecord.setUser(user);
            analysisRecord.setAiResult(aiResultJson);
            analysisRecord.setCreatedAt(LocalDateTime.now());

            AnalysisRecord savedRecord = analysisRecordRepository.save(analysisRecord);
            logger.info("AnalysisRecord saved: id={}, user={}", savedRecord.getId(),
                    user != null ? user.getId() : "ANONYMOUS");

            // Bước 4: Tạo và lưu InferenceMetadata (cho phản hồi FR-14/15)
            saveInferenceMetadata(savedRecord, aiResponse, testId, patientId);

            return savedRecord;

        } catch (Exception e) {
            logger.error("Failed to save inference result: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save inference to database: " + e.getMessage(), e);
        }
    }

    /**
     * Lưu tệp ảnh vào đĩa
     */
    private String saveImageFile(MultipartFile imageFile) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);

        // Tạo thư mục nếu chưa tồn tại
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.info("Created upload directory: {}", uploadPath.toAbsolutePath());
        }

        // Tạo tên tệp duy nhất
        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Lưu tệp
        try (InputStream inputStream = imageFile.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }

    /**
     * Chuyển đổi DTO phản hồi AI thành chuỗi JSON để lưu trữ
     */
    private String convertAiResponseToJson(AiResponseDto aiResponse) {
        try {
            // Sử dụng ObjectMapper để chuyển đổi thành JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(aiResponse);
        } catch (Exception e) {
            logger.error("Failed to convert AI response to JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize AI response", e);
        }
    }

    /**
     * Lưu siêu dữ liệu suy luận cho theo dõi phản hồi bác sĩ (FR-14/15)
     */
    private void saveInferenceMetadata(AnalysisRecord analysisRecord, AiResponseDto aiResponse,
            String testId, String patientId) {
        try {
            InferenceMetadata metadata = new InferenceMetadata();
            metadata.setInferenceId(UUID.randomUUID().toString()); // ID duy nhất cho theo dõi
            metadata.setAnalysisRecord(analysisRecord);

            // Trích xuất siêu dữ liệu từ phản hồi AI
            if (aiResponse.getMeta() != null) {
                if (aiResponse.getMeta().getModel() != null) {
                    metadata.setModelVersion(aiResponse.getMeta().getModel().getVersion());
                }
                metadata.setInferenceTimeMs(aiResponse.getMeta().getInference_time_ms());
                metadata.setDevice(aiResponse.getMeta().getDevice());
            }

            // Trích xuất mức độ rủi ro
            if (aiResponse.getAnalysis() != null) {
                metadata.setOverallRisk(aiResponse.getAnalysis().getOverall_risk());
            }

            // Ban đầu chưa được nhān xét
            metadata.setDoctorReviewed(false);
            metadata.setDoctorApproved(null); // Chờ xát duyệt

            metadata.setCreatedAt(LocalDateTime.now());

            InferenceMetadata savedMetadata = inferenceMetadataRepository.save(metadata);
            logger.info("InferenceMetadata saved: id={}, inferenceId={}", savedMetadata.getId(),
                    savedMetadata.getInferenceId());

        } catch (Exception e) {
            logger.error("Failed to save inference metadata: {}", e.getMessage(), e);
            // Không throw - siêu dữ liệu là tùy chọn cho chức năng cơ bản
        }
    }

    /**
     * Truy xuất kết quả suy luận theo ID
     */
    public AnalysisRecord getAnalysisRecord(Long recordId) {
        return analysisRecordRepository.findById(recordId).orElse(null);
    }

    /**
     * Truy xuất siêu dữ liệu suy luận theo ID suy luận
     */
    public InferenceMetadata getInferenceMetadata(String inferenceId) {
        return inferenceMetadataRepository.findByInferenceId(inferenceId).orElse(null);
    }
}
