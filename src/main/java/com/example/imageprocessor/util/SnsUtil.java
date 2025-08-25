package com.example.imageprocessor.util;

import com.example.imageprocessor.config.ConfigReader;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnsUtil {
    private static final SnsClient snsClient = SnsClient.create();
    
    public static void sendProcessingNotification(String imageId, String status, String message) {
        try {
            String topicArn = ConfigReader.getSns();
            
            String notificationMessage = String.format(
                "Image Processing Update\n\nImage ID: %s\nStatus: %s\nMessage: %s\nTimestamp: %s",
                imageId, status, message, java.time.Instant.now().toString()
            );
            
            PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(notificationMessage)
                .subject("Image Processing " + status)
                .build();
            
            snsClient.publish(request);
            log.info("SNS notification sent for image: {}", imageId);
            
        } catch (Exception e) {
            log.error("Failed to send SNS notification for image: {}", imageId, e);
        }
    }
    
    public static void sendSuccessNotification(String imageId, String processedUrl) {
        String message = String.format("Processing completed successfully. Download URL: %s", processedUrl);
        sendProcessingNotification(imageId, "COMPLETED", message);
    }
    
    public static void sendFailureNotification(String imageId, String error) {
        String message = String.format("Processing failed. Error: %s", error);
        sendProcessingNotification(imageId, "FAILED", message);
    }
}