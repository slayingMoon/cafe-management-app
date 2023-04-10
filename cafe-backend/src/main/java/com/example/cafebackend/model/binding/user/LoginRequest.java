package com.example.cafebackend.model.binding.user;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {

    @NotNull
    private String email;
    @NotNull
    private String password;

}
