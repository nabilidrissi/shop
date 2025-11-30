package com.carrefour.carrefourShop.config;

import com.carrefour.carrefourShop.entity.Product;
import com.carrefour.carrefourShop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            initializeProducts();
        }
    }

    private void initializeProducts() {
        List<Product> products = Arrays.asList(
                Product.builder()
                        .name("Lait entier")
                        .description("Lait entier pasteurisé 1L")
                        .price(new BigDecimal("1.99"))
                        .stock(100)
                        .category("Dairy")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Pain de mie")
                        .description("Pain de mie complet 500g")
                        .price(new BigDecimal("2.49"))
                        .stock(50)
                        .category("Bakery")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Pommes Golden")
                        .description("Pommes Golden 1kg")
                        .price(new BigDecimal("3.99"))
                        .stock(75)
                        .category("Fruits")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Yaourt nature")
                        .description("Yaourt nature bio x8")
                        .price(new BigDecimal("4.99"))
                        .stock(60)
                        .category("Dairy")
                        .brand("Carrefour Bio")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Pâtes spaghetti")
                        .description("Pâtes spaghetti 500g")
                        .price(new BigDecimal("1.29"))
                        .stock(120)
                        .category("Pasta")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Sauce tomate")
                        .description("Sauce tomate basilic 500g")
                        .price(new BigDecimal("2.19"))
                        .stock(80)
                        .category("Sauces")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Poulet rôti")
                        .description("Poulet rôti 1.2kg")
                        .price(new BigDecimal("8.99"))
                        .stock(20)
                        .category("Meat")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Saumon frais")
                        .description("Filet de saumon 300g")
                        .price(new BigDecimal("12.99"))
                        .stock(15)
                        .category("Fish")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Riz basmati")
                        .description("Riz basmati 1kg")
                        .price(new BigDecimal("3.49"))
                        .stock(90)
                        .category("Rice")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Huile d'olive")
                        .description("Huile d'olive extra vierge 750ml")
                        .price(new BigDecimal("6.99"))
                        .stock(40)
                        .category("Oils")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Eau minérale")
                        .description("Eau minérale naturelle 6x1.5L")
                        .price(new BigDecimal("4.99"))
                        .stock(100)
                        .category("Beverages")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Café moulu")
                        .description("Café arabica moulu 250g")
                        .price(new BigDecimal("5.99"))
                        .stock(55)
                        .category("Beverages")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Chocolat noir")
                        .description("Tablette chocolat noir 70% 200g")
                        .price(new BigDecimal("3.79"))
                        .stock(70)
                        .category("Sweets")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Fromage de chèvre")
                        .description("Fromage de chèvre frais 200g")
                        .price(new BigDecimal("4.49"))
                        .stock(35)
                        .category("Dairy")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Jus d'orange")
                        .description("Jus d'orange pressé 1L")
                        .price(new BigDecimal("2.99"))
                        .stock(65)
                        .category("Beverages")
                        .brand("Carrefour")
                        .imageUrl("https://via.placeholder.com/300")
                        .active(true)
                        .build()
        );

        productRepository.saveAll(products);
    }
}

