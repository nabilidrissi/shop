package com.carrefour.carrefourShop.mapper;

import com.carrefour.carrefourShop.dto.UserDto;
import com.carrefour.carrefourShop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CartMapper.class)
public interface UserMapper {
    UserDto toDto(User user);
    
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDto dto);
}
