package com.example.imageprocessor.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3Config {
    private String originalBucket;
    private String processedBucket;
}