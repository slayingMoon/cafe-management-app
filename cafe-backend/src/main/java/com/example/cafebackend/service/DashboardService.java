package com.example.cafebackend.service;

import com.example.cafebackend.repository.BillRepository;
import com.example.cafebackend.repository.CategoryRepository;
import com.example.cafebackend.repository.ProductRepository;
import com.example.cafebackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DashboardService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    public ResponseEntity<?> getCount() {
        Map<String, Long> map = new HashMap<>();
        map.put("category", categoryRepository.count());
        map.put("product", productRepository.count());
        map.put("bill", billRepository.count());

        return ResponseEntity.ok(map);
    }
}
