package com.example.cafebackend.service;

import com.example.cafebackend.jwt.CustomerDetailsService;
import com.example.cafebackend.jwt.JwtFilter;
import com.example.cafebackend.jwt.JwtUtil;
import com.example.cafebackend.jwt.UserPrincipal;
import com.example.cafebackend.model.binding.user.*;
import com.example.cafebackend.model.entity.User;
import com.example.cafebackend.model.mapper.UserMapper;
import com.example.cafebackend.repository.UserRepository;
import com.example.cafebackend.utils.EmailUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomerDetailsService customerDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtFilter jwtFilter;

    @Mock
    private EntityManager em;

    @Mock
    private EmailUtils emailUtils;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testSignUp_SuccessfulRegistration() {
        //ARRANGE
        RegistrationRequest request = new RegistrationRequest();
        request.setName("test");
        request.setContactNumber("0888777333");
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setIsVerified("false");
        user.setRole("ROLE_USER");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userMapper.userDTOtoUserEntity(request)).thenReturn(user);

        //ACT
        ResponseEntity<String> response = userService.signUp(request);

        //ASSERT
        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(userRepository, times(1)).save(user);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("{\"message\":\"Successful Registration\"}", response.getBody());
    }

    @Test
    public void testSignUserAlreadyExists() {
        //ARRANGE
        RegistrationRequest request = new RegistrationRequest();
        request.setName("test");
        request.setContactNumber("0888777333");
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = new User();
        user.setEmail(request.getEmail());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        //ACT
        ResponseEntity<String> response = userService.signUp(request);

        //ASSERT
        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(userRepository, times(0)).save(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\":\"User with this email already exists\"}", response.getBody());
    }


    @Test
    public void login_shouldReturnError_whenInvalidCredentialsAreProvided() {
        // Arrange
        LoginRequest request = new LoginRequest("invalid-email@example.com", "password");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(null);

        // Act
        ResponseEntity<?> response = userService.login(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\":\"Bad Credentials\"}", response.getBody());
    }

    @Test
    public void getAllUsers_shouldReturnAllUsers_whenUsersExist() {
        // Arrange
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setName("user1");
        user1.setPassword("hashed-password");
        user1.setContactNumber("0888777666");
        user1.setIsVerified("true");
        user1.setRole("ROLE_USER");
        users.add(user1);

        User user2 = new User();
        user1.setId(2L);
        user1.setEmail("user2@example.com");
        user2.setName("user2");
        user2.setPassword("hashed-password");
        user2.setContactNumber("0888777555");
        user2.setIsVerified("false");
        user1.setRole("ROLE_USER");
        users.add(user2);

        TypedQuery<User> query = mock(TypedQuery.class);
        when(query.setParameter(eq("role"), any())).thenReturn(query); // add this line to return a non-null value
        when(query.getResultStream()).thenReturn(users.stream());
        when(em.createQuery("select u from User u where u.role=:role", User.class)).thenReturn(query);
        when(userMapper.userEntityToDTO(any())).thenReturn(new UserBindingModel());

        // Act
        List<UserBindingModel> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(query).getResultStream();
    }


}
