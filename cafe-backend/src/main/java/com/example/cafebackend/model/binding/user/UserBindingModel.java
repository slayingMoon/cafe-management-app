package com.example.cafebackend.model.binding.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBindingModel {

    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private String userRole;
    private Long roleId;
    private String status;

}
