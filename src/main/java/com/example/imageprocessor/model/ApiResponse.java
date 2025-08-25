package com.example.imageprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private int statusCode;
    private String body;
    private Map<String, String> headers;
    private boolean isBase64Encoded;
    
    // Default values for convenience
    public static class ApiResponseBuilder {
        private boolean isBase64Encoded = false;
    }
    
    // Helper method for creating success responses
    public static ApiResponse success(String body) {
        return ApiResponse.builder()
                .statusCode(200)
                .body(body)
                .headers(Map.of("Content-Type", "application/json"))
                .build();
    }
    
    // Helper method for creating error responses
    public static ApiResponse error(int statusCode, String message) {
        return ApiResponse.builder()
                .statusCode(statusCode)
                .body("{\"error\": \"" + message + "\"}")
                .headers(Map.of("Content-Type", "application/json"))
                .build();
    }
}