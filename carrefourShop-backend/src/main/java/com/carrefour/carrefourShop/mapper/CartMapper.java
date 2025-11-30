package com.carrefour.carrefourShop.mapper;

import com.carrefour.carrefourShop.dto.CartDto;
import com.carrefour.carrefourShop.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CartItemMapper.class)
public interface CartMapper {
    CartDto toDto(Cart cart);
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Cart toEntity(CartDto dto);
}
