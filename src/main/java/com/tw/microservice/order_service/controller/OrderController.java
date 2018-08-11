package com.tw.microservice.order_service.controller;

import com.tw.microservice.order_service.controller.requests.AddOrderRequest;
import com.tw.microservice.order_service.controller.response.ResponseOrder;
import com.tw.microservice.order_service.entity.Order;
import com.tw.microservice.order_service.entity.OrderItem;
import com.tw.microservice.order_service.exeption.OrderItemNotFoundException;
import com.tw.microservice.order_service.exeption.OrderNotFoundException;
import com.tw.microservice.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    private ResponseEntity addOrder(@RequestBody AddOrderRequest addOrderRequest,
                                    @RequestHeader(name = "userId") Long userId) {
        Order addedOrder = orderService.addOrderByUser(addOrderRequest, userId);
        String URIstr = "/orders/" + addedOrder.getId();
        return ResponseEntity.created(URI.create(URIstr)).build();
    }

    @GetMapping
    private ResponseEntity<List<ResponseOrder>> getAllOrderByUser(@RequestHeader(name = "userId") Long userId) {
        List<ResponseOrder> getOrderResponse = orderService.getAllOrderByUser(userId);
        return ResponseEntity.ok(getOrderResponse);
    }

    @DeleteMapping("/{orderId}")
    private ResponseEntity removeOrder(@RequestHeader Long userId,
                                       @PathVariable Long orderId) {
        orderService.removeOrder(userId, orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/orderItems")
    private ResponseEntity addOrderItem(@RequestHeader long userId,
                                        @PathVariable long orderId,
                                        @RequestBody OrderItem addedOrderItem) {
        OrderItem orderItem = orderService.addOrderItem(userId, orderId, addedOrderItem);
        return ResponseEntity.created(URI.create(String.format("/orders/%s/orderItems/%s", orderId, orderItem.getId()))).build();
    }

    @PutMapping("/{orderId}/orderItems/{orderItemId}")
    private ResponseEntity updateOrderItem(@RequestHeader long userId,
                                           @PathVariable long orderId,
                                           @PathVariable long orderItemId,
                                           @RequestBody OrderItem updatedOrderItem) {
        OrderItem orderItem = orderService.updateOrderItem(userId, orderId, orderItemId, updatedOrderItem);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}/orderItems/{orderItemId}")
    public ResponseEntity deleteOrderItem(@RequestHeader long userId,
                                          @PathVariable long orderId,
                                          @PathVariable long orderItemId) {
        orderService.removeByOrderItemId(userId, orderId, orderItemId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void OrderNotFoundHandle(OrderNotFoundException ex) {

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void OrderItemNotFoundHandle(OrderItemNotFoundException ex) {

    }

}
