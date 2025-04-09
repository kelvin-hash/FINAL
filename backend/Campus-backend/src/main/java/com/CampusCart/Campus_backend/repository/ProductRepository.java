package com.CampusCart.Campus_backend.repository;

import com.CampusCart.Campus_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Basic Finders
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByCategoryId(Integer categoryId);
    List<Product> findByUserId(Integer userId);
    List<Product> findByStatus(String status);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByCategoryName(String categoryName);
    List<Product> findByProductNameContainingIgnoreCaseAndCategoryName(String productName, String categoryName);

    // Enhanced finders with Optional
    Optional<Product> findFirstByOrderByProductIdAsc();
    
    // Custom queries
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR p.productName LIKE %:name%) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:condition IS NULL OR p.condition = :condition) AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId)")
    List<Product> advancedSearch(
        String name,
        Double minPrice,
        Double maxPrice,
        String condition,
        Integer categoryId);

    // Category operations moved to CategoryRepository
    // (This should be in CategoryRepository.java instead)
    // @Query("SELECT c.categoryId FROM Category c")
    // List<Integer> findAllCategoryIds();

    // New utility methods
    boolean existsByCategoryId(Integer categoryId);
    boolean existsByUserId(Integer userId);
    
    @Query("SELECT DISTINCT p.categoryId FROM Product p")
    List<Integer> findDistinctCategoryIds();
    List<Product> findByProductNameContainingAndCategoryName(String name, String category);
}