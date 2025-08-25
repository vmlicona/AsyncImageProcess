package com.example.imageprocessor.util;

import com.example.imageprocessor.config.ConfigReader;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
public class S3Util {
    private static final S3Client s3Client = S3Client.create();
    
    public static void uploadOriginalImage(String key, byte[] imageData, String contentType) {
        String bucket = ConfigReader.getS3OriginalBucket(); // Use the helper method!
        log.info("bucket: {}", bucket);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(imageData));
    }
    
    public static void uploadProcessedImage(String key, byte[] imageData, String contentType) {
        String bucket = ConfigReader.getS3ProcessedBucket();
        
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
        
        s3Client.putObject(request, RequestBody.fromBytes(imageData));
    }

    public static byte[] downloadOriginalImage(String key) {
        try {
            String bucket = ConfigReader.getS3OriginalBucket();
            log.info("Downloading from bucket: {}, key: {}", bucket, key);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            byte[] imageData = objectBytes.asByteArray();

            log.info("Successfully downloaded image: {} bytes", imageData.length);
            return imageData;

        } catch (S3Exception e) {
            log.error("Failed to download image from S3: {}", e.getMessage());
            throw new RuntimeException("S3 download failed", e);
        }
    }

    public static String getOriginalBucket() {
        return ConfigReader.getConfig().getAws().getS3().getOriginalBucket();
    }
    
    public static String getProcessedBucket() {
        return ConfigReader.getConfig().getAws().getS3().getProcessedBucket();
    }
}