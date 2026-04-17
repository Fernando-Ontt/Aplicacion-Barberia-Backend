package com.sistemabarberia.fadex_backend.commons.exception;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja tus excepciones de negocio
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(ex.getMessage());
        response.setData(null);
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    // Maneja cualquier otro error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage("Error interno del servidor");
        response.setData(null);
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(500).body(response);
    }
}