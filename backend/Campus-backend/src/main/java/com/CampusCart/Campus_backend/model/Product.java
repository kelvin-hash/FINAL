package com.CampusCart.Campus_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "product_name", nullable = false, length = 250)
    private String productName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_id", nullable = true)
    private Integer categoryId;

    @Column(name = "product_condition", columnDefinition = "ENUM('New','Used')")
    private String condition;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "status", columnDefinition = "ENUM('Available','Pending','Sold')")
    private String status;

    public void setFormattedPrice(String format) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setFormattedPrice'");
    }
}