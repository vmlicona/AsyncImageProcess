package com.example.imageprocessor.config;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@Slf4j
@ToString
public class ConfigReader {
    private static Map<String, Object> configMap;
    private static final String CONFIG_FILE = "/application.yml";
    @Getter
    private static AppConfig config;
    
    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream("application.yml")) {
            if (inputStream == null) {
                throw new RuntimeException("Config file not found: application.yml");
            }

            Yaml yaml = new Yaml();
            configMap = yaml.load(inputStream);
            log.info("Configuration loaded successfully: {}", configMap);

        } catch (Exception e) {
            log.error("Failed to load configuration", e);
            throw new RuntimeException("Configuration loading failed", e);
        }
    }

    // Helper methods to access values easily
    public static String getAwsRegion() {
        return getNestedValue("aws.region");
    }

    public static String getS3OriginalBucket() {
        return getNestedValue("aws.s3.originalBucket");
    }

    public static String getProcessingQueueUrl(){
        return getNestedValue("aws.sqs.processingQueueUrl");
    }

    public static String getS3ProcessedBucket() {
        return getNestedValue("aws.s3.processedBucket");
    }

    public static int getMaxWidth() {
        return getNestedValue("app.image.maxWidth", 1920);
    }
    public static String getStatusTable() {
        return getNestedValue("aws.dynamodb.statusTable");
    }

    public static String getSns() {
        return getNestedValue("aws.sns.notificationTopicArn");
    }

    // Generic method to get nested values from the map
    @SuppressWarnings("unchecked")
    private static <T> T getNestedValue(String keyPath, T defaultValue) {
        try {
            String[] keys = keyPath.split("\\.");
            Map<String, Object> current = configMap;

            for (int i = 0; i < keys.length - 1; i++) {
                current = (Map<String, Object>) current.get(keys[i]);
                if (current == null) return defaultValue;
            }

            T value = (T) current.get(keys[keys.length - 1]);
            return value != null ? value : defaultValue;

        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static <T> T getNestedValue(String keyPath) {
        return getNestedValue(keyPath, null);
    }

}