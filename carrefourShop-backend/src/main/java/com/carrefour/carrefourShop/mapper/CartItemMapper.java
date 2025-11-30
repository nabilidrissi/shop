package com.carrefour.carrefourShop.mapper;

import com.carrefour.carrefourShop.dto.CartItemDto;
import com.carrefour.carrefourShop.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ProductMapper.class)
public interface CartItemMapper {
    CartItemDto toDto(CartItem cartItem);
    
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CartItem toEntity(CartItemDto dto);
}
