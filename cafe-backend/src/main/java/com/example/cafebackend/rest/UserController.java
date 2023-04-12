package com.example.cafebackend.rest;

import com.example.cafebackend.constants.CafeConstants;
import com.example.cafebackend.jwt.UserPrincipal;
import com.example.cafebackend.model.binding.user.*;
import com.example.cafebackend.model.entity.User;
import com.example.cafebackend.service.UserServiceImpl;
import com.example.cafebackend.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUp(@RequestBody @Valid RegistrationRequest request) {

        try {
            return userService
                    .signUp(request);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CafeConstants.SOMETHING_WENT_WRONG);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {

        try {
            return userService
                    .login(request);
        }catch (BadCredentialsException ex) {
            ex.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CafeConstants.SOMETHING_WENT_WRONG);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserBindingModel>> getAllUsers() {

            return ResponseEntity
                    .ok()
                    .body(userService.getAllUsers());

    }

    @PatchMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> update(@RequestBody UserUpdateModel userUpdateModel) {

        try {
            return CafeUtils
                    .getResponseEntity(userService.update(userUpdateModel), HttpStatus.OK);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PatchMapping("/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> editUser(@RequestBody UserEditModel userEditDTO) {

        try {
            return userService.edit(userEditDTO);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PatchMapping("/changeUsername")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<String> updateUsername(@RequestBody UsernameUpdateModel usernameUpdateModel) {

        try {
            return CafeUtils
                    .getResponseEntity(userService.updateUsername(usernameUpdateModel), HttpStatus.OK);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @GetMapping("/getUser")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();

        try {
            return userService.getUserByEmail(user.getUsername());
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    //FOR ROUTING FROM PAGE TO PAGE AFTER TOKEN IS PASSED (CONTAINS NO FUNCTIONALITY)
    @GetMapping("/checkToken")
    public ResponseEntity<String> checkToken() {
        try {

            return CafeUtils.getResponseEntity(userService.checkToken(), HttpStatus.OK);

        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/changePassword")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordBindingModel passwordChangeModel) {
        try {
            return userService
                    .changePassword(passwordChangeModel);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordModel forgotPasswordModel) {
        try {
            return userService
                    .forgotPassword(forgotPasswordModel);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CafeConstants.SOMETHING_WENT_WRONG);
    }
}
