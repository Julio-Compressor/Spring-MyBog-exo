package com.julio_compressor.myblog.exceptions;

public class ExeptionStatus extends RuntimeException {
    private String status;
    public ExeptionStatus(String message, String status) {
        super(message);
        this.status=status;
    }
    public String getStatus() {
        return status;
    }
}
