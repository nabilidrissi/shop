package com.carrefour.carrefourShop.service;

import com.carrefour.carrefourShop.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();
    List<ProductDto> getProductsByCategory(String category);
    List<ProductDto> searchProducts(String keyword);
    ProductDto getProductById(Long id);
}

