package com.tw.microservice.order_service.controller.response;

import com.tw.microservice.order_service.controller.VO.Product;
import lombok.Data;

@Data
public class ResponseOrderItem {
    private Product product;
    private int count;
}
