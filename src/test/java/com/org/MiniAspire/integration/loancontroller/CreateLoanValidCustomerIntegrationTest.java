package com.org.MiniAspire.integration.loancontroller;


import com.org.MiniAspire.MiniAspireApplication;
import com.org.MiniAspire.request.CreateLoanRequest;
import com.org.MiniAspire.request.CreateUserRequest;
import com.org.MiniAspire.util.Constants;
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
@SpringBootTest(classes = MiniAspireApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CreateLoanValidCustomerIntegrationTest {

    @LocalServerPort
    private int port = 8080;

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void shouldCreateNewLoanIfUserExists() {
        HttpHeaders headers = new HttpHeaders();
        CreateUserRequest createUserRequest = new CreateUserRequest("abc", "123");
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(createUserRequest, headers);
        restTemplate.exchange(
                createURLWithPort("/api/aspire/user", 8080),
                HttpMethod.POST, requestEntity, String.class);
        CreateLoanRequest createLoanRequest = new CreateLoanRequest(100.0,3);
        headers.add(Constants.USERNAME_HEADER, "abc");
        headers.add(Constants.PASSWORD_HEADER, "123");
        HttpEntity<CreateLoanRequest> createLoanRequestHttpEntity = new HttpEntity<>(createLoanRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/aspire/loan", 8080),
                HttpMethod.POST, createLoanRequestHttpEntity, String.class);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals("Loan Sent for Approval", response.getBody());
    }
}


