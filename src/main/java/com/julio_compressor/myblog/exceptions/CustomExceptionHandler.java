package com.julio_compressor.myblog.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ExeptionStatus.class)
    public ResponseEntity<Map<String, String>> handlerException(ExeptionStatus ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", ex.getStatus());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatus()));
    }
}