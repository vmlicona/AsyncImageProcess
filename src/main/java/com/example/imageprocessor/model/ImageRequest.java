package com.example.imageprocessor.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ImageRequest {
    private String imageData; // Base64 encoded image
    private String fileName;
    private String contentType;
    private ProcessingOptions options;
}