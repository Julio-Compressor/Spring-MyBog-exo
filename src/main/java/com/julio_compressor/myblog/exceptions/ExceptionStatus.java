package com.julio_compressor.myblog.exceptions;

public class ExceptionStatus extends RuntimeException {
    private final String status;

    public ExceptionStatus(String message, String status) {
        super(message);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static ExceptionStatus notFound(String message) {
        return new ExceptionStatus(message, "NOT_FOUND");
    }

    public static ExceptionStatus badRequest(String message) {
        return new ExceptionStatus(message, "BAD_REQUEST");
    }

    public static ExceptionStatus conflict(String message) {
        return new ExceptionStatus(message, "CONFLICT");
    }
}
