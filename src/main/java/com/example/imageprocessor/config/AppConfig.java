package com.example.imageprocessor.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppConfig {
    private AwsConfig aws;
    private AppImageConfig image;
}
