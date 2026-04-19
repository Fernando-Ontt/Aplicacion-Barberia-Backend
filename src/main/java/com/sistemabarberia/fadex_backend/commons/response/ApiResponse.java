package com.sistemabarberia.fadex_backend.commons.response;


import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  ApiResponse<T> {

    private boolean success;
    private String message;
    private Object data;
    private String timestamp;

    // Para paginación
    public static <T> ApiResponse<T> ok(String message, Page<T> page) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(PageResponse.of(page));
        response.setTimestamp(LocalDateTime.now().toString());
        return response;
    }

    // Para objetos simples (tu caso actual)
    public static <T> ApiResponse<T> ok(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now().toString());
        return response;
    }

    public static ApiResponse<Void> ok(String message) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(null);
        response.setTimestamp(LocalDateTime.now().toString());
        return response;
    }
}