package com.example.imageprocessor.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqsConfig {
    private String processingQueueUrl;
}