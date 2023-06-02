package com.org.MiniAspire.integration.loancontroller;


import com.org.MiniAspire.MiniAspireApplication;
import com.org.MiniAspire.models.Loan;
import com.org.MiniAspire.request.CreateLoanRequest;
import com.org.MiniAspire.request.CreateUserRequest;
import com.org.MiniAspire.util.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.org.MiniAspire.testutils.TestUtil.createURLWithPort;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = MiniAspireApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class GetLoansAdminIntegrationTest {

    @LocalServerPort
    private int port = 8080;

    TestRestTemplate restTemplate = new TestRestTemplate();


    @Test
    public void shouldGetLoansPendingApprovalIfUserAdmin() {
        CreateUserRequest createUserRequest = new CreateUserRequest("abc", "123");
        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(createUserRequest, new HttpHeaders());
        restTemplate.exchange(
                createURLWithPort("/api/aspire/user", port),
                HttpMethod.POST, requestEntity, String.class);
        CreateLoanRequest createLoanRequest = new CreateLoanRequest(100.0,3);
        HttpHeaders createLoanRequestHeaders = new HttpHeaders();
        createLoanRequestHeaders.add(Constants.USERNAME_HEADER, "abc");
        createLoanRequestHeaders.add(Constants.PASSWORD_HEADER, "123");
        HttpEntity<CreateLoanRequest> createLoanRequestHttpEntity =
                new HttpEntity<>(createLoanRequest, createLoanRequestHeaders);
        restTemplate.exchange(
                createURLWithPort("/api/aspire/loan", port),
                HttpMethod.POST, createLoanRequestHttpEntity, String.class);
        HttpHeaders getLoansRequestHeaders = new HttpHeaders();
        getLoansRequestHeaders.add(Constants.USERNAME_HEADER, "admin");
        getLoansRequestHeaders.add(Constants.PASSWORD_HEADER, "xyz123");
        HttpEntity<CreateLoanRequest> getLoanRequestHeaders =
                new HttpEntity<>(null, getLoansRequestHeaders);
        ResponseEntity<List<Loan>> response = restTemplate.exchange(
                createURLWithPort("/api/aspire/loan", port),
                HttpMethod.GET, getLoanRequestHeaders, new ParameterizedTypeReference<List<Loan>>() {});
        Assert.assertEquals(200, response.getStatusCode().value());
    }
}

