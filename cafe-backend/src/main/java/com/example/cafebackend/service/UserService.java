package com.example.cafebackend.service;

import com.example.cafebackend.model.binding.user.*;
import com.example.cafebackend.model.entity.User;
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

    List<User> findInactiveUsers();

    void notifyAdminsAboutInactiveUsers(List<User> inactiveUsers);

    void notifyAdminsAboutUserRegistration(String userEmail);

    String updateUsername(UsernameUpdateModel usernameUpdateModel);

    ResponseEntity<?> getUserByEmail(String email);

    ResponseEntity<String> edit(UserEditModel userEditDTO);
}
