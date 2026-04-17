package com.sistemabarberia.fadex_backend.commons.response;


import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Map;

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

    public static <T> ApiResponse<T> ok(String message, Page<T> page) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(PageResponse.of(page));
        response.setTimestamp(LocalDateTime.now().toString());
        return response;
    }
}