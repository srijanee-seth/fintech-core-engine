package com.fintech.wallet.controller;

import com.fintech.wallet.entity.User;
import com.fintech.wallet.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Dependency Injection! Spring Boot hands the Controller the Service.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public User createNewUser(@RequestParam String name, @RequestParam String email) {
        // The receptionist hands the data to the brain
        return userService.createUser(name, email);
    }
}