package com.example.imageprocessor.model;

import lombok.Data;
import java.util.Map;

@Data
public class ApiRequest {
    private String body;
    private Map<String, String> pathParameters;
    private Map<String, String> queryStringParameters;
    private Map<String, String> headers;
    private String httpMethod;
    
    // Helper methods for easier access
    public String getHeader(String key) {
        return headers != null ? headers.get(key) : null;
    }
    
    public String getPathParameter(String key) {
        return pathParameters != null ? pathParameters.get(key) : null;
    }
    
    public String getQueryParameter(String key) {
        return queryStringParameters != null ? queryStringParameters.get(key) : null;
    }
}