package com.hector.orders.controller;

import com.hector.orders.model.entity.Order;
import com.hector.orders.model.entity.OrderItem;
import com.hector.orders.model.entity.dto.OrderDTO;
import com.hector.orders.model.entity.dto.OrderItemsDTO;
import com.hector.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public OrderDTO createOrder() {
        return orderService.createOrder();
    }

    @PostMapping("{orderId}/items")
    public Order createOrderItem(@PathVariable Long orderId, @Valid @RequestBody OrderItemsDTO orderItem) {
        System.out.println("test");
        return orderService.createOrderItem(orderId,orderItem);

    }

    @PostMapping("/{orderId}/confirm")

    public void confirmOrder(@PathVariable Long orderId) {
        System.out.println("test");

    }
    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }


}
