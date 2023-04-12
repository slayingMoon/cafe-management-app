package com.example.cafebackend.model.binding.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditModel {
    private Long id;
    private String name;
    private String email;
    private String contactNumber;
}
