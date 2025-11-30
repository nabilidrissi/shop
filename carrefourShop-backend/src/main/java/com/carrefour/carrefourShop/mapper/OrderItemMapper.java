package com.carrefour.carrefourShop.mapper;

import com.carrefour.carrefourShop.dto.OrderItemDto;
import com.carrefour.carrefourShop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ProductMapper.class)
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);
    
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OrderItem toEntity(OrderItemDto dto);
}
