package com.CampusCart.Campus_backend.repository;

import com.CampusCart.Campus_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByCategoryName(String categoryName);
    
    @Query("SELECT c.categoryName FROM Category c")
    List<String> findAllCategoryNames();
}
