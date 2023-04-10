package com.example.cafebackend.model.mapper;

import com.example.cafebackend.model.binding.user.RegistrationRequest;
import com.example.cafebackend.model.binding.user.UserBindingModel;
import com.example.cafebackend.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserBindingModel userEntityToDTO(User user);
    User userDTOtoUserEntity(RegistrationRequest userRegisterDTO);
}
