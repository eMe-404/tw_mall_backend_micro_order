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
import com.tw.microservice.order_service.restService.ProductClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private final ProductClient productClient;


    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductClient productClient) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productClient = productClient;
    }


    //创建一条订单
    public Order addOrderByUser(AddOrderRequest addOrderRequest, Long userId) {
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

        OrderItem selectedOrderItem = selectedOrder.getOrderItems().stream()
                .filter(orderItem -> orderItem.getId().equals(deleteOrderItemId))
                .findAny()
                .orElseThrow(OrderItemNotFoundException::new);
        selectedOrder.remove(selectedOrderItem);
        orderItemRepository.deleteById(deleteOrderItemId);
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
        Product receivedProduct = productClient.getProductById(orderItem.getProductId());
        responseOrderItem.setProduct(receivedProduct);
        return responseOrderItem;
    }

    private double getTotalPrice(double totalPrice, OrderItem orderItem) {
        Product receivedProduct = productClient.getProductById(orderItem.getProductId());
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

    public void removeOrder(Long userId, Long orderId) {
        List<Order> selectedOrders = orderRepository.findByUserId(userId);
        selectedOrders.stream()
                .filter(order -> order.getId().equals(orderId))
                .findAny()
                .orElseThrow(OrderNotFoundException::new);
        orderRepository.deleteById(orderId);

    }
}
