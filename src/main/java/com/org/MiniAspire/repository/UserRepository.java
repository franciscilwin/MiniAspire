package com.org.MiniAspire.repository;


import com.org.MiniAspire.models.User;
import com.org.MiniAspire.models.UserType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;


@Getter
@Setter
@Component
public class UserRepository {

    @Getter @Setter
    private static Map<String, User> users;

    public UserRepository() {
        this.users = new TreeMap<>();
        users.put("admin", new User("admin", "xyz123", UserType.ADMIN));
    }

    public void addUser(String username, String password) {
        users.put(username,
                new User(username, password, UserType.CUSTOMER));
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

}
