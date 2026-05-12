package com.hector.orders.service;

import com.hector.orders.model.entity.Order;
import com.hector.orders.model.entity.OrderItem;
import com.hector.orders.model.entity.Product;
import com.hector.orders.model.entity.dto.OrderDTO;
import com.hector.orders.model.entity.dto.OrderItemsDTO;
import com.hector.orders.model.entity.enums.OrderStatus;
import com.hector.orders.repository.OrderItemRepository;
import com.hector.orders.repository.OrderRepository;
import com.hector.orders.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ProductRepository productRepository;

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

    @Test
    void deberiaLanzarErrorSiOrdenNoExiste() {

        // Arrange
        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.confirmarOrder(1L)
        );

        assertEquals("Orden no encontrada", exception.getMessage());
    }

    @Test
    void deberiaLanzarErrorSiStockEsInsuficiente() {

        // Arrange
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        order.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(1L);
        product.setStock(10);

        OrderItemsDTO orderItem = new OrderItemsDTO();
        orderItem.setProductoId(1L);
        orderItem.setCantidad(15);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.createOrderItem(1L, orderItem)
        );

        // Assert
        assertEquals(
                "Stock insuficiente para el producto",
                exception.getMessage()
        );
    }
}