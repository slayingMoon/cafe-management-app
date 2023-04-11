package com.example.cafebackend.service;

import com.example.cafebackend.repository.BillRepository;
import com.example.cafebackend.repository.CategoryRepository;
import com.example.cafebackend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DashboardServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    public void getCount_shouldReturnCountMap() {
        // Arrange
        long categoryCount = 5;
        long productCount = 10;
        long billCount = 15;

        when(categoryRepository.count()).thenReturn(categoryCount);
        when(productRepository.count()).thenReturn(productCount);
        when(billRepository.count()).thenReturn(billCount);

        Map<String, Long> expectedCountMap = new HashMap<>();
        expectedCountMap.put("category", categoryCount);
        expectedCountMap.put("product", productCount);
        expectedCountMap.put("bill", billCount);

        // Act
        ResponseEntity<?> responseEntity = dashboardService.getCount();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedCountMap, responseEntity.getBody());
    }
}
