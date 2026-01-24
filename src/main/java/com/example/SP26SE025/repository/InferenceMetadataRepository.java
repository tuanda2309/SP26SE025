package com.example.SP26SE025.repository;

import com.example.SP26SE025.entity.InferenceMetadata;
import com.example.SP26SE025.entity.AnalysisRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Kho lưu trữ cho thực thể InferenceMetadata
 * Quản lý theo dõi suy luận AI và dữ liệu phản hồi (FR-14/15, FR-19)
 */
@Repository
public interface InferenceMetadataRepository extends JpaRepository<InferenceMetadata, Long> {

    /**
     * Tìm siêu dữ liệu suy luận theo ID suy luận từ AI-Service
     */
    Optional<InferenceMetadata> findByInferenceId(String inferenceId);

    /**
     * Tìm siêu dữ liệu suy luận theo bản ghi phân tích liên kết
     */
    Optional<InferenceMetadata> findByAnalysisRecord(AnalysisRecord analysisRecord);

    /**
     * Tìm tất cả suy luận chờ nhận xét của bác sĩ
     */
    List<InferenceMetadata> findByDoctorReviewedFalse();

    /**
     * Find all inferences reviewed by specific doctor
     */
    List<InferenceMetadata> findByReviewedByDoctorId(Long doctorId);

    /**
     * Find all inferences that were approved by doctor
     */
    List<InferenceMetadata> findByDoctorApprovedTrue();

    /**
     * Find all inferences that were rejected by doctor (approved = false)
     */
    List<InferenceMetadata> findByDoctorApprovedFalse();
}
