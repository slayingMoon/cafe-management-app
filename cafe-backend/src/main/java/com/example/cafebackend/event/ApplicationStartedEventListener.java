package com.example.cafebackend.event;

import com.example.cafebackend.model.entity.Role;
import com.example.cafebackend.model.entity.User;
import com.example.cafebackend.repository.RoleRepository;
import com.example.cafebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        List<User> adminList = userRepository.findAllByRoleId(1L);
        if(adminList.isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@admin.com");
            admin.setContactNumber("0999999999");
            admin.setPassword(encoder.encode("admin"));
            admin.setIsVerified("true");
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            admin.setRole(adminRole);

            userRepository.save(admin);
        }
    }
}
