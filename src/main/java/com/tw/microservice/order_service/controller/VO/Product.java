package com.tw.microservice.order_service.controller.VO;

import lombok.Data;

@Data
public class Product {
    private int id;
    private String name;
    private int price;
    private String unit;
}
