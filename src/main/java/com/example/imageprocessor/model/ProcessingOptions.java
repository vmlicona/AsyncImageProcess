package com.example.imageprocessor.model;

import lombok.Data;

@Data
public class ProcessingOptions {
    private Integer maxWidth;
    private Integer maxHeight;
    private Boolean createThumbnail;
    private String format;
}