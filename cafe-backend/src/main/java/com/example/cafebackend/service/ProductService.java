package com.example.cafebackend.service;

import com.example.cafebackend.model.binding.product.AddProductModel;
import com.example.cafebackend.model.binding.product.GetProductModel;
import com.example.cafebackend.model.binding.product.UpdateProductStatusModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    ResponseEntity<String> addNewProduct(AddProductModel addProductModel);
    List<GetProductModel> getAllProducts();
    ResponseEntity<String> updateProduct(AddProductModel updateProductModel);
    ResponseEntity<String> deleteProduct(Long id);
    ResponseEntity<String> updateStatus(UpdateProductStatusModel updateProductStatus);
    ResponseEntity<?> getProductsByCategory(Long categoryId);
    ResponseEntity<?> getProductById(Long id);
}
