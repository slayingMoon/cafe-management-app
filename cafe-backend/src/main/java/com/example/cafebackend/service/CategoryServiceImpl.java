package com.example.cafebackend.service;

import com.example.cafebackend.constants.CafeConstants;
import com.example.cafebackend.model.binding.category.AddCategoryModel;
import com.example.cafebackend.model.binding.category.UpdateCategoryModel;
import com.example.cafebackend.model.entity.Category;
import com.example.cafebackend.model.entity.Product;
import com.example.cafebackend.model.mapper.CategoryMapper;
import com.example.cafebackend.repository.CategoryRepository;
import com.example.cafebackend.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResponseEntity<String> addNewCategory(AddCategoryModel addCategoryModel) {

        try {
            Category category = categoryRepository.findByName(addCategoryModel.getName()).orElse(null);

            if (Objects.isNull(category)) {
                category = constructCategory(addCategoryModel);
                categoryRepository.save(category);
                return CafeUtils.getResponseEntity("Category added successfully.", HttpStatus.OK);
            }

            return CafeUtils.getResponseEntity("Category already exists.", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Category constructCategory(AddCategoryModel addCategoryDTO) {
        return categoryMapper
                .categoryDTOtoCategoryEntity(addCategoryDTO);
    }

    @Override
    public List<Category> getAllCategories() {
        log.info("Fetching categories..");

        return categoryRepository.findAll();
    }

    @Override
    public ResponseEntity<String> updateCategory(UpdateCategoryModel updateCategoryModel) {

        try {
            Category category = categoryRepository.findById(updateCategoryModel.getId()).orElse(null);

            if (Objects.nonNull(category)) {

                Category existingCategory = categoryRepository
                        .findByName(updateCategoryModel.getName())
                        .orElse(null);

                if (Objects.isNull(existingCategory)) {
                    category.setName(updateCategoryModel.getName());
                    categoryRepository.save(category);
                    return CafeUtils.getResponseEntity("Category Updated Successfully.", HttpStatus.OK);
                }

                return CafeUtils.getResponseEntity("Category Already Exists.", HttpStatus.BAD_REQUEST);
            }

            return CafeUtils.getResponseEntity("Category not found", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public Category findCategoryById(String categoryId) {
        return categoryRepository
                .findById(Long.parseLong(categoryId))
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public ResponseEntity<String> deleteCategory(Long id) {
        log.info("Deleting category...");

        try {
            Category category = categoryRepository.findById(id).orElse(null);

            if (Objects.nonNull(category)) {
                categoryRepository.delete(category);
                log.info("Category Deleted Successfully");
                return CafeUtils.getResponseEntity("Category Deleted Successfully", HttpStatus.OK);
            }

            log.info("Category with id {} does not exist.", id);
            return CafeUtils.getResponseEntity("Category does not exist.", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
