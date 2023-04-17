package com.example.cafebackend.repository;
import com.example.cafebackend.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findProductByName(String newProductName);
    @Query("select p from Product p where p.category.id=:categoryId and p.status=:status")
    List<Product> findAllByCategoryIdAndStatus(Long categoryId, String status);
}
