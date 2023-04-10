package com.example.cafebackend.service;

import com.example.cafebackend.model.binding.user.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<String> signUp(RegistrationRequest request);
    ResponseEntity<?> login(LoginRequest request);
    List<UserBindingModel> getAllUsers();
    String update(UserUpdateModel userUpdateModel);
    String checkToken();
    ResponseEntity<String> changePassword(ChangePasswordBindingModel passwordChangeModel);
    ResponseEntity<String> forgotPassword(ForgotPasswordModel forgotPasswordModel);
}
