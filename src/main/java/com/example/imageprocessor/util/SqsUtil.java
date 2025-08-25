package com.example.imageprocessor.util;

import com.example.imageprocessor.config.ConfigReader;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SqsUtil {
    private static final SqsClient sqsClient = SqsClient.create();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static void sendProcessMessage(Object message) {
        try {
            String queueUrl = getQueueUrl();
            String messageBody = objectMapper.writeValueAsString(message);
            
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();
            
            sqsClient.sendMessage(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SQS message", e);
        }
    }
    
    public static String getQueueUrl() {
        return ConfigReader.getProcessingQueueUrl();
    }
}