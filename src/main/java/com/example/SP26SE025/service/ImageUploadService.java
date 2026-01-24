package com.example.SP26SE025.service;

import com.example.SP26SE025.dtos.AiResponseDto;
import com.example.SP26SE025.entity.AnalysisRecord;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.exception.AiServiceException;
import com.example.SP26SE025.exception.InvalidImageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Dịch vụ xử lý tải lên hình ảnh và quy trình phân tích AI
 * Điều phối xác thực -> gọi AI-Service -> lưu trữ kết quả (FR-2, FR-3)
 */
@Service
public class ImageUploadService {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadService.class);

    // Các định dạng ảnh được hỗ trợ
    private static final Set<String> ALLOWED_FORMATS = new HashSet<>(
            Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/bmp", "image/tiff"));

    // Kích thước tệp tối đa: 50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    // Thư mục tải lên tạm thời để xử lý - sử dụng thư mục temp hệ thống
    private static final String TEMP_UPLOAD_DIR = System.getProperty("java.io.tmpdir") + File.separator
            + "ai-service-uploads" + File.separator;

    @Autowired
    private AiServiceClient aiServiceClient;

    @Autowired
    private ReportService reportService;

    @Autowired
    private AiInferenceService aiInferenceService; // Phase 1: Save to database

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tải lên và phân tích hình ảnh võng mạc
     * Quy trình: Xác thực -> Lưu tạm -> Gọi AI-Service -> Phân tích phản hồi -> Lưu
     * kết quả
     * 
     * @param imageFile MultipartFile từ frontend
     * @param user      Người dùng thực hiện tải lên (có thể là null cho chế độ
     *                  test)
     * @param testId    ID bài kiểm tra/phân tích (để theo dõi)
     * @param patientId ID bệnh nhân (để theo dõi)
     * @return Phản hồi phân tích AI
     * @throws InvalidImageException nếu ảnh không hợp lệ
     * @throws AiServiceException    nếu lệnh gọi AI-Service không thành công
     */
    public AiResponseDto uploadAndAnalyzeImage(MultipartFile imageFile, User user, String testId, String patientId) {
        String userId = (user != null) ? String.valueOf(user.getId()) : "ANONYMOUS";
        logger.info("Starting image upload and analysis for user: {}, testId: {}", userId, testId);

        try {
            // Step 1: Validate image file
            // Bước 1: Xác thực tệp ảnh

            // Bước 2: Lưu vào vị trí tạm thời
            File tempImageFile = saveTemporaryImage(imageFile);
            logger.info("Image saved to temp location: {}", tempImageFile.getAbsolutePath());

            try {
                // Bước 3: Gọi AI-Service để phân tích
                AiResponseDto aiResponse = aiServiceClient.analyzRetinalImage(tempImageFile, testId, patientId);
                logger.info("Received AI analysis for testId: {}", testId);

                // Bước 4: Lưu kết quả phân tích vào cơ sở dữ liệu
                saveAnalysisRecord(imageFile, user, aiResponse);

                return aiResponse;

            } finally {
                // Dọn sạch tệp tạm thời
                cleanupTemporaryFile(tempImageFile);
            }

        } catch (InvalidImageException e) {
            logger.error("Invalid image file: {}", e.getMessage());
            throw e;
        } catch (AiServiceException e) {
            logger.error("AI-Service error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during image upload: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process image: " + e.getMessage(), e);
        }
    }

    /**
     * Xác thực tệp ảnh (định dạng, kích thước, v.v.)
     * 
     * @param imageFile Tệp cần xác thực
     * @throws InvalidImageException nếu xác thực không thành công
     */
    private void validateImageFile(MultipartFile imageFile) throws InvalidImageException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new InvalidImageException("Image file is empty", "EMPTY_FILE");
        }

        String contentType = imageFile.getContentType();
        if (!ALLOWED_FORMATS.contains(contentType)) {
            throw new InvalidImageException(
                    "Unsupported image format: " + contentType + ". Allowed: JPEG, PNG, BMP, TIFF",
                    "INVALID_FORMAT");
        }

        if (imageFile.getSize() > MAX_FILE_SIZE) {
            throw new InvalidImageException(
                    "File size exceeds maximum limit of 50MB. Uploaded size: " + (imageFile.getSize() / 1024 / 1024)
                            + "MB",
                    "FILE_TOO_LARGE");
        }

        logger.debug("Image validation passed: {}", imageFile.getOriginalFilename());
    }

    /**
     * Lưu ảnh vào vị trí tạm thời để xử lý
     * 
     * @param imageFile MultipartFile để lưu
     * @return Tham chiếu tệp đến ảnh đã lưu
     * @throws IOException nếu lưu không thành công
     */
    private File saveTemporaryImage(MultipartFile imageFile) throws IOException {
        File tempDir = new File(TEMP_UPLOAD_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        File tempFile = new File(tempDir, fileName);

        imageFile.transferTo(tempFile);
        logger.info("Image file transferred to: {}", tempFile.getAbsolutePath());

        return tempFile;
    }

    /**
     * Dọn sạch tệp ảnh tạm thời
     * 
     * @param tempFile Tệp tạm thời để xóa
     */
    private void cleanupTemporaryFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            boolean deleted = tempFile.delete();
            if (!deleted) {
                logger.warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath());
            } else {
                logger.debug("Temporary file cleaned up: {}", tempFile.getAbsolutePath());
            }
        }
    }

    /**
     * Lưu bản ghi phân tích vào cơ sở dữ liệu
     * Sử dụng AiInferenceService để lưu trữ cả AnalysisRecord và InferenceMetadata
     * 
     * @param imageFile  Tệp gốc được tải lên
     * @param user       Người dùng tải lên
     * @param aiResponse Phản hồi phân tích AI
     */
    private void saveAnalysisRecord(MultipartFile imageFile, User user, AiResponseDto aiResponse) {
        try {
            aiInferenceService.saveInferenceResult(imageFile, user, aiResponse,
                    "TEST_" + System.currentTimeMillis(),
                    (user != null) ? String.valueOf(user.getId()) : "ANONYMOUS");

            String userId = (user != null) ? String.valueOf(user.getId()) : "ANONYMOUS";
            logger.info("Analysis record saved for user: {}", userId);
        } catch (Exception e) {
            logger.error("Failed to save analysis record: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save analysis: " + e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra xem AI-Service có sẵn sàng hay không
     * 
     * @return true nếu AI-Service khỏe mạnh
     */
    public boolean isAiServiceAvailable() {
        return aiServiceClient.isAiServiceHealthy();
    }
}
