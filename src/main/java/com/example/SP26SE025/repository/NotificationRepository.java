package com.example.SP26SE025.repository;

import com.example.SP26SE025.entity.Notification;
import com.example.SP26SE025.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Lấy tất cả notification của một user, sắp xếp theo thời gian mới nhất
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Lấy notification chưa đọc của user
     */
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    /**
     * Đếm notification chưa đọc của user
     */
    long countByUserAndIsReadFalse(User user);

    /**
     * Lấy notification liên kết với một AnalysisRecord
     */
    Notification findByRelatedRecordId(Long relatedRecordId);
}
