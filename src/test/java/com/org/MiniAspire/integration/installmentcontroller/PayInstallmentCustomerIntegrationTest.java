package com.org.MiniAspire.integration.installmentcontroller;


import com.org.MiniAspire.MiniAspireApplication;
import com.org.MiniAspire.models.LoanStatus;
import com.org.MiniAspire.request.CreateLoanRequest;
import com.org.MiniAspire.request.CreateUserRequest;
import com.org.MiniAspire.request.PayInstallmentRequest;
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
@SpringBootTest(classes = MiniAspireApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PayInstallmentCustomerIntegrationTest {

    @LocalServerPort
    private int port = 8080;

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void shouldPayInstallmentIfUserExistsLoanExistsPendingPaymentsExist() {
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
        restTemplate.exchange(
                createURLWithPort("/api/aspire/loan", 8080),
                HttpMethod.POST, createLoanRequestHttpEntity, String.class);
        ProcessLoanRequest processLoanRequest = new ProcessLoanRequest(0, LoanStatus.APPROVED);
        HttpHeaders processLoanRequestHeaders = new HttpHeaders();
        processLoanRequestHeaders.add(Constants.USERNAME_HEADER, "admin");
        processLoanRequestHeaders.add(Constants.PASSWORD_HEADER, "xyz123");
        HttpEntity<ProcessLoanRequest> processLoanRequestHttpEntity =
                new HttpEntity<>(processLoanRequest, processLoanRequestHeaders);
        restTemplate.exchange(
                createURLWithPort("/api/aspire/loan", 8080),
                HttpMethod.PUT, processLoanRequestHttpEntity, String.class);
        PayInstallmentRequest payInstallmentRequest = new PayInstallmentRequest(0, 0);
        HttpHeaders payInstallmentRequestHeaders = new HttpHeaders();
        payInstallmentRequestHeaders.add(Constants.USERNAME_HEADER, "abc");
        payInstallmentRequestHeaders.add(Constants.PASSWORD_HEADER, "123");
        HttpEntity<PayInstallmentRequest> payInstallmentRequestHttpEntity =
                new HttpEntity<>(payInstallmentRequest, payInstallmentRequestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/aspire/installment", 8080),
                HttpMethod.PUT, payInstallmentRequestHttpEntity, String.class);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals("Installment processed", response.getBody());
    }
}

