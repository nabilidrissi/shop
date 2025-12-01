package com.carrefour.carrefourShop.service.impl;

import com.carrefour.carrefourShop.dto.ProductDto;
import com.carrefour.carrefourShop.entity.Product;
import com.carrefour.carrefourShop.exception.ExceptionConstants;
import com.carrefour.carrefourShop.exception.ResourceNotFoundException;
import com.carrefour.carrefourShop.mapper.ProductMapper;
import com.carrefour.carrefourShop.repository.ProductRepository;
import com.carrefour.carrefourShop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Flux<ProductDto> getAllProducts() {
        return Flux.fromIterable(productRepository.findByActiveTrue())
                .map(productMapper::toDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<ProductDto> getProductsByCategory(String category) {
        return Flux.fromIterable(productRepository.findByActiveTrueAndCategory(category))
                .map(productMapper::toDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<ProductDto> searchProducts(String keyword) {
        return Flux.fromIterable(productRepository.searchActiveProducts(keyword))
                .map(productMapper::toDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.PRODUCT_NOT_FOUND_BY_ID, ExceptionConstants.getMessage(ExceptionConstants.PRODUCT_NOT_FOUND_BY_ID, id)));
        if (!product.getActive()) {
            throw new ResourceNotFoundException(ExceptionConstants.PRODUCT_NOT_FOUND_BY_ID, ExceptionConstants.getMessage(ExceptionConstants.PRODUCT_NOT_FOUND_BY_ID, id));
        }
        return productMapper.toDto(product);
    }
}

