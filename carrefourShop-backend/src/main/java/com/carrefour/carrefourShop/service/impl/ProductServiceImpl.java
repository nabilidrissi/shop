package com.carrefour.carrefourShop.service.impl;

import com.carrefour.carrefourShop.dto.ProductDto;
import com.carrefour.carrefourShop.entity.Product;
import com.carrefour.carrefourShop.exception.ResourceNotFoundException;
import com.carrefour.carrefourShop.mapper.ProductMapper;
import com.carrefour.carrefourShop.repository.ProductRepository;
import com.carrefour.carrefourShop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductsByCategory(String category) {
        return productRepository.findByActiveTrueAndCategory(category).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {
        return productRepository.searchActiveProducts(keyword).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (!product.getActive()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        return productMapper.toDto(product);
    }
}

