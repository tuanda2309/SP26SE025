package com.example.SP26SE025.exception;

/**
 * Ngoại lệ được năng khi hình ảnh tải lên không hợp lệ (định dạng, kích thước,
 * hư hạng)
 */
public class InvalidImageException extends RuntimeException {

    private String reason; // vờ dụ: "INVALID_FORMAT", "FILE_TOO_LARGE", "CORRUPTED"

    public InvalidImageException(String message) {
        super(message);
    }

    public InvalidImageException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public InvalidImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidImageException(String message, String reason, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
