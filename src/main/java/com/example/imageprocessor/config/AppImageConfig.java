package com.example.imageprocessor.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppImageConfig {
    private int maxWidth;
    private int maxHeight;
    private String defaultFormat;
    private int quality;
}