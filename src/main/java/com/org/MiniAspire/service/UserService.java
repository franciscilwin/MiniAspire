package com.org.MiniAspire.service;

import com.org.MiniAspire.exception.UserDoesNotExistException;
import com.org.MiniAspire.models.UserType;
import com.org.MiniAspire.repository.UserRepository;
import com.org.MiniAspire.request.CreateUserRequest;
import com.org.MiniAspire.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public void createUser(CreateUserRequest createUserRequest) {
        userRepository.addUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }

    public boolean userExists(CreateUserRequest createUserRequest) {
        return userRepository.userExists(createUserRequest.getUsername());
    }

    public boolean authenticateUser(HttpServletRequest request) throws UserDoesNotExistException {
        String username = request.getHeader(Constants.USERNAME_HEADER);
        String password = request.getHeader(Constants.PASSWORD_HEADER);
        if (!userRepository.getUsers().containsKey(username))
            throw new UserDoesNotExistException();
        if (userRepository.getUsers().get(username).getPassword().equals(password))
            return true;
        return false;
    }

    public UserType getUserType(String username) throws UserDoesNotExistException {
        if (!userRepository.getUsers().containsKey(username))
            throw new UserDoesNotExistException();
        return userRepository.getUsers().get(username).getUserType();
    }
}
