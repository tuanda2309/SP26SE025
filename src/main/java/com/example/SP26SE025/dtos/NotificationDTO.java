package com.example.SP26SE025.dtos;

import java.time.LocalDateTime;

public class NotificationDTO {
    private String title;
    private String message;
    private String type; // SUCCESS, WARNING, INFO
    private LocalDateTime time;
    private boolean read;

    public NotificationDTO(String title, String message, String type, LocalDateTime time, boolean read) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.time = time;
        this.read = read;
    }

    // Getters (Bắt buộc để Thymeleaf hiển thị)
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public LocalDateTime getTime() { return time; }
    public boolean isRead() { return read; }
}