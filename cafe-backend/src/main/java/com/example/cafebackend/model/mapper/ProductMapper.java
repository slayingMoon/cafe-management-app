package com.example.cafebackend.model.mapper;

import com.example.cafebackend.model.binding.product.AddProductModel;
import com.example.cafebackend.model.binding.product.GetProductModel;
import com.example.cafebackend.model.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product productDTOtoProductEntity(AddProductModel addProductDTO);
    GetProductModel productEntityToProductDTO(Product product);
}
