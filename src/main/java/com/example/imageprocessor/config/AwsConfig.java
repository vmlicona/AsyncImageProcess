package com.example.imageprocessor.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwsConfig {
    private String region;
    private S3Config s3;
    private SqsConfig sqs;
    private DynamoDBConfig dynamodb;
}