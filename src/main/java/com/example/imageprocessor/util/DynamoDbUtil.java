package com.example.imageprocessor.util;

import com.example.imageprocessor.config.ConfigReader;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;

public class DynamoDbUtil {
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.create();
    
    public static void updateStatus(String imageId, String status, String errorMessage) {
        String tableName = ConfigReader.getStatusTable();
        
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                        "imageId", AttributeValue.builder().s(imageId).build()
                ))
                .updateExpression("SET #status = :status, #error = :error")
                .expressionAttributeNames(Map.of(
                        "#status", "processingStatus",
                        "#error", "errorMessage"
                ))
                .expressionAttributeValues(Map.of(
                        ":status", AttributeValue.builder().s(status).build(),
                        ":error", AttributeValue.builder().s(errorMessage != null ? errorMessage : "").build()
                ))
                .build();
        
        dynamoDbClient.updateItem(request);
    }
    
    public static String getStatusTable() {
        return ConfigReader.getStatusTable();
    }


    public static void saveInitialStatus(String imageId, String status, String message) {
        String tableName = getStatusTable();

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(Map.of(
                        "imageId", AttributeValue.builder().s(imageId).build(),
                        "processingStatus", AttributeValue.builder().s(status).build(),
                        "errorMessage", AttributeValue.builder().s(message).build(),
                        "createdAt", AttributeValue.builder().s(java.time.Instant.now().toString()).build()
                ))
                .build();

        dynamoDbClient.putItem(request);
    }
}