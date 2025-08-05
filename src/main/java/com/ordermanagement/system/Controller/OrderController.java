package com.ordermanagement.system.Controller;

import com.ordermanagement.system.model.Order;
import com.ordermanagement.system.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * POST /orders
     * - Uploads invoice PDF to S3
     * - Stores order data in DynamoDB
     * - Publishes SNS notification
     */
    @PostMapping
    public ResponseEntity<String> createOrder(
            @RequestParam("customerName") String customerName,
            @RequestParam("orderAmount") Double orderAmount,
            @RequestParam("invoiceFile") MultipartFile invoiceFile
    ) {
        try {
            Order order = new Order();
            order.setCustomerName(customerName);
            order.setOrderAmount(orderAmount);

            String orderId = orderService.createOrder(order, invoiceFile);
            return ResponseEntity.ok("Order created successfully. Order ID: " + orderId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }

    /**
     * GET /orders/{id}
     * - Fetch a single order by its ID from DynamoDB
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * GET /orders
     * - List all orders from DynamoDB
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
