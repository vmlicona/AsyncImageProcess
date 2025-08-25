package com.example.imageprocessor.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.imageprocessor.model.ApiRequest;
import com.example.imageprocessor.model.ApiResponse;
import com.example.imageprocessor.model.ImageRequest;
import com.example.imageprocessor.model.ProcessMessage;
import com.example.imageprocessor.util.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class ImageUploadHandler implements RequestHandler<ApiRequest, ApiResponse> {

    @Override
    public ApiResponse handleRequest(ApiRequest request, Context context) {
        try {
            // Parse the request body
            ImageRequest imageRequest = JsonUtil.fromJson(request.getBody(), ImageRequest.class);
            log.info("ImageRequest: {}", imageRequest);
            if (imageRequest == null || imageRequest.getImageData() == null) {
                return ApiResponse.error(400, "Invalid request body");
            }

            String imageId = UUID.randomUUID().toString();
            String originalKey = "original/" + imageId + "/" + imageRequest.getFileName();
            log.info("OriginalKey: {}", originalKey);
            // Upload original image to S3
            byte[] imageData = java.util.Base64.getDecoder().decode(imageRequest.getImageData());

            S3Util.uploadOriginalImage(originalKey, imageData, imageRequest.getContentType());

            // Save initial status
            DynamoDbUtil.saveInitialStatus(imageId, "PENDING", "Uploaded and queued for processing");

            // Send message to SQS for processing
            ProcessMessage message = new ProcessMessage();
            message.setImageId(imageId);
            message.setOriginalKey(originalKey);
            message.setProcessedKey("processed/" + imageId + "/" + imageRequest.getFileName());
            message.setOptions(imageRequest.getOptions());

            SqsUtil.sendProcessMessage(message);

            SnsUtil.sendProcessingNotification(
                    imageId,
                    "QUEUED",
                    "Image uploaded and queued for processing"
            );

            log.info("Image uploaded and queued for processing: {}", imageId);

            // Return success response with image ID
            String responseBody = "{\"imageId\": \"" + imageId + "\", \"status\": \"PENDING\"}";
            return ApiResponse.success(responseBody);

        } catch (Exception e) {
            log.error("Image upload failed", e);
            return ApiResponse.error(500, "Upload failed: " + e.getMessage());
        }
    }
}