package com.tw.microservice.order_service.controller.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ResponseOrder {
    private Long id;
    private Date createDate;
    private double totalPrice;
    List<ResponseOrderItem> responseOrderItems = new ArrayList<>();
    private Long userId;

}
