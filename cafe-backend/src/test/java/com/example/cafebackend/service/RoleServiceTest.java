package com.example.cafebackend.service;

import com.example.cafebackend.model.entity.Role;
import com.example.cafebackend.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    public void findRoleById_returnsAdminRole() {
        //ARRANGE
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        //ACT
        Role foundRole = roleService.findRoleById(1L);
        //ASSERT
        assertEquals("ROLE_ADMIN", foundRole.getName());
        assertEquals(1L, foundRole.getId());
    }

    @Test
    public void findRoleById_returnsUserRole() {
        //ARRANGE
        Role role = new Role();
        role.setId(2L);
        role.setName("ROLE_USER");

        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));

        //ACT
        Role foundRole = roleService.findRoleById(2L);
        //ASSERT
        assertEquals("ROLE_USER", foundRole.getName());
        assertEquals(2L, foundRole.getId());
    }

    @Test
    public void findRoleById_returnsNull_whenNoRoleWithGivenId() {
        //ARRANGE
        when(roleRepository.findById(3L)).thenReturn(Optional.empty());

        //ACT
        Role foundRole = roleService.findRoleById(3L);
        //ASSERT
        assertNull(foundRole);
    }

    @Test
    public void findAllRoles_returnsRolesAdminAndUser() {
        //ARRANGE
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ROLE_ADMIN");

        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setName("ROLE_USER");

        List<Role> roles = new ArrayList<>();
        roles.add(adminRole);
        roles.add(userRole);

        when(roleRepository.findAll()).thenReturn(roles);

        //ACT
        List<Role> foundRoles = roleService.getAllRoles();

        //ASSERT
        assertEquals(roles, foundRoles);
        assertEquals(2, foundRoles.size());
        assertEquals(adminRole, foundRoles.get(0));
        assertEquals("ROLE_ADMIN", foundRoles.get(0).getName());
        assertEquals(userRole, foundRoles.get(1));
        assertEquals("ROLE_USER", foundRoles.get(1).getName());
    }
}
