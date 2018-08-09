//package com.tw.microservice.order_service.service;
//
//import com.tw.microservice.order_service.entity.OrderItem;
//import com.tw.microservice.order_service.exeption.OrderItemNotFoundException;
//import com.tw.microservice.order_service.repository.OrderItemRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OrderItemService {
//
//
//    private OrderItemRepository orderItemRepository;
//
//    @Autowired
//    public OrderItemService(OrderItemRepository orderItemRepository) {
//
//        this.orderItemRepository = orderItemRepository;
//    }
//
//    public OrderItem add(OrderItem orderItem) {
//        return orderItemRepository.save(orderItem);
//    }
//
//    public OrderItem update(Integer id, OrderItem orderItem) throws OrderItemNotFoundException {
//        orderItemRepository.findById(id).orElseThrow(OrderItemNotFoundException::new);
//        orderItem.setId(id);
//        return orderItemRepository.save(orderItem);
//    }
//
//    public void remove(int id) throws OrderItemNotFoundException {
//        orderItemRepository.findById(id).orElseThrow(OrderItemNotFoundException::new);
//        orderItemRepository.deleteById(id);
//
//    }
//}
