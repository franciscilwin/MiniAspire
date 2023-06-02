package com.org.MiniAspire.integration.loancontroller;


import com.org.MiniAspire.MiniAspireApplication;
import com.org.MiniAspire.models.LoanStatus;
import com.org.MiniAspire.request.CreateLoanRequest;
import com.org.MiniAspire.request.CreateUserRequest;
import com.org.MiniAspire.request.ProcessLoanRequest;
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
public class ProcessLoanCustomerIntegrationTest {

    @LocalServerPort
    private int port = 8080;

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void shouldReturn401StatusCodeIfCustomerProcessesLoan() {
        CreateUserRequest createUserRequest = new CreateUserRequest("abc", "123");
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(createUserRequest, new HttpHeaders());
        restTemplate.exchange(
                createURLWithPort("/api/aspire/user", port),
                HttpMethod.POST, requestEntity, String.class);
        CreateLoanRequest createLoanRequest = new CreateLoanRequest(100.0,3);
        HttpHeaders createLoanRequestHeaders = new HttpHeaders();
        createLoanRequestHeaders.add(Constants.USERNAME_HEADER, "abc");
        createLoanRequestHeaders.add(Constants.PASSWORD_HEADER, "123");
        HttpEntity<CreateLoanRequest> createLoanRequestHttpEntity = new HttpEntity<>(createLoanRequest, createLoanRequestHeaders);
        restTemplate.exchange(
                createURLWithPort("/api/aspire/loan", port),
                HttpMethod.POST, createLoanRequestHttpEntity, String.class);
        ProcessLoanRequest processLoanRequest = new ProcessLoanRequest(0, LoanStatus.APPROVED);
        HttpHeaders processLoanRequestHeaders = new HttpHeaders();
        processLoanRequestHeaders.add(Constants.USERNAME_HEADER, "abc");
        processLoanRequestHeaders.add(Constants.PASSWORD_HEADER, "123");
        HttpEntity<ProcessLoanRequest> processLoanRequestHttpEntity =
                new HttpEntity<>(processLoanRequest, processLoanRequestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/aspire/loan", port),
                HttpMethod.PUT, processLoanRequestHttpEntity, String.class);
        Assert.assertEquals(401, response.getStatusCodeValue());
    }
}
