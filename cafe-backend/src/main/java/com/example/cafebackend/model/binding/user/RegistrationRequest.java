package com.example.cafebackend.model.binding.user;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class RegistrationRequest {

    @NotBlank
    @Size(min = 4, max = 20)
    private String name;

    @NotBlank
    @Size(min = 9, max = 13)
    private String contactNumber;

    @Email
    @NotNull
    private String email;

    @NotBlank
    @Size(min = 4, max = 20)
    private String password;
}
