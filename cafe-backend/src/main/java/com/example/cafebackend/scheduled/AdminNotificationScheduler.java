package com.example.cafebackend.scheduled;

import com.example.cafebackend.model.entity.User;
import com.example.cafebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class AdminNotificationScheduler {

    @Autowired
    private UserService userService;

    /*Scheduled Job that runs every 10 minutes
    and notifies admins if there are any users
    that require activation*/
    @Transactional
    @Scheduled(cron = "${cma.notification.batch.cron}")
    public void notifyAdminsAboutDeactivatedUsers() {
        log.info("Admin notification batch started.");

        List<User> inactiveUsers = userService.findInactiveUsers();

        userService.notifyAdminsAboutInactiveUsers(inactiveUsers);
        log.info("Admin notification batch finished.");
    }

}
