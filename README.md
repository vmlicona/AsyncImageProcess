# AsyncImageProcess

User Upload → API Gateway → Lambda → S3 (original) → SQS →
Processing Lambda → S3 (processed) → DynamoDB (update status) →
SNS (notification - optional)
