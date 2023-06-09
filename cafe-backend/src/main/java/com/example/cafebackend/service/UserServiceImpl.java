package com.example.cafebackend.service;

import com.example.cafebackend.constants.CafeConstants;
import com.example.cafebackend.event.UserRegistrationEvent;
import com.example.cafebackend.jwt.JwtFilter;
import com.example.cafebackend.jwt.JwtUtil;
import com.example.cafebackend.jwt.UserPrincipal;
import com.example.cafebackend.model.binding.user.*;
import com.example.cafebackend.model.entity.Role;
import com.example.cafebackend.model.entity.User;
import com.example.cafebackend.model.mapper.UserMapper;
import com.example.cafebackend.repository.UserRepository;
import com.example.cafebackend.utils.CafeUtils;
import com.example.cafebackend.utils.EmailUtils;
import com.example.cafebackend.utils.PasswordGenerator;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public ResponseEntity<String> signUp(RegistrationRequest request) {
        log.info("Inside signup: {}", request);

        try {
            Optional<User> existsUserByEmail = userRepository.findByEmail(request.getEmail());

            if (existsUserByEmail.isEmpty()) {
                User newUser = constructUser(request);
                userRepository.save(newUser);

                //instantiate and publish the event
                UserRegistrationEvent registrationEvent = new UserRegistrationEvent(this, request.getEmail());
                eventPublisher.publishEvent(registrationEvent);

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
        User user = userMapper.userDTOtoUserEntity(request);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setIsVerified("false");
        Role role = roleService.findRoleById(2L);
        user.setRole(role);
        return user;
    }

    @Override
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

    @Override
    public List<UserBindingModel> getAllUsers() {
        log.info("Fetching all users");

        return userRepository.findAll()
                .stream()
                .map(this::mapUser)
                .collect(Collectors.toList());
    }

    private UserBindingModel mapUser(User user) {
        UserBindingModel userDTO = userMapper.userEntityToDTO(user);
        userDTO.setStatus(user.getIsVerified());
        String role = user.getRole().getName().equals("ROLE_ADMIN") ? "Admin" : "User";
        Long roleId = user.getRole().getId();
        userDTO.setRoleName(role);
        userDTO.setRoleId(roleId);
        return userDTO;
    }

    @Transactional
    @Override
    public String update(UserUpdateModel userUpdateModel) {

        User userToUpdate = em.find(User.class, userUpdateModel.getId());

        if (userToUpdate != null) {

            log.info("updating user");

            userToUpdate
                    .setIsVerified(userUpdateModel.getStatus());

            //NOTIFY ADMINS ABOUT THE UPDATE
            sendMailToAllAdmins(userUpdateModel.getStatus(), userToUpdate.getEmail());

            return String.format("User %s updated successfully", userToUpdate.getEmail());
        }else {
            throw new UsernameNotFoundException("User with id " + userUpdateModel.getId() + " not found");
        }

    }

    private void sendMailToAllAdmins(String status, String user) {
        List<String> adminMails = getAdminMails();
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

    private void sendMailToAllAdmins(List<User> inactiveUsers) {
        if (!inactiveUsers.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder
                    .append("Following Users are not activated:")
                    .append("\n");

            inactiveUsers.forEach(u ->
                    messageBuilder
                            .append("-").append(u.getEmail())
                            .append("\n")
            );

            List<String> adminMails = getAdminMails();

            emailUtils
                    .sendSimpleMessage("INACTIVE USERS REPORT",
                            messageBuilder.toString(),
                            adminMails);
        }
    }

    private void sendMailToAllAdmins(String userEmail) {

        List<String> adminMails = getAdminMails();

        if(!adminMails.isEmpty()) {
            emailUtils
                    .sendSimpleMessage("USER ACTIVATION",
                            String.format("Registered user: %s\n Activation required.", userEmail),
                            adminMails);
        }
    }

    private List<String> getAdminMails() {
        log.info("Fetching admin mails");

        return userRepository.findAllByRoleId(1L)
                .stream().map(User::getEmail)
                .collect(Collectors.toList());
    }

    //FOR ROUTING FROM PAGE TO PAGE AFTER TOKEN IS PASSED
    @Override
    public String checkToken() {
        return "true";
    }

    @Transactional
    @Override
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
    @Override
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

    @Override
    public List<User> findInactiveUsers() {
        return userRepository.findAllByIsVerified("false");
    }

    @Override
    public void notifyAdminsAboutInactiveUsers(List<User> inactiveUsers) {
        sendMailToAllAdmins(inactiveUsers);
    }

    @Override
    public void notifyAdminsAboutUserRegistration(String userEmail) {
        sendMailToAllAdmins(userEmail);
    }

    @Override
    public String updateUsername(UsernameUpdateModel usernameUpdateModel) {
        User userToUpdate = userRepository.findByEmail(usernameUpdateModel.getEmail()).orElse(null);

        if (!Objects.isNull(userToUpdate)) {

            log.info("updating user username");

            userToUpdate
                    .setName(usernameUpdateModel.getUsername());

            userRepository.save(userToUpdate);
            return String.format("User %s updated successfully", userToUpdate.getEmail());
        }else {
            throw new UsernameNotFoundException("User with email " + usernameUpdateModel.getEmail() + " not found");
        }
    }

    @Override
    public ResponseEntity<?> getUserByEmail(String email) {
        log.info("Fetching user by email...");

        try {
            User user = userRepository.findByEmail(email).orElse(null);
            if (!Objects.isNull(user)) {
                log.info("User with email {} found", email);
                return ResponseEntity.ok(mapUser(user));
            }

            log.info("User not found");
            return CafeUtils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> edit(UserEditModel userEditModel) {
        log.info("Updating user...");

        try {
            User user = userRepository.findById(userEditModel.getId()).orElse(null);

            boolean isChanged = false;

            if(!Objects.isNull(user)) {
                if(!user.getName().equals(userEditModel.getName())) {
                    user.setName(userEditModel.getName());
                    isChanged=true;
                }

                if (!user.getEmail().equals(userEditModel.getEmail())) {
                    user.setEmail(userEditModel.getEmail());
                    isChanged=true;
                }

                if (!user.getContactNumber().equals(userEditModel.getContactNumber())) {
                    user.setContactNumber(userEditModel.getContactNumber());
                    isChanged=true;
                }

                if(!user.getRole().getId().equals(userEditModel.getRoleId())) {
                    user.setRole(roleService.findRoleById(userEditModel.getRoleId()));
                    isChanged=true;
                }

                if(isChanged) {
                    userRepository.save(user);
                    log.info("User updated successfully.");
                    return CafeUtils.getResponseEntity("User updated successfully", HttpStatus.OK);
                }

                log.info("User has no changes to commit");
                return CafeUtils.getResponseEntity("User has no changes to commit", HttpStatus.OK);
            }

            log.info("User not found.");
            return CafeUtils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
