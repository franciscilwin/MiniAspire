package com.org.MiniAspire.controller;

import com.org.MiniAspire.request.CreateUserRequest;
import com.org.MiniAspire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aspire/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest createUserRequest) {
        if (userService.userExists(createUserRequest)) {
            System.out.println(userService.userExists(createUserRequest));
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        else {
            userService.createUser(createUserRequest);
            return ResponseEntity.ok("User created");
        }
    }
}
