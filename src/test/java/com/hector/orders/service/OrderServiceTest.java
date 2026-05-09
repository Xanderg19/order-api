package com.hector.orders.service;

import com.hector.orders.model.entity.Order;
import com.hector.orders.model.entity.dto.OrderDTO;
import com.hector.orders.model.entity.enums.OrderStatus;
import com.hector.orders.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void deberiaCrearOrdenCorrectamente() {

        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotal(BigDecimal.ZERO);
        order.setItems(new ArrayList<>());

        when(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class)))
                .thenReturn(order);

        // Act
        OrderDTO resultado = orderService.createOrder();

        // Assert
        assertEquals(OrderStatus.CREATED, resultado.getStatus());
    }
}