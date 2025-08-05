package com.ordermanagement.system.services;

import com.ordermanagement.system.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
public class OrderService {

    private final DynamoDbClient dynamoDbClient;
    private final S3Client s3Client;
    private final SnsClient snsClient;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.sns.topicArn}")
    private String topicArn;

    // It’s better to get this from config as well
    @Value("${aws.dynamodb.tableName:Orders}")
    private String tableName;

    public OrderService(DynamoDbClient dynamoDbClient, S3Client s3Client, SnsClient snsClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.s3Client = s3Client;
        this.snsClient = snsClient;
    }

    public String createOrder(Order order, MultipartFile invoiceFile) throws IOException {
        // Upload invoice PDF to S3 and get the public URL
        String invoiceUrl = uploadInvoiceToS3(invoiceFile);

        // Assign generated values
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        order.setOrderDate(Instant.now());
        order.setInvoiceFileUrl(invoiceUrl);

        // Save the order data in DynamoDB
        saveOrderToDynamoDB(order);

        // Publish SNS notification about the new order
        sendNotification(order);

        return orderId;
    }

    private String uploadInvoiceToS3(MultipartFile file) throws IOException {
        String fileName = "invoices/" + UUID.randomUUID() + "-" + Objects.requireNonNull(file.getOriginalFilename());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                
                .contentType(file.getContentType()) // Set content type properly
                .build();

        s3Client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        // Return public URL of uploaded file
        return s3Client.utilities().getUrl(b -> b.bucket(bucketName).key(fileName)).toExternalForm();
    }

    private void saveOrderToDynamoDB(Order order) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("orderId", AttributeValue.fromS(order.getOrderId()));
        item.put("customerName", AttributeValue.fromS(order.getCustomerName()));
        item.put("orderAmount", AttributeValue.fromN(order.getOrderAmount().toString()));
        item.put("orderDate", AttributeValue.fromS(order.getOrderDate().toString()));
        item.put("invoiceFileUrl", AttributeValue.fromS(order.getInvoiceFileUrl()));

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    private void sendNotification(Order order) {
        String message = String.format(
                "New order created:\nOrder ID: %s\nCustomer: %s\nAmount: ₹%.2f",
                order.getOrderId(), order.getCustomerName(), order.getOrderAmount());

        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .subject("New Order Notification")
                .build();

        snsClient.publish(request);
    }

    public Order getOrderById(String id) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("orderId", AttributeValue.fromS(id)))
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();

        if (item == null || item.isEmpty()) {
            return null;
        }

        return mapToOrder(item);
    }

    public List<Order> getAllOrders() {
        ScanRequest request = ScanRequest.builder()
                .tableName(tableName)
                .build();

        ScanResponse response = dynamoDbClient.scan(request);

        List<Order> orders = new ArrayList<>();
        for (Map<String, AttributeValue> item : response.items()) {
            orders.add(mapToOrder(item));
        }

        return orders;
    }

    private Order mapToOrder(Map<String, AttributeValue> item) {
        Order order = new Order();
        order.setOrderId(item.get("orderId").s());
        order.setCustomerName(item.get("customerName").s());
        order.setOrderAmount(Double.parseDouble(item.get("orderAmount").n()));
        order.setOrderDate(Instant.parse(item.get("orderDate").s()));
        order.setInvoiceFileUrl(item.get("invoiceFileUrl").s());
        return order;
    }
}
