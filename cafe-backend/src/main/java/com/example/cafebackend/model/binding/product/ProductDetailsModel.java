package com.example.cafebackend.model.binding.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsModel {

    private String name;

    private String category;

    private String quantity;

    private String price;

    private String total;
}
