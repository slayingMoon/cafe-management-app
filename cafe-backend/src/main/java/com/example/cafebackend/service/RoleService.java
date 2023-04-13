package com.example.cafebackend.service;

import com.example.cafebackend.model.entity.Role;

import java.util.List;

public interface RoleService {
    Role findRoleById(Long id);

    List<Role> getAllRoles();
}
