package com.carrefour.carrefourShop.service;

import com.carrefour.carrefourShop.dto.ProductDto;
import reactor.core.publisher.Flux;

public interface ProductService {
    Flux<ProductDto> getAllProducts();
    Flux<ProductDto> getProductsByCategory(String category);
    Flux<ProductDto> searchProducts(String keyword);
    ProductDto getProductById(Long id);
}

