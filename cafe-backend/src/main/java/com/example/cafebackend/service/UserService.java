package com.example.cafebackend.service;

import com.example.cafebackend.constants.CafeConstants;
import com.example.cafebackend.jwt.CustomerDetailsService;
import com.example.cafebackend.jwt.JwtFilter;
import com.example.cafebackend.jwt.JwtUtil;
import com.example.cafebackend.jwt.UserPrincipal;
import com.example.cafebackend.model.binding.user.*;
import com.example.cafebackend.model.entity.User;
import com.example.cafebackend.repository.UserRepository;
import com.example.cafebackend.utils.CafeUtils;
import com.example.cafebackend.utils.EmailUtils;
import com.example.cafebackend.utils.PasswordGenerator;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EmailUtils emailUtils;

    public ResponseEntity<String> signUp(RegistrationRequest request) {
        log.info("Inside signup: {}", request);

        try {
            Optional<User> existsUserByEmail = userRepository.findByEmail(request.getEmail());

            if (existsUserByEmail.isEmpty()) {
                User newUser = constructUser(request);
                userRepository.save(newUser);

                return CafeUtils.getResponseEntity("Successful Registration", HttpStatus.CREATED);
            } else {
                return CafeUtils.getResponseEntity("User with this email already exists", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CafeConstants.SOMETHING_WENT_WRONG);
    }

    private User constructUser(RegistrationRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setContactNumber(request.getContactNumber());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setIsVerified("false");
        user.setRole("ROLE_USER");
        return user;
    }

    public ResponseEntity<?> login(LoginRequest request) {
        log.info("Inside login");

        try {
            Authentication auth = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            if (auth.isAuthenticated()) {
                UserPrincipal user = (UserPrincipal) auth.getPrincipal();

                if (user.isApproved()) {
                    String userRole = user.getAuthorities().toString().replaceAll("[\\[\\],]", "");
                    String accessToken = jwtUtil.generateToken(user.getUsername(), userRole);

                    //THIS RESPONSE NEED TO BE INTERCEPTED BY THE ANGULAR FRONT-END
                    AuthResponse response = new AuthResponse(user.getUsername(), accessToken);

                    return ResponseEntity
                            .ok()
                            .body(response);
                }else {

                    return CafeUtils.getResponseEntity("Wait for admin approval", HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception ex) {
            log.error("{}", ex.getMessage());
        }

        return CafeUtils.getResponseEntity("Bad Credentials", HttpStatus.BAD_REQUEST);
    }

    public List<UserBindingModel> getAllUsers() {
        log.info("Fetching all users");

        return em.createQuery("select u from User u where u.role=:role", User.class)
                .setParameter("role", "ROLE_USER")
                .getResultStream()
                .map(this::mapUser)
                .collect(Collectors.toList());
    }

    private UserBindingModel mapUser(User user) {
        return new UserBindingModel(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getContactNumber(),
                user.getIsVerified()
        );
    }

    @Transactional
    public String update(UserUpdateModel userUpdateModel) {

        User userToUpdate = em.find(User.class, userUpdateModel.getId());

        if (userToUpdate != null) {

            log.info("updating user");

            userToUpdate
                    .setIsVerified(userUpdateModel.getStatus());

            //NOTIFY ADMINS ABOUT THE UPDATE
            sendMailToAllAdmins(userUpdateModel.getStatus(), userToUpdate.getEmail(), getAdminMails());

            return String.format("User %s updated successfully", userToUpdate.getEmail());
        }else {
            throw new UsernameNotFoundException("User with id " + userUpdateModel.getId() + " not found");
        }

    }

    private void sendMailToAllAdmins(String status, String user, List<String> adminMails) {
        //we do not want to send the same mail twice to the current user if he is admin
        adminMails.remove(jwtFilter.getCurrentUser());

        if (Boolean.parseBoolean(status)) {
            emailUtils
                    .sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved",
                            String.format("USER:- %s is approved by %s", user, jwtFilter.getCurrentUser()),
                            adminMails);
        }else {
            emailUtils
                    .sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled",
                            String.format("USER:- %s is disabled by %s", user, jwtFilter.getCurrentUser()),
                            adminMails);
        }
    }

    private List<String> getAdminMails() {
        log.info("Fetching admin mails");

        return em.createQuery("select u.email from User u where u.role=:role", String.class)
                .setParameter("role", "ROLE_ADMIN")
                .getResultStream()
                .collect(Collectors.toList());
    }

    //FOR ROUTING FROM PAGE TO PAGE AFTER TOKEN IS PASSED
    public String checkToken() {
        return "true";
    }

    @Transactional
    public ResponseEntity<String> changePassword(ChangePasswordBindingModel passwordChangeModel) {
        User userObj = em.createQuery("select u from User u where email=:email", User.class)
                .setParameter("email", jwtFilter.getCurrentUser())
                .getSingleResult();

        if (Objects.nonNull(userObj)) {

            String rawPassword = passwordChangeModel.getOldPassword();
            String encodedPassword = userObj.getPassword();

            if (encoder.matches(rawPassword, encodedPassword)) {
                if(passwordChangeModel.getNewPassword().equals(passwordChangeModel.getConfirmPassword())) {
                    if(!passwordChangeModel.getNewPassword().equals(rawPassword)) {
                        userObj.setPassword(encoder.encode(passwordChangeModel.getNewPassword()));

                        return CafeUtils.getResponseEntity("Password Successfully Updated.", HttpStatus.OK);
                    }

                    return CafeUtils.getResponseEntity("New Password cannot be same as Old Password.", HttpStatus.BAD_REQUEST);
                }

                return CafeUtils.getResponseEntity("Passwords don't match.", HttpStatus.BAD_REQUEST);
            }else {
                return CafeUtils.getResponseEntity("Incorrect Old Password.", HttpStatus.BAD_REQUEST);
            }

        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Transactional
    public ResponseEntity<String> forgotPassword(ForgotPasswordModel forgotPasswordModel) {

        try {
            User user = em.createQuery("select u from User u where email=:email", User.class)
                    .setParameter("email", forgotPasswordModel.getEmail())
                    .getSingleResult();

            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
                String randomPass = PasswordGenerator.generatePassword();
                user.setPassword(encoder.encode(randomPass));
                emailUtils.forgotMail(user.getEmail(), "Credentials by Cafe Management System", randomPass);
            }
            return CafeUtils.getResponseEntity("Check your mail for Credentials.", HttpStatus.OK);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
