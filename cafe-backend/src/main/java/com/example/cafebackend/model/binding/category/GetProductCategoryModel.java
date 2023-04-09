package com.example.cafebackend.model.binding.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductCategoryModel {

    private Long categoryId;

    private String categoryName;
}
