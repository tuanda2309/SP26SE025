package com.example.SP26SE025.controller;

import com.example.SP26SE025.dtos.FeedbackRequestDto;
import com.example.SP26SE025.dtos.FeedbackResponseDto;
import com.example.SP26SE025.service.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bộ điều khiển API REST cho phản hồi bác sĩ về dự đoán AI (FR-14/15)
 * Xử lý gửi phản hồi, truy xuất và thống kê
 */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    private FeedbackService feedbackService;

    /**
     * Gửi phản hồi về dự đoán AI (FR-14)
     * Bác sĩ gửi sửa chúa, cháp nhẫn hoặc từ chối
     * 
     * POST /api/feedback/submit
     * 
     * @param feedbackRequest Dữ liệu phản hồi cả bác sĩ
     * @return Phản hồi gửi phản hồi với ID phản hồi
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequestDto feedbackRequest) {
        logger.info("Feedback submission request for inference: {}", feedbackRequest.getInferenceId());

        try {
            // Xac thực các trường bắt buộc
            if (feedbackRequest.getInferenceId() == null || feedbackRequest.getInferenceId().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "inferenceId is required");
                errorResponse.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Gửi phản hồi
            FeedbackResponseDto response = feedbackService.submitFeedback(feedbackRequest);

            if ("error".equals(response.getStatus())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", response.getMessage());
                errorResponse.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("message", response.getMessage());
            successResponse.put("data", response);
            successResponse.put("timestamp", System.currentTimeMillis());

            logger.info("Feedback submitted successfully: {}", response.getFeedbackId());
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            logger.error("Error submitting feedback: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to submit feedback");
            errorResponse.put("detail", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Lấy lịch sử phản hồi cho suy luận cụ thể (FR-15)
     * 
     * GET /api/feedback/inference/{inferenceId}
     * 
     * @param inferenceId ID suy luận để lấy phản hồi
     * @return Danh sách các bản ghi phản hồi
     */
    @GetMapping("/inference/{inferenceId}")
    public ResponseEntity<?> getFeedbackHistory(@PathVariable String inferenceId) {
        logger.info("Retrieving feedback history for inference: {}", inferenceId);

        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbackHistory(inferenceId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("inferenceId", inferenceId);
            response.put("feedbackCount", feedbacks.size());
            response.put("data", feedbacks);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving feedback history: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to retrieve feedback history");
            errorResponse.put("detail", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Lấy tất cả phản hồi do bác sĩ gửi
     * 
     * GET /api/feedback/doctor/{doctorId}
     * 
     * @param doctorId ID bác sĩ
     * @return Danh sách các bản ghi phản hồi do bác sĩ gửi
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorFeedback(@PathVariable Long doctorId) {
        logger.info("Retrieving feedback for doctor: {}", doctorId);

        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getDoctorFeedback(doctorId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("doctorId", doctorId);
            response.put("feedbackCount", feedbacks.size());
            response.put("data", feedbacks);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving doctor feedback: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to retrieve doctor feedback");
            errorResponse.put("detail", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Lấy thống kê phản hồi (xem trước FR-19)
     * 
     * GET /api/feedback/statistics
     * 
     * @return Thống kê phản hồi (tỷ lệ cháp nhẫn, v.v.)
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getFeedbackStatistics() {
        logger.info("Retrieving feedback statistics");

        try {
            FeedbackService.FeedbackStatsDto stats = feedbackService.getFeedbackStatistics();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", stats);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving statistics: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to retrieve statistics");
            errorResponse.put("detail", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
