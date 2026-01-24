package com.example.SP26SE025.service;

import com.example.SP26SE025.config.AiServiceConfig;
import com.example.SP26SE025.dtos.AiResponseDto;
import com.example.SP26SE025.exception.AiServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * Dịch vụ client gọi AI-Service (FastAPI) microservice
 * Xử lý upload ảnh và yêu cầu dự đoán (FR-3 AI Output)
 */
@Service
public class AiServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AiServiceClient.class);

    @Autowired
    private RestTemplate aiServiceRestTemplate;

    @Autowired
    private AiServiceConfig aiServiceConfig;

    /**
     * Gửi ảnh võng mạc tới AI-Service để phân tích
     * 
     * @param imageFile Tệp ảnh để phân tích
     * @param testId    ID bài test/phân tích (để theo dõi)
     * @param patientId ID bệnh nhân (để theo dõi)
     * @return Phản hồi phân tích AI với predictions
     * @throws AiServiceException nếu AI service không sẵn sàng hoặc trả về lỗi
     */
    public AiResponseDto analyzRetinalImage(File imageFile, String testId, String patientId) {
        if (!aiServiceConfig.isEnabled()) {
            logger.warn("AI Service is disabled in configuration");
            throw new AiServiceException("AI Service is currently disabled");
        }

        if (!imageFile.exists()) {
            throw new AiServiceException("Image file not found: " + imageFile.getAbsolutePath());
        }

        try {
            String predictUrl = aiServiceConfig.getAiServiceUrl() + "/predict";

            // Build multipart form data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(imageFile));

            // Optional: Add metadata headers for tracking
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("X-Test-ID", testId);
            headers.add("X-Patient-ID", patientId);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            logger.info("Sending image to AI-Service: {} (testId: {}, patientId: {})", predictUrl, testId, patientId);

            ResponseEntity<AiResponseDto> response = aiServiceRestTemplate.postForEntity(
                    predictUrl,
                    requestEntity,
                    AiResponseDto.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new AiServiceException(
                        "AI-Service returned error: " + response.getStatusCode(),
                        predictUrl,
                        response.getStatusCodeValue());
            }

            AiResponseDto result = response.getBody();
            if (result == null) {
                throw new AiServiceException("AI-Service returned empty response", predictUrl, 200);
            }

            if (!"success".equalsIgnoreCase(result.getStatus())) {
                throw new AiServiceException(
                        "AI-Service analysis failed: " + result.getStatus(),
                        predictUrl,
                        200);
            }

            logger.info("Successfully received AI analysis for testId: {}", testId);
            return result;

        } catch (RestClientException e) {
            String errorMsg = "Failed to communicate with AI-Service: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new AiServiceException(errorMsg, aiServiceConfig.getAiServiceUrl(), null, e);
        }
    }

    /**
     * Lấy biểu đồ nhiệt cho điều kiện chẩn đoán cụ thể
     * 
     * @param imageFile  Tệp ảnh để phân tích
     * @param classIndex Chỉ số bệnh để trực quan hóa (0-6)
     * @return Dữ liệu ảnh nhị phân (JPEG với heatmap overlay)
     * @throws AiServiceException nếu AI service không sẵn sàng hoặc trả về lỗi
     */
    public byte[] getPredictionHeatmap(File imageFile, int classIndex) {
        if (!aiServiceConfig.isEnabled()) {
            throw new AiServiceException("AI Service is currently disabled");
        }

        if (!imageFile.exists()) {
            throw new AiServiceException("Image file not found: " + imageFile.getAbsolutePath());
        }

        try {
            String camUrl = aiServiceConfig.getAiServiceUrl() + "/predict_with_cam";

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(imageFile));
            body.add("class_idx", String.valueOf(classIndex));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            logger.info("Requesting heatmap from AI-Service for class index: {}", classIndex);

            ResponseEntity<byte[]> response = aiServiceRestTemplate.postForEntity(
                    camUrl,
                    requestEntity,
                    byte[].class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new AiServiceException(
                        "AI-Service heatmap request failed: " + response.getStatusCode(),
                        camUrl,
                        response.getStatusCodeValue());
            }

            byte[] imageData = response.getBody();
            if (imageData == null || imageData.length == 0) {
                throw new AiServiceException("AI-Service returned empty heatmap", camUrl, 200);
            }

            logger.info("Successfully received heatmap for class index: {}", classIndex);
            return imageData;

        } catch (RestClientException e) {
            String errorMsg = "Failed to get heatmap from AI-Service: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new AiServiceException(errorMsg, aiServiceConfig.getAiServiceUrl(), null, e);
        }
    }

    /**
     * Health check - verify AI-Service is running
     * 
     * @return true if AI-Service is accessible, false otherwise
     */
    public boolean isAiServiceHealthy() {
        if (!aiServiceConfig.isEnabled()) {
            return false;
        }

        try {
            String healthUrl = aiServiceConfig.getAiServiceUrl() + "/";
            ResponseEntity<String> response = aiServiceRestTemplate.getForEntity(healthUrl, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.warn("AI-Service health check failed: {}", e.getMessage());
            return false;
        }
    }
}
