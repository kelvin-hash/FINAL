package com.CampusCart.Campus_backend.repository;

import com.CampusCart.Campus_backend.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    
    // Custom query method
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.productId = :productId")
    Optional<Cart> findByUserIdAndProductId(
        @Param("userId") Integer userId, 
        @Param("productId") Integer productId);
        @Modifying
        @Query("DELETE FROM Cart c WHERE c.userId = :userId AND c.productId = :productId")
        void deleteByUserIdAndProductId(@Param("userId") Integer userId, 
                                      @Param("productId") Integer productId);
    
    // Find all cart items for a user
    List<Cart> findByUserId(Integer userId);
}