package com.example.SP26SE025.exception;

/**
 * Ngoại lệ được năng khi AI-Service không sẵn sàng, hết thời gian hoặc trả về
 * lỗi
 */
public class AiServiceException extends RuntimeException {

    private String aiServiceUrl;
    private Integer httpStatusCode;

    public AiServiceException(String message) {
        super(message);
    }

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiServiceException(String message, String aiServiceUrl, Integer httpStatusCode) {
        super(message);
        this.aiServiceUrl = aiServiceUrl;
        this.httpStatusCode = httpStatusCode;
    }

    public AiServiceException(String message, String aiServiceUrl, Integer httpStatusCode, Throwable cause) {
        super(message, cause);
        this.aiServiceUrl = aiServiceUrl;
        this.httpStatusCode = httpStatusCode;
    }

    public String getAiServiceUrl() {
        return aiServiceUrl;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }
}
