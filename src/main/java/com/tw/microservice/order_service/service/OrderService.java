package com.tw.microservice.order_service.service;

import com.tw.microservice.order_service.controller.VO.Product;
import com.tw.microservice.order_service.controller.requests.AddOrderRequest;
import com.tw.microservice.order_service.controller.response.ResponseOrder;
import com.tw.microservice.order_service.controller.response.ResponseOrderItem;
import com.tw.microservice.order_service.entity.Order;
import com.tw.microservice.order_service.entity.OrderItem;
import com.tw.microservice.order_service.exeption.OrderItemNotFoundException;
import com.tw.microservice.order_service.exeption.OrderNotFoundException;
import com.tw.microservice.order_service.repository.OrderItemRepository;
import com.tw.microservice.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;


    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }


    //创建一条订单
    public Order addOrder(AddOrderRequest addOrderRequest, Long userId) {
        Order toAddedOrder = new Order();
        double totalPrice = 0;
        toAddedOrder.setCreateDate(new Date());
        toAddedOrder.setUserId(userId);
        orderRepository.save(toAddedOrder);
        for (OrderItem orderItem : addOrderRequest.getOrderItems()) {
            totalPrice = getTotalPrice(totalPrice, orderItem);
            orderItem.setOrderId(toAddedOrder.getId());
            orderItemRepository.save(orderItem);
        }
        toAddedOrder.setTotalPrice(totalPrice);
        return orderRepository.save(toAddedOrder);
    }

    //获取用户所有订单
    public List<ResponseOrder> getAllOrderByUser(Long userId) {
        return orderRepository
                .findByUserId(userId)
                .stream()
                .map(this::mapOrderToResponseOrder)
                .collect(Collectors.toList());
    }

    //添加一条订单项
    public OrderItem addOrderItem(long userId, long orderId, OrderItem addedOrderItem) {
        Order selectedOrder = findOrderInUserOrderList(userId, orderId);
        for (OrderItem orderItem : selectedOrder.getOrderItems()) {
            if (orderItem.getId().equals(addedOrderItem.getId())) {
                orderItem.setCount(orderItem.getCount() + addedOrderItem.getCount());
                return orderItemRepository.save(orderItem);
            }
        }
        return orderItemRepository.save(addedOrderItem);
    }

    //修改一条订单项
    public OrderItem updateOrderItem(long userId, long orderId, long orderItemId, OrderItem updatedOrderItem) throws OrderItemNotFoundException {
        Order selectedOrder = findOrderInUserOrderList(userId, orderId);
        for (OrderItem orderItem : selectedOrder.getOrderItems()) {
            if (orderItem.getId().equals(orderItemId)) {
                orderItem.setCount(updatedOrderItem.getCount());
                orderItem.setProductId(updatedOrderItem.getProductId());
                return orderItemRepository.save(orderItem);
            }
        }
        throw new OrderItemNotFoundException();
    }

    //删除一条订单项
    public void removeByOrderItemId(Long userId, long orderId, long deleteOrderItemId) {
        Order selectedOrder = findOrderInUserOrderList(userId, orderId);
        for (OrderItem orderItem : selectedOrder.getOrderItems()) {
            if (orderItem.getId().equals(deleteOrderItemId)) {
                orderItemRepository.deleteById(deleteOrderItemId);
                return;
            }
        }
        throw new OrderItemNotFoundException();
    }

    private ResponseOrder mapOrderToResponseOrder(Order order) {
        ResponseOrder responseOrder = new ResponseOrder();
        responseOrder.setCreateDate(order.getCreateDate());
        responseOrder.setId(order.getId());
        responseOrder.setTotalPrice(order.getTotalPrice());
        responseOrder.setUserId(order.getUserId());
        List<ResponseOrderItem> responseOrderItems = order
                .getOrderItems()
                .stream()
                .map(this::mapOrderItemToResponseOrderItem)
                .collect(Collectors.toList());
        responseOrder.setResponseOrderItems(responseOrderItems);
        return responseOrder;
    }

    private ResponseOrderItem mapOrderItemToResponseOrderItem(OrderItem orderItem) {
        ResponseOrderItem responseOrderItem = new ResponseOrderItem();
        responseOrderItem.setCount(orderItem.getCount());
        Product receivedProduct = getProductInfo(orderItem);
        responseOrderItem.setProduct(receivedProduct);
        return responseOrderItem;
    }

    private Product getProductInfo(OrderItem orderItem) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(String.format("http://localhost:8083/products/%s",orderItem.getProductId()), Product.class);
    }

    private double getTotalPrice(double totalPrice, OrderItem orderItem) {
        Product receivedProduct = getProductInfo(orderItem);
        assert receivedProduct != null;
        totalPrice += orderItem.getCount() * receivedProduct.getPrice();
        return totalPrice;
    }

    private Order findOrderInUserOrderList(long userId, long orderId) throws OrderNotFoundException {
        List<Order> orderListByUserId = orderRepository.findByUserId(userId);
        return orderListByUserId.stream()
                .filter(order -> order.getId() == orderId)
                .findAny()
                .orElseThrow(OrderNotFoundException::new);
    }

}
