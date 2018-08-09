package com.tw.microservice.order_service.controller.requests;

import com.tw.microservice.order_service.entity.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class AddOrderRequest {
    List<OrderItem> orderItems;
}
