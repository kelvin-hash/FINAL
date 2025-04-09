package com.CampusCart.Campus_backend.dto;

import com.CampusCart.Campus_backend.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDTO {
    private Integer productId;
    private String productName;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Double subtotal;

    public CartItemDTO(Product product, Integer quantity) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.imageUrl = product.getImageUrl();
        this.price = product.getPrice();
        this.quantity = quantity;
        this.subtotal = product.getPrice() * quantity;
    }
}
