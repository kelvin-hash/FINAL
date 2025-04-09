package com.CampusCart.Campus_backend.controller;

import com.CampusCart.Campus_backend.dto.CartItemDTO;
import com.CampusCart.Campus_backend.model.Cart;
import com.CampusCart.Campus_backend.model.Category;
import com.CampusCart.Campus_backend.model.Product;
import com.CampusCart.Campus_backend.repository.CartRepository;
import com.CampusCart.Campus_backend.repository.CategoryRepository;
import com.CampusCart.Campus_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Collections;
import java.io.File;
import java.nio.file.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;  // Changed from CrudRepository to CartRepository

    @Autowired
    public ProductController(ProductRepository productRepository, 
                           CategoryRepository categoryRepository,
                           CartRepository cartRepository) {  // Added cartRepository parameter
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return ResponseEntity.ok(products != null ? products : Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Map.of(
                       "error", "Database error",
                       "message", e.getMessage()
                   ));
        }
    }

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product) {
        try {
            // Validate required fields
            if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Product name is required");
            }
            
            if (product.getPrice() == null || product.getPrice() <= 0) {
                return ResponseEntity.badRequest().body("Price must be positive");
            }
    
            // Validate category exists
            Category category = categoryRepository.findByCategoryName(product.getCategoryName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category"));
            
            // Set required fields
            product.setCategoryId(category.getCategoryId());
            product.setUserId(1); // Hardcoded user ID for development <<< ADD THIS LINE
            
            // Set default status if not provided
            if (product.getStatus() == null) {
                product.setStatus("Available");
            }
    
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Map.of(
                       "error", "Failed to save product",
                       "message", e.getMessage()
                   ));
        }
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
    try {
        // Create upload directory if it doesn't exist
        String uploadDir = "uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Generate unique filename
        String fileName = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = fileName + fileExtension;
        
        // Save file
        Path filePath = Paths.get(uploadDir + newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return ResponseEntity.ok("/uploads/" + newFileName);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload image: " + e.getMessage());
    }
}
@GetMapping("/search")
public ResponseEntity<List<Product>> searchProducts(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String category) {
    
    try {
        List<Product> products;
        
        if (name != null && category != null) {
            // Combined search
            products = productRepository.findByProductNameContainingAndCategoryName(name, category);
        } else if (name != null) {
            // Name search only
            products = productRepository.findByProductNameContainingIgnoreCase(name);
        } else if (category != null) {
            // Category filter only
            products = productRepository.findByCategoryName(category);
        } else {
            // No filters - return all
            products = productRepository.findAll();
        }
        
        return ResponseEntity.ok(products);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
// Add this to your ProductController.java
@GetMapping("/categories")
public ResponseEntity<List<Category>> getAllCategories() {
    try {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
@PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(
        @RequestParam Integer productId,
        @RequestParam Integer userId,
        @RequestParam(defaultValue = "1") Integer quantity) {
        
        try {
            productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            
            // Check if item already in cart
            Optional<Cart> existingItem = ((CartRepository) cartRepository).findByUserIdAndProductId(userId, productId);
            
            if (existingItem.isPresent()) {
                // Update quantity
                Cart cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartRepository.save(cartItem);
            } else {
                // Add new item
                Cart cartItem = new Cart();
                cartItem.setProductId(productId);
                cartItem.setUserId(userId);
                cartItem.setQuantity(quantity);
                cartRepository.save(cartItem);
            }
            
            return ResponseEntity.ok("Product added to cart");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error adding to cart: " + e.getMessage());
        }
    }

    @GetMapping("/cart/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCart(@PathVariable Integer userId) {
        try {
            List<Cart> cartItems = cartRepository.findByUserId(userId);
            List<CartItemDTO> cartItemDTOs = cartItems.stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                    return new CartItemDTO(product, item.getQuantity());
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(cartItemDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/cart/remove")
public ResponseEntity<String> removeFromCart(
    @RequestParam Integer productId,
    @RequestParam Integer userId) {
    
    try {
        // Find the cart item
        Optional<Cart> cartItem = cartRepository.findByUserIdAndProductId(userId, productId);
        
        if (cartItem.isPresent()) {
            // Remove the item
            cartRepository.delete(cartItem.get());
            return ResponseEntity.ok("Product removed from cart");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body("Item not found in cart");
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("Error removing from cart: " + e.getMessage());
    }
}}
