package com.example.cafebackend.rest;

import com.example.cafebackend.model.entity.Category;
import com.example.cafebackend.model.entity.Product;
import com.example.cafebackend.repository.CategoryRepository;
import com.example.cafebackend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductControllerIT {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost:";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product product = new Product();

    @Test
    @WithMockUser(username = "admin@admin.com", password = "admin", roles = "ADMIN")
    public void when_getOneProduct_returnFirst() throws Exception {
        Category category = new Category();
        category.setName("newCategory");
        categoryRepository.save(category);

        product.setName("one");
        product.setCategory(categoryRepository.findByName("newCategory").get());
        product.setDescription("desc");
        product.setPrice(new BigDecimal(2));
        product.setStatus("false");
        productRepository.save(product);

        mockMvc.perform(get(baseUrl + port + "/product/getById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(product.getName())))
                .andExpect(jsonPath("$.price", equalTo(product.getPrice().doubleValue())))
                .andExpect(jsonPath("$.categoryName", equalTo(product.getCategory().getName())));
    }

    @Test
    @WithMockUser(username = "admin@admin.com", password = "admin", roles = "ADMIN")
    public void when_productNotExists_returnsNotFound() throws Exception {

        mockMvc.perform(get(baseUrl + port + "/product/getById/2"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("{\"message\":\"Product not found\"}"));
    }

    @Test
    public void when_noUserLogged_returnsForbidden() throws Exception {

        mockMvc.perform(get(baseUrl + port + "/product/getById/1"))
                .andExpect(status().isForbidden());
    }

}
