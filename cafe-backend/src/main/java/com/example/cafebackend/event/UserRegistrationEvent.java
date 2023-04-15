package com.example.cafebackend.event;

import org.springframework.context.ApplicationEvent;

public class UserRegistrationEvent extends ApplicationEvent {
    private final String userEmail;

    public UserRegistrationEvent(Object source, String userEmail) {
        super(source);
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
