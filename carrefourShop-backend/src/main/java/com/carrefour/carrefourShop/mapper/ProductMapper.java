package com.carrefour.carrefourShop.mapper;

import com.carrefour.carrefourShop.dto.ProductDto;
import com.carrefour.carrefourShop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    ProductDto toDto(Product product);
    Product toEntity(ProductDto dto);
}
