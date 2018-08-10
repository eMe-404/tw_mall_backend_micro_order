package com.tw.microservice.order_service.service;

import com.tw.microservice.order_service.controller.VO.Product;
import com.tw.microservice.order_service.controller.requests.AddOrderRequest;
import com.tw.microservice.order_service.entity.Order;
import com.tw.microservice.order_service.entity.OrderItem;
import com.tw.microservice.order_service.repository.OrderItemRepository;
import com.tw.microservice.order_service.repository.OrderRepository;
import com.tw.microservice.order_service.restService.ProductClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ProductClient productClient;

    private OrderService orderService;


    @Before
    public void setUp() {
        orderService = new OrderService(orderRepository, orderItemRepository, productClient);
    }

    @Test
    public void should_return_order_when_call_addOrder() {
        //given
        Long userId = 1L;
        long orderId = 1L;
        long orderItemId = 2L;
        Product product = new Product();


        OrderItem orderItem = OrderItem
                .builder()
                .id(orderItemId)
                .orderId(orderId)
                .productId(1L)
                .count(3)
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        AddOrderRequest addOrderRequest = new AddOrderRequest();
        addOrderRequest.setOrderItems(orderItems);
        given(productClient.getProductById(orderItem.getProductId())).willReturn(product);
        //when
        orderService.addOrder(addOrderRequest, userId);
        //then
        verify(orderRepository, times(2)).save(any());
        verify(orderItemRepository, times(orderItems.size())).save(any());

    }

    @Test
    public void should_return_all_orders_when_call_getAll() {
        //given
        Long userId = 1L;
        long orderId = 1L;
        long orderItemId = 2L;

        Product product = new Product();

        OrderItem orderItem = OrderItem
                .builder()
                .id(orderItemId)
                .orderId(orderId)
                .productId(1L)
                .count(3)
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        Order order = Order
                .builder()
                .createDate(new Date())
                .totalPrice(100)
                .orderItems(orderItems)
                .userId(userId)
                .id(orderId)
                .build();
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        given(orderRepository.findByUserId(userId)).willReturn(orders);
        given(productClient.getProductById(orderItem.getProductId())).willReturn(product);

        //when
        orderService.getAllOrderByUser(userId);

        //then
        verify(orderRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void should_add_order_item_when_call_addOrderItem() {
        //given
        Long userId = 1L;
        long orderId = 1L;
        long orderItemId = 2L;

        OrderItem addedorderItem = OrderItem
                .builder()
                .id(orderItemId + 1)
                .orderId(orderId)
                .productId(1L)
                .count(3)
                .build();

        OrderItem orderItem = OrderItem
                .builder()
                .id(orderItemId)
                .orderId(orderId)
                .productId(1L)
                .count(3)
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        List<Order> orders = new ArrayList<>();
        Order order = Order
                .builder()
                .createDate(new Date())
                .totalPrice(100)
                .orderItems(orderItems)
                .userId(userId)
                .id(orderId)
                .build();
        orders.add(order);
        given(orderRepository.findByUserId(userId)).willReturn(orders);
        given(orderItemRepository.save(addedorderItem)).willReturn(addedorderItem);
        //when
        OrderItem returnOrderItem = orderService.addOrderItem(userId, orderId, addedorderItem);
        //then
        assertThat(returnOrderItem).isEqualTo(addedorderItem);
        verify(orderItemRepository, times(1)).save(any());
    }

    @Test
    public void should_update_order_item_when_call_updateOrderItem() {
        //given
        Long userId = 1L;
        long orderId = 1L;
        long orderItemId = 2L;

        OrderItem updatedorderItem = OrderItem
                .builder()
                .id(orderItemId)
                .orderId(orderId)
                .productId(1L)
                .count(5)
                .build();

        OrderItem orderItem = OrderItem
                .builder()
                .id(orderItemId)
                .orderId(orderId)
                .productId(1L)
                .count(3)
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        List<Order> orders = new ArrayList<>();
        Order order = Order
                .builder()
                .createDate(new Date())
                .totalPrice(100)
                .orderItems(orderItems)
                .userId(userId)
                .id(orderId)
                .build();
        orders.add(order);
        given(orderRepository.findByUserId(userId)).willReturn(orders);
        given(orderItemRepository.save(updatedorderItem)).willReturn(updatedorderItem);
        //when
        OrderItem returnOrderItem = orderService.updateOrderItem(userId, orderId, orderItemId, updatedorderItem);
        //then
        assertThat(returnOrderItem).isEqualTo(updatedorderItem);
        verify(orderItemRepository, times(1)).save(any());
    }

    @Test
    public void should_remove_orderItem_when_call_removeOrderItem() {
        //given
        Long userId = 1L;
        long orderId = 1L;
        long orderItemId = 2L;

        OrderItem orderItem = OrderItem
                .builder()
                .id(orderItemId)
                .orderId(orderId)
                .productId(1L)
                .count(3)
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        List<Order> orders = new ArrayList<>();
        Order order = Order
                .builder()
                .createDate(new Date())
                .totalPrice(100)
                .orderItems(orderItems)
                .userId(userId)
                .id(orderId)
                .build();
        orders.add(order);

        given(orderRepository.findByUserId(userId)).willReturn(orders);
        //when
        orderService.removeByOrderItemId(userId, orderId, orderItemId);
        //then
        verify(orderItemRepository, times(1)).deleteById(orderItemId);
    }


}