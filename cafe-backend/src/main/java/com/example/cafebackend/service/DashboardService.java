package com.example.cafebackend.service;

import org.springframework.http.ResponseEntity;

public interface DashboardService {
    ResponseEntity<?> getCount();
}
