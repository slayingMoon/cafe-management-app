package com.example.cafebackend.model.binding.user;

import lombok.Data;

@Data
public class ChangePasswordBindingModel {

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
