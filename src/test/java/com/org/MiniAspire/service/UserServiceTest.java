package com.org.MiniAspire.service;

import com.org.MiniAspire.exception.UserDoesNotExistException;
import com.org.MiniAspire.models.User;
import com.org.MiniAspire.models.UserType;
import com.org.MiniAspire.repository.UserRepository;
import com.org.MiniAspire.request.CreateUserRequest;
import com.org.MiniAspire.util.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = UserService.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createUser_testCallToUserRepository_SuccessfullyCallsWithCorrectArguments() {
        CreateUserRequest mockRequest = new CreateUserRequest("abc", "123");
        userService.createUser(mockRequest);
        Mockito.verify(userRepository).addUser(mockRequest.getUsername(),mockRequest.getPassword());
    }


    @Test
    public void userExists_testCallToUserRepository_SuccessfullyCallsWithCorrectArguments() {
        CreateUserRequest mockRequest = new CreateUserRequest("abc", "123");
        userService.userExists(mockRequest);
        Mockito.verify(userRepository).userExists(mockRequest.getUsername());
    }



    @Test
    public void authenticateUser_correctCredentialsEntered_ReturnsTrue() throws UserDoesNotExistException {
        try (MockedStatic<UserRepository> mockedStatic = mockStatic(UserRepository.class)) {
            Map<String, User> mockUsers = new TreeMap<>();
            mockUsers.put("abc", new User("abc", "123", UserType.CUSTOMER));
            when(userRepository.getUsers()).thenReturn(mockUsers);
            MockHttpServletRequest mockRequest = new MockHttpServletRequest();
            mockRequest.addHeader(Constants.USERNAME_HEADER,"abc");
            mockRequest.addHeader(Constants.PASSWORD_HEADER,"123");
            Assert.assertEquals(userService.authenticateUser(mockRequest), true);
        }
    }
    @Test
    public void authenticateUser_incorrectCredentialsEntered_ReturnsFalse() {
        try (MockedStatic<UserRepository> mockedStatic = mockStatic(UserRepository.class)) {
            Map<String, User> mockUsers = new TreeMap<>();
            mockUsers.put("abc", new User("abc", "123", UserType.CUSTOMER));
            when(userRepository.getUsers()).thenReturn(mockUsers);
            MockHttpServletRequest mockRequest = new MockHttpServletRequest();
            mockRequest.addHeader(Constants.USERNAME_HEADER,"abc");
            mockRequest.addHeader(Constants.PASSWORD_HEADER,"12");
            Assert.assertEquals(userService.authenticateUser(mockRequest), false);
        } catch (UserDoesNotExistException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void authenticateUser_nonexistentUser_exceptionThrown() {
        try (MockedStatic<UserRepository> mockedStatic = mockStatic(UserRepository.class)) {
            Map<String, User> mockUsers = new TreeMap<>();
            mockUsers.put("abc", new User("abc", "123", UserType.CUSTOMER));
            MockHttpServletRequest mockRequest = new MockHttpServletRequest();
            mockRequest.addHeader(Constants.USERNAME_HEADER,"ab");
            mockRequest.addHeader(Constants.PASSWORD_HEADER,"12");
            Assert.assertThrows(UserDoesNotExistException.class, () -> {
                when(userRepository.getUsers()).thenReturn(mockUsers);
                userService.authenticateUser(mockRequest);
            });
        }
    }

    @Test
    public void getUserType_nonexistentUser_exceptionThrown() {
        try (MockedStatic<UserRepository> mockedStatic = mockStatic(UserRepository.class)) {
            Map<String, User> mockUsers = new TreeMap<>();
            mockUsers.put("abc", new User("abc", "123", UserType.CUSTOMER));
            when(userRepository.getUsers()).thenReturn(mockUsers);
            Assert.assertThrows(UserDoesNotExistException.class, () -> {
                userService.getUserType("ab");
            });
        }
    }

    @Test
    public void getUserType_existentUser_returnsCorrectUserType() {
        try (MockedStatic<UserRepository> mockedStatic = mockStatic(UserRepository.class)) {
            Map<String, User> mockUsers = new TreeMap<>();
            mockUsers.put("abc", new User("abc", "123", UserType.CUSTOMER));
            when(userRepository.getUsers()).thenReturn(mockUsers);
            Assert.assertEquals(userService.getUserType("abc"), UserType.CUSTOMER);
        } catch (UserDoesNotExistException e) {
            throw new RuntimeException(e);
        }
    }

}
