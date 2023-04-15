package com.example.cafebackend.event;

import com.example.cafebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationEventListener implements ApplicationListener<UserRegistrationEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(UserRegistrationEvent event) {
        String userEmail = event.getUserEmail();
        //send mail to all admins
        userService.notifyAdminsAboutUserRegistration(userEmail);
    }
}
