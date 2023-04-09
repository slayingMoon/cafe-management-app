package com.example.cafebackend.jwt;

import com.example.cafebackend.model.entity.User;
import com.example.cafebackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername {}", username);

        return userRepository
                .findByEmail(username)
                .map(this::map)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    //map to spring security User
    private UserDetails map(User userEntity) {
        return new UserPrincipal(
                userEntity.getEmail(),
                userEntity.getPassword(),
                List.of(mapRole(userEntity.getRole())),
                Boolean.parseBoolean(userEntity.getIsVerified())
        );
    }

    private SimpleGrantedAuthority mapRole(String role) {
        return new SimpleGrantedAuthority(role);
    }


}
