#!/bin/bash

# Build project
mvn clean package

aws lambda create-function \
    --function-name ImageUploadHandler \
    --runtime java11 \
    --role arn:aws:iam::492149691622:role/lambda-execution-role \
    --handler com.example.imageprocessor.handler.ImageUploadHandler::handleRequest \
    --zip-file fileb://target/image-processor-1.0-SNAPSHOT.jar \
    --region us-east-2 \
    --memory-size 512 \
    --timeout 30

aws lambda create-function \
    --function-name ImageProcessHandler \
    --runtime java11 \
    --role arn:aws:iam::492149691622:role/lambda-execution-role \
    --handler com.example.imageprocessor.handler.ImageProcessHandler::handleRequest \
    --zip-file fileb://target/image-processor-1.0-SNAPSHOT.jar \
    --region us-east-2 \
    --memory-size 1024 \
    --timeout 300

# Create StatusCheckHandler function (if you have one)
aws lambda create-function \
    --function-name StatusCheckHandler \
    --runtime java11 \
    --role arn:aws:iam::492149691622:role/lambda-execution-role \
    --handler com.example.imageprocessor.handler.StatusCheckHandler::handleRequest \
    --zip-file fileb://target/image-processor-1.0-SNAPSHOT.jar \
    --region us-east-2 \
    --memory-size 512 \
    --timeout 30

# Deploy Lambda functions
aws lambda update-function-code \
    --function-name ImageUploadHandler \
    --zip-file fileb://target/image-processor-1.0-SNAPSHOT.jar

aws lambda update-function-code \
    --function-name ImageProcessHandler \
    --zip-file fileb://target/image-processor-1.0-SNAPSHOT.jar

aws lambda update-function-code \
    --function-name StatusCheckHandler \
    --zip-file fileb://target/image-processor-1.0-SNAPSHOT.jar

# Add SQS trigger to Lambda
aws lambda create-event-source-mapping \
  --function-name ImageProcessHandler \
  --event-source-arn arn:aws:sqs:us-east-2:492149691622:image-processing-queue \
  --batch-size 10 \
  --maximum-batching-window-in-seconds 30 \
  --region us-east-2

# Delete and recreate functions
aws lambda delete-function --function-name ImageUploadHandler --region us-east-2
aws lambda delete-function --function-name ImageProcessHandler --region us-east-2
aws lambda delete-function --function-name StatusCheckHandler --region us-east-2

# Check if function exists
aws lambda get-function --function-name ImageUploadHandler --region us-east-2
aws lambda get-function --function-name ImageProcessHandler --region us-east-2
aws lambda get-function --function-name StatusCheckHandler --region us-east-2

# List all functions to see what's available
aws lambda list-functions --region us-east-2

Create API Gateway Endpoints
POST /upload - Upload image, returns immediate response with job ID

GET /status/{imageId} - Check processing status

GET /download/{imageId} - Download processed image