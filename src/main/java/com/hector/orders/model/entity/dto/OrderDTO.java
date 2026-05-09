package com.hector.orders.model.entity.dto;

import com.hector.orders.model.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderDTO {

    private Long orderId;
    private OrderStatus status;


}
