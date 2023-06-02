package com.org.MiniAspire.integration.usercontroller;


import com.org.MiniAspire.MiniAspireApplication;
import com.org.MiniAspire.request.CreateUserRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static com.org.MiniAspire.testutils.TestUtil.createURLWithPort;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MiniAspireApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CreateUserIntegrationTest {

    @LocalServerPort
    private int port = 8080;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Test
    public void shouldCreateNewUserWhenUsernameDoesntExist() {
        CreateUserRequest createUserRequest = new CreateUserRequest("abc", "123");
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(createUserRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/aspire/user", port),
                HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals("User created", response.getBody());
    }
}

