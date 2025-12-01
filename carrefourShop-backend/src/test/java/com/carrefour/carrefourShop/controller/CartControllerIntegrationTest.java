package com.carrefour.carrefourShop.controller;

import com.carrefour.carrefourShop.dto.AddToCartRequest;
import com.carrefour.carrefourShop.dto.AuthRequest;
import com.carrefour.carrefourShop.dto.AuthResponse;
import com.carrefour.carrefourShop.dto.CartDto;
import com.carrefour.carrefourShop.dto.RegisterRequest;
import com.carrefour.carrefourShop.entity.Product;
import com.carrefour.carrefourShop.entity.User;
import com.carrefour.carrefourShop.repository.ProductRepository;
import com.carrefour.carrefourShop.repository.UserRepository;
import com.carrefour.carrefourShop.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ActiveProfiles("test")
class CartControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();
        
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> username);
        registry.add("spring.datasource.password", () -> password);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        registry.add("spring.flyway.validate-on-migrate", () -> "false");
        registry.add("spring.flyway.clean-disabled", () -> "true");
        registry.add("spring.flyway.clean-on-validation-error", () -> "false");
        
        System.out.println("Using Testcontainers PostgreSQL: " + jdbcUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private String authToken;
    private Long userId;
    private Long productId;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        productRepository.deleteAll();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");

        userService.register(registerRequest);

        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        userId = user.getId();

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        AuthResponse authResponse = userService.authenticate(authRequest);
        authToken = authResponse.getToken();

        Product product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("10.99"))
                .stock(100)
                .active(true)
                .category("Test Category")
                .brand("Test Brand")
                .build();
        Product savedProduct = productRepository.save(product);
        productId = savedProduct.getId();
    }

    @Test
    void getCart_WhenCartIsEmpty_ShouldReturnEmptyCart() throws Exception {
        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(0))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void getCart_WhenCartHasItems_ShouldReturnCartWithItems() throws Exception {
        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setProductId(productId);
        addRequest.setQuantity(2);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").exists())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].product.id").value(productId))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void getCart_WhenNotAuthenticated_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void addItemToCart_WhenValidRequest_ShouldAddItemToCart() throws Exception {
        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setProductId(productId);
        addRequest.setQuantity(3);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].product.id").value(productId))
                .andExpect(jsonPath("$.items[0].quantity").value(3))
                .andExpect(jsonPath("$.totalPrice").value(32.97));
    }

    @Test
    void addItemToCart_WhenProductNotFound_ShouldReturnNotFound() throws Exception {
        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setProductId(99999L);
        addRequest.setQuantity(1);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addItemToCart_WhenInsufficientStock_ShouldReturnBadRequest() throws Exception {
        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setProductId(productId);
        addRequest.setQuantity(101);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemToCart_WhenItemAlreadyExists_ShouldUpdateQuantity() throws Exception {
        AddToCartRequest firstRequest = new AddToCartRequest();
        firstRequest.setProductId(productId);
        firstRequest.setQuantity(2);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        AddToCartRequest secondRequest = new AddToCartRequest();
        secondRequest.setProductId(productId);
        secondRequest.setQuantity(3);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(5));
    }

    @Test
    void addItemToCart_WhenNotAuthenticated_ShouldReturnForbidden() throws Exception {
        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setProductId(productId);
        addRequest.setQuantity(1);

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void addItemToCart_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setProductId(null);
        addRequest.setQuantity(0);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isBadRequest());
    }
}
