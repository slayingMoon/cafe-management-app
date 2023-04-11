package com.example.cafebackend.service;

import com.example.cafebackend.model.binding.product.AddProductModel;
import com.example.cafebackend.model.binding.product.GetProductModel;
import com.example.cafebackend.model.entity.Category;
import com.example.cafebackend.model.entity.Product;
import com.example.cafebackend.model.entity.User;
import com.example.cafebackend.model.mapper.ProductMapper;
import com.example.cafebackend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @Mock
    private CategoryServiceImpl categoryService;

    @Mock
    @PersistenceContext
    EntityManager em;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void addNewProduct_shouldReturnSuccessResponseEntity() {
        // Arrange
        AddProductModel addProductModel = new AddProductModel();
        addProductModel.setName("Test Product");
        addProductModel.setPrice("10.50");
        addProductModel.setCategoryId("1");
        addProductModel.setDescription("desc");

        Category productCategory = new Category();
        productCategory.setId(1L);
        productCategory.setName("Test Category");

        Product newProduct = new Product();
        newProduct.setId(1L);
        newProduct.setName(addProductModel.getName());
        newProduct.setPrice(new BigDecimal(addProductModel.getPrice()));
        newProduct.setStatus("true");
        newProduct.setCategory(productCategory);

        when(productRepository.findProductByName(addProductModel.getName())).thenReturn(Optional.empty());
        when(productMapper.productDTOtoProductEntity(addProductModel)).thenReturn(newProduct);
        when(categoryService.findCategoryById("1")).thenReturn(productCategory);

        // Act
        ResponseEntity<String> responseEntity = productService.addNewProduct(addProductModel);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Product added successfully.\"}", responseEntity.getBody());
    }

    @Test
    public void addNewProduct_shouldFail_ifProductExists() {
        // Arrange
        AddProductModel addProductModel = new AddProductModel();
        addProductModel.setName("Test Product");
        addProductModel.setPrice("10.50");
        addProductModel.setCategoryId("1");
        addProductModel.setDescription("desc");

        Category productCategory = new Category();
        productCategory.setId(1L);
        productCategory.setName("Test Category");

        Product newProduct = new Product();
        newProduct.setId(1L);
        newProduct.setName(addProductModel.getName());
        newProduct.setPrice(new BigDecimal(addProductModel.getPrice()));
        newProduct.setStatus("true");
        newProduct.setCategory(productCategory);

        when(productRepository.findProductByName(addProductModel.getName())).thenReturn(Optional.of(newProduct));

        // Act
        ResponseEntity<String> responseEntity = productService.addNewProduct(addProductModel);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Product already exists.\"}", responseEntity.getBody());
    }

    @Test
    public void testGetAllProducts() {
        // Create some test data
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(1L,"Product 1", new Category().setName("Burgers"), "Description 1", new BigDecimal("10.00"), "false"));
        productList.add(new Product(2L,"Product 2", new Category().setName("Burgers"), "Description 2", new BigDecimal("12.00"), "false"));

        // Mock the EntityManager to return the test data
        when(productRepository.findAll()).thenReturn(productList);

        // Mock the ProductMapper to return the expected GetProductModel objects
        GetProductModel productModel1 = new GetProductModel(1L,"Product 1", "Description 1", new BigDecimal("10.00"), "false", 1L, "Burgers");
        GetProductModel productModel2 = new GetProductModel(2L,"Product 2", "Description 2", new BigDecimal("12.00"), "false", 1L, "Burgers");
        when(productMapper.productEntityToProductDTO(any(Product.class))).thenReturn(productModel1, productModel2);

        // Call the getAllProducts method and verify the results
        List<GetProductModel> productModels = productService.getAllProducts();
        assertEquals(2, productModels.size());
        assertEquals(productModel1, productModels.get(0));
        assertEquals(productModel2, productModels.get(1));
    }

    @Test
    public void testDeleteProduct_Successful() {
        //ARRANGE
        Product product = new Product(1L,"Product 1", new Category().setName("Burgers"), "Description 1", new BigDecimal("10.00"), "false");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        //ACT
        ResponseEntity<String> response = productService.deleteProduct(1L);

        //ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\":\"Product Deleted Successfully\"}", response.getBody());
    }

    @Test
    public void testDeleteProduct_Fails_WhenProductNotFound() {
        //ARRANGE
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        //ACT
        ResponseEntity<String> response = productService.deleteProduct(1L);

        //ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\":\"Product does not exist.\"}", response.getBody());
    }

    @Test
    public void testUpdateProduct_Unsuccessful_WhenProductNotFound() {
        //ARRANGE
        AddProductModel updateDTO = new AddProductModel();
        updateDTO.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        //ACT
        ResponseEntity<String> response = productService.updateProduct(updateDTO);

        //ASSERT
        //ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateProduct_Unsuccessful_WhenNoChangesToCommit() {
        //ARRANGE
        AddProductModel updateDTO = new AddProductModel();
        updateDTO.setName("Product1");
        updateDTO.setCategoryId("1");
        updateDTO.setDescription("desc");
        updateDTO.setPrice("10.00");
        updateDTO.setId(1L);

        Category category = new Category();
        category.setId(1L);
        Product product = new Product();
        product.setName("Product1");
        product.setCategory(category);
        product.setDescription("desc");
        product.setPrice(new BigDecimal("10.00"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.findCategoryById("1")).thenReturn(new Category());
        //ACT
        ResponseEntity<String> response = productService.updateProduct(updateDTO);

        //ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\":\"Product has no changes to commit.\"}", response.getBody());
    }

    @Test
    public void testUpdateProduct_Successful() {
        //ARRANGE
        AddProductModel updateDTO = new AddProductModel();
        updateDTO.setName("Product2");
        updateDTO.setCategoryId("2");
        updateDTO.setDescription("descc");
        updateDTO.setPrice("11.00");
        updateDTO.setId(1L);

        Category category = new Category();
        category.setId(1L);
        Product product = new Product();
        product.setName("Product1");
        product.setCategory(category);
        product.setDescription("desc");
        product.setPrice(new BigDecimal("10.00"));

        Category newCategory = new Category();
        category.setId(2L);
        category.setName("Cakes");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.findCategoryById("1")).thenReturn(new Category());
        //ACT
        ResponseEntity<String> response = productService.updateProduct(updateDTO);

        //ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\":\"Product Updated Successfully.\"}", response.getBody());
    }
}
