package com.example.SP26SE025.service;

import com.example.SP26SE025.entity.Notification;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Tạo notification mới
     */
    public Notification createNotification(User user, String title, String message, String type) {
        Notification notif = new Notification(user, title, message, type);
        return notificationRepository.save(notif);
    }

    /**
     * Tạo notification mới với liên kết đến AnalysisRecord
     */
    public Notification createNotification(User user, String title, String message, String type, Long relatedRecordId) {
        Notification notif = new Notification(user, title, message, type, relatedRecordId);
        return notificationRepository.save(notif);
    }

    /**
     * Lấy tất cả notification của user
     */
    public List<Notification> getAllNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Lấy notification chưa đọc của user
     */
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    /**
     * Đếm notification chưa đọc
     */
    public long countUnreadNotifications(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Mark notification as read
     */
    public void markAsRead(Long notificationId) {
        Optional<Notification> notif = notificationRepository.findById(notificationId);
        if (notif.isPresent()) {
            Notification notification = notif.get();
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    /**
     * Mark all notifications of user as read
     */
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifs = getUnreadNotifications(user);
        for (Notification notif : unreadNotifs) {
            notif.setRead(true);
            notificationRepository.save(notif);
        }
    }

    /**
     * Xóa notification
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Lấy notification theo ID
     */
    public Optional<Notification> getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId);
    }

    /**
     * Lấy notification liên kết với AnalysisRecord
     */
    public Notification getNotificationByRecordId(Long recordId) {
        return notificationRepository.findByRelatedRecordId(recordId);
    }
}