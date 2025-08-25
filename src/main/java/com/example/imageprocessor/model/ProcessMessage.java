package com.example.imageprocessor.model;

import lombok.Data;

@Data
public class ProcessMessage {
    private String imageId;
    private String originalKey;
    private String processedKey;
    private ProcessingOptions options;
}