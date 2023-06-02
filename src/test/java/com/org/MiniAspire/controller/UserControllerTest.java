package com.org.MiniAspire.controller;

import com.org.MiniAspire.request.CreateUserRequest;
import com.org.MiniAspire.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = UserController.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void createUser_creatingNewUser_expectedResponseEntityReturned() {
        CreateUserRequest mockCreateUserRequest = new CreateUserRequest("abc","123");
        when(userService.userExists(mockCreateUserRequest)).thenReturn(false);
        ResponseEntity<String> response = userController.createUser(mockCreateUserRequest);
        verify(userService).createUser(mockCreateUserRequest);
        Assert.assertEquals(response.getBody(), "User created");
    }


    @Test
    public void createUser_userExists_expectedResponseEntityReturned() {
        CreateUserRequest mockCreateUserRequest = new CreateUserRequest("abc","123");
        when(userService.userExists(mockCreateUserRequest)).thenReturn(true);
        ResponseEntity<String> response = userController.createUser(mockCreateUserRequest);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(409));
    }
}
