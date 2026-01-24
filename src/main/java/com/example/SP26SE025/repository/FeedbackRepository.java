package com.example.SP26SE025.repository;

import com.example.SP26SE025.entity.Feedback;
import com.example.SP26SE025.entity.InferenceMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Kho lưu trữ cho thực thể Feedback
 * Quản lý phản hồi và sửa chúa của bác sĩ về dự đoán AI (FR-14/15, FR-19)
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Tìm phản hồi theo ID phản hồi
     */
    Optional<Feedback> findByFeedbackId(String feedbackId);

    /**
     * Tìm tất cả phản hồi cho suy luận cụ thể
     */
    List<Feedback> findByInferenceMetadata(InferenceMetadata inferenceMetadata);

    /**
     * Tìm tất cả phản hồi do bác sĩ cụ thể gửi
     */
    List<Feedback> findByDoctorId(Long doctorId);

    /**
     * Tìm tất cả phản hồi được cháp nhẫn (cho cải tiến mô hình)
     */
    List<Feedback> findByApprovedTrue();

    /**
     * Tìm tất cả phản hồi bị từ chối (khi bác sĩ không đồng ý với AI)
     */
    List<Feedback> findByApprovedFalse();

    /**
     * Tìm phản hồi được đánh dấu cho đào tạo lại
     */
    List<Feedback> findByIsFeedbackForRetrainingTrue();

    /**
     * Count feedback for specific doctor
     */
    long countByDoctorId(Long doctorId);

    /**
     * Check if feedback already exists for inference
     */
    boolean existsByInferenceMetadata(InferenceMetadata inferenceMetadata);
}
