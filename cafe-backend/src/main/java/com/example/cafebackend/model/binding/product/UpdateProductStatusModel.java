package com.example.cafebackend.model.binding.product;

import lombok.Data;

@Data
public class UpdateProductStatusModel {

    private Long id;

    private String status;
}
