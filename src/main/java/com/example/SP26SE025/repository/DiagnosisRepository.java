package com.example.SP26SE025.repository;

import com.example.SP26SE025.entity.DiagnosisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<DiagnosisResult, Long> {

    // Tìm kiếm linh hoạt:
    // 1. Nếu keyword rỗng -> Bỏ qua điều kiện tên BN
    // 2. Nếu doctorId rỗng -> Bỏ qua điều kiện bác sĩ
    @Query("SELECT d FROM DiagnosisResult d WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR d.patient.fullName LIKE %:keyword%) AND " +
           "(:doctorId IS NULL OR d.doctor.id = :doctorId)")
    List<DiagnosisResult> searchReports(@Param("keyword") String keyword, 
                                        @Param("doctorId") Long doctorId);
}