package com.carrefour.carrefourShop.mapper;

import com.carrefour.carrefourShop.dto.OrderDto;
import com.carrefour.carrefourShop.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = OrderItemMapper.class)
public interface OrderMapper {
    OrderDto toDto(Order order);
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderDto dto);
}
