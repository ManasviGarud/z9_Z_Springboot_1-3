package com.ordermanagement.system.model;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private String orderId;            
    private String customerName;       
    private Double orderAmount;        
    private Instant orderDate;         
    private String invoiceFileUrl;
    
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Double getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}
	public Instant getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Instant orderDate) {
		this.orderDate = orderDate;
	}
	public String getInvoiceFileUrl() {
		return invoiceFileUrl;
	}
	public void setInvoiceFileUrl(String invoiceFileUrl) {
		this.invoiceFileUrl = invoiceFileUrl;
	}     

    
}