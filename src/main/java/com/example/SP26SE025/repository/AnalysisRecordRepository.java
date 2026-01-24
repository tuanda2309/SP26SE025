package com.example.SP26SE025.repository;

import com.example.SP26SE025.entity.AnalysisRecord;
import com.example.SP26SE025.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisRecordRepository extends JpaRepository<AnalysisRecord, Long> {
    // Tìm lịch sử phân tích theo User, sắp xếp mới nhất lên đầu
    List<AnalysisRecord> findByUserOrderByCreatedAtDesc(User user);
}