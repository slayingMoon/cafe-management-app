package com.example.cafebackend.model.binding.product;

import com.example.cafebackend.model.binding.category.GetProductCategoryModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductModel {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private String status;

//    it is important that GetProductModel contains both
//    categoryId and categoryName, so that get and edit can display category name
    private Long categoryId;

    private String categoryName;

}
