package com.example.SP26SE025.service;

import com.example.SP26SE025.dtos.FeedbackRequestDto;
import com.example.SP26SE025.dtos.FeedbackResponseDto;
import com.example.SP26SE025.entity.Feedback;
import com.example.SP26SE025.entity.InferenceMetadata;
import com.example.SP26SE025.repository.FeedbackRepository;
import com.example.SP26SE025.repository.InferenceMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Dịch vụ cho phản hồi bác sĩ về dự đoán AI (FR-14/15)
 * Xử lý gửi phản hồi, cháp nhận, từ chối và truy xuất
 */
@Service
public class FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private InferenceMetadataRepository inferenceMetadataRepository;

    /**
     * Gửi phản hồi về kết quả dự đoán AI (FR-14)
     * Bác sĩ có thể cháp nhẫn, từ chối hoặc sỚ đằng kến trỊ AI
     * 
     * @param feedbackRequest Chứa dữ liệu phản hồi cả bác sĩ
     * @return Trả lời với ID phản hồi và trạng thái
     */
    @Transactional
    public FeedbackResponseDto submitFeedback(FeedbackRequestDto feedbackRequest) {
        try {
            logger.info("Processing feedback for inference: {}, doctorId: {}",
                    feedbackRequest.getInferenceId(), feedbackRequest.getDoctorId());

            // Tìm suy luận để phản hồi
            Optional<InferenceMetadata> inferenceOpt = inferenceMetadataRepository
                    .findByInferenceId(feedbackRequest.getInferenceId());

            if (!inferenceOpt.isPresent()) {
                logger.warn("Inference not found: {}", feedbackRequest.getInferenceId());
                return new FeedbackResponseDto(
                        UUID.randomUUID().toString(),
                        feedbackRequest.getInferenceId(),
                        feedbackRequest.getDoctorId(),
                        null,
                        "error",
                        "Inference not found");
            }

            InferenceMetadata inference = inferenceOpt.get();

            // Kiểm tra nếu phản hồi đã tồn tại cho suy luận này
            if (feedbackRepository.existsByInferenceMetadata(inference)) {
                logger.warn("Feedback already exists for inference: {}", feedbackRequest.getInferenceId());
                return new FeedbackResponseDto(
                        UUID.randomUUID().toString(),
                        feedbackRequest.getInferenceId(),
                        feedbackRequest.getDoctorId(),
                        null,
                        "error",
                        "Feedback already submitted for this inference");
            }

            // Tạo bản ghi phản hồi mới
            Feedback feedback = new Feedback();
            feedback.setFeedbackId(UUID.randomUUID().toString());
            feedback.setInferenceMetadata(inference);
            feedback.setDoctorId(Long.parseLong(feedbackRequest.getDoctorId()));
            feedback.setApproved(feedbackRequest.getApproveAiResult());
            feedback.setCorrectedOverallRisk(feedbackRequest.getCorrectedOverallRisk());
            feedback.setMedicalNotes(feedbackRequest.getMedicalNotes());
            feedback.setConfidenceAdjustment(feedbackRequest.getConfidenceAdjustment());
            feedback.setIsFeedbackForRetraining(true); // Đánh dấu cho cải tiến mô hình
            feedback.setSubmittedAt(LocalDateTime.now());

            // Chuyển đổi mảng thành JSON để lưu trữ
            if (feedbackRequest.getCorrectedConditions() != null) {
                feedback.setCorrectedConditions(convertArrayToJson(feedbackRequest.getCorrectedConditions()));
            }
            if (feedbackRequest.getDisagreedConditions() != null) {
                feedback.setDisagreedConditions(convertArrayToJson(feedbackRequest.getDisagreedConditions()));
            }

            // Lưu phản hồi
            Feedback savedFeedback = feedbackRepository.save(feedback);

            // Cập nhật siêu dữ liệu suy luận - đánh dấu đã nhān xét
            inference.setDoctorReviewed(true);
            inference.setDoctorApproved(feedbackRequest.getApproveAiResult());
            inference.setReviewedByDoctorId(Long.parseLong(feedbackRequest.getDoctorId()));
            inference.setReviewNotes(feedbackRequest.getMedicalNotes());
            inference.setReviewedAt(LocalDateTime.now());
            inferenceMetadataRepository.save(inference);

            logger.info("Feedback saved: id={}, approved={}", savedFeedback.getId(), savedFeedback.getApproved());

            // Return response
            FeedbackResponseDto response = new FeedbackResponseDto();
            response.setFeedbackId(savedFeedback.getFeedbackId());
            response.setInferenceId(feedbackRequest.getInferenceId());
            response.setDoctorId(feedbackRequest.getDoctorId());
            response.setApproved(feedbackRequest.getApproveAiResult());
            response.setMedicalNotes(feedbackRequest.getMedicalNotes());
            response.setStatus("submitted");
            response.setMessage("Feedback submitted successfully");
            response.setSubmittedAt(savedFeedback.getSubmittedAt());

            return response;

        } catch (Exception e) {
            logger.error("Error submitting feedback: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to submit feedback: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy lịch sử phản hồi cho suy luận cụ thể (FR-15)
     * 
     * @param inferenceId ID suy luận để lấy phản hồi
     * @return Danh sách các phản hồi đã gửi
     */
    public List<FeedbackResponseDto> getFeedbackHistory(String inferenceId) {
        try {
            Optional<InferenceMetadata> inferenceOpt = inferenceMetadataRepository.findByInferenceId(inferenceId);

            if (!inferenceOpt.isPresent()) {
                logger.warn("Inference not found: {}", inferenceId);
                return new ArrayList<>();
            }

            List<Feedback> feedbacks = feedbackRepository.findByInferenceMetadata(inferenceOpt.get());
            List<FeedbackResponseDto> responses = new ArrayList<>();

            for (Feedback feedback : feedbacks) {
                FeedbackResponseDto dto = new FeedbackResponseDto();
                dto.setFeedbackId(feedback.getFeedbackId());
                dto.setInferenceId(inferenceId);
                dto.setDoctorId(String.valueOf(feedback.getDoctorId()));
                dto.setApproved(feedback.getApproved());
                dto.setMedicalNotes(feedback.getMedicalNotes());
                dto.setStatus(feedback.getApproved() != null ? (feedback.getApproved() ? "approved" : "rejected")
                        : "pending");
                dto.setSubmittedAt(feedback.getSubmittedAt());
                responses.add(dto);
            }

            return responses;

        } catch (Exception e) {
            logger.error("Error retrieving feedback history: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve feedback: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy tất cả phản hồi do bác sĩ gửi
     * 
     * @param doctorId ID bác sĩ
     * @return Danh sách các bản ghi phản hồi
     */
    public List<FeedbackResponseDto> getDoctorFeedback(Long doctorId) {
        try {
            List<Feedback> feedbacks = feedbackRepository.findByDoctorId(doctorId);
            List<FeedbackResponseDto> responses = new ArrayList<>();

            for (Feedback feedback : feedbacks) {
                FeedbackResponseDto dto = new FeedbackResponseDto();
                dto.setFeedbackId(feedback.getFeedbackId());
                dto.setInferenceId(feedback.getInferenceMetadata().getInferenceId());
                dto.setDoctorId(String.valueOf(feedback.getDoctorId()));
                dto.setApproved(feedback.getApproved());
                dto.setMedicalNotes(feedback.getMedicalNotes());
                dto.setStatus(feedback.getApproved() != null ? (feedback.getApproved() ? "approved" : "rejected")
                        : "pending");
                dto.setSubmittedAt(feedback.getSubmittedAt());
                responses.add(dto);
            }

            return responses;

        } catch (Exception e) {
            logger.error("Error retrieving doctor feedback: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve doctor feedback: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy thống kê phản hồi
     * 
     * @return Đối tượng với các métric phản hồi khác nhau
     */
    public FeedbackStatsDto getFeedbackStatistics() {
        try {
            long totalApproved = feedbackRepository.findByApprovedTrue().size();
            long totalRejected = feedbackRepository.findByApprovedFalse().size();
            long forRetraining = feedbackRepository.findByIsFeedbackForRetrainingTrue().size();

            FeedbackStatsDto stats = new FeedbackStatsDto();
            stats.setTotalApproved(totalApproved);
            stats.setTotalRejected(totalRejected);
            stats.setTotalForRetraining(forRetraining);
            stats.setApprovalRate(totalApproved > 0 ? (double) totalApproved / (totalApproved + totalRejected) : 0.0);

            return stats;

        } catch (Exception e) {
            logger.error("Error retrieving feedback statistics: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve statistics: " + e.getMessage(), e);
        }
    }

    /**
     * Chuyển đổi mảng thành chuỗi JSON để lưu trữ
     */
    private String convertArrayToJson(String[] array) {
        if (array == null || array.length == 0) {
            return "[]";
        }
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                json.append(",");
            json.append("\"").append(array[i]).append("\"");
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Lớp nội cho thống kê phản hồi
     */
    public static class FeedbackStatsDto {
        private long totalApproved;
        private long totalRejected;
        private long totalForRetraining;
        private double approvalRate;

        // Getters and Setters
        public long getTotalApproved() {
            return totalApproved;
        }

        public void setTotalApproved(long totalApproved) {
            this.totalApproved = totalApproved;
        }

        public long getTotalRejected() {
            return totalRejected;
        }

        public void setTotalRejected(long totalRejected) {
            this.totalRejected = totalRejected;
        }

        public long getTotalForRetraining() {
            return totalForRetraining;
        }

        public void setTotalForRetraining(long totalForRetraining) {
            this.totalForRetraining = totalForRetraining;
        }

        public double getApprovalRate() {
            return approvalRate;
        }

        public void setApprovalRate(double approvalRate) {
            this.approvalRate = approvalRate;
        }
    }
}
