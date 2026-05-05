package com.hector.orders.model.entity.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
public class OrderItemsDTO {
    private Long productoId;
    private Integer cantidad;
}
