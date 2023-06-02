package com.org.MiniAspire.repository;

import com.org.MiniAspire.models.User;
import com.org.MiniAspire.models.UserType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UserRepository.class)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void addUser_withNewUser_addsToUsers() {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User("abc", "123", UserType.CUSTOMER));
        expectedUsers.add(new User("admin", "xyz123", UserType.ADMIN));
        userRepository.addUser("abc", "123");
        Assert.assertEquals(userRepository.getUsers().values().stream().toList(), expectedUsers);
    }

    @Test
    public void userExists_checkExistingUser_returnTrue() {
        Assert.assertEquals(userRepository.userExists("admin"), true);
    }


    @Test
    public void userExists_checkNonExistentUser_returnFalse() {
        Assert.assertEquals(userRepository.userExists("abc"), false);
    }

    @Test
    public void getUsers_startingUserList_matchesMockArray() {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User("admin", "xyz123", UserType.ADMIN));
        Assert.assertEquals(userRepository.getUsers().values().stream().toList(), expectedUsers);
    }
}
