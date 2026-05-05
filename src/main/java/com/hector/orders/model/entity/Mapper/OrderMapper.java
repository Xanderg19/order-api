package com.hector.orders.model.entity.Mapper;

import com.hector.orders.model.entity.Order;
import com.hector.orders.model.entity.dto.OrderDTO;

public class OrderMapper {

    public static OrderDTO toDTO(Order order) {
        return OrderDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .build();
    }
}
