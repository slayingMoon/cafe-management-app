package com.example.cafebackend.model.binding.product;

import lombok.Data;

@Data
public class AddProductModel {

    private Long id;

    private String name;

    private String categoryId;

    private String description;

    private String price;
}
