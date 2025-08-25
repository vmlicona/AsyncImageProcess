package com.example.imageprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingStatus {
    private String imageId;
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private String originalUrl;
    private String processedUrl;
    private String errorMessage;
    private String createdAt;
    private String updatedAt;
}