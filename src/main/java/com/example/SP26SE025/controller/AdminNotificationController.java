package com.example.SP26SE025.controller;

import com.example.SP26SE025.entity.Notification;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.repository.NotificationRepository;
import com.example.SP26SE025.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/notifications")
public class AdminNotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // ================= GET ALL =================
    @GetMapping
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Notification req) {

        if (req.getUser() == null || req.getUser().getId() == null) {
            return ResponseEntity.badRequest().body("User is required");
        }

        User user = userRepository.findById(req.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(req.getTitle());
        notification.setMessage(req.getMessage());
        notification.setType(req.getType());
        notification.setRelatedRecordId(req.getRelatedRecordId());
        notification.setRead(false);

        notificationRepository.save(notification);
        return ResponseEntity.ok(notification);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Notification req) {

        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        n.setTitle(req.getTitle());
        n.setMessage(req.getMessage());
        n.setType(req.getType());
        n.setRelatedRecordId(req.getRelatedRecordId());

        notificationRepository.save(n);
        return ResponseEntity.ok(n);
    }

    // ================= MARK AS READ =================
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
        return ResponseEntity.ok().build();
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        notificationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
