# Create S3 buckets
aws s3 mb s3://your-app-original-images
aws s3 mb s3://your-app-processed-images

# Create SQS queue
aws sqs create-queue --queue-name image-processing-queue --region us-east-2

# Create DynamoDB table for status tracking
aws dynamodb create-table \
    --table-name ImageProcessingStatus \
    --attribute-definitions \
        AttributeName=imageId,AttributeType=S \
    --key-schema \
        AttributeName=imageId,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST

aws sns create-topic --name image-processing-notifications --region us-east-2

aws sns subscribe \
  --topic-arn arn:aws:sns:us-east-2:492149691622:image-processing-notifications \
  --protocol email \
  --notification-endpoint vmlicona@gmail.com \
  --region us-east-2