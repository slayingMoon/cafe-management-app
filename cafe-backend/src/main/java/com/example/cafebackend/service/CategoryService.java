package com.example.cafebackend.service;

import com.example.cafebackend.constants.CafeConstants;
import com.example.cafebackend.model.binding.category.AddCategoryModel;
import com.example.cafebackend.model.binding.category.UpdateCategoryModel;
import com.example.cafebackend.model.entity.Category;
import com.example.cafebackend.model.entity.Product;
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
public class CategoryService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public ResponseEntity<String> addNewCategory(AddCategoryModel addCategoryModel) {

        try {
            Category category = em.createQuery("select c from Category c", Category.class)
                    .getResultStream()
                    .filter(c -> c.getName().equals(addCategoryModel.getName()))
                    .findAny()
                    .orElse(null);

            if (Objects.isNull(category)) {
                category = constructCategory(addCategoryModel.getName());
                em.persist(category);
                return CafeUtils.getResponseEntity("Category added successfully.", HttpStatus.OK);
            }

            return CafeUtils.getResponseEntity("Category already exists.", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Category constructCategory(String name) {
        return new Category().setName(name);
    }

    public List<Category> getAllCategories() {
        log.info("Fetching categories..");

        return em.createQuery("select c from Category c", Category.class)
                .getResultList();
    }

    @Transactional
    public ResponseEntity<String> updateCategory(UpdateCategoryModel updateCategoryModel) {

        try {
            Category category = em.find(Category.class, updateCategoryModel.getId());

            if (Objects.nonNull(category)) {

                Category existingCategory = em.createQuery("select c from Category c", Category.class)
                        .getResultStream()
                        .filter(c -> c.getName().equals(updateCategoryModel.getName()))
                        .findAny()
                        .orElse(null);

                if (Objects.isNull(existingCategory)) {
                    category.setName(updateCategoryModel.getName());
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

    public Category findCategoryById(String categoryId) {
        return getAllCategories()
                .stream()
                .filter(c -> c.getId().equals(Long.parseLong(categoryId)))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional
    public ResponseEntity<String> deleteCategory(Long id) {
        log.info("Deleting category...");

        try {
            Category category = em.find(Category.class, id);

            if (Objects.nonNull(category)) {
                em.remove(category);
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
