package com.example.cafebackend.model.mapper;

import com.example.cafebackend.model.binding.category.AddCategoryModel;
import com.example.cafebackend.model.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category categoryDTOtoCategoryEntity(AddCategoryModel addCategoryDTO);
}
