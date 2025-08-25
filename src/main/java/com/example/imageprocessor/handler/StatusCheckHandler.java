package com.example.imageprocessor.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.imageprocessor.model.ApiRequest;
import com.example.imageprocessor.model.ApiResponse;
import com.example.imageprocessor.model.ProcessingStatus;
import com.example.imageprocessor.util.DynamoDbUtil;
import com.example.imageprocessor.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Map;

@Slf4j
public class StatusCheckHandler implements RequestHandler<ApiRequest, ApiResponse> {

    private static final DynamoDbClient dynamoDb = DynamoDbClient.create();

    @Override
    public ApiResponse handleRequest(ApiRequest request, Context context) {
        try {
            // Extract imageId from path parameters
            String imageId = request.getPathParameter("imageId");

            if (imageId == null || imageId.isEmpty()) {
                return ApiResponse.error(400, "Image ID is required in path parameters");
            }

            log.info("Checking status for image: {}", imageId);

            // Get status from DynamoDB
            ProcessingStatus status = getProcessingStatus(imageId);

            if (status == null) {
                return ApiResponse.error(404, "Image not found: " + imageId);
            }

            return ApiResponse.success(JsonUtil.toJson(status));

        } catch (Exception e) {
            log.error("Error checking status", e);
            return ApiResponse.error(500, "Failed to check status: " + e.getMessage());
        }
    }

    private ProcessingStatus getProcessingStatus(String imageId) {
        String tableName = DynamoDbUtil.getStatusTable();

        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                        "imageId", AttributeValue.builder().s(imageId).build()
                ))
                .build();

        GetItemResponse response = dynamoDb.getItem(request);

        if (!response.hasItem()) {
            return null;
        }

        Map<String, AttributeValue> item = response.item();
        return mapToProcessingStatus(item);
    }

    private ProcessingStatus mapToProcessingStatus(Map<String, AttributeValue> item) {
        ProcessingStatus status = new ProcessingStatus();

        status.setImageId(getStringValue(item, "imageId"));
        status.setStatus(getStringValue(item, "processingStatus"));
        status.setErrorMessage(getStringValue(item, "errorMessage"));
        status.setOriginalUrl(getStringValue(item, "originalUrl"));
        status.setProcessedUrl(getStringValue(item, "processedUrl"));
        status.setCreatedAt(getStringValue(item, "createdAt"));
        status.setUpdatedAt(getStringValue(item, "updatedAt"));

        return status;
    }

    private String getStringValue(Map<String, AttributeValue> item, String key) {
        return item.containsKey(key) && item.get(key).s() != null ?
                item.get(key).s() : "";
    }
}