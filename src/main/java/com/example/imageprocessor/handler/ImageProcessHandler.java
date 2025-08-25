package com.example.imageprocessor.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.example.imageprocessor.model.ProcessMessage;
import com.example.imageprocessor.util.DynamoDbUtil;
import com.example.imageprocessor.util.S3Util;
import com.example.imageprocessor.util.ImageUtil;
import com.example.imageprocessor.util.SnsUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageProcessHandler implements RequestHandler<SQSEvent, Void> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                ProcessMessage processMessage = objectMapper.readValue(
                        message.getBody(), ProcessMessage.class);

                log.info("Processing image: {}", processMessage.getImageId());
                DynamoDbUtil.updateStatus(processMessage.getImageId(), "PROCESSING", "Image processing started");

                // 1. Download original image from S3
                byte[] originalImage = S3Util.downloadOriginalImage(processMessage.getOriginalKey());
                log.info("Downloaded original image: {} bytes", originalImage.length);

                // 2. Process the image
                byte[] processedImage = processImage(originalImage, processMessage);
                log.info("Processed image: {} bytes", processedImage.length);

                // 3. Upload processed image to S3
                S3Util.uploadProcessedImage(
                        processMessage.getProcessedKey(),
                        processedImage,
                        "image/png"
                );

                DynamoDbUtil.updateStatus(processMessage.getImageId(), "COMPLETED", "Image processing completed successfully");
                log.info("Image processing completed: {}", processMessage.getImageId());

                SnsUtil.sendSuccessNotification(
                        processMessage.getImageId(),
                        "s3://my-app-processed-images/" + processMessage.getProcessedKey()
                );

            } catch (Exception e) {
                log.error("Failed to process message: {}", message.getBody(), e);
                SnsUtil.sendFailureNotification(getImageIdFromMessage(message.getBody()), e.getMessage());
                DynamoDbUtil.updateStatus(getImageIdFromMessage(message.getBody()), "FAILED", "Processing failed: " + e.getMessage());
            }
        }
        return null;
    }

    private byte[] processImage(byte[] originalImage, ProcessMessage message) {
        return ImageUtil.resizeImage(
                originalImage,
                message.getOptions().getMaxWidth(),
                message.getOptions().getMaxHeight(),
                "image/png"
        );
    }

    private String getImageIdFromMessage(String messageBody) {
        try {
            ProcessMessage msg = objectMapper.readValue(messageBody, ProcessMessage.class);
            return msg.getImageId();
        } catch (Exception e) {
            return "unknown";
        }
    }
}