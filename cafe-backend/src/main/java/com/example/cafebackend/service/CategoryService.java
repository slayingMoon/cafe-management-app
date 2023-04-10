package com.example.cafebackend.service;

import com.example.cafebackend.model.binding.category.AddCategoryModel;
import com.example.cafebackend.model.binding.category.UpdateCategoryModel;
import com.example.cafebackend.model.entity.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {
    ResponseEntity<String> addNewCategory(AddCategoryModel addCategoryModel);
    List<Category> getAllCategories();
    ResponseEntity<String> updateCategory(UpdateCategoryModel updateCategoryModel);
    Category findCategoryById(String categoryId);
    ResponseEntity<String> deleteCategory(Long id);
}
