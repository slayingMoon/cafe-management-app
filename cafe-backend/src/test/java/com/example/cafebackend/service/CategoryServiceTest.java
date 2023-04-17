package com.example.cafebackend.service;

import com.example.cafebackend.model.binding.category.AddCategoryModel;
import com.example.cafebackend.model.binding.category.UpdateCategoryModel;
import com.example.cafebackend.model.entity.Category;
import com.example.cafebackend.model.mapper.CategoryMapper;
import com.example.cafebackend.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void addNewCategory_Successful() {
        //ARRANGE
        AddCategoryModel addCategoryModel = new AddCategoryModel();
        addCategoryModel.setName("Burgers");

        Category category = new Category();
        category.setName("Burgers");

        when(categoryRepository.findByName("Burgers")).thenReturn(Optional.empty());
        when(categoryMapper.categoryDTOtoCategoryEntity(addCategoryModel)).thenReturn(category);

        //ACT
        ResponseEntity<String> response = categoryService.addNewCategory(addCategoryModel);

        //ASSERT
        verify(categoryRepository, times(1)).save(category);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\":\"Category added successfully.\"}", response.getBody());
    }

    @Test
    public void addNewCategory_returnsCategoryExists() {
        AddCategoryModel addCategoryModel = new AddCategoryModel();
        addCategoryModel.setName("Burgers");

        Category category = new Category();
        category.setName("Burgers");

        when(categoryRepository.findByName("Burgers")).thenReturn(Optional.of(category));

        //ACT
        ResponseEntity<String> response = categoryService.addNewCategory(addCategoryModel);

        //ASSERT
        verify(categoryRepository, never()).save(any(Category.class));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\":\"Category already exists.\"}", response.getBody());
    }

    @Test
    public void addNewCategory_returns_internalServerError() {
        //ARRANGE
        AddCategoryModel addCategoryModel = new AddCategoryModel();
        addCategoryModel.setName("Burgers");

        when(categoryRepository.findByName("Burgers")).thenThrow(new RuntimeException());

        //ACT
        ResponseEntity<String> response = categoryService.addNewCategory(addCategoryModel);

        //ASSERT
        verify(categoryRepository, never()).save(any(Category.class));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"message\":\"Something went wrong.\"}", response.getBody());
    }

    @Test
    public void getAllCategories_returnsSavedCategories() {
        //ARRANGE
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Burgers");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Desserts");

        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        //ACT
        List<Category> foundCategories = categoryService.getAllCategories();

        //ASSERT
        assertEquals(categories, foundCategories);
        assertEquals(2, foundCategories.size());
        assertEquals(category1, foundCategories.get(0));
        assertEquals("Burgers", foundCategories.get(0).getName());
        assertEquals(category2, foundCategories.get(1));
        assertEquals("Desserts", foundCategories.get(1).getName());
    }

    @Test
    public void findCategoryById_categoryExists_returnsCategory() {
        //ARRANGE
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        //ACT
        Category result = categoryService.findCategoryById("1");

        //ASSERT
        assertEquals(category, result);
    }

    @Test
    public void findCategoryById_categoryDoesNotExist_throwsRuntimeException() {
        //ARRANGE
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        //ACT & ASSERT
        assertThrows(RuntimeException.class, () -> categoryService.findCategoryById("1"));
    }

    @Test
    public void updateCategory_categoryExistsAndNameIsUnique_updatesCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("oldName");

        UpdateCategoryModel updateCategoryModel = new UpdateCategoryModel();
        updateCategoryModel.setId(1L);
        updateCategoryModel.setName("newName");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("newName")).thenReturn(Optional.empty());

        ResponseEntity<String> result = categoryService.updateCategory(updateCategoryModel);

        assertEquals("{\"message\":\"Category Updated Successfully.\"}", result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("newName", category.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void updateCategory_categoryExistsAndNameIsNotUnique_returnsBadRequest() {
        Category category = new Category();
        category.setId(1L);
        category.setName("oldName");

        Category existingCategory = new Category();
        existingCategory.setId(2L);
        existingCategory.setName("newName");

        UpdateCategoryModel updateCategoryModel = new UpdateCategoryModel();
        updateCategoryModel.setId(1L);
        updateCategoryModel.setName("newName");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("newName")).thenReturn(Optional.of(existingCategory));

        ResponseEntity<String> result = categoryService.updateCategory(updateCategoryModel);

        assertEquals("{\"message\":\"Category Already Exists.\"}", result.getBody());
        assertEquals(400, result.getStatusCodeValue());
        assertEquals("oldName", category.getName());
        verify(categoryRepository, never()).save(category);
    }

    @Test
    public void updateCategory_categoryDoesNotExist_returnsBadRequest() {
        UpdateCategoryModel updateCategoryModel = new UpdateCategoryModel();
        updateCategoryModel.setId(1L);
        updateCategoryModel.setName("newName");

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> result = categoryService.updateCategory(updateCategoryModel);

        assertEquals("{\"message\":\"Category not found\"}", result.getBody());
        assertEquals(400, result.getStatusCodeValue());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void updateCategory_throws_internalServerError() {
        // Arrange
        UpdateCategoryModel updateCategoryModel = new UpdateCategoryModel();
        updateCategoryModel.setId(1L);
        when(categoryRepository.findById(updateCategoryModel.getId())).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<String> responseEntity = categoryService.updateCategory(updateCategoryModel);

        // Assert
        verify(categoryRepository, never()).save(any());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Something went wrong.\"}", responseEntity.getBody());
    }


    @Test
    public void testDeleteCategorySuccess() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        ResponseEntity<String> responseEntity = categoryService.deleteCategory(categoryId);

        // Assert
        verify(categoryRepository, times(1)).delete(category);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Category Deleted Successfully\"}", responseEntity.getBody());
    }

    @Test
    public void testDeleteCategoryNotFound() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> responseEntity = categoryService.deleteCategory(categoryId);

        // Assert
        verify(categoryRepository, never()).delete(any());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Category does not exist.\"}", responseEntity.getBody());
    }

    @Test
    public void testDeleteCategoryException() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<String> responseEntity = categoryService.deleteCategory(categoryId);

        // Assert
        verify(categoryRepository, never()).delete(any());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Something went wrong.\"}", responseEntity.getBody());
    }

}
